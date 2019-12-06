
package debugger;

import debugger.ui.UserInterface;
import interpreter.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

/**
 * The DebugVirtualMachine carries out the program's byte codes when the 
 * interpreter has been executed in debugger mode by the user.  It can execute
 * several new byte code types and extends the functionality of the VirtualMachine.
 *
 * The various operations of the byte codes are performed by methods in this class
 * if operating in debug mode.
 *
 * This class extends the VirtualMachine and adds various operations performed by the debugger,
 * such as setting, clearing and displaying break points. performing step-over and step-into,
 * displaying and setting variables at break points, and so on.
 * 
 */
public class DebugVirtualMachine extends VirtualMachine {
    
    String xFile; // the source code file
    
    private ArrayList<DebugEntries> entriesList;
    
    private Stack <FunctionEnvironmentRecord> envStack;
    
    private UserInterface ui;
    
    private int lineCodeNumber; // the most recently executed LineCode's line number
    
    //lineCodes holds all the source code lines for which a LineCode exists
    private ArrayList<Integer> lineCodes;
    
    private int envSize; // the number of elements of the envStack
    
    private int lineNumberBeforeStep; // line number tracker used by stepping functions
    
    private boolean isStepOutSet, isStepIntoSet, isStepOverSet,
            // flags whether execution is at the very start of the main function
            // this flag helps with rollback to the start of main function
            isMainFunctionStart; 
                            
    
    /* Each stack entry holds a backup of the arguments at the point in time when
       the body of a function starts executing, for rollback purposes */
    private Stack<ArrayList<Integer>> argsStack; 
    
    
    public DebugVirtualMachine() {}
    
    public DebugVirtualMachine(Program p, String sourceFile) {
       super(p);
       xFile = sourceFile;
       entriesList = new ArrayList<>();
       initializeEntriesList(xFile);
       envStack = new Stack<>();
       ui = getUI(); 
       argsStack = new Stack<>();
       initializeArgsStack();
       
    }
    
    @Override
    public void executeProgram() {
        setPC(0);
        setIsRunning(true);
        lineCodes = getAllLineCodeNumbers(getProgram());
        // before execution of Program, print entire source code
        printSource();
        String command;
        createNewEnvStackEntry(); // envStack entry for main function
        
        while (getIsRunning()) {
            command = promptUser(); 
            processCommand(command); // receive command from UI and process it
            if (isContinueExecutionSet(command)) {
                while (true) {
                    //execute byte codes until breakpoint reached
                    ByteCode code = (getProgram()).getCode(getPC()); 
                    code.execute((DebugVirtualMachine)this);
                    Class cl = code.getClass();
                    String className = cl.getName(); 
                    if (className.equals("interpreter.bytecode.LineCode") ) {  
                        if (checkForBreakPoint()) {
                            // show current frame info, stop execution
                            incrementPC();
                            displayAtBreakPointLine(lineCodeNumber);
                            break;
                        }
                        if (isStepOverSet) {  
                            if ( (lineNumberBeforeStep!=lineCodeNumber) && 
                                    envSize==envStack.size() ) {
                                incrementPC();
                                displayAtBreakPointLine(lineCodeNumber);
                                isStepOverSet=false;
                                break;
                            }
                        }
                        if (isStepIntoSet) {
                            if ( (lineNumberBeforeStep!=lineCodeNumber) ||
                                    (envStack.size()==envSize+1) ) {
                                incrementPC();
                                displayAtBreakPointLine(lineCodeNumber);
                                isStepIntoSet = false;
                                break;
                            }
                        }
                    }
                    
                    else if (className.equals("interpreter.bytecode.HaltCode")) {
                        break;
                    }
                    if (isStepOutSet) {
                        if (className.equals("interpreter.bytecode.DebugReturnCode")) {
                            /* determine if the envStack has decreased by one 
                             * since the stepOut command was issued */
                            if (isStepOutCompleted()) { 
                                incrementPC();
                                break;
                            }
                        }
                    }
                    else if (isStepOverSet) {
                        // if at the last line of current invocation, stepover and stepout do the same thing
                        if (className.equals("interpreter.bytecode.DebugReturnCode")) {
                            if (isStepOutCompleted()) {
                                incrementPC();
                                break;
                            }
                        }
                        
                        
                    }
                    
                    incrementPC();
                } // end while loop        
            } // end if(continue) block
        } // while isRunning
        
    } // end executeProgram
    
