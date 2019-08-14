
package interpreter.bytecode;

import interpreter.ByteCode;
import interpreter.VirtualMachine;

import java.util.ArrayList;

/**
 * DUMP byte code: instructs interpreter to turn dumping on or off
 */

public class DumpCode extends ByteCode {
    
    private int dumpState;
    public void init(ArrayList<String> args) {
        String state = args.get(0);
        if (state.equals("ON") ) {
            dumpState = 1;
        }
        if (state.equals("OFF")) {
            dumpState = 0;
        }
    }
    
    public void print(VirtualMachine vm) { }
    
    public void execute(VirtualMachine vm) {
        vm.setDump(dumpState);
    }
    
    
}
