package cool;
import java.util.*;
public class InheritanceGraph implements InheritanceGraphInterface{

  public HashMap<String, AST.class_> graph = new HashMap<String, AST.class_>();
  private AST.program p;

  //constructor to create inheritance graph.
  public InheritanceGraph(AST.program pr){
    p = pr;
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

  public void traverseGraph(AST.class_ node){
    visit(node);
    for(String it : node.children){
      traverseGraph(graph.get(it));
    }
  }

  public void checkGraph(){
    List<AST.class_> classes = p.classes;

		// go to each class and add it to classTable
		for(AST.class_ iter : classes){
			if(GlobalData.classTable.containsKey(iter.name)){
				GlobalData.GiveError("class redefined : " + iter.name, iter.lineNo);
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
				GlobalData.GiveError("class does not exist : " + parent, iter.lineNo);
			}
			 else{
				// check for loops
				String grandparent = GlobalData.classTable.get(parent);
				while(!grandparent.equals(GlobalData.Const.ROOT_TYPE)){
					// check for cycles
					if(grandparent.equals(iter.name)){
						GlobalData.GiveError("cycle detected", iter.lineNo);
						System.exit(1);
					}
					grandparent = GlobalData.classTable.get(grandparent);
				}
				GlobalData.classTable.put(iter.name, parent);
			}
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
      if(GlobalData.Const.is_standard()) continue;
      HashMap.Entry pair = (HashMap.Entry)it.next();
      if(!pair.getValue().equals(GlobalData.Const.ROOT_TYPE)){
        //parentClass gets the parent class from the inheritance graph.
        AST.class_ parentClass = graph.get(pair.getValue());
        //The current iterating class is added as child to its parent class.
        parentClass.AddChild((String)pair.getKey());
        //Parent class is updated in the inheritance graph.
        graph.put((String)pair.getValue(),parentClass);
      }
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
  
  protected void printList(List<String> l)
  {
    for(int i = 0; i < l.size(); i++) {
            System.out.println(l.get(i));
        }
  }
}
