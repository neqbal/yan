class Main {

  public static void main(String[] args) {
      Disassembler dis = new Disassembler("output.txt");
      dis.disass();
    if (args[0].equals("-d")) {

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
      Assembler ass = new Assembler("read_flag.yan");
      ass.start();
    }
  }
}
