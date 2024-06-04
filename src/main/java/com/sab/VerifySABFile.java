package com.sab;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.poi.ss.usermodel.*;

public class VerifySABFile {
    public static void main(String[] args) {
        String excelFilePath = "F:\\temp\\0604\\saptco_18Apr_01jun_missingTXN.xlsx";
        run(excelFilePath);
    }

    private static void run(String excelFilePath) {
        Path originalPath = Paths.get(excelFilePath);
        String newFilePath = originalPath.toString().replace(".xlsx", "_verified.xlsx");

        try (FileInputStream fis = new FileInputStream(excelFilePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);

            if (headerRow == null) {
                System.err.println("The first sheet is empty.");
                System.exit(1);
            }

            // Add new column header
            int newColumnIndex = headerRow.getLastCellNum();
            Cell newHeaderCell = headerRow.createCell(newColumnIndex);
            newHeaderCell.setCellValue("Settlement_status");

            // Database connection
            try (Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/ocity", "ocity", "ocity")) {
                String query = "SELECT 1 FROM unsettled_transactions WHERE rrn = ?";

                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row != null) {
                        Cell rrnCell = row.getCell(findColumnIndex(headerRow, "rrn"));
                        if (rrnCell != null) {
                            String rrnValue = rrnCell.getStringCellValue();
                            boolean exists = checkRrnInDatabase(conn, query, rrnValue);

                            Cell newCell = row.createCell(newColumnIndex);
                            newCell.setCellValue(exists ? "OK" : "");
                        }
                    }
                }
            }

            // Write the updated workbook to a new file
            try (FileOutputStream fos = new FileOutputStream(newFilePath)) {
                workbook.write(fos);
            }

            System.out.println("New file created: " + newFilePath);

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private static int findColumnIndex(Row headerRow, String columnName) {
        for (Cell cell : headerRow) {
            if (cell.getStringCellValue().equalsIgnoreCase(columnName)) {
                return cell.getColumnIndex();
            }
        }
        throw new IllegalArgumentException("Column" + columnName + " not found in the header row.");
    }

    private static boolean checkRrnInDatabase(Connection conn, String query, String rrnValue) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, rrnValue);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }
}
