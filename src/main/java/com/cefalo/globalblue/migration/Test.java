package com.cefalo.globalblue.migration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Test {
    public static void main(String[] args) {
        String test = "/home/satyajit/Media/GlobalBlue";
        System.out.println(test.replaceAll("/", "."));
        /*List<String> stringList = new ArrayList<>();
        for (int c = 0; c < 20; c++) {
            stringList.add("String: " + (c + 1));
        }

        for (int i = 0; i < 4; i++) {
            try {
                ExecutorService pool = Executors.newFixedThreadPool(2);
                //s(0,4)
                //s(5,9)
                //s(10,14)
                //stringList.subList(i, (i + 4)).forEach(s -> pool.submit(new Test.DownloadTask(i)));
                for (int j = (i * 5); j < ((i + 1) * 5); j++) {
                    String s = stringList.get(j);
                    pool.submit(new Test.DownloadTask(i + 1, s));
                }
                pool.shutdown();
                pool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
                System.out.println("-------------------------------------------------");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/
    }


    private static class DownloadTask implements Runnable {


        int job;
        String str;

        public DownloadTask(int job, String str) {
            this.job = job;
            this.str = str;
        }

        @Override
        public void run() {
            System.out.println("Job: " + job + ", Thread: " + Thread.currentThread().getName() + ", Str: " + str);
        }
    }
}
