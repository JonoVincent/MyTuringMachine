import java.util.ArrayList;
import java.util.Arrays;

public class SyntaxChecker {
    private SyntaxTreeNode rootNode;
    private ArrayList<String> stateNameList;
    private String errorLog = "";
    private boolean hasError;
    private ArrayList<String> alphabetList;

    private final static String alphaKey = "Alphabet";
    private final static String tapeKey = "Tape";
    private final static String startKey = "Start";
    private final static String stateKey = "State";
    private final static String readKey = "Read";
    private final static String writeKey = "Write";
    private final static String moveKey = "Move";
    private final static String gotoKey = "Goto";
    final static String trueStateKey = "True";
    final static String falseStateKey = "False";

    private final static ArrayList<String> protectedSymbols = new ArrayList<String> (Arrays.asList(":", ";", ".", "=", ","));
    private final static ArrayList<String> validDirections = new ArrayList<String> (Arrays.asList("Left", "Right", "None"));

    public SyntaxChecker(SyntaxTreeNode rootNode, ArrayList<String> stateNameList){
        this.rootNode = rootNode;
        this.stateNameList = stateNameList;
        stateNameList.add(trueStateKey);
        stateNameList.add(falseStateKey);

    }

    public boolean getHasError(){
        return(hasError);
    }

    public String getErrorLog(){
        return(errorLog);
    }

    public void checkSyntax(){
        boolean hasAlpha = false;
        boolean hasTape = false;
        boolean hasStart = false;
        ArrayList<SyntaxTreeNode> allKeyWords = rootNode.getAllChildNodes();
        for(int i = 0; i < allKeyWords.size(); i ++){
            if(allKeyWords.get(i).getValue().equals(alphaKey)){
                if(!hasAlpha){
                    hasAlpha = true;
                    checkAlphaStatement(allKeyWords.get(i));
                    if(hasTape){
                        checkTapeStatement(allKeyWords.get(i));
                    }
                }else{
                    hasError = true;
                    errorLog = errorLog + "Your TMcode had multiple Alphabet Statements. The TMcode should only have 1 Alphabet.\n";
                }
            }else if(allKeyWords.get(i).getValue().equals(tapeKey)){
                if(!hasTape){
                    hasTape = true;
                    if(hasAlpha){
                        checkTapeStatement(allKeyWords.get(i));
                    }
                }else{
                    hasError = true;
                    errorLog = errorLog + "Your TMcode had multiple Tape Statements. The TMcode should only have 1 Tape.\n";
                }
            }else if(allKeyWords.get(i).getValue().equals(startKey)){
                if(!hasStart){
                    hasStart = true;
                    checkState(allKeyWords.get(i));
                }else{
                    hasError = true;
                    errorLog = errorLog + "Your TMcode had multiple Start States. The TMcode should only have 1 Start State.\n";
                }
            }else if(allKeyWords.get(i).getValue() == stateKey){
                checkState(allKeyWords.get(i));
            }
        }
        if(!hasAlpha){
            errorLog = errorLog + "Your TMcode does not contain an alphabet declaration.\n";
        }
        if(!hasTape){
            errorLog = errorLog + "Your TMcode does not contain a tape declaration.\n";
        }
        if(!hasStart){
            errorLog = errorLog + "Your TMcode does not contain a start state.\n";
        }
    }

    private void checkAlphaStatement(SyntaxTreeNode alphabetNode){
        ArrayList<SyntaxTreeNode> valueNode = alphabetNode.getAllChildNodes();
        if(valueNode.size() == 0){
            hasError = true;
            errorLog = errorLog + "Your TMcode alphabet has no value. Make sure you have Alphabet = .\n";
        }else if (valueNode.size() > 1){
            hasError = true;
            errorLog = errorLog + "Your TMcode alphabet has mutlpile values. Make sure you only have one = after Alphabet.\n";
        }else{
            SyntaxTreeNode alphabetValueNode = valueNode.get(0);
            String alphabet = alphabetValueNode.getValue();
            alphabetList = new ArrayList<String>(Arrays.asList(alphabet.split(",")));
            alphabetList.add("Empty");
            for(int i = 0; i < alphabetList.size(); i++){
                for(int i2 = 0; i2 < protectedSymbols.size(); i2++){
                    if(alphabetList.get(i).contains(protectedSymbols.get(i2))){
                        hasError = true;
                        errorLog = errorLog + "Your Alphabet contains a protected symbol. Your Alphabet shouldn't contain " + protectedSymbols.get(i2) + ".\n";
                    }
                }
            }
        }
    }

    private void checkTapeStatement(SyntaxTreeNode tapeNode){
        ArrayList<SyntaxTreeNode> valueNode = tapeNode.getAllChildNodes();
        if(valueNode.size() == 0){
            hasError = true;
            errorLog = errorLog + "Your TMcode tape has no value. Make sure you have Tape = .\n";
        }else if (valueNode.size() > 1){
            hasError = true;
            errorLog = errorLog + "Your TMcode tape has mutlpile values. Make sure you only have one = after Tape.\n";
        }else{
            String tape = valueNode.get(0).getValue();
            ArrayList<String> tapeList = new ArrayList<>(Arrays.asList(tape.split(",")));
            for(int i = 0; i < tapeList.size(); i++){
                if(!(alphabetList.contains(tapeList.get(i)))){
                    hasError = true;
                    errorLog = errorLog + "Your tape contians a symbol not included in your alphabet: " + tapeList.get(i) + "\n";
                }
            }
        }
    }

