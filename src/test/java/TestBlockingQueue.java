import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TestBlockingQueue {
    public static void main(String[] args) {
        new TestBlockingQueue().test1();
    }

    public void test1() {
        BlockingQueue<Integer> blockingQueue = new LinkedBlockingQueue<>();
        Thread t1 = new Thread(() -> {});

        Thread t2 = new Thread(() -> {
            for (int i = 10; i < 20; i ++) {
                try {
                    Thread.sleep(1000);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });


        Thread t3 = new Thread(() -> {
            try {
                while (true) {
                    Integer val = blockingQueue.take();
                    System.out.println("get value");
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        t1.start();
        t2.start();
        t3.start();

        try {
            t1.join();
            t2.join();
            t3.join();

        } catch (Exception ex) {
            ex.printStackTrace();
        }



    }



}
