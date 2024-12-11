package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import error.handlers.ErrorHandler;
import utils.Message;

public class PharmacyDAO {
    private final Connection conn;
    private final Message message = new Message();
    private final ErrorHandler errorHandler = new ErrorHandler();

    public PharmacyDAO(Connection conn) {
        this.conn = conn;
    }

    public List<HashMap<String, String>> createPharmacy(String phoneNr, String address) {
        String query = "INSERT INTO pharmacy (phonenr, address) VALUES (?, ?)";
        List<HashMap<String, String>> result = new ArrayList<>();

        HashMap<String, String> staticInfo1 = new HashMap<>(message.getDefaultErrorMessageAsHashMap());

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, phoneNr);
            stmt.setString(2, address);
            stmt.executeUpdate();
            staticInfo1.replace("status", "success");
        } catch (SQLException e) {
            staticInfo1 = errorHandler.handleSQLException(e, staticInfo1, message);
        }
        result.add(staticInfo1);
        return result;
    }

    public List<HashMap<String, String>> updatePharmacy(String address, String phoneNr) {
        String query = "UPDATE pharmacy SET phonenr = ? WHERE address = ?";
        List<HashMap<String, String>> result = new ArrayList<>();

        HashMap<String, String> staticInfo1 = new HashMap<>(message.getDefaultErrorMessageAsHashMap());

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, phoneNr);
            stmt.setString(2, address);
            if (stmt.executeUpdate() > 0) {
                staticInfo1.replace("status", "success");
            } else {
                staticInfo1.replace(message.getHashIdStatus(), "error");
                staticInfo1.replace(message.getHashIdUserFriendlyError(), "Pharmacy update failed");
            }

        } catch (SQLException e) {
            staticInfo1 = errorHandler.handleSQLException(e, staticInfo1, message);
        }

        result.add(staticInfo1);
        return result;
    }

    public List<HashMap<String, String>> deletePharmacy(String address) {
        String query = "DELETE FROM pharmacy WHERE address = ?";
        List<HashMap<String, String>> result = new ArrayList<>();

        HashMap<String, String> staticInfo1 = new HashMap<>(message.getDefaultErrorMessageAsHashMap());

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, address);
            if (stmt.executeUpdate() > 0) {
                staticInfo1.replace("status", "success");
            } else {
                staticInfo1.replace(message.getHashIdStatus(), "error");
                staticInfo1.replace(message.getHashIdUserFriendlyError(), "Pharmacy delete failed");
            }
        } catch (SQLException e) {
            staticInfo1 = errorHandler.handleSQLException(e, staticInfo1, message);
        }
        result.add(staticInfo1);
        return result;
    }

    public List<HashMap<String, String>> getPharmacy(String address) {
        String query = "SELECT * FROM pharmacy WHERE address = ?";
        List<HashMap<String, String>> result = new ArrayList<>();

        HashMap<String, String> staticInfo1 = new HashMap<>(message.getDefaultErrorMessageAsHashMap());

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, address);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    HashMap<String, String> pharmacy = new HashMap<>();
                    pharmacy.put("phoneNr", rs.getString("phonenr"));
                    pharmacy.put("address", rs.getString("address"));
                    result.add(pharmacy);
                } else {
                    staticInfo1.replace(message.getHashIdStatus(), "error");
                    staticInfo1.replace(message.getHashIdUserFriendlyError(), "Pharmacy not found");
                }
            }
        } catch (SQLException e) {
            staticInfo1 = errorHandler.handleSQLException(e, staticInfo1, message);
        }
        result.add(staticInfo1);
        return result;
    }

    public List<HashMap<String, String>> getAllPharmacies() {
        String query = "SELECT * FROM pharmacy";
        List<HashMap<String, String>> result = new ArrayList<>();

        HashMap<String, String> staticInfo1 = new HashMap<>(message.getDefaultErrorMessageAsHashMap());

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    HashMap<String, String> pharmacy = new HashMap<>();
                    pharmacy.put("phoneNr", rs.getString("phonenr"));
                    pharmacy.put("address", rs.getString("address"));
                    result.add(pharmacy);
                }
                staticInfo1.replace("status", "success");
            }
        } catch (SQLException e) {
            staticInfo1 = errorHandler.handleSQLException(e, staticInfo1, message);
        }
        result.add(staticInfo1);
        return result;
    }

    public List<HashMap<String, String>> getPharmacyIdByAddress(String address) {
        String query = "SELECT pharmacyid FROM pharmacy WHERE address = ?";
        List<HashMap<String, String>> result = new ArrayList<>();

        HashMap<String, String> staticInfo1 = new HashMap<>(message.getDefaultErrorMessageAsHashMap());

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, address);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    HashMap<String, String> pharmacyIdInfo = new HashMap<>();
                    pharmacyIdInfo.put("pharmacyId", String.valueOf(rs.getInt("pharmacyid")));
                    result.add(pharmacyIdInfo);
                    staticInfo1.replace("status", "success");
                } else {
                    staticInfo1.replace(message.getHashIdStatus(), "error");
                    staticInfo1.replace(message.getHashIdUserFriendlyError(), "Pharmacy not found for the given address");
                }
            }
        } catch (SQLException e) {
            staticInfo1 = errorHandler.handleSQLException(e, staticInfo1, message);
        }

        result.add(staticInfo1);
        return result;
    }

}
