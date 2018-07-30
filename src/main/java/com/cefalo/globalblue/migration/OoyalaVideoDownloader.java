package com.cefalo.globalblue.migration;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by satyajit on 7/20/18.
 */
public class OoyalaVideoDownloader {

  private static final Logger LOGGER = LoggerFactory.getLogger(OoyalaVideoDownloader.class);

  private static final String OOYALA_SYNDICATION_URL_OLD = "http://cdn-api.ooyala.com/syndication/mp4?id=ad6928a3-07ac-48ff-862b-269641a4c1e3";
  private static final String OOYALA_SYNDICATION_URL_NEW = "http://cdn-api.ooyala.com/syndication/mp4?id=ad6928a3-07ac-48ff-862b-269641a4c1e3&offset=0&limit=1000";
  private static String VIDEO_DIR = "/Users/satyajit/Media/vzaar";
  private static int NTHREAD = 4;

  private static SortedSet<OoyalaVideo> videosOld = new TreeSet<>(Comparator.comparing(OoyalaVideo::getGuid));
  private static SortedSet<OoyalaVideo> videosNew = new TreeSet<>(Comparator.comparing(OoyalaVideo::getGuid));
  private static SortedSet<OoyalaVideo> videosDiff = new TreeSet<>(Comparator.comparing(OoyalaVideo::getGuid));

  private static List<String> MISSING_VIDEOS = Arrays.asList("00M3ZpdDoxp-JbQJfY5fCpRX81KcSYZ1",
      "5oZzVvMDE6XYKMZeY7znvDlRIXRicMuh",
      "8yaDVvMDE6Ww9JQKhNlfegk8DOR_DMWt",
      "BtZzdtMzE62a8g4qFpwnvByO8Cuf4OVj",
      "E3aGpiNDE6WRecCJFL9BqK-JvhimRzn4",
      "g1dGRtMzrWw7PpWG3soNwMpii4UPS1Gc",
      "htYnR3YToMf592thlqj9GuGRQ-_6C_vI",
      "lrd2RtMzoJ75KRcLEKBusq0tGBNZ2lu0",
      "RwdmRtMzqTsI-wtdAz5t9XohIl62LLuj",
      "V0YnJpMTE6bSCdLG8KdBJi-qgSj5vqEc",
      "VwYzVnMjE6da_o_g-ng8CcawvI4Rnc5a",
      "VxNzZuMDE63BQex5iguR9WIOosIAmMVd");

  public static void main(String[] args) {
    try {
      InputStream input500Stream = new FileInputStream(
          "/Users/satyajit/Media/vzaar/ooyala500.xml");
      InputStreamReader file500Reader = new InputStreamReader(input500Stream);
      loadVideoInfos(file500Reader, videosOld);

      InputStream input629Stream = new FileInputStream(
          "/Users/satyajit/Media/vzaar/ooyala629.xml");
      InputStreamReader file629Reader = new InputStreamReader(input629Stream);
      loadVideoInfos(file629Reader, videosNew);
      findDiffs();
      updateOoyalaVideo();
      //listCurlCommands();
      //showVideoInfo();
      download();
    } catch (Exception e) {
      e.printStackTrace();
    }


  }

  private static void findDiffs() {
    //Collection<OoyalaVideo> diffs = CollectionUtils.subtract(videosOld, videosNew);
    videosNew.removeAll(videosOld);
    videosDiff.addAll(videosNew);

    /*videosOld.removeAll(videosNew);
    videosDiff.addAll(videosOld);*/
  }


  private static void showVideoInfo() {
    //videosDiff.sort(Comparator.comparing(OoyalaVideo::getGuid));
    videosDiff.forEach(v -> {
      System.out.println(v.getGuid());
      /*System.out.println(v.getFileName());
      System.out.println(v.getContentUrl());
      System.out.println("------------------------------------------");*/
    });
  }

