package kiwi.jordancrawford.mywallpapers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by Jordan on 31/08/16.
 */
public class DeleteWallpaperTask  extends AsyncTask<Wallpaper, Void, Void> {
    public static final String WALLPAPER_DELETED_BROADCAST_INTENT = "wallpaper_deleted_message";
    private Context context;

    public DeleteWallpaperTask(Context context) {
        super();
        this.context = context.getApplicationContext();
    }

    @Override
    protected Void doInBackground(Wallpaper... wallpapers) {
        if (wallpapers.length == 0) {
            return null;
        }
        Wallpaper wallpaper = wallpapers[0];
        WallpaperUtils.deleteWallpaper(context, wallpaper);

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        // Broadcast that the delete is done.
        Intent intent = new Intent(WALLPAPER_DELETED_BROADCAST_INTENT);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
