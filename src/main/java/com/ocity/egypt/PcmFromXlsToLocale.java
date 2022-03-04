package com.ocity.egypt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class PcmFromXlsToLocale {
    public static void main(String[] args) throws IOException {
//        convert("/Users/dmitrenkoandrey/IdeaProjects/misc_tools/src/main/resources/app/to_translate_app_ar.xlsx",
//                "/Users/dmitrenkoandrey/IdeaProjects/misc_tools/src/main/resources/app/strings-ar-rEG_new.xml");
//
//        convert("/Users/dmitrenkoandrey/IdeaProjects/misc_tools/src/main/resources/data/to_translate_data1Page.xlsx",
//                "/Users/dmitrenkoandrey/IdeaProjects/misc_tools/src/main/resources/data/strings-ar-rEG_new.xml");

        convert("/Users/dmitrenkoandrey/IdeaProjects/misc_tools/src/main/resources/device/to_translate_device1Page.xlsx",
                "/Users/dmitrenkoandrey/IdeaProjects/misc_tools/src/main/resources/device/strings-ar-rEG_new.xml");
    }

    public static void convert(String xlsxPath, String outPath) throws IOException {
        FileInputStream file = new FileInputStream(new File(xlsxPath));
        Workbook workbook = new XSSFWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0);

        int i = 1;
        Resources res = new Resources();
        for (Row row : sheet) {
            Cell cell0 = row.getCell(0);
            Cell cell2 = row.getCell(2);
            res.getString().add(new ResourceItem(value(cell0), value(cell2)));
        }
        ObjectMapper xmlMapper = new XmlMapper();
        String resultString = xmlMapper.writeValueAsString(res);
        Files.writeString(new File(outPath).toPath(), resultString, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);

    }

    public static String value(Cell cell) {
        switch (cell.getCellType()) {
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            default:
                return cell.getStringCellValue();
        }
    }
}
