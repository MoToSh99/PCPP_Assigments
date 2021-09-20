package exercises04;
import java.util.concurrent.atomic.AtomicLong;

public class Person {

    private final long id;
    private String name;
    private int zip;
    private String address;
    private static AtomicLong idCounter = new AtomicLong(-1);

    public Person() {
        idCounter.getAndIncrement();        
        this.id = idCounter.get();
    }

    public Person(long id) {
        if (idCounter.get() == -1) {
            Person.idCounter.set(id);
            this.id = id;
        } else {
            idCounter.getAndIncrement(); 
            this.id = idCounter.get();
        }
    }

    public synchronized void changeAddress(int zip, String address) {
        this.zip = zip;
        this.address = address;
    }

    public synchronized long getId() {
        return id;
    }

    public synchronized String getName() {
        return name;
    }

    public synchronized int getZip() {
        return zip;
    }

    public synchronized String getAddress() {
        return address;
    }

    public static void main(String[] args) throws InterruptedException {

        for (int i = 0; i < 123; i++) {
            new Thread(() -> {
                try {
                    Person m = new Person();
                    System.out.println(m.getId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
