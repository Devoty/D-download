package top.devoty.m3u8.download;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoclaDataTest {
    public static void main(String[] args) {

        LocalDateTime now = LocalDateTime.now();

        System.out.println(now.format(DateTimeFormatter.ISO_DATE_TIME));
        System.out.println(now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));


        LocalDateTime twoYearLater = now.minusDays(33);

//        int s = now.is(twoYearLater);
//        System.out.println(s);


    }
}
