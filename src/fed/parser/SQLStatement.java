package fed.parser;

import java.util.List;
import java.util.Map;

public class SQLStatement {

    private String table;
    private int sql_type;
    private Map<String, String> columns;
    private List<String> conditions;

    /* Getters & Setters */
    public Map<String, String> getColumns () {
        return columns;
    }

    public void setColumns (Map<String, String> columns) {
        this.columns = columns;
    }

    public List<String> getConditions () {
        return conditions;
    }

    public void setConditions (List<String> conditions) {
        this.conditions = conditions;
    }
    
    public String getTableName() {
        return table;
    }

    public void setTableName(String table) {
        this.table = table;
    }

    public int getStatementType () {
        return sql_type;
    }

    public void setStatementType (int sql_type) {
        this.sql_type = sql_type;
    }
}
