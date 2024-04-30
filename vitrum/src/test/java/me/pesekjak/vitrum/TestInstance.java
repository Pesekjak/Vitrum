package me.pesekjak.vitrum;

public class TestInstance {

    public int number = 10;

    public static final String staticString = "Hello World";

    public double changeMe = 0;

    public int plusOne(Integer num) {
        return num + 1;
    }

    public void foo() {
    }

    public String v1() {
        return "v1";
    }

    public String v2() {
        return "v2";
    }

    public static boolean staticMethod(int i, Integer i2) {
        return i + i2 == 10;
    }

    public float complex(int i, Integer i2, double i3) {
        return 1;
    }

}
