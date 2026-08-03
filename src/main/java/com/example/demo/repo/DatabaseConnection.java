package com.example.demo.repo;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {

    private final String USERNAME = "root";
    private final String PASSWORD = "Edmund@123";
    private final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private final String DATABASE = "expense_db";
    private final String URL = "jdbc:mysql://localhost:3306/" + DATABASE;


    private Connection connection;
    public static DatabaseConnection databaseConnection;

    public DatabaseConnection(){
        try {
            Class.forName(DRIVER);
            connection = DriverManager.getConnection(URL,USERNAME,PASSWORD);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public static  DatabaseConnection getInstance(){
        if(databaseConnection == null){
            databaseConnection = new DatabaseConnection();
        }
        return databaseConnection;
    }

    public boolean isConnected(){
        try{
            return connection != null && !connection.isClosed();

        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public Connection getConnection() {
        return connection;
    }
}