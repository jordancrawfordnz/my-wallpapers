package kiwi.jordancrawford.mywallpapers;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Represents a Wallpaper. This is Parcelable so it can be passed with intents.
 *
 * Created by Jordan on 30/08/16.
 */
public class Wallpaper implements Parcelable {
    long id = -1, wallpaperSince = -1;
    boolean isCurrent;
    int daysAsWallpaper = -1;
    String description;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Wallpaper{" +
                "id=" + id +
                ", wallpaperSince=" + wallpaperSince +
                ", isCurrent=" + isCurrent +
                ", daysAsWallpaper=" + daysAsWallpaper +
                ", description='" + description + '\'' +
                '}';
    }

    public Wallpaper() {}

    protected Wallpaper(Parcel in) {
        id = in.readLong();
        daysAsWallpaper = in.readInt();
        isCurrent = in.readByte() != 0;
        wallpaperSince = in.readLong();
        description = in.readString();
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
        dest.writeString(description);
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
