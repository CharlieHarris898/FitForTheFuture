package controllers;

import org.glassfish.jersey.media.multipart.FormDataParam;
import server.Main;

import javax.ws.rs.*;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import java.sql.PreparedStatement;


@Path("userexercises/")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)


public class UserExercises {

    @POST
    @Path("add")

    // caloriesBurned has to be String as it's not a whole number!!!!

    public String add(@CookieParam("token") Cookie token,
                      @FormDataParam("exercise") String exercise,
                      @FormDataParam("time") Integer duration,
                      @FormDataParam("intensity") String intensity,
                      @FormDataParam("caloriesBurned") String caloriesBurned,
                      @FormDataParam("date") String date){

        System.out.println("Invoked userexercises.add()");

        int userID = UserInformation.validateToken(token);
        if (userID == -1){
            return "{\"Error\": \"Login Status Error. Please Login Again.\"}";
        }
        try {
            PreparedStatement ps = Main.db.prepareStatement("INSERT INTO UserExercises (UserID, Exercise, Duration, Intensity, CaloriesBurned, Date) VALUES (?, ?, ?, ?, ?, ?)");
            ps.setInt(1, userID);
            ps.setString(2, exercise);
            ps.setInt(3, duration);
            ps.setString(4, intensity);
            ps.setString(5, caloriesBurned);
            ps.setString(6, date);
            ps.execute();
            return "{\"OK\": \"Calorie Calculation Complete.\"}";
        }
        catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Error add exercise to database. Seek help of server admin.\"}";

        }
    }

}
