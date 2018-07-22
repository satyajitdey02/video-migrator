package com.cefalo.globalblue.migration;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


/**
 * Created by satyajit on 7/20/18.
 */
public class OoyalaVideoDownloader {

    private static final Logger LOGGER = LoggerFactory.getLogger(OoyalaVideoDownloader.class);

    private static final String OOYALA_SYNDICATION_URL = "http://cdn-api.ooyala.com/syndication/mp4?id=ad6928a3-07ac-48ff-862b-269641a4c1e3";
    private static final int BUFFER_SIZE = 4096;
    private static String VIDEO_DIR = "/home/satyajit/Media/OoyalaVideo";
    private static int NTHREAD = 4;
    private static List<OoyalaVideo> videos = new ArrayList<>();

    public static void main(String[] args) {
        loadVideoInfos();
        updateOoyalaVideo();
        download();
    }

    private static void download() {
        try {
            ExecutorService pool = Executors.newFixedThreadPool(NTHREAD);
            videos.forEach(v -> pool.submit(new DownloadTask(v.getContentUrl(), v.getFileName())));
            pool.shutdown();

            pool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void updateOoyalaVideo() {
        try {
            ExecutorService pool = Executors.newFixedThreadPool(NTHREAD);
            videos.forEach(v -> pool.submit(new UpdateUrlTask(v)));

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


    private static void loadVideoInfos() {

        try {
            URL feedUrl = new URL(OOYALA_SYNDICATION_URL);
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new InputStreamReader(feedUrl.openStream()));
            List<SyndEntry> syndEntries = feed.getEntries();
            for (SyndEntry entry : syndEntries) {

                try {
                    OoyalaVideo video = new OoyalaVideo();
                    video.setTitle(entry.getTitle());
                    video.setGuid(entry.getUri());

                    List<Element> foreignMarkups = entry.getForeignMarkup();
                    for (Element element : foreignMarkups) {
                        if ("description".equalsIgnoreCase(element.getName()) && element.getContent().size() > 0) {
                            video.setDescription(element.getContent().get(0).getValue());
                            continue;
                        }

                        if ("category".equalsIgnoreCase(element.getName()) && element.getContent().size() > 0) {
                            element.getContent().forEach(c -> video.addPublication(c.getValue()));
                            continue;
                        }

                        if ("thumbnail".equalsIgnoreCase(element.getName()) && element.getAttributes().size() > 0) {
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
                    videos.add(video);
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
        videos.forEach(v -> v.setContentUrl(redirectUrl(v.getContentUrl())));
    }


    private static void listCurlCommands() {
        videos.forEach(v -> System.out.println(String.format("wget  -O \"/home/satyajit/Media/Ooyala/%s\" \"%s\"", v.getFileName(), v.getContentUrl())));
    }

    private static void downloadVideo(String fileURL, String filePath) {
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
                System.out.println(String.format("START::Downloading: %s from: %s", this.fileName, this.fileUrl));
                downloadVideo(this.fileUrl, String.format("%s%s%s", VIDEO_DIR, File.separator, this.fileName));
                LOGGER.debug(String.format("END::Downloaded: %s", this.fileName));
                System.out.println(String.format("END::Downloaded: %s", this.fileName));
            } catch (Exception e) {
                LOGGER.error("Error downloading video.", e);
                e.printStackTrace();
            }
        }
    }


}

