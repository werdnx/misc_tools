package com.saptco;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SaptcoRoutesV1 {
    public static void main(String[] args) throws IOException, URISyntaxException {
        String routesPath = "/Users/werdn/Downloads/Saptco_Dammam_A.xlsx";
        String csvFolder = "/Users/werdn/Downloads/Dammam/Dammam/Route A 1_1_up.csv";
        String outFolder = "/Users/werdn/Downloads/Dammam/out";
        String direction = "Up";

        Map<Key, Item> stopsMap = buildStopsMap(routesPath, direction);
        File fld = new File(csvFolder);
//        for (File file : fld.listFiles()) {
//            processFile(file, stopsMap, outFolder);
//        }
        processFile(fld, stopsMap, outFolder);
    }

    private static void processFile(File file, Map<Key, Item> stopsMap, String outFolder) throws IOException, URISyntaxException {
        System.out.println("Process file " + file.getName());
        List<String> lines = Files.readAllLines(Paths.get(file.toURI()));
        List<String> outLines = new ArrayList<>();
        boolean first = true;
        Key prevKey = null;
        Key currKey = null;
        for (String line : lines) {
            String resLine = null;
            if (first) {
                resLine = line + ",\"direct\",\"stationName\",\"busStopId\",\"busStopName\"";
                first = false;
            } else {
                String[] split = line.split(",");
                String lon = split[1].replace("\"", "");
                String lat = split[2].replace("\"", "");
                Key k = new Key(lon, lat);

                currKey = k;
                Item item = stopsMap.get(k);
                if (item != null) {
                    resLine = line + ",\"" + item.getDirect() + "\",\"" + item.getStationName() + "\",\"" + item.getBusStopId() + "\",\"" + item.getBusStopName() + "\"";
                } else if (prevKey != null && currKey != null) {
                    Map.Entry<Key, Item> entry = checkStopBetween(stopsMap, prevKey, currKey);
                    if (entry != null) {
                        resLine = "," + "\"" + entry.getKey().getLon() + "\"," + "\"" + entry.getKey().getLat() + "\","
                                + "\"" + entry.getValue().getDirect() + "\",\"" + entry.getValue().getStationName() + "\",\"" + entry.getValue().getBusStopId()
                                + "\",\"" + entry.getValue().getBusStopName() + "\"";
                    } else {
                        resLine = line + ",,,,";
                    }
                } else {
                    resLine = line + ",,,,";
                }
                prevKey = k;
            }
            outLines.add(resLine);
        }
        Files.write(Paths.get(outFolder + File.separator + file.getName().split("\\.")[0].replace(" ", "") + "_res.csv"),
                outLines, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
    }

    private static Map.Entry<Key, Item> checkStopBetween(Map<Key, Item> stopsMap, Key prevKey, Key currKey) {
        for (Map.Entry<Key, Item> keyItemEntry : stopsMap.entrySet()) {
            if (stationBetween(keyItemEntry, prevKey, currKey)) {
                return keyItemEntry;
            }
        }
        return null;
    }

    private static boolean stationBetween(Map.Entry<Key, Item> keyItemEntry, Key x1, Key x2) {
        double xalpha = (Double.parseDouble(keyItemEntry.getKey().getLon()) - Double.parseDouble(x1.getLon())) / (Double.parseDouble(x2.getLon()) - Double.parseDouble(x1.getLon()));
        double yalpha = (Double.parseDouble(keyItemEntry.getKey().getLat()) - Double.parseDouble(x1.getLat())) / (Double.parseDouble(x2.getLat()) - Double.parseDouble(x1.getLat()));
        if (xalpha > 0.0 && xalpha < 1.0 && yalpha > 0.0 && yalpha < 1.0) {
            return true;
        } else {
            return false;
        }
    }

    private static Map<Key, Item> buildStopsMap(String routesPath, String direction) throws IOException {
        FileInputStream fis = new FileInputStream(routesPath);
        Workbook workbook = new XSSFWorkbook(fis);
        Sheet sheet = workbook.getSheetAt(0);

        Map<Key, Item> res = new HashMap<>();
        for (Row row : sheet) {
            Cell dir = row.getCell(0);
            Cell lonCell = row.getCell(5);
            Cell latCell = row.getCell(7);
            if (lonCell != null && latCell != null) {
//                lonCell.setCellType(CellType.STRING);
//                latCell.setCellType(CellType.STRING);
//                String lonV = cellValue(lonCell);
//                String latV = cellValue(latCell);
                String lonV = lonCell.toString();
                String latV = latCell.toString();
                if (lonV != null && latV != null && lonV.contains(".") && latV.contains(".") && dir.toString().equals(direction)) {
                    lonV = lonV.replace(",", ".").trim();
                    latV = latV.replace(",", ".").trim();
                    Key k = new Key(lonV, latV);
                    Item i = Item.builder()
                            .direct(cellValue(row.getCell(0)))
                            .stationName(cellValue(row.getCell(1)))
                            .busStopId(cellValue(row.getCell(2)))
                            .busStopName(cellValue(row.getCell(3)))
                            .build();

                    Item item = res.get(k);
                    if (item != null) {
                        i.setDirect(item.getDirect() + " | " + i.getDirect());
                        i.setBusStopName(item.getBusStopName() + " | " + i.getBusStopName());
                        i.setStationName(item.getStationName() + " | " + i.getStationName());
                    }
                    res.put(k, i);
                }
            }

        }
        return res;

    }

    private static String cellValue(Cell cell) {
        switch (cell.getCellType()) {
            case _NONE:
            case BLANK:
            case BOOLEAN:
            case ERROR:
            case FORMULA:
                return null;
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
            default:
                throw new IllegalStateException("Axtung");
        }
    }

    @Data
    @AllArgsConstructor
    private static class Key {
        String lon;
        String lat;

    }

    @Data
    @Builder
    private static class Item {
        String direct;
        String stationName;
        String busStopId;
        String busStopName;
    }
}
