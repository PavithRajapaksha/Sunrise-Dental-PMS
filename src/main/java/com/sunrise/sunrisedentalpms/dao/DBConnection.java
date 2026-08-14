package com.sunrise.sunrisedentalpms.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {

    private static DBConnection instance;
    private Connection connection;

    private static final Properties properties = new Properties();

    // Load application.properties
    static {
        try (InputStream input = DBConnection.class
                .getClassLoader()
                .getResourceAsStream("application.properties")) {

            if (input == null) {
                throw new RuntimeException(
                        "application.properties file not found"
                );
            }

            properties.load(input);

        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to load application.properties", e
            );
        }
    }

    private DBConnection() {
        try {
            String dbUrl = properties.getProperty("db.url");
            String dbUsername = properties.getProperty("db.username");
            String dbPassword = properties.getProperty("db.password");
            String dbDriver = properties.getProperty("db.driver");

            Class.forName(dbDriver);

            connection = DriverManager.getConnection(
                    dbUrl,
                    dbUsername,
                    dbPassword
            );

            System.out.println("Database connection successful!");

        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Database connection failed!");
            e.printStackTrace();
        }
    }

    public static DBConnection getInstance() {

        if (instance == null) {
            synchronized (DBConnection.class) {
                if (instance == null) {
                    instance = new DBConnection();
                }
            }
        }

        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}