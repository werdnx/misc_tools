package com.saptco;

import java.util.Map;

public interface Conf {

    String[][] filesConf();

    boolean skipFirst();

    Map<String, Integer> xlsMap();
}
