package cool;
import java.util.*;
public class Visitor{

	public void visitProgram(AST.program p){
		List<AST.class_> classes = p.classes;

		// init classTable
		GlobalData.classTable = new HashMap<String, String>();
		GlobalData.inheritanceGraph = new InheritanceGraph(p);

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
						GlobalData.GiveError("cycles detected", iter.lineNo);
						return;
					}
					grandparent = GlobalData.classTable.get(grandparent);
				}
				GlobalData.classTable.put(iter.name, parent);
			}
		}
		GlobalData.inheritanceGraph.setChildren();
		GlobalData.inheritanceGraph.printGraph();
	}
}
