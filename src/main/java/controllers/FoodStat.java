package controllers;

import server.Main;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;



@Path("FoodStat")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)

public class FoodStat {
    @GET
    @Path("list")
    public String FoodStatList() {
        System.out.println("Invoked FoodStat.FoodStatList()");
        JSONArray response = new JSONArray();
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT FoodID, Name, SugarPer100, FatPer100, CaloriePer100 FROM FoodStat");
            ResultSet results = ps.executeQuery();
            while (results.next() == true) {
                JSONObject row = new JSONObject();
                row.put("FoodID", results.getInt(1));
                row.put("Name", results.getString(2));
                row.put("SugarPer100", results.getDouble(3));
                row.put("FatPer100", results.getDouble(4));
                row.put("CaloriePer100", results.getDouble(5));
                response.add(row);
            }
            return response.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to list items.  Error code xx.\"}";
        }
    }
}



