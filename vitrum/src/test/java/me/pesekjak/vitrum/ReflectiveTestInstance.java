package me.pesekjak.vitrum;

public class ReflectiveTestInstance {

    private final String bar;

    private ReflectiveTestInstance(String bar) {
        this.bar = bar;
    }

    public ReflectiveTestInstance() {
        this.bar = "bar";
    }

    private String getFoo(int value, String second) {
        return value + " foo " + second;
    }

}
