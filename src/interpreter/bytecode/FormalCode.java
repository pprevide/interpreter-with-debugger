
package interpreter.bytecode;

import debugger.DebugVirtualMachine;
import interpreter.ByteCode;
import interpreter.VirtualMachine;

import java.util.ArrayList;

/**
 * FormalCode byte code presents Symbol-Binder pairings for insertion in the 
 * symbol table of the FunctionEnvironmentRecord instance at the top the environmentStack.
 */

public class FormalCode extends ByteCode {

    private String varName;
    private int offset;
    
    public void init(ArrayList<String> args) {
        varName = args.get(0);
        offset = Integer.parseInt(args.get(1));
    }
    
    public void print(VirtualMachine vm) {
        System.out.println("FORMAL " + varName + " " + offset);
    }
    
    public void execute (VirtualMachine vm) {
        Class cl = vm.getClass();
        String className = cl.getName();  
        if (className.equals("debugger.DebugVirtualMachine"))
            execute((DebugVirtualMachine) vm);
    }
    
    public void execute(DebugVirtualMachine vm) {
        // add mappings to the symbols map
        vm.setVarValueRollBack(varName, offset);
        // update the pc value associated with the FunctionEnvironmentRecord
        vm.updatePCRollBackofFER();
    }
    
}