package com.zg.natural_transmute.common.data.generating.format;

import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class TsvGeneratingTable extends MemoryGeneratingTable {
    @SneakyThrows
    public static TsvGeneratingTable fromResource(String path) {
        try (var stream = TsvGeneratingTable.class.getResourceAsStream(path)) {
            if (stream == null) {
                throw new IllegalArgumentException("Resource " + path + " is not exist");
            }
            try (var reader = new BufferedReader(new InputStreamReader(stream))) {
                return new TsvGeneratingTable(reader.lines().toArray(String[]::new));
            }
        }
    }

    public TsvGeneratingTable(String[] lines) {
        for (var row = 0; row < lines.length; row++) {
            var line = lines[row];
            if (line.isBlank()) {
                continue;
            }
            var cells = line.split("\t");
            var columns = 0;
            for (var column = 0; column < cells.length; column++) {
                var value = cells[column].trim();
                if (value.startsWith("\"") && value.endsWith("\"")) {
                    value = value.substring(1, value.length() - 1);
                }
                value = value.replace("\"\"", "\"");
                if (!value.isBlank()) {
                    table.put(row, column, value);
                }
                columns += 1;
            }
            this.columnsTotal = Math.max(columns, columnsTotal);
            this.rowsTotal += 1;
        }
    }
}
