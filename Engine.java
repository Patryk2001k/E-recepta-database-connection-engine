import utils.EngineSettings;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;


public class Engine {
    private final Connection engineConnection;
    private final Statement statement;
    private final String DBURL;
    private final EngineStartup engineStartup = new EngineStartup();

    public Engine(String dbname, String user, String password, String DBURL){
        DBConnection db = new DBConnection(DBURL);
        this.statement = new Statement();
        if (!doesDatabaseExist(dbname, user, password, DBURL)) {
            createDatabase(dbname, user, password, DBURL);
        }
        engineConnection = db.connectToDb(dbname, user, password);
        this.DBURL = DBURL;
    }

    //Default DBURL leads to localhost -> jdbc:postgresql://localhost:5432/
    //"MedicalS", "postgres", "admin" <- default values
    public Engine(String dbname, String user, String password){
        DBConnection db = new DBConnection();
        this.DBURL = "jdbc:postgresql://localhost:5432/";
        this.statement = new Statement();
        if (!doesDatabaseExist(dbname, user, password, DBURL)) {
            createDatabase(dbname, user, password, DBURL);
        }
        engineConnection = db.connectToDb(dbname, user, password);
    }

    //localhost connection
    public Engine(String dbname){
        DBConnection db = new DBConnection();
        this.DBURL = "jdbc:postgresql://localhost:5432/";
        this.statement = new Statement();
        if (!doesDatabaseExist(dbname, "postgres", "admin", DBURL)) {
            createDatabase(dbname, "postgres", "admin", DBURL);
        }
        engineConnection = db.connectToDb(dbname, "postgres", "admin");
    }

    public Engine(){
        DBConnection db = new DBConnection();
        this.DBURL = "jdbc:postgresql://localhost:5432/";
        this.statement = new Statement();
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
            System.out.println("default return");
            return true;
        }
    }

    private void createDatabase(String dbname, String user, String password, String dbUrl) {
        try (Connection tempConnection = DriverManager.getConnection(dbUrl, user, password)) {
            this.statement.setStatement("CREATE DATABASE " + dbname, tempConnection);
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }
    }

    void start() {
        this.engineStartup.run(engineConnection);
        Map<String, String> mapOfTables = new LinkedHashMap<>(this.engineStartup.returnMapOfTables());
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

    public Connection returnConnection(){
        return engineConnection;
    }
}
