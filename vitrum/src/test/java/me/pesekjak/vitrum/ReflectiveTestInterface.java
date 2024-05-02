package me.pesekjak.vitrum;

public interface ReflectiveTestInterface {

    @Target(handler = "v1", source = "me/pesekjak/vitrum/ReflectiveTestInstance", name = "<init>", descriptor = "(Ljava/lang/String;)V", action = Target.Action.CALL_CONSTRUCTOR, reflective = true)
    ReflectiveTestInstance construct(Object instance, Object... arguments);

    @Target(handler = "v1", source = "me/pesekjak/vitrum/ReflectiveTestInstance", name = "bar", descriptor = "Ljava/lang/String;", action = Target.Action.GET_FIELD, reflective = true)
    String getBar(Object instance, Object... arguments);

    @Target(handler = "v1", source = "me/pesekjak/vitrum/ReflectiveTestInstance", name = "bar", descriptor = "Ljava/lang/String;", action = Target.Action.SET_FIELD, reflective = true)
    ReflectiveTestInstance setBar(Object instance, Object... arguments);

    @Target(handler = "v1", source = "me/pesekjak/vitrum/ReflectiveTestInstance", name = "getFoo", descriptor = "(ILjava/lang/String;)Ljava/lang/String;", action = Target.Action.CALL_METHOD, reflective = true)
    String getFoo(Object instance, Object... arguments);

}
