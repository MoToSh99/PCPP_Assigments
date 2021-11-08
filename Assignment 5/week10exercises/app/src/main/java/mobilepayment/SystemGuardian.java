package mobilepayment;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;

public class SystemGuardian extends AbstractBehavior<SystemGuardian.KickOff> {
    
    /* --- Messages ------------------------------------- */
    public static final class KickOff { }
    

    /* --- State ---------------------------------------- */
    // To be Implemented
    

    /* --- Constructor ---------------------------------- */
    private SystemGuardian(ActorContext<KickOff> context) {
        super(context);
    }
    

    /* --- Actor initial behavior ----------------------- */
    public static Behavior<KickOff> create() {
        return Behaviors.setup(SystemGuardian::new);
    }
    

    /* --- Message handling ----------------------------- */
    @Override
    public Receive<KickOff> createReceive() {
	return newReceiveBuilder()
	    .onMessage(KickOff.class, this::onKickOff)
	    .build();
    }
    

    /* --- Handlers ------------------------------------- */
    public Behavior<KickOff> onKickOff(KickOff msg) {
        // spawn the observable server
        final ActorRef<Bank.BankMessage> b1 = getContext().spawn(Bank.create(), "b1");
        final ActorRef<Bank.BankMessage> b2 = getContext().spawn(Bank.create(), "b2");
        
        final ActorRef<Account.BankMessage> a1 = getContext().spawn(Account.create(), "a1");
        final ActorRef<Account.BankMessage> a2 = getContext().spawn(Account.create(), "a2");

        final ActorRef<MobileApp.BankMessage> mb1 = getContext().spawn(MobileApp.create(b1), "mb1");
        final ActorRef<MobileApp.BankMessage> mb2 = getContext().spawn(MobileApp.create(b2), "mb2");
 
        mb1.tell(new MobileApp.TransactionMessage(a2, a1, -100));
        
        mb2.tell(new MobileApp.TransactionMessage(a1, a2, 100));
        return this;
        }

}
