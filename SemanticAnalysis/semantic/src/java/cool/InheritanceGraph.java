package cool;

public class InheritanceGraph implements InheritanceGraphInterface{

  public HashMap<String, AST.Class_> inheritanceGraph = new HashMap<String, AST.Class_>();

  public void insert(AST.Class_ c){
     if(GlobalData.classTable.containsKey(c.parent))
     {
       inheritanceGraph.put(c.name,c);
     }
     else
     {
       //give error that parent does not exits.
     }
  }
  public void setChildren(){
    Iterator it = GlobalData.classTable.entrySet().iterator();
    while(it.hasNext()){
      HashMap.Entry pair = (HashMap.Entry)it.next();
      if(pair.getValue() != "Object")
      {
        AST.Class_ parentClass = inheritanceGraph.get(pair.getKey());
        parentClass.AddChild(pair.getKey());
        inheritanceGraph.put(pair.getKey(),parentClass);
      }
      it.remove();
    }
  }
}
