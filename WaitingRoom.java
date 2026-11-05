public class WaitingRoom {

    private int size;

    private Patient[] store;

    private int input = 0;
    private int output = 0;

    private Semaphore empty;
    private Semaphore filled;

    private Semaphore mutexIn;
    private Semaphore mutexOut;

    public WaitingRoom(int size) {

        if (size <= 0) {
            throw new IllegalArgumentException(
                    "Waiting room size must be greater than zero"
            );
        }

        this.size = size;

        store = new Patient[size];

        empty = new Semaphore(size);
        filled = new Semaphore(0);

        mutexIn = new Semaphore(1);
        mutexOut = new Semaphore(1);
    }

    // A new patient arrives at the hospital
    public void addPatient(Patient patient)
            throws InterruptedException {

        System.out.println(
                patient.getPatientName() + " arrived."
        );

        enter(patient);
    }

    // A patient sent back by his doctor tries to enter again
    // (he waits outside if the waiting room is full)
    public void returnPatient(Patient patient)
            throws InterruptedException {

        enter(patient);
    }

    // Common entering logic for arriving and returning patients
    private void enter(Patient patient)
            throws InterruptedException {

        if (!empty.tryAcquire()) {

            System.out.println(
                    "The waiting room is full."
            );

            System.out.println(
                    patient.getPatientName()
                            + " is waiting outside for an available seat."
            );

            empty.acquire();
        }

        mutexIn.acquire();

        try {

            store[input] = patient;

            input = (input + 1) % size;

        } finally {

            mutexIn.release();
        }

        // Print before signaling the doctors, so "entered the waiting room"
        // always appears before "Doctor X is assigned to ..."
        System.out.println(
                patient.getPatientName() + " entered the waiting room."
        );

        filled.release();
    }

    // Remove a patient (null means a stop signal)
    public Patient removePatient()
            throws InterruptedException {

        filled.acquire();

        mutexOut.acquire();

        Patient patient;

        try {

            patient = store[output];

            store[output] = null;

            output = (output + 1) % size;

        } finally {

            mutexOut.release();
        }

        // The seat becomes free as soon as the doctor takes the patient
        empty.release();

        return patient;
    }

    // Add a stop signal for a doctor
    public void addStopSignal()
            throws InterruptedException {

        empty.acquire();

        mutexIn.acquire();

        try {

            store[input] = null;

            input = (input + 1) % size;

        } finally {

            mutexIn.release();
        }

        filled.release();
    }
}