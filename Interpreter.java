import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class Interpreter {

  byte[] ByteCode;
  int[] vmMemory = new int[0x420];

  Machine machine;

  Interpreter(String file) {
    machine = new Machine();
    try {
      ByteCode = Files.readAllBytes(Path.of(file));

      for(int i=0; i<ByteCode.length; i++) {
        vmMemory[i] = ByteCode[i]&0xFF;
      }
    } catch (IOException e) {
      System.err.println(e);
    }
  }

  public void start() {
    do {
      int i = vmMemory[0x405]&0xFF;
      vmMemory[0x405] = i + 1;
      interpret_instruction(i*3);
    } while(true);
  }
  
  void write_register(Machine.Register reg, int val) {
    vmMemory[0x400 + machine.conf_RegOffset.get(reg)] = val;
  }

  void write_memory(int offset, int val) {
    vmMemory[0x300 + offset] = val;
  }

  int read_register(Machine.Register reg) {
    return vmMemory[0x400 + machine.conf_RegOffset.get(reg)];
  }

  int read_memory(int offset) {
    return vmMemory[0x300 + offset];
  }
  
  void interpret_imm(Machine.Register reg, int imm) {
    write_register(reg, imm);
  }
  
  void interpret_add(Machine.Register reg1, Machine.Register reg2) {

    int val1 = read_register(reg1);
    int val2 = read_register(reg2);

    write_register(reg1, (val1 + val2)&0xFF);
  }

  void interpret_stk(Machine.Register reg1, Machine.Register reg2) {

    if(reg2 != Machine.Register.NONE) {
      write_register(Machine.Register.s, read_register(Machine.Register.s)+1);
      write_memory(read_register(Machine.Register.s), read_register(reg2)); 
    }

    if(reg1 != Machine.Register.NONE) {
      write_register(reg1, read_memory(read_register(Machine.Register.s)));
      write_register(Machine.Register.s, read_register(Machine.Register.s)-1);
    }
  }

  void interpret_stm(Machine.Register reg1, Machine.Register reg2) {
    int offset = read_register(reg1);
    int val = read_register(reg2);
    write_memory(offset, val);
  }

  void interpret_ldm(Machine.Register reg1, Machine.Register reg2) {
    int offset = read_register(reg2);
    int val = read_memory(offset);
    write_register(reg1, val);
  }

  void interpret_cmp(Machine.Register reg1, Machine.Register reg2) {
    int val1 = read_register(reg1);
    int val2 = read_register(reg2);
    write_register(Machine.Register.f, 0);
    if(val1 < val2) {
      int flag = read_register(Machine.Register.f);
      write_register(Machine.Register.f, flag | machine.conf_FlagOffset.get(Machine.Flag.l));
    }

    if(val1 > val2) {
      int flag = read_register(Machine.Register.f);
      write_register(Machine.Register.f, flag | machine.conf_FlagOffset.get(Machine.Flag.g));
    }

    if(val1 == val2) {
      int flag = read_register(Machine.Register.f);
      write_register(Machine.Register.f, flag | machine.conf_FlagOffset.get(Machine.Flag.e));
    }

    if(val1 != val2) {
      int flag = read_register(Machine.Register.f);
      write_register(Machine.Register.f, flag | machine.conf_FlagOffset.get(Machine.Flag.n));
    }

    if(val1 == 0 && val2 == 0) {
      int flag = read_register(Machine.Register.f);
      write_register(Machine.Register.f, flag | machine.conf_FlagOffset.get(Machine.Flag.z));
    }
  }

  boolean interpret_jmp(int cond, Machine.Register reg) {
    int flag = read_register(Machine.Register.f);
    int label = read_register(reg);
    if(cond == 0 || (flag & cond) != 0 ) {
      write_register(Machine.Register.i, label);
      return true;
    } 

    return false;
  }

  void interpret_sys(int sysType, Machine.Register reg) {
    Machine.Syscall sys = machine.conf_DescSyscall.get(sysType);

    int a = read_register(Machine.Register.a);
    int b = read_register(Machine.Register.b);
    int c = read_register(Machine.Register.c);
    int i;

    switch(sys) {
      case Machine.Syscall.open:
        break;
      case Machine.Syscall.read_code:
        break;
      case Machine.Syscall.read_memory:

        try (Scanner sc = new Scanner(System.in)) {
          String buff = sc.next();
          for(i=0; i<(0x100 - b <= c ? 0x100 - b : c ) && i < buff.length() ; i++) {
            vmMemory[0x300 + b + i] = buff.charAt(i);
          } 
          sc.close();
        }
        write_register(reg, i);

        break;
      case Machine.Syscall.write:
        for(i=0; i<(0x100 - b <= c ? 0x100 - b : c ); i++) {
          System.out.print(vmMemory[0x300 + b + i] + " ");
        } 
        System.out.println();
        break;
      case Machine.Syscall.sleep:
        break;
      case Machine.Syscall.exit:
        System.exit(a); 
        break;
    }
  }
  
  Machine.Register describe_register(int a) {
    return machine.conf_DescRegister.getOrDefault(a, Machine.Register.NONE);
  }

  public void interpret_instruction(int i) {
    display_register();
    int opcodeOffset = machine.conf_InsByte.get(Machine.InstructionByte.opcode);
    int param1Offset = machine.conf_InsByte.get(Machine.InstructionByte.param1);
    int param2Offset = machine.conf_InsByte.get(Machine.InstructionByte.param2);

    int opcode = vmMemory[i + opcodeOffset];
    int param1 = vmMemory[i + param1Offset];
    int param2 = vmMemory[i + param2Offset];

    System.out.printf("op:0x%02x param1:0x%02x param2:0x%02x\n", opcode, param1, param2);
    Machine.Instruction ins = machine.conf_DescInstruction.get(opcode & 0xFF);
    System.out.print(ins + " ");
    
    Machine.Register reg1 = Machine.Register.NONE;
    Machine.Register reg2 = Machine.Register.NONE;
    switch(ins) {
      case Machine.Instruction.IMM:
        reg1 = describe_register(param1);
        System.out.println(reg1 + " " + String.format("0x%02x",param2));
        interpret_imm(reg1, param2);
        break;

      case Machine.Instruction.SYS:
        reg1 = describe_register(param2);
        interpret_sys(param1&0xFF, reg1);
        System.out.print(String.format("0x%02x", param1) + " ");
        System.out.println(machine.conf_DescRegister.getOrDefault(param2 & 0xFF, Machine.Register.NONE));
        break;

      case Machine.Instruction.STK:
        reg1 = describe_register(param1);
        reg2 = describe_register(param2); 
        System.out.println(reg1 + " " + reg2);
        interpret_stk(reg1, reg2);
        break;
      case Machine.Instruction.STM:
        reg1 = describe_register(param1);
        reg2 = describe_register(param2);
        System.out.println(reg1 + " " + reg2);
        interpret_stm(reg1, reg2);
        break;
      case Machine.Instruction.LDM:
        reg1 = describe_register(param1);
        reg2 = describe_register(param2);
        System.out.println(reg1 + " " + reg2);
        interpret_ldm(reg1, reg2);
        break;
      case Machine.Instruction.JMP:
        reg2 = describe_register(param2);
        System.out.println(String.format("0x%02x",param1) + " " + reg2);
        interpret_jmp(param1&0xFF, reg2);
        break;
      case Machine.Instruction.CMP:
        reg1 = describe_register(param1);
        reg2 = describe_register(param2);
        System.out.println(reg1 + " " + reg2);
        interpret_cmp(reg1, reg2);
        break;
      case Machine.Instruction.ADD:
        reg1 = describe_register(param1);
        reg2 = describe_register(param2);
        System.out.println(reg1 + " " + reg2);
        interpret_add(reg1, reg2);
        break;
    }

  }

  void display_register() {
    System.out.printf("a:0x%02x b:0x%02x c:0x%02x d:0x%02x s:0x%02x i:0x%02x f:0x%02x\n",
    vmMemory[0x400], vmMemory[0x401], vmMemory[0x402], vmMemory[0x403], vmMemory[0x404], vmMemory[0x405], vmMemory[0x406]);
  }
}
