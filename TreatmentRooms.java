public class TreatmentRooms {

    private Semaphore rooms;
    private long timeout;

    public TreatmentRooms(int numberOfRooms, long timeout) {

        rooms = new Semaphore(numberOfRooms);
        this.timeout = timeout;
    }

    public boolean acquire() throws InterruptedException {

        return rooms.acquire(timeout);
    }

    public void release() {

        rooms.release();
    }
}