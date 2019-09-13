
package interpreter;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Stack;

/**
 * RunTimeStack class manages the RunTimeStack (an ArrayList) and the frame-pointers
 * Stack.
 * Many methods are provided to allow the VirtualMachine to access and
 * manipulate the RunTimeStack and framePointers Stack.
 */

public class RunTimeStack {

    private ArrayList<Integer> runStack;
    private Stack<Integer> framePointers;

    public RunTimeStack() {
        runStack = new ArrayList<>();
        framePointers = new Stack();
    }

    /**
     * This method outputs the contents of the RunTimeStack in a formatted
     * way for easier debugging.
     * Per instructions, no arguments are passed to it.
     * The frame-pointers Stack is saved, analyzed and used in
     * printing the RunTimeStack, then restored at the end of the method.
     */
    public void dump() {

        if (runStack.isEmpty()) {
            System.out.println("[]");
        } else {
            // populate arraylist temp with framePointers elements
            ArrayList<Integer> temp = new ArrayList<>();
            while (!framePointers.empty()) {
                temp.add(framePointers.pop());
            }
            // the start and end indices of each frame
            int startIndex, endIndex;
            // indices for printing each element of a frame
            int i, j;
            // iterate through the frames
            for (i = temp.size() - 1; i > 0; i--) {
                startIndex = temp.get(i);
                endIndex = (temp.get(i - 1)) - 1;
                System.out.print("[");
                // iterate through the current frame, print elements
                for (j = startIndex; j <= endIndex; j++) {
                    System.out.print(runStack.get(j));
                    if (j != endIndex) {
                        System.out.print(",");
                    }
                }
                System.out.print("] ");
            }

            // print last frame (may be the only frame)
            System.out.print("[");
            for (j = temp.get(0); j < runStack.size(); j++) {
                System.out.print(runStack.get(j));
                if (j != runStack.size() - 1) {
                    System.out.print(",");
                }
            }
            System.out.println("] ");

            // restore framePointers Stack
            while (!temp.isEmpty()) {
                framePointers.push(temp.remove(temp.size() - 1));
            }

        }
    }

    //  Methods to manipulate the RunTimeStack 

    /**
     * Reveals the top item of the RunTimeStack and checks to make sure the
     * RunTimeStack isn't empty.  If it is, the program terminates
     * and an error message is displayed.
     *
     * @return top item on RunTimeStack
     */
    public int peek() {
        int topItem = 0;
        try {
            topItem = runStack.get((runStack.size() - 1));
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("RunTimeStack error! Stack is empty.");
            System.out.println("The program will terminate.");
            System.exit(1);
        }
        return topItem;
    }


    /**
     * Removes and returns the top item of the RunTime Stack, and checks that
     * the RunTimeStack isn't empty.  If it is, the program terminates and an
     * error message is displayed.
     *
     * @return the top item of the RunTimeStack
     */
    public int pop() {
        int removedItem = 0;
        try {
            removedItem = runStack.remove((runStack.size() - 1));
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("RunTimeStack error! Stack is empty.");
            System.out.println("The program will terminate.");
            System.exit(1);
        }

        if (runStack.size() == framePointers.peek()) {
            framePointers.pop();
        }
        return removedItem;
    }

    /**
     * Pushes an element to the top of the stack
     *
     * @param pushItem the integer item to be put atop the stack
     * @return the item pushed
     */
    public int push(int pushItem) {
        if (runStack.isEmpty() && framePointers.empty()) {
            framePointers.push(0);
        }
        runStack.add(pushItem);
        return pushItem;
    }

    /**
     * Saves the top item of the stack, pops the top frame,
     * and places the saved item atop the next frame.
     *
     * @return the top of the stack prior to popping the frame
     */
    public int popFrame() {
        // store the top value of the RunTimeStack
        int topOfStack = runStack.get(runStack.size() - 1);
        // clear the runTimeStack of components in the frame being popped
        int stopAtIndex = peekFrameStack();
        while (runStack.size() > stopAtIndex) {
            pop();
        }
        return push(topOfStack);
    }

    /**
     * Pops the given number of levels off the run time stack
     *
     * @param levels the number of items to pop from the stack
     */
    public void popLevels(int levels) {
        int i;
        for (i = 0; i < levels; i++) {
            pop();
        }
    }

    public int store(int offset) {
        int returnValue = pop(); // removes top item of runStack
        int indexToStore = peekFrameStack() + offset;
        runStack.set(indexToStore, returnValue);
        return returnValue;
    }

    public int getValueAt(int index) {
        return runStack.get(index);
    }

    public void setValueAt(int offset, int newValue) {
        runStack.set(offset, newValue);
    }

    public int load(int offset) {
        int currentFrameStart = peekFrameStack();
        int indexOfValueToLoad = currentFrameStart + offset;
        int valueToLoad = getValueAt(indexOfValueToLoad);
        return push(valueToLoad);
    }

    public int sizeOfRunStack() {
        return runStack.size();
    }

    /**
     * Provides the elements of the current frame, as an ArrayList, for use during
     * rollback in restoring the top frame to the contents that it had at the
     * start of the function's execution.
     *
     * @return ArrayList of the frame's elements, generated before a function
     * starts to execute
     */
    public ArrayList<Integer> topFrameElements() {
        ArrayList<Integer> topFrameElements = new ArrayList<>();
        int topFrameOffset = peekFrameStack();
        int i;
        for (i = topFrameOffset; i < runStack.size(); i++) {
            topFrameElements.add(runStack.get(i));
        }

        return topFrameElements;
    }

    /**
     * Clears the elements from the current frame (for use during rollback)
     */
    public void clearCurrentFrameElements() {
        int topFrameOffset = peekFrameStack();
        while (runStack.size() > topFrameOffset) {
            runStack.remove(runStack.size() - 1);
        }
    }

    public Integer push(Integer i) {
        runStack.add(i);
        return peek();
    }


    /**
     * Provides a string representation of the elements in the frame.
     *
     * @return String containing the elements of the top frame
     */
    public String topFrameAsString() {
        String topFrame = "(";
        int i;
        for (i = framePointers.peek(); i < runStack.size(); i++) {
            topFrame = topFrame.concat(String.valueOf(runStack.get(i)));
            if (i != runStack.size() - 1) {
                topFrame = topFrame.concat(",");
            }
        }
        topFrame = topFrame.concat(")");
        return topFrame;
    }

    // Methods that handle or access the framePointers Stack

    /**
     * Reveals the top value of the framePointers stack and checks to make sure
     * that framePointers isn't empty.  If it is, the program terminates and an
     * error message is displayed.
     *
     * @return the top of the framePointers stack
     */
    public int popFrameStack() {
        int returnValue = 0;
        try {
            returnValue = framePointers.pop();
        } catch (EmptyStackException e) {
            System.out.println("Error! The framePointers stack is empty.");
            System.out.println("The program will terminate.");
            System.exit(1);
        }
        return returnValue;
    }

    /**
     * Removes and returns the top of the framePointers stack, and checks that
     * it isn't empty.  If it is, the program terminates and an error message
     * is displayed.
     *
     * @return the top of the framePointers stack
     */
    public int peekFrameStack() {
        int returnValue = 0;
        try {
            returnValue = framePointers.peek();
        } catch (EmptyStackException e) {
            System.out.println("Error! The framePointers stack is empty.");
            System.out.println("The program will terminate.");
            System.exit(1);
        }
        return returnValue;


    }

    public void newFrameAt(int startIndex) {
        framePointers.push(startIndex);
    }

} // end class
