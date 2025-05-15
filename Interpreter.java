import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Interpreter {

  byte[] ByteCode;
  byte[] vmMemory = new byte[0x420];

  Machine machine;

  Interpreter(String file) {
    machine = new Machine();
    try {
      ByteCode = Files.readAllBytes(Path.of(file));

      for(int i=0; i<ByteCode.length; i++) {
        vmMemory[i] = ByteCode[i];
      }
    } catch (IOException e) {
      System.err.println(e);
    }
  }

  public void start() {
    do {
      int i = vmMemory[0x405]&0xFF;
      vmMemory[0x405] = (byte) (i + 1);
      interpret_instruction(i);
    } while(false);
  }

  void write_memory(int offset, int val) {
    vmMemory[0x300 + offset] = (byte) val;
  }
  
  public void interpret_imm(Machine.Register reg, int imm) {
    System.out.println(reg + " " + imm);
    vmMemory[0x400 + machine.conf_RegOffset.get(reg)] = (byte) imm;
  }
  
  public void interpret_add(Machine.Register reg1, Machine.Register reg2) {
    System.out.println(reg1 + " " + reg2);
    vmMemory[0x400 + machine.conf_RegOffset.get(reg1)] = (byte) (vmMemory[0x400 + machine.conf_RegOffset.get(reg1)] + vmMemory[0x400 + machine.conf_RegOffset.get(reg2)]);
  }

  public void interpret_stk(Machine.Register reg1, Machine.Register reg2) {
    int stkPtr = vmMemory[0x400 + machine.conf_RegOffset.get(Machine.Register.s)]&0xFF;

    if(reg2 != Machine.Register.NONE) {
      vmMemory[0x300 + stkPtr ] = vmMemory[0x400 + machine.conf_RegOffset.get(reg2)];
    }

    if(reg1 != Machine.Register.NONE) {
      vmMemory[0x400 + machine.conf_RegOffset.get(reg1)] = vmMemory[0x300 + stkPtr];
      vmMemory[0x400 + machine.conf_RegOffset.get(Machine.Register.s)] -= 1;
    }
  }

  void interpret_stm(Machine.Register reg1, Machine.Register reg2) {
    int offset = vmMemory[0x400 + machine.conf_RegOffset.get(reg1)]&0xFF;

    vmMemory[0x300 + offset] = vmMemory[0x400 + machine.conf_RegOffset.get(reg2)];  
  }

  public void interpret_instruction(int i) {
    display_register();
    int opcodeOffset = machine.conf_InsByte.get(Machine.InstructionByte.opcode);
    int param1Offset = machine.conf_InsByte.get(Machine.InstructionByte.param1);
    int param2Offset = machine.conf_InsByte.get(Machine.InstructionByte.param2);

    byte opcode = vmMemory[i + opcodeOffset];
    byte param1 = vmMemory[i + param1Offset];
    byte param2 = vmMemory[i + param2Offset];

    System.out.printf("op:0x%02x param1:0x%02x param2:0x%02x\n", opcode, param1, param2);
    Machine.Instruction ins = machine.conf_DescInstruction.get(opcode & 0xFF);
    System.out.print(ins + " ");

    switch(ins) {
      case Machine.Instruction.IMM:
        interpret_imm(machine.conf_DescRegister.get(param1 & 0xFF), param2&0xFF);
        break;
      
      case Machine.Instruction.SYS:
        System.out.print(String.format("0x%02x", param1) + " ");
        System.out.println(machine.conf_DescRegister.getOrDefault(param2 & 0xFF, Machine.Register.NONE));
        break;

      case Machine.Instruction.STK:
        interpret_stk(machine.conf_DescRegister.getOrDefault(param1&0xFF, Machine.Register.NONE), machine.conf_DescRegister.getOrDefault(param2&0xFF, Machine.Register.NONE));
        break;
      case Machine.Instruction.STM:
        System.out.print("*"+machine.conf_DescRegister.getOrDefault(param1&0xFF, Machine.Register.NONE) + " ");
        System.out.println(machine.conf_DescRegister.getOrDefault(param2&0xFF, Machine.Register.NONE) + " ");
        break;
      case Machine.Instruction.LDM:
        System.out.print(machine.conf_DescRegister.getOrDefault(param1&0xFF, Machine.Register.NONE) + " ");
        System.out.println("*" + machine.conf_DescRegister.getOrDefault(param2&0xFF, Machine.Register.NONE) + " ");
        break;
      case Machine.Instruction.JMP:
        StringBuilder sb = new StringBuilder();

        if( ((param1&0xFF)&machine.conf_FlagOffset.get(Machine.Flag.l)) == 0) {
          sb.append(Machine.Flag.l); 
        } else if(((param1&0xFF)&machine.conf_FlagOffset.get(Machine.Flag.g)) == 0) {
          sb.append(Machine.Flag.g);
        } else if(((param1&0xFF)&machine.conf_FlagOffset.get(Machine.Flag.e)) == 0) {
          sb.append(Machine.Flag.e);
        } else if(((param1&0xFF)&machine.conf_FlagOffset.get(Machine.Flag.n)) == 0) {
          sb.append(Machine.Flag.n);
        } else if(((param1&0xFF)&machine.conf_FlagOffset.get(Machine.Flag.z)) == 0) {
          sb.append(Machine.Flag.z); 
        } else if(((param1&0xFF)&machine.conf_FlagOffset.get(Machine.Flag.astr)) == 0) {
          sb.append("*");
        }

        System.out.print(sb.toString() + " ");
        System.out.println(machine.conf_DescRegister.getOrDefault(param2&0xFF, Machine.Register.NONE));
        
        break;
      case Machine.Instruction.CMP:
        System.out.print(machine.conf_DescRegister.getOrDefault(param1&0xFF, Machine.Register.NONE) + " ");
        System.out.println(machine.conf_DescRegister.getOrDefault(param2&0xFF, Machine.Register.NONE));
        break;
      case Machine.Instruction.ADD:
        interpret_add(machine.conf_DescRegister.getOrDefault(param1&0xFF, Machine.Register.NONE), machine.conf_DescRegister.getOrDefault(param2&0xFF, Machine.Register.NONE));
        break;
    }

  }

  void display_register() {
    System.out.printf("a:0x%02x b:0x%02x c:0x%02x d:0x%02x s:0x%02x i:0x%02x f:0x%02x",
    vmMemory[0x400], vmMemory[0x401], vmMemory[0x402], vmMemory[0x403], vmMemory[0x404], vmMemory[0x405], vmMemory[0x406]);
  }
}
