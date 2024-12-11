import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.Map;

public class EngineStartup {
    private final Tables table = new Tables();
    private final Map<String, String> tablesToCreate = new LinkedHashMap<>();

    public void run(Connection engineConnection){

        tablesToCreate.put("users", """
            CREATE TABLE users (
                id SERIAL PRIMARY KEY,
                login VARCHAR(255) NOT NULL UNIQUE,
                password VARCHAR(255) NOT NULL,
                user_type VARCHAR(50) NOT NULL,
                name VARCHAR(255) NOT NULL,
                surname VARCHAR(255) NOT NULL
            )
        """);

        tablesToCreate.put("pharmacy", """
            CREATE TABLE pharmacy (
                pharmacyid SERIAL PRIMARY KEY,
                phonenr VARCHAR(15),
                address TEXT
            )
        """);

        tablesToCreate.put("medicines", """
            CREATE TABLE medicines (
                drugid SERIAL PRIMARY KEY,
                name VARCHAR(255) NOT NULL,
                description TEXT,
                price NUMERIC(10, 2) NOT NULL
            )
        """);

        tablesToCreate.put("availability_drug", """
            CREATE TABLE availability_drug (
                drugid INT REFERENCES medicines(drugid),
                pharmacyid INT REFERENCES pharmacy(pharmacyid),
                amount INT NOT NULL,
                PRIMARY KEY (drugid, pharmacyid)
            )
        """);

        tablesToCreate.put("recipe", """
            CREATE TABLE recipe (
                recipeid SERIAL PRIMARY KEY,
                date DATE NOT NULL,
                doctorid INT REFERENCES users(id),
                patientid INT REFERENCES users(id)
            )
        """);

        tablesToCreate.put("drug_list", """
            CREATE TABLE drug_list (
                recipeid INT REFERENCES recipe(recipeid),
                drugid INT REFERENCES medicines(drugid),
                amount INT NOT NULL,
                pharmacistid INT REFERENCES users(id),
                fulfill_method VARCHAR(50),
                PRIMARY KEY (recipeid, drugid)
            )
        """);

        tablesToCreate.put("pharmacy_worker", """
            CREATE TABLE pharmacy_worker (
                pharmacyid INT REFERENCES pharmacy(pharmacyid),
                pharmacistid INT REFERENCES users(id),
                PRIMARY KEY (pharmacyid, pharmacistid)
            )
        """);

        tablesToCreate.put("couriers", """
            CREATE TABLE couriers (
                courierid SERIAL PRIMARY KEY
            )
        """);

        for (Map.Entry<String, String> entry : tablesToCreate.entrySet()) {
            String tableName = entry.getKey();
            String createQuery = entry.getValue();

            if (!table.doesTableExist(engineConnection, tableName)) {
                table.createTable(createQuery, engineConnection);
            }
        }
    }

    public Map<String, String> returnMapOfTables(){
        return tablesToCreate;
    }
}
