package controllers;

import server.Main;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.simple.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import java.io.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;
import static server.Convertor.convertToJSONObject;

@Path("userInformation/")
public class UserInformation {
    @POST
    @Path("login")
    public String loginUser(@FormDataParam("Username") String username, @FormDataParam("Password") String password) {
        System.out.println("Invoked loginUser() on path userInformation/login");
        try {
            PreparedStatement ps1 = Main.db.prepareStatement("SELECT Password FROM UserInformation WHERE Username = ?");
            ps1.setString(1, username);
            ResultSet loginResults = ps1.executeQuery();
            if (loginResults.next() == true) {
                String correctPassword = loginResults.getString(1);
                if (password.equals(correctPassword)) {
                    String token = UUID.randomUUID().toString();
                    PreparedStatement ps2 = Main.db.prepareStatement("UPDATE UserInformation SET Token = ? WHERE Username = ?");
                    ps2.setString(1, token);
                    ps2.setString(2, username);
                    ps2.executeUpdate();
                    JSONObject userDetails = new JSONObject();
                    userDetails.put("username", username);
                    userDetails.put("token", token);
                    return userDetails.toString();
                } else {
                    return "{\"Error\": \"Incorrect password!\"}";
                }
            } else {
                return "{\"Error\": \"Username and password are incorrect.\"}";
            }
        } catch (Exception exception) {
            System.out.println("Database error during /userInformation/login: " + exception.getMessage());
            return "{\"Error\": \"Server side error!\"}";
        }
    }




    public static boolean validToken(String token) {		// this method MUST be called before any data is returned to the browser
        // token is taken from the Cookie sent back automatically with every HTTP request
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT UserID FROM UserInformation WHERE Token = ?");
            ps.setString(1, token);
            ResultSet logoutResults = ps.executeQuery();
            return logoutResults.next();   //logoutResults.next() will be true if there is a record in the ResultSet
        } catch (Exception exception) {
            System.out.println("Database error" + exception.getMessage());
            return false;
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

    @GET
    @Path("getUser")
    public String getUser(@CookieParam("sessionToken") Cookie Token) {

        System.out.println("Invoked User.userGet()");

        if (Token == null) {
            return "{\"Error\": \"Something as gone wrong.  Please contact the administrator with the error code UC-UG. \"}";
        }

        try {
            String uuid = Token.getValue();
            PreparedStatement statement = Main.db.prepareStatement(
                    "SELECT * FROM Users WHERE UUID = ?"
            );
            statement.setString(1, uuid);
            ResultSet resultSet = statement.executeQuery();
            JSONObject resultsJSON = convertToJSONObject(resultSet);
            return resultsJSON.toString();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "{\"Error\": \"Something as gone wrong.  Please contact the administrator with the error code UC-UG. \"}";
        }
    }


}
