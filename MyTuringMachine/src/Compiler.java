import java.util.ArrayList;
import java.util.Arrays;


public class Compiler {
    final static String alphaKey = "Alphabet";
    final static String tapeKey = "Tape";
    final static String startKey = "Start";
    final static String stateKey = "State";
    final static String readKey = "Read";
    final static String writeKey = "Write";
    final static String moveKey = "Move";
    final static String gotoKey = "Goto";
    final static String trueStateKey = "True";
    final static String falseStateKey = "False";

    //Compiler takes in no arguments for instancing as relevant armugmennts are passed during method calls
    public Compiler(){
    }

    public ArrayList<String> getStateList(String tmCode){
        ArrayList<String> stateList = new ArrayList<String>();
        tmCode = tmCode.replaceAll("\\r\\n|\\r|\\n", "");
        ArrayList<String> tmCodeLines = new ArrayList<String>(Arrays.asList(tmCode.split(";")));
        for(int i = 0; i < tmCodeLines.size(); i++){
            String currentLine = tmCodeLines.get(i).replaceAll(" ", "");
            ArrayList<String> tmCodeLineBreak = new ArrayList<String>(Arrays.asList(currentLine.split("=")));
            String keyWord = tmCodeLineBreak.get(0);
            ArrayList<String> splitKeyWord = new ArrayList<String>(Arrays.asList(keyWord.split(":")));
            if(splitKeyWord.get(0).equals(startKey)){
                stateList.add(0, splitKeyWord.get(1).replace(" ", ""));
            }else if (splitKeyWord.get(0).equals(stateKey)){
                stateList.add(splitKeyWord.get(1).replace(" ", ""));
            }
        }
        return(stateList);
    }

    public SyntaxTreeNode getSyntaxTree(String tmCode){
        tmCode = tmCode.replaceAll("\\r\\n|\\r|\\n", "");
        tmCode = tmCode.replaceAll(" ", "");
        SyntaxTreeNode syntaxTreeRoot = new SyntaxTreeNode("root");
        ArrayList<String> tmCodeLines = new ArrayList<String>(Arrays.asList(tmCode.split(";")));
        for(int i = 0; i < tmCodeLines.size(); i++){
            String currentLine = tmCodeLines.get(i);
            ArrayList<String> tmCodeLineBreak = new ArrayList<String>(Arrays.asList(currentLine.split("=")));
            String keyWord = tmCodeLineBreak.get(0);
            String value = tmCodeLineBreak.get(1);
            SyntaxTreeNode currentNode;
            ArrayList<String> splitKeyWord = new ArrayList<String>(Arrays.asList(keyWord.split(":")));
            if(splitKeyWord.size() == 1){
                syntaxTreeRoot.addChildNode(keyWord);
                currentNode = syntaxTreeRoot.getChildNode(i);
                currentNode.addChildNode(value);
            }else if(splitKeyWord.size() == 2){
                syntaxTreeRoot.addChildNode(splitKeyWord.get(0));
                currentNode = syntaxTreeRoot.getChildNode(i);
                currentNode.addChildNode(splitKeyWord.get(1));
                currentNode = currentNode.getChildNode(0);
                getStateTree(value, currentNode);
            }
        }
        return(syntaxTreeRoot);
    }

    private void getStateTree(String state, SyntaxTreeNode parentNode){
        SyntaxTreeNode stateTreeRoot = parentNode;
        ArrayList<String> instructionList  = new ArrayList<String>(Arrays.asList(state.split("\\.")));
        SyntaxTreeNode currentNode;
        for(int i = 0; i < instructionList.size(); i++ ){
            stateTreeRoot.addChildNode(Integer.toString(i));
            currentNode = stateTreeRoot.getChildNode(i);
            getInstructionTree(instructionList.get(i), currentNode);
        }
    }

    private void getInstructionTree(String instruction, SyntaxTreeNode parentNode){
        SyntaxTreeNode instructionTreeRoot = parentNode;
        ArrayList<String> subinstructionList = new ArrayList<String>(Arrays.asList(instruction.split(",")));
        SyntaxTreeNode currentNode;
        for(int i = 0; i < subinstructionList.size(); i++ ){
            ArrayList<String> splitSubinstruction = new ArrayList<String>(Arrays.asList(subinstructionList.get(i).split(":")));
            instructionTreeRoot.addChildNode(splitSubinstruction.get(0));
            currentNode = instructionTreeRoot.getChildNode(i);
            for (int i2 = 1; i2 < splitSubinstruction.size(); i2++ ){
                currentNode.addChildNode(splitSubinstruction.get(i2));
            }
        }
    }

    public String translateSyntaxTree(SyntaxTreeNode rootNode, ArrayList<String> stateNameList){
        String executorCode = "";
        String allStatesExecutorCode = "";
        String alphabetValue = "";
        String tapeValue = "";
        String startValue = "";
        ArrayList<SyntaxTreeNode> keyWordList = rootNode.getAllChildNodes();
        for(int i = 0; i < keyWordList.size(); i++ ){
            String keyWord = keyWordList.get(i).getValue();
            if(keyWord.equals(alphaKey)){
                alphabetValue = keyWordList.get(i).getChildNode(0).getValue() + ",Empty;";
            } else if(keyWord.equals(tapeKey)){
                tapeValue = keyWordList.get(i).getChildNode(0).getValue() + ";";
            } else if(keyWord.equals(startKey)){
                startValue = translateState(keyWordList.get(i), stateNameList);
            } else if(keyWord.equals(stateKey)){
                allStatesExecutorCode = allStatesExecutorCode + translateState(keyWordList.get(i), stateNameList);
            }
        }
        executorCode = alphabetValue + tapeValue + startValue + allStatesExecutorCode;
        return(executorCode);
    }

    private String translateState(SyntaxTreeNode stateNode, ArrayList<String> stateNameList){
        String stateExecutorCode = "";
        String instructionCode = "";
        stateNode = stateNode.getChildNode(0);
        ArrayList<SyntaxTreeNode> instructionNodeList = stateNode.getAllChildNodes();
        for(int i = 0; i < instructionNodeList.size(); i++){
            SyntaxTreeNode instruction = instructionNodeList.get(i);
            String readValue = instruction.getChildNode(0).getChildNode(0).getValue();
            String writeValue = instruction.getChildNode(1).getChildNode(0).getValue();
            String moveValue = instruction.getChildNode(2).getChildNode(0).getValue();
            String stateName = instruction.getChildNode(3).getChildNode(0).getValue();
            String gotoValue = "";
            if(stateName.equals(trueStateKey)){
                gotoValue = "-1";
            }else if (stateName.equals(falseStateKey)){
                gotoValue = "-2";
            }else{
                for(int i2 = 0; i2 < stateNameList.size(); i2++){
                    if(stateNameList.get(i2).equals(stateName)){
                        gotoValue = Integer.toString(i2);
                    }
                }
            }
            if(i == (instructionNodeList.size() - 1)){
                instructionCode = readValue + "," + writeValue + "," + moveValue + "," + gotoValue + ";";
            }else{
                instructionCode = readValue + "," + writeValue + "," + moveValue + "," + gotoValue + ":";
            }
            stateExecutorCode = stateExecutorCode + instructionCode;
        }
        System.out.println(stateExecutorCode);
        return(stateExecutorCode);
    }

    public String compileCode(String tmCode){
        ArrayList<String> stateList = getStateList(tmCode);
        SyntaxTreeNode rootNode = getSyntaxTree(tmCode);
        return(translateSyntaxTree(rootNode, stateList));
    }


}




    
