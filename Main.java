class Main {

  public static void main(String[] args) {
    if (args[0].equals("-d")) {
      Disassembler dis = new Disassembler(args[1]);
      dis.disass();
    }

    if (args[0].equals("-i")) {
      Interpreter inter = new Interpreter(args[1]);
      inter.start();
    }
  }
}
