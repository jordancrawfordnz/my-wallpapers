package kiwi.jordancrawford.mywallpapers;

import android.os.AsyncTask;

/**
 * Created by Jordan on 30/08/16.
 */
public interface OnTaskCompleted {
    public void onTaskCompleted(String taskKey, AsyncTask task);
}
