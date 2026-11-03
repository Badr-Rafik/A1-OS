public class Patiant extends Thread {

    private int PatiantId;
    private String name;
    private int age;
    private Hospital hospital;

    public Patiant(int PatiantId, String name, int age, Hospital hospital) {
        this.PatiantId = PatiantId;
        this.name = name;
        this.age = age;
        this.hospital = hospital;
    }


    public int getPatiantId() {
        return PatiantId;
    }

    public String GetName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    @Override
    public void run() {
        hospital.patiantArrived(this);
    }


}