    /**
     * Determines whether the entered command from the user is one, such as step-out
     * or continue, that indicates that program execution should continue until the 
     * next stopping point (a breakpoint or the endpoint of the step).
     * @param command is the String entered by the user
     * @return whether the entered command indicates that execution should continue
     */
    public boolean isContinueExecutionSet(String command) {
        return (command.equals("cont") || command.equals("sout") || 
                command.equals("sover") || command.equals("sinto"));
    }
    
    public void processCommand(String command) {
        StringTokenizer st = new StringTokenizer(command);
        String action;
        try {
            action = st.nextToken();
        } catch (NoSuchElementException e) {
            print("The command you entered is not recognized.\n");
            print("Type help or ? to see a list of commands.\n");
            return;
        }
        
        switch (action) {
            case "q": 
                setIsRunning(false);
                print("\n***** Execution has been halted *****\n");
                break;
            
            case "setbp": 
                setBreakPoints(command);           
                break;
             
            case "clrbp":  
                clearBreakPoints(command);
                break;
                
            case "clrall":
                clearAllBreakPoints(command);
                break;
                
            case "showbp":
                showBreakPoints();
                break;
                
            case "sout":
                stepOut();
                break;
             
            case "sover":
                stepOver();
                break;
                
            case "sinto":
                stepInto();
                break;
             
            case "vars": 
                displayVariables();            
                break;
                
            case "cv":
                changeVariable(command);  
                break;
                
            case "cont":
                break;          
                
            case "dcf":
                displayAtBreakPointLine(lineCodeNumber);
                break;
                
            case "rb":
                rollback();
                break; 
                
            case "help":
            case "?":
                displayAvailableActions();
                break;
            default:
                print("The command you entered is not recognized.\n");
                print("Type help or ? to see a list of commands.\n");
                
        } // end switch
    } // end processCommand
    
    
    public void setBreakPoints(String command) {
        StringTokenizer st = new StringTokenizer(command);
        String next = st.nextToken();
        if (!next.equals("setbp") || st.countTokens()<1) {
            print("Invalid setbp commmand.\n");
            return;
        }
        int breakLine;
        while (st.hasMoreTokens()) {
            try {
                next = st.nextToken();
                breakLine = Integer.parseInt(next);
                
            } catch (Exception e) {
                print ("Invalid setbp line: "+next +"\n" ); 
                continue;
            }
            // confirm that the desired breakpoint line has an associated LineCode
            if (!lineCodes.contains( (Integer)breakLine) ) {
                print("A break point may not be set on line "+next+ "\n");
                continue;
            }
            DebugEntries entry = entriesList.get(breakLine-1);
            entry.setIsBreakPointSet(true);
            print("Breakpoint set at line "+next+ "\n");
              
        } // end while loop    
    } // end setBreakPoints method
    
    public void clearBreakPoints(String command) {
        StringTokenizer st = new StringTokenizer(command);
        String next = st.nextToken();
        if (!next.equals("clrbp") || st.countTokens()<1) {
            print("Invalid clrbp commmand.");
            return;
        }
        int clearLine;
        while (st.hasMoreTokens()){
            try {
                next = st.nextToken();
                clearLine = Integer.parseInt(next);        
            } catch (Exception e) {
                print ("Invalid clrbp line: "+next +"\n" ); 
                continue;
            }
            DebugEntries entry = entriesList.get(clearLine-1);
            if (!entry.isBreakPointSet()) {
                print("There was no break point set to line "+next +"\n");
                continue;
            }
            entry.setIsBreakPointSet(false);
            print("Breakpoint cleared from line " + next + "\n");
        } // end while loop
    } // end clearBreakPoints method
    
