import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    private final String DBURL;

    public DBConnection(String DBURl){
        this.DBURL = DBURl;
    }

    public DBConnection(){
        this.DBURL = "jdbc:postgresql://localhost:5432/";
    }

    public Connection connectToDb(String dbname, String user, String password){
        Connection conn = null;
        try{
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(this.DBURL+dbname, user, password);
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
