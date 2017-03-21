/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myInterfaces;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import javax.naming.NamingException;

/**
 *
 * @author nilay jha
 */
public interface Dbinf {
    public void open()throws NamingException,SQLException;
    public void close(Connection cn) throws SQLException;
    public void setAutoCommit(boolean opt) throws SQLException;
    public void commit() throws SQLException;
    public void rollback() throws SQLException;
    public void rollbackto(Savepoint s) throws SQLException;
    }
