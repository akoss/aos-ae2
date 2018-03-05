package info.szente.gla.aos_ae2;

import java.util.Random;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

import info.szente.gla.aos_ae2.Clock.Tick;
import info.szente.gla.aos_ae2.TemperatureConverter.Temperature;
import info.szente.gla.aos_ae2.TemperatureConverter.ConvertToCelsius;
import info.szente.gla.aos_ae2.TemperatureConverter.ConvertToFahrenheit;

public class Sensor extends AbstractActor {

	static public Props props(int sensorId, ActorRef displayActor, ActorRef temperatureConverterActor) {
		return Props.create(Sensor.class, () -> new Sensor(sensorId, displayActor, temperatureConverterActor));
	}

	static public class MeasurementResult {
		public final int sensorId;
		public final double celsius;
		public final double fahrenheit; 

		public MeasurementResult(int sensorId, double celsius, double fahrenheit) {
			this.sensorId = sensorId;
			this.celsius = celsius;
			this.fahrenheit = fahrenheit;
		}
	}

	private static double minTemp = 50.0;
	private static double maxTemp = 90.0;

	private final int sensorId;
	private final ActorRef displayActor;
	private final ActorRef temperatureConverterActor;

	public Sensor(int sensorId, ActorRef displayActor, ActorRef temperatureConverterActor) {
		this.sensorId = sensorId;
		this.displayActor = displayActor;
		this.temperatureConverterActor = temperatureConverterActor;
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder()
				.match(Tick.class, tick -> {
					Random r = new Random();
					double randomTemp = (minTemp + (maxTemp - minTemp) * r.nextDouble());
					boolean isFahrenheit = r.nextBoolean();

					if(isFahrenheit) {
						temperatureConverterActor.tell(new ConvertToCelsius(randomTemp), getSelf());	
					} else {
						temperatureConverterActor.tell(new ConvertToFahrenheit(randomTemp), getSelf());	
					}
					
				})
				.match(Temperature.class, temp -> {
					displayActor.tell(new MeasurementResult(this.sensorId,temp.celsius, temp.fahrenheit), getSelf());
				})
				.build();
	}
}
