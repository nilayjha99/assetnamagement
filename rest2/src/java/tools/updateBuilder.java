/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;
import java.util.Map;

/**
 *
 * @author nilay jha
 */
public class updateBuilder {
    public static String createUpdateQuery(String tablename,Map<String,String> attr,String col,String id){
    String base="update "+tablename+" set ";
    String attrs = "";
    if(attr.size()==1){
    Object kys[]= attr.keySet().toArray();
    attrs=kys[0].toString();    
    base+=attrs+"="+getQuot(attrs);
    }
    else{
        int i=0;
    for(String key : attr.keySet()){
        if(i<attr.size()-1){
    attrs+=key+"="+getQuot(attr.get(key))+",";
        }
        else{
        attrs+=key+"="+getQuot(attr.get(key));
        }
        i++;
    }
    base+=attrs;
    }
    return base+" where "+col+"="+getQuot(id);
    }
    static String getQuot(String str){
    return "'"+str+"'";
    }
    /*public String createUpdateQuery(String tablename,HashMap<String,String> attr,String where){
    "update "+tablename+" set ";
    }*/
}
