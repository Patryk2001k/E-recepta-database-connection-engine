package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import error.handlers.ErrorHandler;
import utils.GeneralPurpouseMethods;
import utils.Message;

public class GeneralDAO {
    private final Connection conn;
    private final Message message = new Message();
    private final ErrorHandler errorHandler = new ErrorHandler();

    public GeneralDAO(Connection conn) {
        this.conn = conn;
    }

    public List<HashMap<String, String>> insert(String tableName, HashMap<String, Object> data) {
        String columns = String.join(", ", data.keySet());
        String placeholders = String.join(", ", data.keySet().stream().map(k -> "?").toList());
        String query = String.format("INSERT INTO %s (%s) VALUES (%s)", tableName, columns, placeholders);
        List<HashMap<String, String>> result = new ArrayList<>();
        HashMap<String, String> staticInfo = new HashMap<>(message.getDefaultErrorMessageAsHashMap());

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            int index = 1;
            for (Object value : data.values()) {
                stmt.setObject(index++, value);
            }
            stmt.executeUpdate();
            staticInfo.replace(message.getHashIdStatus(), "success");
        } catch (SQLException e) {
            staticInfo = errorHandler.handleSQLException(e, staticInfo, message);
        }

        result.add(staticInfo);
        return result;
    }

    public List<HashMap<String, String>> update(String tableName, HashMap<String, Object> data, String whereClause, Object... whereArgs) {
        String setClause = String.join(", ", data.keySet().stream().map(key -> key + " = ?").toList());
        String query = String.format("UPDATE %s SET %s WHERE %s", tableName, setClause, whereClause);
        List<HashMap<String, String>> result = new ArrayList<>();
        HashMap<String, String> staticInfo = new HashMap<>(message.getDefaultErrorMessageAsHashMap());

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            int index = 1;
            for (Object value : data.values()) {
                stmt.setObject(index++, value);
            }
            for (Object arg : whereArgs) {
                stmt.setObject(index++, arg);
            }
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                staticInfo.replace(message.getHashIdStatus(), "success");
            } else {
                staticInfo.replace(message.getHashIdUserFriendlyError(), "No rows updated.");
            }
        } catch (SQLException e) {
            staticInfo = errorHandler.handleSQLException(e, staticInfo, message);
        }

        result.add(staticInfo);
        return result;
    }

    public List<HashMap<String, String>> delete(String tableName, String whereClause, Object... whereArgs) {
        String query = String.format("DELETE FROM %s WHERE %s", tableName, whereClause);
        List<HashMap<String, String>> result = new ArrayList<>();
        HashMap<String, String> staticInfo = new HashMap<>(message.getDefaultErrorMessageAsHashMap());

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            int index = 1;
            for (Object arg : whereArgs) {
                stmt.setObject(index++, arg);
            }
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                staticInfo.replace(message.getHashIdStatus(), "success");
            } else {
                staticInfo.replace(message.getHashIdUserFriendlyError(), "No rows deleted.");
            }
        } catch (SQLException e) {
            staticInfo = errorHandler.handleSQLException(e, staticInfo, message);
        }

        result.add(staticInfo);
        return result;
    }

    public List<HashMap<String, String>> select(String tableName, Set<String> columns, String whereClause, Object... whereArgs) {
        String columnList = columns.isEmpty() ? "*" : String.join(", ", columns);
        String query = String.format("SELECT %s FROM %s WHERE %s", columnList, tableName, whereClause);
        List<HashMap<String, String>> result = new ArrayList<>();
        HashMap<String, String> staticInfo = new HashMap<>(message.getDefaultErrorMessageAsHashMap());

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            int index = 1;
            for (Object arg : whereArgs) {
                stmt.setObject(index++, arg);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    HashMap<String, String> row = new HashMap<>();
                    for (String column : columns) {
                        Object value = rs.getObject(column);
                        String stringValue = GeneralPurpouseMethods.convertToString(value);
                        row.put(column, stringValue);
                    }
                    result.add(row);
                }
            }

            staticInfo.replace(message.getHashIdStatus(), "success");
        } catch (SQLException e) {
            staticInfo = errorHandler.handleSQLException(e, staticInfo, message);
        }

        result.add(staticInfo);
        return result;
    }
}