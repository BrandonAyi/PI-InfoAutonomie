package controllers;

import com.avaje.ebean.Ebean;
import play.libs.Yaml;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

import java.util.List;

/**
 * The main controller of the Play application.
 */
public class Application extends Controller {

    /**
     * Initializes the controller with test data.
     *
     * @return the index result.
     */
    public static Result init() {
        Ebean.save((List) Yaml.load("test-data.yml"));

        return index();
    }

    /**
     * Loads the home page of the application.
     *
     * @return the index result.
     */
    public static Result index() {
        return ok(index.render("Your new application is ready."));
    }
}
