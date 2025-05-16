class Main {

  public static void main(String[] args) {
    Interpreter inter = new Interpreter("output.txt");
    inter.start();
    if (args[0].equals("-d")) {
      Disassembler dis = new Disassembler(args[1]);
      dis.disass();
    }

    if (args[0].equals("-i")) {
      
    }
  }
}
