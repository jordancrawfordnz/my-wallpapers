package kiwi.jordancrawford.mywallpapers;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;

/**
 * Created by Jordan on 31/08/16.
 */
public class WallpaperUtils {
    private static final double SMALL_IMAGE_SCALE_MAX_DIMENSION = 512.0;
    private static final int LARGE_IMAGE_MAX_DIMENSION = 3000;
    private static final String SMALL_IMAGES_DIR = "small_images";
    private static final String LARGE_IMAGES_DIR = "large_images";
    private static final String IMAGE_URI_PREFIX = "kiwi.jordancrawford.mywallpapers";

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

    // Gets the directory that holds the small images.
    private static File getSmallImagesDir(Context context) {
        File filesDir = context.getFilesDir();
        File smallImagesDir = new File(filesDir, SMALL_IMAGES_DIR + File.separator);
        if (!smallImagesDir.exists())
            smallImagesDir.mkdirs();
        return smallImagesDir;
    }

    // Gets the directory that holds the large images.
    private static File getLargeImagesDir(Context context) {
        File filesDir = context.getFilesDir();
        File largeImagesDir = new File(filesDir, LARGE_IMAGES_DIR + File.separator);
        if (!largeImagesDir.exists())
            largeImagesDir.mkdirs();
        return largeImagesDir;
    }

    // Writes a bitmap to the specified file.
    private static void writeImageToFile(Bitmap toWrite, File imageFile) throws IOException {
        FileOutputStream imageFileStream = new FileOutputStream(imageFile);
        toWrite.compress(Bitmap.CompressFormat.JPEG, 85, imageFileStream);
        imageFileStream.flush();
        imageFileStream.close();
    }

    // Gets a file reference for a wallpaper's image in a directory.
    private static File getFileForImage(File directory, Wallpaper wallpaper) {
        if (wallpaper.getId() == -1) {
            throw new IllegalArgumentException("The wallpaper has no ID provided. Is it saved?");
        }
        String fileName = wallpaper.getId() + ".jpg";
        return new File(directory, fileName);
    }

    // Gets the small image file for a wallpaper.
    private static File getSmallImageFile(Context context, Wallpaper wallpaper) {
        return getFileForImage(getSmallImagesDir(context), wallpaper);
    }

    // Gets the large image file for a wallpaper.
    private static File getLargeImageFile(Context context, Wallpaper wallpaper) {
        return getFileForImage(getLargeImagesDir(context), wallpaper);
    }

    // Get the URI for a the wallpapers small image.
    public static Uri getSmallImageUri(Context context, Wallpaper wallpaper) {
        return FileProvider.getUriForFile(context, IMAGE_URI_PREFIX, getSmallImageFile(context, wallpaper));
    }

    // Get the URI for a the wallpapers large image.
    public static Uri getLargeImageUri(Context context, Wallpaper wallpaper) {
        return FileProvider.getUriForFile(context, IMAGE_URI_PREFIX, getLargeImageFile(context, wallpaper));
    }

    // Deletes a wallpaper from the database and the internal storage.
    public static void deleteWallpaper(Context context, Wallpaper wallpaperToDelete) {
        // Delete the wallpaper from the database.
        WallpaperDbHelper.getInstance(context).deleteWallpaper(wallpaperToDelete);

        // Delete the images from the filesystem.
        File largeImage = getLargeImageFile(context, wallpaperToDelete);
        File smallImage = getSmallImageFile(context, wallpaperToDelete);
        largeImage.delete();
        smallImage.delete();
    }

    // Stores WallpaperBitmaps in the file system and saves the Wallpaper in the database.
    public static Wallpaper createWallpaperFromBitmaps(Context context, WallpaperBitmaps imagesToUse) throws IOException {
        // Create the wallpaper.
        Wallpaper wallpaper = new Wallpaper();
        wallpaper.setDaysAsWallpaper(0);

        // Add the wallpaper to the database.
        WallpaperDbHelper.getInstance(context).addWallpaper(wallpaper);

        // Save the images.
        writeImageToFile(imagesToUse.getSmallImage(), getSmallImageFile(context, wallpaper));
        writeImageToFile(imagesToUse.getLargeImage(), getLargeImageFile(context, wallpaper));

        return wallpaper;
    }
}