    public void clearAllBreakPoints(String command) {
        StringTokenizer st = new StringTokenizer(command);
        if (st.countTokens()>1) {
            print("Invalid clrall command.");
            return;
        }
        for (DebugEntries entry : entriesList) {
            if (entry.isBreakPointSet) {
                entry.setIsBreakPointSet(false);
            }
        }
        print("Breakpoint(s) cleared from all lines.\n");
    }
    
    public void showBreakPoints() {
        String breakPoints = ""; // String representation of the set of lines with breaks
        int numberOfBreakPointsSet=0;
        for (DebugEntries entry : entriesList) {
            if (entry.isBreakPointSet() ) {
                breakPoints=breakPoints.concat( (String.valueOf(entry.getLineNumber()))+" ");
                numberOfBreakPointsSet++;
            }
        }
        switch (numberOfBreakPointsSet){
            case(0):
                ui.print("\nNo break points are set.\n");
                break;
            case(1):
                ui.print("A breakpoint is set on line: "+breakPoints+"\n");
                break;
            default:
                ui.print("Breakpoints are set on the following lines: ");
                ui.print(breakPoints+"\n");
                    
        }
    }
    
    public void stepOut() {
        isStepOutSet=true;
        envSize = envStack.size();                                   
    }
    
    /**
     * Checks whether the envStack has decreased by one since the user issued
     * the stepOut command; if so, execution should be halted so that the user
     * may issue the next debugger instruction.
     * Note: since stepOver may be the same as stepOut, it may also use this 
     * method to check whether the stepOver action has reached its endpoint.
     * @return whether the stepOut action has reached its endpoint  
     */
    public boolean isStepOutCompleted() {
        boolean stepOutCompleted = false;                           
        if (envStack.size()==envSize-1) {
            FunctionEnvironmentRecord f = envStack.peek();
            lineCodeNumber = f.getCurrentLineNumber();
            displayAtBreakPointLine(lineCodeNumber);
            isStepOutSet = false;
            isStepOverSet = false;
            stepOutCompleted = true;
        }
        return stepOutCompleted;
    }
    
    public void stepOver () {
        isStepOverSet = true;  
        envSize = envStack.size();
        lineNumberBeforeStep = lineCodeNumber;
    }
    
    public void stepInto() {
        isStepIntoSet = true;
        envSize = envStack.size();
        lineNumberBeforeStep = lineCodeNumber;
    }
    
    /* Shows the line at which execution is stopped, before displaying 
     * the current function
     */
    public void displayAtBreakPointLine(int line) {
        if (line < 0) {
            if (getReadWrite() == 1) {
                print("\nExecution stopped in function read()\n");
            }
            else if (getReadWrite() == 2) {
                print("\nExecution stopped in function write()\n");
            }
            print("Source code is unavailable for this function\n");
            return;
        }
        if (line > 0) {
            print("\nExecution stopped at line "+String.valueOf(line) +"\n");
        }
        displayCurrentFunction(line);
    }
    
    /* Displays the current function, with the current line and breakpoints 
     * indicated by an arrow and asterisks respectively  
     */
    public void displayCurrentFunction (int line) {
        FunctionEnvironmentRecord fer = envStack.peek();
        int startingLine = fer.getStartLine();   
        int endingLine = fer.getEndLine();
        if (endingLine==0 && envStack.size()==1) {
                print("The program has not yet entered the first block.\n");
                return;
        }
        else if (endingLine==0 || startingLine==0) {
                FunctionEnvironmentRecord fer2 = envStack.pop();
                fer = envStack.peek();
                envStack.push(fer2);
                startingLine = fer.getStartLine();
                endingLine = fer.getEndLine();
            }
        int i;
        DebugEntries entry;
        for (i=startingLine; i<=endingLine; i++) {
            entry = entriesList.get(i-1);
            if(entry.isBreakPointSet()) {
                print("*");
            }
            if (entry.getLineNumber()<10 && !entry.isBreakPointSet()) {
                print(" ");
            }
            print(String.valueOf(entry.getLineNumber()));
            print(". ");
            print(entry.getSourceLine());
            if(entry.getLineNumber()==line) {
                print("  <-----------");
            }
            print("\n");
        } // end for loop
    }
    
