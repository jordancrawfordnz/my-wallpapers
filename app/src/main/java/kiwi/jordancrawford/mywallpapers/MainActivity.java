package kiwi.jordancrawford.mywallpapers;

import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements OnTaskCompleted {

    private final int PICK_IMAGE_REQUEST = 1;
    private final int SET_WALLPAPER_REQUEST = 2;
    private final String INTENT_USED_KEY = "used";

    private ImageView sentImageDisplay;

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

    public void onTaskCompleted(String taskKey, AsyncTask task) {
        if (taskKey.equals(ProcessSentImage.TASK_KEY)) {
            ProcessSentImage processSentImageTask = (ProcessSentImage) task;
            try {
                WallpaperBitmaps bitmaps = processSentImageTask.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        String intentAction = intent.getAction();
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

        if (Intent.ACTION_SEND.equals(intentAction) && !intent.hasExtra(INTENT_USED_KEY)) {
            // Mark the intent as used.
            intent.putExtra(INTENT_USED_KEY, true);

            // Check this is an image.
            if (intent.getType().startsWith("image/")) {
                Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
                new ProcessSentImage(this).execute(imageUri);
        }

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
}
