
package debugger;

/**
 * DebugEntries hold information about source code lines.  Each instance of 
 * this class holds a single line of source code, and indicates whether a break
 * point has been set in the debugger for that line.
 * 
 */

public class DebugEntries {
    private String sourceLine;
    boolean isBreakPointSet;
    int lineNumber;
    
    public DebugEntries () {}
    
    public DebugEntries (String source, int line) {
        sourceLine = source;
        lineNumber = line;
    }
    
    public String getSourceLine () {
        return sourceLine;
    }
    
    public boolean isBreakPointSet () {
        return isBreakPointSet;
    }
    
    public void setIsBreakPointSet(boolean changeBkPt) {
        isBreakPointSet = changeBkPt;
    }
    
    public int getLineNumber () {
        return lineNumber;
    }
            
    
}
