package DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class UsersDAO {
    private final Connection conn;

    public UsersDAO(Connection conn) {
        this.conn = conn;
    }

    public List<HashMap<String, String>> createUser(String login, String password, String userType, String name, String surname) {
        String query = "INSERT INTO users (login, password, user_type, name, surname) VALUES (?, ?, ?, ?, ?)";
        List<HashMap<String, String>> result = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, login);
            stmt.setString(2, password);
            stmt.setString(3, userType);
            stmt.setString(4, name);
            stmt.setString(5, surname);

            int rowsAffected = stmt.executeUpdate();

            HashMap<String, String> successMap = new HashMap<>();
            successMap.put("status", "success");
            successMap.put("message", "User added successfully.");
            successMap.put("rowsAffected", String.valueOf(rowsAffected));
            result.add(successMap);

        } catch (SQLException e) {
            HashMap<String, String> errorMap = new HashMap<>();
            if (e.getSQLState().equals("23505")) {
                errorMap.put("status", "error");
                errorMap.put("message", "User with this login already exists.");
            } else {
                errorMap.put("status", "error");
                errorMap.put("message", "An unexpected error occurred: " + e.getMessage());
            }
            result.add(errorMap);
        }

        return result;
    }



    public boolean updateUserPassword(String login, String newPassword) {
        String query = "UPDATE users SET password = ? WHERE login = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, newPassword);
            stmt.setString(2, login);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace(System.out);
            return false;
        }
    }

    public boolean deleteUser(String login) {
        String query = "DELETE FROM users WHERE login = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, login);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace(System.out);
            return false;
        }
    }

    public boolean isUserValid(String login, String password) {
        String query = "SELECT login, password FROM users WHERE login = ? AND password = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, login);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace(System.out);
            return false;
        }
    }

    public List<HashMap<String, String>> getUser(String login, String password) {
        List<HashMap<String, String>> userList = new ArrayList<>();

        HashMap<String, String> staticInfo1 = new HashMap<>();
        staticInfo1.put("status", "Succes");
        staticInfo1.put("exception", "noException");
        staticInfo1.put("userFriendlyError", "there is no error");
        userList.add(staticInfo1);

        String query = "SELECT user_type, name, surname FROM users WHERE login = ? AND password = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, login);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    HashMap<String, String> user = new HashMap<>();
                    user.put("userType", rs.getString("user_type"));
                    user.put("name", rs.getString("name"));
                    user.put("surname", rs.getString("surname"));
                    userList.add(user);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }

        return userList.isEmpty() ? null : userList;
    }


}
