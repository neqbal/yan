class Main {

  public static void main(String[] args) {
    if (args[0].equals("-d")) {
      Disassembler dis = new Disassembler(args[1]);
      dis.disass();
    }

    if (args[0].equals("-i")) {
      Interpreter inter = new Interpreter(args[1], args[2]);
      inter.start();
    }

    if(args[0].equals("-dbg")) {
      Debugger dbg = new Debugger(args[1], args[2]);
      dbg.start();
    }

    if(args[0].equals("-a")) {
      Assembler ass = new Assembler(args[1]);
      ass.start();
    }
  }
}
