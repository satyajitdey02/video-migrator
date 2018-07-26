package com.cefalo.globalblue.migration;

import com.vzaar.Pages;
import com.vzaar.Video;
import com.vzaar.Vzaar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by satyajit on 7/23/18.
 */
public class VzaarVideoUploader {

  private static final String CLIENT_ID = "yah1997-thou";
  private static final String AUTH_TOKEN = "D_dALLPyvJxQDf9rBMKW";
  private static final Vzaar VZAAR = Vzaar.make(CLIENT_ID, AUTH_TOKEN);

  public static void main(String[] args) {
    listVideos();
  }

  private static void listVideos() {
    String str = "London is one of the world’s most prominent cities when it comes to chic menswear. We asked the design duo behind Casely-Hayford which side of the British capital offers the best clothes for men---03cXQ4ODoU420P6i506QRJTWhayXEaNV---globalblue.mp4\n";

    Pattern p = Pattern.compile("---(.*)---");
    Matcher m = p.matcher(str);
    m.find();
    String text = m.group(1);
    System.out.println(text);
    /*List<Video> videos = Pages.list(VZAAR.videos().list().results());
    videos.forEach(v -> {
      System.out.println(v.getAccountId());
      System.out.println(v.getCreatedAt());
      System.out.println(v.getId());
      System.out.println(v.getTitle());
      System.out.println(v.getDescription());
      System.out.println(v.getUrl());
      *//*v.getRenditions().forEach(r-> {
        System.out.println(r.getBitrate());
        System.out.println(r.getHeight());
        System.out.println(r.getWidth());
        System.out.println(r.getStatus());
      });*//*
      System.out.println(v.getThumbnailUrl());
      System.out.println(v.getEmbedCode());
    });*/
  }

}
