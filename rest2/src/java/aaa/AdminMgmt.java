/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aaa;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import java.sql.PreparedStatement;
import java.io.StringWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.naming.NamingException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import tools.modRes;
import tools.DbConnector;
import tools.JsonMan;
import tools.PubObjs;
import tools.updateBuilder;
/**
 *
 * @author nilay jha
 */
@Path("admin")
public class AdminMgmt {
    @Context
    private UriInfo context;
    private DbConnector dao;
    private PreparedStatement pst=null;
    public AdminMgmt() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException, NamingException{
                         dao=new DbConnector(PubObjs.BURL, PubObjs.BUSR, PubObjs.BPAS);
   
    }
    @Path("rega")
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public Response reguser(@QueryParam("fname") String fname, @QueryParam("lname") String lname,
            @QueryParam("eml") String email, @QueryParam("psswd") String psswd, @QueryParam("dob") String dob, @QueryParam("dept") String dept
    ) throws SQLException {
        Connection cn = null;
        CallableStatement cst = null;
        try {
            dao.open();
            cst = dao.cn.prepareCall("{call ins_admin(?,?,?,?,?,?,?)}");
            cst.setString(1, fname);
            cst.setString(2, lname);
            cst.setString(3, email);
            cst.setString(4, psswd);
            Date utilDate = new SimpleDateFormat("yyyy-MM-dd").parse(dob);
            java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
            cst.setDate(5, sqlDate);
            cst.setString(6, dept);
            cst.registerOutParameter(7, java.sql.Types.INTEGER);
      
            cst.executeUpdate();
            int val = cst.getInt(7);
            if (val == 1) {
               return modRes.cbrs("operation successfully done");
            }

        } catch (SQLException | ParseException e) {
           return modRes.cbrs(e.getMessage());
        }
        finally{
        if(cst!=null && cn!=null){    
        cst.close();
        dao.cn.close();
        }
        }
           return modRes.cbrs("sorry");
        
    }
    
    
    @GET
    @Path("login")
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@QueryParam("user") String uid,@QueryParam("psswd") String psswd) throws NamingException, SQLException
    {
        dao.open();
         pst=dao.cn.prepareStatement("select *from admin where email=? AND password=?");
         String res = null;
         pst.setString(1, uid);
         pst.setString(2, psswd);
         ResultSet rs=pst.executeQuery();
         if(rs != null){
            rs.next();
             //if(rs.getString(4).equals(uid) && rs.getString(5).equals(psswd)){
               rs.previous();
             Gson gson = new Gson();
            try (StringWriter stringWriter = new StringWriter()) {
                JsonWriter writer = new JsonWriter(stringWriter);
                tools.ResultSetAdapter ra=new tools.ResultSetAdapter();        
                 ra.write(writer, rs);
                 res="{ \"data\" :"+stringWriter.getBuffer().toString()+"}";
             }catch(Exception e){
                 res=e.getMessage()+"";
             }
            finally{
            if(pst!=null && dao.cn!=null){
            rs.close();
            pst.close();
            dao.cn.close();
            }
            //}
             }
             
         }
    return modRes.cbrs(res);    
    
    }
    
    
    @POST
    @Path("rmad")
    @Produces(MediaType.TEXT_PLAIN)
    public Response remove(@QueryParam("rmid") String rmid) throws SQLException
    {
    String res="sorry some error occured";
       dao.open();
       pst=dao.cn.prepareStatement("delete from admin where idsuperadmin=?");
       pst.setString(1, rmid);
       if(pst.executeUpdate()>0){
           res="superadmin "+rmid+"was successfully removed";
       }
       return modRes.cbrs(res);
    }
    
   @POST
   @Path("edadm")
   @Produces(MediaType.TEXT_PLAIN)
   public Response edsu(@QueryParam("id")String idsu,@QueryParam("firstname") String fname, @QueryParam("lastname") String lname,
            @QueryParam("email") String email, @QueryParam("password") String psswd, @QueryParam("dob") String dob,@QueryParam("dept") String dept
    ) throws SQLException, ParseException, NamingException{
   Map<String,String> upd=new HashMap<>();   
   String res="error";
   if(!(fname == null) || !fname.equals("")){
    upd.put("firstname", fname);
   }
   if(!(lname == null) || !lname.equals("")){
    upd.put("lastname", lname);
   }
   if(!(email == null) || !email.equals("")){
    upd.put("email", email);
   }
   if(!(psswd == null) || !psswd.equals("")){
    upd.put("password", psswd);
   }
   if(!(dob == null) || !dob.equals("")){
     Date utilDate = new SimpleDateFormat("yyyy-MM-dd").parse(dob);
     java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
       upd.put("dob", sqlDate.toString());
   }
   if(!(dept==null) || !dob.equals("")){
       upd.put("department", dept);
   }
   dao.open();
   Statement st=dao.cn.createStatement();
   if(st.executeUpdate(updateBuilder.createUpdateQuery("admin", upd,"idsuperadmin",idsu))>1){
   res="update successful";
   }
   if(dao.cn!=null){
   st.close();
   dao.cn.close();
   }
   return modRes.cbrs(dob);
   }
   @GET
   @Path("seekid")
  public Response seekId(@QueryParam("skid")String skid) throws NamingException, SQLException{
      String res="error";
      ResultSet a;
         dao.open();
      if(skid.equals("*")){
          Statement st=dao.cn.createStatement();
          a=st.executeQuery("select *from admin");
          res=JsonMan.rstojson(a);
          if(!st.isClosed()){st.close();}
      }
      else{
          pst=dao.cn.prepareStatement("select *from admin where idsuperadmin=?");
          pst.setString(1, skid);
          a=pst.executeQuery();
          res=JsonMan.rstojson(a);
          if(dao.cn!=null && pst!=null){dao.cn.close();pst.close();}
      }
      if(a!=null){a.close();}
  return modRes.cbrs(res);
  }
  @GET
  @Path("seek")
   public Response seekRange(@QueryParam("st")String st,@QueryParam("en")String en) throws SQLException{
   String res="error";
      ResultSet a;
      dao.open();
       pst=dao.cn.prepareStatement("select *from admin where idsuperadmin>=? AND idsuperadmin<=?");
          pst.setString(1, st);
          pst.setString(2, en);
          a=pst.executeQuery();
          res=JsonMan.rstojson(a);
          if(dao.cn!=null && pst!=null){dao.cn.close();pst.close();}
          if(a!=null){a.close();}
       return modRes.cbrs(res);
   }

   
    
}
