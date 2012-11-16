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
    public static Boolean daolock;
    
    static {
        daolock = false;
        createConnection();
    }
    
/*    public static void deleteDAO() {
        closeConnection(); //This will release all resources
    }
*/    
    //set parameter methods
    public synchronized void setObject(int parameterIndex, Object x) {
        
        try {
            if (daolock)
            getPstmt().setObject(parameterIndex, x);
        }
        catch (SQLException sqle) {
            System.err.println(" pstmt.setObject(parameterIndex, x) Fail! Error = " + sqle.toString());
        }
        
    }
    
    public synchronized void setBoolean(int parameterIndex, boolean flag) {
        
        try {
            if (daolock)
            getPstmt().setBoolean(parameterIndex, flag);
        }
        catch (SQLException sqle) {
            System.err.println(" pstmt.setBoolean(parameterIndex, flag) Fail! Error = " + sqle.toString());
        }
        
    }
    
    public synchronized void setDouble(int parameterIndex, double x) {
        
        try {
            if (daolock)
            getPstmt().setDouble(parameterIndex, x);
        }
        catch (SQLException sqle) {
            System.err.println(" pstmt.setDouble(parameterIndex, x) Fail! Error = " + sqle.toString());
        }
        
    }
    
    public synchronized void setInt(int parameterIndex, int x) {
        
        try {
            if (daolock)
            getPstmt().setInt(parameterIndex, x);
        }
        catch (SQLException sqle) {
            System.err.println(" pstmt.setInt(parameterIndex, x) Fail! Error = " + sqle.toString());
        }
        
    }
    
    public synchronized void setString(int parameterIndex, String s) throws DAOException {
        
        try {
            if (daolock)
            getPstmt().setString(parameterIndex, s);
        }
        catch (SQLException sqle) {
            System.err.println(" pstmt.setString(parameterIndex, s) Fail! Error = " + sqle.toString() + " SQL state?" + getPstmt().toString());
            throw new DAOException("DAOException setString:", sqle);
        }
    }
    
    public synchronized boolean executeUpdate() throws DAOException {
        boolean flag = true;
        try {
            if (daolock) {
                getPstmt().executeUpdate();
                daolock = false;
                System.err.println("Update informaiton:" + getPstmt().toString());
                pstmt = null;
            } else {
                throw new DAOException("DAOException, sql statement not inited");
            }
        }
        catch (SQLException sqle) {
            flag = false;
            System.out.println("executeUpdate() Error!" + sqle.toString() + getPstmt().toString());
            throw new DAOException("DAOException of udpate:", sqle);
        }
        
        return flag;
        
    }
    
    public synchronized void update(String expression) throws DAOException{
        pstmt = null;
        try {
            if (!daolock) {
                pstmt = conn.prepareStatement(expression);
                daolock = true;
            } else {
                throw new DAOException("Othser statemetn is waiting to be executed! " + pstmt.toString());
            }
        } 
        catch (SQLException sqle) {
            System.err.println("Update DataBase Preparation Fail! Error = " + sqle.toString());
        }
    }
    
    public synchronized ResultSet executeQuery() throws DAOException {
        rs = null;
        try {
            if (daolock) {
                rs = getPstmt().executeQuery();
                daolock = false;
                pstmt = null;
            } else {
                throw new DAOException("DAOException when query, sql statement not initiated");
            }
        }
        catch (SQLException sqle) {
            System.err.println("Query DataBase Fail! Error = " + sqle.toString());
            throw new DAOException("DAO query exception:", sqle);
        }
        return rs;
    }
        
    public synchronized void query(String expression) throws DAOException {
        pstmt = null;
        try {
            if (!daolock) {
                pstmt = conn.prepareStatement(expression);
                daolock = true;
            } else {
                throw new DAOException("Other statement is waiting to be executed!" + pstmt.toString());
            }
        } 
        catch (SQLException sqle) {
            System.err.println("Query DataBase Fail! Error = " + sqle.toString() + " " + expression);
            throw new DAOException("DAO query prepare exception", sqle);
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
    
    public synchronized void commit() throws DAOException {
        
         try {
            conn.commit();
        }
        catch (SQLException sqle) {
            System.err.println(sqle.toString());
            throw new DAOException("DAOException commit:", sqle);
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
                conn = DriverManager.getConnection(Res.DATABASE_ADDRESS + ";shutdown=true", Res.getProp().getString("dbusername"), Res.getProp().getString("dbpassword"));
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
class DAOException extends Exception {
  public DAOException() {
  }
 
  public DAOException(String msg) {
    super(msg);
  }
  public DAOException(String msg, Exception e) {
      super(msg, e);
  }
}
