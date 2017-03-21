/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

/**
 *
 * @author nilay jha
 */
import java.io.*;
import java.sql.*;
import com.google.gson.*;
import com.google.gson.stream.*;

public class ResultSetAdapter extends TypeAdapter<ResultSet> {
    public static class NotImplemented extends RuntimeException {}
    private static final Gson gson = new Gson();
    @Override
    public ResultSet read(JsonReader reader)
        throws IOException {
        throw new NotImplemented();
    }
    @Override
    public void write(JsonWriter writer, ResultSet rs)
        throws IOException {
        try {
            ResultSetMetaData meta = rs.getMetaData();
            int cc = meta.getColumnCount();

            writer.beginArray();
            while (rs.next()) {
                writer.beginObject();
                for (int i = 1; i <= cc; ++i) {
                    writer.name(meta.getColumnName(i));
                    Class<?> type = Class.forName(meta.getColumnClassName(i));
                    gson.toJson(rs.getObject(i), type, writer);
                    //writer.value(rs.getString(i));
                }
                writer.endObject();
            }
            writer.endArray();
            
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e.getClass().getName(), e);
        }
    }
}

//depricated code
/*
    public  JsonObject convertResultSetIntoJSON(ResultSet resultSet) throws Exception {
        JsonArray jsonArray = new JsonArray();
        while (resultSet.next()) {
            int total_rows = resultSet.getMetaData().getColumnCount();
            JsonObject obj = new JsonObject();
            for (int i = 0; i < total_rows; i++) {
                String columnName = resultSet.getMetaData().getColumnLabel(i + 1).toLowerCase();
                Object columnValue = resultSet.getObject(i + 1);
                // if value in DB is null, then we set it to default value
                if (columnValue == null){
                    columnValue = "null";
                }
                /*
                Next if block is a hack. In case when in db we have values like price and price1 there's a bug in jdbc - 
                both this names are getting stored as price in ResulSet. Therefore when we store second column value,
                we overwrite original value of price. To avoid that, i simply add 1 to be consistent with DB.
                // 
                if (obj.has(columnName)){
                    columnName += "1";
                }
                obj.add(columnName, (JsonElement) columnValue);
            }
            jsonArray.add(obj);
            
        }
         JsonObject obj1 = new JsonObject();
            obj1.add("data",jsonArray);
           
        return obj1;
    }*/