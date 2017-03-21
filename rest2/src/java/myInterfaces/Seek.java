/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myInterfaces;

import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/**
 *
 * @author nilay jha
 */
public interface Seek {
    public Response seekId(@PathParam("skid")String skid);
    public Response seekRange(@QueryParam("st")String st,@QueryParam("en")String en);
}
