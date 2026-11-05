public class Semaphore {

    private int permits;

    public Semaphore(int permits) {

        if (permits < 0) {
            throw new IllegalArgumentException(
                    "Permits cannot be negative"
            );
        }

        this.permits = permits;
    }

    public synchronized void acquire()
            throws InterruptedException {

        while (permits == 0) {

            wait();
        }

        permits--;
    }

    public synchronized boolean acquire(long timeout)
            throws InterruptedException {

        long startTime = System.nanoTime();

        long timeoutNanos = timeout * 1_000_000L;

        long remainingTime = timeoutNanos;

        while (permits == 0) {

            if (remainingTime <= 0) {

                return false;
            }

            long millis = remainingTime / 1_000_000L;

            int nanos = (int) (remainingTime % 1_000_000L);

            wait(millis, nanos);

            long elapsedTime = System.nanoTime() - startTime;

            remainingTime = timeoutNanos - elapsedTime;
        }

        permits--;

        return true;
    }

    public synchronized boolean tryAcquire() {

        if (permits == 0) {

            return false;
        }

        permits--;

        return true;
    }

    public synchronized void release() {

        permits++;

        notifyAll();
    }
}