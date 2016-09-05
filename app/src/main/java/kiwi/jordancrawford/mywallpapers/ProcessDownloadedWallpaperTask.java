package kiwi.jordancrawford.mywallpapers;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import java.io.IOException;

/**
 * A task that turns downloaded Flickr photos into a Wallpaper.
 *
 * Created by Jordan on 2/09/16.
 */
public class ProcessDownloadedWallpaperTask extends AsyncTask<WallpaperBitmaps, Void, Wallpaper> {
    public static final String WALLPAPER_DOWNLOADED_BROADCAST_INTENT = "wallpaper_downloaded_message";
    public static final String WALLPAPER_EXTRA = "wallpaper";

    private Context context;
    String description;

    public ProcessDownloadedWallpaperTask(Context context, String description) {
        this.context = context.getApplicationContext();
        this.description = description;
    }

    @Override
    protected Wallpaper doInBackground(WallpaperBitmaps... wallpaperBitmaps) {
        if (wallpaperBitmaps.length > 0) {
            WallpaperBitmaps bitmaps = wallpaperBitmaps[0];

            // Create the wallpaper.
            try {
                return WallpaperUtils.createWallpaperFromBitmaps(context, bitmaps, description);
            } catch (IOException e) {
                return null;
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Wallpaper result) {
        if (result != null) {
            // Show a success message.
            Toast.makeText(context, R.string.flickr_image_downloaded, Toast.LENGTH_SHORT).show();

            // Broadcast that the download is done.
            Intent intent = new Intent(WALLPAPER_DOWNLOADED_BROADCAST_INTENT);
            intent.putExtra(WALLPAPER_EXTRA, result);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        } else {
            Toast.makeText(context, R.string.flickr_processing_failed, Toast.LENGTH_LONG).show();
        }
    }
}
