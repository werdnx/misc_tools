package com.saptco;

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
import java.util.List;
import java.util.Map;

public class SaptcoRoutesV2 {
    public static void main(String[] args) throws IOException, URISyntaxException {
        Conf medina = new MedinaConf();
//        Conf dammam = new DammamConf();

        run(medina);
    }

    private static void run(Conf conf) throws IOException, URISyntaxException {
        for (String[] one : conf.filesConf()) {
            List<Item> stopsListUp = buildStopsList(one[0], "UP", conf.xlsMap());
            if(stopsListUp.size() == 0){
                throw new IllegalStateException("stopsListUp  is empty");
            }
            File upCsv = new File(one[1]);
            List<String> up = processFile(upCsv, stopsListUp, conf.skipFirst());

            List<Item> stopsListDown = buildStopsList(one[0], "DOWN", conf.xlsMap());
            if(stopsListDown.size() == 0){
                throw new IllegalStateException("stopsListDown  is empty");
            }
            File downCsv = new File(one[2]);
            List<String> down = processFile(downCsv, stopsListDown, conf.skipFirst());

            if (!stopsListDown.isEmpty()) {
                checkIntercetption(up, down);
                up.addAll(down);
            }
            up.add(0, "\"point_num\",\"longitude\",\"latitude\",\"direct\",\"stationName\",\"busStopId\",\"busStopName\"");
            Files.write(Paths.get(one[3]), up, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        }
    }

    private static void checkIntercetption(List<String> up, List<String> down) {
        String lastUp = up.get(up.size() - 1);
        String firstDown = down.get(0);

        String[] split = lastUp.split(",");
        String lonUp = split[1].replace("\"", "");
        String latUp = split[2].replace("\"", "");

        String[] split2 = firstDown.split(",");
        String lonDown = split2[1].replace("\"", "");
        String latDown = split2[2].replace("\"", "");

        if (lonUp.equalsIgnoreCase(lonDown) && latUp.equalsIgnoreCase(latDown)) {
            down.remove(0);
        }
    }

    private static List<String> processFile(File file, List<Item> stopsList, boolean skipFirst) throws IOException, URISyntaxException {
        System.out.println("Process file " + file.getName());
        List<Item> points = points(Files.readAllLines(Paths.get(file.toURI())), skipFirst);

        for (Item stop : stopsList) {
            appendStop(points, stop);
        }

        return convertToOuts(points);
    }

    private static List<String> convertToOuts(List<Item> points) {
        List<String> res = new ArrayList<>();
        for (Item point : points) {
            res.add("\"" + point.getPos() + "\",\""
                    + point.getLon() + "\",\""
                    + point.getLat() + "\",\""
                    + point.getDirect() + "\",\""
                    + point.getStationName() + "\",\""
                    + point.getBusStopId() + "\",\""
                    + point.getBusStopName() + "\"");
        }

        return res;
    }

    private static List<Item> points(List<String> readAllLines, boolean skipFirst) {
        List<Item> res = new ArrayList<>();
        boolean first = true;
        for (String line : readAllLines) {
            if (first && skipFirst) {
                first = false;
                continue;
            }
            String[] split = line.split(",");
            String lon = split[1].replace("\"", "");
            String lat = split[2].replace("\"", "");
            res.add(Item.builder().busStopId("").busStopName("").direct("").stationName("").lon(lon).lat(lat).pos(split[0].replace("\"", "")).build());
        }
        return res;
    }

    private static void appendStop(List<Item> points, Item stop) {
        int i = pointBefore(points, stop);
        // pointBefore -> stop -> pointAfter
        Item pointBefore = points.get(i);
        if (pointBefore.getLat().equals(stop.getLat()) && pointBefore.getLon().equals(stop.getLon())) {
            pointBefore.setBusStopId(stop.busStopId);
            pointBefore.setDirect(stop.direct);
            pointBefore.setBusStopName(stop.getBusStopName());
            pointBefore.setStationName(stop.stationName);
        } else {
            points.add(i + 1, stop);
        }
    }

    private static int pointBefore(List<Item> points, Item stop) {
        int i = nearestPoint(points, stop);
        if (i == 0 || i == (points.size() - 1)) {
            return i;
        } else {
            Item prev = points.get(i - 1);
            Item next = points.get(i + 1);
            if (distance(prev, stop) < distance(next, stop)) {
                return i;
            } else {
                return i + 1;
            }
        }
    }

    private static double distance(Item p1, Item p2) {
        Double x1 = Double.parseDouble(p1.lat);
        Double y1 = Double.parseDouble(p1.lon);

        Double x2 = Double.parseDouble(p2.lat);
        Double y2 = Double.parseDouble(p2.lon);
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    private static int nearestPoint(List<Item> points, Item stop) {
        int i = 0;
        int nearestIdx = 0;
        Item nearest = null;
        for (Item point : points) {
            if (nearest == null) {
                nearest = point;
                nearestIdx = i;
            } else {
                if (distance(point, stop) < distance(nearest, stop)) {
                    nearest = point;
                    nearestIdx = i;
                }
            }
            i++;
        }
        return nearestIdx;
    }

    private static List<Item> buildStopsList(String routesPath, String direction, Map<String, Integer> xlsMap) throws IOException {
        FileInputStream fis = new FileInputStream(routesPath);
        Workbook workbook = new XSSFWorkbook(fis);
        Sheet sheet = workbook.getSheetAt(0);

        List<Item> res = new ArrayList<>();
        for (Row row : sheet) {
            Cell dir = row.getCell(xlsMap.get("dir"));
            Cell lonCell = row.getCell(xlsMap.get("lon"));
            Cell latCell = row.getCell(xlsMap.get("lat"));
            if (lonCell != null && latCell != null) {
                String lonV = lonCell.toString();
                String latV = latCell.toString();
                if (lonV != null && latV != null && lonV.contains(".") && latV.contains(".") && dir.toString().trim().equalsIgnoreCase(direction)) {
                    lonV = lonV.replace(",", ".").trim();
                    latV = latV.replace(",", ".").trim();
                    Item i = Item.builder()
                            .lon(lonV)
                            .lat(latV)
                            .direct(cellValue(row.getCell(xlsMap.get("dir"))))
                            .stationName(cellValue(row.getCell(xlsMap.get("stationName"))))
                            .busStopId(cellValue(row.getCell(xlsMap.get("busStopId"))))
                            .busStopName(cellValue(row.getCell(xlsMap.get("busStopName"))))
                            .pos("")
                            .build();

                    res.add(i);
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
                return cell.toString().split("\\.")[0];
            default:
                throw new IllegalStateException("Axtung");
        }
    }

    @Data
    @Builder
    private static class Item {
        String pos;
        String lon;
        String lat;
        String direct;
        String stationName;
        String busStopId;
        String busStopName;
    }
}
