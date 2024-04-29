package me.pesekjak.vitrum;

public interface TestInterface {

    @Target(handler = "v1", source = "me/pesekjak/vitrum/TestInstance", name = "plusOne", descriptor = "(Ljava/lang/Integer;)I", isStatic = false)
    int plusOne(Object instance, Object... arguments);

    @Target(handler = "v1", source = "me/pesekjak/vitrum/TestInstance", name = "foo", descriptor = "()V", isStatic = false)
    void foo(Object instance, Object... arguments);

    @Target(handler = "v1", source = "me/pesekjak/vitrum/TestInstance", name = "v1", descriptor = "()Ljava/lang/String;", isStatic = false)
    @Target(handler = "v2", source = "me/pesekjak/vitrum/TestInstance", name = "v2", descriptor = "()Ljava/lang/String;", isStatic = false)
    String v(Object instance, Object... arguments);

    @Target(handler = "v1", source = "me/pesekjak/vitrum/TestInstance", name = "staticMethod", descriptor = "(ILjava/lang/Integer;)Z", isStatic = true)
    boolean staticMethod(Object instance, Object... arguments);

    @Target(handler = "v1", source = "me/pesekjak/vitrum/TestInstance", name = "complex", descriptor = "(ILjava/lang/Integer;D)F", isStatic = false)
    float complex(Object instance, Object... arguments);

}
