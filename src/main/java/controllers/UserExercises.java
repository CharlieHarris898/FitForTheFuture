package controllers;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import server.Main;

import javax.ws.rs.*;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static server.Convertor.convertToJSONObject;


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
            return "{\"Error\": \"Error addExercise() to database. Seek help of server admin.\"}";

        }
    }

    @GET
    @Path("getInfo")
    public String getInfo(@CookieParam("token") Cookie token) {

        System.out.println("Invoked userExercises.getInfo()");
        int userID;
        if (token == null) {
            return "{\"Error\": \"Something has gone wrong.  Please contact the administrator with the error code UC-UG. \"}";
        } else{
            userID=validateToken(token);
        }
        if (userID == -1){
            return "{\"Error\": \"Something has gone wrong.  Please contact the administrator with the error code UC-UG. \"}";
        }

        JSONArray response = new JSONArray();
        try {
            PreparedStatement statement = Main.db.prepareStatement("SELECT Exercise, Duration, Intensity, CaloriesBurned, Date FROM UserExercises WHERE UserID = ?");
            statement.setInt(1, userID);
            ResultSet results = statement.executeQuery();
            while (results.next()==true){
                JSONObject row = new JSONObject();
                row.put("Exercise", results.getString(1));
                row.put("Duration", results.getInt(2));
                row.put("Intensity", results.getString(3));
                row.put("CaloriesBurned", results.getInt(4));
                row.put("Date", results.getString(5));
                response.add(row);
            }
            System.out.println(response.toString());
            return response.toString();



//            statement.setInt(1, userID);
//            ResultSet resultSet = statement.executeQuery();
//            JSONObject resultsJSON = convertToJSONObject(resultSet);
//            System.out.println(resultsJSON);
//            return resultsJSON.toString();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "{\"Error\": \"Something has gone wrong listing your previous exercsies, please contact a server admin \"}";
        }
    }




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
            return resultSet.getInt("UserID");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return -1;  //rogue value indicating error

        }
    }

}
