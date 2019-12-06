
package interpreter;

import debugger.ui.UserInterface;

import java.util.Scanner;
import java.util.Stack;

/**
 * The VirtualMachine executes the byte codes in the Program instance and
 * manages the RunTimeStack, manipulating it as requested by the byte codes.
 * As required by the instructions, none of the methods in this class return any 
 * component of the VirtualMachine.  The other classes send a message to the
 * VirtualMachine to provide desired services.
 *
 * The various operations of the byte codes are performed by methods in this class
 * if operating in non-debug mode.
 * 
 */

public class VirtualMachine {
    private Program program = null;
    // According to instructions, this class maintains the dump flag
    private int dumpState = 0;
    private RunTimeStack runStack;
    private int pc = 0;
    private Stack<Integer> returnAddrs;
    private boolean isRunning = true;
    /* readWrite flag is used to format the dumping of Read and Write codes.
     * If the program is returning from a Read or Write call, the output 
     * of the dumping is formatted appropriately by consulting the flag status:
     * 1 for read, 2 for write, zero otherwise
     */
    private int readWrite; 
    private UserInterface ui;
    
    public VirtualMachine() {}
    
    public VirtualMachine(Program p) {
        
        program = p;
        runStack = new RunTimeStack();
        returnAddrs = new Stack<>();
        ui = new UserInterface();
    }
    
    public Program getProgram() {
        return program;
    }
    
    public RunTimeStack getRunStack() {
        return runStack;
    }
    
    public Stack<Integer> getReturnAddrsStack() {
        return returnAddrs;
    }
    
    public void setPC (int newPCValue) {
        pc = newPCValue;
    }
    
    public int getPC() {
        return pc;
    }
    
    public void incrementPC() {
        pc++;
    }
    
    public void setIsRunning(boolean runningState) {
        isRunning = runningState;
    }
    
    public boolean getIsRunning() {
        return isRunning;
    }
    
    public UserInterface getUI () {
        return ui;
    }
    
    /**
     * Goes through the byte codes in the Program instance, executing the 
     * instructions associated with that byte code, and then dumps if the 
     * dump status is ON.
     */
    public void executeProgram() {
        pc = 0;
        isRunning = true;
        while(isRunning) {
            ByteCode code = program.getCode(pc);
            code.execute(this);
            if (dumpState == 1) {
                code.print(this);
                // Do not provide dumping for the DumpCode instances
                Class cl = code.getClass();
                String className = cl.getName();
                if (!(className.equals("interpreter.bytecode.DumpCode"))  ) {
                    runStack.dump();
                }
            }
            pc++;
        } 
    }
    
    public int popRunStack() {
        return runStack.pop();
    }
    
    public int pushRunStack(int value) {
        runStack.push(value);
        return value;
    }
    
    public int peekRunStack() {
        return runStack.peek();
    }
    
    public int getRunStackValue(int index) {
        return runStack.getValueAt(index); 
    }
    
    
    public int storeRunStack(int offset) {
        return runStack.store(offset);
    }
    
    public int loadRunStack(int offset) {
        return runStack.load(offset);
    }
    
    public void popLevelsOffRunStack(int levels) {
        runStack.popLevels(levels); 
    }
    
    // Create a new frame to hold specified number of elements from top of stack
    public void createArgsFrame(int numberOfArgs) {
        int size = runStack.sizeOfRunStack();
        int startIndex = size - numberOfArgs;
        runStack.newFrameAt(startIndex);
    }
    
    public int sizeOfRunStack() {
        return runStack.sizeOfRunStack();
    }
    
    /* Return a String that holds the top frame of the RunTimeStack in
     * the desired format for printing convenience
     */ 
    public String topFrameAsString() {
        return runStack.topFrameAsString();
    }
    
    public int peekFramePointerStack() {
        return runStack.peekFrameStack();
    }
    
    public int popAddrsStack() {
        return (int)returnAddrs.pop();
    }
    
    public void pushAddrsStack() {
        returnAddrs.push(pc); 
    }
    public int pushAddrsStack(int i) {
        returnAddrs.push(i);
        return (int)returnAddrs.peek();
    }
    
    public int peekAddrsStack() {
        return (int)returnAddrs.peek();
    }
    
    public void setDump(int state) {
        dumpState = state;
    }

    public int returnAfterCall () {
        pc = returnAddrs.pop();
        return runStack.popFrame();
    }
    
    // Prompts user to enter a number and pushes it to RunTimeStack
    public int read() {
        int inputNumber=0;
        Scanner input;
        while (true) {
            try {
                input = new Scanner(System.in);
                ui.print("\nEnter an integer, or -1 to quit: ");
                inputNumber = input.nextInt();
                if (inputNumber == -1) {
                    System.out.println("Terminating program.");
                    System.exit(0);
                }
                break;
            } catch (Exception e) {
                ui.print("Invalid input.\n");
            }
        }
        pushRunStack(inputNumber);
        return inputNumber;
    }
    
    public int write() {
        int topOfStack = runStack.peek();
        ui.print("\n"+topOfStack+"\n");
        return topOfStack;
    }
    
    public void setReadWrite (int newState) {
        readWrite = newState;
    }
    
    public int getReadWrite () {
        return readWrite;
    }
    
    // carries out operations of the BOP codes on the top two elements of stack
    public int doOperation (int firstOperand, int secondOperand, String op) {
        int result;
        switch (op) {
            case "+": {
                result = firstOperand + secondOperand;
                break;
            }
            case "-": {
                result = secondOperand - firstOperand;
                break;
            }
            case "*": {
                result = secondOperand * firstOperand;
                break;
            }
            case "/": {
                result = secondOperand / firstOperand;
                break;
            }
            case "==": {
                if (secondOperand == firstOperand) {
                    result = 1;
                }
                else result = 0;
                break;
            }
            case "!=": {
                if (secondOperand == firstOperand) {
                    result = 0;
                }
                else result = 1;
                break;
            }
            case "<=": {
                if (secondOperand <= firstOperand) {
                    result = 1;
                }
                else result = 0;
                break;
            }
            case "<": {
                if (secondOperand < firstOperand) {
                    result = 1;
                }
                else result = 0;
                break;
            }
            case ">=": {
                if (secondOperand >= firstOperand) {
                    result = 1;
                }
                else result = 0;
                break;
            }
            case ">": {
                if (secondOperand > firstOperand) {
                    result = 1;
                }
                else result = 0;
                break;
            }
            case "|": {
                if ( (secondOperand!=0) || (firstOperand!=0) ) {
                    result = 1;
                }
                else result = 0;
                break;
            }
            case "&": {
                if ( (secondOperand!=0) && (firstOperand!=0) ) {
                    result = 1;
                }
                else result = 0;
                break;
            }
            default: {
                result = -1;
                ui.print("\nOperation not recognized.\n");
            }
        } // end switch
        
        return pushRunStack(result);
        
    }



} // end class
