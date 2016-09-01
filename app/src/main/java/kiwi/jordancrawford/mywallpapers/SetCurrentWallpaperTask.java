package kiwi.jordancrawford.mywallpapers;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;

import java.util.ArrayList;

/**
 * Created by Jordan on 1/09/16.
 */
public class SetCurrentWallpaperTask extends AsyncTask<Wallpaper, Void, Void> {
    public static final String WALLPAPER_SET_COMPLETE_BROADCAST_INTENT = "wallpaper_set_complete_message";
    private Context context;
    private Wallpaper currentWallpaper;

    public SetCurrentWallpaperTask(Context context, Wallpaper currentWallpaper) {
        super();
        this.context = context.getApplicationContext();
        this.currentWallpaper = currentWallpaper;
    }

    @Override
    protected Void doInBackground(Wallpaper... wallpapers) {
        if (wallpapers.length == 0) {
            return null;
        }
        Wallpaper wallpaper = wallpapers[0];
        WallpaperUtils.setNewWallpaper(context, currentWallpaper, wallpaper);

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        // Broadcast that the delete is done.
        Intent intent = new Intent(WALLPAPER_SET_COMPLETE_BROADCAST_INTENT);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

}
