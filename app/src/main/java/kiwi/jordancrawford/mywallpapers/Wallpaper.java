package kiwi.jordancrawford.mywallpapers;

/**
 * Created by Jordan on 30/08/16.
 */
public class Wallpaper {
    private String smallPictureFilename, largePictureFilename;
    long id = -1;
    int daysAsWallpaper = -1;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSmallPictureFilename() {
        return smallPictureFilename;
    }

    public void setSmallPictureFilename(String smallPictureFilename) {
        this.smallPictureFilename = smallPictureFilename;
    }

    public String getLargePictureFilename() {
        return largePictureFilename;
    }

    public void setLargePictureFilename(String largePictureFilename) {
        this.largePictureFilename = largePictureFilename;
    }

    public int getDaysAsWallpaper() {
        return daysAsWallpaper;
    }

    public void setDaysAsWallpaper(int daysAsWallpaper) {
        this.daysAsWallpaper = daysAsWallpaper;
    }

    @Override
    public String toString() {
        return "Wallpaper{" +
                "smallPictureFilename='" + smallPictureFilename + '\'' +
                ", largePictureFilename='" + largePictureFilename + '\'' +
                ", id=" + id +
                ", daysAsWallpaper=" + daysAsWallpaper +
                '}';
    }

}
