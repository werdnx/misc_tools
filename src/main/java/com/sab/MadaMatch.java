package com.sab;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MadaMatch {
    public static void main(String[] args) {
        String firstFilePath = "F:\\temp\\0503\\ocity_mada2.csv";
        String secondFilePath = "F:\\temp\\0503\\SAPTCO_backlog_overage.csv";

        String outputFirstFile = "F:\\temp\\0503\\unmatched_in_first.csv";
        String outputSecondFile = "F:\\temp\\0503\\unmatched_in_second.csv";

        Map<String, String> firstFileRecords = new HashMap<>();
        Map<String, String> secondFileRecords = new HashMap<>();
        Map<String, Integer> firstFileDateCounts = new HashMap<>();
        Map<String, Integer> secondFileDateCounts = new HashMap<>();

        // Read the first CSV file
        try (BufferedReader br = new BufferedReader(new FileReader(firstFilePath))) {
            String line;
            br.readLine(); // skip the header
            while ((line = br.readLine()) != null) {
                String[] values = line.split(";");
                String rrn = values[3].trim();
                firstFileRecords.put(rrn, line);
                String date = values[5].trim(); // assuming 'event_date' is the 6th column
                firstFileDateCounts.put(date, firstFileDateCounts.getOrDefault(date, 0) + 1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Read the second CSV file
        try (BufferedReader br = new BufferedReader(new FileReader(secondFilePath))) {
            String line;
            br.readLine(); // skip the header
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                String rrn = values[6].trim();
                secondFileRecords.put(rrn, line);
                String date = values[8].trim(); // assuming 'LOCALDATE' is the 9th column
                secondFileDateCounts.put(date, secondFileDateCounts.getOrDefault(date, 0) + 1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Determine unmatched records
        Set<String> unmatchedFirst = new HashSet<>(firstFileRecords.keySet());
        unmatchedFirst.removeAll(secondFileRecords.keySet());

        Set<String> unmatchedSecond = new HashSet<>(secondFileRecords.keySet());
        unmatchedSecond.removeAll(firstFileRecords.keySet());

        // Write unmatched records to their respective new files
        writeUnmatchedRecords(outputFirstFile, unmatchedFirst, firstFileRecords, firstFileDateCounts);
        writeUnmatchedRecords(outputSecondFile, unmatchedSecond, secondFileRecords, secondFileDateCounts);

        System.out.println("Unmatched records and their counts per date written to files.");
    }

    private static void writeUnmatchedRecords(String filePath, Set<String> unmatched, Map<String, String> records, Map<String, Integer> dateCounts) {
        try (PrintWriter out = new PrintWriter(filePath)) {
            // First write header for the date counts
            out.println("Date,Count");
            dateCounts.forEach((date, count) -> out.println(date + "," + count));
            out.println(); // add a blank line before records
            out.println("Records:");
            // Now write the unmatched records
            for (String key : unmatched) {
                out.println(records.get(key));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
