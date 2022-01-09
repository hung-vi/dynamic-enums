package com.hungvi.labs.dynamicenums;

public class DynamicEnumParser {

    @SuppressWarnings("unchecked")
    public static <E extends DynamicJPAEnumType<E, T, C>, T, C extends DynamicAttributeConverter<E, T>> E parse(Class<E> clazz, T value) {
        if (value == null) {
            return null;
        }
        DynamicAttributeConverter<?, ?> converter = DynamicJPACache.getConverter(clazz);
        if (converter == null) {
            return null;
        }
        JPAEnumType<T> enumObject = DynamicJPACache.getEnumObject((DynamicAttributeConverter<E, T>) converter, value);
        if (enumObject == null) {
            return null;
        }
        return clazz.cast(enumObject);
    }

}
