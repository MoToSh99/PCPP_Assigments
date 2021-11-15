package mathsserver;

// Hint: The imports below may give you hints for solving the exercise.
//       But feel free to change them.

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.ChildFailed;
import akka.actor.typed.Terminated;
import akka.actor.typed.javadsl.*;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.stream.IntStream;

public class Server extends AbstractBehavior<Server.ServerCommand> {
    /* --- Messages ------------------------------------- */
    public interface ServerCommand { }
    
    public static final class ComputeTasks implements ServerCommand {
	public final List<Task> tasks;
	public final ActorRef<Client.ClientCommand> client;

	public ComputeTasks(List<Task> tasks,
				 ActorRef<Client.ClientCommand> client) {
	    this.tasks  = tasks;
	    this.client = client;
	}
    }

	public static final class ComputeTask implements ServerCommand {
		public final Task task;
		public final ActorRef<Client.ClientCommand> client;
	
		public ComputeTask(Task task,
					 ActorRef<Client.ClientCommand> client) {
			this.task  = task;
			this.client = client;
		}
		}


    public static final class WorkDone implements ServerCommand {
	ActorRef<Worker.WorkerCommand> worker;

	public WorkDone(ActorRef<Worker.WorkerCommand> worker) {
	    this.worker = worker;
	}
    }
    
    /* --- State ---------------------------------------- */
	private final List<ComputeTask> pendingTasks;
	private final HashMap<ActorRef<Worker.WorkerCommand>,Task> busyWorkers;
	private final List<ActorRef<Worker.WorkerCommand>> idleWorkers;
	int minWorkers;
	int maxWorkers;
	int totalworkers;
    
    

    /* --- Constructor ---------------------------------- */
    private Server(ActorContext<ServerCommand> context, int minWorkers, int maxWorkers) {
    super(context);
	this.minWorkers = minWorkers;
	this.maxWorkers = maxWorkers;
	this.pendingTasks = new LinkedList<ComputeTask>();
	this.busyWorkers = new HashMap<ActorRef<Worker.WorkerCommand>,Task>();
	this.idleWorkers = new ArrayList<ActorRef<Worker.WorkerCommand>>();
	this.totalworkers = minWorkers+1;


	IntStream
	.range(1,minWorkers+1)
	.forEach((workerId) -> {
	    final ActorRef<Worker.WorkerCommand> newJobWorker =
	    	getContext().spawn(Worker.create(getContext().getSelf()),
	    			   getContext().getSelf().path().name()+(++workerId));
		idleWorkers.add(newJobWorker);
		getContext().watch(newJobWorker);
	});
	getContext().getLog().info("{}: Server and workers started", getContext().getSelf().path().name());
    }


    /* --- Actor initial state -------------------------- */
    public static Behavior<ServerCommand> create(int minWorkers, int maxWorkers) {
    	return Behaviors.setup(context -> new Server(context, minWorkers, maxWorkers));
    }
    

    /* --- Message handling ----------------------------- */
    @Override
    public Receive<ServerCommand> createReceive() {
    	return newReceiveBuilder()
    	    .onMessage(ComputeTasks.class, this::onComputeTasks)
    	    .onMessage(WorkDone.class, this::onWorkDone)
			.onSignal(ChildFailed.class, this::onChildFailed)
			.onSignal(Terminated.class, this::onTerminated)
    	    .build();
    }


    /* --- Handlers ------------------------------------- */
    public Behavior<ServerCommand> onComputeTasks(ComputeTasks msg) {

		for (Task t : msg.tasks) {
		if(idleWorkers.size() > 0){
			final ActorRef<Worker.WorkerCommand> selectedWorker = idleWorkers.get(0);
			selectedWorker.tell(new Worker.ComputeTask(t, msg.client));
			idleWorkers.remove(0);
			busyWorkers.put(selectedWorker, t);
		}
		else if(busyWorkers.size() < maxWorkers){

			final ActorRef<Worker.WorkerCommand> newJobWorker = getContext().spawn(Worker.create(getContext().getSelf()),
	    			   getContext().getSelf().path().name()+(totalworkers + 1));
			totalworkers += 1;
			getContext().watch(newJobWorker);
			newJobWorker.tell(new Worker.ComputeTask(t, msg.client));
		}
		else {
			pendingTasks.add(new ComputeTask(t, msg.client));
		}
	}
    	return this;
    }

	public Behavior<ServerCommand> onChildFailed(ChildFailed msg) {
		ActorRef<Void> crashedChild = msg.getRef();
		Task taskfailed = busyWorkers.remove(crashedChild);
		
		final ActorRef<Worker.WorkerCommand> newJobWorker =
				getContext().spawn(Worker.create(getContext().getSelf()),
						   getContext().getSelf().path().name()+(totalworkers+1));
		totalworkers += 1;
		getContext().watch(newJobWorker);
			
		idleWorkers.add(newJobWorker);

		getContext().getLog().info("{}: Worker {} crashed trying to compute {} due to {}\nNew worker {} added to idleworkers.",
		getContext().getSelf().path().name(),
		crashedChild.path().name(),taskfailed, msg.cause(),
		newJobWorker.path().name());

		return this;	
		}
	
		public Behavior<ServerCommand> onTerminated(Terminated msg) {
		if(busyWorkers.remove(msg.getRef()) != null) {
			getContext().getLog().info("{}: {} terminated normally.",
						   getContext().getSelf().path().name(),
						   msg.getRef().path().name());	    
		} else {
			// Never going to be executed
			getContext().getLog().info("{}: No job from worker {} found.",
						   getContext().getSelf().path().name(),
						   msg.getRef().path().name());
		}
		return this;	
		}

    public Behavior<ServerCommand> onWorkDone(WorkDone msg) {
		if(pendingTasks.size() > 0){
			ComputeTask ct = ((LinkedList<ComputeTask>) pendingTasks).removeFirst();
			msg.worker.tell(new Worker.ComputeTask(ct.task, ct.client));
		}
		else if (idleWorkers.size() < minWorkers) {
			busyWorkers.remove(msg.worker);
			idleWorkers.add(msg.worker);
		}
		else{
			busyWorkers.remove(msg.worker);
			msg.worker.tell(new Worker.Stop());
		}
	return this;	
    }
}
