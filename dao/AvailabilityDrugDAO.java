package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import utils.Message;
import error.handlers.ErrorHandler;

public class AvailabilityDrugDAO {
    private final Connection conn;
    private final Message message = new Message();
    private final ErrorHandler errorHandler = new ErrorHandler();
    private final MedicinesDAO medicinesDAO;
    private final PharmacyDAO pharmacyDAO;

    public AvailabilityDrugDAO(Connection conn) {
        this.conn = conn;
        this.medicinesDAO = new MedicinesDAO(conn);
        this.pharmacyDAO = new PharmacyDAO(conn);
    }

    public List<HashMap<String, String>> getAvailabilityByMedicineName(String medicineName) {
        List<HashMap<String, String>> result = new ArrayList<>();
        HashMap<String, String> staticInfo = new HashMap<>(message.getDefaultErrorMessageAsHashMap());

        try {
            List<HashMap<String, String>> medicineResult = medicinesDAO.getMedicineIdByName(medicineName);
            String drugId = extractIdFromResult(medicineResult, "drugId");

            if (drugId == null) {
                staticInfo.replace(message.getHashIdStatus(), "error");
                staticInfo.replace(message.getHashIdUserFriendlyError(), "Medicine not found");
                result.add(staticInfo);
                return result;
            }

            String query = """
                SELECT ad.amount, p.address
                FROM availability_drug ad
                JOIN pharmacy p ON ad.pharmacyid = p.pharmacyid
                WHERE ad.drugid = ?
            """;

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, Integer.parseInt(drugId));
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        HashMap<String, String> availabilityInfo = new HashMap<>();
                        availabilityInfo.put("amount", String.valueOf(rs.getInt("amount")));
                        availabilityInfo.put("address", rs.getString("address"));
                        result.add(availabilityInfo);
                    }
                }
                staticInfo.replace("status", "success");
            }
        } catch (SQLException e) {
            staticInfo = errorHandler.handleSQLException(e, staticInfo, message);
        }

        if (result.isEmpty()) {
            result.add(staticInfo);
        }
        return result;
    }

    public List<HashMap<String, String>> getAvailabilityByPharmacyAddress(String pharmacyAddress) {
        List<HashMap<String, String>> result = new ArrayList<>();
        HashMap<String, String> staticInfo = new HashMap<>(message.getDefaultErrorMessageAsHashMap());

        try {
            List<HashMap<String, String>> pharmacyResult = pharmacyDAO.getPharmacyIdByAddress(pharmacyAddress);
            String pharmacyId = extractIdFromResult(pharmacyResult, "pharmacyId");

            if (pharmacyId == null) {
                staticInfo.replace(message.getHashIdStatus(), "error");
                staticInfo.replace(message.getHashIdUserFriendlyError(), "Pharmacy not found");
                result.add(staticInfo);
                return result;
            }

            String query = """
                SELECT ad.amount, m.name
                FROM availability_drug ad
                JOIN medicines m ON ad.drugid = m.drugid
                WHERE ad.pharmacyid = ?
            """;

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, Integer.parseInt(pharmacyId));
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        HashMap<String, String> availabilityInfo = new HashMap<>();
                        availabilityInfo.put("amount", String.valueOf(rs.getInt("amount")));
                        availabilityInfo.put("name", rs.getString("name"));
                        result.add(availabilityInfo);
                    }
                }
                staticInfo.replace("status", "success");
            }
        } catch (SQLException e) {
            staticInfo = errorHandler.handleSQLException(e, staticInfo, message);
        }

        if (result.isEmpty()) {
            result.add(staticInfo);
        }
        return result;
    }

    public List<HashMap<String, String>> insertAvailabilityDrug(String medicineName, String pharmacyAddress, int amount) {
        List<HashMap<String, String>> result = new ArrayList<>();
        HashMap<String, String> staticInfo = new HashMap<>(message.getDefaultErrorMessageAsHashMap());

        try {
            List<HashMap<String, String>> medicineResult = medicinesDAO.getMedicineIdByName(medicineName);
            String drugId = extractIdFromResult(medicineResult, "drugId");

            if (drugId == null) {
                staticInfo.replace(message.getHashIdStatus(), "error");
                staticInfo.replace(message.getHashIdUserFriendlyError(), "Medicine not found");
                result.add(staticInfo);
                return result;
            }

            List<HashMap<String, String>> pharmacyResult = pharmacyDAO.getPharmacyIdByAddress(pharmacyAddress);
            String pharmacyId = extractIdFromResult(pharmacyResult, "pharmacyId");

            if (pharmacyId == null) {
                staticInfo.replace(message.getHashIdStatus(), "error");
                staticInfo.replace(message.getHashIdUserFriendlyError(), "Pharmacy not found");
                result.add(staticInfo);
                return result;
            }

            String insertQuery = """
            INSERT INTO availability_drug (drugid, pharmacyid, amount) 
            VALUES (?, ?, ?)
        """;

            try (PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
                stmt.setInt(1, Integer.parseInt(drugId));
                stmt.setInt(2, Integer.parseInt(pharmacyId));
                stmt.setInt(3, amount);

                int rowsInserted = stmt.executeUpdate();
                if (rowsInserted > 0) {
                    staticInfo.replace(message.getHashIdStatus(), "success");
                    staticInfo.replace(message.getHashIdUserFriendlyError(), "Row inserted successfully");
                } else {
                    staticInfo.replace(message.getHashIdStatus(), "error");
                    staticInfo.replace(message.getHashIdUserFriendlyError(), "Failed to insert row");
                }
            }
        } catch (SQLException e) {
            staticInfo = errorHandler.handleSQLException(e, staticInfo, message);
        }

        result.add(staticInfo);
        return result;
    }


    private String extractIdFromResult(List<HashMap<String, String>> result, String key) {
        for (HashMap<String, String> map : result) {
            if (map.containsKey(key)) {
                return map.get(key);
            }
        }
        return null;
    }
}
