import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;


public class StateCircleDrawer {
    private static int xBound = 900;
    private static int yBound = 300;
    private static int rightCornerXCoord = 335;
    private static int rightCornerYCoord = 450;
    private static String labelTextColor = "#ffffff";
    private static String trueStateName = "True";
    private static String falseStateName = "False";
    private static int stateListMod = 2;

    public StateCircleDrawer(){}

    private int[] calculateCircleArrangement(int numCircles){
        int xCircles = 1;
        int yCircles = 1;
        int totalCircles = 1;
        int boxRatio = xBound/yBound;
        int circleRatio;
        while(totalCircles < numCircles){
            circleRatio = xCircles/yCircles;
            if((circleRatio / 2) < boxRatio){
                ++ xCircles;
            }else{
                ++ yCircles;
            }
            totalCircles = xCircles * yCircles;
        }
        int[] circleGridDimensions = new int[]{xCircles, yCircles};
        return(circleGridDimensions);
    }
    
    
    public ArrayList<StackPane> calculateStateCircles(ArrayList<String> stateList, int currentState){
        ArrayList<StackPane> labeledStateCircleList = new ArrayList<StackPane>();
        stateList.add(0, falseStateName);
        stateList.add(1, trueStateName);
        currentState = currentState + stateListMod;
        int[] circleGridDimensions = calculateCircleArrangement(stateList.size());
        int xCircles = circleGridDimensions[0];
        int yCircles = circleGridDimensions[1];
        ArrayList<Integer> circleSizeLimits = new ArrayList<Integer>(Arrays.asList(xBound/xCircles, yBound/yCircles));
        int circleDiametre = Collections.min(circleSizeLimits);
        int circleRadius = circleDiametre / 2;
        int xCentreCoord = rightCornerXCoord;
        int yCentreCoord = rightCornerYCoord;
        Circle circle = new Circle(xCentreCoord, yCentreCoord, circleRadius);
        circle.setStroke(Color.web("#3d3d3d"));
        circle.setStrokeWidth(circleDiametre/100);
        if(0 == currentState){
            circle.setFill(Color.web("#3d3d3d"));
            circle.setStroke(Color.web("#3981f5"));
        }
        Label stateLabel = new Label(stateList.get(0));
        stateLabel.setTextFill(Color.web(labelTextColor));
        StackPane labeledCircle = new StackPane();
        labeledCircle.getChildren().addAll(circle, stateLabel);
        labeledCircle.setLayoutX(xCentreCoord);
        labeledCircle.setLayoutY(yCentreCoord);
        labeledStateCircleList.add(labeledCircle);
        for(int i = 1; i < stateList.size(); i++){            
            if((i % xCircles) == 0 ){
                yCentreCoord = yCentreCoord + circleDiametre;
                xCentreCoord = rightCornerXCoord - circleDiametre;
            }
            xCentreCoord = xCentreCoord + circleDiametre;
            circle = new Circle(xCentreCoord, yCentreCoord, circleRadius);
            circle.setStroke(Color.web("#3d3d3d"));
            circle.setStrokeWidth(circleDiametre/100);
            if(i == currentState){
                circle.setFill(Color.web("#3d3d3d"));
                circle.setStroke(Color.web("#3981f5"));
            }
            stateLabel = new Label(stateList.get(i));
            stateLabel.setTextFill(Color.web(labelTextColor));
            labeledCircle = new StackPane();
            labeledCircle.getChildren().addAll(circle, stateLabel);
            labeledCircle.setLayoutX(xCentreCoord);
            labeledCircle.setLayoutY(yCentreCoord);
            labeledStateCircleList.add(labeledCircle);
       }
       return(labeledStateCircleList);
    }
}
