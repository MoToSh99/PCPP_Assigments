package mobilepayment;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;

public class Bank extends AbstractBehavior<Bank.BankMessage> {

    /* --- Messages ------------------------------------- */
    public interface BankMessage { }

    public static final class TransactionMessage implements BankMessage {
        public final int value;
        public final ActorRef<Account.BankMessage> sender;
        public final ActorRef<Account.BankMessage> receiver;

        public TransactionMessage(ActorRef<Account.BankMessage> sender, ActorRef<Account.BankMessage> receiver, int value) {
            this.value = value;
            this.sender = sender;
            this.receiver = receiver;
        }
    }
    
    /* --- State ---------------------------------------- */
    // To be Implemented
    

    /* --- Constructor ---------------------------------- */
    private Bank(ActorContext<BankMessage> context) {
        super(context);
    }
    

    /* --- Actor initial behavior ----------------------- */
    public static Behavior<BankMessage> create() {
        return Behaviors.setup(Bank::new);
    }
    

    /* --- Message handling ----------------------------- */
    @Override
    public Receive<BankMessage> createReceive() {
	return newReceiveBuilder()
	    .onMessage(TransactionMessage.class, this::onTransaction)
	    .build();
    }

    /* --- Handlers ------------------------------------- */
    public Behavior<BankMessage> onTransaction(TransactionMessage msg) {
        getContext().getLog().info("{}: Account {} transferred {} to account {}",
                       getContext().getSelf().path().name(),
                       msg.sender.path().name(),
                       msg.value,
                       msg.receiver.path().name());
        
        msg.receiver.tell(new Account.DepositMessage(msg.value));
        msg.sender.tell(new Account.DepositMessage(-msg.value));
        return this;
        }
}
