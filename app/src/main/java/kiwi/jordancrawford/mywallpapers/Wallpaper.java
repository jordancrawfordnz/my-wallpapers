package kiwi.jordancrawford.mywallpapers;

/**
 * Created by Jordan on 30/08/16.
 */
public class Wallpaper {
    long id = -1;
    int daysAsWallpaper = -1;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
                "id=" + id +
                ", daysAsWallpaper=" + daysAsWallpaper +
                '}';
    }

}
