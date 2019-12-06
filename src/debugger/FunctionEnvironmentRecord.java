
package debugger;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * FunctionEnvironmentRecord instances are the entries in the environmentStack of the
 * debugger version of the Virtual Machine.  Each instance of this class contains 
 * information about a frame on the runtime stack.
 * 
 */

class Symbol {
    private String name;
    private static HashMap<String, Symbol> symbols = new HashMap<>();
    
    public Symbol (String n) {
        name = n;
    }
    
    @Override
    public String toString() {
	return name;
  }
    
    public static Symbol symbol(String newTokenString) {
        Symbol s = symbols.get(newTokenString);
        if (s == null) {
            s = new Symbol(newTokenString);
	    symbols.put(newTokenString, s);
    }
      return s;
  }
    
} // end Symbol class

class Binder {
    private Object value;
    private Symbol prevtop;   // prior symbol in same scope
    private Binder tail;      // prior binder for same symbol
    // restore this when closing scope
    Binder(Object v, Symbol p, Binder t) {
	value=v; 
        prevtop=p; 
        tail=t;
    }

    Object getValue() {
      return value; 
  }
  
    public void setValue(Object i) {
      value = i;
  }
  
    Symbol getPrevTop() {
      return prevtop; 
  }
  
    Binder getTail() {
      return tail; 
  }
} // end Binder class

public class FunctionEnvironmentRecord { 
    private int startLine;
    private int endLine;
    private int currentLine;
    private String name;
    private int pcRollBack; // pc value to return to if user chooses rollback
    
    // tracks number of original mappings (after formals) and total mappings
    private int originalMapSize, numberOfMappings;
    private HashMap<Symbol, Binder> symbols = new HashMap<>();
    //private java.util.HashMap<Symbol,Binder> rollbackSymbols = new java.util.HashMap<>();
    private Symbol top;    // reference to last symbol added to current scope
    private Binder marks;  // scope mark
    
    public FunctionEnvironmentRecord () {}
    
    // getters and setters
    public  int getStartLine() {
        return startLine;
    }
    
    public void setStartLine(int l) {
        startLine = l;
    }
    
    public int getEndLine() {
        return endLine;
    }
    
    public void setEndLine(int l) {
        endLine = l;
    }
    
    public int getCurrentLineNumber() {
        return currentLine;
    }
    
