
package interpreter.bytecode;

import debugger.DebugVirtualMachine;
import interpreter.VirtualMachine;

/**
 * DebugCallCode extends the functionality of CallCode by facilitating
 * the creation of a new entry in the debugger's argsStack. 
 */

public class DebugCallCode extends CallCode {
    
    public void execute(VirtualMachine vm) {
        super.execute(vm);
        execute((DebugVirtualMachine) vm);
    }
    
    public void execute(DebugVirtualMachine vm) {
        vm.pushArgsStack(); 
    }



}
