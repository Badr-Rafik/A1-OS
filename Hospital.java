public class Hospital {

    private WatingRoom waitingRoom;
    private TreatmentRooms treatmentRooms;
    private Doctor[] doctors;

    public Hospital(int waitingRoomSize, int numberOfDoctors) {

        waitingRoom = new WatingRoom(waitingRoomSize);

        // Two available treatment rooms
        treatmentRooms = new TreatmentRooms(2, 3000);

        doctors = new Doctor[numberOfDoctors];

        for (int i = 0; i < numberOfDoctors; i++) {
            //doctors[i] = new Doctor(this, i + 1);
        }
    }

    public void patiantArrived(Patiant patiant)
            throws InterruptedException {

        waitingRoom.AddPatiant(patiant);

        System.out.println(
                "Patient " + patiant.GetName() + " arrived at the hospital."
        );
    }

    public Patiant getNextPatiant()
            throws InterruptedException {

        return waitingRoom.MovePatiant();
    }

    public boolean acquireTreatmentRoom()
            throws InterruptedException {

        return treatmentRooms.acquire();
    }

    public void releaseTreatmentRoom() {

        treatmentRooms.release();
    }

    public void startDoctors() {

        for (Doctor doctor : doctors) {
            //doctor.start();
        }
    }
}