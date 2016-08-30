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

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private final int PICK_IMAGE_REQUEST = 1;
    private final int SET_WALLPAPER_REQUEST = 2;
    private final String INTENT_USED_KEY = "used";
    public final String WALLPAPER_ADDED_BROADCAST_INTENT = "wallpaper_added_message";
    private ImageView sentImageDisplay;


    private BroadcastReceiver wallpaperAddedMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("Activity will refresh");
        }
    };

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sentImageDisplay = (ImageView) findViewById(R.id.sent_image_display);

        // Evil blocking
        List<Wallpaper> wallpapers = WallpaperDbHelper.getInstance(this).getAllWallpapers();
        System.out.println("Existing wallpapers");
        for (Wallpaper currentWallpaper : wallpapers) {
            System.out.println(currentWallpaper.toString());
        }

        Wallpaper testWallpaper = new Wallpaper();
        testWallpaper.setLargePictureFilename("something large");
        testWallpaper.setSmallPictureFilename("something small");
        testWallpaper.setDaysAsWallpaper(50);

        System.out.println("Added wallpaper");
        WallpaperDbHelper.getInstance(this).addWallpaper(testWallpaper);
        System.out.println(testWallpaper.toString());

        List<Wallpaper> wallpapersPostAdd = WallpaperDbHelper.getInstance(this).getAllWallpapers();
        System.out.println("Existing wallpapers post add");
        for (Wallpaper currentWallpaper : wallpapersPostAdd) {
            System.out.println(currentWallpaper.toString());
        }

        System.out.println(WallpaperDbHelper.getInstance(this).deleteWallpaper(testWallpaper));

        List<Wallpaper> wallpapersPostDelete = WallpaperDbHelper.getInstance(this).getAllWallpapers();
        System.out.println("Existing wallpapers post delete");
        for (Wallpaper currentWallpaper : wallpapersPostDelete) {
            System.out.println(currentWallpaper.toString());
        }
        // End evil blocking

        LocalBroadcastManager.getInstance(this).registerReceiver(wallpaperAddedMessageReceiver, new IntentFilter(WALLPAPER_ADDED_BROADCAST_INTENT));

        processIntent(getIntent());

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
        System.out.println("Destory broadcast listener");
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        processIntent(intent);
    }

    // Does all the processing on images sent to the activity. After completion, sents a broadcast to the activity (if it is around) to refresh the list of Wallpapers.
    private class ProcessSentImage extends AsyncTask<Uri, Void, Wallpaper> {
        private Context context;

        public ProcessSentImage(Context context) {
            super();
            this.context = context.getApplicationContext();
        }

        @Override
        protected Wallpaper doInBackground(Uri... uris) {
            System.out.println("Async task running");
            // Process the image.
            if (uris.length == 0) {
                return null;
            }
            Uri imageUri = uris[0];
            try {
                WallpaperBitmaps wallpaperBitmaps = WallpaperUtils.getProcessedBitmapsFromUri(context, imageUri);

                // Create the Wallpaper by persisting it.
                Wallpaper createdWallpaper = WallpaperUtils.createWallpaperFromBitmaps(wallpaperBitmaps);

                return createdWallpaper;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        protected void onPostExecute(Wallpaper result) {
            // Send a broadcast to the activity if there is a result.
            if (result != null) {
                Intent intent = new Intent(WALLPAPER_ADDED_BROADCAST_INTENT);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
        }
    }
}
