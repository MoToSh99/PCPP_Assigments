package mobilepayment;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;

// Hint: You may generate random numbers using Random::ints
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class MobileApp extends AbstractBehavior<MobileApp.BankMessage> {

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
    private final ActorRef<Bank.BankMessage> bank;
    

    /* --- Constructor ---------------------------------- */
    private MobileApp(ActorContext<BankMessage> context, ActorRef<Bank.BankMessage> bank) {
        super(context);
        this.bank =  bank;
    }
    

    /* --- Actor initial behavior ----------------------- */
    public static Behavior<BankMessage> create(ActorRef<Bank.BankMessage> bank) {
	    return Behaviors.setup((context) -> new MobileApp(context, bank));
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
        final int N = 100;

        new Random().ints(0, 1000).limit(N)
        .forEach((value) -> {
            getContext().getLog().info("{}: Account {} transferred {} to account {}",
                       getContext().getSelf().path().name(),
                       msg.sender.path().name(),
                       value,
                       msg.receiver.path().name());
            this.bank.tell(new Bank.TransactionMessage(msg.sender, msg.receiver, value));	
        });              
        return this;
    }
}
