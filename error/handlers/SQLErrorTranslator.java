package error.handlers;
import java.util.HashMap;

public class SQLErrorTranslator {
    private static final HashMap<String, String> errorMessages = new HashMap<>();

    static {
        // Unique constraint violations
        errorMessages.put("23505", "A record with the specified unique value already exists.");
        errorMessages.put("23502", "A required field is missing.");
        errorMessages.put("23514", "The data violates a CHECK constraint.");

        // Foreign key and integrity constraint errors
        errorMessages.put("23503", "Cannot delete or update due to existing related data.");
        errorMessages.put("23000", "Integrity constraint violation (foreign key or primary key).");

        // Connection issues
        errorMessages.put("08001", "Unable to establish a connection to the database.");
        errorMessages.put("08003", "The connection to the database has been closed.");
        errorMessages.put("08006", "Connection failure during the transaction.");

        // Table and column issues
        errorMessages.put("42P01", "The specified table does not exist.");
        errorMessages.put("42P07", "The table already exists.");
        errorMessages.put("42703", "The specified column does not exist.");

        // Data type errors
        errorMessages.put("22P02", "Invalid input syntax for the expected data type.");
        errorMessages.put("22001", "Data value is too long for the column.");
        errorMessages.put("22007", "Invalid format for date or time value.");

        // Access and permission errors
        errorMessages.put("42501", "Insufficient privileges to perform this operation.");
        errorMessages.put("28P01", "Invalid username or password.");

        // Syntax and transaction errors
        errorMessages.put("42601", "Syntax error in the SQL statement.");
        errorMessages.put("40001", "Transaction conflict detected and rolled back.");
        errorMessages.put("HY000", "General SQL error: unspecified server issue.");
    }

    public static String translate(String sqlState) {
        return errorMessages.getOrDefault(sqlState, "An unknown SQL error occurred.");
    }
}
