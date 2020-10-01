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



@Path("Exercises")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)

public class Exercises {
    @GET
    @Path("list")
    public String ExercisesList() {
        System.out.println("Invoked Exercises.ExercisesList()");
        JSONArray response = new JSONArray();
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT ExerciseID, ExerciseName, LowIntensityPerMin, MedIntensityPerMin, HighIntensityPerMin FROM Exercises");
            ResultSet results = ps.executeQuery();
            while (results.next() == true) {
                JSONObject row = new JSONObject();
                row.put("ExerciseID", results.getInt(1));
                row.put("ExercisesName", results.getString(2));
                row.put("LowIntensityPerMin", results.getDouble(3));
                row.put("MedIntensityPerMin", results.getDouble(4));
                row.put("HighIntensityPerMin", results.getDouble(5));
                response.add(row);
            }
            return response.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to list items.  Error code xx.\"}";
        }
    }
}