
package interpreter.bytecode;

import debugger.DebugVirtualMachine;
import interpreter.VirtualMachine;


/**
 * DebugLitCode class extends the functionality of LitCode by facilitating
 * the addition of new Symbol-Binder pairings to the symbol table of an
 * FunctionEnvironmentStack instance.
 */

public class DebugLitCode extends LitCode {
    
    public void execute(VirtualMachine vm) {
        execute((DebugVirtualMachine)vm);
    }
    
    public void execute(DebugVirtualMachine vm) {
        vm.pushRunStack(getValue());
        // if the value has an associated variable name, add the 
        // mapping for the Symbol/Binder pair to the symbol table
        if (getName()!=null) {
            if (vm.IsMainFunctionStart()) {
                // if at the very start of main function, then upon rollback
                //     these mappings are preserved
                vm.setVarValueRollBack(getName(), vm.getNextEmptyOffset());
                vm.updatePCRollBackofFER(); // update the pc value to roll back to
            }
            else {
                vm.setVarValueForEnvStackEntry(getName(), vm.getNextEmptyOffset());
            }
   
        }
        
    }
    
}
