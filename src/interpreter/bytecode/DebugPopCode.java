/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package interpreter.bytecode;

import debugger.DebugVirtualMachine;
import interpreter.VirtualMachine;

/**
 * DebugPopCode extends the functionality of PopCode by eliminating entries
 * from the symbol table of FunctionEnvironmentRecord upon exit from a function.
 */
public class DebugPopCode extends PopCode {

    public void execute(VirtualMachine vm) {
        super.execute(vm);
        execute((DebugVirtualMachine) vm);
    }

    public void execute(DebugVirtualMachine vm) {
        vm.popEnvStackEntryItems(getNumberOfLevels());
    }

}
