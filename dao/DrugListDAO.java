package dao;

import utils.Message;
import error.handlers.ErrorHandler;

import javax.print.event.PrintJobAttributeListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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


    public List<HashMap<String, String>> insertDrugToList(String pharmacistLogin, String drugName, int amount, String patientLogin, String fulfillMethod) {
        List<HashMap<String, String>> result = new ArrayList<>();
        HashMap<String, String> staticInfo = new HashMap<>(message.getDefaultErrorMessageAsHashMap());

        String findPharmacistIdQuery = "SELECT id FROM users WHERE login = ? AND user_type = 'pharmacist'";
        String findDrugIdQuery = "SELECT id FROM medicines WHERE name = ?";
        String findRecipeIdQuery = "SELECT r.recipeid FROM recipe r JOIN users u ON r.patientid = u.id WHERE u.login = ? AND u.user_type = 'patient'";
        String insertDrugQuery = """
        INSERT INTO drug_list (recipeid, drugid, amount, pharmacistid, fulfill_method) 
        VALUES (?, ?, ?, ?, ?)
    """;

        try (
                PreparedStatement findPharmacistStmt = conn.prepareStatement(findPharmacistIdQuery);
                PreparedStatement findDrugStmt = conn.prepareStatement(findDrugIdQuery);
                PreparedStatement findRecipeStmt = conn.prepareStatement(findRecipeIdQuery);
                PreparedStatement insertStmt = conn.prepareStatement(insertDrugQuery)
        ) {
            findPharmacistStmt.setString(1, pharmacistLogin);
            try (ResultSet rs = findPharmacistStmt.executeQuery()) {
                if (!rs.next()) {
                    staticInfo.replace(message.getHashIdStatus(), "error");
                    staticInfo.replace(message.getHashIdUserFriendlyError(), "Pharmacist not found");
                    result.add(staticInfo);
                    return result;
                }
                int pharmacistId = rs.getInt("id");

                findDrugStmt.setString(1, drugName);
                try (ResultSet rsDrug = findDrugStmt.executeQuery()) {
                    if (!rsDrug.next()) {
                        staticInfo.replace(message.getHashIdStatus(), "error");
                        staticInfo.replace(message.getHashIdUserFriendlyError(), "Medicine not found");
                        result.add(staticInfo);
                        return result;
                    }
                    int drugId = rsDrug.getInt("id");

                    findRecipeStmt.setString(1, patientLogin);
                    try (ResultSet rsRecipe = findRecipeStmt.executeQuery()) {
                        if (!rsRecipe.next()) {
                            staticInfo.replace(message.getHashIdStatus(), "error");
                            staticInfo.replace(message.getHashIdUserFriendlyError(), "Recipe not found");
                            result.add(staticInfo);
                            return result;
                        }
                        int recipeId = rsRecipe.getInt("recipeid");

                        insertStmt.setInt(1, recipeId);
                        insertStmt.setInt(2, drugId);
                        insertStmt.setInt(3, amount);
                        insertStmt.setInt(4, pharmacistId);
                        insertStmt.setString(5, fulfillMethod);

                        int rowsInserted = insertStmt.executeUpdate();
                        if (rowsInserted > 0) {
                            staticInfo.replace(message.getHashIdStatus(), "success");
                            staticInfo.replace(message.getHashIdUserFriendlyError(), "Drug added to list successfully");
                        } else {
                            staticInfo.replace(message.getHashIdStatus(), "error");
                            staticInfo.replace(message.getHashIdUserFriendlyError(), "Failed to add drug to list");
                        }
                    }
                }
            }
        } catch (SQLException e) {
            staticInfo = errorHandler.handleSQLException(e, staticInfo, message);
        }

        result.add(staticInfo);
        return result;
    }

}
