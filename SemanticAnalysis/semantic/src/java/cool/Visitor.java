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

	public void visit(AST.no_expr e){
		e.type = "_no_type";
	}

	public void visit(AST.bool_const e){
		e.type = GlobalData.Const.BOOL_TYPE;
	}

	public void visit(AST.string_const e){
		e.type = GlobalData.Const.STRING_TYPE;
	}

	public void visit(AST.int_const e){
		e.type = GlobalData.Const.INT_TYPE;
	}

	public void visit(AST.comp e){
		visit(e.e1);
		if(!(e.e1.type).equals(GlobalData.Const.BOOL_TYPE)){
			//error : NOT a boolean type expression
		}
		e.type = GlobalData.Const.BOOL_TYPE;
	}

	public void visit(AST.eq e){
		visit(e.e1);
		visit(e.e2);
		if(!e.e1.type.equals(e.e2.type)){
			//error : The types of expressions to be checked are not same.
		}
		e.type = GlobalData.Const.BOOL_TYPE;
	}

	public void visit(AST.leq e){
		visit(e.e1);
		visit(e.e2);
		if(!e.e1.type.equals(GlobalData.Const.INT_TYPE) || !e.e2.type.equals(GlobalData.Const.INT_TYPE)){
			//error : NON INT types. Operation cannot be done.
		}
		e.type = GlobalData.Const.BOOL_TYPE;
	}

	public void visit(AST.lt e){
		visit(e.e1);
		visit(e.e2);
		if(!e.e1.type.equals(GlobalData.Const.INT_TYPE) || !e.e2.type.equals(GlobalData.Const.INT_TYPE)){
			//error : NON INT types. Operation cannot be done.
		}
		e.type = GlobalData.Const.BOOL_TYPE;
	}

	public void visit(AST.divide e){
		visit(e.e1);
		visit(e.e2);
		if(!e.e1.type.equals(GlobalData.Const.INT_TYPE) || !e.e2.type.equals(GlobalData.Const.INT_TYPE)){
			//error : NON INT types. Operation cannot be done.
		}
		e.type = GlobalData.Const.BOOL_TYPE;
	}

	public void visit(AST.mul e){
		visit(e.e1);
		visit(e.e2);
		if(!e.e1.type.equals(GlobalData.Const.INT_TYPE) || !e.e2.type.equals(GlobalData.Const.INT_TYPE)){
			//error : NON INT types. Operation cannot be done.
		}
		e.type = GlobalData.Const.BOOL_TYPE;
	}

	public void visit(AST.sub e){
		visit(e.e1);
		visit(e.e2);
		if(!e.e1.type.equals(GlobalData.Const.INT_TYPE) || !e.e2.type.equals(GlobalData.Const.INT_TYPE)){
			//error : NON INT types. Operation cannot be done.
		}
		e.type = GlobalData.Const.BOOL_TYPE;
	}

	public void visit(AST.plus e){
		visit(e.e1);
		visit(e.e2);
		if(!e.e1.type.equals(GlobalData.Const.INT_TYPE) || !e.e2.type.equals(GlobalData.Const.INT_TYPE)){
			//error : NON INT types. Operation cannot be done.
		}
		e.type = GlobalData.Const.BOOL_TYPE;
	}

	public void visit(AST.isvoid e){
		visit(e.e1);
		e.e1.type = GlobalData.Const.BOOL_TYPE;
	}

	public void visit(AST.new_ e){
		if(!GlobalData.inheritanceGraph.containsClass(e.typeid)){
			//error : Type not defined.
			e.type = GlobalData.Const.ROOT_TYPE;
		}
		else{
			e.type = e.typeid;
		}
	}

	public void visit(AST.assign e){
		visit(e.e1);
		if(!(GlobalData.attrScopeTable.lookUpGlobal(e.name)).equals(e.e1.type)){
			if(GlobalData.attrScopeTable.lookUpGlobal(e.name) == null){
				//error : attr not declared.
			}
			else{
				//error : Types do not match for assignment
			}
		}
		e.type = e.e1.type;
	}

	public void visit(AST.block e){
		for(AST.expression e1 : e.l1){
			visit(e1);
		}
		e.type = e.l1.get(e.l1.size()-1).type;
	}

	public void vist(AST.loop e){
		visit(e.predicate);
		visit(e.body);
		if(!e.predicate.type.equals(GlobalData.Const.BOOL_TYPE)){
			//error: Predicate is not of type bool.
		}
		e.type = GlobalData.Const.ROOT_TYPE;
	}

	public void visit(AST.cond e){
		visit(e.predicate);
		visit(e.ifbody);
		visit(e.elsebody);
		if(!e.predicate.type.equals(GlobalData.Const.BOOL_TYPE)){
			//error: Predicate is not of type bool.
		}
		if(e.ifbody.type.equals(e.elsebody.type)){
			e.type = e.ifbody.type;
		}
		else{
			e.type = GlobalData.Const.ROOT_TYPE;
		}
	}

	public void visit(AST.let e){
		visit(e.value);
		visit(e.body);
		GlobalData.attrScopeTable.enterScope();
		if(!GlobalData.inheritanceGraph.containsClass(e.typeid)){
			//error : Type not defined.
			GlobalData.attrScopeTable.insert(e.name,GlobalData.Const.BOOL_TYPE);
		}
		else{
			GlobalData.attrScopeTable.insert(e.name,e.typeid);
		}
		if(!(e.value instanceof AST.no_expr)){
			if(!GlobalData.inheritanceGraph.isConforming(e.typeid,e.value.type)){
				//error : types not conforming for assignment.
			}
		}
		e.type = e.body.type;
		GlobalData.attrScopeTable.exitScope();
	}

	private boolean isStdType(AST.expression e){
		if(e.type.equals(GlobalData.Const.INT_TYPE) || e.type.equals(GlobalData.Const.BOOL_TYPE) || e.type.equals(GlobalData.Const.STRING_TYPE) )
			return true;
		return false;
	}

}
