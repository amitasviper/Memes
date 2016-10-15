package models;

/**
 * Created by viper on 13/09/16.
 */
public class ImageDetails {
    private String url;
    private String description;
    private int likes;


    public ImageDetails(String url, String description) {
        this.url = url;
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
