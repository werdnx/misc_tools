package com.ocity.egypt.logs;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FlakeSearcher {

    //2022-10-23 10:44:28.710
    //yyMMddHHmmss
    public static void main(String[] args) throws IOException, ParseException {
//        String path = "E:\\temp\\1220\\2\\ru.bpc.otter.core.log.node1.txt";
        String path = "E:\\temp\\1220\\2\\ru.bpc.otter.core.log.node2.txt";

        List<String> lines = Files.readAllLines(Path.of(new File(path).toURI()));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        long prevTime = -1L;
        int lineNumber = 0;
        int flakeCounts = 0;
        long maxDiff = 0;
        for (String line : lines) {
            if (line.startsWith(" ") || line.length() < 23) {
                continue;
            }
            String s = line.substring(0, 23);

            Date date = null;
            try {
                date = sdf.parse(s);
            } catch (ParseException e) {
                continue;
            }

            long currentTime = date.getTime();
            if (currentTime + 1000 < prevTime) {
                long diff = (prevTime - currentTime);
                System.out.println("LOOPING: TIME SHIFT !!!!!!!");
                System.out.println("\tline number " + lineNumber);
                System.out.println("\tprev time " + sdf.format(new Date(prevTime)));
                System.out.println(	"\tnext time " + sdf.format(new Date(currentTime)));
                if (diff > maxDiff) {
                    maxDiff = diff;
                    System.out.println("\tMAX DIFF = " + maxDiff + "\n");
                }
                flakeCounts++;
            }
            prevTime = currentTime;
            lineNumber++;
        }
        System.out.println("Total flake count " + flakeCounts);
        System.out.println("MAX DIFF = " + maxDiff);
    }
}
