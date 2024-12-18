package dao;

import utils.Message;
import error.handlers.ErrorHandler;

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

    public List<HashMap<String, String>> getDrugsByPharmacistLogin(String pharmacistLogin) {
        List<HashMap<String, String>> result = new ArrayList<>();
        HashMap<String, String> staticInfo = new HashMap<>(message.getDefaultErrorMessageAsHashMap());

        try {
            String queryGetId = "SELECT id FROM users WHERE login = ?";
            Integer pharmacistId = null;

            try (PreparedStatement stmtGetId = conn.prepareStatement(queryGetId)) {
                stmtGetId.setString(1, pharmacistLogin);
                try (ResultSet rsGetId = stmtGetId.executeQuery()) {
                    if (rsGetId.next()) {
                        pharmacistId = rsGetId.getInt("id");
                    }
                }
            }

            if (pharmacistId == null) {
                staticInfo.replace(message.getHashIdStatus(), "error");
                staticInfo.replace(message.getHashIdUserFriendlyError(), "Pharmacist not found");
                result.add(staticInfo);
                return result;
            }

            // Pobranie listy leków przypisanych do farmaceuty
            String query = """
            SELECT dl.recipeid, m.name AS drug_name, dl.amount, dl.fulfill_method 
            FROM drug_list dl
            JOIN medicines m ON dl.drugid = m.drugid
            WHERE dl.pharmacistid = ?
        """;

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, pharmacistId);

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        HashMap<String, String> drugInfo = new HashMap<>();
                        drugInfo.put("recipeId", String.valueOf(rs.getInt("recipeid")));
                        drugInfo.put("drugName", rs.getString("drug_name"));
                        drugInfo.put("amount", String.valueOf(rs.getInt("amount")));
                        drugInfo.put("fulfillMethod", rs.getString("fulfill_method"));
                        result.add(drugInfo);
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

    public List<HashMap<String, String>> insertDrugToList(String pharmacistLogin, String drugName, int amount, String patientLogin, String fulfillMethod) {
        List<HashMap<String, String>> result = new ArrayList<>();
        HashMap<String, String> staticInfo = new HashMap<>(message.getDefaultErrorMessageAsHashMap());

        Optional<Integer> pharmacistIdOpt = getPharmacistId(pharmacistLogin);
        if (pharmacistIdOpt.isEmpty()) {
            staticInfo.replace(message.getHashIdStatus(), "error");
            staticInfo.replace(message.getHashIdUserFriendlyError(), "Pharmacist not found");
            result.add(staticInfo);
            return result;
        }

        Optional<Integer> drugIdOpt = getDrugId(drugName);
        if (drugIdOpt.isEmpty()) {
            staticInfo.replace(message.getHashIdStatus(), "error");
            staticInfo.replace(message.getHashIdUserFriendlyError(), "Medicine not found");
            result.add(staticInfo);
            return result;
        }

        Optional<Integer> recipeIdOpt = getRecipeId(patientLogin);
        if (recipeIdOpt.isEmpty()) {
            staticInfo.replace(message.getHashIdStatus(), "error");
            staticInfo.replace(message.getHashIdUserFriendlyError(), "Recipe not found");
            result.add(staticInfo);
            return result;
        }

        String insertDrugQuery = """
        INSERT INTO drug_list (recipeid, drugid, amount, pharmacistid, fulfill_method)
        VALUES (?, ?, ?, ?, ?);
    """;

        try (PreparedStatement insertStmt = conn.prepareStatement(insertDrugQuery)) {
            insertStmt.setInt(1, recipeIdOpt.get());
            insertStmt.setInt(2, drugIdOpt.get());
            insertStmt.setInt(3, amount);
            insertStmt.setInt(4, pharmacistIdOpt.get());
            insertStmt.setString(5, fulfillMethod);

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
