import DAO.UsersDAO;

import java.sql.Connection;

public class Main {

    public static void main(String[] args) {
        Engine myEngine = new Engine();
        myEngine.start();
        Connection newConnection = myEngine.returnConnection();
        UsersDAO newUsersDAO = new UsersDAO(newConnection);
        newUsersDAO.createUser("testUser1", "password1", "doctor", "testName", "TestSurname");
        boolean testBoolean = newUsersDAO.isUserValid("testUser1", "password1");
        System.out.print(testBoolean);
    }
}
