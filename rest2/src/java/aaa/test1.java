/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aaa;

import com.google.gson.stream.JsonWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
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
import tools.PubObjs;
import tools.modRes;
import tools.JsonMan;
import tools.updateBuilder;

/**
 *
 * @author nilay jha
 */

@Path("assets")
public class test1 {

    @Context
    private UriInfo context;
    /*  private String getasset="SELECT a.idassets, a.category,a.type,, b.tutorial_count\n" +
     "FROM tutorials_tbl a, tcount_tbl b\n" +
     "WHERE a.tutorial_author = b.tutorial_author;";*/
    private final DbConnector dao;
    private PreparedStatement pst = null;

    public test1() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException, NamingException {
        dao = new DbConnector(PubObjs.BURL, PubObjs.BUSR, PubObjs.BPAS);
    }

    @GET
    @Path("retrive")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAssetById(@QueryParam("id") String id) throws NamingException, SQLException {
        dao.open();
        if (id.equals("*")) {
            pst = dao.cn.prepareStatement("select idassets,category,type,issuedto,issuedon,returnstatus,reeturndate,deptid,dop,ponum,billno from assets");
        } else {
            pst = dao.cn.prepareStatement("select idassets,category,type,issuedto,issuedon,returnstatus,reeturndate,deptid,dop,ponum,billno from assets where idassets=?");
            pst.setString(1, id);
        }

        ResultSet qrs = pst.executeQuery();
        if (qrs != null) {
            return modRes.cbrs(JsonMan.rstojson(qrs));
        } else {
            return modRes.cbrs("sorry");
        }
    }

    @GET
    @Path("retprint")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAssetForPrint(@QueryParam("st") int st, @QueryParam("en") int en) throws NamingException, SQLException {
        dao.open();
        pst = dao.cn.prepareStatement("select idassets,category,type,deptid,qrcode from assets where idassets between ? and ?");
        pst.setInt(1, st);
        pst.setInt(2, en);
        ResultSet prt = pst.executeQuery();
        String s;
        try (StringWriter stringWriter = new StringWriter()) {
            JsonWriter writer = new JsonWriter(stringWriter);
            tools.ResultSetAdapter ra = new tools.ResultSetAdapter();
            ra.write(writer, prt);
            s = "{ \"data\" :" + stringWriter.getBuffer().toString() + "}";
            return modRes.cbrs(s);
        } catch (Exception e) {
            return modRes.cbrs(e.getMessage() + "");
        }
    }

    @POST
    @Path("addasset")
    @Produces(MediaType.TEXT_PLAIN)
    public Response insasset(@QueryParam("cat") String cat, @QueryParam("type") String typ,
            @QueryParam("qrh") String qr, @QueryParam("deptid") String depid, @QueryParam("dop") String dop,
            @QueryParam("po") String po, @QueryParam("bn") String bn
    )
            throws SQLException, IOException, NamingException, ParseException {

        String res = "failed to register \n";
        try {
            dao.open();

            pst = dao.cn.prepareStatement("select max(idassets) from assets");
            ResultSet a = pst.executeQuery();

            if (a.next()) {
                res += a;
            }

            pst = dao.cn.prepareStatement("insert into assets(category,type,qrcode,deptid,dop,ponum,billno) values(?,?,?,?,?,?,?)");
            pst.setString(1, cat);
            pst.setString(2, typ);
            File f = tools.qrgen.getQrcf("AST:" + (a.getInt(1) + 1) + "-" + qr);
            byte[] byt = new byte[(int) f.length()];
            try (FileInputStream fileInputStream = new FileInputStream(f)) {
                fileInputStream.read(byt);
            }
            pst.setString(3, Base64.getEncoder().encodeToString(byt));
            pst.setString(4, depid);
            Date utilDate = new SimpleDateFormat("yyyy-MM-dd").parse(dop);
            java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
            pst.setDate(5, sqlDate);
            pst.setString(6, po);
            pst.setString(7, bn);
            if (pst.executeUpdate() > 0) {
                res = "ASSET:" + (a.getInt(1) + 1) + "-" + qr;
            }

        } catch (SQLException | FileNotFoundException e) {
            res += e.toString();
        } finally {
            if (pst != null && dao != null) {
                pst.close();
                dao.cn.close();
            }
        }
        return modRes.cbrs(res);

    }

    @GET
    @Path("getqr")
    @Produces(MediaType.TEXT_PLAIN)
    public String makeQr(@QueryParam("qrtext") String qrtxt) throws IOException {
        //  String h=Base64.getEncoder().encodeToString(tools.qrgen.getQRc(qrtxt).toByteArray());
        File f = tools.qrgen.getQrcf("AST:" + "1-" + qrtxt);
        byte[] byt = new byte[(int) f.length()];
        try (FileInputStream fileInputStream = new FileInputStream(f)) {
            fileInputStream.read(byt);
        }
        return Base64.getEncoder().encodeToString(byt);
//encodeBase64URLSafeString(tools.qrgen.getQRc(qrtxt));
//          return tools.qrgen.getQrcf(qrtxt);
    }

