package com.cheolpang.cpplugin.data;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLite {
    private final File dbFile;
    private Connection connection;

    public SQLite(File pluginFolder) {
        if (!pluginFolder.exists()) pluginFolder.mkdirs();
        this.dbFile = new File(pluginFolder, "cpplugin.db");
    }

    public void connect() throws SQLException {
        String url = "jdbc:sqlite:" + dbFile.getAbsolutePath();
        this.connection = DriverManager.getConnection(url);
    }

    public Connection getConnection() {
        return connection;
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) connection.close();
        } catch (SQLException ignored) {}
    }
}
