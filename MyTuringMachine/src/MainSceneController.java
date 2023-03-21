
import java.util.ArrayList;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;


public class MainSceneController {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private TextArea codeInputBox;

    @FXML
    private TextArea codeOutputBox;

    @FXML
    private Button runButton;

    @FXML
    private Button fastForwardButton;

    @FXML
    private Button stepButton;

    @FXML
    private Label tapeOutputLabel;

    @FXML
    private CheckBox textOutputCheckBox;

    private int stepPauseTime = 500;

    private String tmCode;

    private boolean pausePressed = true;

    private Compiler compiler = new Compiler();

    private Executor executor;

    private SyntaxChecker syntaxChecker;

    private StateCircleDrawer stateCircleDrawer = new StateCircleDrawer();

    private ArrayList<StackPane> stateCircleList;

    private String compiledCode;

    private boolean shouldResetOutputBox = true;

    private boolean outputThreadStarted = false;

    @FXML
    synchronized void runButtonHandler(ActionEvent event) {
        if(pausePressed == true){
            runButton.setText("Pause");
            notify();
            if (!outputThreadStarted) {
                outputThread.setDaemon(true);
                outputThread.start();
                outputThreadStarted = true;
            }
    
            if (shouldResetOutputBox) {
                codeOutputBox.setText("");
                tmCode = codeInputBox.getText();
                syntaxChecker = new SyntaxChecker(compiler.getSyntaxTree(tmCode), compiler.getStateList(tmCode));
                syntaxChecker.checkSyntax();
                if (syntaxChecker.getHasError()) {
                    codeOutputBox.setText(syntaxChecker.getErrorLog());
                    shouldResetOutputBox = true;
                    pausePressed = true;
                } else {
                    compiledCode = compiler.compileCode(tmCode);
                    executor = new Executor(compiledCode);
                    shouldResetOutputBox = false;
                }
            }
        }else{
            runButton.setText("Run");
        }
        
        pausePressed = !pausePressed;
    }

    @FXML
    void stepButtonHandler(ActionEvent event) {
        if (shouldResetOutputBox) {
            codeOutputBox.setText("");
            tmCode = codeInputBox.getText();
            syntaxChecker = new SyntaxChecker(compiler.getSyntaxTree(tmCode), compiler.getStateList(tmCode));
            syntaxChecker.checkSyntax();
            if (syntaxChecker.getHasError()) {
                codeOutputBox.setText(syntaxChecker.getErrorLog());
                shouldResetOutputBox = true;
                pausePressed = true;
            } else {
                compiledCode = compiler.compileCode(tmCode);
                executor = new Executor(compiledCode);
                shouldResetOutputBox = false;
            }
        }

        if(stateCircleList != null){
            Platform.runLater(() -> removeStateCircles());
        }
        if(textOutputCheckBox.isSelected()){
            codeOutputBox.setVisible(true);
            ArrayList<String> nextStep = executor.executeStep();
            String stepOutput = (nextStep.get(0) + "-" + nextStep.get(1) + "-" + nextStep.get(2) + "\n");
            Platform.runLater(() -> codeOutputBox.appendText(stepOutput));
        }else{
            Platform.runLater(() -> removeStateCircles());
            codeOutputBox.setVisible(false);
            ArrayList<String> nextStep = executor.executeStep();
            Platform.runLater(() -> printStateCircles(Integer.parseInt(nextStep.get(0))));
            Platform.runLater(() -> tapeOutputLabel.setText(nextStep.get(2)));
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    @FXML
    void stopButtonHandler(ActionEvent event) {
        shouldResetOutputBox = true;
        pausePressed = true;
        runButton.setText("Run");
    }


    @FXML
    void fastForwardButtonHandler(ActionEvent event){
        if(stepPauseTime == 500){
            stepPauseTime = 250;
            fastForwardButton.setText("x2 Speed");
        }else if(stepPauseTime == 250){
            stepPauseTime = 125;
            fastForwardButton.setText("x4 Speed");
        }else if(stepPauseTime ==  125){
            stepPauseTime = 500;
            fastForwardButton.setText("x1 Speed");
        }
    }

    @FXML
    void textOutputCheckBoxHandler(ActionEvent event) {
        shouldResetOutputBox = true;
        pausePressed = true;
    }

    private synchronized boolean getPausePressed() {
        return (pausePressed);
    }

    private void printStateCircles(int currentState) {
        stateCircleList = stateCircleDrawer.calculateStateCircles(compiler.getStateList(tmCode), currentState);
        for (int i = 0; i < stateCircleList.size(); i++) {
            (anchorPane).getChildren().add(stateCircleList.get(i));
        }
    }

    private void removeStateCircles(){
        for (int i = 0; i < stateCircleList.size(); i++) {
            (anchorPane).getChildren().remove(stateCircleList.get(i));
        }
    }

    Runnable outputThreadRunnable = new Runnable() {
        @Override
        public void run() {
            String currentStateName;
            while (true) {
                while (!(getPausePressed())) {
                    if(textOutputCheckBox.isSelected()){
                        if(stateCircleList != null){
                            Platform.runLater(() -> removeStateCircles());
                        }
                        tapeOutputLabel.setVisible(false);;
                        codeOutputBox.setVisible(true);
                        try {
                            Thread.sleep(stepPauseTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        ArrayList<String> nextStep = executor.executeStep();
                        if(nextStep.get(0).equals("-1")){
                            currentStateName = "True";
                        }else if(nextStep.get(0).equals("-2")){
                            currentStateName = "False";
                        }else{
                            currentStateName = compiler.getStateList(tmCode).get(Integer.parseInt(nextStep.get(0)));
                        }
                        String stepOutput = ("State: " + currentStateName + "   " + "Tape: " + nextStep.get(2) + "\n");
                        Platform.runLater(() -> codeOutputBox.appendText(stepOutput));
                    }else{
                        if(stateCircleList != null){
                            Platform.runLater(() -> removeStateCircles());
                        }
                        codeOutputBox.setVisible(false);
                        tapeOutputLabel.setVisible(true);
                        ArrayList<String> nextStep = executor.executeStep();
                        Platform.runLater(() -> printStateCircles(Integer.parseInt(nextStep.get(0)))); 
                        Platform.runLater(() -> tapeOutputLabel.setText("Tape: " + nextStep.get(2)));
                        try {
                            Thread.sleep(stepPauseTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    };

    Thread outputThread = new Thread(outputThreadRunnable);

}