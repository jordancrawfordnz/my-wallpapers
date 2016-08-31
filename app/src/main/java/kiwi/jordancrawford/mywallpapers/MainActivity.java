package kiwi.jordancrawford.mywallpapers;

import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private final int PICK_IMAGE_REQUEST = 1;
    private final int SET_WALLPAPER_REQUEST = 2;
    private final String INTENT_USED_KEY = "used";
    private ImageView sentImageDisplay;

    private BroadcastReceiver wallpaperAddedMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadAllWallpapers();
        }
    };

    private BroadcastReceiver getAllWallpaperMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<Wallpaper> wallpapers = intent.getParcelableArrayListExtra(GetAllWallpapers.ALL_WALLPAPERS_EXTRA);

            System.out.println("Existing wallpapers");
            for (Wallpaper currentWallpaper : wallpapers) {
                System.out.println(currentWallpaper.toString());
            }

        }
    };

    // Starts the GetAllWallpapers task.
    private void loadAllWallpapers() {
        System.out.println("Loading all wallpapers");
        new GetAllWallpapers(this).execute();
    }

    private void processIntent(Intent intent) {
        String intentAction = intent.getAction();
        if (Intent.ACTION_SEND.equals(intentAction) && !intent.hasExtra(INTENT_USED_KEY)) {
            // Mark the intent as used.
            intent.putExtra(INTENT_USED_KEY, true);

            // Check this is an image.
            if (intent.getType().startsWith("image/")) {
                Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
                new ProcessSentImage(this).execute(imageUri);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
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
        sentImageDisplay = (ImageView) findViewById(R.id.sent_image_display);

        LocalBroadcastManager.getInstance(this).registerReceiver(wallpaperAddedMessageReceiver, new IntentFilter(ProcessSentImage.WALLPAPER_ADDED_BROADCAST_INTENT));
        LocalBroadcastManager.getInstance(this).registerReceiver(getAllWallpaperMessageReceiver, new IntentFilter(GetAllWallpapers.GET_ALL_WALLPAPERS_BROADCAST_INTENT));

        processIntent(getIntent());
        loadAllWallpapers();

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
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        processIntent(intent);
    }

}
