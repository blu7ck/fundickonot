package com.blu4ck.fundickonot;

import java.sql.Connection;
import java.sql.DriverManager;

public class Database {

    public static Connection connectionDb(){

        try{

            Class.forName("com.mysql.cj.jdbc.Driver");

            return DriverManager.getConnection("jdbc:mysql://localhost:user","root","");
        }catch (Exception e){e.printStackTrace();}
        return null;
    }
}
