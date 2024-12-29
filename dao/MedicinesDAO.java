package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import error.handlers.ErrorHandler;
import utils.Message;

public class MedicinesDAO {
    private final Connection conn;
    private final Message message = new Message();
    private final ErrorHandler errorHandler = new ErrorHandler();

    public MedicinesDAO(Connection conn) {
        this.conn = conn;
    }

    public List<HashMap<String, String>> addMedicine(String name, String description, double price) {
        String query = "INSERT INTO medicines (name, description, price) VALUES (?, ?, ?)";
        List<HashMap<String, String>> result = new ArrayList<>();

        HashMap<String, String> staticInfo = new HashMap<>(message.getDefaultErrorMessageAsHashMap());

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setString(2, description);
            stmt.setDouble(3, price);
            stmt.executeUpdate();
        } catch (SQLException e) {
            staticInfo = errorHandler.handleSQLException(e, staticInfo, message);
        }
        result.add(staticInfo);
        return result;
    }

    public List<HashMap<String, String>> updateMedicinePrice(String name, double newPrice) {
        String query = "UPDATE medicines SET price = ? WHERE name = ?";
        List<HashMap<String, String>> result = new ArrayList<>();

        HashMap<String, String> staticInfo = new HashMap<>(message.getDefaultErrorMessageAsHashMap());

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDouble(1, newPrice);
            stmt.setString(2, name);
            if (stmt.executeUpdate() > 0) {
                HashMap<String, String> successInfo = new HashMap<>();
                successInfo.put("success", "true");
                result.add(successInfo);
            } else {
                staticInfo.replace(message.getHashIdStatus(), "error");
                staticInfo.replace(message.getHashIdUserFriendlyError(), "Medicine price was not updated");
            }
        } catch (SQLException e) {
            staticInfo = errorHandler.handleSQLException(e, staticInfo, message);
        }
        result.add(staticInfo);
        return result;
    }

    public List<HashMap<String, String>> deleteMedicine(String name) {
        String query = "DELETE FROM medicines WHERE name = ?";
        List<HashMap<String, String>> result = new ArrayList<>();

        HashMap<String, String> staticInfo = new HashMap<>(message.getDefaultErrorMessageAsHashMap());

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, name);
            if (stmt.executeUpdate() > 0) {
                HashMap<String, String> successInfo = new HashMap<>();
                successInfo.put("success", "true");
                result.add(successInfo);
            } else {
                staticInfo.replace(message.getHashIdStatus(), "error");
                staticInfo.replace(message.getHashIdUserFriendlyError(), "Medicine was not deleted");
            }
        } catch (SQLException e) {
            staticInfo = errorHandler.handleSQLException(e, staticInfo, message);
        }

        result.add(staticInfo);
        return result;
    }

    public List<HashMap<String, String>> getMedicine(String name) {
        String query = "SELECT * FROM medicines WHERE name = ?";
        List<HashMap<String, String>> result = new ArrayList<>();

        HashMap<String, String> staticInfo = new HashMap<>(message.getDefaultErrorMessageAsHashMap());
        result.add(staticInfo);

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    HashMap<String, String> medicine = new HashMap<>();
                    medicine.put("drugId", String.valueOf(rs.getInt("drugid")));
                    medicine.put("name", rs.getString("name"));
                    medicine.put("description", rs.getString("description"));
                    medicine.put("price", String.valueOf(rs.getDouble("price")));
                    result.add(medicine);
                } else {
                    staticInfo.replace(message.getHashIdStatus(), "error");
                    staticInfo.replace(message.getHashIdUserFriendlyError(), "Medicine not found");
                    result.set(0, staticInfo);
                }
            }
        } catch (SQLException e) {
            staticInfo = errorHandler.handleSQLException(e, staticInfo, message);
            result.set(0, staticInfo);
        }
        return result;
    }

    public List<HashMap<String, String>> getAllMedicines() {
        String query = "SELECT * FROM medicines";
        List<HashMap<String, String>> medicines = new ArrayList<>();

        HashMap<String, String> staticInfo = new HashMap<>(message.getDefaultErrorMessageAsHashMap());

        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                HashMap<String, String> medicine = new HashMap<>();
                medicine.put("drugId", String.valueOf(rs.getInt("drugid")));
                medicine.put("name", rs.getString("name"));
                medicine.put("description", rs.getString("description"));
                medicine.put("price", String.valueOf(rs.getDouble("price")));
                medicines.add(medicine);
            }
        } catch (SQLException e) {
            staticInfo = errorHandler.handleSQLException(e, staticInfo, message);
        }

        medicines.add(staticInfo);
        return medicines;
    }

    public List<HashMap<String, String>> getMedicineIdByName(String name) {
        String query = "SELECT drugid FROM medicines WHERE name = ?";
        List<HashMap<String, String>> result = new ArrayList<>();

        HashMap<String, String> staticInfo = new HashMap<>(message.getDefaultErrorMessageAsHashMap());

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    HashMap<String, String> medicineIdInfo = new HashMap<>();
                    medicineIdInfo.put("drugId", String.valueOf(rs.getInt("drugid")));
                    result.add(medicineIdInfo);
                    staticInfo.replace("status", "success");
                } else {
                    staticInfo.replace(message.getHashIdStatus(), "error");
                    staticInfo.replace(message.getHashIdUserFriendlyError(), "Medicine not found for the given name");
                }
            }
        } catch (SQLException e) {
            staticInfo = errorHandler.handleSQLException(e, staticInfo, message);
        }

        result.add(staticInfo);
        return result;
    }

}