    private void checkState(SyntaxTreeNode stateNode){
        ArrayList<SyntaxTreeNode> stateNameNode = stateNode.getAllChildNodes();
        if(stateNameNode.size() == 0){
            hasError = true;
            errorLog = errorLog + "One of your states has no name assigned to it. Make sure all your states have State : NAME = .\n";
        }else if (stateNameNode.size() > 1){
            hasError = true;
            errorLog = errorLog + stateNameNode.get(0).getValue() + " has mulitple names assigned to it. Make sure you only have one : after the State declaration.\n";
        }else{
            String stateName = stateNameNode.get(0).getValue();
            if(!(stateNameList.contains(stateName))){
                hasError = true;
                errorLog = errorLog + ("This error exist as a precaution. There is an unknown problem with " + stateName + " make sure this state is correct.\n");
            }
            ArrayList<SyntaxTreeNode> instructionNodeList = stateNameNode.get(0).getAllChildNodes();
            for(int i = 0; i < instructionNodeList.size(); i++){
                checkInstruction(instructionNodeList.get(i));
            }
        }
    }

    private void checkInstruction(SyntaxTreeNode instructionNode){
        ArrayList<SyntaxTreeNode> subinstructionList = instructionNode.getAllChildNodes();
        if(subinstructionList.get(0).getValue().equals(readKey)){
            checkRead(subinstructionList.get(0));
        }else{
            hasError = true;
            errorLog = errorLog + "Missing a Read subinstruction. Every instruction should start with a Read subinstruction.\n";
        }
        if(subinstructionList.get(1).getValue().equals(writeKey)){
            checkWrite(subinstructionList.get(1));
        }else{
            hasError = true;
            errorLog = errorLog + "Missing a Write subinstruction. Every instruction's 2nd subinstruction should be a Write subinstruction.\n";
        }
        if(subinstructionList.get(2).getValue().equals(moveKey)){
            checkMove(subinstructionList.get(2));
        }else{
            hasError = true;
            errorLog = errorLog + "Missing a Move subinstruction. Every instruction's 3rd subinstruction should be a Move subinstruction.\n";
        }
        if(subinstructionList.get(3).getValue().equals(gotoKey)){
            checkGoto(subinstructionList.get(3));
        }else{
            hasError = true;
            errorLog = errorLog + "Missing a Goto subinstruction. Every instruction's 4th subinstruction should be a Goto subinstruction.\n";
        }
        if(subinstructionList.size() > 4){
            hasError = true;
            errorLog = errorLog + "An instruction has to many subinstructions. Check for extra commas.\n";
        }
    }

    private void checkRead(SyntaxTreeNode readNode){
        ArrayList<SyntaxTreeNode> readValueNodes = readNode.getAllChildNodes();
        if(readValueNodes.size() > 1){
            hasError = true;
            errorLog = errorLog + "You have passed more than one value to the a Read instruction.\n" ;   
        }else if(readValueNodes.size() < 1){
            hasError = true;
            errorLog = errorLog + "You haven't passed a value to a Read instruction.\n" ;
        }if(readValueNodes.size() == 1){
            if(!(alphabetList.contains(readValueNodes.get(0).getValue()))){
                hasError = true;
                errorLog = errorLog + "The symbol " + readValueNodes.get(0).getValue() + " isn't part of the alpahbet, so can't be passed to a Read instruction.\n";
            }
        }
    }

    private void checkWrite(SyntaxTreeNode writeNode){
        ArrayList<SyntaxTreeNode> writeValueNodes = writeNode.getAllChildNodes();
        if(writeValueNodes.size() > 1){
            hasError = true;
            errorLog = errorLog + "You have passed more than one value to the a Write instruction.\n" ;
        }else if(writeValueNodes.size() < 1){
            hasError = true;
            errorLog = errorLog + "You haven't passed a value to the a Write instruction.\n" ;
        }if(writeValueNodes.size() == 1){
            if(!(alphabetList.contains(writeValueNodes.get(0).getValue()))){
                hasError = true;
                errorLog = errorLog + "The symbol " + writeValueNodes.get(0).getValue() + " isn't part of the alpahbet, so can't be passed to a Write instruction.\n" ;
            }
        }
    }

    private void checkMove(SyntaxTreeNode moveNode){
        ArrayList<SyntaxTreeNode> moveValueNodes = moveNode.getAllChildNodes();
        if(moveValueNodes.size() > 1){
            hasError = true;
            errorLog = errorLog + "You have passed more than one value to the a Move instruction.\n" ;
        }else if(moveValueNodes.size() < 1){
            hasError = true;
            errorLog = errorLog + "You haven't passed a value to the a Move instruction.\n" ;
        }if(moveValueNodes.size() == 1){
            if(!(validDirections.contains(moveValueNodes.get(0).getValue()))){
                hasError = true;
                errorLog = errorLog + "The value " + moveValueNodes.get(0).getValue() + " isn't a valid direction, so can't be passed to a Move instruction.\n" ;
            }
        }
    }
    private void checkGoto(SyntaxTreeNode gotoNode){
        ArrayList<SyntaxTreeNode> gotoValueNodes = gotoNode.getAllChildNodes();
        if(gotoValueNodes.size() > 1){
            hasError = true;
            errorLog = errorLog + "You have passed more than one value to the a Goto instruction.\n" ;
        }else if(gotoValueNodes.size() < 1){
            hasError = true;
            errorLog = errorLog + "You haven't passed a value to the a goto instruction.\n" ;
        }if(gotoValueNodes.size() == 1){
            if(!(stateNameList.contains(gotoValueNodes.get(0).getValue()))){
                hasError = true;
                errorLog = errorLog + "The value " + gotoValueNodes.get(0).getValue() + " isn't a State, so can't be passed to a Goto instruction.\n" ;
            }
        }
    }
    
}
