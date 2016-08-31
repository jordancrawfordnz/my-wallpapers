package kiwi.jordancrawford.mywallpapers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Gets all wallpapers from the database asynchronously.
 * Created by Jordan on 31/08/16.
 */
public class GetAllWallpapers extends AsyncTask<Void, Void, ArrayList<Wallpaper>> {
    public static final String GET_ALL_WALLPAPERS_BROADCAST_INTENT = "get_all_wallpapers_message";
    public static final String ALL_WALLPAPERS_EXTRA = "all_wallpapers";

    private Context context;
    public GetAllWallpapers(Context context) {
        super();
        this.context = context.getApplicationContext();
    }

    @Override
    protected ArrayList<Wallpaper> doInBackground(Void... params) {
        return WallpaperDbHelper.getInstance(context).getAllWallpapers();
    }

    protected void onPostExecute(ArrayList<Wallpaper> result) {
        if (result != null) {
            Intent intent = new Intent(GET_ALL_WALLPAPERS_BROADCAST_INTENT);
            intent.putParcelableArrayListExtra(ALL_WALLPAPERS_EXTRA, result);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }
    }
}
