
package interpreter.bytecode;

import debugger.DebugVirtualMachine;
import interpreter.VirtualMachine;

/**
 * DebugReturnCode extends the functionality of ReturnCode by facilitating
 * the pop of environmentStack and argsStack entries upon exit from a function.
 */

public class DebugReturnCode extends ReturnCode {
    
    public void execute(VirtualMachine vm) {
        super.execute(vm);
        execute((DebugVirtualMachine) vm);
    }
    
    // pop the current frame off the debugger's envStack and ArgsStack structures
    public void execute(DebugVirtualMachine dvm) {
        dvm.popEnvStack();
        dvm.popArgsStack();  
    }
}
