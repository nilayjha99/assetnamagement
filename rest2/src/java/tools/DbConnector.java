/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;
import java.sql.*;
import java.util.Properties;
import myInterfaces.Dbinf;
/**
 *
 * @author nilay jha
 */
public class DbConnector implements Dbinf{
    public Connection cn;
    private final String Url;
    private final Properties props;
    public DbConnector(String Url,String uname,String psswd) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException{
        Class.forName("com.mysql.jdbc.Driver").newInstance();   
        props = new java.util.Properties();
        this.Url=Url;
        props.put("user",uname);
        props.put("password",psswd);
    }
    @Override
    public void open() throws SQLException {
        cn = DriverManager.getConnection(Url, props);
    }

    @Override
    public void close(Connection cn) throws SQLException {
        if(cn!=null)
        {
        cn.close();
        }
    }

    @Override
    public void setAutoCommit(boolean opt) throws SQLException {
        cn.setAutoCommit(opt);
    }

    @Override
    public void commit() throws SQLException {
        cn.commit();
    }

    @Override
    public void rollback() throws SQLException {
        cn.rollback();
    }

    @Override
    public void rollbackto(Savepoint s) throws SQLException {
        cn.rollback(s);
    }

}
