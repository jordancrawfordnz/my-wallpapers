package kiwi.jordancrawford.mywallpapers;

/**
 * Created by Jordan on 1/09/16.
 */
public class FlickrPhoto {
    String id, farmId, serverId, secret;

    @Override
    public String toString() {
        return "FlickrPhoto{" +
                "id='" + id + '\'' +
                ", farmId='" + farmId + '\'' +
                ", serverId='" + serverId + '\'' +
                ", secret='" + secret + '\'' +
                '}';
    }

    private String getBaseUrl() {
        return "https://farm" + farmId + ".staticflickr.com/" + serverId + "/" + id + "_" + secret;
    }

    public String getSmallUrl() {
        return getBaseUrl() + ".jpg";
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
}
