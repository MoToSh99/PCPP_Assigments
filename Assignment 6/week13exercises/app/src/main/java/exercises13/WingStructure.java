package exercises13;
class WingStructure{

  private int[] items; 
  private int back;
  private int iNull;

  public WingStructure(int size, int iNull){ 
    items = new int[size]; 
    for (int j = 0; j<size; j++) items[j] = iNull;
    this.iNull = iNull;
    back = 0;
  }

  public synchronized int INC(){
    int old = this.back;
    this.back = this.back + 1 % this.items.length;
    return old;
  }

  public synchronized int SWAP(int i){
    int old = items[i];
    items[i] = this.iNull;
    return old;
  }

  public synchronized int READ() {
    return back;
  }

  public synchronized void STORE(int i, int x) {
    items[i] = x;
  }
}
