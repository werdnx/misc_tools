package com.ocity.egypt.nginx;

import lombok.Builder;
import lombok.Data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class NginxDataLoader {
    private static final int BATCH_SIZE = 1000;

    public static void main(String[] args) throws SQLException, IOException, ParseException {
        String nginxLogPath = "D:\\temp\\0531\\nginx.node2.txt";

        String url = "jdbc:postgresql://localhost:5432/ubs_metrics";
        Properties props = new Properties();
        props.setProperty("user", "postgres");
        props.setProperty("password", "postgres");

        Connection conn = DriverManager.getConnection(url, props);
        conn.setAutoCommit(false);
        List<String> lines = Files.readAllLines(Paths.get(nginxLogPath));

        int count = 0;
        int globalCount = 0;
        List<Dto> batch = new ArrayList<>();
        for (String line : lines) {
            if (count >= BATCH_SIZE) {
                saveBatch(conn, batch);
                batch = new ArrayList<>();
                count = 0;
                System.out.println("saved " + globalCount + " rows");
            } else {
                batch.add(dtoFromLine(line));
                count++;
            }
            globalCount++;
        }
        if (!batch.isEmpty()) {
            saveBatch(conn, batch);
            System.out.println("Save last batch");
        }
        System.out.println("Finished, total rows " + globalCount);
    }

    private static Dto dtoFromLine(String line) throws ParseException {
        String[] arr = line.split(" ");
        String req = arr[6].replace("//", "/");
        String[] params = req.split("\\?");
        return Dto.builder().date(createDate(arr[3]))
                .request(params[0].substring(req.indexOf("api") != -1 ? req.indexOf("api") : 0))
                .code(Integer.parseInt(arr[8]))
                .duration(Long.parseLong(arr[9]))
                .add1(arr[10])
                .add2(arr[11])
                .add3(arr[12])
                .params(params.length > 1 ? params[1] : "")
                .build();
    }

    private static SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss");

    private static Date createDate(String s) throws ParseException {
        return sdf.parse(s.replace("[", ""));
    }

    private static void saveBatch(Connection conn, List<Dto> batch) throws SQLException {
        for (Dto dto : batch) {
            try (PreparedStatement ps = conn.prepareStatement("insert into nginx(date,request,code,duration,add1,add2,add3,params) values(?,?,?,?,?,?,?,?)")) {
                ps.setTimestamp(1, new Timestamp(dto.getDate().getTime()));
                ps.setString(2, dto.getRequest());
                ps.setInt(3, dto.getCode());
                ps.setLong(4, dto.getDuration());
                ps.setString(5, dto.getAdd1());
                ps.setString(6, dto.getAdd2());
                ps.setString(7, dto.getAdd3());
                ps.setString(8, dto.getParams());
                ps.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        conn.commit();
    }

    @Data
    @Builder
    public static class Dto {
        private Date date;
        private String request;
        private Integer code;
        private Long duration;
        private String add1;
        private String add2;
        private String add3;
        private String params;
    }
}
