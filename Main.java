import dao.*;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.sql.Date;

public class Main {

    public static void main(String[] args) {

        /**/
        Engine myEngine = new Engine();
        myEngine.start();
        Connection newConnection = myEngine.returnConnection();
        UsersDAO newUsersDAO = new UsersDAO(newConnection);
        RecipeDAO newRecipeDAO = new RecipeDAO(newConnection);
        PharmacyWorkerDAO newPharmacyWorkerDAO = new PharmacyWorkerDAO(newConnection);
        PharmacyDAO newPharmacyDAO = new PharmacyDAO(newConnection);
        MedicinesDAO medicinesDAO = new MedicinesDAO(newConnection);
        DrugListDAO drugListDAO = new DrugListDAO(newConnection);
        AvailabilityDrugDAO availabilityDrugDAO = new AvailabilityDrugDAO(newConnection);
        List<HashMap<String, String>> testList = new ArrayList<>();
        List<HashMap<String, String>> testList2 = new ArrayList<>();

        testList = newUsersDAO.createUser("tdlogin1", "tdpassword1", "doctor", "dname1", "dsurname1");
        testList = newUsersDAO.createUser("tplogin1", "tppassword1", "patient", "pname1", "psurname1");
        testList = newUsersDAO.createUser("tphlogin1", "tphpassword1", "pharmacist", "pname11", "psurname11");
        Date d1 = new Date(2020, 1, 14);
        testList = newRecipeDAO.insertRecipe("tdlogin1", "tplogin1", d1);
        testList = newRecipeDAO.getRecipes("tdlogin1", "doctor");
        testList = newPharmacyDAO.createPharmacy("600100200", "street1");
        testList = newPharmacyWorkerDAO.assignPharmacistToPharmacy("tphlogin1", "street1");
        testList = medicinesDAO.addMedicine("firstMedicine", "description", 12.50);
        testList = medicinesDAO.getAllMedicines();

        testList = drugListDAO.insertDrugToList("tphlogin1", "firstMedicine", 10, "tplogin1", "poczta");
        testList = drugListDAO.getDrugsByPharmacistLogin("tphlogin1");
        testList = availabilityDrugDAO.insertAvailabilityDrug("firstMedicine", "street1", 3);
        testList = availabilityDrugDAO.getAvailabilityByPharmacyAddress("street1");
        System.out.println(testList);


        //testList = newUsersDAO.createUser("testUser2", "password2", "patient", "testName1", "TestSurname1");
        //System.out.println(testList);
        //boolean testBoolean = newUsersDAO.isUserValid("testUser1", "password1");
        //System.out.println(testBoolean);
        //testList2 = newUsersDAO.getUser("testUser21", "password2");
        //System.out.println(testList2);
        //testList = newUsersDAO.isUserValid("testUser2", "password2");
        //System.out.println(testList);
        //testList = newUsersDAO.isUserValid("testUser1", "password1");
        //System.out.println(testList);
        //testList = newUsersDAO.deleteUser("testUser2");
        //System.out.println(testList);
        //System.out.println("Testowy");
        //testList = newUsersDAO.updateUserPassword("testUser1", "password1");
        //System.out.println(testList);
        //testList = newUsersDAO.updateUserPassword("testUser11", "newPassword");
        //System.out.println(testList);






        /*




        //Przykładowe wywołania metod z GeneralDAO
        GeneralDAO generalDAO = new GeneralDAO(newConnection);

        HashMap<String, Object> data = new HashMap<>();
        data.put("name", "Paracetamol1");
        data.put("description", "Pain relief");
        data.put("price", 12.50);

        List<HashMap<String, String>> insertResult = generalDAO.insert("medicines", data);

        Set<String> columns = Set.of("drugid", "name", "description", "price");
        String whereClause = "name = ?";
        Object[] whereArgs = {"Paracetamol"};

        List<HashMap<String, String>> results = generalDAO.select("medicines", columns, whereClause, whereArgs);

        System.out.println(results);


        *
         */



    }
}
