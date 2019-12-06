package interpreter;

import debugger.DebugVirtualMachine;

import java.io.IOException;

/**
 *  The interpreter defined by this class performs all initializations, loads the bytecodes, and runs the VM.
 */
public class Interpreter {

    ByteCodeLoader bcl;

    // constructor for interpreter mode
    public Interpreter(String xCodFile) {
	try {
		CodeTable.init();
		bcl = new ByteCodeLoader(xCodFile);
	} catch (IOException e) {
		System.out.println("**** " + e);
       	}
    }
    
    // constructor for debugger mode
    public Interpreter(String xCodFile, boolean isDebugMode) {
        this(xCodFile); 
        CodeTable.initDebugCodes();
    }

    // Overloaded run method for interpreter mode   
    void run() {
	Program program = bcl.loadCodes();
        VirtualMachine vm = new VirtualMachine(program);
	vm.executeProgram();
    }
    
    // Overloaded run method for debugger mode
    void run(String sourceFile) {
        Program program = bcl.loadCodes();
        VirtualMachine vm = new DebugVirtualMachine(program, sourceFile);
        vm.executeProgram();
    }

    // This main method is the driver of the interpreter in either mode.
    public static void main(String args[]) {
        
        if (args.length == 0) {
            System.out.println("***Incorrect command.");
                        System.out.println("For debugger mode, enter java -jar "
                                + "Interpreter.jar -d <filename>");
                        System.out.println("For interpreter mode, enter java -jar "
                                + "Interpreter.jar <filemame>.x <filename>.x.cod");
                        System.exit(1);
        }
        String xFile, codFile;
        try {
            if (args[0].equals("-d")) {
                String file = args[1];
                xFile = file + ".x"; 
                codFile = file + ".x.cod";  
                // call the appropriate constructor for either debugger or interpreter modes
                new Interpreter(codFile,true).run(xFile);
            }
            else {
                (new Interpreter(args[1])).run();
            }
        } catch (NumberFormatException e) {
            System.out.println(e);
            System.out.println("***Incorrect command.");
            System.out.println("For debugger mode, enter java -jar "
                    + "Interpreter.jar -d <filename>");
            System.out.println("For interpreter mode, enter java -jar "
                    + "Interpreter.jar <filemame>.x <filename>.x.cod");
            System.exit(1);
        }
    }
}