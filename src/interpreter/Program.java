
package interpreter;

import interpreter.bytecode.*;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Program class manages the Program file, an ArrayList that contains
 * each instance of the concrete byte code classes. It also has a method
 * to resolve symbolic addresses into numeric locations within the ArrayList
 * that correspond to the associated label.
 */

public class Program {
    private ArrayList<ByteCode> codes;

    public Program() {
        codes = new ArrayList<>();
    }

    public void addCode(ByteCode b) {
        codes.add(b);
    }

    public ByteCode getCode(int index) {
        ByteCode bc = codes.get(index);
        return bc;
    }

    /**
     * This method uses a HashMap, in which the keys are the Strings associated with
     * each LabelCode and the values are the integer locations of the LabelCode
     * instances.
     * Step 1: Iterate through Program; when a LabelCode is found, create a map
     * entry.
     * Step 2: Iterate through Program again; when one of the other byte code
     * types, like FalseBranch, Call, Return or Goto, is encountered, look up
     * the integer position corresponding to its label and provide that integer
     * address within the Program list to the byte code instance.
     */
    public void resolveAddresses() {

        HashMap<String, Integer> map = new HashMap();
        Class cl;
        String className;
        int position = 0; // the ArrayList position during iteration

        // Step 1: iterate through Program and populate HashMap

        for (ByteCode bc : codes) {
            cl = bc.getClass();
            className = cl.getName();
            if (className.equals("interpreter.bytecode.LabelCode") ||
                    className.equals("interpreter.bytecode.DebugLabelCode")) {
                LabelCode label = (LabelCode) bc;
                map.put(label.getLabel(), position);
            }
            position++;
        } // end for loop

        // Step 2: resolve symbolic addresses of relevant bytecode instances
        for (ByteCode bc : codes) {
            cl = bc.getClass();
            className = cl.getName();
            if (className.equals("interpreter.bytecode.FalseBranchCode")) {
                FalseBranchCode fbcode = (FalseBranchCode) bc;
                String fbString = fbcode.getLabel();
                fbcode.setTarget(map.get(fbString));
            }
            if (className.equals("interpreter.bytecode.GoToCode")) {
                GoToCode gtcode = (GoToCode) bc;
                String gtString = gtcode.getLabel();
                gtcode.setTarget(map.get(gtString));
            }
            if (className.equals("interpreter.bytecode.CallCode") ||
                    className.equals("interpreter.bytecode.DebugCallCode")) {
                CallCode ccode = (CallCode) bc;
                String cString = ccode.getLabel();
                ccode.setTarget(map.get(cString));
            }
            if (className.equals("interpreter.bytecode.ReturnCode") ||
                    className.equals("interpreter.bytecode.DebugReturnCode")) {
                ReturnCode rcode = (ReturnCode) bc;
                if (rcode.getLabel() != null) {
                    String rString = rcode.getLabel();
                    rcode.setReturnValue(map.get(rString));
                }
            }

        }
    }

    /**
     * Provides all numbers for which a Line byte code exists.
     *
     * @return ArrayList of all line code numbers
     */
    public ArrayList<Integer> getAllLineCodeNumbers() {
        ArrayList<Integer> lines = new ArrayList<>();
        Class cl;
        String className;
        int nextLineNumber;
        for (ByteCode bc : codes) {
            cl = bc.getClass();
            className = cl.getName();
            if (className.equals("interpreter.bytecode.LineCode")) {
                LineCode lc = (LineCode) bc;
                nextLineNumber = lc.getLineNumber();
                if (nextLineNumber > 0)
                    lines.add(nextLineNumber);
            }
        }
        return lines;
    }

}
