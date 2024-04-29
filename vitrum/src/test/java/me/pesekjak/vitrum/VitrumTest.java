package me.pesekjak.vitrum;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class VitrumTest {

    @Test
    public void testSimpleMethod() throws IllegalAccessException {
        TestInstance instance = new TestInstance();
        TestInterface window = Vitrum.createWindow(TestInterface.class, "v1");
        window.foo(instance);
    }

    @Test
    public void missingImpl() throws IllegalAccessException {
        TestInterface window = Vitrum.createWindow(TestInterface.class, "v2");
        Assertions.assertThrows(UnsupportedOperationException.class, () -> window.foo(new TestInstance()));
    }

    @Test
    public void testArgument() throws IllegalAccessException {
        TestInstance instance = new TestInstance();
        TestInterface window = Vitrum.createWindow(TestInterface.class, "v1");
        assert window.plusOne(instance, 0) == 1;
    }

    @Test
    public void testMultiVersion() throws IllegalAccessException {
        TestInstance instance = new TestInstance();
        TestInterface window = Vitrum.createWindow(TestInterface.class, "v1");
        assert window.v(instance).equals("v1");

        window = Vitrum.createWindow(TestInterface.class, "v2");
        assert window.v(instance).equals("v2");
    }

    @Test
    public void testStatic() throws IllegalAccessException {
        TestInterface window = Vitrum.createWindow(TestInterface.class, "v1");
        assert window.staticMethod(null, 2, 8);
    }

    @Test
    public void testComplex() throws IllegalAccessException {
        TestInstance instance = new TestInstance();
        TestInterface window = Vitrum.createWindow(TestInterface.class, "v1");
        assert window.complex(instance, 1, 2, 3) == 1f;
    }

}
