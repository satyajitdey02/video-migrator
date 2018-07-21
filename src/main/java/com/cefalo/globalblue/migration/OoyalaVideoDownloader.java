package com.cefalo.globalblue.migration;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import org.apache.commons.lang.StringUtils;
import org.jdom2.Element;

import java.io.*;
import java.net.HttpURLConnection;
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

    private static final String OOYALA_SYNDICATION_URL = "http://cdn-api.ooyala.com/syndication/mp4?id=ad6928a3-07ac-48ff-862b-269641a4c1e3";
    private static final int BUFFER_SIZE = 4096;
    private static String VIDEO_DIR = "/home/satyajit/Media/Ooyala";

    public static void main(String[] args) {
        //download();
        //updateContentUrl();
        listCurlCommands();
        //getRedirectUrl("http://api.ooyala.com/syndication/stream_redirect?pcode=Jxb28663ef9GxvZq830juSPFtD48&expires=1532151123&streamID=10832331&signature=XmmnSJ0DyLaoG8QzLCLBs%2FG7Xmz4IOpvRQUuHqvH0g0&size=3562553&length=25000");

    }

    private static void download() {
        try {
            ExecutorService pool = Executors.newFixedThreadPool(10);
            for (OoyalaVideo video : getVideoInfos()) {
                pool.submit(new DownloadTask(video.getContentUrl(), video.getFileName()));
            }
            pool.shutdown();

            pool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static String getRedirectUrl(String originalUrl) {
        try {
            URL obj = new URL(originalUrl);

            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

            conn.setReadTimeout(5000);
            conn.setInstanceFollowRedirects(false);
            conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
            conn.addRequestProperty("User-Agent", "Mozilla");

            boolean redirect = false;

            // normally, 3xx is redirect
            int status = conn.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                if (status == HttpURLConnection.HTTP_MOVED_TEMP
                        || status == HttpURLConnection.HTTP_MOVED_PERM
                        || status == HttpURLConnection.HTTP_SEE_OTHER)
                    redirect = true;
            }

           // System.out.println("Response Code ... " + status);

            if (redirect) {

                // get redirect url from "location" header field
                String newUrl = conn.getHeaderField("Location");

                return newUrl;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return originalUrl;
    }

    private static List<OoyalaVideo> getVideoInfos() {
        List<OoyalaVideo> videos = new ArrayList<>();
        try {
            URL feedUrl = new URL(OOYALA_SYNDICATION_URL);
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new InputStreamReader(feedUrl.openStream()));
            List<SyndEntry> syndEntries = feed.getEntries();
            for (SyndEntry entry : syndEntries) {
                /*System.out.println(entry.getTitle() + "::"+entry.getUri());
                System.out.println("---------------------------------------");*/
                try {
                    OoyalaVideo video = new OoyalaVideo();
                    video.setTitle(entry.getTitle());
                    //video.setDescription(entry.getDescription().getValue());
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
                                        //System.out.println(url);
                                    }

                                }
                            }
                        }
                    }
                    videos.add(video);
                } catch (Exception e) {
                    System.out.println("Entry: " + entry);
                }


            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return videos;
    }

    private static void updateContentUrl() {
        getVideoInfos().forEach(v -> {
            v.setContentUrl(getRedirectUrl(v.getContentUrl()));
        });
    }



    private static void listCurlCommands() {
        getVideoInfos().forEach(v -> {
            System.out.println(String.format("wget  -O \"/home/satyajit/Media/Ooyala/%s\" \"%s\"", v.getFileName(), v.getContentUrl()));
        });
    }

    public static void downloadFile(String fileURL, String fileName) throws IOException {
        URL url = new URL(fileURL);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        int responseCode = httpConn.getResponseCode();
        // always check HTTP response code first
        if (responseCode == HttpURLConnection.HTTP_OK) {

            String disposition = httpConn.getHeaderField("Content-Disposition");
            String contentType = httpConn.getContentType();
            int contentLength = httpConn.getContentLength();


            System.out.println("Content-Type = " + contentType);
            System.out.println("Content-Disposition = " + disposition);
            System.out.println("Content-Length = " + contentLength);
            System.out.println("fileName = " + fileName);

            // opens input stream from the HTTP connection
            InputStream inputStream = httpConn.getInputStream();
            String saveFilePath = VIDEO_DIR + File.separator + fileName;

            // opens an output stream to save into file
            FileOutputStream outputStream = new FileOutputStream(saveFilePath);

            int bytesRead = -1;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();

            System.out.println("File downloaded");
        } else {
            System.out.println("No file to download. Server replied HTTP code: " + responseCode);
        }

        httpConn.disconnect();
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
                downloadFile(this.fileUrl, this.fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}

