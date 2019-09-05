
package interpreter.bytecode;

import interpreter.ByteCode;
import interpreter.VirtualMachine;

import java.util.ArrayList;

/**
 * POP byte code: pops the indicated number of levels off the RunTimeStack
 */

public class PopCode extends ByteCode {
    private int numberOfLevels;
    
    public int getNumberOfLevels() {
        return numberOfLevels;
    }
    public void init(ArrayList<String> args) {
        numberOfLevels = Integer.parseInt(args.get(0));
    }
    
    public void print(VirtualMachine vm) {
        System.out.println("POP " + numberOfLevels);
    }
    
    public void execute(VirtualMachine vm) {
        vm.popLevelsOffRunStack(numberOfLevels);
    }
    
}
