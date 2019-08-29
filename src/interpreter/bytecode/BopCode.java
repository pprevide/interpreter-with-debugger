
package interpreter.bytecode;

import interpreter.ByteCode;
import interpreter.VirtualMachine;

import java.util.ArrayList;

/**
 * BOP byte code: perform binary operations using two operands and a specified code indicating which operation to do
 * See the doOperation() method of the VirtualMachine.java file for all supported operators.
 * The oepration of this byte code pops two values from the top of the Runtime Stack
 */

public class BopCode extends ByteCode {
    
    private String op = null;
    int topOperand;
    int secondOperand;
    int result;
    public void init(ArrayList<String> args) {
        op = args.get(0);
    }
    
    public void print(VirtualMachine vm) {
        System.out.println("BOP " + op);
    }
    
    public void execute(VirtualMachine vm) {
        topOperand = vm.popRunStack();
        secondOperand = vm.popRunStack();
        result = vm.doOperation(topOperand, secondOperand, op);
    }
    
    
}
