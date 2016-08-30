package kiwi.jordancrawford.mywallpapers;

/**
 * Created by Jordan on 30/08/16.
 */
public class Config {
    private int setWallpaperDate;
    private int currentWallpaperId;

    public int getCurrentWallpaperId() {
        return currentWallpaperId;
    }

    public void setCurrentWallpaperId(int currentWallpaperId) {
        this.currentWallpaperId = currentWallpaperId;
    }

    public int getSetWallpaperDate() {
        return setWallpaperDate;
    }

    public void setSetWallpaperDate(int setWallpaperDate) {
        this.setWallpaperDate = setWallpaperDate;
    }

    @Override
    public String toString() {
        return "Config{" +
                "setWallpaperDate=" + setWallpaperDate +
                ", currentWallpaperId=" + currentWallpaperId +
                '}';
    }

}
