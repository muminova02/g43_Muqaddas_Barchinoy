package uz.app.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;


public class TestConnection {
    private final  String databaseURL = "jdbc:postgresql://localhost:5432/market_db";
    private  Connection connection = null;

    public  void test() {
        Properties props = new Properties();
        props.setProperty("user", "postgres");
        props.setProperty("password", "root123");
        try {
            connection = DriverManager.getConnection(databaseURL, props);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public Statement getStatement() {
        try {
            return connection.createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private static TestConnection instance;

    public static TestConnection getInstance() {
        if (instance == null) {
            instance = new TestConnection();
            instance.test();
        }
        return instance;
    }
}
