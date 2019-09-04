
package interpreter.bytecode;

import interpreter.ByteCode;
import interpreter.VirtualMachine;

import java.util.ArrayList;

/**
 * LOAD byte code: pushes the value at the indicated offset from the start of 
 * the current frame to the top of the RunTimeStack
 */

public class LoadCode extends ByteCode {
    
    private int offsetValue;
    private String name=null;
    
    public void init(ArrayList<String> args) {
        offsetValue=Integer.parseInt(args.get(0));
        if (args.size()==2) {
            name=args.get(1);
        }    
    }
    
    public void print(VirtualMachine vm) {
        System.out.print("LOAD "+offsetValue);
        if (name!=null) {
            System.out.print(" " + name + "   ");
            System.out.print("<load " + name + ">");
        }
        System.out.print("\n");
    }
    
    public void execute(VirtualMachine vm) {
        vm.loadRunStack(offsetValue);
    }
    
}
