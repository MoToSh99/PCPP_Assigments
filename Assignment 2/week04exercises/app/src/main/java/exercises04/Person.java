package exercises04;
import java.util.concurrent.atomic.AtomicLong;

public class Person {

    private final long id;
    private final String name;
    private int zip;
    private String address;
    private static AtomicLong idCounter = new AtomicLong(-1);

    public Person(String name) {
        this.name = name;
        idCounter.getAndIncrement();        
        this.id = idCounter.get();
    }

    public Person(String name, long id) {
        this.name = name;

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

    public long getId() {
        return id;
    }

    public String getName() {
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
            int I = i;
            new Thread(() -> {
                try {
                    Person m = new Person("Hans" + I );
                    System.out.println(m.getId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
