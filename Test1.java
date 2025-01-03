import utils.Message;
/*
* Lista do zrobienia:
* - Generator wierszy by wygenerować przykładowe dane
* - Obsługa transakcji
* - Dorobić DAO
* - Hash-e haseł
* */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Test1 {
    private final Connection engineConnection;
    private final Statement statement;
    private final String DBURL;

    public Test1(String dbname, String user, String password, String DBURL, Message message){
        DBConnection db = new DBConnection(DBURL);
        this.statement = new Statement();
        if (doesDatabaseExist(dbname, user, password, DBURL)) {
            createDatabase(dbname, user, password, DBURL);
        }
        engineConnection = db.connectToDb(dbname, user, password);
        this.DBURL = DBURL;
    }

    //Default DBURL leads to localhost -> jdbc:postgresql://localhost:5432/
    //"MedicalS", "postgres", "admin" <- default values
    public Test1(String dbname, String user, String password){
        DBConnection db = new DBConnection();
        this.DBURL = "jdbc:postgresql://localhost:5432/";
        this.statement = new Statement();
        if (doesDatabaseExist(dbname, user, password, DBURL)) {
            createDatabase(dbname, user, password, DBURL);
        }
        engineConnection = db.connectToDb(dbname, user, password);
    }

    //localhost connection
    public Test1(String dbname){
        DBConnection db = new DBConnection();
        this.DBURL = "jdbc:postgresql://localhost:5432/";
        this.statement = new Statement();
        if (doesDatabaseExist(dbname, "postgres", "admin", DBURL)) {
            createDatabase(dbname, "postgres", "admin", DBURL);
        }
        engineConnection = db.connectToDb(dbname, "postgres", "admin");
    }

    public Test1(){
        DBConnection db = new DBConnection();
        this.DBURL = "jdbc:postgresql://localhost:5432/";
        this.statement = new Statement();
        System.out.println(!doesDatabaseExist("medicals", "postgres", "admin", DBURL));
        if (!doesDatabaseExist("medicals", "postgres", "admin", DBURL)) {
            createDatabase("medicals", "postgres", "admin", DBURL);
        }
        engineConnection = db.connectToDb("medicals", "postgres", "admin");
    }

    private boolean doesDatabaseExist(String dbname, String user, String password, String dbUrl) {
        try (Connection tempConnection = DriverManager.getConnection(dbUrl, user, password)) {
            try (ResultSet rs = tempConnection.createStatement().executeQuery("SELECT 1 FROM pg_database WHERE datname = '" + dbname + "';")) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Main return");
        return false;
    }

    private void createDatabase(String dbname, String user, String password, String dbUrl) {
        try (Connection tempConnection = DriverManager.getConnection(dbUrl, user, password)) {
            this.statement.setStatement("CREATE DATABASE " + dbname, tempConnection);
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }
    }

    void start(){
        Tables table = new Tables();

        Map<String, String> tablesToCreate = Map.of(
                "users", """
                    CREATE TABLE users (
                        id SERIAL PRIMARY KEY,
                        login VARCHAR(255) NOT NULL UNIQUE,
                        password VARCHAR(255) NOT NULL,
                        user_type VARCHAR(50) NOT NULL,
                        name VARCHAR(255) NOT NULL,
                        surname VARCHAR(255) NOT NULL
                    )
                """,

                "pharmacy", """
                    CREATE TABLE pharmacy (
                        pharmacyid SERIAL PRIMARY KEY,
                        phonenr VARCHAR(15),
                        address TEXT
                    )
                """,

                "medicines", """
                    CREATE TABLE medicines (
                        drugid SERIAL PRIMARY KEY,
                        name VARCHAR(255) NOT NULL,
                        description TEXT,
                        price NUMERIC(10, 2) NOT NULL
                    )
                """,

                "availability_drug", """
                    CREATE TABLE availability_drug (
                        drugid INT REFERENCES medicines(drugid),
                        pharmacyid INT REFERENCES pharmacy(pharmacyid),
                        amount INT NOT NULL,
                        PRIMARY KEY (drugid, pharmacyid)
                    )
                """,

                "recipe", """
                    CREATE TABLE recipe (
                        recipeid SERIAL PRIMARY KEY,
                        date DATE NOT NULL,
                        doctorid INT REFERENCES users(id),
                        patientid INT REFERENCES users(id)
                    )
                """,

                "drug_list", """
                    CREATE TABLE drug_list (
                        recipeid INT REFERENCES recipe(recipeid),
                        drugid INT REFERENCES medicines(drugid),
                        amount INT NOT NULL,
                        pharmacistid INT REFERENCES users(id),
                        fulfill_method VARCHAR(50),
                        PRIMARY KEY (recipeid, drugid)
                    )
                """,

                "pharmacy_worker", """
                    CREATE TABLE pharmacy_worker (
                        pharmacyid INT REFERENCES pharmacy(pharmacyid),
                        pharmacistid INT REFERENCES users(id),
                        PRIMARY KEY (pharmacyid, pharmacistid)
                    )
                """,

                "couriers", """
                    CREATE TABLE couriers (
                        courierid SERIAL PRIMARY KEY
                    )
                """
        );
        for (Map.Entry<String, String> entry : tablesToCreate.entrySet()) {
            String tableName = entry.getKey();
            String createQuery = entry.getValue();

            System.out.println(tableName);
            System.out.println(!table.doesTableExist(engineConnection, tableName));
            if (!table.doesTableExist(engineConnection, tableName)) {
                table.createTable(createQuery, engineConnection);
            }
        }

    }

    public List<Map<String, Object>> executeReturnQuery(String query) {
        List<Map<String, Object>> results = new ArrayList<>();

        try (ResultSet rs = statement.getStatement(query, engineConnection)) {
            if (rs != null) {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();

                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnName(i);
                        Object columnValue = rs.getObject(i);
                        row.put(columnName, columnValue);
                    }

                    results.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }

        return results;
    }

    public void executeNonReturnQuery(String query) {
        try {
            statement.setStatement(query, engineConnection);
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    void closeConnection() {
        try {
            if (engineConnection != null && !engineConnection.isClosed()) {
                engineConnection.close();
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    Connection returnConnection(){
        return engineConnection;
    }
}
