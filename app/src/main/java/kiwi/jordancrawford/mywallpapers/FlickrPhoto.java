package kiwi.jordancrawford.mywallpapers;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Represents a photo result from Flickr.
 * This is Parcelable so it can be passed in a LocalBroadcast intent.
 *
 * Created by Jordan on 1/09/16.
 */
public class FlickrPhoto implements Parcelable {
    String id, farmId, serverId, secret, description;

    @Override
    public String toString() {
        return "FlickrPhoto{" +
                "id='" + id + '\'' +
                ", farmId='" + farmId + '\'' +
                ", serverId='" + serverId + '\'' +
                ", secret='" + secret + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    // Gets the base image URL for Flickr without the extension or size parameter.
    private String getBaseUrl() {
        return "https://farm" + farmId + ".staticflickr.com/" + serverId + "/" + id + "_" + secret;
    }

    // Gets the URL of the small image. This is the size that is kept as a wallpaper preview.
    public String getSmallUrl() {
        return getBaseUrl() + ".jpg";
    }

    // Gets the URL of the thumbnail image. This is the size that is shown in Flickr search results.
    public String getThumbnailUrl() {
        return getBaseUrl() + "_n.jpg";
    }

    // Gets the URL of the large image. This is the size that is kept to be used when the user sets the wallpaper.
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public FlickrPhoto() {}

    protected FlickrPhoto(Parcel in) {
        id = in.readString();
        farmId = in.readString();
        serverId = in.readString();
        secret = in.readString();
        description = in.readString();
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
        dest.writeString(description);
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
