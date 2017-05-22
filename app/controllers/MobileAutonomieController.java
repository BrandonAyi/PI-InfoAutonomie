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
import views.html.MobileAutonomie.mobileAutonomie;
import views.html.account.login;
import views.html.blank;
import views.html.index;
import play.data.DynamicForm;
import play.data.Form;
import java.util.List;
import java.io.*;
import java.util.*;


import static play.data.Form.form;

/**
 * The main controller of the Play application.
 */
@Security.Authenticated(WebAuthentication.class)
public class MobileAutonomieController extends Controller {

    static List<ArrayList<String>> stockList = new ArrayList<ArrayList<String>>();

    /**
     * Loads the home page of the application.
     *
     * @return the index result.
     */
    public static Result mobileAutonomie() {
        String content = "";
        return getData();
    }

    public static Result getData() {

        try{
            String[] str = null;

            InputStream ips=new FileInputStream("data.csv"); 
            InputStreamReader ipsr=new InputStreamReader(ips);
            BufferedReader br=new BufferedReader(ipsr);
            String ligne;

            ArrayList<String> temp = new ArrayList<String>();

            while ((ligne=br.readLine())!=null){
                str = ligne.split(";");                
                for (int i=0; i<str.length; i++) {
                    temp.add(str[i]);
                }
                stockList.add(temp);
                temp = new ArrayList<String>();
            }
            br.close(); 
        }       
        catch (Exception e){}

        return ok(mobileAutonomie.render(stockList));
    }
}
