
package interpreter.bytecode;

import debugger.DebugVirtualMachine;
import interpreter.ByteCode;
import interpreter.VirtualMachine;

import java.util.ArrayList;

/**
 * FunctionCode byte code contains information that is provided to the topmost
 * FunctionEnvironmentRecord instance of the environmentStack.
 * The debugger uses this byte code to present information about the current frame.
 */

public class FunctionCode extends ByteCode {

    private String name;
    private int startLine;
    private int endLine;
    
    public void init(ArrayList<String> args) {
        name = args.get(0); 
        startLine = Integer.parseInt(args.get(1)); 
        endLine = Integer.parseInt(args.get(2)); 
    }
    
    public void print(VirtualMachine vm) {
        System.out.println("FUNCTION " + name + " " + startLine + " " + endLine);
    }
    
    public void execute(VirtualMachine vm) {
        Class cl = vm.getClass();
        String className = cl.getName();  
        if (className.equals("debugger.DebugVirtualMachine"))
            execute( (DebugVirtualMachine) vm);
    }
    
    public void execute(DebugVirtualMachine vm) {
        
        vm.setInfoEnvStackEntry(name, startLine, endLine);
        vm.updatePCRollBackofFER(); // update the pc value to roll back to
       if (name.equals("main")) {
           vm.setIsMainFunctionStart(true); // at the start of main, raise the main function flag 
       }
    }
    
}
