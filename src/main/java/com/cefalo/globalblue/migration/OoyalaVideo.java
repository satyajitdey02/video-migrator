package com.cefalo.globalblue.migration;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

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
    descriptionOrTitle.replaceAll("\\/", ".").replaceAll("\\\\", ".").replaceAll("\n", " ");

    String publications = CollectionUtils.isEmpty(this.publications) ?
        "globalblue" : String.join(".", this.publications);
    return String.format("%s---%s---%s.mp4", descriptionOrTitle, this.getGuid(), publications);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final OoyalaVideo other = (OoyalaVideo) obj;
    if (guid == null) {
      if (other.guid != null) {
        return false;
      }
    } else if (!guid.equals(other.guid)) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result
        + ((guid == null) ? 0 : guid.hashCode());
    return result;
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
