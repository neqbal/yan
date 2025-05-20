import java.util.HashMap;
import java.util.Map;


public class Machine {
  private static final RandomC randGen = new RandomC();

  public int getRandom() {
    return randGen.getRandom();
  }
   
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
  
  int[] VALUES = {0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80};

  void shuffleValues() {
    for(int i=0; i<0xFFFF; i++) {
      int a = getRandom();
      int b = getRandom();
      int c = VALUES[a % 8];
      VALUES[a % 8] = VALUES[b % 8];
      VALUES[b % 8] = c;
    }
  }

  void initReg() {
    conf_RegOffset.put(Register.a, 0x00);
    conf_RegOffset.put(Register.b, 0x01);
    conf_RegOffset.put(Register.c, 0x02);
    conf_RegOffset.put(Register.d, 0x03);
    conf_RegOffset.put(Register.s, 0x04);
    conf_RegOffset.put(Register.i, 0x05);
    conf_RegOffset.put(Register.f, 0x06);

    conf_DescRegister.put(VALUES[0], Register.a);
    conf_DescRegister.put(VALUES[1], Register.b);
    conf_DescRegister.put(VALUES[2], Register.c);
    conf_DescRegister.put(VALUES[3], Register.d);
    conf_DescRegister.put(VALUES[4], Register.s);
    conf_DescRegister.put(VALUES[5], Register.i);
    conf_DescRegister.put(VALUES[6], Register.f);
    
    conf_RegisterOP.put(Register.a, VALUES[0]);
    conf_RegisterOP.put(Register.b, VALUES[1]);
    conf_RegisterOP.put(Register.c, VALUES[2]);
    conf_RegisterOP.put(Register.d, VALUES[3]);
    conf_RegisterOP.put(Register.s, VALUES[4]);
    conf_RegisterOP.put(Register.i, VALUES[5]);
    conf_RegisterOP.put(Register.f, VALUES[6]);

    System.out.println(Register.a + " " + conf_RegisterOP.get(Register.a));
    System.out.println(Register.b + " " + conf_RegisterOP.get(Register.b));
    System.out.println(Register.c + " " + conf_RegisterOP.get(Register.c));
    System.out.println(Register.d + " " + conf_RegisterOP.get(Register.d));
    System.out.println(Register.s + " " + conf_RegisterOP.get(Register.s));
    System.out.println(Register.i + " " + conf_RegisterOP.get(Register.i));
    System.out.println(Register.f + " " + conf_RegisterOP.get(Register.f));

  }

  void initIns() {
    conf_DescInstruction.put(VALUES[0], Instruction.IMM);
    conf_DescInstruction.put(VALUES[1], Instruction.STK);
    conf_DescInstruction.put(VALUES[2], Instruction.ADD);
    conf_DescInstruction.put(VALUES[3], Instruction.STM);
    conf_DescInstruction.put(VALUES[4], Instruction.LDM);
    conf_DescInstruction.put(VALUES[5], Instruction.JMP);
    conf_DescInstruction.put(VALUES[6], Instruction.CMP);
    conf_DescInstruction.put(VALUES[7], Instruction.SYS);
  
    conf_InstructionOP.put(Instruction.IMM, VALUES[0]);
    conf_InstructionOP.put(Instruction.STK, VALUES[1]);
    conf_InstructionOP.put(Instruction.ADD, VALUES[2]);
    conf_InstructionOP.put(Instruction.STM, VALUES[3]);
    conf_InstructionOP.put(Instruction.LDM, VALUES[4]);
    conf_InstructionOP.put(Instruction.JMP, VALUES[5]);
    conf_InstructionOP.put(Instruction.CMP, VALUES[6]);
    conf_InstructionOP.put(Instruction.SYS, VALUES[7]);

    System.out.println(Instruction.IMM + " " + conf_InstructionOP.get(Instruction.IMM));
    System.out.println(Instruction.ADD + " " + conf_InstructionOP.get(Instruction.ADD));
    System.out.println(Instruction.STK + " " + conf_InstructionOP.get(Instruction.STK));
    System.out.println(Instruction.STM + " " + conf_InstructionOP.get(Instruction.STM));
    System.out.println(Instruction.LDM + " " + conf_InstructionOP.get(Instruction.LDM));
    System.out.println(Instruction.CMP + " " + conf_InstructionOP.get(Instruction.CMP));
    System.out.println(Instruction.JMP + " " + conf_InstructionOP.get(Instruction.JMP));
    System.out.println(Instruction.SYS + " " + conf_InstructionOP.get(Instruction.SYS));
  }

  void initSys() {
    conf_DescSyscall.put(VALUES[0], Syscall.open);
    conf_DescSyscall.put(VALUES[1], Syscall.read_memory);
    conf_DescSyscall.put(VALUES[2], Syscall.read_code);
    conf_DescSyscall.put(VALUES[3], Syscall.write);
    conf_DescSyscall.put(VALUES[4], Syscall.sleep);
    conf_DescSyscall.put(VALUES[5], Syscall.exit);

    conf_SyscallOP.put(Syscall.open, VALUES[0]);
    conf_SyscallOP.put(Syscall.read_code, VALUES[1]);
    conf_SyscallOP.put(Syscall.read_memory, VALUES[2]);
    conf_SyscallOP.put(Syscall.write, VALUES[3]);
    conf_SyscallOP.put(Syscall.sleep, VALUES[4]);
    conf_SyscallOP.put(Syscall.exit, VALUES[5]);
  }

  void initFlag() {
    conf_DescFlag.put(VALUES[1], Flag.l);
    conf_DescFlag.put(VALUES[2], Flag.g);
    conf_DescFlag.put(VALUES[3], Flag.e);
    conf_DescFlag.put(VALUES[4], Flag.n);
    conf_DescFlag.put(VALUES[5], Flag.z);

    conf_FlagOffset.put(Flag.l, VALUES[1]);
    conf_FlagOffset.put(Flag.g, VALUES[2]);
    conf_FlagOffset.put(Flag.e, VALUES[3]);
    conf_FlagOffset.put(Flag.n, VALUES[4]);
    conf_FlagOffset.put(Flag.z, VALUES[5]);
  }

  public Machine() {
    
    shuffleValues();
    initReg();

    shuffleValues();
    initIns();

    shuffleValues();
    initSys();

    shuffleValues();
    initFlag();

    
    conf_InsByte.put(InstructionByte.opcode, 0);    
    conf_InsByte.put(InstructionByte.param1, 1);    
    conf_InsByte.put(InstructionByte.param2, 2);    
  }
}
