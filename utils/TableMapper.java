package utils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TableMapper {
    public List<TableSchema> mapTables(Map<String, String> tablesToCreate) {
        List<TableSchema> tableSchemas = new ArrayList<>();
        Pattern tablePattern = Pattern.compile("CREATE TABLE (\\w+) \\((.*?)\\)", Pattern.DOTALL);
        Pattern columnPattern = Pattern.compile("(\\w+)\\s+([A-Z0-9\\(\\),\\s]+)(?:,|$)", Pattern.CASE_INSENSITIVE);

        for (Map.Entry<String, String> entry : tablesToCreate.entrySet()) {
            String createQuery = entry.getValue();
            Matcher tableMatcher = tablePattern.matcher(createQuery);

            if (tableMatcher.find()) {
                String tableName = tableMatcher.group(1);
                String columnsPart = tableMatcher.group(2);

                TableSchema schema = new TableSchema(tableName);
                Matcher columnMatcher = columnPattern.matcher(columnsPart);

                while (columnMatcher.find()) {
                    String columnName = columnMatcher.group(1);
                    String columnType = columnMatcher.group(2).trim();
                    schema.addColumn(columnName, columnType);
                }

                tableSchemas.add(schema);
            }
        }

        return tableSchemas;
    }
}
