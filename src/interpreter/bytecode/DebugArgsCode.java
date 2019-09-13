package interpreter.bytecode;

import debugger.DebugVirtualMachine;
import interpreter.VirtualMachine;

/**
 * Debugger ARGS code expands the ARGS byte code and causes a new entry to
 * be pushed on to the debugger's Environment Stack.
 */
public class DebugArgsCode extends ArgsCode {
    
    @Override
    public void execute (VirtualMachine vm) {
        super.execute(vm);
        execute((DebugVirtualMachine)vm);
    }
    
    public void execute(DebugVirtualMachine vm) {
        vm.createNewEnvStackEntry();
    }
       
}
