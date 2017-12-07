package chart.postgres;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class PostgresConnection {
    private final PostgresConfig config;

    public PostgresConnection(PostgresConfig config) {
        this.config = config;
    }

    public void createDatabase() throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");

        try (Connection conn = DriverManager.getConnection(
                "jdbc:postgresql://localhost/",
                config.user(),
                config.password())) {
            Statement statement = conn.createStatement();
            // TODO guard against sql injection here
            int output = statement.executeUpdate("CREATE DATABASE " + config.dbName());
            System.out.println("Create database returned " + 1);
        }
    }
}
