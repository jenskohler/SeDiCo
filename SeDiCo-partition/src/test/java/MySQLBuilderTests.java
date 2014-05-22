import com.sedico.sql.*;
import org.junit.*;
import static org.junit.Assert.*;

import java.sql.Types;
import java.util.*;
/**
 * Diese Klasse testet den Erzeuger der MySQL-Datenbank.
 * @author jens
 *
 */
public class MySQLBuilderTests {

    private MySQLBuilder builder;
    private Table table;
    /**
     * Bevor der Test durchgeführt wird, wird einen Tabelle angelegt und ein MySQLBuilder erzeugt.
     */
    @Before
    public void before() {
        List<Row> rows = new ArrayList();
        List<ColumnDescriptor> columnDescriptors = new ArrayList();
        List<Column> columns = new ArrayList();
        columns.add(new Column("ID", 1));
        columns.add(new Column("Data", "myData"));
        Row row1 = new Row(columns);
        rows.add(row1);
        columnDescriptors.add(new PrimaryColumnDescriptor("ID", Types.INTEGER, 4, 0));
        columnDescriptors.add(new ColumnDescriptor("Data", Types.VARCHAR, 100, 0));
        table = new Table("customers", rows, columnDescriptors);
        builder = new MySQLBuilder();
    }
    /**
     * Test, ob Löschen von Rückgabe-Statements funktioniert.
     */
    @Test
    public void deleteReturnsDeleteStatement() {
        List<String> deletes = builder.buildDeletes(table, "testDB");

        assertTrue(deletes.get(0).contains("DELETE FROM testDB.customers WHERE ID = '1';"));
    }
    /**
     * Test, ob Aktualisierung von Statements funktioniert.
     */
    @Test
    public void updateReturnsUpdateStatement() {
        List<String> updates = builder.buildUpdates(table, "testDB");

        assertTrue(updates.get(0).contains("UPDATE testDB.customers SET Data = 'myData' WHERE ID = '1'"));
    }
    /**
     * Test, ob das Einfügen eines Statements funktioniert.
     */
    @Test
    public void insertReturnsInsertStatement() {
        List<String> inserts = builder.buildInserts(table, "testDB");

        assertTrue(inserts.get(0).contains("INSERT INTO testDB.customers (Data,ID) VALUES ('myData','1')"));
    }
    /**
     * Test, ob das Auswählen eines Statements funktioniert.
     */
    @Test
    public void selectReturnsSelectStatement() {
        String select = builder.buildSelect(table, "testDB");

        assertTrue(select.contains("SELECT ID,Data FROM testDB.customers WHERE ID = '1'"));
    }
}
