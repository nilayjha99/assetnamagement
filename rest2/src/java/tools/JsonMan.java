/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import com.google.gson.stream.JsonWriter;
import java.io.StringWriter;
import java.sql.ResultSet;

/**
 *
 * @author nilay jha
 */
public class JsonMan {
   public static String rstojson(ResultSet victim){
        try (StringWriter stringWriter = new StringWriter()) {
                JsonWriter writer = new JsonWriter(stringWriter);
                tools.ResultSetAdapter ra=new tools.ResultSetAdapter();        
                 ra.write(writer, victim);
                 return "{ \"data\" :"+stringWriter.getBuffer().toString()+"}";
        }
        catch(Exception e){
        return "error";
        }
        
    
    }
    
}
