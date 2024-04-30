import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CrptApi {
    private final Lock lock = new ReentrantLock();
    private int requestLimit;
    private long lastRequestTime;
    private TimeUnit timeUnit;

    public CrptApi(TimeUnit timeUnit, int requestLimit) {
        this.timeUnit = timeUnit;
        this.requestLimit = requestLimit;
        this.lastRequestTime = System.currentTimeMillis();
    }

    public void createDocument(String document, String signature) {
        lock.lock();
        try {
            long currentTime = System.currentTimeMillis();
            long timeElapsed = currentTime - lastRequestTime;
            long timeLimitMillis = timeUnit.toMillis(1);

            if (timeElapsed >= timeLimitMillis) {
                lastRequestTime = currentTime;
            } else if (timeElapsed < timeLimitMillis && requestLimit > 0) {
                try {
                    Thread.sleep(timeLimitMillis - timeElapsed);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                lastRequestTime = System.currentTimeMillis();
            }

            // Simulate API call
            System.out.println("Document created: " + document);
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        CrptApi api = new CrptApi(TimeUnit.SECONDS, 5);
        for (int i = 0; i < 10; i++) {
            String document = "Document " + i;
            String signature = "Signature " + i;
            new Thread(() -> api.createDocument(document, signature)).start();
        }
    }
}
