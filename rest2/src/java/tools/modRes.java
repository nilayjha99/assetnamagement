/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;
import javax.ws.rs.core.Response;
/**
 *
 * @author nilay jha
 */
public class modRes {
    Response res=null;
    public static Response cbrs(String s){
        return Response.ok().entity(s).header("Access-Control-Allow-Origin", "*")
			.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
			.allow("OPTIONS").build();    
    }
}
