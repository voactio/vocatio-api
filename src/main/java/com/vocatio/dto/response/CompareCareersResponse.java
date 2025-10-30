package com.vocatio.dto.response;

import java.util.List;

public class CompareCareersResponse {

    public static class Column {
        private String careerId;
        private String name;
        public Column() {}
        public Column(String careerId, String name) { this.careerId = careerId; this.name = name; }
        public String getCareerId() { return careerId; }
        public void setCareerId(String careerId) { this.careerId = careerId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    public static class Row {
        private String key;     // p.ej. "durationYears"
        private String label;   // p.ej. "Duración"
        private List<?> values; // String / Number / List<String>
        public Row() {}
        public Row(String key, String label, List<?> values) { this.key = key; this.label = label; this.values = values; }
        public String getKey() { return key; }
        public void setKey(String key) { this.key = key; }
        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }
        public List<?> getValues() { return values; }
        public void setValues(List<?> values) { this.values = values; }
    }

    private List<Column> columns;
    private List<Row> rows;

    public CompareCareersResponse() {}
    public CompareCareersResponse(List<Column> columns, List<Row> rows) {
        this.columns = columns; this.rows = rows;
    }
    public List<Column> getColumns() { return columns; }
    public void setColumns(List<Column> columns) { this.columns = columns; }
    public List<Row> getRows() { return rows; }
    public void setRows(List<Row> rows) { this.rows = rows; }
}
