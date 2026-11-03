public class Semaphore {

    private int permits;

    public Semaphore(int permits) {
        this.permits = permits;
    }

    public synchronized void acquire() throws InterruptedException {
        while (permits == 0) {
            wait();
        }
        permits--;
    }

    public synchronized boolean acquire(long timeout) throws InterruptedException {
        
        long startTime = System.currentTimeMillis();
        long remainingTime = timeout;

        while (permits == 0 ) {
            if (remainingTime <= 0) {
                return false;
            }
            wait(remainingTime);
            long elapsedTime = System.currentTimeMillis() - startTime;
            remainingTime = timeout - elapsedTime;
        }

        permits--;
        return true;

    }

    public synchronized void release() {
        permits++;
        notifyAll();
    }

}
