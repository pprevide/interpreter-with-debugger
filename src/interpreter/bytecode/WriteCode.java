
package interpreter.bytecode;

import interpreter.ByteCode;
import interpreter.VirtualMachine;

import java.util.ArrayList;

/**
 * WRITE byte code: output the value at top of RunTimeStack
 */
public class WriteCode extends ByteCode {
    
    private int value;
    
    public void init(ArrayList<String> args) {}
    
    public void print(VirtualMachine vm) {
        System.out.println("WRITE");
    }
    
    public void execute(VirtualMachine vm) {
        value = vm.write();
    }
    
}