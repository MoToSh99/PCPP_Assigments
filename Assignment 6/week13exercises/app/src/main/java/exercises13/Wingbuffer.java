package exercises13;
class WingBuffer {
  private final int iNull= -1;
  private final int size;
  private final WingStructure rep;

public WingBuffer(int size) {
    this.size= size;
    rep= new WingStructure(size, iNull);
  }

  public int Deq() {
    //TO DO implement

  }

  public void Enq(int x){
    //TO DO implement

  } 
}