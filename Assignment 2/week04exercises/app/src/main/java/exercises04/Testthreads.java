package exercises04;

public class Testthreads {
    public static void main(String[] args) throws InterruptedException{

        final BoundedBuffer pc = new BoundedBuffer(5);
 
        for (int i = 0; i < 10; i ++)  {
        new Thread(() -> {
            try {
                pc.insert(100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                pc.take();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
}