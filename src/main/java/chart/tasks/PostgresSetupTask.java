package chart.tasks;

import java.io.IOException;
import java.sql.SQLException;

import chart.postgres.PostgresConfig;
import chart.postgres.PostgresConnectionManager;

public class PostgresSetupTask {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        PostgresConfig config = TaskUtils.getConfig().postgresConfig();

        PostgresConnectionManager connection = new PostgresConnectionManager(config);

        try {
            connection.setupSchema();
        } catch (SQLException e) {
            System.out.println("Encountered exception " + e.getMessage() + " creating database.");
            e.printStackTrace();
        }
    }
}
