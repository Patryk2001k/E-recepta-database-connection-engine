import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    public Connection connectToDb(String dbname, String user, String password){
        Connection conn = null;
        try{
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/"+dbname, user, password);
            if(conn != null){
                System.out.println("Connection established");
            }
            else{
                System.out.println("Connection failed");
            }
        }
        catch (Exception e){
            e.printStackTrace(System.out);
        }
        return conn;
    }
}
