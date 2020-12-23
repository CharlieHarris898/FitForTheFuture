package controllers;

import server.Main;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.simple.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

import static server.Convertor.convertToJSONObject;



@Path("userinformation/")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)

public class UserInformation {

    @POST
    @Path("login")
    public String loginUser(@FormDataParam("Username") String username, @FormDataParam("Password") String password) {
        System.out.println("Invoked loginUser() on path userInformation/login");
        try {
            PreparedStatement statement = Main.db.prepareStatement("SELECT UserID FROM UserInformation WHERE Username = ? AND Password = ?");
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet results = statement.executeQuery();
            //if there is not record with this email and password, condition below will be false
            if (results.next() == false) {
                return "{\"Error\": \"Username or password is incorrect.  Are you sure you've registered? \"}";
            } else {
                int userID = results.getInt("UserID");          //take the userId from the record returned in results
                String token = UUID.randomUUID().toString();                 //create a unique ID for session

                if (isTokenSetInDB(userID, token) == true) {   //store token for the user in the database
                    JSONObject cookie = new JSONObject();
                    cookie.put("token", token);
                    return cookie.toString();
                } else {
                    return "{\"Error\": \"Something as gone wrong.  Please contact the administrator with the error code UC-UL. \"}";
                }
            }
        } catch (Exception exception) {
            System.out.println("Database error during /userInformation/login: " + exception.getMessage());
            return "{\"Error\": \"Server side error!\"}";
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



    @POST
    @Path("add")
    public String UserInformationAdd(@FormDataParam("Username") String username, @FormDataParam("Password") String password, @FormDataParam("Gender") String gender, @FormDataParam("DOB") String dob, @FormDataParam("Height") Integer height, @FormDataParam("Weight") Integer weight){
        System.out.println("Invoked userInformation.submitUser()");
        try {
            PreparedStatement UsernameCheck = Main.db.prepareStatement("SELECT Username FROM UserInformation WHERE Username = (?)");
            UsernameCheck.setString(1, username);
            ResultSet result = UsernameCheck.executeQuery();
            JSONObject response = new JSONObject();
            if (result.next()== true) {
                return "{\"Error\": \"Sorry, that Username is already taken, please try again.\"}";
            }
        }
        catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Check server log for information\"}";

        }
        try {
            PreparedStatement RegisterSQL = Main.db.prepareStatement("INSERT INTO UserInformation (Username, Password, Gender, DOB, Height, Weight) VALUES (?, ?, ?, ?, ?, ?) ");
            RegisterSQL.setString(1, username);
            RegisterSQL.setString(2, password);
            RegisterSQL.setString(3, gender);
            RegisterSQL.setString(4, dob);
            RegisterSQL.setInt(5, height);
            RegisterSQL.setInt(6, weight);

            RegisterSQL.execute();
            return "{\"OK\": \"Added user.\"}";
            }
        catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Sorry, something went wrong making your account, please try again.\"}";
            }
    }



    @GET
    @Path("getUser")
    public String getUser(@CookieParam("token") Cookie token) {

        System.out.println("Invoked User.userGet()");
        int userID;
        if (token == null) {
            return "{\"Error\": \"Something has gone wrong.  Please contact the administrator with the error code UC-UG. \"}";
        } else{
            userID=validateToken(token);
        }
        if (userID == -1){
            return "{\"Error\": \"Something has gone wrong.  Please contact the administrator with the error code UC-UG. \"}";
        }

        try {
            PreparedStatement statement = Main.db.prepareStatement(
                    "SELECT * FROM UserInformation WHERE UserID = ?"
            );
            statement.setInt(1, userID);
            ResultSet resultSet = statement.executeQuery();
            JSONObject resultsJSON = convertToJSONObject(resultSet);
            return resultsJSON.toString();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "{\"Error\": \"Something as gone wrong.  Please contact the administrator with the error code UC-UG. \"}";
        }
    }

