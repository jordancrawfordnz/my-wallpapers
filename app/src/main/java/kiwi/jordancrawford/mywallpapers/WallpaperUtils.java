package kiwi.jordancrawford.mywallpapers;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Jordan on 31/08/16.
 */
public class WallpaperUtils {
    private static final double SMALL_IMAGE_SCALE_MAX_DIMENSION = 512.0;
    private static final int LARGE_IMAGE_MAX_DIMENSION = 3000;
    private static final String SMALL_IMAGES_DIR = "small_images";
    private static final String LARGE_IMAGES_DIR = "large_images";

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

    private static void writeImageToFile(Bitmap toWrite, File directory, String fileName) throws IOException {
        File imageFile = new File(directory, fileName);
        FileOutputStream imageFileStream = new FileOutputStream(imageFile);
        toWrite.compress(Bitmap.CompressFormat.JPEG, 85, imageFileStream);
        imageFileStream.flush();
        imageFileStream.close();
    }

    // Stores WallpaperBitmaps in the file system and saves the Wallpaper in the database.
    public static Wallpaper createWallpaperFromBitmaps(Context context, WallpaperBitmaps imagesToUse) throws IOException {
        // Setup the directories for images.
        File smallImagesDir = context.getDir(SMALL_IMAGES_DIR, Context.MODE_PRIVATE);
        if (!smallImagesDir.exists())
            smallImagesDir.mkdirs();

        File largeImagesDir = context.getDir(LARGE_IMAGES_DIR, Context.MODE_PRIVATE);
        if (!largeImagesDir.exists())
            smallImagesDir.mkdirs();

        // Create the wallpaper.
        Wallpaper wallpaper = new Wallpaper();
        wallpaper.setDaysAsWallpaper(0);

        // Add the wallpaper to the database.
        WallpaperDbHelper.getInstance(context).addWallpaper(wallpaper);

        String fileName = wallpaper.getId() + ".jpg";

        // Save the images.
        writeImageToFile(imagesToUse.getSmallImage(), smallImagesDir, fileName);
        writeImageToFile(imagesToUse.getLargeImage(), largeImagesDir, fileName);

        return wallpaper;
    }
}
