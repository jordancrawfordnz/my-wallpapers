package kiwi.jordancrawford.mywallpapers;

import android.app.SearchManager;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;
import java.util.ArrayList;

/**
 * Displays the list of wallpapers and provides options to add new wallpapers, set, and delete wallpapers.
 */
public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private final int PICK_IMAGE_REQUEST = 1;
    private final int SET_WALLPAPER_REQUEST = 2;
    private final String INTENT_USED_KEY = "used";
    private final String NEW_CURRENT_WALLPAPER_KEY = "new_current_wallpaper";
    private final String CURRENT_WALLPAPER_KEY = "current_wallpaper";
    private final String SEARCH_TEXT_KEY = "search_text";

    private ArrayList<Wallpaper> wallpapers = new ArrayList<>();
    private Wallpaper currentWallpaper = null;
    private Wallpaper newCurrentWallpaper = null; // to be used only while in the process of changing the current wallpaper.
    private RecyclerView wallpaperRecyclerView;
    private RecyclerView.LayoutManager wallpaperRecyclerViewLayoutManager;
    private RecyclerView.Adapter wallpaperRecyclerViewAdapter;
    private LinearLayoutCompat noWallpapersMessage;
    private SearchView searchView;
    private String searchText;

    private BroadcastReceiver wallpaperAddedMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadWallpapers();
        }
    };

    private BroadcastReceiver wallpaperUpdateCompleteReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadWallpapers();
        }
    };

    private BroadcastReceiver wallpaperDownloadedMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadWallpapers();
        }
    };

    private BroadcastReceiver changeWallpaperDescriptionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final Wallpaper wallpaper = intent.getParcelableExtra(WallpaperListAdapter.WALLPAPER_EXTRA);

            final EditText newDescription = new EditText(MainActivity.this);
            if (wallpaper.getDescription() != null) {
                newDescription.setText(wallpaper.getDescription());
            }

            // Make an alert dialog to allow the user to change the description.
            new AlertDialog.Builder(MainActivity.this).setTitle(R.string.wallpaper_change_description_dialog_title)
                    .setView(newDescription)
                    .setPositiveButton(R.string.wallpaper_change_description_dialog_set_button_text, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            wallpaper.setDescription(newDescription.getText().toString());
                            new UpdateWallpaperTask(MainActivity.this).execute(wallpaper);
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton(R.string.wallpaper_change_description_dialog_cancel_button_text, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.dismiss();
                        }
                    })
                    .show();

        }
    };

    private BroadcastReceiver getAllWallpaperMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<Wallpaper> updatedWallpapers = intent.getParcelableArrayListExtra(GetWallpapersTask.WALLPAPERS_EXTRA);
            currentWallpaper = intent.getParcelableExtra(GetWallpapersTask.CURRENT_WALLPAPER_EXTRA);

            // Update the list of wallpapers.
            wallpapers.clear();

            // Display the no wallpaper message if there are none.
            wallpapers.addAll(updatedWallpapers);
            if (wallpapers.size() == 0) {
                noWallpapersMessage.setVisibility(View.VISIBLE);
                wallpaperRecyclerView.setVisibility(View.GONE);
            } else {
                wallpaperRecyclerViewAdapter.notifyDataSetChanged();
                noWallpapersMessage.setVisibility(View.GONE);
                wallpaperRecyclerView.setVisibility(View.VISIBLE);
            }
        }
    };

    private BroadcastReceiver setWallpaperMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            newCurrentWallpaper = intent.getParcelableExtra(WallpaperListAdapter.WALLPAPER_EXTRA);

            // Show the wallpaper manager's dialog to set the wallpaper.
            Uri uri = WallpaperUtils.getLargeImageUri(context, newCurrentWallpaper);
            Intent changeWallpaperIntent = WallpaperManager.getInstance(context).getCropAndSetWallpaperIntent(uri);
            startActivityForResult(changeWallpaperIntent, SET_WALLPAPER_REQUEST);
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
            loadWallpapers();
        }
    };

    private BroadcastReceiver wallpaperSetCompleteMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Reload the wallpapers to update the newly set wallpaper.
            loadWallpapers();
            Toast.makeText(context, R.string.wallpaper_set, Toast.LENGTH_SHORT).show();
        }
    };

    // Starts the GetWallpapersTask task.
    private void loadWallpapers() {
        new GetWallpapersTask(this).execute(searchText);
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
        // Show all wallpapers.
        loadWallpapers();
    }

    // Inflate the action bar menu's options.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_bar, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);

        // Expand the search view again if a search query was provided.
        if (!TextUtils.isEmpty(searchText)) {
            searchMenuItem.expandActionView();
        }

        searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        // Don't use the full screen keyboard by setting the IME options.
            // Get the current IME options to OR with.
        int options = searchView.getImeOptions();
        searchView.setImeOptions(options|EditorInfo.IME_FLAG_NO_EXTRACT_UI);

        searchView.setOnQueryTextListener(this);

        // Fill in the search field if a search query was provided.
        if (!TextUtils.isEmpty(searchText)) {
            searchView.setQuery(searchText, false);
            searchView.clearFocus();
        }

        return super.onCreateOptionsMenu(menu);
    }

    // Handle actions in the action bar.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_local: {
                // Open the dialog to pick content from other applications.
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, getString(R.string.pick_image_message)), PICK_IMAGE_REQUEST);
                return true;
            }
            case R.id.action_add_flickr: {
                // Open the Flickr search activity.
                Intent intent = new Intent(this, FlickrSearchResultActivity.class);
                startActivity(intent);
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Add the content.
            Uri uri = data.getData();
            new ProcessSentImageTask(this).execute(uri);
        } else if (requestCode == SET_WALLPAPER_REQUEST) {
            if (resultCode == RESULT_OK) {
                // Set the current wallpaper to persist.
                new SetCurrentWallpaperTask(this, currentWallpaper).execute(newCurrentWallpaper);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Restore the newCurrentWallpaper and currentWallpaper.
        if (savedInstanceState != null) {
            newCurrentWallpaper = savedInstanceState.getParcelable(NEW_CURRENT_WALLPAPER_KEY);
            currentWallpaper = savedInstanceState.getParcelable(CURRENT_WALLPAPER_KEY);
            searchText = savedInstanceState.getString(SEARCH_TEXT_KEY);
        }

        // Setup broadcast listeners. Broadcasts are used to deal with async tasks and menu options. These are important because the activity instance could be re-created at any time.
        LocalBroadcastManager.getInstance(this).registerReceiver(wallpaperAddedMessageReceiver, new IntentFilter(ProcessSentImageTask.WALLPAPER_ADDED_BROADCAST_INTENT));
        LocalBroadcastManager.getInstance(this).registerReceiver(getAllWallpaperMessageReceiver, new IntentFilter(GetWallpapersTask.GET_WALLPAPERS_BROADCAST_INTENT));
        LocalBroadcastManager.getInstance(this).registerReceiver(setWallpaperMessageReceiver, new IntentFilter(WallpaperListAdapter.SET_WALLPAPER_BROADCAST_INTENT));
        LocalBroadcastManager.getInstance(this).registerReceiver(deleteWallpaperMessageReceiver, new IntentFilter(WallpaperListAdapter.DELETE_WALLPAPER_BROADCAST_INTENT));
        LocalBroadcastManager.getInstance(this).registerReceiver(wallpaperDeletedMessageReceiver, new IntentFilter(DeleteWallpaperTask.WALLPAPER_DELETED_BROADCAST_INTENT));
        LocalBroadcastManager.getInstance(this).registerReceiver(wallpaperSetCompleteMessageReceiver, new IntentFilter(SetCurrentWallpaperTask.WALLPAPER_SET_COMPLETE_BROADCAST_INTENT));
        LocalBroadcastManager.getInstance(this).registerReceiver(wallpaperDownloadedMessageReceiver, new IntentFilter(ProcessDownloadedWallpaperTask.WALLPAPER_DOWNLOADED_BROADCAST_INTENT));
        LocalBroadcastManager.getInstance(this).registerReceiver(changeWallpaperDescriptionReceiver, new IntentFilter(WallpaperListAdapter.CHANGE_WALLPAPER_DESCRIPTION_BROADCAST_INTENT));
        LocalBroadcastManager.getInstance(this).registerReceiver(wallpaperUpdateCompleteReceiver, new IntentFilter(UpdateWallpaperTask.WALLPAPER_UPDATE_COMPLETE_BROADCAST_INTENT));

        processIntent(getIntent());

        // Setup the wallpaper recycler view.
        wallpaperRecyclerView = (RecyclerView) findViewById(R.id.wallpaper_list_recycler_view);
        int deviceOrientation = getResources().getConfiguration().orientation;
        int numberOfColumns = deviceOrientation == Configuration.ORIENTATION_LANDSCAPE ? 2 : 1;
        wallpaperRecyclerViewLayoutManager = new StaggeredGridLayoutManager(numberOfColumns, StaggeredGridLayoutManager.VERTICAL);
        wallpaperRecyclerView.setLayoutManager(wallpaperRecyclerViewLayoutManager);
        wallpaperRecyclerViewAdapter = new WallpaperListAdapter(this, wallpapers);
        wallpaperRecyclerView.setAdapter(wallpaperRecyclerViewAdapter);

        noWallpapersMessage = (LinearLayoutCompat) findViewById(R.id.no_wallpapers_message);
    }

    @Override
    protected void onResume() {
        // Ensure the time as a wallpaper is up to date.
        wallpaperRecyclerViewAdapter.notifyDataSetChanged();
        super.onResume();
    }

    // Unregister from local async events.
    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(wallpaperAddedMessageReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(getAllWallpaperMessageReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(setWallpaperMessageReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(deleteWallpaperMessageReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(wallpaperDeletedMessageReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(wallpaperSetCompleteMessageReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(wallpaperDownloadedMessageReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(changeWallpaperDescriptionReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(wallpaperUpdateCompleteReceiver);

        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        processIntent(intent);
    }

    @Override
    protected void onSaveInstanceState (Bundle outState) {
        // Save the newCurrentWallpaper and currentWallpaper.
        outState.putParcelable(NEW_CURRENT_WALLPAPER_KEY, newCurrentWallpaper);
        outState.putParcelable(CURRENT_WALLPAPER_KEY, currentWallpaper);
        outState.putString(SEARCH_TEXT_KEY, searchText);
    }

    // Do nothing on text submit.
    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    // Search Wallpapers as the user types.
    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText.length() == 0) {
            searchText = null;
        } else {
            searchText = newText;
        }
        loadWallpapers();

        return true;
    }
}