    @GET
    @Path("getcat")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getcat(@QueryParam("a") String a) throws SQLException {
        String res = "error123";
        dao.open();
        pst = dao.cn.prepareStatement("select DISTINCT(category) from assettypes");
        ResultSet rs = pst.executeQuery();
        if (rs != null) {
            res = JsonMan.rstojson(rs);
        }
        return modRes.cbrs(res);
    }

    @GET
    @Path("gettyp")
    @Produces(MediaType.APPLICATION_JSON)
    public Response gettyp(@QueryParam("a") String a) throws SQLException {
        String res = "error";
        dao.open();
        pst = dao.cn.prepareStatement("select DISTINCT(type) from assettypes where category=?");
        pst.setString(1, a);
        ResultSet rs = pst.executeQuery();
        res = JsonMan.rstojson(rs);
        return modRes.cbrs(res);
    }

    @POST
    @Path("rm")
    @Produces(MediaType.TEXT_PLAIN)
    public Response rmasset(@QueryParam("rmid") String id) throws SQLException{
        String res="sorry some error occured";
       dao.open();
       pst=dao.cn.prepareStatement("delete from assets where idassets=?");
       pst.setString(1, id);
       if(pst.executeUpdate()>0){
           res="asset "+id+" was successfully removed";
       }
        if(dao.cn!=null && pst!=null){
        dao.cn.close();
        pst.close();
        }
       return modRes.cbrs(res);
   }
    
   @POST
   @Path("update")
   @Produces(MediaType.TEXT_PLAIN)
   public Response updateasset(@QueryParam("cat") String cat, @QueryParam("type") String typ,
             @QueryParam("deptid") String depid, @QueryParam("dop") String dop,
            @QueryParam("po") String po, @QueryParam("bn") String bn,@QueryParam("id") String id
    ) throws SQLException, ParseException{
    
   String res="some error occured";
   Map<String,String> upd=new HashMap<>();   
   if(!cat.equals(null) || !cat.equals("")){
    upd.put("category", cat);
   }
   if(!typ.equals(null) || !typ.equals("")){
    upd.put("type", typ);
   }
   if(!depid.equals(null) || !depid.equals("")){
    upd.put("deptid", depid);
   }
   if(!dop.equals(null) || !dop.equals("")){
       Date utilDate = new SimpleDateFormat("yyyy-MM-dd").parse(dop.replaceAll("[' \"]", ""));
            java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
            
    upd.put("dop", sqlDate.toString());
   }
   if(!po.equals(null) || !po.equals("")){
    upd.put("ponum", po);
   }
   if(!bn.equals(null) || !bn.equals("")){
    upd.put("billno", bn);
   }
   dao.open();
   Statement st=dao.cn.createStatement();
   String query=updateBuilder.createUpdateQuery("assets", upd,"idassets",id);
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
   
   @POST
   @Path("lend")
   @Produces(MediaType.TEXT_PLAIN)
   public Response lend(@QueryParam("asid") String id,@QueryParam("usid") String uid,@QueryParam("lendt") String dt) throws SQLException, ParseException{
   String res="error";
   dao.open();
   pst=dao.cn.prepareStatement("update assets set issuedto=?,issuedon=?,returnstatus=? where idassets=? ");
   pst.setString(1, uid);
   Date utilDate = new SimpleDateFormat("yyyy-MM-dd").parse(dt);
   java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
   pst.setDate(2, sqlDate);
   pst.setString(3, "N");
   pst.setString(4, id);
   if(pst.executeUpdate()>0){
   res="asset was successfully lended";
   }
   return modRes.cbrs(res);
   }
   
   @POST
   @Path("collect")
   @Produces(MediaType.TEXT_PLAIN)
   public Response collect(@QueryParam("asid") String id,@QueryParam("asdt") String dt) throws SQLException, ParseException{
   String res="error";
   dao.open();
   pst=dao.cn.prepareStatement("update assets set returnstatus=?,reeturndate=? where idassets=? ");
   pst.setString(1, "Y");
   Date utilDate = new SimpleDateFormat("yyyy-MM-dd").parse(dt);
   java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
   pst.setDate(2, sqlDate);
   pst.setString(3, id);
   if(pst.executeUpdate()>0){
   res="asset was successfully collected";
   }
   return modRes.cbrs(res);
       
   
   }
   @GET
   @Path("retdet")
   @Produces(MediaType.APPLICATION_JSON)
   public Response retdet(@QueryParam("cat") String cat,@QueryParam("typ") String typ) throws SQLException{
   String res="error";
   dao.open();
   pst=dao.cn.prepareStatement("select *from assettypes where category=? AND type=?");
   pst.setString(1, cat);
   pst.setString(2, typ);
   ResultSet rs=pst.executeQuery();
   if(rs.isBeforeFirst()){
   res=JsonMan.rstojson(rs);
   }
   return modRes.cbrs(res);
   }
}
