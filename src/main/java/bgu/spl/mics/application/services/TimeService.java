package bgu.spl.mics.application.services;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.Messages.Broadcasts.Tick;
import bgu.spl.mics.MicroService;
import java.util.TimerTask;
import java.util.Timer;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link Tick Broadcast}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */

public class TimeService extends MicroService{
	private Timer timer = new Timer();
	private int delayinMiliSec;
	private int duration;
	private int currentTick = 0;
	private Broadcast tickBroadcast = new Tick(0, false);
	private TimerTask task = new TimerTask() {
		@Override
		public void run() {
			tickBroadcast = new Tick(currentTick , currentTick >=  duration -1 );

			System.out.println("\n ----------------------tick "+(currentTick)+"  ------------------------");
			sendBroadcast(tickBroadcast);
			if(currentTick >= duration -1) {
				timer.cancel();
			}
			currentTick++;
		}
	};

	public  TimeService(int speed,int duration) {
		super("Timer Service");
		delayinMiliSec = speed;
		this.duration = duration;


	}

	//The init should be after all other services are registered.
	@Override
	protected synchronized void initialize() {
		try{wait(1000);}catch (InterruptedException e){}
		terminate();
		timer.scheduleAtFixedRate(task,0,delayinMiliSec);
	}

}
