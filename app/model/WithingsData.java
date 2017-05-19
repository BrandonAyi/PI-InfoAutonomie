package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;


/**
 * TODO document
 * Created by Brandon on 18/05/2017.
 */

@Entity
public class WithingsData extends Model {
    // {"id":"1","name":"Light Sensor","address":"153.111","type":"LIGHT","location":"Bureau TN","description":"Sur la commode"}
	
    @Id
    private String date;
    private Integer pulse;
    private Double distance;
	private Integer steps;
    private Double totalCalories;
    private Integer durationToSleep;
    private Integer lightSleepDuration;
    private Integer deepSleepDuration;
    private Integer sleepDuration;

    /*public static WithingsData create(WithingsData withings) {
        withings.save();
        return withings;
    }*/

    public static Model.Finder<String,WithingsData> find = new Model.Finder<>(String.class, WithingsData.class);

    public static List<WithingsData> all() {
        return find.all();
    }

    public Integer getSteps() {
		return steps;
	}

	public void setSteps(Integer steps) {
		this.steps = steps;
	}

	public Double getDistance() {
		return distance;
	}

	public void setDistance(Double distance) {
		this.distance = distance;
	}

	public Double getTotalCalories() {
		return totalCalories;
	}

	public void setTotalCalories(Double totalCalories) {
		this.totalCalories = totalCalories;
	}

	public Integer getPulse() {
		return pulse;
	}

	public void setPulse(Integer pulse) {
		this.pulse = pulse;
	}

	public Integer getDurationToSleep() {
		return durationToSleep;
	}

	public void setDurationToSleep(Integer durationToSleep) {
		this.durationToSleep = durationToSleep;
	}

	public Integer getLightSleepDuration() {
		return lightSleepDuration;
	}

	public void setLightSleepDuration(Integer lightSleepDuration) {
		this.lightSleepDuration = lightSleepDuration;
	}

	public Integer getDeepSleepDuration() {
		return deepSleepDuration;
	}

	public void setDeepSleepDuration(Integer deepSleepDuration) {
		this.deepSleepDuration = deepSleepDuration;
	}

	public Integer getSleepDuration() {
		return sleepDuration;
	}

	public void setSleepDuration(Integer sleepDuration) {
		this.sleepDuration = sleepDuration;
	}

	public void setDate(String date) {
		this.date = date;
	}
	
	public String getDate() {
		return date;
	}


    @Override
    public String toString() {
        return "WithingsData{" +
                "date='" + getDate() + '\'' +
                ", pulse='" + getPulse() + '\'' +
                ", distance='" + getDistance() + '\'' +
                ", steps=" + getSteps() +
                ", totalCalories='" + getTotalCalories() + '\'' +
                ", durationToSleep='" + getDurationToSleep() + '\'' +
                ", lightSleepDuration='" + getLightSleepDuration() + '\'' +
                ", deepSleepDuration='" + getDeepSleepDuration() + '\'' +
                ", sleepDuration='" + getSleepDuration() + '\'' +
                '}';
    }

}
