import DAO.UsersDAO;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class Main {

    public static void main(String[] args) {
        Engine myEngine = new Engine();
        myEngine.start();
        Connection newConnection = myEngine.returnConnection();
        UsersDAO newUsersDAO = new UsersDAO(newConnection);
        List<HashMap<String, String>> testList = new ArrayList<>();
        testList = newUsersDAO.createUser("testUser1", "password1", "doctor", "testName", "TestSurname");
        System.out.println(testList);
        boolean testBoolean = newUsersDAO.isUserValid("testUser1", "password1");
        System.out.println(testBoolean);
        System.out.println(newUsersDAO.getUser("testUser1", "password1"));


    }
}
