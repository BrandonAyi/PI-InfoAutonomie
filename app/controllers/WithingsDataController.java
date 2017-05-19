package controllers;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.JsonNode;

import jsonUtils.*;
import model.*;
import play.libs.Json;
import play.libs.Yaml;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import play.mvc.With;
import views.html.WithingsData.withingsPage;
import views.html.account.login;
import views.html.blank;
import views.html.index;
import play.data.DynamicForm;
import play.data.Form;
import java.util.List;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import static play.data.Form.form;


/**
 * The main controller of the Play application.
 */
@Security.Authenticated(WebAuthentication.class)
public class WithingsDataController extends Controller {

	static String datas;

    static List<ArrayList<String>> stockList = new ArrayList<ArrayList<String>>();

    /**
     * Loads the home page of the application.
     *
     * @return the index result.
     */
    public static Result withings() throws IOException, JSONException {

        String content = "";
        ArrayList<String> temp = new ArrayList<String>();

        content=getDataFromUrl();

        String[] str = content.split(";");
        //temp = new ArrayList<String>();
        for (int i=0; i<str.length; i++) {
            temp.add(str[i]);
        }
        stockList.add(temp);
        temp = new ArrayList<String>();


        return ok(withingsPage.render(stockList));
    }


    public static Result saveData() throws IOException, JSONException {

        datas=getDataFromUrl();
        stockList = new ArrayList<ArrayList<String>>();

        File file=new File("data.txt");

        if (!file.exists()) {
            file.createNewFile();
        }
        try {
            FileWriter writer=new FileWriter(file,true);
            writer.write(datas+"\n");
            writer.close(); 
        } catch (Exception e) {}


        try{
            String[] str = null;

            InputStream ips=new FileInputStream("data.txt"); 
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


        return redirect(controllers.routes.WithingsDataController.withings());
        //return ok(withingsPage.render(stockList));
    }

    public static Result getData() {
        // POST http://localhost:9000/WithingsData/Data  {"address":"153.111","type":"LIGHT"}
        String json = request().body().asText();
        System.out.println(json);
        List<ArrayList<String>> stockList = new ArrayList<ArrayList<String>>();
        return ok(withingsPage.render(stockList));
    }

        public static String getDataFromUrl() throws IOException, JSONException {

        String date = "";
        String heure="";
        Integer steps=0;
        Double distance=0.0;
        Double totalCalories=0.0;
        Integer pulse=0;
        Integer durationToSleep=0;
        Integer lightSleepDuration=0;
        Integer deepSleepDuration=0;
        Integer sleepDuration=0;

        
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        date = df.format(Calendar.getInstance().getTime());
        SimpleDateFormat hf = new SimpleDateFormat ("HH:mm");    
        Date currentTime_12 = new Date();    
        heure = hf.format(currentTime_12);
        //date="2017-05-17";

        JSONObject jsonBody = readJsonFromUrl("https://wbsapi.withings.net/v2/measure?action=getactivity&date="+date+"&oauth_callback=https://github.com/BrandonAyi/MyOBApp&oauth_consumer_key=07422690e2b4be3eb396d509c796f607eb54fd59d9109e4c94292352206e&oauth_nonce=10e305a7bdf032b4e789f1542015ae8b&oauth_signature=rg%2BRzTmjAWjj11NFE%2Bm8FuRg7gA%3D&oauth_signature_method=HMAC-SHA1&oauth_timestamp=1494936737&oauth_token=429df174237c17de536937623812cbb49d809281e98bc7f70b598101f780789&oauth_version=1.0&userid=13569087");
        JSONObject body1 = (JSONObject) jsonBody.get("body");
        if (body1.has("steps"))
            steps = (Integer) body1.get("steps");
        if (body1.has("distance"))
            distance = (Double) body1.get("distance");
        if (body1.has("totalcalories"))
            totalCalories = (Double) body1.get("totalcalories");
        
        JSONObject jsonSleep = readJsonFromUrl("https://wbsapi.withings.net/v2/sleep?action=getsummary&date="+date+"&oauth_callback=https://github.com/BrandonAyi/MyOBApp&oauth_consumer_key=07422690e2b4be3eb396d509c796f607eb54fd59d9109e4c94292352206e&oauth_nonce=e7803aa0dec34e2ccec3e18c17e19692&oauth_signature=2YR%2BNWflgSBTuj7QbzGKRXgxb%2B0%3D&oauth_signature_method=HMAC-SHA1&oauth_timestamp=1494968903&oauth_token=429df174237c17de536937623812cbb49d809281e98bc7f70b598101f780789&oauth_version=1.0&userid=13569087");
        JSONObject body2 = (JSONObject) jsonSleep.get("body");
        JSONArray series = (JSONArray) body2.get("series");
        
        if (series!=null) { //récupérer les informations du sommeil
            
            for (int i=0; i<series.length(); i++) {
                            
                if (((JSONObject) series.get(i)).get("date").equals(date)) {
                    
                
                    JSONObject objectData = (JSONObject) series.get(i);         
                    JSONObject data = (JSONObject) objectData.get("data");
            
                    
                    Integer endTime = (Integer) objectData.get("enddate");
                    Integer startTime = (Integer) objectData.get("startdate");
                    
                    sleepDuration = endTime - startTime;
                    
                    durationToSleep = data.getInt("durationtosleep");
                    lightSleepDuration = data.getInt("lightsleepduration");
                    deepSleepDuration = data.getInt("deepsleepduration");
                }
            }
            
        }

        JSONObject jsonMeas = readJsonFromUrl("https://wbsapi.withings.net/measure?action=getmeas&date="+date+"&meastype=11&oauth_callback=https://github.com/BrandonAyi/MyOBApp&oauth_consumer_key=07422690e2b4be3eb396d509c796f607eb54fd59d9109e4c94292352206e&oauth_nonce=e154c6adf9e1dd1c9c4e7d61cf8a2cf9&oauth_signature=3HBvfKCk%2F7Y%2BwJXvRekf6BzYKPA%3D&oauth_signature_method=HMAC-SHA1&oauth_timestamp=1494970726&oauth_token=429df174237c17de536937623812cbb49d809281e98bc7f70b598101f780789&oauth_version=1.0&userid=13569087");
        JSONObject body3 = (JSONObject) jsonMeas.get("body");
        JSONArray measuregrps = (JSONArray) body3.get("measuregrps");
        JSONObject measures = (JSONObject) measuregrps.get(1);
        JSONArray measures2 = (JSONArray) measures.get("measures");
        pulse = (Integer) ((JSONObject) measures2.get(0)).get("value");
        
        try{
            String buffer="";
            buffer+=date+" à "+heure+";"+pulse+";"+steps+";"+distance+";"+totalCalories+";"+sleepDuration+";"+durationToSleep+";"+lightSleepDuration+";"+deepSleepDuration;
            return buffer;
        } catch (Exception e) {}
        
        return "probleme";

      }
    
    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
          sb.append((char) cp);
        }
        return sb.toString();
      }
    
      public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
            InputStream is = new URL(url).openStream();
            try {
              BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
              String jsonText = readAll(rd);
              JSONObject json = new JSONObject(jsonText);
              return json;
            } finally {
              is.close();
            }
        }

}
