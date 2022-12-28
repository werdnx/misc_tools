package com.saptco;

import java.util.HashMap;
import java.util.Map;

public class DammamConf implements Conf {
    private String[][] confDammam = {
            {"/Users/werdn/Downloads/RouteA.xlsx", "/Users/werdn/Downloads/Dammam/Dammam/Route A 1_1_up.csv", "/Users/werdn/Downloads/Dammam/Dammam/Route A 1_1_down.csv", "/Users/werdn/Downloads/Dammam/out/RouteA_res.csv"},
            {"/Users/werdn/Downloads/RouteB.xlsx", "/Users/werdn/Downloads/Dammam/Dammam/Rahmaniyah_2_up.csv", "/Users/werdn/Downloads/Dammam/Dammam/Rahmaniyah_2_up.csv", "/Users/werdn/Downloads/Dammam/out/RouteB_res.csv"},
            {"/Users/werdn/Downloads/RouteC.xlsx", "/Users/werdn/Downloads/Dammam/Dammam/Route C_3_up.csv", "/Users/werdn/Downloads/Dammam/Dammam/Route C_3_down.csv", "/Users/werdn/Downloads/Dammam/out/RouteC_res.csv"},
            {"/Users/werdn/Downloads/RouteD.xlsx", "/Users/werdn/Downloads/Dammam/Dammam/Route D4_4_up.csv", "/Users/werdn/Downloads/Dammam/Dammam/Route D4_4_down.csv", "/Users/werdn/Downloads/Dammam/out/RouteD_res.csv"},
            {"/Users/werdn/Downloads/RouteE.xlsx", "/Users/werdn/Downloads/Dammam/Dammam/Route E5_5_up.csv", "/Users/werdn/Downloads/Dammam/Dammam/Route E5_5_down.csv", "/Users/werdn/Downloads/Dammam/out/RouteE_res.csv"},
            {"/Users/werdn/Downloads/RouteF.xlsx", "/Users/werdn/Downloads/Dammam/Dammam/F6_6_up.csv", "/Users/werdn/Downloads/Dammam/Dammam/F6_6_down.csv", "/Users/werdn/Downloads/Dammam/out/RouteF_res.csv"},
            {"/Users/werdn/Downloads/RouteG.xlsx", "/Users/werdn/Downloads/Dammam/Dammam/marina-Dmm_7_up.csv", "/Users/werdn/Downloads/Dammam/Dammam/marina-Dmm_7_down.csv", "/Users/werdn/Downloads/Dammam/out/RouteG_res.csv"},
            {"/Users/werdn/Downloads/RouteH.xlsx", "/Users/werdn/Downloads/Dammam/Dammam/Second Industrial_8_up.csv", "/Users/werdn/Downloads/Dammam/Dammam/Second Industrial_8_down.csv", "/Users/werdn/Downloads/Dammam/out/RouteH_res.csv"}
    };

    @Override
    public String[][] filesConf() {
        return confDammam;
    }

    @Override
    public boolean skipFirst() {
        return true;
    }

    @Override
    public Map<String, Integer> xlsMap() {
        Map<String, Integer> map = new HashMap<>();
        map.put("dir", 0);
        map.put("stationName", 1);
        map.put("busStopId", 2);
        map.put("busStopName", 3);
        map.put("lon", 5);
        map.put("lat", 7);

        return map;
    }
}
