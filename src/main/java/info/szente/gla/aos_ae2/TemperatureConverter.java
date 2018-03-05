package info.szente.gla.aos_ae2;

import java.util.Random;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

public class TemperatureConverter extends AbstractActor {

	static public Props props() {
		return Props.create(TemperatureConverter.class, () -> new TemperatureConverter());
	}

	static public class ConvertToCelsius {
		public final double fahrenheit; 
		public ConvertToCelsius(double fahrenheit) {
			this.fahrenheit = fahrenheit;
		}
	}

	static public class ConvertToFahrenheit {
		public final double celsius; 
		public ConvertToFahrenheit(double celsius) {
			this.celsius = celsius;
		}
	}

	static public class Temperature {
		public final double celsius;
		public final double fahrenheit; 

		public Temperature(double celsius, double fahrenheit) {
			this.celsius = celsius;
			this.fahrenheit = fahrenheit;
		}
	}

	public TemperatureConverter() {
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder()
			.match(ConvertToFahrenheit.class, x -> {
				getSender().tell(new Temperature(x.celsius, (x.celsius*(9.0/5.0) + 32)), getSelf());
			})
			.match(ConvertToCelsius.class, x -> {
				getSender().tell(new Temperature(((x.fahrenheit-32) * 5.0/9.0), x.fahrenheit), getSelf());
			})
			.build();
	}
}
