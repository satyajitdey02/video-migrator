package com.cefalo.globalblue.migration;

import com.vzaar.Pages;
import com.vzaar.Video;
import com.vzaar.Vzaar;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Created by satyajit on 7/23/18.
 */
public class VzaarVideoUploader {

  private static final String CLIENT_ID = "yah1997-thou";
  private static final String AUTH_TOKEN = "D_dALLPyvJxQDf9rBMKW";
  private static final Vzaar VZAAR = Vzaar.make(CLIENT_ID, AUTH_TOKEN);

  public static void main(String[] args) {
    String ooyala = "syeHNrZjE6uumk8cW1ukZh-HNC5jnzFh,FpMGxhZjE6HNwdLe6BUe4uxWSBNL0weh,QxNnB3ZTE6igTOIytawC-VEoM48YDt9r,psbHg0ZjE6EBBpwCk1EQc-bSIkds1WI0,1,hiNDB3MjpLQca_CgSo3vzev-vfVuDpkL,hiNDB3MjpLQca_CgSo3vzev-vfVuDpkL,VsOXZ5ZDE6tY7Eflgo_RU6HwqWGnD6nt,Q5YTh1ZDE6anzhsgagMOcC2hOP8MfsgG,QweW5tZDE6w3cjS4oD3fz0ZSCCkIR7pG,tpYmttZDE6_JnWh5YTq6zJf43z3ts9qP,VsYjhsZDE6nWl_JMIvARPqhx8u94-NSX,94dTZpZDE6QORfFNPOhJZreMgi-HY9NQ,xlZnJoZDE6Iapm5TUjMzfB5NU0Bz31U4,JuNHZpODE6AIS-XYE-2hu1c3Gxgy2U-l,Jpb3cxYzE6omaIFTET70uWmscAR8a_3I,9mY2JrYzE6ogtrJnTQ-64aRZU9h-boQr,txaWNrYzE6G8Xtm4cYf0Rwtg0WkNfwg2,g5dzhpYzE6pQiia3Ss9CtDmI2IhclQdY,g5dzhpYzE6pQiia3Ss9CtDmI2IhclQdY,kyZnYxYzE6M9p2yjtM0vY4MokPhZh3Gx,E0NjJuMzrghDvQGVLG_bS5uLiFRdeGRd,9lZjhvMzpuJug61-uITSGzWGDpGNpauh,9nZ2FpNDok4JhxMNESsFlrn-yolXt-D5,wyYzlsNDrFjAfV0ENS7uixizEHSuINrV,JrZ2FpNDp_HQrnYp234babapH6lxhf4K,toZThwNDqobILoazF7LOq8C2SUQFZnzz,90MWl5NDq22K_N3rZ__gclwnw0wMVA4z,V1N243NToEjhlQ9tvTZiObjLErf2sp_i,w5bGdoNTqb0rh_Q6Z27t8JxYX3yJ4wit,MyaGxwNTphGxxUcy0XtiD5SkvlJ3O7AE,0zM290NToHj3Z6tQpNpToJ2hHjAyWlUw,Y0cWh5NTowSMbvVMz_bC8W56yAqWtjzh,F4MDgzNjqyy-k0mljc5YLiH2oorrED0h,lkOWo5NjqzBTWtUUas0h6fWUslDlFBuk,doY3lqNjp4rQyGYtkUIlV7VG55WDhbBf,hjMW5iNzox3kTPhzkmL3oXDp4uwayJxH,lqd25kNzpM2Cx4RV_VrXAznY3uIf2ePB,RqczloNzqHbWftPHUvE4F63BZVUQRtLb,5mZXBsNzpisCTEeaiASUdTwufCylwgNP,9zbmlxNzqAMaSGFGvEN61l1Dr0HTieTL,h0Y2YyODpAin2fdB7K_z9Lnvb8HpJKCz,03cXQ4ODoU420P6i506QRJTWhayXEaNV,o1dmJoODp7T-f7qaYFkEu1vGR9TMx_Zi,o1dmJoODp7T-f7qaYFkEu1vGR9TMx_Zi,VxbDNxODq6ypYtipSgUmtrcVGWlVKBus,BhdDB4ODqDTwYpZ7uRyZC69iGbPxD7Lh,M2dDB4ODpdloqDllaFdhdJZnLTllIor_,I5NXQyOTp5HYouoC9kdHKRD27AmsEMLS,Rmc3QzOTpbA7wq22HfYMJ7djZHmohE-5,AwaTY1OTp1Dm_1wPo5ivy65q9Q54zjaz,lqd25kNzpM2Cx4RV_VrXAznY3uIf2ePB,gyMHJhOTpi8uNhV7c8w5hPOKmO2lUOIU,4zcXJiOTpZXmTbEKnhyyWQm8ojBixcGf,h4b3FjOTr8b67ww5XXeim3cJMJzQ_PzP,Jxb28663ef9GxvZq830juSPFtD48&autoplay=1,BpMjZuOTojHEuLb3qv8Vy5GvjIfGa5qc,tuNmNvOTqSxCDE4H7XYfl6jVl7SHHhHt,d0b2RyOTp7sS2al3WvpY7uErlx61OuqM,41d2RyOTpjs1IHSuuhQxvZjbhE90Fg1t,RiazV4OTqiGcprwm2LBJ-4uCPapfEL6r,U1ZHEwYTpq0sOtfgDXOt5uKdq6V4Cw9r,02b2U0YTrCvJsIUvDCNdYMkB8MNR7ad9,hhajY3YTqok9EiNztgv6nvxIvqMdjiMF,syZXVnYzpXyfiiGODhzWv8b_-04Mge_S,BlaHl1ZDq2QJ_eLQg3UoVLQV7DJ_NAIr,NvY3BraDpDtIY57VkJGaTa-EgNWtj2XH,NheHhhazo4uuMGEtXejxZmcFwUD4hNrZ,g0bGdwazoWzb4_RBq7LG5n37U0tficYD,02eDRyazrcGqaXGn89U6F141KKh7axFU,RqOGUwbDpKQKksjSaYNeFk1tI09240W-,9mc3U4bDpTEc_TY-yTcMd1s_SpuN2Hbk,EwdGNhbDocWXg1P5TPODDdIxVVOKLm5v,Ixb2w2bjrPlD-cFgep2ng8DERFlEmupZ,1vMHNpbjoksMiFZl_-ckDRSMoLcOMh2Y,9saHFrbjp566w1TN_PgkVS5Sf7QaT226,9mMm9vdTqan1nDeSGpajBAVVwIhFWJX9,44aDZvdzpbtYZ08n6fYyELfc0grZR9aC,V1dTUxeDpGjEIg5u5LSnBNblxkAkdx5D,JjaGJ5eDoMPamb9rQpSt_M270W5OEkNz,NhNHlmeToBBYLEYthqDts4d027IxcBQT,E3aGpiNDE6WRecCJFL9BqK-JvhimRzn4,JuNHZpODE6AIS-XYE-2hu1c3Gxgy2U-l";
    List<String> ooyalaContentIdList = Arrays.asList(ooyala.split(","));

    List<Video> videos = Pages.list(VZAAR.videos().list().results());
    List<String> vzaarContentIdList = videos.stream().map(v -> {
      try {
        String[] parts = v.getTitle().split("---");
        return parts[1];
      } catch (Exception e) {
        System.out.println("Error: " + v.getTitle());
      }

      return v.getTitle();
    }).collect(Collectors.toList());

    ooyalaContentIdList.forEach(s -> {

      if (vzaarContentIdList.contains(s)) {
        System.out.println(s + " : hit");
      } else {
        System.out.println(s + " : miss");
      }
    });

    /*ooyalaContentIdSet.forEach(System.out::println);
    System.out.println("Total: " + ooyalaContentIdList.size());*/
    //listVideos();
  }

  private static void listVideos() {

    List<Video> videos = Pages.list(VZAAR.videos().list().results());
    videos.forEach(v -> {

      System.out.println(v.getId());
      System.out.println(v.getTitle());

    });
  }

}