    /**
     * Changes a local variable to a different value entered by the user. The
     * command is scanned for the variable name, then the new value.
     * @param command the next command
     */
    public void changeVariable(String command) {
        StringTokenizer st = new StringTokenizer(command);
        String next = st.nextToken();
        if (!next.equals("cv") || st.countTokens()<1) {
            print("Invalid cv command.\n");
            return;
        } 
        int newValue;
        String varName;
        
        // get variable name to change
        try {
            varName = st.nextToken(); 
        } catch (Exception e) {
            print("Invalid variable name.\n");
            return;
        }
        try {
            next = st.nextToken();
            newValue = Integer.parseInt(next);
        } catch (Exception e) {
            print("Invalid variable value.\n");
            return;
        }
        
       FunctionEnvironmentRecord fer = envStack.peek();
       int indexToChange = fer.indexOfVariableToChange(varName);
       if (indexToChange<0 ) {
           print("There is no variable with that name in the current frame.\n");
       }
       else {
           RunTimeStack r = getRunStack();
           int startOfCurrentFrame = r.peekFrameStack();
           r.setValueAt( (indexToChange + startOfCurrentFrame), newValue );
           print("\nValue of "+varName+ " changed to "+newValue + "\n");
       }
    }
    
    public void displayVariables () {
        FunctionEnvironmentRecord fer = envStack.peek();
        if (fer.getStartLine()==0) {
            if (envStack.size()==1) {
                print("No local variables\n");
                return;
            }
            FunctionEnvironmentRecord fer2 = envStack.pop();
            fer = envStack.peek();
            envStack.push(fer2);
        }
        print("\nLocal variables:\n");
        print(fer.showVariables(this));
        print("\n");
    }
    
    /**
     * Provides the element at a given offset in the current frame of the 
     * runtime stack.
     * @param offset of the desired element
     * @return the value of that element
     */
    public int getRunStackValueAtOffset(int offset) {
        int indexStartCurrentFrame = peekFramePointerStack();
        int indexOfValue = indexStartCurrentFrame + offset;
        int value = getRunStackValue(indexOfValue);
        return value;
    }
    
    /**
     * Changes the value of an existing element on the runtime stack, when the 
     * user enters the "change variable" command.
     * @param offset of the value to be changed
     * @param newValue 
     */
    public void changeRunStackValue(int offset, int newValue) {
        RunTimeStack r = getRunStack();
        int currentFrameOffset = offset + r.peekFrameStack();
        r.setValueAt(currentFrameOffset, newValue);
    }
    
    
    // Returns the offset of the next available vacancy in the runtime stack
    public int getNextEmptyOffset() {
        int nextOffset = sizeOfRunStack() - peekFramePointerStack()-1;
        return nextOffset;
    }
    
    // Checks whether the line number just reached has a breakpoint on it
    public boolean checkForBreakPoint() {
        if (lineCodeNumber<0) return false;
        DebugEntries currentEntry = entriesList.get(lineCodeNumber-1);
        boolean breakPointIsSet = currentEntry.isBreakPointSet();
        return breakPointIsSet;
    }
    
    // Creates a list of all line numbers where break points may be set
    public ArrayList<Integer> getAllLineCodeNumbers (Program program) {
        ArrayList<Integer> lines = program.getAllLineCodeNumbers();
        return lines;
    }
    
