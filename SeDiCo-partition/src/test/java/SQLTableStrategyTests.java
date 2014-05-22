import com.sedico.partition.*;
import com.sedico.sql.*;
import org.junit.*;
import static org.junit.Assert.*;

import java.sql.Types;
import java.util.*;
/**
 * Diese Klasse testet die Strategie der SQL-Tabelle.
 * @author jens
 *
 */
public class SQLTableStrategyTests {
    private SQLTableStrategy strategy;
    private Table table;
    /**
     * Bevor der Test startet, wird eine Tabelle mit 6 Spalten und 2 Zeilen angelegt.
     */
    @Before
    public void setupTable() {
        List<Row> rows = new ArrayList();
        List<ColumnDescriptor> columnDescriptors = new ArrayList();
        List<Column> rowColumns1 = new ArrayList();
        List<Column> rowColumns2 = new ArrayList();
        rowColumns1.add(new Column("ID", 1));
        rowColumns1.add(new Column("DataLeft", "myData"));
        rowColumns1.add(new Column("DataRight", "mySecretData"));
        rowColumns2.add(new Column("ID", 2));
        rowColumns2.add(new Column("DataLeft", "anotherData"));
        rowColumns2.add(new Column("DataRight", "anotherSecretData"));
        Row row1 = new Row(rowColumns1);
        Row row2 = new Row(rowColumns2);
        rows.add(row1);
        rows.add(row2);
        columnDescriptors.add(new PrimaryColumnDescriptor("ID", Types.INTEGER, 4, 0));
        columnDescriptors.add(new ColumnDescriptor("DataLeft", Types.VARCHAR, 100, 0));
        columnDescriptors.add(new ColumnDescriptor("DataRight", Types.VARCHAR, 100, 0));
        table = new Table("customers", rows, columnDescriptors);
    }
    /**
     * Bevor der Test startet, wird eine Strategie für die Tabelle angelegt.
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
        PartitionerStrategy partitionerStrategy = new PartitionByColumnNamesStrategy(partitionDescriptors);
        columns1.add("DataLeft");
        columns2.add("DataRight");
        partitionDescriptors.add(partition1);
        partitionDescriptors.add(partition2);
        strategy = new SQLTableStrategy(partitionerStrategy);
    }
    /**
     * Test, ob partitionierte Tabelle zwei Tablle mit Zeilen zurückliefern.
     */
    @Test
    public void getPartitionedTablesReturnsTwoTablesWithRows() {
        Iterable<PartitionedTable> tables = strategy.getPartitionedTables(table);
        int tableCounter = 0;
        int rowCounter = 0;

        //es ist unglaublich schwer hier wirklich die Inhalte der Tabellen zu testen, weil Java bei den benutzten
        //Iterables den Zugriff auf die Inhalte sehr schwer macht - von daher nur dieser einfache Test
        for (PartitionedTable partitionedTable : tables) {
            tableCounter++;

            for (Row row : partitionedTable.getTable().getRows()) {
                rowCounter++;
            }
            assertEquals(2, rowCounter);
            rowCounter = 0;
        }
        assertEquals(2, tableCounter);
    }
}
