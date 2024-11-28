package DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsersDAO {
    private final Connection conn;

    public UsersDAO(Connection conn) {
        this.conn = conn;
    }

    public boolean createUser(String login, String password, String userType, String name, String surname) {
        String query = "INSERT INTO users (Login, Password, UserType, Name, Surname) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, login);
            stmt.setString(2, password);
            stmt.setString(3, userType);
            stmt.setString(4, name);
            stmt.setString(5, surname);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace(System.out);
            return false;
        }
    }

    public List<Integer> getAllUserIds() {
        String query = "SELECT Id FROM users";
        List<Integer> userIds = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                userIds.add(rs.getInt("Id"));
            }
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }
        return userIds;
    }

    public boolean updateUserPassword(String login, String newPassword) {
        String query = "UPDATE users SET Password = ? WHERE Login = ?";
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
        String query = "DELETE FROM users WHERE Login = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, login);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace(System.out);
            return false;
        }
    }

    public List<String> getAllLogins() {
        String query = "SELECT Login FROM users";
        List<String> logins = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                logins.add(rs.getString("login"));
            }
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }
        return logins;
    }

    public boolean isUserValid(String login, String password) {
        String query = "SELECT 1 FROM users WHERE Login = ? AND Password = ?";
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

}
