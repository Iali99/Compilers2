package cool;
import java.util.*;
public class InheritanceGraph{

  private HashMap<String, AST.class_> graph = new HashMap<String, AST.class_>();
  private AST.program p;

  //constructor to create inheritance graph.
  public InheritanceGraph(AST.program pr){
    p = pr;
    // TODO: insert default classes
    insert(GlobalData.ROOT_CLASS);
    insert(GlobalData.IO_CLASS);
    insert(GlobalData.BOOL_CLASS);
    insert(GlobalData.INT_CLASS);
    insert(GlobalData.STRING_CLASS);
    for(AST.class_ iter : p.classes){
      insert(iter);
    }
    // TODO: insert default classes to classTable
    GlobalData.classTable.put(GlobalData.Const.IO_TYPE, GlobalData.Const.ROOT_TYPE);
    GlobalData.classTable.put(GlobalData.Const.INT_TYPE, GlobalData.Const.ROOT_TYPE);
    GlobalData.classTable.put(GlobalData.Const.BOOL_TYPE, GlobalData.Const.ROOT_TYPE);
    GlobalData.classTable.put(GlobalData.Const.STRING_TYPE, GlobalData.Const.ROOT_TYPE);
    makeClassTable();
    setChildren();
  }

  public void makeClassTable(){
    List<AST.class_> classes = p.classes;

    // go to each class and add it to classTable
    for(AST.class_ iter : classes){
      GlobalData.classTable.put(iter.name, GlobalData.Const.ROOT_TYPE);
    }

    // iterate over each class and check for cycles
    for(AST.class_ iter : classes){
      String parent = iter.parent;
      if(parent.equals(GlobalData.Const.ROOT_TYPE)){
        continue;
      }
      // check existence of parent
      // check for loops
      GlobalData.classTable.put(iter.name, parent);
    }
  }

  //function to check if a class exists in the inheritance graph.
  public boolean containsClass(String classname)
  {
    return graph.containsKey(classname);
  }

  //insert a class in the inheritance graph.
  public void insert(AST.class_ c){
    GlobalData.filename = c.filename;
     graph.put(c.name,c);
  }

  //sets the children of all the classes in the graph.
  public void setChildren(){
    //iterates over the classes of the class table.
    Iterator it = GlobalData.classTable.entrySet().iterator();
    while(it.hasNext()){
      HashMap.Entry pair = (HashMap.Entry)it.next();
      // if(!pair.getValue().equals(GlobalData.Const.ROOT_TYPE)){
        //parentClass gets the parent class from the inheritance graph.
        AST.class_ parentClass = graph.get(pair.getValue());
        //The current iterating class is added as child to its parent class.
        parentClass.AddChild((String)pair.getKey());
        //Parent class is updated in the inheritance graph.
        graph.put((String)pair.getValue(),parentClass);
      // }
    }
  }
}