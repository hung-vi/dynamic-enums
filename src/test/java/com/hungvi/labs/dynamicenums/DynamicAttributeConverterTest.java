package com.hungvi.labs.dynamicenums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DynamicAttributeConverterTest {

    @Test
    void testStaticInstanceCreatedBeforeAnyUnknownEnums() {
        FareClass economy = DynamicEnumParser.parse(FareClass.class, "economy");
        assertTrue(economy == FareClass.ECONOMY);;
    }
}