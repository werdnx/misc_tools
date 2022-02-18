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
        convert("C:\\develop\\misc_tools\\src\\main\\resources\\app\\to_translate_app.xlsx",
                "C:\\develop\\misc_tools\\src\\main\\resources\\app\\strings-ar-rEG_new.xml");
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
            res.getString().add(new ResourceItem(cell0.getStringCellValue(), cell2.getStringCellValue()));
        }
        ObjectMapper xmlMapper = new XmlMapper();
        String resultString = xmlMapper.writeValueAsString(res);
        Files.writeString(new File(outPath).toPath(), resultString, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);

    }
}
