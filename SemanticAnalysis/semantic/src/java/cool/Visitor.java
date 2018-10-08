package cool;
import java.util.*;
public class Visitor{

	public void visit(AST.program p){
		// init classTable
		GlobalData.attrScopeTable = new ScopeTable();
		GlobalData.methodScopeTable = new ScopeTable();
		GlobalData.classTable = new HashMap<String, String>();
		GlobalData.inheritanceGraph = new InheritanceGraph(p);
		GlobalData.inheritanceGraph.traverseGraph();
		GlobalData.inheritanceGraph.printGraph();
	}

	public void visit(AST.class_ c){

        Global.currentClass = c.name;

		// checking all its features for semantics
        for(AST.feature f: c.features){
            if(f instanceof AST.attr){ // Its a variable
                AST.attr a = (AST.attr) f;
                visit(a);
            }else{ // Its a method
                AST.method m = (AST.method) f;
                visit(m);
            }
        }

		// visitor for class
		if(GlobalData.is_standard(AST.class_.name)){
			return;
		}

		// visiting all features
        for(AST.feature f: c.features){
            visit(f);
        }
	}

	public void visit(AST.attr a){
		// not available globally
		if(GlobalData.attrScopeTable.lookUpGlobal(a.name) == null){
			GlobalData.attrScopeTable.insert(a.name, a.typeid);
		}else{
			// re defined
			if(GlobalData.attrScopeTable.lookUpLocal(a.name) != null){
				//error redefined attribute same class
			}else{
				// error redefined globally
			}
		}
		if("self".equals(a.name)){
			// error self keyword cant be used
		} else if(!GlobalData.inheritanceGraph.con(a.typeid)){
			// error 
		}
		if(GlobalData.inheritanceGraph(a.typeid))
		

	}
}
