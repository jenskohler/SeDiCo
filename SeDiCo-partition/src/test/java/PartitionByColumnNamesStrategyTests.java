import com.sedico.partition.*;
import com.sedico.sql.*;
import org.junit.*;
import static org.junit.Assert.*;

import java.sql.Types;
import java.util.*;
/**
 * Diese Klasse testet die Partitionen.
 * @author jens
 *
 */
public class PartitionByColumnNamesStrategyTests {
    private PartitionByColumnNamesStrategy strategy;
    private Table table;
    /**
     * Bevor der Test startet, wird eine Tabelle erzegut mit 3 Spalten und 1 Zeile.
     */
    @Before
    public void setupTable() {
        List<Row> rows = new ArrayList();
        List<ColumnDescriptor> columnDescriptors = new ArrayList();
        List<Column> columns = new ArrayList();
        columns.add(new Column("ID", 1));
        columns.add(new Column("DataLeft", "myData"));
        columns.add(new Column("DataRight", "mySecretData"));
        Row row1 = new Row(columns);
        rows.add(row1);
        columnDescriptors.add(new PrimaryColumnDescriptor("ID", Types.INTEGER, 4, 0));
        columnDescriptors.add(new ColumnDescriptor("DataLeft", Types.VARCHAR, 100, 0));
        columnDescriptors.add(new ColumnDescriptor("DataRight", Types.VARCHAR, 100, 0));
        table = new Table("customers", rows, columnDescriptors);
    }
    /**
     * Bevor der Test startet wird zusätzlich noch eine PartitionByColumnNamesStrategy angelegt.
     */
    @Before
    public void setupStrategy() {
        List<PartitionDescriptor> partitionDescriptors = new ArrayList();
        List<String> columns1 = new ArrayList();
        List<String> columns2 = new ArrayList();
        SqlConnectionDescriptor connection1 = SqlConnectionDescriptor.create("server", 0, "database", "table", "user", "password", SQLServers.MySQL);
        SqlConnectionDescriptor connection2 = MySqlConnectionDescriptor.create("server", 0, "database", "table", "user", "password", SQLServers.MySQL);
        PartitionDescriptor partition1 = new PartitionDescriptor("part1", true, "", columns1, connection1);
        PartitionDescriptor partition2 = new PartitionDescriptor("part2", false, "", columns2, connection2);
        columns1.add("DataLeft");
        columns2.add("DataRight");
        partitionDescriptors.add(partition1);
        partitionDescriptors.add(partition2);
        strategy = new PartitionByColumnNamesStrategy(partitionDescriptors);
    }
    /**
     * Test, ob die korrekten Spalten der Partition 0 zurückgegeben werden
     */
    @Test
    public void getColumnsForPartition0ReturnsCorrectColumns() {
        Iterable<Column> columns = strategy.getColumnsForPartition(0, table, table.getRows().get(0));
        int counter = 0;

        for (Column column : columns) {
            counter++;
            if (counter == 1) {
                assertEquals("ID", column.getColumnName());
                assertEquals(1, column.getColumnValue());
            }
            else {
                assertEquals("DataLeft", column.getColumnName());
                assertEquals("myData", column.getColumnValue());
            }
        }
        assertEquals(2, counter);
    }
    /**
     *Test, ob die korrekten Spalten der Partition 0 zurückgegeben werden
     **/
    @Test
    public void getColumnsForPartition1ReturnsCorrectColumns() {
        Iterable<Column> columns = strategy.getColumnsForPartition(1, table, table.getRows().get(0));
        int counter = 0;

        for (Column column : columns) {
            counter++;
            if (counter == 1) {
                assertEquals("ID", column.getColumnName());
                assertEquals(1, column.getColumnValue());
            }
            else {
                assertEquals("DataRight", column.getColumnName());
                assertEquals("mySecretData", column.getColumnValue());
            }
        }
        assertEquals(2, counter);
    }
}