    // Upon the execution of a LineCode, update the DebugVirtualMachine's line number
    public void updateLineCodeNumber(int line) {
        lineCodeNumber = line;
        updateCurrentLineOfFER(line);
    }
    
    public void displayAvailableActions() {
        print("The following is a list of debugger commands and their associated "
                + "actions:\n");
        printLeftAligned("COMMAND");
        print("ACTION\n");
        printLeftAligned("setbp");
        print("Sets breakpoint(s) (example: setbp 2 3)\n");
        printLeftAligned("clrbp");
        print("Clears breakpoint(s) (example: clrbp 6)\n");
        printLeftAligned("clrall");
        print("Clears all breakpoint(s) (example: clrall)\n");
        printLeftAligned("showbp");
        print("Shows set breakpoint(s)\n");
        printLeftAligned("sout");
        print("Steps out of current function\n");
        printLeftAligned("sover");
        print("Steps over the next line\n");
        printLeftAligned("sinto");
        print("Steps into the function on the current line\n");
        printLeftAligned("rb");
        print("Rolls back execution of the current function\n");
        printLeftAligned("dcf");
        print("Displays current function\n");
        printLeftAligned("cont");
        print("Continues execution\n");               
        printLeftAligned("vars");
        print("Displays local variables\n");
        printLeftAligned("cv");
        print("Changes value of one local variable to specified value (Example: cv n 3)\n");
        printLeftAligned("q");
        print("Terminates program\n"); 
        printLeftAligned("help");
        print("Displays command options\n");
        
    }
    
    public String promptUser() {
        print("\nType ? for help\n>> ");
        // The UserInterface is called upon to get the next action from the user
        String commandEntered = ui.getUserAction();
        return commandEntered;
    }
    
    /* 
     * The four print methods below send the DebugVirtualMachine's printout requests
     * to the UserInterface.  
     * The virtual machine is not responsible for output or input tasks.
     * Should the UserInterface be replaced with something
     * else, only the methods below would have to be modified.
     */
    private void print (String s) {
        ui.print(s);
    }
    
    private void printLeftAligned (String s) {
        ui.printLeftAligned(s);
    }
    public void printSource() {    
        printPortion(1, entriesList.size());   
    }
    
    public void printPortion(int startLine, int endLine) {
        ListIterator<DebugEntries> itr = entriesList.listIterator(startLine-1);
        int currentLine = startLine;
        DebugEntries nextEntry;
        while (currentLine<=endLine) {
            nextEntry = itr.next();
            if (currentLine<10)
                print(" ");
            print(String.valueOf(currentLine)+". ");
            print(nextEntry.getSourceLine()+"\n");
            currentLine++;
        }
        
    }
    
    /* The methods below handle the envStack of FunctionEnvironmentRecord
     * instances or manipulate particular features of specific instances.
     */
    public void createNewEnvStackEntry() {
        FunctionEnvironmentRecord fer = new FunctionEnvironmentRecord();
        fer.beginScope();
        envStack.push(fer);
    }
    
    public FunctionEnvironmentRecord popEnvStack() {
        FunctionEnvironmentRecord fer = envStack.pop();
        updateCurrentLineUponReturn();
        return fer;
    }
    
    // displays a String representation of the current FunctionEnvironmentRecord
    public void displayTopEnvStackEntry() {
        FunctionEnvironmentRecord fer = envStack.peek();
        print(fer.toString());
        print("\n");
    }
    
    /* Creates a new Symbol-Binder pairing in the current FunctionEnvironmentRecord.
     * This method is used for variables other than the formals of a function. 
     * Those variables are deleted during a rollback operation.
     */
    public void setVarValueForEnvStackEntry(String name, int value) {
        FunctionEnvironmentRecord fer = envStack.peek();
        fer.setVarVal(name, value); 
    }
    
    /*
     * Creates a new Symbol-Binder pairing in the current FunctionEnvironmentRecord.
     * This method is used for the variables that comprise the formals of a function,
     * and therefore are not deleted upon rollback.
     */
    public void setVarValueRollBack(String name, int value) {
        FunctionEnvironmentRecord fer = envStack.peek();
        fer.setVarValRollBack(name, value); 
    }
    
