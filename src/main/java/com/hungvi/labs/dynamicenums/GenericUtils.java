package com.hungvi.labs.dynamicenums;

import java.lang.reflect.ParameterizedType;

public class GenericUtils {

    public static <G> Class<G> getGenericClass(Class<?> clazz, int typeIndex) {
        return (Class<G>) ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments()[typeIndex];
    }

}
