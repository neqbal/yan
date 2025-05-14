import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Disassembler {

  public byte[] ByteCode;
  Machine machine;
  Disassembler(String file) {
    machine = new Machine();
    try {
      ByteCode = Files.readAllBytes(Path.of(file));
      System.out.println("Reading ");
      for(int i=0; i<9; i++) {
        System.out.printf("Byte %d: 0x%02x\n", i, ByteCode[i]);
      }
    } catch (IOException e) {
      System.err.println(e);
    }
  }

  public void disass() {
    int opcodeOffset = machine.conf_InsByte.get(Machine.InstructionByte.opcode);
    int param1Offset = machine.conf_InsByte.get(Machine.InstructionByte.param1);
    int param2Offset = machine.conf_InsByte.get(Machine.InstructionByte.param2);

    for(int i=0; i<ByteCode.length-3; i+=3) {
      byte opcode = ByteCode[i+opcodeOffset];
      byte param1 = ByteCode[i+param1Offset];
      byte param2 = ByteCode[i+param2Offset];
      
      // System.out.println(String.format("[op]: 0x%02x", opcode) + " " + String.format("[param1]: 0x%02x",param1) + " " + String.format("[param2]: 0x%02x", param2) + " ");
      Machine.Instruction ins = machine.conf_DescInstruction.get(opcode & 0xFF);
      System.out.print(ins + " ");
      switch(ins) {
        case Machine.Instruction.IMM:
          System.out.print(machine.conf_DescRegister.get(param1 & 0xFF) + " ");
          System.out.println(String.format("0x%02x",param2));
          break;
        
        case Machine.Instruction.SYS:
          System.out.print(String.format("0x%02x", param1) + " ");
          System.out.println(machine.conf_DescRegister.getOrDefault(param2 & 0xFF, Machine.Register.NONE));
          break;

        case Machine.Instruction.STK:
          System.out.print(machine.conf_DescRegister.getOrDefault(param1&0xFF, Machine.Register.NONE) + " ");
          System.out.println(machine.conf_DescRegister.getOrDefault(param2&0xFF, Machine.Register.NONE) + " ");
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
          System.out.print(machine.conf_DescRegister.getOrDefault(param1&0xFF, Machine.Register.NONE) + " ");
          System.out.println(machine.conf_DescRegister.getOrDefault(param2&0xFF, Machine.Register.NONE));
          break;
      }
    }
  }
}
