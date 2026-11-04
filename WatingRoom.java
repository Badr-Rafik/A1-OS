public class WatingRoom {

    private int size;

    public WatingRoom(int size) {
        this.size = size;
    }

    private Patiant store [] = new Patiant [size];
    private int input = 0;
    private int output = 0;

    Semaphore empty = new Semaphore(size);
    Semaphore filled = new Semaphore(0);

    public void AddPatiant(Patiant patiant) throws InterruptedException{

        empty.acquire(); 
        store[input] = patiant;

        input++;
        input %= size;

        filled.release();
    }

    public Patiant MovePatiant() throws InterruptedException {

        filled.acquire();
        Patiant patiant = store[output];

        output++;
        output%=size;

        empty.release();
        return patiant;
    }
}
