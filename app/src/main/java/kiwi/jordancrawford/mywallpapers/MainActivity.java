package kiwi.jordancrawford.mywallpapers;

import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.BoolRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final int PICK_IMAGE_REQUEST = 1;
    private final int SET_WALLPAPER_REQUEST = 2;
    private final String INTENT_USED_KEY = "used";
    private ArrayList<Wallpaper> wallpapers = new ArrayList<>();
    private RecyclerView wallpaperRecyclerView;
    private RecyclerView.LayoutManager wallpaperRecyclerViewLayoutManager;
    private RecyclerView.Adapter wallpaperRecyclerViewAdapter;

    private BroadcastReceiver wallpaperAddedMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadAllWallpapers();
        }
    };

    private BroadcastReceiver getAllWallpaperMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<Wallpaper> updatedWallpapers = intent.getParcelableArrayListExtra(GetAllWallpapersTask.ALL_WALLPAPERS_EXTRA);

            System.out.println("Existing wallpapers");
            for (Wallpaper currentWallpaper : updatedWallpapers) {
                System.out.println(currentWallpaper.toString());
            }

            wallpapers.clear();
            wallpapers.addAll(updatedWallpapers);
            wallpaperRecyclerViewAdapter.notifyDataSetChanged();
        }
    };

    private BroadcastReceiver setWallpaperMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Wallpaper wallpaper = intent.getParcelableExtra(WallpaperListAdapter.WALLPAPER_EXTRA);

            // Show the wallpaper manager's dialog to set the wallpaper.
            Uri uri = WallpaperUtils.getLargeImageUri(context, wallpaper);
            Intent changeWallpaperIntent = WallpaperManager.getInstance(context).getCropAndSetWallpaperIntent(uri);
            startActivityForResult(changeWallpaperIntent, SET_WALLPAPER_REQUEST);

            // TODO: Mark this as the current wallpaper.
        }
    };

    private BroadcastReceiver deleteWallpaperMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            final Wallpaper wallpaper = intent.getParcelableExtra(WallpaperListAdapter.WALLPAPER_EXTRA);

            // Show a prompt for the user to confirm deletion.
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle(R.string.wallpaper_delete_confirm_title);
            alertDialog.setMessage(getString(R.string.wallpaper_delete_confirm_message));

            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.wallpaper_delete_confirm_cancel_button),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.wallpaper_delete_confirm_delete_button),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            new DeleteWallpaperTask(context).execute(wallpaper);
                            dialog.dismiss();
                        }
                    });

            alertDialog.show();
        }
    };

    private BroadcastReceiver wallpaperDeletedMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Reload the wallpapers to exclude the deleted wallpaper.
            loadAllWallpapers();
        }
    };

    // Starts the GetAllWallpapersTask task.
    private void loadAllWallpapers() {
        new GetAllWallpapersTask(this).execute();
    }

    private void processIntent(Intent intent) {
        String intentAction = intent.getAction();
        if (Intent.ACTION_SEND.equals(intentAction) && !intent.hasExtra(INTENT_USED_KEY)) {
            // Mark the intent as used.
            intent.putExtra(INTENT_USED_KEY, true);

            // Check this is an image.
            if (intent.getType().startsWith("image/")) {
                Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
                new ProcessSentImageTask(this).execute(imageUri);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // TODO: Turn this into add wallpaper.

            Uri uri = data.getData();
            Intent changeWallpaperIntent = WallpaperManager.getInstance(this).getCropAndSetWallpaperIntent(uri);
            startActivityForResult(changeWallpaperIntent, SET_WALLPAPER_REQUEST);
        } else if (requestCode == SET_WALLPAPER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, R.string.wallpaper_set, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        LocalBroadcastManager.getInstance(this).registerReceiver(wallpaperAddedMessageReceiver, new IntentFilter(ProcessSentImageTask.WALLPAPER_ADDED_BROADCAST_INTENT));
        LocalBroadcastManager.getInstance(this).registerReceiver(getAllWallpaperMessageReceiver, new IntentFilter(GetAllWallpapersTask.GET_ALL_WALLPAPERS_BROADCAST_INTENT));
        LocalBroadcastManager.getInstance(this).registerReceiver(setWallpaperMessageReceiver, new IntentFilter(WallpaperListAdapter.SET_WALLPAPER_BROADCAST_INTENT));
        LocalBroadcastManager.getInstance(this).registerReceiver(deleteWallpaperMessageReceiver, new IntentFilter(WallpaperListAdapter.DELETE_WALLPAPER_BROADCAST_INTENT));
        LocalBroadcastManager.getInstance(this).registerReceiver(wallpaperDeletedMessageReceiver, new IntentFilter(DeleteWallpaperTask.WALLPAPER_DELETED_BROADCAST_INTENT));

        processIntent(getIntent());
        loadAllWallpapers();

        // Setup the wallpaper recycler view.
        wallpaperRecyclerView = (RecyclerView) findViewById(R.id.wallpaper_list_recycler_view);
        int deviceOrientation = getResources().getConfiguration().orientation;
        int numberOfColumns = deviceOrientation == Configuration.ORIENTATION_LANDSCAPE ? 2 : 1;
        wallpaperRecyclerViewLayoutManager = new StaggeredGridLayoutManager(numberOfColumns, StaggeredGridLayoutManager.VERTICAL);
        wallpaperRecyclerView.setLayoutManager(wallpaperRecyclerViewLayoutManager);
        wallpaperRecyclerViewAdapter = new WallpaperListAdapter(this, wallpapers);
        wallpaperRecyclerView.setAdapter(wallpaperRecyclerViewAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                // Show only images, no videos or anything else
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                // Always show the chooser (if there are multiple options available)
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });
    }

    // Unregister from local async events.
    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(wallpaperAddedMessageReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(getAllWallpaperMessageReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(setWallpaperMessageReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(deleteWallpaperMessageReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(wallpaperDeletedMessageReceiver);
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        processIntent(intent);
    }

}
