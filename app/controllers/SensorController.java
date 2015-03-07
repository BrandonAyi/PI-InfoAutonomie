package controllers;

import model.Sensor;
import static play.data.Form.*;

import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.sensor.sensors;
import views.html.sensor.editSensor;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller to manage and display more complex events.
 * Created by Ced on 31/01/2015.
 */
public class SensorController extends Controller {

    public static Result sensors() {

        List<Sensor> allSensors = Sensor.all();
        List<Sensor> oldSensors = new ArrayList<>();
        List<Sensor> newSensors = new ArrayList<>();

        for(Sensor sensor : allSensors) {
            if(sensor.getDescription() == null) {
                newSensors.add(sensor);
            } else {
                oldSensors.add(sensor);
            }
        }
        return ok(sensors.render(oldSensors, newSensors));
    }

    public static Result sensor(String id) {
        Form<Sensor> form = form(Sensor.class);
        Sensor sensor = Sensor.find.byId(id);
        //System.out.println("List: id "+ id + " " + sensor);
        form.data().put("id", sensor.getId());
        form.data().put("name", sensor.getName());
        form.data().put("address", sensor.getAddress());
        form.data().put("location", sensor.getLocation());
        form.data().put("description", sensor.getDescription());
        String type = "Type";
        switch(sensor.getType()) {
            case DOOR:
                type = "Contact de porte";
                break;
            case LIGHT:
                type = "Luminosité";
                break;
            case TEMP:
                type = "Température";
                break;
            case HUMIDITY:
                type = "humidité";
                break;
            case POWER:
                type = "Consommation électrique";
                break;
            case PRESENCE:
                type = "Présence";
                break;
        }
        form.data().put("type", type);

        return ok(editSensor.render(form));
    }

    public static Result updateSensor() {
        Form<Sensor> sensorForm = form(Sensor.class).bindFromRequest();

        //System.out.println("\n\n\nForm : name " + sensorForm.get().getName() + sensorForm.get().getAddress()+"\n\n\n");
        Sensor sensor = Sensor.find.ref(sensorForm.get().id);
        sensor.setDescription(sensorForm.get().getDescription());
        sensor.setLocation(sensorForm.get().getLocation());
        sensor.setName(sensorForm.get().getName());

        sensor.update();

        return redirect(controllers.routes.SensorController.sensors());
    }

    public static Result resetSensor(String id) {
        Sensor sensor = Sensor.find.byId(id);
        sensor.setDescription(null);
        sensor.setName(null);
        sensor.setLocation(null);
        sensor.update();

        return redirect(controllers.routes.SensorController.sensors());
    }
}
