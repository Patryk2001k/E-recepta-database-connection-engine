package utils;

import java.util.List;

public class EngineSettings {
    private String DbName;
    private List<TableSchema> DbSchema;

    public String returnDbName(){
        return this.DbName;
    }

    public List<TableSchema> returnDbSchema(){
        return this.DbSchema;
    }

    public void setDbName(String newDbName){
        this.DbName = newDbName;
    }

    public void setDbSchema(List<TableSchema> newDbSchema){
        this.DbSchema = newDbSchema;
    }
}
