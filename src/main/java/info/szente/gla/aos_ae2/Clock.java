package info.szente.gla.aos_ae2;

import java.util.concurrent.TimeUnit;
import scala.concurrent.duration.Duration;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.ActorSystem;

public class Clock extends AbstractActor {

	static public Props props(int duration, ActorRef[] toTick) {
		return Props.create(Clock.class, () -> new Clock(duration, toTick));
	}

	static public class Tick {
		public final int time; 
		public Tick(int time) {
			this.time = time;
		}
	}

	static public class StartTicking {
		public StartTicking() {
		}
	}

	private final int duration;
	private final ActorRef[] toTick;
	private int time = 0;

	public Clock(int duration, ActorRef[] toTick) {
		this.duration = duration;
		this.toTick = toTick;
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder()
				.match(Tick.class, x -> {
					ActorSystem system = getContext().system(); 

					system.scheduler().scheduleOnce(Duration.create(1000, TimeUnit.MILLISECONDS), getSelf(), new Tick(this.time), system.dispatcher(), null);

					Tick tick = new Tick(this.time);
					for (ActorRef actorRef : toTick) {
						actorRef.tell(tick, getSelf());
					}
					this.time++;
				})
				.match(StartTicking.class, x -> {
					this.time = 0;
					getSelf().tell(new Tick(this.time), getSelf());
				})
				.build();
	}
}
