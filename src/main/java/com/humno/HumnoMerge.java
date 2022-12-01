package com.humno;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class HumnoMerge {
        /*
    select pan, TRUNC(payment_date, 'dd') as day, sum(amount) as amt
from t_afc_event
where payment_date between TO_TIMESTAMP('2022/10/05', 'YYYY/MM/DD') and TO_TIMESTAMP('2022/10/12', 'YYYY/MM/DD')
and pan like '9860%'
and action = 'AUTHORIZATION'
group by pan, TRUNC(payment_date, 'dd')
order by TRUNC(payment_date, 'dd')
     */

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        String attoPath = "C:\\develop\\misc_tools\\src\\main\\resources\\humno\\Atto_inpput_12_17.xlsx";
//        String humnoPath = "/Users/werdn/Downloads/Humo_v22.xlsx";
        String humnoPath = "C:\\develop\\misc_tools\\src\\main\\resources\\humno\\Humno_input_part2.xlsx";
        String outPath = "C:\\develop\\misc_tools\\out\\Humno_lost_out_part2.xlsx";
        ExecutorService executor = Executors.newFixedThreadPool(4);

        Future<List<Item>> sAtto = executor.submit(() -> buildItems(attoPath)
                .stream()
                .map(i -> new Item(i.getPan(), attoDate(i.getDate()), i.getAmount() / 100, null))
                .collect(Collectors.toList()));
        Future<List<Item>> sHu = executor
                .submit(() -> buildItems(humnoPath)
                        .stream()
                        .map(i -> new Item(i.getPan(), humnoDate(i.getDate()), i.getAmount(), null))
                        .collect(Collectors.toList()));

        List<Item> attoItems = sAtto.get();
        List<Item> humnoItems = sHu.get();
        executor.shutdownNow();
        Map<String, List<Item>> attoMap = attoItems.stream().collect(Collectors.groupingBy(i -> i.getPan() + i.getDate()));

        for (Item humnoItem : humnoItems) {
            List<Item> ai = attoMap.get(humnoItem.getPan() + humnoItem.getDate());
            if (ai == null) {
                continue;
            }
            if (ai.size() > 1) {
                throw new IllegalStateException("ASAP MEGA URGENT CRITICAL!!!");
            }
            humnoItem.setDiff(humnoItem.getAmount() - ai.get(0).getAmount());
        }
        write(humnoItems, outPath);
    }

    private static String humnoDate(String date) {
        String[] split = date.split("\\.");
        return split[0] + "." + split[1];
    }

    private static String attoDate(String date) {
        String[] split = date.split("-");
        return split[2].split(" ")[0] + "." + split[1];
    }

    private static void write(List<Item> humnoItems, String outPath) throws IOException {
        Workbook workbook = new XSSFWorkbook();

        Sheet sheet = workbook.createSheet("Humno lost transactions");
        int rowNum = 0;
        for (Item humnoItem : humnoItems) {
            Row row = sheet.createRow(rowNum++);
            Cell cell = row.createCell(0);
            cell.setCellValue(humnoItem.getPan());

            cell = row.createCell(1);
            cell.setCellValue(humnoItem.getDate());

            cell = row.createCell(2);
            cell.setCellValue(humnoItem.getAmount());

            cell = row.createCell(3);
            cell.setCellValue(humnoItem.getDiff() == null ? 0 : humnoItem.getDiff());
        }
        FileOutputStream outputStream = new FileOutputStream(outPath);
        workbook.write(outputStream);
        workbook.close();
    }

    private static List<Item> buildItems(String path) throws IOException {
        FileInputStream file = new FileInputStream(new File(path));
        Workbook workbook = new XSSFWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0);

        List<Item> res = new ArrayList<>();
        for (Row row : sheet) {
            Item item = new Item();
            for (Cell cell : row) {
                if (cell.getStringCellValue().equals("Column1")) {
                    break;
                }
                switch (cell.getColumnIndex()) {
                    case 0:
                        item.setPan(cell.getStringCellValue());
                        break;
                    case 1:
                        item.setDate(cell.getStringCellValue());
                        break;
                    case 2:
                        item.setAmount(Integer.parseInt(cell.getStringCellValue()));
                        break;
                }
            }
            if (item.getPan() != null) {
                res.add(item);
            }
        }
        return res;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class Item {
        String pan;
        String date;
        Integer amount;
        Integer diff;
    }
}
