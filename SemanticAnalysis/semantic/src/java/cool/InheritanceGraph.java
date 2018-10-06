package cool;

public class InheritanceGraph implements InheritanceGraphInterface{

  public HashMap<String, AST.Class_> inheritanceGraph = new HashMap<String, AST.Class_>();

  //constructor to create inheritance graph.
  public InheritanceGraph(AST.program p){
    for(AST.class_ iter : p.classes){
      insert(iter);
    }
  }

  //insert a class in the inheritance graph.
  public void insert(AST.class_ c){
     inheritanceGraph.put(c.name,c);
  }

  //sets the children of all the classes in the graph.
  public void setChildren(){
    //iterates over the classes of the class table.
    Iterator it = GlobalData.classTable.entrySet().iterator();
    while(it.hasNext()){
      HashMap.Entry pair = (HashMap.Entry)it.next();
      if(pair.getValue() != "Object"){
        //parentClass gets the parent class from the inheritance graph.
        AST.class_ parentClass = inheritanceGraph.get(pair.getValue());
        //The current iterating class is added as child to its parent class.
        parentClass.AddChild(pair.getKey());
        //Parent class is updated in the inheritance graph.
        inheritanceGraph.put(pair.getValue(),parentClass);
      }
      it.remove();
    }
  }
}
