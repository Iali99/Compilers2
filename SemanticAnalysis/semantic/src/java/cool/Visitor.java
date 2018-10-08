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
		// visiting all its features for semantics
        for(AST.feature f: c.features){
            if(f instanceof AST.attr){ // Its an attribute
                AST.attr a = (AST.attr) f;
                visit(a);
            }else{ // Its a method
                AST.method m = (AST.method) f;
                visit(m);
            }
        }
		// if this is main class
		if(c.name.equals(GlobalData.Const.MAIN_TYPE)){
			// main class
			String mainMethod = GlobalData.methodScopeTable.lookUpLocal(c.name);
			if(mainMethod==null){
				// error main method missing
			}else if(hasArguments(mainMethod)){ 
				// error main has arguments
			}
		}

		// visitor for class
		if(GlobalData.is_standard(AST.class_.name)){
			return;
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
		}else if(!GlobalData.inheritanceGraph.containsClass(a.typeid)){
			// error typeid does not exits
			// todo recover
		}else{
			// attribute is valid
			visit(a.value);
			if(!(a.value instanceof AST.no_expr)){ // expression exists
				// check if return type of a is same is expression return type
				// keep in mind that return types can be instance of each other
			}
		}
	}

	public void visit(AST.method m){
		// check for undefined types
		if(!GlobalData.InheritanceGraph.containsClass(m.typeid)){
			// error type not defined
			// recover
		}
		String globalMangledName;
		// check if already defined locally
		if(GlobalData.methodScopeTable.lookUpLocal(m.name) != null){
			// error class redefined 
		}
		// check if the method exists globally
		else if(globalMangledName = GlobalData.methodScopeTable.lookUpGlobal(m.name) && globalMangledName != null){
			String thisTypeMangledName = mangledNameWithType(this);
			if(!thisTypeMangledName.equals(globalMangledName)){
				// error unmatched methods globally
			}
			GlobalData.methodScopeTable.insert(m.name, thisTypeMangledName);
		}
		// visit method internally
		// enter scope
		GlobalData.attrScopeTable.enterScope();

		// loop through formal list
		set<String> flist = new HashSet<>;
		for(AST.formal fl : m.formals){
			if(fl.name.equals("self")){
				// error self cant be name
			}else if(flist.contains(fl.name)){
				// error formal redefined
			}else{
				flist.add(fl.name);
			}
			visit(fl);
		}
		// visit method body
		visit(m);

		// check conformance of method's return type and body return type

		// exit scope
		GlobalData.attrScopeTable.exitScope();

	}

	public void visit(AST.formal f){
		// check for undefined type
		if(GlobalData.inheritanceGraph.containsClass(f.typeid)){
			// error undefined type
		}
		else{
			GlobalData.attrScopeTable.insert(f.name, f.typeid);
		}
	}

}
