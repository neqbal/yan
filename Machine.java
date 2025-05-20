import java.util.HashMap;
import java.util.Map;


public class Machine {
  
  static enum Register { a, b, c, d, s, i, f, NONE};
  static enum InstructionByte {opcode, param1, param2 }
  static enum Instruction {IMM, ADD, STK, STM, LDM, CMP, JMP, SYS}
  static enum Syscall {open, read_code, read_memory, write, sleep, exit}
  static enum Flag {l, g, e, n, z, astr}

  Map<Register, Integer> conf_RegOffset = new HashMap<>();
  Map<Integer, Register> conf_DescRegister = new HashMap<>();
  Map<Register, Integer> conf_RegisterOP = new HashMap<>();

  Map<Integer, Instruction> conf_DescInstruction = new HashMap<>();
  Map<Instruction, Integer> conf_InstructionOP = new HashMap<>();

  Map<Flag, Integer> conf_FlagOffset = new HashMap<>();
  Map<Integer, Flag> conf_DescFlag = new HashMap<>(); 

  Map<InstructionByte, Integer> conf_InsByte = new HashMap<>();
  
  Map<Integer, Syscall> conf_DescSyscall = new HashMap<>();
  Map<Syscall, Integer> conf_SyscallOP = new HashMap<>();

  public Machine() {
    
    conf_DescSyscall.put(0x01, Syscall.open);
    conf_DescSyscall.put(0x10, Syscall.read_code);
    conf_DescSyscall.put(0x02, Syscall.read_memory);
    conf_DescSyscall.put(0x20, Syscall.write);
    conf_DescSyscall.put(0x04, Syscall.sleep);
    conf_DescSyscall.put(0x08, Syscall.exit);

    conf_SyscallOP.put(Syscall.open, 0x01);
    conf_SyscallOP.put(Syscall.read_code, 0x10);
    conf_SyscallOP.put(Syscall.read_memory, 0x02);
    conf_SyscallOP.put(Syscall.write, 0x20);
    conf_SyscallOP.put(Syscall.sleep, 0x04);
    conf_SyscallOP.put(Syscall.exit, 0x08);

    conf_DescFlag.put(0x04, Flag.l);
    conf_DescFlag.put(0x02, Flag.g);
    conf_DescFlag.put(0x01, Flag.e);
    conf_DescFlag.put(0x10, Flag.n);
    conf_DescFlag.put(0x08, Flag.z);
    conf_DescFlag.put(0x00, Flag.astr);

    conf_InsByte.put(InstructionByte.opcode, 2);    
    conf_InsByte.put(InstructionByte.param1, 0);    
    conf_InsByte.put(InstructionByte.param2, 1);    

    conf_FlagOffset.put(Flag.l, 0x04);
    conf_FlagOffset.put(Flag.g, 0x02);
    conf_FlagOffset.put(Flag.e, 0x01);
    conf_FlagOffset.put(Flag.n, 0x10);
    conf_FlagOffset.put(Flag.z, 0x08);

    conf_RegOffset.put(Register.a, 0x00);
    conf_RegOffset.put(Register.b, 0x01);
    conf_RegOffset.put(Register.c, 0x02);
    conf_RegOffset.put(Register.d, 0x03);
    conf_RegOffset.put(Register.s, 0x04);
    conf_RegOffset.put(Register.i, 0x05);
    conf_RegOffset.put(Register.f, 0x06);

    conf_DescRegister.put(0x01, Register.a);
    conf_DescRegister.put(0x04, Register.b);
    conf_DescRegister.put(0x40, Register.c);
    conf_DescRegister.put(0x20, Register.d);
    conf_DescRegister.put(0x10, Register.s);
    conf_DescRegister.put(0x02, Register.i);
    conf_DescRegister.put(0x08, Register.f);
    
    conf_RegisterOP.put(Register.a, 0x01);
    conf_RegisterOP.put(Register.b, 0x04);
    conf_RegisterOP.put(Register.c, 0x40);
    conf_RegisterOP.put(Register.d, 0x20);
    conf_RegisterOP.put(Register.s, 0x10);
    conf_RegisterOP.put(Register.i, 0x02);
    conf_RegisterOP.put(Register.f, 0x08);

    conf_DescInstruction.put(0x80, Instruction.IMM);
    conf_DescInstruction.put(0x02, Instruction.ADD);
    conf_DescInstruction.put(0x20, Instruction.STK);
    conf_DescInstruction.put(0x10, Instruction.STM);
    conf_DescInstruction.put(0x08, Instruction.LDM);
    conf_DescInstruction.put(0x40, Instruction.CMP);
    conf_DescInstruction.put(0x01, Instruction.JMP);
    conf_DescInstruction.put(0x04, Instruction.SYS);
  
    conf_InstructionOP.put(Instruction.IMM, 0x80);
    conf_InstructionOP.put(Instruction.ADD, 0x02);
    conf_InstructionOP.put(Instruction.STK, 0x20);
    conf_InstructionOP.put(Instruction.STM, 0x10);
    conf_InstructionOP.put(Instruction.LDM, 0x08);
    conf_InstructionOP.put(Instruction.CMP, 0x40);
    conf_InstructionOP.put(Instruction.JMP, 0x01);
    conf_InstructionOP.put(Instruction.SYS, 0x04);
  }
}
