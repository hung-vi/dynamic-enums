package com.hungvi.labs.dynamicenums;

public class FareClass extends DynamicJPAEnumType<FareClass, String, FareClass.JPAConverter> {

    public static final FareClass ECONOMY = new FareClass("economy");
    public static final FareClass PREMIUM_ECO = new FareClass("premium_economy");
    public static final FareClass BUSINESS = new FareClass("business");
    public static final FareClass FIRST = new FareClass("first");

    private FareClass(String dbValue) {
        super(dbValue);
    }

    public static class JPAConverter extends DynamicAttributeConverter<FareClass, String> {
        @Override
        protected FareClass createEntityAttribute(String dbValue) {
            return new FareClass(dbValue);
        }
    }
}
