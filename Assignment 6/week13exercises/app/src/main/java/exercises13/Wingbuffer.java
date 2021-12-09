package exercises13;
class WingBuffer {
  private final int iNull= -1;
  private final int size;
  private final WingStructure rep;

  public WingBuffer(int size) {
      this.size = size;
      rep = new WingStructure(size, iNull);
  }

  public int Deq() {
    while (true) {
      int range = rep.READ();
      for (int i = 0; i < range; i++) {
        int x = rep.SWAP(i);
        if (x != this.iNull) return x;
      }
    }
  }

  public void Enq(int x){
    int i = rep.INC();
    rep.STORE(i, x);
  }

  public static void main(String[] args) {
    WingBuffer wb = new WingBuffer(100);
    wb.Enq(50);
    int i = wb.Deq();
    System.out.println(i);
  }
}