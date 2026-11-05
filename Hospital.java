import java.util.InputMismatchException;
import java.util.Scanner;

public class Hospital {

    private WaitingRoom waitingRoom;

    private TreatmentRooms treatmentRooms;

    private Doctor[] doctors;

    private Patient[] patients;

    private int totalPatients;

    private int completedPatients = 0;

    private boolean shutdownStarted = false;

    // Reads the input from the console, then runs the simulation
    public void run() {

        Scanner scanner = new Scanner(System.in);

        int waitingRoomSize;
        int numberOfDoctors;
        int numberOfPatients;

        try {

            System.out.print("Waiting room capacity: ");
            waitingRoomSize = scanner.nextInt();

            System.out.print("Number of doctors: ");
            numberOfDoctors = scanner.nextInt();

            System.out.print("Number of patients: ");
            numberOfPatients = scanner.nextInt();

        } catch (InputMismatchException e) {

            System.out.println("Invalid input.");

            scanner.close();

            return;
        }

        scanner.close();

        run(waitingRoomSize, numberOfDoctors, numberOfPatients);
    }

    // Creates doctors and patients, then runs the simulation
    // (used by the console version and by the GUI)
    public void run(
            int waitingRoomSize,
            int numberOfDoctors,
            int numberOfPatients
    ) {

        if (waitingRoomSize <= 0
                || numberOfDoctors <= 0
                || numberOfPatients < 0) {

            System.out.println("Invalid input.");

            return;
        }

        setup(waitingRoomSize, numberOfDoctors, numberOfPatients);

        try {

            // Start doctors
            startDoctors();

            // Show the arriving patients
            printArrivingPatients();

            // Use one producer so patients enter the bounded buffer in arrival order.
            for (Patient patient : patients) {

                patient.run();
            }

            // Wait until all doctors finish
            waitForDoctors();

            System.out.println();

            System.out.println("All patients have been treated.");

            System.out.println("Simulation completed.");

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            System.out.println("Simulation interrupted.");
        }
    }

    // Create the shared resources, doctors and patients
    private void setup(
            int waitingRoomSize,
            int numberOfDoctors,
            int numberOfPatients
    ) {

        waitingRoom = new WaitingRoom(waitingRoomSize);

        treatmentRooms = new TreatmentRooms(2, 3000);

        totalPatients = numberOfPatients;

        doctors = new Doctor[numberOfDoctors];

        for (int i = 0; i < numberOfDoctors; i++) {

            doctors[i] = new Doctor(this, i + 1);
        }

        patients = new Patient[numberOfPatients];

        for (int i = 0; i < numberOfPatients; i++) {

            patients[i] = new Patient(
                    i + 1,
                    "P" + (i + 1),
                    20 + i,
                    this
            );
        }
    }

    private void printArrivingPatients() {

        if (patients.length == 0) {

            return;
        }

        System.out.println();

        System.out.println("Patients arriving:");

        StringBuilder names = new StringBuilder();

        for (Patient patient : patients) {

            names.append(patient.getPatientName()).append(" ");
        }

        System.out.println(names.toString().trim());

        System.out.println();
    }

    // Patient arrives
    public void patientArrived(Patient patient)
            throws InterruptedException {

        waitingRoom.addPatient(patient);
    }

    // Get the next patient (null means stop)
    public Patient getNextPatient(int doctorId)
            throws InterruptedException {

        return waitingRoom.removePatient(doctorId);
    }

    // Return patient to waiting room
    public void returnPatient(Patient patient)
            throws InterruptedException {

        waitingRoom.returnPatient(patient);
    }

    // Acquire treatment room
    public boolean acquireTreatmentRoom()
            throws InterruptedException {

        return treatmentRooms.acquire();
    }

    // Release treatment room
    public void treatmentStarted(int doctorId, Patient patient) {

        treatmentRooms.treatmentStarted(doctorId, patient);
    }

    // Finish treatment and release the room as one ordered event
    public void finishAndReleaseTreatmentRoom(int doctorId, Patient patient) {

        treatmentRooms.finishAndRelease(doctorId, patient);
    }

    // Release a room when treatment is interrupted
    public void releaseTreatmentRoom(int doctorId) {

        treatmentRooms.release(doctorId);
    }

    // Start doctors
    public void startDoctors()
            throws InterruptedException {

        for (Doctor doctor : doctors) {

            doctor.start();
        }

        // Handle the case where there are no patients
        if (totalPatients == 0) {

            sendStopSignals();
        }
    }

    // Called when a patient finishes treatment
    public void patientTreated()
            throws InterruptedException {

        boolean allPatientsTreated = false;

        synchronized (this) {

            completedPatients++;

            System.out.println(
                    "Completed patients: "
                            + completedPatients + "/" + totalPatients
            );

            if (completedPatients == totalPatients
                    && !shutdownStarted) {

                shutdownStarted = true;

                allPatientsTreated = true;
            }
        }

        if (allPatientsTreated) {

            sendStopSignals();
        }
    }

    // Send one stop signal to each doctor
    private void sendStopSignals()
            throws InterruptedException {

        for (int i = 0; i < doctors.length; i++) {

            waitingRoom.addStopSignal();
        }
    }

    // Wait until all doctors finish
    public void waitForDoctors()
            throws InterruptedException {

        for (Doctor doctor : doctors) {

            doctor.join();
        }
    }
}
