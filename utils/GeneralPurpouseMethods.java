package utils;
import java.util.Date;

public class GeneralPurpouseMethods {
    public static String convertToString(Object value) {
        if (value == null) {
            return "null";
        } else if (value instanceof Integer) {
            return Integer.toString((Integer) value);
        } else if (value instanceof Double) {
            return Double.toString((Double) value);
        } else if (value instanceof Float) {
            return Float.toString((Float) value);
        } else if (value instanceof Long) {
            return Long.toString((Long) value);
        } else if (value instanceof Boolean) {
            return Boolean.toString((Boolean) value);
        } else if (value instanceof Date) {
            return value.toString();
        } else if (value instanceof String) {
            return (String) value;
        } else {
            return value.toString();
        }
    }
}
