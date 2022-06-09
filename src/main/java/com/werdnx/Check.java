package com.werdnx;

import java.util.concurrent.atomic.AtomicInteger;

public class Check {
    public static void main(String[] args) {
        AtomicInteger ai = null;
        System.out.println(ai != null ? String.valueOf(ai.getAndIncrement()) : "-1");
    }
}
