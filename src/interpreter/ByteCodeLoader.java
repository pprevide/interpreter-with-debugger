
package interpreter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * ByteCodeLoader class does the following:
 * 1. Reads through the bytecode file
 * 2. Creates instances of each concrete class of bytecodes
 * 3. Parses the arguments of each byte code in the file and puts them in ArrayLists
 * 4. Initializes each bytecode instance by passing the ArrayList for processing
 * by the individual bytecode
 * 5. Resolves the symbolic addresses in the Program instance
 */

public class ByteCodeLoader {

    private String filename = null;

    public ByteCodeLoader() {
    }

    public ByteCodeLoader(String file) throws IOException {
        filename = file;
    }

    /**
     * This method reads the bytecode file, creates appropriate instances of the
     * concrete bytecode classes, and initializes those bytecode instances by
     * passing an ArrayList containing the arguments to those instances for
     * processing.  The particular bytecode instance handles processing of
     * the arguments during initialization of the instance.
     *
     * @return Program instance populated with concrete bytecode class instances
     */
    public Program loadCodes() {
        Program program = new Program();
        BufferedReader reader = null;
        try {
            reader = createReader(filename);
        } catch (Exception e) {
        }
        ByteCode bc = null;
        StringTokenizer st;
        // args ArrayList holds the bytecode's arguments
        ArrayList<String> args = new ArrayList<>();
        String nextLine = null;
        String nextToken;
        String nextClass;
        int i = 1;
        while (true) {
            System.out.println("i is " + i);
            i++;
            try {
                nextLine = reader.readLine();
            } catch (Exception e) {
                System.exit(1);
            }
            if (nextLine == null) break;
            st = new StringTokenizer(nextLine);
            nextToken = st.nextToken();
            // use CodeTable to get the text of the bytecode's class name
            nextClass = CodeTable.get(nextToken);
            // use reflection to create new instances of the bytecodes
            try {
                bc = (ByteCode) (Class.forName("interpreter.bytecode." + nextClass).newInstance());
            } catch (ClassNotFoundException | InstantiationException |
                    IllegalAccessException e) {
                System.out.println("Got here");
            }

            Class Cl;
            try {
                Cl = bc.getClass();
            } catch (NullPointerException npe) {
                System.out.println("Null pointer exception!!!!!");
                System.out.println(npe.getMessage());
                System.out.println(System.getProperty("user.dir"));
            }
            //Class cl = bc.getClass();
            while (st.hasMoreTokens()) {
                nextToken = st.nextToken();
                args.add(nextToken);
            }
            /* Pass the ArrayList args, now populated with the bytecode's 
             * arguments, to the bytecode instance so that the bytecode
             * instance can do the processing of the arguments.
             */
            bc.init(args);

            // Place the now-initialized bytecode instance into Program instance
            program.addCode(bc);

            args.clear();  // reset the ArrayList for the next bytecode from file
        } 
              
    /* 
     * The Program instance iterates through the bytecodes and resolves 
     * symbolic addresses into particular numeric addresses.
     */
        program.resolveAddresses();
        return program;
    } // end loadCodes method

    private BufferedReader createReader(String codefile) throws IOException {
        BufferedReader reader = null;
        reader = new BufferedReader(new FileReader(codefile));
        return reader;
    }


} 
