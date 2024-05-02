package me.pesekjak.vitrum;

import org.junit.jupiter.api.Test;

public class ReflectiveVitrumTest {

    @Test
    public void testReflectiveWindow() throws IllegalAccessException {
        ReflectiveTestInterface window = Vitrum.createWindow(ReflectiveTestInterface.class, "v1");
        ReflectiveTestInstance instance = window.construct(null, "Hello");

        String bar = window.getBar(instance);
        assert bar.equals("Hello");

        window.setBar(instance, "Goodbye");
        bar = window.getBar(instance);
        assert bar.equals("Goodbye");

        String foo = window.getFoo(instance, 10, "World");
        assert foo.equals("10 foo World");
    }

}
