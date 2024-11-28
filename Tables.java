import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

public class Tables {
    void createTable(String statement, Connection conn){
        Statement new_statement = new Statement();
        new_statement.setStatement(statement, conn);
    }
    boolean doesTableExist(Connection conn, String tableName) {
        try {
            DatabaseMetaData dbMetaData = conn.getMetaData();
            try (ResultSet rs = dbMetaData.getTables(null, null, tableName, null)) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            return false;
        }
    }
}
