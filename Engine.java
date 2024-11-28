import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Engine {
    private final Connection engineConnection;
    private final Statement statement;
    private final String DBURL;

    public Engine(String dbname, String user, String password, String DBURL){
        DBConnection db = new DBConnection();
        engineConnection = db.connectToDb(dbname, user, password);
        this.statement = new Statement();
        this.DBURL = DBURL;
        if (!doesDatabaseExist(dbname, user, password, DBURL)) {
            createDatabase(dbname, user, password, DBURL);
        }
    }

    //Default DBURL leads to localhost -> jdbc:postgresql://localhost:5432/
    //"MedicalS", "postgres", "admin" <- default values
    public Engine(String dbname, String user, String password){
        DBConnection db = new DBConnection();
        engineConnection = db.connectToDb(dbname, user, password);
        this.statement = new Statement();
        this.DBURL = "jdbc:postgresql://localhost:5432/";
        if (!doesDatabaseExist(dbname, user, password, DBURL)) {
            createDatabase(dbname, user, password, DBURL);
        }
    }

    //localhost connection
    public Engine(String dbname){
        DBConnection db = new DBConnection();
        engineConnection = db.connectToDb(dbname, "postgres", "admin");
        this.statement = new Statement();
        this.DBURL = "jdbc:postgresql://localhost:5432/";
        if (!doesDatabaseExist(dbname, "postgres", "admin", DBURL)) {
            createDatabase(dbname, "postgres", "admin", DBURL);
        }
    }

    public Engine(){
        DBConnection db = new DBConnection();
        engineConnection = db.connectToDb("MedicalS", "postgres", "admin");
        this.statement = new Statement();
        this.DBURL = "jdbc:postgresql://localhost:5432/";
        if (!doesDatabaseExist("MedicalS", "postgres", "admin", DBURL)) {
            createDatabase("MedicalS", "postgres", "admin", DBURL);
        }
    }

    private boolean doesDatabaseExist(String dbname, String user, String password, String dbUrl) {
        try (Connection tempConnection = DriverManager.getConnection(dbUrl, user, password)) {
            try (ResultSet rs = tempConnection.createStatement().executeQuery("SELECT 1 FROM pg_database WHERE datname = '" + dbname + "';")) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
                        Id SERIAL PRIMARY KEY,
                        Login VARCHAR(255) NOT NULL UNIQUE,
                        Password VARCHAR(255) NOT NULL,
                        UserType VARCHAR(50) NOT NULL,
                        Name VARCHAR(255) NOT NULL,
                        Surname VARCHAR(255) NOT NULL
                    )
                """,

                            "pharmacy", """
                    CREATE TABLE pharmacy (
                        PharmacyID SERIAL PRIMARY KEY,
                        PhoneNr VARCHAR(15),
                        Address TEXT
                    )
                """,

                            "medicines", """
                    CREATE TABLE medicines (
                        DrugID SERIAL PRIMARY KEY,
                        Name VARCHAR(255) NOT NULL,
                        Description TEXT,
                        Price NUMERIC(10, 2) NOT NULL
                    )
                """,

                            "availability_drug", """
                    CREATE TABLE availability_drug (
                        DrugID INT REFERENCES medicines(DrugID),
                        PharmacyID INT REFERENCES pharmacy(PharmacyID),
                        Amount INT NOT NULL,
                        PRIMARY KEY (DrugID, PharmacyID)
                    )
                """,

                            "recipe", """
                    CREATE TABLE recipe (
                        RecipeID SERIAL PRIMARY KEY,
                        Date DATE NOT NULL,
                        DoctorID INT REFERENCES users(Id),
                        PatientID INT REFERENCES users(Id)
                    )
                """,

                            "drug_list", """
                    CREATE TABLE drug_list (
                        RecipeID INT REFERENCES recipe(RecipeID),
                        DrugID INT REFERENCES medicines(DrugID),
                        Amount INT NOT NULL,
                        PharmacistID INT REFERENCES users(Id),
                        FulfillMethod VARCHAR(50),
                        PRIMARY KEY (RecipeID, DrugID)
                    )
                """,

                            "pharmacy_worker", """
                    CREATE TABLE pharmacy_worker (
                        PharmacyID INT REFERENCES pharmacy(PharmacyID),
                        PharmacistID INT REFERENCES users(Id),
                        PRIMARY KEY (PharmacyID, PharmacistID)
                    )
                """,

                            "couriers", """
                    CREATE TABLE couriers (
                        CourierID SERIAL PRIMARY KEY
                    )
                """
        );
        for (Map.Entry<String, String> entry : tablesToCreate.entrySet()) {
            String tableName = entry.getKey();
            String createQuery = entry.getValue();

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
