package kiwi.jordancrawford.mywallpapers;

import android.graphics.Bitmap;

/**
 * A small object wrapper that stores the two bitmaps used to create a Wallpaper. This is required to process a Wallpaper.
 *
 * Created by Jordan on 30/08/16.
 */
public class WallpaperBitmaps {
    Bitmap smallImage, largeImage;

    public Bitmap getSmallImage() {
        return smallImage;
    }

    public void setSmallImage(Bitmap smallImage) {
        this.smallImage = smallImage;
    }

    public Bitmap getLargeImage() {
        return largeImage;
    }

    public void setLargeImage(Bitmap largeImage) {
        this.largeImage = largeImage;
    }

    @Override
    public String toString() {
        return "WallpaperBitmaps{" +
                "smallImage=" + smallImage +
                ", largeImage=" + largeImage +
                '}';
    }
}
