package de.uni_marburg.sp21.company_data_structure;

import java.io.Serializable;

public enum Season implements Serializable {
    SPRING,
    SUMMER,
    AUTUMN,
    WINTER;

    /**
     * Generates a Season from the String, that has been passed from the Database
     * @param s the Database-String
     * @return the Season representation of the Database-String
     */
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
