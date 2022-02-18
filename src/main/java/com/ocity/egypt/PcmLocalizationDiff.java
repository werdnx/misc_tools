package com.ocity.egypt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class PcmLocalizationDiff {
    public static void main(String[] args) throws IOException {
        findDiff("C:\\develop\\misc_tools\\src\\main\\resources\\app\\strings.xml",
                "C:\\develop\\misc_tools\\src\\main\\resources\\app\\strings-ar-rEG.xml",
                "C:\\develop\\misc_tools\\src\\main\\resources\\app\\to_translate_app.xlsx");

        findDiff("C:\\develop\\misc_tools\\src\\main\\resources\\data\\strings.xml",
                "C:\\develop\\misc_tools\\src\\main\\resources\\data\\strings-ar-rEG.xml",
                "C:\\develop\\misc_tools\\src\\main\\resources\\data\\to_translate_data.xlsx");

        findDiff("C:\\develop\\misc_tools\\src\\main\\resources\\device\\strings.xml",
                "C:\\develop\\misc_tools\\src\\main\\resources\\device\\strings-ar-rEG.xml",
                "C:\\develop\\misc_tools\\src\\main\\resources\\device\\to_translate_device.xlsx");
    }

    public static void findDiff(String fileEn, String fileAr, String outFile) throws IOException {
        ObjectMapper xmlMapper = new XmlMapper();
        Resources resEn = xmlMapper.readValue(new File(fileEn), Resources.class);
        Resources resAr = xmlMapper.readValue(new File(fileAr), Resources.class);

        Map<String, List<ResourceItem>> enMap = resEn.getString().stream().collect(Collectors.groupingBy(ResourceItem::getName));
        Map<String, List<ResourceItem>> arMap = resAr.getString().stream().collect(Collectors.groupingBy(ResourceItem::getName));

        final List<ResourceItem> result = new ArrayList<>();
        enMap.entrySet().forEach(entry -> {
            String key = entry.getKey();
            if (!arMap.containsKey(key)) {
                result.add(entry.getValue().get(0));
            }
        });
        Resources resResource = new Resources(result);
        createXls(outFile, resResource);
//        String resultString = xmlMapper.writeValueAsString(resResource);
//        Files.writeString(new File(outFile).toPath(), resultString, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
    }

    public static void createXls(String name, Resources resources) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Translate to Arabic in column C");
//        sheet.setColumnWidth(0, 50);
//        sheet.setColumnWidth(1, 50);
//        sheet.setColumnWidth(2, 50);

        Row header = sheet.createRow(0);

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        XSSFFont font = ((XSSFWorkbook) workbook).createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 16);
        font.setBold(true);
        headerStyle.setFont(font);

        Cell headerCell = header.createCell(0);
        headerCell.setCellValue("Name");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(1);
        headerCell.setCellValue("English");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(2);
        headerCell.setCellValue("Arabic");
        headerCell.setCellStyle(headerStyle);

        CellStyle style = workbook.createCellStyle();
        style.setWrapText(true);

        AtomicInteger idx = new AtomicInteger(1);
        resources.getString().forEach(item -> {
            Row row = sheet.createRow(idx.getAndIncrement());
            Cell cell = row.createCell(0);
            cell.setCellValue(item.getName());
            cell.setCellStyle(style);

            cell = row.createCell(1);
            cell.setCellValue(item.getValue());
            cell.setCellStyle(style);

            cell = row.createCell(2);
            cell.setCellValue("");
            cell.setCellStyle(style);
        });

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);

        File f = new File(name);
        if (f.exists()) {
            f.delete();
        }
        FileOutputStream outputStream = new FileOutputStream(name);
        workbook.write(outputStream);
        workbook.close();
    }

}
