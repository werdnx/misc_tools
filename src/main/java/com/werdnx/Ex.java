package com.werdnx;

public class Ex {
    public static void main(String[] args) {
        Item i1 = new Item("i1");
        Item i3 = m(i1);
        System.out.println("i1 + " + i1);
        System.out.println("i3 + " + i3);
    }

    static Item m(Item i) {
        i = new Item("i2");
        return i;
    }


    static class Item {
        public Item(String b) {
            a = b;
        }

        String a;

        @Override
        public String toString() {
            return "Item{" +
                    "a='" + a + '\'' +
                    '}';
        }
    }
}
