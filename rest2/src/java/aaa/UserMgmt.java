/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aaa;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.naming.NamingException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import tools.DbConnector;
import tools.PubObjs;
import tools.modRes;

/**
 *
 * @author nilay jha
 */
@Path("user")
public class UserMgmt {
        @Context
    private UriInfo context;
    private final DbConnector dao;

    public UserMgmt() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
            dao=new DbConnector(PubObjs.BURL, PubObjs.BUSR, PubObjs.BPAS);   
    }
    private PreparedStatement pst=null;
    
    @POST
    @Path("permit")
    @Produces(MediaType.TEXT_PLAIN)
    public Response loginsa(@QueryParam("id") String id) throws NamingException, SQLException{
    String res="error";
    dao.open();
    pst=dao.cn.prepareStatement("insert into userspermitted(iduserspermitted) values(?)");
    pst.setString(1, id);
    if(pst.executeUpdate()>0){
    res="user successfully permitted";
    }
    dao.cn.close();
    pst.close();
    return modRes.cbrs(res);
    }
    
     @POST
    @Path("delete")
    @Produces(MediaType.TEXT_PLAIN)
    public Response rmsa(@QueryParam("id") String id) throws NamingException, SQLException{
    String res="error";
    dao.open();
    pst=dao.cn.prepareStatement("delete from userspermitted where iduserspermitted=?");
    pst.setString(1, id);
    if(pst.executeUpdate()>0){
    res="user successfully removed";
    }
    dao.cn.close();
    pst.close();
    return modRes.cbrs(res);
    }
}
