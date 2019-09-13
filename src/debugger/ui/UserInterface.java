
package debugger.ui;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * UserInterface class communicates with the user of the debugger and acts as a
 * liaison between the user and the debugger.  The DebugVirtualMachine instructs
 * the UserInterface as to what it wants printed, and the UserInterface informs 
 * the debugger about the user's commands.  This user interface can be easily 
 * swapped out for another one because there is minimal coupling between this
 * class and the DebugVirtualMachine.
 * 
 */

public class UserInterface {
    
    private BufferedReader reader;
    
    public UserInterface () {}
    
    public void print(String s) {
        System.out.print(s);
    }
    
    public void printPrompt () {
        System.out.print("Type ? for help\n> ");
    }
    
    public void printLeftAligned(String s) {
        System.out.printf("%-15s", s);
    }
    
    public String getUserAction () {
        String command=null;
        reader = new BufferedReader(new InputStreamReader(System.in));
        //System.out.print("Enter your name: ");
        
        try  {
            command = reader.readLine();
        } catch (Exception e) {}
        return command;
    }
    
}
