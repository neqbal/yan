import java.util.Scanner;

public class Debugger {
  Interpreter intp;

  Debugger(String file, String mem) {
    intp = new Interpreter(file, mem);
  }

  void start() {
    System.out.println("Debugger started");
    Scanner sc = new Scanner(System.in); // Open scanner once
    intp.setScanner(sc);
    String cmd = "";
    String[] cmdList = new String[3];
    while (true) {
      System.out.print("ydbg> ");

      try {
        cmd = sc.nextLine(); // Read next token
      } catch (Exception e) {
        System.err.println(e);
        break; // Exit loop if input fails
      }
      if(cmd != "") cmdList = cmd.split(" ");
      System.out.println(cmdList[0]);
      switch (cmdList[0]) {
        case "ni":
          next_ins();
          break;
        case "q":
          System.exit(2);
          break;
        case "d":
          display_memory(cmdList);
          break;
        case "set":
          overwrite(cmdList);
          break;
        default:
          System.out.println("Unknown command: " + cmd);
      }
    }

    sc.close();

    // Optional: sc.close(); // You may close it here if the loop ends
  }
  
  void overwrite(String[] cmdList) {
    if(cmdList.length != 3) return;
    int offset = Integer.decode(cmdList[1]);
    int val = Integer.decode(cmdList[2]);

    intp.write_memory(offset, val);

    System.out.printf("0x%02x: 0x%02x\n", offset, intp.read_memory(0, offset));
  }

  void display_memory(String[] cmdList) {
    if(cmdList.length != 3) return;
    int offset = Integer.decode(cmdList[1]);
    for(int i=0; i< Integer.decode(cmdList[2]); i++) {
      System.out.printf("0x%02x: 0x%02x\n", offset+i, intp.read_memory(offset, i));
    }
  }

  void next_ins() {
    intp.interpret_instruction(intp.get_next_ins());
  }
}
