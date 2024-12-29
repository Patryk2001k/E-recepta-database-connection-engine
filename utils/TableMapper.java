package utils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TableMapper {
    public List<TableSchema> mapTables(Map<String, String> tablesToCreate) {
        List<TableSchema> tableSchemas = new ArrayList<>();
        Pattern tablePattern = Pattern.compile("CREATE TABLE (\\w+) \\((.*)\\)", Pattern.DOTALL);

        for (Map.Entry<String, String> entry : tablesToCreate.entrySet()) {
            String createQuery = entry.getValue();
            Matcher tableMatcher = tablePattern.matcher(createQuery);

            if (tableMatcher.find()) {
                String tableName = tableMatcher.group(1);
                String columnsPart = tableMatcher.group(2).trim();

                TableSchema schema = new TableSchema(tableName);
                List<String> columns = extractColumns(columnsPart);

                for (String column : columns) {
                    String[] columnParts = column.trim().split("\\s+", 2);
                    if (columnParts.length == 2) {
                        String columnName = columnParts[0];
                        String columnType = columnParts[1];
                        schema.addColumn(columnName, columnType);
                    }
                }

                tableSchemas.add(schema);
            }
        }

        return tableSchemas;
    }

    private List<String> extractColumns(String columnsPart) {
        List<String> columns = new ArrayList<>();
        StringBuilder currentColumn = new StringBuilder();
        int openParentheses = 0;

        for (char c : columnsPart.toCharArray()) {
            if (c == '(') {
                openParentheses++;
            } else if (c == ')') {
                openParentheses--;
            }

            if (c == ',' && openParentheses == 0) {
                // Koniec definicji kolumny
                columns.add(currentColumn.toString().trim());
                currentColumn.setLength(0);
            } else {
                currentColumn.append(c);
            }
        }

        // Dodaj ostatnią kolumnę, jeśli istnieje
        if (currentColumn.length() > 0) {
            columns.add(currentColumn.toString().trim());
        }

        return columns;
    }
}
