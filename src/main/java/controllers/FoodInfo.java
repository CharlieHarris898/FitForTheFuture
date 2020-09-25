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



@Path("FoodInfo")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)

public class FoodInfo {
    @GET
    @Path("list")
    public String FoodInfoList() {
        System.out.println("Invoked FoodInfo.FoodInfoList()");
        JSONArray response = new JSONArray();
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT FoodID, Vegetarian, Vegan, Carbohydrate, FruitAndVeg, Dairy, Protein, Fat FROM FoodInfo");
            ResultSet results = ps.executeQuery();
            while (results.next() == true) {
                JSONObject row = new JSONObject();
                row.put("FoodID", results.getInt(1));
                row.put("Vegetarian", results.getBoolean(2));
                row.put("Vegan", results.getBoolean(3));
                row.put("Carbohydrate", results.getBoolean(4));
                row.put("FruitAndVeg", results.getBoolean(5));
                row.put("Dairy", results.getBoolean(6));
                row.put("Protein", results.getBoolean(7));
                row.put("Fat", results.getBoolean(8));
                response.add(row);
            }
            return response.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to list items.  Error code xx.\"}";
        }
    }
}