    public void setCurrentLineNumber(int l) {
        currentLine = l;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName (String s) {
        name = s;
    }
    
    public int getPCRollBack() {
        return pcRollBack;
    }
    
    public void setPCRollBack(int newPC) {
        pcRollBack = newPC;
    }
    
    /**
     * Start a new scope for a new FunctionEnvironmentRecord instance.
     */
    public void beginScope() {
        marks = new Binder(null,top,marks);
        top=null;
    }
    
    /**
     * Set the function name (String) and the start and end lines in the 
     * source code for this function (int startLine, int endLine).
     * @param functionName
     * @param start
     * @param end 
     */
    public void setFunctionInfo(String functionName, int start, int end) {
        name = functionName;
        startLine = start;
        endLine = end;
    }
    
    /**
     * Create a new Symbol-Binder pair and add it to the symbols HashMap.
     * @param identifier is the name of the function
     * @param value is the value being associated within the Binder instance
     */
    public void setVarVal (String identifier, int value) {
        Symbol s = Symbol.symbol(identifier);
        Integer i = new Integer(value);
        Binder b = new Binder(i, top, symbols.get(s));
        symbols.put(s,b);
        top = s;
        numberOfMappings++;
    }
    
    /**
     * Create a new Symbol-Binder pair, add it to the map, and track the number 
     * of formals for purposes of the rollback feature.  Upon rollback, the number
     * of mappings will be trimmed back to the original map size using doPop calls.
     * @param identifier is the name of the function
     * @param value is the value being associated within the Binder instance
     */
    public void setVarValRollBack (String identifier, int value) {
        Symbol s = Symbol.symbol(identifier);
        Integer i = new Integer(value);
        Binder b = new Binder(i, top, symbols.get(s));
        symbols.put(s,b);
        top = s;
        originalMapSize++;
        numberOfMappings++;
    }
    
    
    private void put (Symbol symbol, Binder binder) {
        symbols.put(symbol, binder);
    }
    
    /**
     * Remove the last n mappings and, if applicable, restore a pre-existing
     * Symbol's Binder aggregate to what it was before the subsequent mapping
     * for that Symbol.
     * @param n 
     */
    public void doPop (int n) {
        Symbol topSymbol;
        Binder topBinder;
        Binder tailBinder;
        int i;
        for (i=0; i<n; i++) {
            topSymbol = top;
            topBinder = symbols.get(top);
            top = topBinder.getPrevTop();
            symbols.remove(topSymbol);
            tailBinder = topBinder.getTail();
            if (tailBinder!=null) {
                put(topSymbol, tailBinder);
            }   
            numberOfMappings--;
        } // end for loop
        
    } // end doPop()
    
    /**
     * Clears the map of the Symbol-Binder pairings added since the start of execution of the 
     * function body.  The pairings are trimmed down back to originalMapSize, which
     * represents the formals (outside of main) or the variable declarations in line
     * 1 of the main program.
     */
    public void restoreSymbolsMap() {   
        while ( numberOfMappings > originalMapSize) {
            doPop(1);
        }
        
    }
    
    /**
     * Provides the offset ("index") of the variable selected by the user to be
     * changed via the "change variable" command.
     * @param name of variable to be changed
     * @return the offset in the runtime stack of the variable to be changed
     */
    public int indexOfVariableToChange(String name) {
        int indexOfVariableInCurrentFrame = -1;   
        Symbol s;
        Binder b;
        Set<Map.Entry<Symbol, Binder>> entrySet = symbols.entrySet();
        for (Map.Entry<Symbol, Binder> entry : entrySet) {
            s = entry.getKey(); 
            if( (s.toString()).equals(name)) {
                b = symbols.get(s);
                indexOfVariableInCurrentFrame = (Integer)b.getValue();
                break;
            }
        }
        return indexOfVariableInCurrentFrame;
    }
    
    public String toString() {
        
        String record = ("( <");
        int entryCounter = symbols.size();              
        Symbol s;
        Binder b;
        Set<Map.Entry<Symbol, Binder>> entrySet = symbols.entrySet();
        for (Map.Entry<Symbol, Binder> entry : entrySet) {
            s = entry.getKey(); 
            b = entry.getValue(); 
 
            record=record.concat(s.toString());
            record=record.concat("/");
            String val = String.valueOf( (Integer)b.getValue() );
            record=record.concat(val);
            entryCounter--;
            if (entryCounter!=0) {
                record=record.concat(",");
            }
        }
        record=record.concat(">, ");
        if (name==null) {
            record=record.concat("-"); }
        else {
            record=record.concat(name);
        }
        
        record=record.concat(", ");
        record=record.concat(fieldAsString((Integer)startLine) );
        record=record.concat(", ");
        record=record.concat(fieldAsString((Integer)endLine) );
        record=record.concat(", ");
        record=record.concat(fieldAsString((Integer)currentLine) );
        record=record.concat(" )"); 
        
        return record;       
    }
    
    public String showVariables(DebugVirtualMachine vm) {
        String allVariablesAsString="";
        String nextPair="";
        int entryCounter = symbols.size(); 
        if (entryCounter<1) return allVariablesAsString;
        Symbol s;
        Binder b;
        Set<Map.Entry<Symbol, Binder>> entrySet = symbols.entrySet();
        for (Map.Entry<Symbol, Binder> entry : entrySet) {
            s = entry.getKey();
            b = entry.getValue();
            nextPair=nextPair.concat(s.toString()+": ");
            int offset = (Integer)b.getValue();
            int variableValue = vm.getRunStackValueAtOffset(offset);
            nextPair=nextPair.concat(String.valueOf(variableValue));
            nextPair=nextPair.concat("\n"); 
            allVariablesAsString=allVariablesAsString.concat(nextPair);
            nextPair="";
        }
        return allVariablesAsString;
    }
    
    private String fieldAsString(int i) {
        String s;
        if (i==0)
            s="-";
        else s=String.valueOf(i);
        return s;
    }
    

    /**************************************************************
    // The FunctionEnvironmentRecord class effectively ends here.
    // The methods below are legacy from the Interpreter project
    // and are not used in the debugger.
    ***************************************************************/ 
    
    public void dump() {
        System.out.print("( <");
        int entryCounter = symbols.size();
        Symbol s;
        Binder b;
        Set<Map.Entry<Symbol, Binder>> entrySet = symbols.entrySet();
        for (Map.Entry<Symbol, Binder> entry : entrySet) {
            s = entry.getKey();
            b = entry.getValue();
            System.out.print(s + "/" + (Integer)b.getValue() );
            entryCounter--;
            if (entryCounter!=0) {
                System.out.print(",");
            }
        }
        
        System.out.print(">, ");
        printField(name);
        System.out.print(", ");
        printField((Integer)startLine);
        System.out.print(", ");
        printField((Integer)endLine);
        System.out.print(", ");
        printField((Integer)currentLine);
        System.out.print(" )  ");                                   
    }
    
    /* overloaded printField methods are used to print the String or int values
     * associated with the line numbers or function name (if any)  */
    private void printField(String s) {
        if (s==null) 
            System.out.print("-"); 
        else 
            System.out.print(s);
    }
    
    private void printField(int i) {
        if (i==0)
            System.out.print("-");
        else 
            System.out.print(i);
        }
    

    public void processCommand(String command) {
        String commandIssued = "Command: " +command;
        System.out.printf("%-28s", commandIssued);
        StringTokenizer st = new StringTokenizer(command);
        String action = st.nextToken();
        switch (action) {
            case "BS":
                beginScope();
                break;
            case "Function":
                String functionName = st.nextToken();
                int firstLine = Integer.parseInt(st.nextToken());
                int lastLine = Integer.parseInt(st.nextToken());
                setFunctionInfo(functionName, firstLine, lastLine);
                break;
            case "Line":
                int setLineTo = Integer.parseInt(st.nextToken());
                setCurrentLineNumber(setLineTo);
                break;
            case "Enter":
                String varName = st.nextToken();
                int value = Integer.parseInt(st.nextToken());
                setVarVal(varName, value);
                break;
            case "Pop":
                int numberOfLevels = Integer.parseInt(st.nextToken());
                doPop(numberOfLevels);
                break;
            default:
                System.out.println("Error! Commmand not recognized.");
        }
        
        // dump the contents of the FunctionEnvironmentRecord after each command
        dump();
    
    } // end processCommand method
    
    // This main method is a river for a test case provided with the project requirements
    public static void main (String[] args) {
        // create instance of FunctionEnvironmentRecord
        FunctionEnvironmentRecord funcEnvRecord = new FunctionEnvironmentRecord();
        
        /* process a series of commands in the test case
         * and dump the contents of funcEnvRecord   */
        funcEnvRecord.processCommand("BS");
        funcEnvRecord.processCommand("Function g 1 20");
        funcEnvRecord.processCommand("Line 5");
        funcEnvRecord.processCommand("Enter a 4");
        funcEnvRecord.processCommand("Enter b 2");
        funcEnvRecord.processCommand("Enter c 7");
        funcEnvRecord.processCommand("Enter a 1");
        funcEnvRecord.processCommand("Pop 2");
        funcEnvRecord.processCommand("Pop 1");
    
    } // end main class
    
} // end FunctionEnvironmentRecord class
