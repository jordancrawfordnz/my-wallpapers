package kiwi.jordancrawford.mywallpapers;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;

import java.util.ArrayList;

/**
 * Gets all wallpapers from the database asynchronously.
 *
 * Created by Jordan on 31/08/16.
 */
public class GetWallpapersTask extends AsyncTask<String, Void, ArrayList<Wallpaper>> {
    public static final String GET_WALLPAPERS_BROADCAST_INTENT = "get_wallpapers_message";
    public static final String WALLPAPERS_EXTRA = "wallpapers";
    public static final String CURRENT_WALLPAPER_EXTRA = "current_wallpaper";

    private Context context;
    public GetWallpapersTask(Context context) {
        super();
        this.context = context.getApplicationContext();
    }

    @Override
    protected ArrayList<Wallpaper> doInBackground(String... params) {
        if (params.length > 0) {
            return WallpaperDbHelper.getInstance(context).searchForWallpaper(params[0]);
        }
        return null;
    }

    protected void onPostExecute(ArrayList<Wallpaper> result) {
        if (result != null) {
            Intent intent = new Intent(GET_WALLPAPERS_BROADCAST_INTENT);
            intent.putParcelableArrayListExtra(WALLPAPERS_EXTRA, result);
            intent.putExtra(CURRENT_WALLPAPER_EXTRA, WallpaperUtils.getCurrentWallpaper(result));
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }
    }
}
