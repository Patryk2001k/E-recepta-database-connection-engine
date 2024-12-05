package DAO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

import ErrorHandlers.ErrorHandler;
import ErrorHandlers.SQLErrorTranslator;
import Utils.Message;

public class UsersDAO {
    private final Connection conn;
    private final Message message = new Message();
    private final ErrorHandler errorHandler = new ErrorHandler();
    private final SQLErrorTranslator sqlErrorTranslator = new SQLErrorTranslator();

    public UsersDAO(Connection conn) {
        this.conn = conn;
    }

    public List<HashMap<String, String>> createUser(String login, String password, String userType, String name, String surname) {
        String query = "INSERT INTO users (login, password, user_type, name, surname) VALUES (?, ?, ?, ?, ?)";
        List<HashMap<String, String>> result = new ArrayList<>();

        HashMap<String, String> staticInfo1;
        staticInfo1 = message.getDefaultErrorMessageAsHashMap();
        result.add(staticInfo1);

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, login);
            stmt.setString(2, password);
            stmt.setString(3, userType);
            stmt.setString(4, name);
            stmt.setString(5, surname);
            stmt.executeUpdate();
            return result;

        } catch (SQLException e) {
            System.out.println("Check");
            String errorMessage = errorHandler.returnStackStraceAsString(e);
            staticInfo1.replace(message.getHashIdStatus(), "error");
            staticInfo1.replace(message.getHashIdException(), e.getSQLState());
            staticInfo1.replace(message.getHashIdUserFriendlyError(), SQLErrorTranslator.translate(e.getSQLState()));
            staticInfo1.replace(message.getHashIdErrorMessage(), errorMessage);
            return result;
        }

    }



    public List<HashMap<String, String>> updateUserPassword(String login, String newPassword) {
        List<HashMap<String, String>> infoList = new ArrayList<>();

        HashMap<String, String> staticInfo1;
        staticInfo1 = message.getDefaultErrorMessageAsHashMap();
        infoList.add(staticInfo1);

        String query = "UPDATE users SET password = ? WHERE login = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, newPassword);
            stmt.setString(2, login);
            HashMap<String, String> staticInfo2 = new HashMap<>();
            if(stmt.executeUpdate() > 0){
                staticInfo2.put("success", "true");
            }
            else{
                staticInfo1.replace(message.getHashIdStatus(), "error");
                staticInfo1.replace(message.getHashIdException(), "");
                staticInfo1.replace(message.getHashIdUserFriendlyError(), "User password was not updated");
                staticInfo1.replace(message.getHashIdErrorMessage(), "");
            }
            infoList.add(staticInfo2);
            return infoList;
        } catch (SQLException e) {
            String errorMessage = errorHandler.returnStackStraceAsString(e);
            staticInfo1.replace(message.getHashIdStatus(), "error");
            staticInfo1.replace(message.getHashIdException(), e.getSQLState());
            staticInfo1.replace(message.getHashIdUserFriendlyError(), SQLErrorTranslator.translate(e.getSQLState()));
            staticInfo1.replace(message.getHashIdErrorMessage(), errorMessage);
            return infoList;
        }
    }

    public List<HashMap<String, String>> deleteUser(String login) {
        List<HashMap<String, String>> infoList = new ArrayList<>();

        HashMap<String, String> staticInfo1;
        staticInfo1 = message.getDefaultErrorMessageAsHashMap();
        infoList.add(staticInfo1);

        String query = "DELETE FROM users WHERE login = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, login);
            HashMap<String, String> staticInfo2 = new HashMap<>();
            if(stmt.executeUpdate() > 0){
                staticInfo2.put("success", "true");
            }
            else{
                staticInfo1.replace(message.getHashIdStatus(), "error");
                staticInfo1.replace(message.getHashIdException(), "");
                staticInfo1.replace(message.getHashIdUserFriendlyError(), "User was not deleted");
                staticInfo1.replace(message.getHashIdErrorMessage(), "");
            }
            infoList.add(staticInfo2);
            return infoList;
        } catch (SQLException e) {
            String errorMessage = errorHandler.returnStackStraceAsString(e);
            staticInfo1.replace(message.getHashIdStatus(), "error");
            staticInfo1.replace(message.getHashIdException(), e.getSQLState());
            staticInfo1.replace(message.getHashIdUserFriendlyError(), SQLErrorTranslator.translate(e.getSQLState()));
            staticInfo1.replace(message.getHashIdErrorMessage(), errorMessage);
            return infoList;
        }
    }

    public List<HashMap<String, String>> isUserValid(String login, String password) {
        List<HashMap<String, String>> userList = new ArrayList<>();

        HashMap<String, String> staticInfo1;
        staticInfo1 = message.getDefaultErrorMessageAsHashMap();
        userList.add(staticInfo1);

        String query = "SELECT login, password FROM users WHERE login = ? AND password = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, login);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                HashMap<String, String> user = new HashMap<>();
                if(rs.next()){
                    user.put("exists", "true");
                }
                else{
                    staticInfo1.replace(message.getHashIdStatus(), "error");
                    staticInfo1.replace(message.getHashIdException(), "");
                    staticInfo1.replace(message.getHashIdUserFriendlyError(), "User does not exist");
                    staticInfo1.replace(message.getHashIdErrorMessage(), "");
                }
                userList.add(user);
                return userList;
            }catch (SQLException e){
                String errorMessage = errorHandler.returnStackStraceAsString(e);
                staticInfo1.replace(message.getHashIdStatus(), "error");
                staticInfo1.replace(message.getHashIdException(), e.getSQLState());
                staticInfo1.replace(message.getHashIdUserFriendlyError(), SQLErrorTranslator.translate(e.getSQLState()));
                staticInfo1.replace(message.getHashIdErrorMessage(), errorMessage);
                return userList;
            }
        } catch (SQLException e) {
            String errorMessage = errorHandler.returnStackStraceAsString(e);
            staticInfo1.replace(message.getHashIdStatus(), "error");
            staticInfo1.replace(message.getHashIdException(), e.getSQLState());
            staticInfo1.replace(message.getHashIdUserFriendlyError(), SQLErrorTranslator.translate(e.getSQLState()));
            staticInfo1.replace(message.getHashIdErrorMessage(), errorMessage);
            return userList;
        }
    }

    public List<HashMap<String, String>> getUser(String login, String password) {
        List<HashMap<String, String>> userList = new ArrayList<>();

        HashMap<String, String> staticInfo1;
        staticInfo1 = message.getDefaultErrorMessageAsHashMap();
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
                if(rs.next()){
                    return userList;
                }
                else{
                    staticInfo1.replace(message.getHashIdStatus(), "error");
                    staticInfo1.replace(message.getHashIdException(), "");
                    staticInfo1.replace(message.getHashIdUserFriendlyError(), "There is no user for that password and login");
                    staticInfo1.replace(message.getHashIdErrorMessage(), "");
                }
            } catch (SQLException e){
                String errorMessage = errorHandler.returnStackStraceAsString(e);
                staticInfo1.replace(message.getHashIdStatus(), "error");
                staticInfo1.replace(message.getHashIdException(), e.getSQLState());
                staticInfo1.replace(message.getHashIdUserFriendlyError(), SQLErrorTranslator.translate(e.getSQLState()));
                staticInfo1.replace(message.getHashIdErrorMessage(), errorMessage);
            }
        } catch (SQLException e) {
            String errorMessage = errorHandler.returnStackStraceAsString(e);
            staticInfo1.replace(message.getHashIdStatus(), "error");
            staticInfo1.replace(message.getHashIdException(), e.getSQLState());
            staticInfo1.replace(message.getHashIdUserFriendlyError(), SQLErrorTranslator.translate(e.getSQLState()));
            staticInfo1.replace(message.getHashIdErrorMessage(), errorMessage);
        }

        return userList.isEmpty() ? null : userList;
    }


}
