public class TreatmentRooms {

    private Semaphore rooms;
    private long timeout;
    private final Object eventLock = new Object();

    public TreatmentRooms(int numberOfRooms, long timeout) {

        rooms = new Semaphore(numberOfRooms);
        this.timeout = timeout;
    }

    public boolean acquire() throws InterruptedException {

        return rooms.acquire(timeout);
    }

    public void treatmentStarted(int doctorId, Patient patient) {

        synchronized (eventLock) {

            System.out.println(
                    "Doctor " + doctorId + " acquired a treatment room.\n"
                            + "Doctor " + doctorId + " begins treating "
                            + patient.getPatientName() + "."
            );
        }
    }

    public void finishAndRelease(int doctorId, Patient patient) {

        synchronized (eventLock) {

            rooms.release();

            System.out.println(
                    "Doctor " + doctorId + " finished treating "
                            + patient.getPatientName() + ".\n"
                            + "Doctor " + doctorId
                            + " released the treatment room."
            );
        }
    }

    public void release(int doctorId) {

        synchronized (eventLock) {

            rooms.release();

            System.out.println(
                    "Doctor " + doctorId
                            + " released the treatment room."
            );
        }
    }
}
