package com.karam.invoice;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionModel {

    private  static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static Connection connection = null;
    private  static final String connStr = "jdbc:mysql://localhost/database1";


    public static Connection dbConnect() throws SQLException, ClassNotFoundException {
        try {
            Class.forName(JDBC_DRIVER);

        } catch(ClassNotFoundException e) {
            System.out.println("Where is mysql jdbc driver");
            e.printStackTrace();
            throw e;
        }
        System.out.println("JDBC driver has been registered");
        try{
                connection = DriverManager.getConnection(connStr,"root","Anshu#2002#");
        }catch (SQLException e)
        {
            System.out.println("Connection failed");
            throw e;
        }
        return connection;
    }

    public static void dbDisConnect() throws SQLException
    {
        try {
            if(connection!= null && !connection.isClosed())
            {
                connection.close();
            }
        }catch (Exception e)
        {
            throw e;
        }
    }


}
