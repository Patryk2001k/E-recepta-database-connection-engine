package dao;

import utils.Message;
import error.handlers.ErrorHandler;

import javax.print.DocFlavor;
import javax.print.event.PrintJobAttributeListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class DrugListDAO {
    private final Connection conn;
    private final Message message = new Message();
    private final ErrorHandler errorHandler = new ErrorHandler();

    public DrugListDAO(Connection conn) {
        this.conn = conn;
    }

    public List<HashMap<String, String>> getDrugsByRecipeId(String recipeId) {
        List<HashMap<String, String>> result = new ArrayList<>();
        HashMap<String, String> staticInfo = new HashMap<>(message.getDefaultErrorMessageAsHashMap());
        result.add(staticInfo);
        try {
            String query = """
            SELECT m.name AS drug_name, dl.amount, dl.fulfill_method, u.login AS pharmacist_login 
            FROM drug_list dl
            JOIN medicines m ON dl.drugid = m.drugid
            LEFT JOIN users u ON dl.pharmacistid = u.id
            WHERE dl.recipeid = ?
        """;

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, Integer.parseInt(recipeId));

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        HashMap<String, String> drugInfo = new HashMap<>();
                        drugInfo.put("drugName", rs.getString("drug_name"));
                        drugInfo.put("amount", String.valueOf(rs.getInt("amount")));
                        drugInfo.put("fulfillMethod", rs.getString("fulfill_method"));
                        drugInfo.put("pharmacistLogin", rs.getString("pharmacist_login"));
                        result.set(0, drugInfo);
                    }
                }

                if (result.isEmpty()) {
                    staticInfo.replace(message.getHashIdStatus(), "error");
                    staticInfo.replace(message.getHashIdUserFriendlyError(), "No drugs found for the given recipe ID");
                    result.set(0, staticInfo);
                } else {
                    staticInfo.replace(message.getHashIdStatus(), "success");
                }
            }
        } catch (SQLException e) {
            staticInfo = errorHandler.handleSQLException(e, staticInfo, message);
            result.set(0, staticInfo);
        }
        return result;
    }

    public List<HashMap<String, String>> updateDrugList(
            String recipeId, String drugName, String pharmacistLogin, String fulfillMethod) {

        List<HashMap<String, String>> result = new ArrayList<>();
        HashMap<String, String> staticInfo = new HashMap<>(message.getDefaultErrorMessageAsHashMap());
        result.add(staticInfo);
        MedicinesDAO medicinesDAO = new MedicinesDAO(this.conn);
        UsersDAO usersDAO = new UsersDAO(this.conn);

        try {
            List<HashMap<String, String>> pharmacistData = usersDAO.getUserByLogin(pharmacistLogin);
            if (pharmacistData.isEmpty()) {
                staticInfo.replace(message.getHashIdStatus(), "error");
                staticInfo.replace(message.getHashIdUserFriendlyError(), "Nie znaleziono takiego farmaceuty");
                result.set(0, staticInfo);
                return result;
            }

            String pharmacistId = pharmacistData.get(1).get("id");

            List<HashMap<String, String>> medicineData = medicinesDAO.getMedicine(drugName);
            if (medicineData.isEmpty()) {
                staticInfo.replace(message.getHashIdStatus(), "error");
                staticInfo.replace(message.getHashIdUserFriendlyError(), "Nie znaleziono takiego leku");
                result.set(0, staticInfo);
                return result;
            }
            String drugId = medicineData.get(1).get("drugId");

            String query = """
            UPDATE drug_list
            SET pharmacistid = ?, fulfill_method = ?
            WHERE recipeid = ? AND drugid = ?
        """;

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, Integer.parseInt(pharmacistId));
                stmt.setString(2, fulfillMethod);
                stmt.setInt(3, Integer.parseInt(recipeId));
                stmt.setInt(4, Integer.parseInt(drugId));

                int rowsUpdated = stmt.executeUpdate();

                if (rowsUpdated > 0) {
                    staticInfo.replace(message.getHashIdStatus(), "success");
                    staticInfo.replace(message.getHashIdUserFriendlyError(), "Entries updated successfully");
                } else {
                    staticInfo.replace(message.getHashIdStatus(), "error");
                    staticInfo.replace(message.getHashIdUserFriendlyError(),
                            "No entries found for the given recipeId and drugName");
                }
                result.set(0, staticInfo);
            }
        } catch (SQLException e) {
            staticInfo = errorHandler.handleSQLException(e, staticInfo, message);
            result.set(0, staticInfo);
        }

        return result;
    }





    private Optional<Integer> getPharmacistId(String pharmacistLogin) {
        String query = "SELECT id FROM users WHERE login = ? AND user_type = 'pharmacist';";
        HashMap<String, String> staticInfo = new HashMap<>(message.getDefaultErrorMessageAsHashMap());
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, pharmacistLogin);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(rs.getInt("id"));
                }
            }
        } catch (SQLException e) {
            errorHandler.handleSQLException(e, staticInfo, message);
        }
        return Optional.empty();
    }

    private Optional<Integer> getDrugId(String drugName) {
        String query = "SELECT drugid FROM medicines WHERE name = ?;";
        HashMap<String, String> staticInfo = new HashMap<>(message.getDefaultErrorMessageAsHashMap());
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, drugName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(rs.getInt("drugid"));
                }
            }
        } catch (SQLException e) {
            errorHandler.handleSQLException(e, staticInfo, message);
        }
        return Optional.empty();
    }

    private Optional<Integer> getRecipeId(String patientLogin) {
        String query = "SELECT r.recipeid FROM recipe r JOIN users u ON r.patientid = u.id WHERE u.login = ? AND u.user_type = 'patient';";
        HashMap<String, String> staticInfo = new HashMap<>(message.getDefaultErrorMessageAsHashMap());
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, patientLogin);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(rs.getInt("recipeid"));
                }
            }
        } catch (SQLException e) {
            errorHandler.handleSQLException(e, staticInfo, message);
        }
        return Optional.empty();
    }

    /*
    * Zrobić dwie metody:
    *
    * W jednej dostaje to co poniżej a w drugiej:
    * - String drugName
    * - int amount
    * - String patientLogin
    *
    * Zrobić metodę na update danej listy leków
    * */


    public List<HashMap<String, String>> insertDrugToList(String prescriptionId, String drugName, int amount) {
        List<HashMap<String, String>> result = new ArrayList<>();
        HashMap<String, String> staticInfo = new HashMap<>(message.getDefaultErrorMessageAsHashMap());

        Optional<Integer> drugIdOpt = getDrugId(drugName);
        if (drugIdOpt.isEmpty()) {
            staticInfo.replace(message.getHashIdStatus(), "error");
            staticInfo.replace(message.getHashIdUserFriendlyError(), "Medicine not found");
            result.add(staticInfo);
            return result;
        }

        String insertDrugQuery = """
        INSERT INTO drug_list (recipeid, drugid, amount, pharmacistid, fulfill_method)
        VALUES (?, ?, ?, NULL, 'manual');
    """;

        try (PreparedStatement insertStmt = conn.prepareStatement(insertDrugQuery)) {
            insertStmt.setInt(1, Integer.parseInt(prescriptionId));
            insertStmt.setInt(2, drugIdOpt.get());
            insertStmt.setInt(3, amount);

            int rowsInserted = insertStmt.executeUpdate();
            if (rowsInserted > 0) {
                staticInfo.replace(message.getHashIdStatus(), "success");
                staticInfo.replace(message.getHashIdUserFriendlyError(), "Drug added to list successfully");
            } else {
                staticInfo.replace(message.getHashIdStatus(), "error");
                staticInfo.replace(message.getHashIdUserFriendlyError(), "Failed to add drug to list");
            }
        } catch (SQLException e) {
            staticInfo = errorHandler.handleSQLException(e, staticInfo, message);
        }

        result.add(staticInfo);
        return result;
    }


}
