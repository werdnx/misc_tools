package com.ocity.egypt.nginx;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NginxFindSpammer {
    private static final Pattern ACL_ENTRIES_PATTERN = Pattern.compile("acl_entries.*terminalId=([^&]+)");
    private static final Pattern TIMESTAMP_PATTERN = Pattern.compile("^\\[(\\d+/[a-zA-Z]+/\\d+:\\d+:\\d+:\\d+ [+-]\\d{4})\\]");
    private static final DateTimeFormatter NGINX_TIME_FORMAT = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z");

    public static void main(String[] args) {
        String logFilePath = "D:\\temp\\0821\\app.access.log-20230821\\app.access.log-20230821";
        String timeFrom =  "21/Aug/2023:00:00:01 +0300";
        ConcurrentHashMap<String, Integer> terminalIdCounts = new ConcurrentHashMap<>();
        LocalDateTime thresholdTime = LocalDateTime.parse(timeFrom, NGINX_TIME_FORMAT);  // Assuming args[0] is provided in the NGINX timestamp format

        try (BufferedReader br = new BufferedReader(new FileReader(logFilePath))) {
            br.lines().parallel().forEach(line -> {
                Matcher timeMatcher = TIMESTAMP_PATTERN.matcher(line);
                if (timeMatcher.find()) {
                    LocalDateTime logTime = LocalDateTime.parse(timeMatcher.group(1), NGINX_TIME_FORMAT);
                    if (logTime.isAfter(thresholdTime) && line.contains("acl_entries")) {
                        Matcher matcher = ACL_ENTRIES_PATTERN.matcher(line);
                        if (matcher.find()) {
                            String terminalId = matcher.group(1);
                            terminalIdCounts.merge(terminalId, 1, Integer::sum);
                        }
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(terminalIdCounts.entrySet());
        sortedEntries.sort(Map.Entry.<String, Integer>comparingByValue().reversed());

        for (Map.Entry<String, Integer> entry : sortedEntries) {
            System.out.println("Count: " + entry.getValue() + ", TerminalId: " + entry.getKey());
        }
    }
}
