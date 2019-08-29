/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package interpreter;

/**
 * CodeTable class creates and populates a HashMap with all possible bytecode
 * types and names.
 * The Map, via the get() method here, is used by ByteCodeLoader class to look up 
 * a name from the bytecode file and retrieve the text of the name of the 
 * corresponding class in order to make a new instance of that class.
 * 
 */
public class CodeTable {
    private String key;
    private String value;
    private static String[] codeNames = {"HALT", "POP", "FALSEBRANCH", "GOTO", "STORE",
        "LOAD", "LIT", "ARGS", "CALL", "RETURN", "BOP", "READ", "WRITE", "LABEL", 
        "DUMP", "LINE", "FUNCTION", "FORMAL"};
    private static String[] classNames = {"HaltCode", "PopCode", "FalseBranchCode", 
        "GoToCode", "StoreCode", "LoadCode", "LitCode", "ArgsCode", "CallCode", 
        "ReturnCode", "BopCode", "ReadCode", "WriteCode", "LabelCode", 
        "DumpCode", "LineCode", "FunctionCode", "FormalCode"};
    
    private static java.util.HashMap<String, String> codes = new java.util.HashMap<>();
    
    /* populates the HashMap with keys (code names from the file) and 
     * values (the text of the corresponding concrete class names)
     */
    public static void init() {
        int i;
        String codeName, className;
        for (i=0; i<codeNames.length; i++){
            codeName = codeNames[i];
            className = classNames[i];
            codes.put(codeName, className);
        }
    }
    
    /*
     * When in debugger mode, this method is called to map the indicated
     * byte codes to the appropriate concrete class names
     */
    public static void initDebugCodes() {
        codes.put("LIT", "DebugLitCode");
        codes.put("POP", "DebugPopCode");
        codes.put("ARGS", "DebugArgsCode");
        codes.put("CALL","DebugCallCode");
        codes.put("RETURN", "DebugReturnCode");
    }
   
    public static String get(String key) {
        return codes.get(key);
    }
}
