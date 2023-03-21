import java.util.ArrayList;
import java.util.Arrays;


public class Executor{
    //the code being executed
    private ArrayList<String> tmCodeLines;
    //the code alphabet for the tm
    private ArrayList<String> alphabet;
    //the tape for the tm
    private ArrayList<String> tape;
    //the current state of the tm
    private int currentState = 0;
    //modifier added to currentState to find it's location in tmCodeLines
    private static final int stateMod = 2;
    //the location of the read write head in the tm  
    private int headIndex = 0;
    //modifier added to head location to find the correct location in the tape arraylist
    private int headIndexMod = 0;
    //int value representing true state
    private static final int trueState = -1;
    //int value representing false state
    private static final int falseState = -2;

    public Executor(String compiledTmCode){
        //Splits code into seaprate lines and stores them all to an array
        tmCodeLines = new ArrayList<String>(Arrays.asList(compiledTmCode.split(";")));
        //sets the values for the alphabet and tape
        String tapeLine = tmCodeLines.get(1);
        String alphaLine = tmCodeLines.get(0);
        tape = new ArrayList<String>(Arrays.asList(tapeLine.split(",")));
        alphabet = new ArrayList<String>(Arrays.asList(alphaLine.split(",")));
    }
    /*Method that executes code passed to Executor.
    Input - non, uses global variable TMCode
    Returns - non
    Side Effects - Sets values of tape and alphabet
    */
    public void executeCode(){
        //while loop that runs until tm goes to states -1 or -2 
        while(currentState != trueState && currentState != falseState ){
            executeState();
        }
        System.out.println("Completed");
        
    }
    /*This method executes a single state
     * input - non 
     * returns - non
     */
    private void executeState(){
        //adjusts state to account for first 2 lines being tapea and alphabet
        String state = tmCodeLines.get(currentState + stateMod);
        //Splits state into it's individual instructions 
        String[] instructionList = state.split(":");
        //loops through instructions, finds the correct instruction and performs it
        int i = 0;
        String[] currentInstruction = instructionList[i].split(",");
        while(!(currentInstruction[0].equals(tape.get(headIndex + headIndexMod)))){
            i++;
            currentInstruction = instructionList[i].split(",");
        }
        executeInstruction(currentInstruction);
    }
    /*method that executes a single instruction
     * input - non
     * returns - non
     * side effects - changes value of headIndex 
     *              - changes value of headIndexMod
     *              - changes value of currentState
     */
    private void executeInstruction(String[] instruction){   
        //Handles write instruction
        tape.set((headIndex + headIndexMod), instruction[1]);
        //Handles move RIGHT instruction
        if(instruction[2].equals("Right")){                
            headIndex ++;
            //Handles read/write head going beyond current tape size
            if(headIndex + headIndexMod > tape.size() - 1){
                tape.add("Empty");
            }
        //handles read/write head going into a negative index
        }else if(instruction[2].equals("Left")){;
            headIndex --;
            if(headIndex + headIndexMod < 0){
                tape.add(0, "Empty");
                headIndexMod ++;
            }
        }
        //handles goto state instruction
        currentState = Integer.valueOf(instruction[3]);
    }


    public ArrayList<String> executeStep(){
        ArrayList<String> nextStep = new ArrayList<String>();
        String formattedTape = "";
        if((currentState != -1) && (currentState != -2)){
            executeState();
        }
        nextStep.add(Integer.toString(currentState));
        nextStep.add(Integer.toString(headIndex));
        for(int i2 = 0; i2 < tape.size(); i2++){
            if(i2 == (headIndex + headIndexMod)){
                formattedTape = formattedTape + ("[" + tape.get(i2) + "],");
            }else{
                formattedTape = formattedTape + (tape.get(i2) + ",");
            }
        }
        nextStep.add(formattedTape);
        return(nextStep);
    }
}


