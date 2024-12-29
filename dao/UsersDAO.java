package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import error.handlers.ErrorHandler;
import utils.Message;

public class UsersDAO {
    private final Connection conn;
    private final Message message = new Message();
    private final ErrorHandler errorHandler = new ErrorHandler();

    public UsersDAO(Connection conn) {
        this.conn = conn;
    }

    public List<HashMap<String, String>> createUser(String login, String password, String userType, String name, String surname) {
        String query = "INSERT INTO users (login, password, user_type, name, surname) VALUES (?, ?, ?, ?, ?)";
        List<HashMap<String, String>> result = new ArrayList<>();

        HashMap<String, String> staticInfo1 = new HashMap<>(message.getDefaultErrorMessageAsHashMap());

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, login);
            stmt.setString(2, password);
            stmt.setString(3, userType);
            stmt.setString(4, name);
            stmt.setString(5, surname);
            stmt.executeUpdate();
        } catch (SQLException e) {
            staticInfo1 = errorHandler.handleSQLException(e, staticInfo1, message);
        }
        result.add(staticInfo1);
        return result;
    }

    public List<HashMap<String, String>> updateUserPassword(String login, String newPassword) {
        List<HashMap<String, String>> infoList = new ArrayList<>();

        HashMap<String, String> staticInfo1 = new HashMap<>(message.getDefaultErrorMessageAsHashMap());

        String query = "UPDATE users SET password = ? WHERE login = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, newPassword);
            stmt.setString(2, login);

            HashMap<String, String> staticInfo2 = new HashMap<>();
            if (stmt.executeUpdate() > 0) {
                staticInfo2.put("success", "true");
            } else {
                staticInfo1.replace(message.getHashIdStatus(), "error");
                staticInfo1.replace(message.getHashIdUserFriendlyError(), "User password was not updated");
            }
            infoList.add(staticInfo2);
        } catch (SQLException e) {
            staticInfo1 = errorHandler.handleSQLException(e, staticInfo1, message);
        }
        infoList.add(staticInfo1);
        return infoList;
    }

    public List<HashMap<String, String>> deleteUser(String login) {
        List<HashMap<String, String>> infoList = new ArrayList<>();

        HashMap<String, String> staticInfo1 = new HashMap<>(message.getDefaultErrorMessageAsHashMap());

        String query = "DELETE FROM users WHERE login = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, login);

            HashMap<String, String> staticInfo2 = new HashMap<>();
            if (stmt.executeUpdate() > 0) {
                staticInfo2.put("success", "true");
            } else {
                staticInfo1.replace(message.getHashIdStatus(), "error");
                staticInfo1.replace(message.getHashIdUserFriendlyError(), "User was not deleted");
            }
            infoList.add(staticInfo2);
        } catch (SQLException e) {
            staticInfo1 = errorHandler.handleSQLException(e, staticInfo1, message);
        }
        infoList.add(staticInfo1);
        return infoList;
    }

    public List<HashMap<String, String>> isUserValid(String login, String password) {
        List<HashMap<String, String>> userList = new ArrayList<>();

        HashMap<String, String> staticInfo1 = new HashMap<>(message.getDefaultErrorMessageAsHashMap());

        String query = "SELECT login, password FROM users WHERE login = ? AND password = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, login);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                HashMap<String, String> user = new HashMap<>();
                if (rs.next()) {
                    user.put("exists", "true");
                } else {
                    staticInfo1.replace(message.getHashIdStatus(), "error");
                    staticInfo1.replace(message.getHashIdException(), "not nown exception");
                    staticInfo1.replace(message.getHashIdErrorMessage(), "There is no user in this database");
                    staticInfo1.replace(message.getHashIdUserFriendlyError(), "User does not exist");
                }
                userList.add(user);
            }
        } catch (SQLException e) {
            staticInfo1 = errorHandler.handleSQLException(e, staticInfo1, message);
        }
        userList.add(staticInfo1);
        return userList;
    }

    public List<HashMap<String, String>> getUser(String login, String password) {
        List<HashMap<String, String>> userList = new ArrayList<>();

        HashMap<String, String> staticInfo1 = new HashMap<>(message.getDefaultErrorMessageAsHashMap());

        String query = "SELECT user_type, name, surname FROM users WHERE login = ? AND password = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, login);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                HashMap<String, String> user = new HashMap<>();
                while (rs.next()) {
                    user.put("userType", rs.getString("user_type"));
                    user.put("name", rs.getString("name"));
                    user.put("surname", rs.getString("surname"));
                }
                if (user.isEmpty()) {
                    staticInfo1.replace(message.getHashIdStatus(), "error");
                    staticInfo1.replace(message.getHashIdUserFriendlyError(), "There is no user for that password and login");
                } else {
                    userList.add(user);
                }
            }
        } catch (SQLException e) {
            staticInfo1 = errorHandler.handleSQLException(e, staticInfo1, message);
        }
        userList.add(staticInfo1);
        return userList;
    }

    public List<HashMap<String, String>> getUserByLogin(String login) {
        List<HashMap<String, String>> userList = new ArrayList<>();

        HashMap<String, String> staticInfo1 = new HashMap<>(message.getDefaultErrorMessageAsHashMap());
        userList.add(staticInfo1);

        String query = "SELECT id, login, user_type, name, surname FROM users WHERE login = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, login);

            try (ResultSet rs = stmt.executeQuery()) {
                HashMap<String, String> user = new HashMap<>();
                while (rs.next()) {
                    user.put("id", rs.getString("id"));
                    user.put("login", rs.getString("login"));
                    user.put("userType", rs.getString("user_type"));
                    user.put("name", rs.getString("name"));
                    user.put("surname", rs.getString("surname"));
                }
                if (user.isEmpty()) {
                    staticInfo1.replace(message.getHashIdStatus(), "error");
                    staticInfo1.replace(message.getHashIdUserFriendlyError(), "There is no user for the given login");
                    userList.set(0, staticInfo1);
                } else {
                    userList.add(user);
                }
            }
        } catch (SQLException e) {
            staticInfo1 = errorHandler.handleSQLException(e, staticInfo1, message);
            userList.set(0, staticInfo1);
        }

        return userList;
    }


}