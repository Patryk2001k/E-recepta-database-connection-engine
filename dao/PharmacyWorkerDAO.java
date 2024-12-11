package dao;

import utils.Message;
import error.handlers.ErrorHandler;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PharmacyWorkerDAO {
    private final Connection conn;
    private final Message message = new Message();
    private final ErrorHandler errorHandler = new ErrorHandler();

    public PharmacyWorkerDAO(Connection conn) {
        this.conn = conn;
    }

    public List<HashMap<String, String>> getPharmacyWorkersByAddress(String pharmacyAddress) {
        List<HashMap<String, String>> result = new ArrayList<>();
        HashMap<String, String> staticInfo = new HashMap<>(message.getDefaultErrorMessageAsHashMap());

        try {
            String queryGetPharmacyId = "SELECT pharmacyid FROM pharmacy WHERE address = ?";
            Integer pharmacyId = null;

            try (PreparedStatement stmtGetPharmacyId = conn.prepareStatement(queryGetPharmacyId)) {
                stmtGetPharmacyId.setString(1, pharmacyAddress);
                try (ResultSet rs = stmtGetPharmacyId.executeQuery()) {
                    if (rs.next()) {
                        pharmacyId = rs.getInt("pharmacyid");
                    }
                }
            }

            if (pharmacyId == null) {
                staticInfo.replace(message.getHashIdStatus(), "error");
                staticInfo.replace(message.getHashIdUserFriendlyError(), "Pharmacy not found");
                result.add(staticInfo);
                return result;
            }

            String query = """
                SELECT u.id AS pharmacist_id, u.name, u.surname 
                FROM pharmacy_worker pw
                JOIN users u ON pw.pharmacistid = u.id
                WHERE pw.pharmacyid = ?
            """;

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, pharmacyId);

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        HashMap<String, String> workerInfo = new HashMap<>();
                        workerInfo.put("pharmacistId", String.valueOf(rs.getInt("pharmacist_id")));
                        workerInfo.put("name", rs.getString("name"));
                        workerInfo.put("surname", rs.getString("surname"));
                        result.add(workerInfo);
                    }
                }
                staticInfo.replace(message.getHashIdStatus(), "success");
            }
        } catch (SQLException e) {
            staticInfo = errorHandler.handleSQLException(e, staticInfo, message);
        }

        if (result.isEmpty()) {
            result.add(staticInfo);
        }
        return result;
    }

    public List<HashMap<String, String>> assignPharmacistToPharmacy(String pharmacistLogin, String pharmacyAddress) {
        List<HashMap<String, String>> result = new ArrayList<>();
        HashMap<String, String> staticInfo = new HashMap<>(message.getDefaultErrorMessageAsHashMap());

        String findPharmacistQuery = "SELECT id FROM users WHERE login = ? AND user_type = 'pharmacist'";
        String findPharmacyQuery = "SELECT pharmacyid FROM pharmacy WHERE address = ?";
        String insertPharmacyWorkerQuery = "INSERT INTO pharmacy_worker (pharmacyid, pharmacistid) VALUES (?, ?)";

        try (
                PreparedStatement findPharmacistStmt = conn.prepareStatement(findPharmacistQuery);
                PreparedStatement findPharmacyStmt = conn.prepareStatement(findPharmacyQuery);
                PreparedStatement insertStmt = conn.prepareStatement(insertPharmacyWorkerQuery)
        ) {
            findPharmacistStmt.setString(1, pharmacistLogin);
            ResultSet pharmacistRs = findPharmacistStmt.executeQuery();
            if (!pharmacistRs.next()) {
                throw new IllegalArgumentException("Pharmacist with login '" + pharmacistLogin + "' does not exist.");
            }
            int pharmacistId = pharmacistRs.getInt("id");

            findPharmacyStmt.setString(1, pharmacyAddress);
            ResultSet pharmacyRs = findPharmacyStmt.executeQuery();
            if (!pharmacyRs.next()) {
                throw new IllegalArgumentException("Pharmacy with address '" + pharmacyAddress + "' does not exist.");
            }
            int pharmacyId = pharmacyRs.getInt("pharmacyid");

            insertStmt.setInt(1, pharmacyId);
            insertStmt.setInt(2, pharmacistId);
            insertStmt.executeUpdate();
        } catch (SQLException e) {
            errorHandler.handleSQLException(e, staticInfo, message);
        }
        result.add(staticInfo);
        return result;
    }

}
