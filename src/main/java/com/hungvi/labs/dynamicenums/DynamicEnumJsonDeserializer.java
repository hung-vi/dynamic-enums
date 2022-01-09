package com.hungvi.labs.dynamicenums;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.IOException;

@NoArgsConstructor
@AllArgsConstructor
public class DynamicEnumJsonDeserializer<E extends DynamicJPAEnumType<E, ?, ?>> extends JsonDeserializer<E>
        implements ContextualDeserializer {
    private Class enumType;

    @Override
    public E deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        if (enumType == null) {
            return null;
        }
        Object enumValue = ctxt.readValue(p, Object.class);
        if (enumValue == null) {
            return null;
        }
        DynamicJPAEnumType<?, ?, ?> parsedEnumValue = DynamicEnumParser.parse(enumType, enumValue);
        return (E) parsedEnumValue;
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        JavaType wrapperType = property.getType();
        DynamicEnumJsonDeserializer deserializer = new DynamicEnumJsonDeserializer();
        deserializer.enumType = wrapperType.getRawClass();
        return deserializer;
    }
}
