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



@Path("userexercises/")
public class UserExercises {

    public static int validateToken(Cookie Token) {     //returns the userID that of the record with the cookie value

        String uuid = Token.getValue();
        System.out.println("Invoked User.validateToken(), Token value: " + uuid);

        try {
            PreparedStatement statement = Main.db.prepareStatement(
                    "SELECT UserID FROM UserInformation WHERE Token = ?"
            );
            statement.setString(1, uuid);
            ResultSet resultSet = statement.executeQuery();
            System.out.println("UserID is " + resultSet.getInt("UserID"));
            return resultSet.getInt("UserID");  //Retrieve by column name  (should really test we only get one result back!)
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return -1;  //rogue value indicating error

        }
    }

    @POST
    @Path("updatecalories")
    public String updateUserExercises(@FormDataParam("exercise") String exercise, @FormDataParam("intensity") String intensity, @FormDataParam("calories") int calories, @CookieParam("token") Cookie token){
        System.out.println("Invoked userexercises.updateCalories()");
        int userID = validateToken(token);
        if (userID == -1){
            return "{\"Error\": \"Login Status Error. Please Login Again.\"}";
        }
        try {
            PreparedStatement ps = Main.db.prepareStatement("INSERT INTO UserExercises (UserID, Exercise, Intensity, Calories) VALUES (?, ?, ?, ?)");
            ps.setInt(1, userID);
            ps.setString(2, exercise);
            ps.setString(3, intensity);
            ps.setInt(4, calories);
            ps.execute();
            return "{\"OK\": \"Calorie Calculation Complete.\"}";
        }
        catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Error calculating calories. Seek help of server admin.\"}";

        }
    }

}
