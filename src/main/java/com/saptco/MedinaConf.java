package com.saptco;

import java.util.HashMap;
import java.util.Map;

public class MedinaConf implements Conf {
    private String[][] confMedina = {
            {"E:\\temp\\1228\\medina2\\400.xlsx", "E:\\temp\\1228\\medina2\\400_UP.csv", "E:\\temp\\1228\\medina2\\400_DOWN.csv", "E:\\temp\\1228\\medina2\\out\\400_res.csv"},
            {"E:\\temp\\1228\\medina2\\401.xlsx", "E:\\temp\\1228\\medina2\\401_UP.csv", "E:\\temp\\1228\\medina2\\401_DOWN.csv", "E:\\temp\\1228\\medina2\\out\\401_res.csv"},
            {"E:\\temp\\1228\\medina2\\402.xlsx", "E:\\temp\\1228\\medina2\\402_UP.csv", "E:\\temp\\1228\\medina2\\402_DOWN.csv", "E:\\temp\\1228\\medina2\\out\\402_res.csv"},
            {"E:\\temp\\1228\\medina2\\403.xlsx", "E:\\temp\\1228\\medina2\\403_UP.csv", "E:\\temp\\1228\\medina2\\403_DOWN.csv", "E:\\temp\\1228\\medina2\\out\\403_res.csv"},
            {"E:\\temp\\1228\\medina2\\403B.xlsx", "E:\\temp\\1228\\medina2\\403B_UP.csv", "E:\\temp\\1228\\medina2\\403B_DOWN.csv", "E:\\temp\\1228\\medina2\\out\\403B_res.csv"}
//            {"/Users/werdn/Downloads/403T.xlsx", "/Users/werdn/Downloads/medina/403_403T_UP.csv", "/Users/werdn/Downloads/medina/403_403T_DOWN.csv", "/Users/werdn/Downloads/medina/out/403T_res.csv"},
//            {"/Users/werdn/Downloads/403BT.xlsx", "/Users/werdn/Downloads/medina/403B_403BT_UP.csv", "/Users/werdn/Downloads/medina/403B_403BT_DOWN.csv", "/Users/werdn/Downloads/medina/out/403BT_res.csv"}
    };

    @Override
    public String[][] filesConf() {
        return confMedina;
    }

    @Override
    public boolean skipFirst() {
        return true;
    }

    @Override
    public Map<String, Integer> xlsMap() {
        Map<String, Integer> map = new HashMap<>();
        map.put("dir", 0);
        map.put("stationName", 2);
        map.put("busStopId", 1);
        map.put("busStopName", 3);
        map.put("lon", 5);
        map.put("lat", 7);

        return map;
    }
}