  private static void download() {
    try {
      ExecutorService pool = Executors.newFixedThreadPool(NTHREAD);
      videosDiff.forEach(v -> pool.submit(new DownloadTask(v.getContentUrl(), v.getFileName())));
      pool.shutdown();

      pool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private static void updateOoyalaVideo() {
    try {
      ExecutorService pool = Executors.newFixedThreadPool(NTHREAD);
      videosDiff.forEach(v -> pool.submit(new UpdateUrlTask(v)));

      pool.shutdown();
      pool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private static String redirectUrl(String url) {
    URL urlTmp = null;
    String redUrl = null;
    HttpURLConnection connection = null;

    try {
      urlTmp = new URL(url);
    } catch (MalformedURLException e1) {
      e1.printStackTrace();
    }

    try {
      connection = (HttpURLConnection) urlTmp.openConnection();
    } catch (IOException e) {
      e.printStackTrace();
    }
    try {
      connection.getResponseCode();
    } catch (IOException e) {
      e.printStackTrace();
    }

    redUrl = connection.getURL().toString();
    connection.disconnect();

    return redUrl;
  }


  private static void loadVideoInfos(InputStreamReader reader, Set<OoyalaVideo> oVideos) {

    try {
      SyndFeedInput input = new SyndFeedInput();
      SyndFeed feed = input.build(reader);
      List<SyndEntry> syndEntries = feed.getEntries();
      for (SyndEntry entry : syndEntries) {

        try {

          OoyalaVideo video = new OoyalaVideo();
          video.setTitle(entry.getTitle());
          video.setGuid(entry.getUri());

          List<Element> foreignMarkups = entry.getForeignMarkup();
          for (Element element : foreignMarkups) {
            if ("description".equalsIgnoreCase(element.getName())
                && element.getContent().size() > 0) {
              video.setDescription(element.getContent().get(0).getValue());
              continue;
            }

            if ("category".equalsIgnoreCase(element.getName()) && element.getContent().size() > 0) {
              element.getContent().forEach(c -> video.addPublication(c.getValue()));
              continue;
            }

            if ("thumbnail".equalsIgnoreCase(element.getName())
                && element.getAttributes().size() > 0) {
              String thumbnailUrl = element.getAttribute("url").getValue();
              video.setThumbnailUrl(thumbnailUrl);
              continue;
            }

            if ("group".equalsIgnoreCase(element.getName()) && element.getChildren().size() > 0) {
              List<Element> groups = element.getChildren();
              for (Element element1 : groups) {
                String bitrate = element1.getAttribute("bitrate").getValue();
                if ("1500".equals(bitrate)) {
                  String url = element1.getAttribute("url").getValue();
                  if (StringUtils.isNotBlank(url)) {
                    video.setContentUrl(url);
                  }

                }
              }
            }
          }
          oVideos.add(video);
        } catch (Exception e) {
          LOGGER.error("Error.", e);
          System.out.println("Entry: " + entry);
        }
      }

    } catch (Exception e) {
      LOGGER.error("Error.", e);
      e.printStackTrace();
    }


  }

  private static void updateContentUrls() {
    videosOld.forEach(v -> v.setContentUrl(redirectUrl(v.getContentUrl())));
  }


  private static void listCurlCommands() {
    videosDiff.forEach(v -> System.out.println(String
        .format("wget  -O \"/home/satyajit/Media/Ooyala/%s\" \"%s\"", v.getFileName(),
            v.getContentUrl())));
  }

  private static void downloadVideo(String fileURL, String filePath) {
    if (StringUtils.isBlank(fileURL)) {
      System.out.println("File URL blank for: " + filePath);
      return;
    }
    try {
      FileUtils.copyURLToFile(new URL(fileURL),
          new File(filePath), 100000, 100000);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  private static void updateContentUrl(OoyalaVideo video) {
    if (StringUtils.isBlank(video.getContentUrl())) {
      return;
    }
    video.setContentUrl(redirectUrl(video.getContentUrl()));
  }

  private static class UpdateUrlTask implements Runnable {

    private OoyalaVideo video;

    public UpdateUrlTask(OoyalaVideo video) {
      this.video = video;
    }

    @Override
    public void run() {
      try {
        updateContentUrl(this.video);
      } catch (Exception e) {
        LOGGER.error("Error updating video: " + video.getFileName(), e);
        e.printStackTrace();
      }
    }
  }

  private static class DownloadTask implements Runnable {

    private String fileUrl;
    private String fileName;

    public DownloadTask(String fileUrl, String fileName) {
      this.fileUrl = fileUrl;
      this.fileName = fileName;
    }

    @Override
    public void run() {
      try {
        System.out
            .println(String.format("START::Downloading: %s from: %s", this.fileName, this.fileUrl));
        downloadVideo(this.fileUrl,
            String.format("%s%s%s", VIDEO_DIR, File.separator, this.fileName));
        LOGGER.debug(String.format("END::Downloaded: %s", this.fileName));
        System.out.println(String.format("END::Downloaded: %s", this.fileName));
      } catch (Exception e) {
        LOGGER.error("Error downloading video.", e);
        e.printStackTrace();
      }
    }
  }


}

