package kiwi.jordancrawford.mywallpapers;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Update the provided wallpaper in the database.
 *
 * Created by Jordan on 5/09/16.
 */
public class UpdateWallpaperTask extends AsyncTask<Wallpaper, Void, Void> {
    public static final String WALLPAPER_UPDATE_COMPLETE_BROADCAST_INTENT = "wallpaper_update_complete_message";
    private Context context;

    public UpdateWallpaperTask(Context context) {
        super();
        this.context = context.getApplicationContext();
    }

    @Override
    protected Void doInBackground(Wallpaper... wallpapers) {
        if (wallpapers.length == 0) {
            return null;
        }
        Wallpaper wallpaper = wallpapers[0];

        WallpaperDbHelper.getInstance(context).updateWallpaper(wallpaper);

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        // Broadcast that the update is done.
        Intent intent = new Intent(WALLPAPER_UPDATE_COMPLETE_BROADCAST_INTENT);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
