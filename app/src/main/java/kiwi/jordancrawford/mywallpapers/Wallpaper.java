package kiwi.jordancrawford.mywallpapers;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jordan on 30/08/16.
 */
public class Wallpaper implements Parcelable {
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

    public Wallpaper() {}

    protected Wallpaper(Parcel in) {
        id = in.readLong();
        daysAsWallpaper = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeInt(daysAsWallpaper);
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
