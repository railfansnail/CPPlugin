package com.cheolpang.cpplugin.data;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.bukkit.plugin.java.JavaPlugin;

public class SQLite {

    private final JavaPlugin plugin;
    private Connection connection;

    public SQLite(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void connect() {
        try {
            File dbFile = new File(plugin.getDataFolder(), "cpplugin.db");
            if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdirs();

            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createTables() {
        try (var stmt = connection.createStatement()) {
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS users (
                    uuid TEXT PRIMARY KEY,
                    nickname TEXT,
                    agreed INTEGER
                );
            """);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
