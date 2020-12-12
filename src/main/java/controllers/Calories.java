package controllers;

import server.Main;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.simple.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.Cookie;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

import static server.Convertor.convertToJSONObject;



@Path("calories/")
public class Calories{
    @GET
    @Path("getLowCalories")
    public String getLowCalories(@FormDataParam("Exercise") String exercise) {
        System.out.println("Invoked Food.getFood() with foodID " + exercise);
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT LowIntensityPerMin FROM Exercises WHERE ExerciseName = ?");
            ps.setString(1, exercise);
            ResultSet results = ps.executeQuery();
            JSONObject response = new JSONObject();
            if (results.next()== true) {
                response.put("LowIntensityPerMin", results.getInt(1));
            }
            return response.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to retrieve information, please report to server admin.\"}";
        }
    }
//    @GET
//    @Path("getMedCalories")
//    public String getMedCalories(@FormDataParam("Exercise") String exercise) {
//        System.out.println("Invoked Food.getFood() with foodID " + exercise);
//        try {
//            PreparedStatement ps = Main.db.prepareStatement("SELECT MedIntensityPerMin FROM Exercises WHERE ExerciseName = ?");
//            ps.setInt(1, exercise);
//            ResultSet results = ps.executeQuery();
//            JSONObject response = new JSONObject();
//            if (results.next()== true) {
//                response.put("FoodID", exercise);
//                response.put("Name", results.getString(1));
//                response.put("Quantity", results.getInt(2));
//            }
//            return response.toString();
//        } catch (Exception exception) {
//            System.out.println("Database error: " + exception.getMessage());
//            return "{\"Error\": \"Unable to get item, please see server console for more info.\"}";
//        }
//    }
//    @GET
//    @Path("getHighCalories")
//    public String getHighCalories(@FormDataParam("Exercise") String exercise) {
//        System.out.println("Invoked Food.getFood() with foodID " + exercise);
//        try {
//            PreparedStatement ps = Main.db.prepareStatement("SELECT HighIntensityPerMin FROM Exercises WHERE ExerciseName = ?");
//            ps.setInt(1, exercise);
//            ResultSet results = ps.executeQuery();
//            JSONObject response = new JSONObject();
//            if (results.next()== true) {
//                response.put("FoodID", exercise);
//                response.put("Name", results.getString(1));
//                response.put("Quantity", results.getInt(2));
//            }
//            return response.toString();
//        } catch (Exception exception) {
//            System.out.println("Database error: " + exception.getMessage());
//            return "{\"Error\": \"Unable to get item, please see server console for more info.\"}";
//        }
//    }

}
