package info.szente.gla.aos_ae2;

import java.text.DecimalFormat;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

import info.szente.gla.aos_ae2.Sensor.MeasurementResult;

public class Display extends AbstractActor {

	static public Props props() {
		return Props.create(Display.class, () -> new Display());
	}

	public Display() {
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder()
				.match(MeasurementResult.class, result -> {
						DecimalFormat df = new DecimalFormat("#.00"); 
						System.out.println("sensor: " + result.sensorId + " - " + df.format(result.celsius) + "ºC, " + df.format(result.fahrenheit) + "ºF");
				})
				.build();
	}
}
