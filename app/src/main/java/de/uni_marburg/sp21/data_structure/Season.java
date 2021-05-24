package de.uni_marburg.sp21.data_structure;

import java.io.Serializable;

public enum Season implements Serializable {
    SPRING,
    SUMMER,
    AUTUMN,
    WINTER;

    public static Season fromDatabaseString(String s) {
        switch (s) {
            case "spring":
                return SPRING;
            case "summer":
                return SUMMER;
            case "autumn":
                return AUTUMN;
            default:
                return WINTER;
        }
    }
}
