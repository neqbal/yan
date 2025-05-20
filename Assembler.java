import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class Assembler {
  
  List<String> code = new ArrayList<>();
  Machine machine;
  List<Byte> ByteCode = new ArrayList<>();
  
  Assembler(String file) {
    machine = new Machine();
    try {
      code = Files.readAllLines(Path.of(file));
    } catch(IOException e) {
      System.err.println(e);
    }
  }

  void start() {
    for(String s: code) {
      String opcode = "";
      String param1 = "";
      String param2 = "";
      int count=0;
      int i=0;
      for(char c: s.toCharArray()) {
        if(c == ' ') {
          count += 1;
          continue;
        }
        if(count == 0) {
          opcode += c;
        } else if(count == 1) {
          param1 += c;
        } else if(count == 2) {
          param2 += c;
        }
        i += 1;
      }
      System.out.println(opcode + " " + param1 + " " + param2);

      assemble(opcode, param1, param2);
    }

    byte[] byteArray = new byte[ByteCode.size()];
    for(int i=0; i<byteArray.length; i++) {
      byteArray[i] = ByteCode.get(i);
      System.out.printf("\\x%02x", byteArray[i]);
    }
    try {
      Files.write(Paths.get("output.txt"), byteArray, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    } catch(IOException e) {
      System.err.println(e);
    }
  }

  void assemble(String opcode, String param1, String param2) {
    int o = 0; 
    int p1 = 0;
    int p2 = 0;
    switch(opcode) {
      case "IMM":
        o = machine.conf_InstructionOP.get(Machine.Instruction.IMM);
        p1 = getReg(param1); 
        p2 = Integer.decode(param2);
        break;
      case "ADD":
        o =  machine.conf_InstructionOP.get(Machine.Instruction.ADD);
        p1 = getReg(param1); 
        p2 = getReg(param2);
        break;
      case "STK":
        o =  machine.conf_InstructionOP.get(Machine.Instruction.STK);
        p1 = getReg(param1);
        p2 = getReg(param2);
        break;
      case "STM":
        o = machine.conf_InstructionOP.get(Machine.Instruction.STM);
        p1 = getReg(param1);
        p2 = getReg(param2);
        break;
      case "LDM":
        o = machine.conf_InstructionOP.get(Machine.Instruction.LDM);
        p1 = getReg(param1);
        p2 = getReg(param2);
        break;
      case "CMP":
        o = machine.conf_InstructionOP.get(Machine.Instruction.CMP);
        p1 = getReg(param1);
        p2 = getReg(param2);
        break;
      case "JMP":
        o = machine.conf_InstructionOP.get(Machine.Instruction.JMP);
        p1 = getFlag(param1);
        p2 = getReg(param1);
        break; 
      case "SYS":
        o = machine.conf_InstructionOP.get(Machine.Instruction.SYS);
        p1 = getSys(param1);
        p2 = getReg(param2);
        break;
    }

    int ins = 0;
    ins |= o << machine.conf_InsByte.get(Machine.InstructionByte.opcode)*8;
    ins |= p1 << machine.conf_InsByte.get(Machine.InstructionByte.param1)*8;
    ins |= p2 << machine.conf_InsByte.get(Machine.InstructionByte.param2)*8;
    
    ByteCode.add((byte) (ins));
    ByteCode.add((byte) (ins >> 0x8));
    ByteCode.add((byte) (ins >> 0x10));
  }

  int getFlag(String param1) {
    char[] c = param1.toCharArray();
    int res = 0;
    for(char f: c) {
      switch(f) {
        case 'l':
          res |= machine.conf_FlagOffset.get(Machine.Flag.l);
          break;
        case 'g':
          res |= machine.conf_FlagOffset.get(Machine.Flag.g);
          break;
        case 'n':
          res |= machine.conf_FlagOffset.get(Machine.Flag.n);
          break;
        case 'z':
          res |= machine.conf_FlagOffset.get(Machine.Flag.z);
          break;
      }
    }

    return res;
  }

  int getSys(String param1) {
    switch(param1) {
      case "open":
        return machine.conf_SyscallOP.get(Machine.Syscall.open);
      case "read_mem":
        return machine.conf_SyscallOP.get(Machine.Syscall.read_memory);
      case "write":
        return machine.conf_SyscallOP.get(Machine.Syscall.write);
      case "exit":
        return machine.conf_SyscallOP.get(Machine.Syscall.exit);
      case "s":
        return machine.conf_RegisterOP.get(Machine.Register.s);
      case "i":
        return machine.conf_RegisterOP.get(Machine.Register.i);
      case "f":
        return machine.conf_RegisterOP.get(Machine.Register.f);
    }

    return 0;

  }
  int getReg(String param1) {
    switch(param1) {
      case "a":
        return machine.conf_RegisterOP.get(Machine.Register.a);
      case "b":
        return machine.conf_RegisterOP.get(Machine.Register.b);
      case "c":
        return machine.conf_RegisterOP.get(Machine.Register.c);
      case "d":
        return machine.conf_RegisterOP.get(Machine.Register.d);
      case "s":
        return machine.conf_RegisterOP.get(Machine.Register.s);
      case "i":
        return machine.conf_RegisterOP.get(Machine.Register.i);
      case "f":
        return machine.conf_RegisterOP.get(Machine.Register.f);
    }

    return 0;
  }
}

