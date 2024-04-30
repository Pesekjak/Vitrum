package me.pesekjak.vitrum;

public interface TestInterface {

    @Target(handler = "v1", source = "me/pesekjak/vitrum/TestInstance", name = "plusOne", descriptor = "(Ljava/lang/Integer;)I")
    int plusOne(Object instance, Object... arguments);

    @Target(handler = "v1", source = "me/pesekjak/vitrum/TestInstance", name = "foo", descriptor = "()V")
    String foo(Object instance, Object... arguments);

    @Target(handler = "v1", source = "me/pesekjak/vitrum/TestInstance", name = "v1", descriptor = "()Ljava/lang/String;")
    @Target(handler = "v2", source = "me/pesekjak/vitrum/TestInstance", name = "v2", descriptor = "()Ljava/lang/String;")
    String v(Object instance, Object... arguments);

    @Target(handler = "v1", source = "me/pesekjak/vitrum/TestInstance", name = "staticMethod", descriptor = "(ILjava/lang/Integer;)Z", isStatic = true)
    boolean staticMethod(Object instance, Object... arguments);

    @Target(handler = "v1", source = "me/pesekjak/vitrum/TestInstance", name = "complex", descriptor = "(ILjava/lang/Integer;D)F")
    float complex(Object instance, Object... arguments);

    @Target(handler = "v1", source = "me/pesekjak/vitrum/TestInstance", name = "number", descriptor = "I", action = Target.Action.GET_FIELD)
    int numberField(Object instance, Object... arguments);

    @Target(handler = "v1", source = "me/pesekjak/vitrum/TestInstance", name = "staticString", descriptor = "Ljava/lang/String;", isStatic = true, action = Target.Action.GET_FIELD)
    String staticString(Object instance, Object... arguments);

    @Target(handler = "v1", source = "me/pesekjak/vitrum/TestInstance", name = "changeMe", descriptor = "D", action = Target.Action.SET_FIELD)
    int changeDouble(Object instance, Object... arguments);

}
