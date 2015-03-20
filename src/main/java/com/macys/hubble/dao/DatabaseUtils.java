package com.macys.hubble.dao;

import com.google.gson.*;
import com.google.gson.internal.bind.DateTypeAdapter;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import javax.xml.transform.Result;
import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * Created by vivek on 1/22/15.
 */
public class DatabaseUtils {

    public static String resultSetToJson(Connection connection, String query) {
        List<Map<String, Object>> listOfMaps = null;
        try {
            QueryRunner queryRunner = new QueryRunner();

            listOfMaps = queryRunner.query(connection, query, new MapListHandler());
            System.out.println("Resultset Size:"+listOfMaps.size());
            System.out.println("SQL Query: "+query);
        } catch (SQLException se) {
            throw new RuntimeException("Couldn't query the database.", se);
        } finally {
            DbUtils.closeQuietly(connection);
        }
        return toJSONString(listOfMaps);
    }

    public static int executeCount(Connection connection, String query) {
        int count=0;
        try {
            Statement stmt = connection.createStatement();
            stmt.setFetchSize(1000);
            ResultSet rs = stmt.executeQuery(query);
            rs.next();
            count = rs.getInt(1);
            rs.close();
        } catch (SQLException se) {
            throw new RuntimeException("Couldn't query the database.", se);
        } finally {
            DbUtils.closeQuietly(connection);
        }
        return count;
    }

    public static String resultSetToJOOQJson(Connection connection, String query){
        String rsJson = "";
        try {
            ResultSet rs = connection.createStatement().executeQuery(query);
            ObjectMapper mapper = new ObjectMapper();
            rsJson = mapper.writeValueAsString(rs);

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rsJson;
    }
    public static Connection getDBConnection() {
        String DB_CONN_STRING = "jdbc:oracle:thin:@sl99d0z1:1521:sdt2mcom";
        String DRIVER_CLASS_NAME = "oracle.jdbc.OracleDriver";
        String USER_NAME = "sdt";
        String PASSWORD = "sdt";

        Connection result = null;
        try {
            Class.forName(DRIVER_CLASS_NAME).newInstance();
        }
        catch (Exception ex){
            log("Check classpath. Cannot load db driver: " + DRIVER_CLASS_NAME);
        }

        try {
            result = DriverManager.getConnection(DB_CONN_STRING, USER_NAME, PASSWORD);
        }
        catch (SQLException e){
            log( "Driver loaded, but cannot connect to db: " + DB_CONN_STRING);
        }
        return result;
    }

    private static void log(Object aObject){
        System.out.println(aObject);
    }

    public static String toJSONString(Object object) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("yyyy-MM-dd");
        Gson gson = gsonBuilder.create();
        System.out.println(object.toString());
        return gson.toJson(object);
    }


}
