package com.ocity.egypt.logs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

public class AuthorizationCounter {
    private final static String[] MERCHANT_FILTERS = {"kz_nur-sultan_tc_lrt"};
    private final static String[] TERMINAL_FILTERS = {};

    public static void main(String[] args) throws IOException {

        String[] folders = {"D:\\temp\\0609\\mts_prod_logs_may\\prod_logs_node1",
                "D:\\temp\\0609\\mts_prod_logs_may\\prod_logs_node2"};
        String resName = "D:\\temp\\0609\\mts_prod_logs_may\\result_for_merchant.csv";
        Map<String, Item> data = new ConcurrentHashMap<>();
        for (String folder : folders) {
            File dir = new File(folder);
            Stream.of(dir.listFiles())
                    .filter(f -> f.getName().contains(".gz"))
                    .parallel()
                    .forEach(f -> processFile(data, f));
        }
        Files.write(Paths.get(resName),
                data.entrySet().stream().sorted(Map.Entry.comparingByKey())
                        .map(e -> e.getKey() + "," + e.getValue().getCount()).collect(Collectors.toList()),
                StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);

    }

    // Accepted 1 Messages: merchant: kz_nur-sultan_tc_lrt
    @SneakyThrows
    private static void processFile(Map<String, Item> data, File gz) {
        System.out.println("Start process file " + gz);
        String expr = "Accepted (.*) Messages: merchant: (.*), terminalId: (.*), correlationId";
        Pattern p = Pattern.compile(expr);
        try (BufferedReader buffered = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(gz)), "UTF-8"))) {
            String line = null;
            while ((line = buffered.readLine()) != null) {
                Matcher m = p.matcher(line);
                if (m.find()) {
                    Integer count = Integer.parseInt(m.group(1));
                    String merchant = m.group(2);
                    String terminal = m.group(3);

                    if (applyFilter(merchant, MERCHANT_FILTERS) && applyFilter(terminal, TERMINAL_FILTERS)) {
                        String key = gz.getName().replace("ru.bpc.otter.core.log.", "").replace(".gz", "");
                        Item v = data.get(key);
                        if (v != null) {
                            data.put(key, new Item(v.getCount() + count, merchant, terminal));
                        } else {
                            data.put(key, new Item(count, merchant, terminal));
                        }
                    }
                }
            }
        }
    }

    private static boolean applyFilter(String value, String[] filters) {
        if (filters.length == 0) {
            return true;
        } else {
            for (String filter : filters) {
                if (value.equals(filter)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Data
    @AllArgsConstructor
    static class Item {
        Integer count;
        String merchant;
        String terminal;
    }
}
