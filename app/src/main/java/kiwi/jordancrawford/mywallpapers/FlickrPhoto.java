package kiwi.jordancrawford.mywallpapers;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jordan on 1/09/16.
 */
public class FlickrPhoto implements Parcelable {
    String id, farmId, serverId, secret;
    boolean chosen = false;

    @Override
    public String toString() {
        return "FlickrPhoto{" +
                "id='" + id + '\'' +
                ", farmId='" + farmId + '\'' +
                ", serverId='" + serverId + '\'' +
                ", secret='" + secret + '\'' +
                ", chosen=" + chosen +
                '}';
    }

    private String getBaseUrl() {
        return "https://farm" + farmId + ".staticflickr.com/" + serverId + "/" + id + "_" + secret;
    }

    public String getSmallUrl() {
        return getBaseUrl() + ".jpg";
    }

    public String getThumbnailUrl() {
        return getBaseUrl() + "_n.jpg";
    }

    public String getLargeUrl() {
        return getBaseUrl() + "_h.jpg";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFarmId() {
        return farmId;
    }

    public void setFarmId(String farmId) {
        this.farmId = farmId;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public boolean isChosen() {
        return chosen;
    }

    public void setChosen(boolean chosen) {
        this.chosen = chosen;
    }

    public FlickrPhoto() {}

    protected FlickrPhoto(Parcel in) {
        id = in.readString();
        farmId = in.readString();
        serverId = in.readString();
        secret = in.readString();
        chosen = in.readByte() != 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(farmId);
        dest.writeString(serverId);
        dest.writeString(secret);
        dest.writeByte((byte) (chosen ? 1 : 0));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<FlickrPhoto> CREATOR = new Parcelable.Creator<FlickrPhoto>() {
        @Override
        public FlickrPhoto createFromParcel(Parcel in) {
            return new FlickrPhoto(in);
        }

        @Override
        public FlickrPhoto[] newArray(int size) {
            return new FlickrPhoto[size];
        }
    };
}
