package controllers;

import org.glassfish.jersey.media.multipart.FormDataParam;
import server.Main;

import javax.ws.rs.*;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import static server.Convertor.convertToJSONObject;


@Path("foodinfo/")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)

public class FoodInfo {
    @POST
    @Path("list")
    public String getFoodInfo(@FormDataParam("Food") String name) {

        System.out.println("Invoked FoodInfo.getFood()");
        try {
            PreparedStatement statement = Main.db.prepareStatement(
                    "SELECT * FROM Foods WHERE Name = ?"
            );
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();
            JSONObject resultsJSON = convertToJSONObject(resultSet);
            return resultsJSON.toString();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "{\"Error\": \"Something as gone wrong.  Please contact the administrator with the error code UC-UG. \"}";
        }
    }
}