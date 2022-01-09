package com.hungvi.labs.dynamicenums;


import javax.persistence.AttributeConverter;
import java.util.Optional;

/**
 * Abstract wrapper for dynamic attribute converter
 *
 * @param <E> Enum wrapper class
 * @param <T> Data type of db column
 */
public abstract class DynamicAttributeConverter<E extends JPAEnumType<T>, T>
        implements AttributeConverter<E, T> {

    protected final Class<E> enumClass;

    public DynamicAttributeConverter() {
        this.enumClass = GenericUtils.getGenericClass(this.getClass(), 0);
    }

    @Override
    public T convertToDatabaseColumn(E attribute) {
        return Optional.ofNullable(attribute)
                .map(JPAEnumType::getDbValue)
                .orElse(null);
    }

    @Override
    public E convertToEntityAttribute(T dbValue) {
        if (dbValue == null) {
            return null;
        }
        return DynamicJPACache.getEnumObject(this, dbValue);
    }

    protected abstract E createEntityAttribute(T dbValue);

    protected T normalizeDBValue(T dbValue) {
        return dbValue instanceof String ? (T) (((String) dbValue).toUpperCase()) : dbValue;
    }
}
