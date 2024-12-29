package utils;

import java.util.ArrayList;
import java.util.List;

public class TableSchema {
    private final String tableName;
    private final List<Column> columns;

    public TableSchema(String tableName) {
        this.tableName = tableName;
        this.columns = new ArrayList<>();
    }

    public void addColumn(String columnName, String columnType) {
        this.columns.add(new Column(columnName, columnType));
    }

    public String getTableName() {
        return tableName;
    }

    public List<Column> getColumns() {
        return columns;
    }

    @Override
    public String toString() {
        return "Table: " + tableName + ", Columns: " + columns;
    }
}
