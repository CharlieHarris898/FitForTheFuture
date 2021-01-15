package controllers;

import server.Main;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.simple.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.UUID;

import static server.Convertor.convertToJSONObject;



@Path("calories/")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)


public class Calories{

    @POST
    @Path("get")
    public String getCalories(@FormDataParam("Exercise") String exercise, @FormDataParam("Intensity") String intensity) {

        System.out.println("Invoked Calories.getCaloriesPerMin() with Exercise Name " + exercise);

        String column = "";
        if (intensity.equals("low")) {
            column = "LowIntensityPerMin";
            System.out.println("Low calories selected");
        }else if (intensity.equals("medium")) {
            column = "MedIntensityPerMin";
            System.out.println("Medium calories selected");
        } else {
            column = "HighIntensityPerMin";
            System.out.println("High calories selected");
        }

       String query = "SELECT " + column + " FROM Exercises WHERE ExerciseName = '" + exercise + "' ";

        int calsPerMin = 0;
        try  ( Statement stmt = Main.db.createStatement() ){
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next() == true) {
                calsPerMin = rs.getInt(column);
            }

            return "{\"CalsPerMin\":"  + calsPerMin + "}";


        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to retrieve information, please report to server admin.\"}";
        }
    }


}
