package kiwi.jordancrawford.mywallpapers;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jordan on 30/08/16.
 */
public class Wallpaper implements Parcelable {
    long id = -1, wallpaperSince = -1;
    boolean isCurrent;
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

    public long getWallpaperSince() {
        return wallpaperSince;
    }

    public void setWallpaperSince(long wallpaperSince) {
        this.wallpaperSince = wallpaperSince;
    }

    public boolean isCurrent() {
        return isCurrent;
    }

    public void setCurrent(boolean current) {
        isCurrent = current;
    }

    @Override
    public String toString() {
        return "Wallpaper{" +
                "id=" + id +
                ", wallpaperSince=" + wallpaperSince +
                ", isCurrent=" + isCurrent +
                ", daysAsWallpaper=" + daysAsWallpaper +
                '}';
    }

    public Wallpaper() {}

    protected Wallpaper(Parcel in) {
        id = in.readLong();
        daysAsWallpaper = in.readInt();
        isCurrent = in.readByte() != 0;
        wallpaperSince = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeInt(daysAsWallpaper);
        dest.writeByte((byte) (isCurrent ? 1 : 0));
        dest.writeLong(wallpaperSince);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Wallpaper> CREATOR = new Parcelable.Creator<Wallpaper>() {
        @Override
        public Wallpaper createFromParcel(Parcel in) {
            return new Wallpaper(in);
        }

        @Override
        public Wallpaper[] newArray(int size) {
            return new Wallpaper[size];
        }
    };

}
