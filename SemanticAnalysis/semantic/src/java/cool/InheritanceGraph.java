
package cool;
import java.util.*;
public class InheritanceGraph implements InheritanceGraphInterface{

  private HashMap<String, AST.class_> graph = new HashMap<String, AST.class_>();
  private AST.program p;

  //constructor to create inheritance graph.
  public InheritanceGraph(AST.program pr){
    p = pr;
    insert(GlobalData.ROOT_CLASS);
    insert(GlobalData.IO_CLASS);
    insert(GlobalData.BOOL_CLASS);
    insert(GlobalData.INT_CLASS);
    insert(GlobalData.STRING_CLASS);
    for(AST.class_ iter : p.classes){
      insert(iter);
    }
    GlobalData.classTable.put(GlobalData.Const.IO_TYPE, GlobalData.Const.ROOT_TYPE);
    GlobalData.classTable.put(GlobalData.Const.INT_TYPE, GlobalData.Const.ROOT_TYPE);
    GlobalData.classTable.put(GlobalData.Const.BOOL_TYPE, GlobalData.Const.ROOT_TYPE);
    GlobalData.classTable.put(GlobalData.Const.STRING_TYPE, GlobalData.Const.ROOT_TYPE);
    checkGraph();
    setChildren();
  }

  public AST.class_ getRootClass(){
    return graph.get(GlobalData.Const.ROOT_TYPE);
  }

  public ArrayList<AST.class_> getClassList(){
    ArrayList<AST.class_> cl_list = new ArrayList<AST.class_>();
    // adding all standard classes as well
    cl_list.add(GlobalData.ROOT_CLASS);
    cl_list.add(GlobalData.STRING_CLASS);
    cl_list.add(GlobalData.BOOL_CLASS);
    cl_list.add(GlobalData.INT_CLASS);
    cl_list.add(GlobalData.IO_CLASS);
    // adding remaining classes
    Iterator it = GlobalData.classTable.entrySet().iterator();
    while(it.hasNext()){
      HashMap.Entry pair = (HashMap.Entry)it.next();
      cl_list.add(graph.get(pair.getKey()));
    }
    // for(String it : GlobalData.classTable){
    //   cl_list.add(graph.get(it));
    // }
    return cl_list;
  }

  public void traverseGraph(AST.class_ node){
    // visit current node
    Visitor v = new Visitor();
    v.visit(node);

    // recursively traverse every child
    for(String it : node.children){
      // enter scope
      GlobalData.attrScopeTable.enterScope();
      GlobalData.methodScopeTable.enterScope();

      traverseGraph(graph.get(it));
      // exit scope
      GlobalData.attrScopeTable.exitScope();
      GlobalData.methodScopeTable.exitScope();
    }

  }

  public void checkGraph(){
    List<AST.class_> classes = p.classes;

    // go to each class and add it to classTable
    for(AST.class_ iter : classes){
      if(GlobalData.Const.is_standard_class_name(iter.name)){
        Semantic.reportError(GlobalData.filename, iter.lineNo, "standard class cannot be redefined : "+iter.name);
      }
      else if(GlobalData.classTable.containsKey(iter.name)){
        Semantic.reportError(GlobalData.filename, iter.lineNo, "class redefined : " + iter.name);
      }
      else
        GlobalData.classTable.put(iter.name, GlobalData.Const.ROOT_TYPE);
    }

    // iterate over each class and check for cycles
    for(AST.class_ iter : classes){
      String parent = iter.parent;

      if(parent.equals(GlobalData.Const.ROOT_TYPE)){
        continue;
      }
      // check existence of parent
      if(!GlobalData.classTable.containsKey(parent)){
        Semantic.reportError(GlobalData.filename, iter.lineNo, "class does not exist : " + parent);
        // if parent is not inheritable is_standard then recover
        if(!GlobalData.Const.is_inheritable_standard(parent)){
          // recover
          iter.parent = GlobalData.Const.ROOT_TYPE;
          GlobalData.classTable.put(iter.name, GlobalData.Const.ROOT_TYPE);
        }
      }else if(!GlobalData.Const.is_inheritable(parent)){
        // error standard class as parent
        Semantic.reportError(GlobalData.filename, iter.lineNo, "standard class can't be inherited : " + parent);
        iter.parent = GlobalData.Const.ROOT_TYPE;
        GlobalData.classTable.put(iter.name, GlobalData.Const.ROOT_TYPE);
      }
      else{
        // check for loops
        String grandparent = GlobalData.classTable.get(parent);
        while(!grandparent.equals(GlobalData.Const.ROOT_TYPE)){
          // check for cycles
          if(grandparent.equals(iter.name)){
            Semantic.reportError(GlobalData.filename, iter.lineNo, "cycle detected : "+iter.name);
            System.exit(1);
          }
          grandparent = GlobalData.classTable.get(grandparent);
        }
        GlobalData.classTable.put(iter.name, parent);
      }
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

  // returns super class of cl1 and cl2
  public String getSuperClass(String cl1, String cl2){
    if(isConforming(cl1, cl2)) return cl1;
    else if(isConforming(cl2, cl1)) return cl2;
    else {
      String parent = GlobalData.classTable.get(cl1);
      while(!(isConforming(parent,cl2))){
        if(parent.equals(GlobalData.Const.ROOT_TYPE))
          return GlobalData.Const.ROOT_TYPE;
        parent = GlobalData.classTable.get(parent);
      }
      return parent;
    }
  }

  // checks if cl can be returned for super_cl
  public boolean isConforming(String super_cl, String cl){
    // if(cl==null || super_cl==null) return false;
    // if super_cl and cl are same or super_cl is root type
    if(cl.equals(super_cl) || super_cl.equals(GlobalData.Const.ROOT_TYPE)) return true;
    if(cl.equals(GlobalData.Const.ROOT_TYPE)) return false;
    // if super_cl or cl is standard type
    if(GlobalData.Const.is_standard(super_cl) || GlobalData.Const.is_standard(cl)) return false;
    String parent = GlobalData.classTable.get(cl);
    // if(parent == null)
    //   System.out.println("parent becoming null for class : " + cl);
    while(!parent.equals(GlobalData.Const.ROOT_TYPE)){
      if(parent.equals(super_cl)) return true;
      parent = GlobalData.classTable.get(parent);
    }
    return false;
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

  protected void printList(List<String> l)
  {
    for(int i = 0; i < l.size(); i++) {
            System.out.println(l.get(i));
        }
  }

}
