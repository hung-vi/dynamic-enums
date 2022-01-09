package com.hungvi.labs.dynamicenums;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

final class DynamicJPACache {

    private static final Map<Class<? extends JPAEnumType<?>>, Map<Object, DynamicJPAEnumType>> STATIC_VALUES = new ConcurrentHashMap<>();

    private static final Map<Class<? extends JPAEnumType<?>>, DynamicAttributeConverter<?, ?>> STATIC_CONVERTERS = new ConcurrentHashMap<>();

    private static final Set<Class> INITIALIZED_ENUM_CLASSES = Collections.synchronizedSet(new HashSet<>());

    static DynamicAttributeConverter<?, ?> getConverter(DynamicJPAEnumType<?, ?, ?> enumType) {
        return STATIC_CONVERTERS.computeIfAbsent(enumType.enumClass, (clazz) -> {
            try {
                Class<? extends DynamicAttributeConverter<?, ?>> converterClass = GenericUtils.getGenericClass(enumType.getClass(), 2);
                return converterClass.getConstructor().newInstance();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    static DynamicAttributeConverter<?, ?> getConverter(final Class<? extends DynamicJPAEnumType<?, ?, ?>> enumClass) {
        return STATIC_CONVERTERS.computeIfAbsent(enumClass, (clazz) -> {
            try {
                Class<? extends DynamicAttributeConverter<?, ?>> converterClass = GenericUtils.getGenericClass(enumClass, 2);
                return converterClass.getConstructor().newInstance();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    static <E extends JPAEnumType<T>, T> E getEnumObject(DynamicAttributeConverter<E, T> converter, T dbValue) {
        if (dbValue == null) {
            return null;
        }
        Map mapValues = getEnumInstancesMap(converter.enumClass);
        T normalizedDBValue = converter.normalizeDBValue(dbValue);
        E o = (E) mapValues.get(normalizedDBValue);
        if (o != null) {
            return o;
        }
        return converter.createEntityAttribute(normalizedDBValue);
    }

    public static <E extends JPAEnumType<T>, C extends DynamicAttributeConverter<E, T>, T> void saveEnumObject(DynamicJPAEnumType enumType) {
        Map mapValues = getEnumInstancesMap(enumType.enumClass);
        mapValues.putIfAbsent(enumType.dbValue, enumType);
    }

    private static <E extends JPAEnumType<T>, T, C extends DynamicAttributeConverter<E, T>> Map getEnumInstancesMap(Class<E> enumClass) {
        Map<Object, DynamicJPAEnumType> enumInstancesMap = STATIC_VALUES.compute(enumClass, (tmpEnumType, mapValue) -> {
            if (mapValue == null) {
                mapValue = new ConcurrentHashMap<>();
            }
            return mapValue;
        });
        initEnumInstancesMap(enumInstancesMap, enumClass);
        return enumInstancesMap;
    }

    static <E> void initEnumInstancesMap(Map enumInstancesMap, Class<E> enumClass) {
        if (INITIALIZED_ENUM_CLASSES.contains(enumClass)) {
            return;
        }
        INITIALIZED_ENUM_CLASSES.add(enumClass);
        Field[] declaredFields = enumClass.getDeclaredFields();
        for (Field field : declaredFields) {
            if (Modifier.isFinal(field.getModifiers())
                    && Modifier.isStatic(field.getModifiers())
                    && field.getType().isAssignableFrom(enumClass)) {
                try {
                    Object fieldValue = field.get(null);
                    if (fieldValue != null) {
                        Object dbValue = ((JPAEnumType) fieldValue).getDbValue();
                        enumInstancesMap.putIfAbsent(dbValue, enumClass.cast(fieldValue));
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
