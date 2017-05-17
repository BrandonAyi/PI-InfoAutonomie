package controllers;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.JsonNode;
import model.*;
import play.libs.Json;
import play.libs.Yaml;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import play.mvc.With;
import views.html.WithingsData.withingsData;
import views.html.account.login;
import views.html.blank;
import views.html.index;
import play.data.DynamicForm;
import play.data.Form;
import java.util.List;


import static play.data.Form.form;

/**
 * The main controller of the Play application.
 */
@Security.Authenticated(WebAuthentication.class)
public class WithingsDataController extends Controller {

    /**
     * Loads the home page of the application.
     *
     * @return the index result.
     */
    public static Result withingsData() {
        String content = "";
        return ok(withingsData.render(content));
    }

    public static Result getData() {
        // POST http://localhost:9000/WithingsData/Data  {"address":"153.111","type":"LIGHT"}
        String json = request().body().asText();
        System.out.println(json);
//        if (json == null) {
//            return badRequest("Expecting some Json data");
//        } else {
//            String address = json.findPath("address").asText();
//            String type = json.findPath("type").asText();
//            if (address == null || type == null) {
//                return badRequest("Missing parameters [name]");
//            } else {
//                String content = "Test";
//                return ok(withingsData.render(content));
//
//            }
//        }
               return ok(withingsData.render(json));
    }
}