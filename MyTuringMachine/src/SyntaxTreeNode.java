import java.util.ArrayList;

public class SyntaxTreeNode {

    //The value of the node
    private final String value;
    //Reference to the nodes parent node
    private SyntaxTreeNode parentNode;
    //List of all the nodes child nodes 
    private ArrayList<SyntaxTreeNode> childNodes = new ArrayList<>();


    //Contructor for root node
    public SyntaxTreeNode(String value){
        this.value = value;
        parentNode = null;
    }

    //constructor for nodes and leaf nodes
    public SyntaxTreeNode (String value, SyntaxTreeNode parentNode){
        this.value = value;
        this.parentNode = parentNode;
        parentNode.addChildNode(this);
    }

    /*
     * adds a new node to the child nodes
     * side effects - adds a new value to childNodes list
     * input - SyntaxTreeNode, the new node you'd like to add to the list of child nodes
     */
    public void addChildNode(SyntaxTreeNode newChild){
        childNodes.add(newChild);
        newChild.setParentNode(this);
    }

    /*
     * Adds a new child node for this node
     * side effects - adds a new value to childNodes list
     * input - value of new child node
     * output - non
     */
    public void addChildNode(String newNodeValue){
        SyntaxTreeNode newChildNode = new SyntaxTreeNode(newNodeValue);
        newChildNode.setParentNode(this);
        childNodes.add(newChildNode);
    }

    private void setParentNode(SyntaxTreeNode parentNode){
        this.parentNode = parentNode;
    }

    /*
     * Returns the list of child nodes for this node.
     * side effects - non
     * input - Non
     * output - ArrayList<SyntaxTreeNode>, list of all the child nodes for this node
     */
    public ArrayList<SyntaxTreeNode> getAllChildNodes(){
        return(childNodes);
    }

    public SyntaxTreeNode getChildNode(int i){
        SyntaxTreeNode childNode = childNodes.get(i);
        return(childNode);
    }

    /*
     * Returns the parent node for this node
     * side effects - non
     * input - non
     * output - SyntaxTreeNode, the parent node for this node
     */
    public SyntaxTreeNode getParentNode(){
        return(parentNode);
    }

    /*
     * Retruns the value of this node 
     * side effects - non
     * input - non
     * output - String, the value of this node
     */
    public String getValue(){
        return(value);
    }

    public boolean hasChild(){
        return(this.childNodes.size() != 0);
    }

    public String toString(){
        String treeAsString = this.getValue() + ",";
        for(int i = 0; i < childNodes.size(); i++){
            treeAsString = treeAsString + childNodes.get(i).toString();
        }
        return(treeAsString);
    }

}

