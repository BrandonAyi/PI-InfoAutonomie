package controllers;

import model.*;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import play.data.Form;
import play.data.format.Formatters;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;
import views.html.event.create;
import views.html.event.events;

import java.text.ParseException;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static play.data.Form.form;
import static play.mvc.Results.*;

/**
 * Controller to manage and display more complex events.
 * Created by Ced on 31/01/2015.
 */
public class EventController extends Controller {

    static Form<Event> eventForm = Form.form(Event.class);

    public static Result events() {
        List<Event> allEvents = Event.all();

        return ok(events.render(allEvents));
    }

    @With(WebAuthorization.class)
    public static Result edit(String id) {
        Event event = Event.find.byId(id);
        eventForm.data().put("id", event.getId());
        eventForm.data().put("name", event.getName());
        eventForm.data().put("expression", event.getExpression());
        eventForm.data().put("beginTime", event.getBeginTime().toString("HH:mm"));
        eventForm.data().put("endTime", event.getEndTime().toString("HH:mm"));
        eventForm.data().put("color", event.getColor());
        eventForm.data().put("icon", event.getIcon());

        // TODO: template to edit an event
        return ok(create.render(eventForm, Sensor.all()));
    }

    @With(WebAuthorization.class)
    public static Result delete(String id) {
        Event event = Event.find.byId(id);
        event.delete();

        return redirect(controllers.routes.EventController.events());
    }

    @With(WebAuthorization.class)
    public static Result save() {
        Formatters.register(DateTime.class, new Formatters.SimpleFormatter<DateTime>() {
            @Override
            public DateTime parse(String input, Locale l) throws ParseException {
                DateTimeFormatter formatter = DateTimeFormat.forPattern("HH:mm");
                DateTime dt = formatter.parseDateTime(input);

                return dt;
            }

            @Override
            public String print(DateTime dt, Locale l) {
                return dt.toString("HH:mm");
            }
        });

        Form<Event> eventForm = form(Event.class).bindFromRequest();

        if (eventForm.hasErrors()) {
            return badRequest(create.render(eventForm, Sensor.all()));
        } else if (Event.find.where().eq("id", eventForm.get().getId()).findRowCount() == 1) {
            Event event = Event.find.ref(eventForm.get().id);

            event.setName(eventForm.get().getName());
            event.setExpression(eventForm.get().getExpression());
            event.setBeginTime(eventForm.get().getBeginTime());
            event.setEndTime(eventForm.get().getEndTime());
            event.setColor(eventForm.get().getColor());
            event.setIcon(eventForm.get().getIcon());

            eventForm.get().update();
        } else {
            eventForm.get().save();
        }

        return redirect(controllers.routes.EventController.events());
    }

    /**
     * Loads the create event page.
     * @return the result of the event page.
     */
    @With(WebAuthorization.class)
    public static Result create() {
        return ok(create.render(form(Event.class), Sensor.all()));
    }

    /**
     * Performs tests about sensors and events. TODO ? Place this in the unit tests
     * @return the test results.
     */
    public static Result test() {
        TimeInterval timeInterval = new TimeInterval();
        timeInterval.setBeginHour(5);
        timeInterval.setBeginMinutes(0);
        timeInterval.setEndHour(12);
        timeInterval.setEndMinutes(0);
        TimeInterval.create(timeInterval);

        System.out.println(timeInterval.toString());
//
        TimeInterval retrieveTimeInterval = TimeInterval.find.byId("1");
////        return ok(views.html.blank.render("Your new application is ready.", timeInterval.toString() +"    "+retrieveTimeInterval.toString()));
        System.out.println(retrieveTimeInterval.toString() +"\n ----------------------------------------------- \n");
//
        Detection detection = new Detection();
        detection.setDelta(10);
        Detection.create(detection);
//
        System.out.println(detection.toString());
        Detection retrieveDetection = Detection.find.byId("1");
        System.out.println(retrieveDetection.toString() +"\n ----------------------------------------------- \n");
////
////        return ok(views.html.blank.render("Your new application is ready.", detection.toString() +"    "+retrieveDetection.toString()));
//
        Sensor sensor = new Sensor();
        sensor.setDescription("Ma description de capteur");
        sensor.setLocation("Salle de bain");
        sensor.setName("TelosB");
        sensor.setId("TelosB");
        sensor.setType(SensorType.HUMIDITY);
        Sensor.create(sensor);
//
        System.out.println(sensor.toString());
//
        Sensor retrieveSensor = Sensor.find.byId("TelosB");
//
        System.out.println(retrieveSensor.toString() +"\n ----------------------------------------------- \n");
//
////        return ok(views.html.blank.render("Your new application is ready.", sensor.toString() +"    "+retrieveSensor.toString()));
//
        BasicEvent basicEvent = new BasicEvent();
        basicEvent.setId("My first Basic Event");
        BasicEvent.create(basicEvent, retrieveDetection.getId(), retrieveSensor.getName());
//
        System.out.println(basicEvent.toString());
        BasicEvent retrieveBasicEvent = BasicEvent.find.byId("My first Basic Event");
        System.out.println(retrieveBasicEvent.toString() +"\n ----------------------------------------------- \n");
//
//        return ok(views.html.blank.render("Your new application is ready.", basicEvent.toString() +"    "+retrieveBasicEvent.toString()));

        Event event = new Event();
        event.setName("My first Event");
        event.getBasicEvents().add(retrieveBasicEvent);
        //event.setTimeInterval(retrieveTimeInterval);

        Event.create(event, retrieveTimeInterval.id);

        Event retrieveEvent = Event.find.byId("My first Event");
//
        return ok(views.html.blank.render("Your new application is ready.", event.toString() +"   !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!   "+ retrieveEvent.toString()));
    }

    /**
     * Displays a list of more complex events (based on basic events) that occurred.
     * @return the result of the events occurred.
     */
    public static Result timeline() {
        return ok(views.html.event.timeline.render("Évènements", EventOccurrence.all()));
    }
}
