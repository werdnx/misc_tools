package com.sab;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.DataFormatter;

public class ImportToDbFromXlsx {

    public static void main(String[] args) {
        String excelFilePath = "F:\\temp\\0604\\unsettled_transactions.xlsx";
//        String excelFilePath = "F:\\temp\\0604\\saptco_18Apr_01jun_missingTXN.xlsx";
        run(excelFilePath);
    }

    private static void run(String excelFilePath) {

        String tableName = Paths.get(excelFilePath).getFileName().toString().replace(".xlsx", "");

        try (FileInputStream fis = new FileInputStream(excelFilePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);

            if (headerRow == null) {
                System.err.println("The first sheet is empty.");
                System.exit(1);
            }

            List<String> headers = new ArrayList<>();
            for (Cell cell : headerRow) {
                headers.add(cell.getStringCellValue());
            }

            String createTableSQL = generateCreateTableSQL(tableName, headers);
            String insertSQL = generateInsertSQL(tableName, headers.size());

            try (Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/ocity", "ocity", "ocity");
                 Statement stmt = conn.createStatement()) {

                // Check if table exists and create if not
                if (!tableExists(conn, tableName)) {
                    stmt.execute(createTableSQL);
                }

                // Insert data
                try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
                    for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                        Row row = sheet.getRow(i);
                        if (row != null) {
                            for (int j = 0; j < headers.size(); j++) {
                                Cell cell = row.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                                pstmt.setObject(j + 1, getCellValue(cell));
                            }
                            pstmt.addBatch();
                        }
                    }
                    pstmt.executeBatch();
                }
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean tableExists(Connection conn, String tableName) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT c.relname FROM pg_class c WHERE  c.relname ILIKE '%"+tableName +"%'")) {
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        }
    }

    private static String generateCreateTableSQL(String tableName, List<String> headers) {
        StringBuilder sb = new StringBuilder("CREATE TABLE ").append(tableName).append(" (");
        for (String header : headers) {
            sb.append(header).append(" TEXT, ");
        }
        sb.setLength(sb.length() - 2); // Remove the last comma and space
        sb.append(")");
        return sb.toString();
    }

    private static String generateInsertSQL(String tableName, int columnCount) {
        StringBuilder sb = new StringBuilder("INSERT INTO ").append(tableName).append(" VALUES (");
        for (int i = 0; i < columnCount; i++) {
            sb.append("?, ");
        }
        sb.setLength(sb.length() - 2); // Remove the last comma and space
        sb.append(")");
        return sb.toString();
    }

    private static Object getCellValue(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return df.formatCellValue(cell);
                } else {
                    return df.formatCellValue(cell);
                }
            case BOOLEAN, FORMULA:
                return df.formatCellValue(cell);
            case BLANK:
                return null;
            default:
                return null;
        }
    }
    private static DataFormatter df = new DataFormatter();
}
