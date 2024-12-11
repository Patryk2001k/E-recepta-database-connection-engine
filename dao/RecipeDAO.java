package dao;

import utils.Message;
import error.handlers.ErrorHandler;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RecipeDAO {
    private final Connection conn;
    private final Message message = new Message();
    private final ErrorHandler errorHandler = new ErrorHandler();

    public RecipeDAO(Connection conn) {
        this.conn = conn;
    }

    public List<HashMap<String, String>> insertRecipe(String doctorLogin, String patientLogin, Date date) {
        List<HashMap<String, String>> result = new ArrayList<>();
        HashMap<String, String> staticInfo = new HashMap<>(message.getDefaultErrorMessageAsHashMap());

        try {
            // Pobranie doctorId
            Integer doctorId = getUserIdByLogin(doctorLogin, "doctor");
            if (doctorId == null) {
                staticInfo.replace(message.getHashIdStatus(), "error");
                staticInfo.replace(message.getHashIdUserFriendlyError(), "doctor not found");
                result.add(staticInfo);
                return result;
            }

            // Pobranie patientId
            Integer patientId = getUserIdByLogin(patientLogin, "patient");
            if (patientId == null) {
                staticInfo.replace(message.getHashIdStatus(), "error");
                staticInfo.replace(message.getHashIdUserFriendlyError(), "patient not found");
                result.add(staticInfo);
                return result;
            }

            // Wstawienie recepty
            String query = """
                INSERT INTO recipe (date, doctorid, patientid) 
                VALUES (?, ?, ?)
            """;
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setDate(1, date);
                stmt.setInt(2, doctorId);
                stmt.setInt(3, patientId);

                int rowsInserted = stmt.executeUpdate();
                if (rowsInserted > 0) {
                    staticInfo.replace(message.getHashIdStatus(), "success");
                    staticInfo.replace(message.getHashIdUserFriendlyError(), "Recipe inserted successfully");
                } else {
                    staticInfo.replace(message.getHashIdStatus(), "error");
                    staticInfo.replace(message.getHashIdUserFriendlyError(), "Failed to insert recipe");
                }
            }
        } catch (SQLException e) {
            staticInfo = errorHandler.handleSQLException(e, staticInfo, message);
        }

        result.add(staticInfo);
        return result;
    }

    public List<HashMap<String, String>> getRecipes(String login, String role) {
        List<HashMap<String, String>> result = new ArrayList<>();
        HashMap<String, String> staticInfo = new HashMap<>(message.getDefaultErrorMessageAsHashMap());

        try {
            // Pobranie userId
            Integer userId = getUserIdByLogin(login, role);
            if (userId == null) {
                staticInfo.replace(message.getHashIdStatus(), "error");
                staticInfo.replace(message.getHashIdUserFriendlyError(), role + " not found");
                result.add(staticInfo);
                return result;
            }
            String query = """
                    """;
            if(role.equals("doctor")){
                query = """
                    SELECT r.recipeid, r.date, u.name AS patient_name, u.surname AS patient_surname 
                    FROM recipe r 
                    JOIN users u ON r.patientid = u.id 
                    WHERE r.doctorid = ?
                """;
            }
            if(role.equals("patient")){
                query = """
                    SELECT r.recipeid, r.date, u.name AS doctor_name, u.surname AS doctor_surname 
                    FROM recipe r 
                    JOIN users u ON r.doctorid = u.id 
                    WHERE r.patientid = ?
                """;
            }

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, userId);

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        HashMap<String, String> recipeInfo = new HashMap<>();
                        recipeInfo.put("recipeId", String.valueOf(rs.getInt("recipeid")));
                        recipeInfo.put("date", String.valueOf(rs.getDate("date")));
                        if (role.equals("doctor")) {
                            recipeInfo.put("patientName", rs.getString("patient_name"));
                            recipeInfo.put("patientSurname", rs.getString("patient_surname"));
                        } else {
                            recipeInfo.put("doctorName", rs.getString("doctor_name"));
                            recipeInfo.put("doctorSurname", rs.getString("doctor_surname"));
                        }
                        result.add(recipeInfo);
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

    private Integer getUserIdByLogin(String login, String userType) {
        String query = """
            SELECT id FROM users
            WHERE login = ? AND user_type = ?
        """;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, login);
            stmt.setString(2, userType);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            errorHandler.handleSQLException(e, message.getDefaultErrorMessageAsHashMap(), message);
        }
        return null;
    }
}