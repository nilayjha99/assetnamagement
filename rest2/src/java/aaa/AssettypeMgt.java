/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aaa;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import java.sql.PreparedStatement;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
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
import tools.JsonMan;
import tools.PubObjs;
import tools.modRes;
import tools.updateBuilder;

/**
 *
 * @author nilay jha
 */
@Path("astmgt")
public class AssettypeMgt {
        @Context
    private UriInfo context;
    private final DbConnector dao;
   private  CallableStatement cst = null;
    private    PreparedStatement pst=null;
    private    ResultSet rs=null;
   
    public AssettypeMgt() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException, NamingException {
    dao=new DbConnector(PubObjs.BURL, PubObjs.BUSR, PubObjs.BPAS);
    }
   // {for async} private ExecutorService executorService = java.util.concurrent.Executors.newCachedThreadPool();
   
    @GET
    @Path("get")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getdetails(@QueryParam("id") String id) throws SQLException, IOException, Exception{
        String res="error";
         try {
            dao.open();
            if(id.equals("*")){
            pst=(PreparedStatement) dao.cn.prepareStatement("select *from assettypes");
            rs=pst.executeQuery();
            }
            else{
            pst=(PreparedStatement) dao.cn.prepareStatement("select *from assettypes where idassettypes=?");
            pst.setString(1, id);
            rs=pst.executeQuery();
            }
            Gson gson = new Gson();
            try {
            if(rs.isBeforeFirst() ){
                res=JsonMan.rstojson(rs);}
            }
            catch(Exception er){
                return modRes.cbrs(res+":"+er.getMessage());
            }
            return modRes.cbrs(res);
         } catch (SQLException e) {
            return modRes.cbrs(e.getMessage()+"");
//encod
        }
        finally{
        if(cst!=null && dao.cn!=null){    
        pst.close();
        dao.cn.close();
        }
        }
    }
    
    @POST
    @Path("add")
    @Produces(MediaType.TEXT_PLAIN)
    public Response addastyp(@QueryParam("cat") String cat,@QueryParam("type") String type,@QueryParam("info") String info,@QueryParam("rate") String rate) throws SQLException, NamingException{
     try {
        /*    Class.forName("com.mysql.jdbc.Driver").newInstance();
            cn = DriverManager.getConnection("jdbc:mysql://localhost:3306/assetmgmt?zeroDateTimeBehavior=convertToNull", "nilay", "Nilay@123");
         */
        
         dao.open();
         cst = dao.cn.prepareCall("{call ins_assettype(?,?,?,?,?)}");
            cst.setString(1, cat);
            cst.setString(2, type);
            cst.setObject(3, info);
            cst.setInt(4, Integer.parseInt(rate));
            cst.registerOutParameter(5, java.sql.Types.INTEGER);
            cst.executeUpdate();
            int val = cst.getInt(5);
            if (val > 0) {
                return modRes.cbrs("operation successfully done");
            }

        } catch (SQLException e) {
            return modRes.cbrs(e.getMessage() + "");
        }
        finally{
        if(cst!=null && dao.cn!=null){    
        cst.close();
        dao.cn.close();
        }
        }
        return modRes.cbrs("sorry");
    }
    
   @POST
   @Path("edastype")
   @Produces(MediaType.TEXT_PLAIN)
   public Response edsu(@QueryParam("id")String idastype,@QueryParam("cat") String cat, @QueryParam("type") String type,
            @QueryParam("info") String info,@QueryParam("rate") String rate
    ) throws SQLException, ParseException, NamingException{
   Map<String,String> upd=new HashMap<>();   
   String res="error";
   if(!cat.equals(null) || !cat.equals("")){
    upd.put("category", cat);
   }
   if(!type.equals(null) || !type.equals("")){
    upd.put("type", type);
   }
   if(!info.equals(null) || !info.equals("")){
    upd.put("details", info);
   }
   if(!rate.equals(null) || !rate.equals("")){
    upd.put("rate", rate);
   }
   dao.open();
   Statement st=dao.cn.createStatement();
   String query=updateBuilder.createUpdateQuery("assettypes", upd,"idassettypes",idastype);
   if(st.executeUpdate(query)>0){
   res="update successful";
   }
   else{
   res=query;
   }
   if(dao.cn!=null){
   st.close();
   dao.cn.close();
   }
   return modRes.cbrs(res);
   }
   
   
  @GET
  @Path("seek")
   public Response seekRange(@QueryParam("st")String st,@QueryParam("en")String en) throws SQLException{
   String res="error";
      ResultSet a;
      dao.open();
       pst=dao.cn.prepareStatement("select *from assettypes where idassettypes>=? AND idassettypes<=?");
          pst.setString(1, st);
          pst.setString(2, en);
          a=pst.executeQuery();
          res=JsonMan.rstojson(a);
          if(dao.cn!=null && pst!=null){dao.cn.close();pst.close();}
          if(a!=null){a.close();}
       return modRes.cbrs(res);
   }

   @POST
   @Path("rmastype")
   @Produces(MediaType.TEXT_PLAIN)
   public Response rmsu(@QueryParam("rmid") String rmid) throws NamingException, SQLException{
       String res="sorry some error occured";
       dao.open();
       pst=dao.cn.prepareStatement("delete from assettypes where idassettypes=?");
       pst.setString(1, rmid);
       if(pst.executeUpdate()>0){
           res="assettype "+rmid+" was successfully removed";
       }
        if(dao.cn!=null && pst!=null){
        dao.cn.close();
        pst.close();
        }
       return modRes.cbrs(res);
   }
}

    
/*public void addAssettype(@Suspended
    final AsyncResponse asyncResponse) {
        executorService.submit(new Runnable() {
            public void run() {
                asyncResponse.resume(doAddAssettype());
            }
        });
    }
//to make it async
    private String doAddAssettype();*/

