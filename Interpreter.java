import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Interpreter {
  
  byte[] ByteCode;
  Interpreter(String file) {
    try {
      ByteCode = Files.readAllBytes(Path.of(file));
    } catch (IOException e) {
      System.err.println(e);
    }
  }

  public void start() {
    System.out.println("Interpreting");
  }
}
