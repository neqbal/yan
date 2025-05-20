public class RandomC {
  static {
    System.loadLibrary("crandom");
  }

  public native int getRandom();
}
