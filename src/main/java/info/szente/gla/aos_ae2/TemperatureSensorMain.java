package info.szente.gla.aos_ae2;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;

import info.szente.gla.aos_ae2.Clock.StartTicking;

import java.io.IOException;

public class TemperatureSensorMain {

	public static int numberOfSensors = 5; 

	public static void main(String[] args) {
		final ActorSystem system = ActorSystem.create("tempsensor");
		try {
			final ActorRef displayActor = system.actorOf(Display.props(), "displayActor");
			final ActorRef temperatureConverterActor = system.actorOf(TemperatureConverter.props(), "temperatureConverterActor");

			ActorRef[] sensors = new ActorRef[numberOfSensors];

			for(int i = 0; i < numberOfSensors; i++) {
				sensors[i] = system.actorOf(Sensor.props(i, displayActor, temperatureConverterActor), "sensor" + i);
			}

			final ActorRef clock = system.actorOf(Clock.props(1, sensors), "clock");

			clock.tell(new StartTicking(), ActorRef.noSender());

		} catch (Exception e) {

		}
	}
}
