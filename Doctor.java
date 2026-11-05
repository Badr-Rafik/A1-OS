import java.util.Random;

public class Doctor extends Thread {

    private int doctorId;
    private Hospital hospital;
    private Random random = new Random();

    public Doctor(Hospital hospital, int doctorId) {

        this.hospital = hospital;
        this.doctorId = doctorId;
    }

    @Override
    public void run() {

        try {

            while (true) {

                // Wait until a patient becomes available
                Patient patient = hospital.getNextPatient();

                // Stop signal
                if (patient == null) {

                    System.out.println(
                            "Doctor " + doctorId + " finished working."
                    );

                    break;
                }

                System.out.println(
                        "Doctor " + doctorId
                                + " is assigned to "
                                + patient.getPatientName() + "."
                );

                System.out.println(
                        "Doctor " + doctorId
                                + " is trying to acquire a treatment room."
                );

                // Try to acquire a treatment room within 3 seconds
                boolean acquired = hospital.acquireTreatmentRoom();

                if (acquired) {

                    System.out.println(
                            "Doctor " + doctorId
                                    + " acquired a treatment room."
                    );

                    try {

                        System.out.println(
                                "Doctor " + doctorId
                                        + " begins treating "
                                        + patient.getPatientName() + "."
                        );

                        // Random treatment duration: 1 to 5 seconds
                        int treatmentTime =
                                random.nextInt(4000) + 1000;

                        Thread.sleep(treatmentTime);

                        System.out.println(
                                "Doctor " + doctorId
                                        + " finished treating "
                                        + patient.getPatientName() + "."
                        );

                    } finally {

                        // Release the treatment room
                        hospital.releaseTreatmentRoom();

                        System.out.println(
                                "Doctor " + doctorId
                                        + " released the treatment room."
                        );
                    }

                    System.out.println(
                            "Doctor " + doctorId + " is now available."
                    );

                    System.out.println(
                            patient.getPatientName() + " left."
                    );

                    // Notify hospital that this patient is treated
                    hospital.patientTreated();

                } else {

                    System.out.println(
                            "Doctor " + doctorId
                                    + " could not acquire a treatment room."
                    );

                    System.out.println(
                            "Doctor " + doctorId
                                    + " is sending "
                                    + patient.getPatientName()
                                    + " back to the waiting room."
                    );

                    // Send the patient back (waits outside if the waiting room is full)
                    hospital.returnPatient(patient);

                    System.out.println(
                            "Doctor " + doctorId + " is now available."
                    );
                }
            }

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            System.out.println(
                    "Doctor " + doctorId + " was interrupted."
            );
        }
    }
}