    private static boolean isTokenSetInDB(int userID, String token) {
        System.out.println("Invoked isTokenSetInDB()");

        try {
            PreparedStatement statement = Main.db.prepareStatement("UPDATE UserInformation SET Token = ? WHERE UserID = ?"
            );
            statement.setString(1, token);
            statement.setInt(2, userID);
            statement.executeUpdate();
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    @POST
    @Path("updateusername/{newName}")
    public String updateUsername(@PathParam("newName") String username, @CookieParam("token") Cookie token){
        System.out.println("Invoked userInformation.updateUsername()");
        int userID = validateToken(token);
        if (userID == -1){
            return "{\"Error\": \"Login Status Error. Please Login Again.\"}";
        }
        try {
            PreparedStatement ps = Main.db.prepareStatement("UPDATE UserInformation SET Username = ? WHERE UserID = ?");
            ps.setString(1, username);
            ps.setInt(2, userID);
            ps.execute();
            return "{\"OK\": \"Updated Username.\"}";
        }
        catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Error updating username. Possible Error: username ealready taken.\"}";

        }
    }
    @POST
    @Path("updatepassword/{newPassword}")
    public String updatePassword(@PathParam("newPassword") String password, @CookieParam("token") Cookie token){
        System.out.println("Invoked userInformation.updatePassword()");
        int userID = validateToken(token);
        if (userID == -1){
            return "{\"Error\": \"Login Status Error. Please Login Again.\"}";
        }
        try {
            PreparedStatement ps = Main.db.prepareStatement("UPDATE UserInformation SET Password = ? WHERE UserID = ?");
            ps.setString(1, password);
            ps.setInt(2, userID);
            ps.execute();
            return "{\"OK\": \"Updated Password.\"}";
        }
        catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Error updating password.\"}";

        }
    }
    @POST
    @Path("updategender/{newGender}")
    public String updateGender(@PathParam("newGender") String gender, @CookieParam("token") Cookie token){
        System.out.println("Invoked userInformation.updateGender()");
        int userID = validateToken(token);
        if (userID == -1){
            return "{\"Error\": \"Login Status Error. Please Login Again.\"}";
        }
        try {
            PreparedStatement ps = Main.db.prepareStatement("UPDATE UserInformation SET Gender = ? WHERE UserID = ?");
            ps.setString(1, gender);
            ps.setInt(2, userID);
            ps.execute();
            return "{\"OK\": \"Updated Gender.\"}";
        }
        catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Error updating Gender.\"}";

        }
    }
    @POST
    @Path("updateDOB/{newDOB}")
    public String updateDOB(@PathParam("newDOB") String dob, @CookieParam("token") Cookie token){
        System.out.println("Invoked userInformation.updateDOB()");
        int userID = validateToken(token);
        if (userID == -1){
            return "{\"Error\": \"Login Status Error. Please Login Again.\"}";
        }
        try {
            PreparedStatement ps = Main.db.prepareStatement("UPDATE UserInformation SET DOB = ? WHERE UserID = ?");
            ps.setString(1, dob);
            ps.setInt(2, userID);
            ps.execute();
            return "{\"OK\": \"Updated DOB.\"}";
        }
        catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Error updating DOB.\"}";

        }
    }
    @POST
    @Path("updateheight/{newHeight}")
    public String updateHeight(@PathParam("newHeight") String height, @CookieParam("token") Cookie token){
        System.out.println("Invoked userInformation.updateHeight()");
        int userID = validateToken(token);
        if (userID == -1){
            return "{\"Error\": \"Login Status Error. Please Login Again.\"}";
        }
        try {
            PreparedStatement ps = Main.db.prepareStatement("UPDATE UserInformation SET Height = ? WHERE UserID = ?");
            ps.setString(1, height);
            ps.setInt(2, userID);
            ps.execute();
            return "{\"OK\": \"Updated Height.\"}";
        }
        catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Error updating Height.\"}";

        }
    }
    @POST
    @Path("updateweight/{newWeight}")
    public String updateWeight(@PathParam("newWeight") String weight, @CookieParam("token") Cookie token){
        System.out.println("Invoked userInformation.updateWeight()");
        int userID = validateToken(token);
        if (userID == -1){
            return "{\"Error\": \"Login Status Error. Please Login Again.\"}";
        }
        try {
            PreparedStatement ps = Main.db.prepareStatement("UPDATE UserInformation SET Weight = ? WHERE UserID = ?");
            ps.setString(1, weight);
            ps.setInt(2, userID);
            ps.execute();
            return "{\"OK\": \"Updated Weight.\"}";
        }
        catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Error updating Weight.\"}";

        }
    }
}
