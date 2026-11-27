package stayops_api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class DatabaseFixRunner implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        try {
            // Find foreign key for guest_id in reservations
            List<Map<String, Object>> results = jdbcTemplate.queryForList(
                    "SELECT CONSTRAINT_NAME FROM information_schema.KEY_COLUMN_USAGE " +
                    "WHERE TABLE_SCHEMA = 'stayops_learning' AND TABLE_NAME = 'reservations' AND COLUMN_NAME = 'guest_id' AND REFERENCED_TABLE_NAME IS NOT NULL"
            );

            for (Map<String, Object> row : results) {
                String fkName = (String) row.get("CONSTRAINT_NAME");
                if (fkName != null) {
                    jdbcTemplate.execute("ALTER TABLE reservations DROP FOREIGN KEY " + fkName);
                    System.out.println("Dropped FK: " + fkName);
                }
            }
        } catch (Exception e) {
            System.out.println("Error dropping FK: " + e.getMessage());
        }
        
        try {
            jdbcTemplate.execute("ALTER TABLE reservations DROP COLUMN guest_id");
            System.out.println("Dropped guest_id column successfully.");
        } catch (Exception e) {
            System.out.println("guest_id column might already be dropped: " + e.getMessage());
        }

        try {
            jdbcTemplate.execute("DROP TABLE guests");
            System.out.println("Dropped guests table successfully.");
        } catch (Exception e) {
            System.out.println("guests table might already be dropped: " + e.getMessage());
        }

        // Schema migration for Tax Snapshot & Financial Components
        addColIfNotExists("tax_settings_snapshot", "TEXT");
        addColIfNotExists("room_base_total", "DECIMAL(10,2)");
        addColIfNotExists("room_subtotal", "DECIMAL(10,2)");
        addColIfNotExists("room_service_charge", "DECIMAL(10,2)");
        addColIfNotExists("room_gst_amount", "DECIMAL(10,2)");

        // Schema migration for ID Documents
        addColIfNotExists("id_type", "VARCHAR(100)");
        addColIfNotExists("id_number", "VARCHAR(100)");
        addColIfNotExists("id_document_url", "TEXT");
        addColIfNotExists("id_documents", "TEXT");
        addColIfNotExists("guest_city", "VARCHAR(100)");
        addColIfNotExists("guest_state", "VARCHAR(100)");

        addColToTableIfNotExists("guest_profiles", "id_type", "VARCHAR(100)");
        addColToTableIfNotExists("guest_profiles", "id_documents", "TEXT");

        // DDL for reservation_rooms mapping table
        try {
            jdbcTemplate.execute(
                "CREATE TABLE IF NOT EXISTS reservation_rooms (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "reservation_id BIGINT NOT NULL, " +
                "room_unit VARCHAR(255), " +
                "room_type_id BIGINT, " +
                "room_type_name VARCHAR(255), " +
                "nightly_rate DECIMAL(10,2) DEFAULT 0.00, " +
                "capacity INT DEFAULT 1, " +
                "FOREIGN KEY (reservation_id) REFERENCES reservations(id) ON DELETE CASCADE" +
                ")"
            );
            System.out.println("Ensured reservation_rooms table exists.");
        } catch (Exception e) {
            System.out.println("Table reservation_rooms check/creation note: " + e.getMessage());
        }
    }

    private void addColIfNotExists(String colName, String colType) {
        addColToTableIfNotExists("reservations", colName, colType);
    }

    private void addColToTableIfNotExists(String tableName, String colName, String colType) {
        try {
            jdbcTemplate.execute("ALTER TABLE " + tableName + " ADD COLUMN " + colName + " " + colType);
            System.out.println("Added column " + colName + " to table " + tableName);
        } catch (Exception e) {
            // Column already exists or table structure handles it
        }
    }
}
