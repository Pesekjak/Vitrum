package me.pesekjak.vitrum;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class VitrumTest {

    @Test
    public void testSimpleMethod() throws IllegalAccessException {
        TestInstance instance = new TestInstance();
        TestInterface window = Vitrum.createWindow(TestInterface.class, "v1");
        assert window.foo(instance) == null;
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

    @Test
    public void testField() throws IllegalAccessException {
        TestInstance instance = new TestInstance();
        TestInterface window = Vitrum.createWindow(TestInterface.class, "v1");
        assert window.numberField(instance) == 10;
    }

    @Test
    public void testStaticField() throws IllegalAccessException {
        TestInstance instance = new TestInstance();
        TestInterface window = Vitrum.createWindow(TestInterface.class, "v1");
        assert window.staticString(instance).equals("Hello World");
    }

    @Test
    public void testSetField() throws IllegalAccessException {
        TestInstance instance = new TestInstance();
        TestInterface window = Vitrum.createWindow(TestInterface.class, "v1");
        assert window.changeDouble(instance, 1) == 0;
        assert instance.changeMe == 1;
    }

    @Test
    public void testConstructor() throws IllegalAccessException {
        TestInterface window = Vitrum.createWindow(TestInterface.class, "v1");
        TestInstance instance = window.construct(null, 75, "Hello");
        assert instance.number == 75;
        assert instance.foo.equals("Hello");
    }

}
