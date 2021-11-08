package mobilepayment;

import akka.actor.typed.ActorSystem;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
    
		// actor system
		final ActorSystem<SystemGuardian.KickOff> guardian = ActorSystem.create(SystemGuardian.create(), "mobilepayment_system");

		// init message
		guardian.tell(new SystemGuardian.KickOff());

		// wait until user presses enter
		try {
			System.out.println(">>> Press ENTER to exit <<<");
			System.in.read();
		}
		catch (IOException e) {
			System.out.println("Error " + e.getMessage());
			e.printStackTrace();
		} finally {
			// terminate actor system execution
			guardian.terminate();
		}
    
    }
    
}
