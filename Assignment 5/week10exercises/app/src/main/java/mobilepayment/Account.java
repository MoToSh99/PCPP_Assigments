package mobilepayment;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;

public class Account extends AbstractBehavior<Account.BankMessage> {

    /* --- Messages ------------------------------------- */
    public interface BankMessage { }

    public static final class DepositMessage implements BankMessage {
        public final int value;

        public DepositMessage(int value) {
            this.value = value;
        }
    }

    /* --- State ---------------------------------------- */
    private int balance;

    /* --- Constructor ---------------------------------- */
    private Account(ActorContext<BankMessage> context) {
        super(context);
        this.balance = 0;
        
    }

    /* --- Actor initial behavior ----------------------- */
    public static Behavior<BankMessage> create() {
        return Behaviors.setup(Account::new);
    }

    /* --- Message handling ----------------------------- */
    @Override
    public Receive<BankMessage> createReceive() {
	return newReceiveBuilder()
	    .onMessage(DepositMessage.class, this::deposit)
	    .build();
    }
    
    /* --- Handlers ------------------------------------- */
    public Behavior<BankMessage> deposit(DepositMessage msg) {
        getContext().getLog().info("{}: Deposited {}",
                       getContext().getSelf().path().name(),
                       msg.value);
        this.balance += msg.value;
        return this;
    }
}
