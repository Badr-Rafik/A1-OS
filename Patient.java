public class Patient extends Thread {

    private int patientId;
    private String name;
    private int age;

    private Hospital hospital;

    public Patient(
            int patientId,
            String name,
            int age,
            Hospital hospital
    ) {

        this.patientId = patientId;
        this.name = name;
        this.age = age;
        this.hospital = hospital;
    }

    public int getPatientId() {

        return patientId;
    }

    public String getPatientName() {

        return name;
    }

    public int getAge() {

        return age;
    }

    @Override
    public void run() {

        try {

            hospital.patientArrived(this);

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            System.out.println(
                    name + " was interrupted."
            );
        }
    }
}