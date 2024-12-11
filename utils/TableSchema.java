package utils;

import java.util.ArrayList;
import java.util.List;

class TableSchema {
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

    static class Column {
        private final String name;
        private final String type;

        public Column(String name, String type) {
            this.name = name;
            this.type = type;
        }

        @Override
        public String toString() {
            return name + " " + type;
        }
    }

    @Override
    public String toString() {
        return "Table: " + tableName + ", Columns: " + columns;
    }
}
