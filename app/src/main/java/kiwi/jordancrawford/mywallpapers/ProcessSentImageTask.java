package kiwi.jordancrawford.mywallpapers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import java.io.IOException;

/**
 * Does all the processing on images sent to the activity. After completion, sends a broadcast to the activity (if it is around) to refresh the list of Wallpapers.
 *
 * Created by Jordan on 31/08/16.
 */
public class ProcessSentImageTask extends AsyncTask<Uri, Void, Wallpaper> {
    public static final String WALLPAPER_ADDED_BROADCAST_INTENT = "wallpaper_added_message";
    private Context context;

    public ProcessSentImageTask(Context context) {
        super();
        this.context = context.getApplicationContext();
    }

    @Override
    protected Wallpaper doInBackground(Uri... uris) {
        // Process the image.
        if (uris.length == 0) {
            return null;
        }
        Uri imageUri = uris[0];
        try {
            WallpaperBitmaps wallpaperBitmaps = WallpaperUtils.getProcessedBitmapsFromUri(context, imageUri);

            // Create the Wallpaper by persisting it.
            Wallpaper createdWallpaper = WallpaperUtils.createWallpaperFromBitmaps(context, wallpaperBitmaps);

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
            Toast.makeText(context, R.string.sent_content_received, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, R.string.sent_content_error, Toast.LENGTH_LONG).show();
        }
    }
}
