/*
 * @Author: yetaai
 * yetaai@gmail.com
 * This software piece is apache license. But the contents generated is governed by wiktionary.org policy.
 */

package org.wiktionary;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import org.apache.commons.configuration.PropertiesConfiguration;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class DAO {
//    private static DAO instance; //not necessary design for single user environment
    private static Connection conn;
    private PreparedStatement pstmt;
    private ResultSet rs;
    
    static {
        createConnection();
    }
    
/*    public static void deleteDAO() {
        closeConnection(); //This will release all resources
    }
*/    
    //set parameter methods
    public synchronized void setObject(int parameterIndex, Object x) {
        
        try {
            getPstmt().setObject(parameterIndex, x);
        }
        catch (SQLException sqle) {
            System.err.println(" pstmt.setObject(parameterIndex, x) Fail! Error = " + sqle.toString());
        }
        
    }
    
    public synchronized void setBoolean(int parameterIndex, boolean flag) {
        
        try {
            getPstmt().setBoolean(parameterIndex, flag);
        }
        catch (SQLException sqle) {
            System.err.println(" pstmt.setBoolean(parameterIndex, flag) Fail! Error = " + sqle.toString());
        }
        
    }
    
    public synchronized void setDouble(int parameterIndex, double x) {
        
        try {
            getPstmt().setDouble(parameterIndex, x);
        }
        catch (SQLException sqle) {
            System.err.println(" pstmt.setDouble(parameterIndex, x) Fail! Error = " + sqle.toString());
        }
        
    }
    
    public synchronized void setInt(int parameterIndex, int x) {
        
        try {
            getPstmt().setInt(parameterIndex, x);
        }
        catch (SQLException sqle) {
            System.err.println(" pstmt.setInt(parameterIndex, x) Fail! Error = " + sqle.toString());
        }
        
    }
    
    public synchronized void setString(int parameterIndex, String s) {
        
        try {
            getPstmt().setString(parameterIndex, s);
        }
        catch (SQLException sqle) {
            System.err.println(" pstmt.setString(parameterIndex, s) Fail! Error = " + sqle.toString() + " SQL state?" + getPstmt().toString());
        }
        
    }
    
    public synchronized boolean executeUpdate() {
        
        boolean flag = true;
        try {
            getPstmt().executeUpdate();
//            System.err.println("Update informaiton:" + getPstmt().toString());
        }
        catch (SQLException sqle) {
            flag = false;
            System.out.println("executeUpdate() Error!" + sqle.toString() + getPstmt().toString());
        }
        
        return flag;
        
    }
    
    public synchronized void update(String expression) {
        pstmt = null;
        try {
            pstmt = conn.prepareStatement(expression);
        } 
        catch (SQLException sqle) {
            System.err.println("Update DataBase Preparation Fail! Error = " + sqle.toString());
        }
    }
    
    public synchronized ResultSet executeQuery() {
        rs = null;
        try {
            rs = getPstmt().executeQuery();
        }
        catch (SQLException sqle) {
            System.err.println("Query DataBase Fail! Error = " + sqle.toString());
        }
        return rs;
    }
        
    public synchronized void query(String expression) {
        pstmt = null;
        try {
            pstmt = conn.prepareStatement(expression);
        } 
        catch (SQLException sqle) {
            System.err.println("Query DataBase Fail! Error = " + sqle.toString() + " " + expression);
        }
    }
    
    public synchronized static void closeConnection() {
        try {
            conn.close();
            conn = null;
            System.out.println("Close Connect DataBase Success!");
        }
        catch (SQLException e) {
            System.err.println("Close Connect DataBase Fail! Error = " + e.toString());
        }
    }
    
    public synchronized void commit() {
        
         try {
            conn.commit();
        }
        catch (SQLException sqle) {
            System.err.println(sqle.toString());
        }
        
    }
    
    public synchronized void rollback() {
        
        try {
            conn.rollback();
        }
        catch (SQLException sqle) {
            System.err.println(sqle.toString());
        }
        
    }
    
    public void setAutoCommit(boolean flag) {
        try {
            conn.setAutoCommit(flag);
        }
        catch (SQLException sqle) {
            System.err.println(sqle.toString());
        }
    }
    
    public static void createConnection(){
         try {
            if (conn == null) {
                Class.forName(Res.DRIVER_NAME);
                conn = DriverManager.getConnection(Res.DATABASE_ADDRESS, Res.getProp().getString("dbusername"), Res.getProp().getString("dbpassword"));
            }
            System.out.println("Connect DB Success: " + Res.DATABASE_ADDRESS + ". Account: " +
                   Res.getProp().getString("dbusername") + ", password: " + Res.getProp().getString("dbpassword") + ".");
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }
    
/*    public static DAO getInstance(){

        
        if (instance != null) {
            return instance;
        } else {
            instance = new DAO();
            return instance;
        }
        
    }
*/
    private PreparedStatement getPstmt() {
        return pstmt; 
    }
    
}
