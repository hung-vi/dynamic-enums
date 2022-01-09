package com.hungvi.labs.dynamicenums;

import java.util.Objects;

/**
 * Abstract wrapper for dynamic enum class
 *
 * @param <T> Data type of db column
 * @param <E> Enum wrapper
 */
public abstract class DynamicJPAEnumType<E extends JPAEnumType<T>, T, C extends DynamicAttributeConverter<E, T>>
        implements JPAEnumType<T> {

    protected final Class<E> enumClass;
    protected final T dbValue;

    public DynamicJPAEnumType(T dbValue) {
        this.enumClass = GenericUtils.getGenericClass(this.getClass(), 0);
        DynamicAttributeConverter<E, T> converter = (DynamicAttributeConverter<E, T>) DynamicJPAEnumType.getConverter(this.getClass());
        this.dbValue = converter.normalizeDBValue(dbValue);
        DynamicJPACache.saveEnumObject(this);
    }

    @Override
    public T getDbValue() {
        return dbValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DynamicJPAEnumType<?, ?, ?> that = (DynamicJPAEnumType<?, ?, ?>) o;
        return Objects.equals(dbValue, that.dbValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dbValue);
    }

    public static <E1 extends DynamicJPAEnumType<E1, T1, ?>, T1> DynamicAttributeConverter<E1, T1> getConverter(Class<?> enumClass) {
        return (DynamicAttributeConverter<E1, T1>) DynamicJPACache.getConverter((Class<? extends DynamicJPAEnumType<?, ?, ?>>) enumClass);
    }
}
