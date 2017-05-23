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
import java.lang.Number;

import static play.data.Form.form;


/**
 * The main controller of the Play application.
 */
@Security.Authenticated(WebAuthentication.class)
public class WithingsDataController extends Controller {

	static String date = "";
    static String heure="";
    static Integer steps=0;
    static Double distance=0.0;
    static Double totalCalories=0.0;
    static Integer pulse=0;
    static Integer durationToSleep=0;
    static Integer lightSleepDuration=0;
    static Integer deepSleepDuration=0;
    static Integer sleepDuration=0;

    static List<ArrayList<String>> stockList = new ArrayList<ArrayList<String>>();

    /**
     * Loads the home page of the application.
     *
     * @return the index result.
     */
    public static Result withings() throws IOException, JSONException {

        return saveData();
    }


    public static Result saveData() throws IOException, JSONException {


        String datas=getDataFromUrl();
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


        //return redirect(controllers.routes.WithingsDataController.withings());
        return ok(withingsPage.render(stockList));
    }

    public static Result getData() {
        // POST http://localhost:9000/WithingsData/Data  {"address":"153.111","type":"LIGHT"}
        String json = request().body().asText();
        System.out.println(json);
        List<ArrayList<String>> stockList = new ArrayList<ArrayList<String>>();
        return ok(withingsPage.render(stockList));
    }

        public static String getDataFromUrl() throws IOException, JSONException {
        
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        date = df.format(Calendar.getInstance().getTime());
        SimpleDateFormat hf = new SimpleDateFormat ("HH:mm");    
        Date currentTime_12 = new Date();    
        heure = hf.format(currentTime_12);

        JSONObject jsonBody = readJsonFromUrl("http://wbsapi.withings.net/v2/measure?action=getactivity&date="+date+"&oauth_callback=https://github.com/BrandonAyi/PI-InfoAutonomie&oauth_consumer_key=07422690e2b4be3eb396d509c796f607eb54fd59d9109e4c94292352206e&oauth_nonce=037be00197b955551c504bd65dc88419&oauth_signature=ztCOm1cjlSdK39xzP8rgZ7ze7kQ%3D&oauth_signature_method=HMAC-SHA1&oauth_timestamp=1495443062&oauth_token=c287780131b140b7e22919ff4c1c22b4980656cecc461524c329872e3154&oauth_version=1.0&userid=13618457");
        JSONObject body1 = (JSONObject) jsonBody.get("body");
        if (body1.has("steps"))
            steps = (Integer) body1.get("steps");
        if (body1.has("distance"))
            distance = (double) body1.get("distance");
        if (body1.has("totalcalories"))
            totalCalories = (double) body1.get("totalcalories");
        
        JSONObject jsonSleep = readJsonFromUrl("https://wbsapi.withings.net/v2/sleep?action=getsummary&date="+date+"&oauth_callback=https://github.com/BrandonAyi/PI-InfoAutonomie&oauth_consumer_key=07422690e2b4be3eb396d509c796f607eb54fd59d9109e4c94292352206e&oauth_nonce=769ffb227e333e96e6d497c16486cfcf&oauth_signature=c02NsaSXAyb0BMvOXoh1UU110GQ%3D&oauth_signature_method=HMAC-SHA1&oauth_timestamp=1495442608&oauth_token=c287780131b140b7e22919ff4c1c22b4980656cecc461524c329872e3154&oauth_version=1.0&userid=13618457");
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

        JSONObject jsonMeas = readJsonFromUrl("http://wbsapi.withings.net/measure?action=getmeas&date="+date+"&meastype=11&oauth_callback=https://github.com/BrandonAyi/PI-InfoAutonomie&oauth_consumer_key=07422690e2b4be3eb396d509c796f607eb54fd59d9109e4c94292352206e&oauth_nonce=250884474336c4f7a9eb1180a37a61ec&oauth_signature=PqJuvY9XCi2bvnfFiWHKVaXjmaw%3D&oauth_signature_method=HMAC-SHA1&oauth_timestamp=1495442749&oauth_token=c287780131b140b7e22919ff4c1c22b4980656cecc461524c329872e3154&oauth_version=1.0&userid=13618457");
        JSONObject body3 = (JSONObject) jsonMeas.get("body");
        JSONArray measuregrps = (JSONArray) body3.get("measuregrps");
        JSONObject measures = (JSONObject) measuregrps.get(0);
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
