package cool;
import java.util.*;
public class InheritanceGraph implements InheritanceGraphInterface{

  public HashMap<String, AST.class_> graph = new HashMap<String, AST.class_>();

  //constructor to create inheritance graph.
  public InheritanceGraph(AST.program p){
    for(AST.class_ iter : p.classes){
      insert(iter);
    }
  }

  //insert a class in the inheritance graph.
  public void insert(AST.class_ c){
     graph.put(c.name,c);
  }

  //sets the children of all the classes in the graph.
  public void setChildren(){
    //iterates over the classes of the class table.
    Iterator it = GlobalData.classTable.entrySet().iterator();
    while(it.hasNext()){
      HashMap.Entry pair = (HashMap.Entry)it.next();
      if(pair.getValue() != "Object"){
        //parentClass gets the parent class from the inheritance graph.
        AST.class_ parentClass = graph.get(pair.getValue());
        //The current iterating class is added as child to its parent class.
        parentClass.AddChild((String)pair.getKey());
        //Parent class is updated in the inheritance graph.
        graph.put((String)pair.getValue(),parentClass);
      }
      it.remove();
    }
  }

  public void printGraph()
  {
    Iterator it = graph.entrySet().iterator();
    while(it.hasNext())
    {
      HashMap.Entry pair = (HashMap.Entry)it.next();
      AST.class_ c = (AST.class_)pair.getValue();
      System.out.println("Class : " + (String)pair.getKey());
      System.out.println("Parent : " + c.parent + "\nChildren : ");
      printList(c.children);
      System.out.println(" ------ Next ------");
    }
  }
  void printList(List<String> l)
  {
    for(int i = 0; i < l.size(); i++) {
            System.out.println(l.get(i));
        }
  }
}
