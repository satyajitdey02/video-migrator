package com.cefalo.globalblue.migration;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by satyajit on 7/20/18.
 */
public class OoyalaVideo {

    private String guid;
    private String title;
    private String description;
    private String type = "video/mp4";
    private int bitRate = 1500;
    private String thumbnailUrl;
    private String contentUrl;
    private List<String> publications = new ArrayList<>();

    public OoyalaVideo() {

    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getBitRate() {
        return bitRate;
    }

    public void setBitRate(int bitRate) {
        this.bitRate = bitRate;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }

    public List<String> getPublications() {
        return publications;
    }

    public void addPublication(String publication) {
        if (StringUtils.containsIgnoreCase(publication, "ru")
                || StringUtils.containsIgnoreCase(publication, "russian")) {
            this.publications.add("globalblueru");
            return;
        }

        if (StringUtils.containsIgnoreCase(publication, "zh")
                || StringUtils.containsIgnoreCase(publication, "cn")
                || StringUtils.containsIgnoreCase(publication, "chinese")) {
            this.publications.add("globalbluezh");
            return;
        }

        this.publications.add("globalblue");

    }

    public String getFileName() {
        String descriptionOrTitle = this.getDescription();
        if (StringUtils.isBlank(this.getDescription()) || this.getDescription().length() > 200) {
            descriptionOrTitle = this.getTitle();
        }
        descriptionOrTitle.replaceAll("\\/", ".").replaceAll("\\\\",".").replaceAll("\n", " ");

        String publications = String.join(".", this.publications);
        return String.format("%s---%s---%s.mp4", descriptionOrTitle, this.getGuid(), publications);
    }


    @Override
    public String toString() {
        return "OoyalaVideo{" +
                "guid='" + guid + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                ", bitRate=" + bitRate +
                ", thumbnailUrl='" + thumbnailUrl + '\'' +
                ", contentUrl='" + contentUrl + '\'' +
                ", publication='" + publications + '\'' +
                '}';
    }
}
