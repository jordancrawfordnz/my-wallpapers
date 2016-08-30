package kiwi.jordancrawford.mywallpapers;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.IOException;

/**
 * Processes the URI of a sent image in the background, returning a WallpaperBitmaps object.
 *
 * Created by Jordan on 30/08/16.
 */
public class ProcessSentImage extends AsyncTask<Uri, Void, WallpaperBitmaps> {
    private final double SMALL_IMAGE_SCALE_MAX_DIMENSION = 512.0;
    private final int LARGE_IMAGE_MAX_DIMENSION = 3000;
    private Context context;
    private Exception exception = null;
    private OnTaskCompleted callback;

    public static final String TASK_KEY = "process_sent_image";

    public ProcessSentImage(Context context, OnTaskCompleted callback) {
        super();
        this.context = context.getApplicationContext();
        this.callback = callback;
    }

    private int getAdjustedImageDimension(int currentSize, int largestDimension) {
        return (int) (currentSize * (SMALL_IMAGE_SCALE_MAX_DIMENSION / largestDimension));
    }

    private Bitmap getSmallVersion(Bitmap image) {
        int smallWidth, smallHeight;
        // Scale the image to its smallest size.
        if (image.getWidth() > image.getHeight()) {
            smallWidth = (int) SMALL_IMAGE_SCALE_MAX_DIMENSION;
            smallHeight = getAdjustedImageDimension(image.getHeight(),image.getWidth());
        } else {
            smallWidth =  getAdjustedImageDimension(image.getWidth(), image.getHeight());
            smallHeight = (int) SMALL_IMAGE_SCALE_MAX_DIMENSION;
        }

        Bitmap scaledImage = Bitmap.createScaledBitmap(image, smallWidth, smallHeight, true);
        return scaledImage;
    }

    @Override
    protected WallpaperBitmaps doInBackground(Uri... uris) {
        WallpaperBitmaps bitmapsToReturn = new WallpaperBitmaps();
        if (uris.length > 1) {
            Uri imageUri = uris[0];

            // Check this image Uri exists.
            if (imageUri != null) {
                // Try display the content.
                try {
                    Bitmap largeImage = ExifUtil.getCorrectlyOrientedImage(context, imageUri, LARGE_IMAGE_MAX_DIMENSION);
                    Bitmap smallImage = getSmallVersion(largeImage);

                    bitmapsToReturn.setLargeImage(largeImage);
                    bitmapsToReturn.setSmallImage(smallImage);
                } catch (IOException exception) {
                    this.exception = exception;
                }
            }
        }
        return bitmapsToReturn;
    }

    protected void onPostExecute(WallpaperBitmaps result) {
        if (exception != null || result.getLargeImage() == null || result.getSmallImage() == null) {
            Toast.makeText(context, R.string.sent_content_error, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, R.string.sent_content_received, Toast.LENGTH_SHORT).show();
        }

        // Tell the callback that we've finished.
        callback.onTaskCompleted(TASK_KEY, this);
    }
}
