package kiwi.jordancrawford.mywallpapers;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import java.io.IOException;

/**
 * Created by Jordan on 31/08/16.
 */
public class WallpaperUtils {
    private static final double SMALL_IMAGE_SCALE_MAX_DIMENSION = 512.0;
    private static final int LARGE_IMAGE_MAX_DIMENSION = 3000;

    // Determines the secondary dimension to use when given its current size and the size of the largest dimension (which will become SMALL_IMAGE_SCALE_MAX_DIMENSION).
    private static int getAdjustedSmallImageDimension(int currentSize, int largestDimension) {
        return (int) (currentSize * (SMALL_IMAGE_SCALE_MAX_DIMENSION / largestDimension));
    }

    // Generates a small bitmap of the provided image. This is used for thumbnail display.
    private static Bitmap getSmallVersion(Bitmap image) {
        int smallWidth, smallHeight;
        // Scale the image to its smallest size.
        if (image.getWidth() > image.getHeight()) {
            smallWidth = (int) SMALL_IMAGE_SCALE_MAX_DIMENSION;
            smallHeight = getAdjustedSmallImageDimension(image.getHeight(),image.getWidth());
        } else {
            smallWidth =  getAdjustedSmallImageDimension(image.getWidth(), image.getHeight());
            smallHeight = (int) SMALL_IMAGE_SCALE_MAX_DIMENSION;
        }

        Bitmap scaledImage = Bitmap.createScaledBitmap(image, smallWidth, smallHeight, true);
        return scaledImage;
    }

    // Generates adjusted images from an image URI.
        // This ensures images are rotated correctly and that there are two sizes, one large (may be reduced if the original is too big) and one small (for preview).
    public static WallpaperBitmaps getProcessedBitmapsFromUri(Context context, Uri imageUri) throws IOException {
        WallpaperBitmaps bitmapsToReturn = new WallpaperBitmaps();

        Bitmap largeImage = ExifUtil.getCorrectlyOrientedImage(context, imageUri, LARGE_IMAGE_MAX_DIMENSION);
        Bitmap smallImage = getSmallVersion(largeImage);

        bitmapsToReturn.setLargeImage(largeImage);
        bitmapsToReturn.setSmallImage(smallImage);

        return bitmapsToReturn;
    }

    // Stores WallpaperBitmaps in the file system and saves the Wallpaper in the database.
    public static Wallpaper createWallpaperFromBitmaps(WallpaperBitmaps imagesToUse) {
        return new Wallpaper();
    }
}