    public void popEnvStackEntryItems(int numberItems) {
        FunctionEnvironmentRecord fer = envStack.peek();
        fer.doPop(numberItems);
    }
    
    public void setInfoEnvStackEntry(String name, int startLine, int endLine) {
        FunctionEnvironmentRecord fer = envStack.peek();
        fer.setFunctionInfo(name, startLine, endLine);
    }
    
    // Updates the current line field of the current FunctionEnvironmentRecord.
    public void updateCurrentLineOfFER (int currentLine) {
        FunctionEnvironmentRecord fer = envStack.peek();
        fer.setCurrentLineNumber(currentLine);
    }
    
    // Updates the pc field of the current FunctionEnvironmentRecord.
    public void updatePCRollBackofFER() {
        FunctionEnvironmentRecord fer = envStack.peek();
        fer.setPCRollBack(getPC()); 
    }
    
    // Updates the lineCodeNumber field of the debugger.
    public void updateCurrentLineUponReturn() {
        if (envStack.size()>0) {
            FunctionEnvironmentRecord fer = envStack.peek();
            lineCodeNumber = fer.getCurrentLineNumber();
        }
    }
    
    // The following methods handle the argsStack and the rollback feature
    public void pushArgsStack() {
        ArrayList<Integer> topFrameElements = getRunStack().topFrameElements();
        argsStack.push(topFrameElements);
    }
    
    public ArrayList<Integer> popArgsStack() {
        ArrayList<Integer> topFrameElements = argsStack.pop();
        return topFrameElements;
    }
    
    public ArrayList<Integer> peekArgsStack() {
        ArrayList<Integer> topFrameElements = argsStack.peek();
        return topFrameElements;
    }
    
    private void initializeArgsStack() {
        ArrayList<Integer> mainArgs = new ArrayList<>();
        argsStack.push(mainArgs);
    }
    
    public void setIsMainFunctionStart(boolean state) {
        isMainFunctionStart = state;
    }
    
    public boolean IsMainFunctionStart() {
        return isMainFunctionStart;
    }
    
    public void rollback() {
        
        RunTimeStack r = getRunStack();
        // clear the values in the current frame
        r.clearCurrentFrameElements();
        // restore the elements back to their original state
        ArrayList<Integer> topFrameElements = peekArgsStack();
        for (Integer value : topFrameElements) {
            r.push(value); 
        }
        // reset PC so that the function restarts execution with formal values
        FunctionEnvironmentRecord fer = envStack.peek();
        setPC(fer.getPCRollBack()+1);    // PC is set to the byte code after the last formal
        
        // clears mappings added to FER's HashMap since the start of the function
        fer.restoreSymbolsMap();
        
        // reset line number to first line of function
        lineCodeNumber=fer.getStartLine();
        
        //reset current line number field within the FunctionEnvironmentRecord
        fer.setCurrentLineNumber(lineCodeNumber);
        
        // display current function for user
        displayAtBreakPointLine(lineCodeNumber);
    }
    
    /*
     * Reads through the source file and creates a new instance of DebugEntries
     * for each line of the source.  The DebugEntries instances are stored in entriesList. 
     */ 
    private void initializeEntriesList(String file) {
        BufferedReader source = null;
        DebugEntries nextEntry;
        try {
            source = new BufferedReader(new FileReader(file));
        } catch (Exception e) {}
        String nextLine = null;
        int line = 0;
        while (true) {
            try {
                nextLine = source.readLine(); 
                line++; 
            } catch (Exception e) {}
        
        if (nextLine==null) {
            try {
                source.close();
            } catch (Exception e) {}
            break;
        }
        nextEntry = new DebugEntries(nextLine, line);
        entriesList.add(nextEntry);
        } // end while loop
    } // end initialize method
    
    
    
} // end class 
    
