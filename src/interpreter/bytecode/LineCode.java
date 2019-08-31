
package interpreter.bytecode;

import debugger.DebugVirtualMachine;
import interpreter.ByteCode;
import interpreter.VirtualMachine;

import java.util.ArrayList;

/**
 * LineCode byte code indicates which source code line corresponds to the current
 * position in the Program's byte codes. That information is useful for several
 * of the DebugVirtualMachine's operations.
 */

public class LineCode extends ByteCode {

    private int lineNumber;

    public void init(ArrayList<String> args) {
        lineNumber = Integer.parseInt(args.get(0));
    }

    public void print(VirtualMachine vm) {
        System.out.println("LINE " + lineNumber);
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void execute(VirtualMachine vm) {
        Class cl = vm.getClass();
        String className = cl.getName();
        if (className.equals("debugger.DebugVirtualMachine"))
            execute((DebugVirtualMachine) vm);
    }

    public void execute(DebugVirtualMachine vm) {
        vm.updateLineCodeNumber(lineNumber);
        // after the first line of the main function, lower the isMainFunction flag
        if (vm.IsMainFunctionStart()) {
            vm.setIsMainFunctionStart(false);
            vm.pushArgsStack();
        }
    }


}