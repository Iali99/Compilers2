package cool;
import java.util.*;
public class Visitor{

	public void visit(AST.program p){
		// init classTable
		GlobalData.attrScopeTable = new ScopeTable();
		GlobalData.methodScopeTable = new ScopeTable();
		GlobalData.methodReturnTable = new HashMap<String, String>();
		GlobalData.classTable = new HashMap<String, String>();
		GlobalData.inheritanceGraph = new InheritanceGraph(p);
		GlobalData.inheritanceGraph.traverseGraph();
		updateMangledNames();
		GlobalData.inheritanceGraph.printGraph();
	}

	// For all the methods in all the classes
    private void updateMangledNames(){
        for(AST.class_ cl: GlobalData.inheritanceGraph.getClassList()){
            for(AST.feature f: cl.features){
                if(f instanceof AST.method){
                    AST.method m = (AST.method) f;
                    GlobalData.methodReturnTable.put(GlobalData.mangledNameWithClass(m), m.typeid);
                }
            }
        } 
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
				Semantic.reportError(GlobalData.filename, c.lineNo, "Main method missing in class main");
			}else if(hasArguments(mainMethod)){
				// error main has arguments
				Semantic.reportError(GlobalData.filename, c.lineNo, "arguments not allowed in main method");
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
				Semantic.reportError(GlobalData.filename, a.lineNo, "redefined attribute in same class : "+attr.name);
			}else{
				// error redefined globally
				Semantic.reportError(GlobalData.filename, a.lineNo, "redefined attribute exists globally : "+attr.name);
			}
			return;
		}
		if("self".equals(a.name)){
			// error self keyword cant be used
			Semantic.reportError(GlobalData.filename, a.lineNo, "name can not be self");
		}else if(!GlobalData.inheritanceGraph.containsClass(a.typeid)){
			// error typeid does not exits
			Semantic.reportError(GlobalData.filename, a.lineNo, "return type does not exist : "+a.typeid);
			// todo recover
			a.typeid = GlobalData.Const.ROOT_TYPE;
		}else{
			// attribute is valid
			visit(a.value);
			if(!(a.value instanceof AST.no_expr)){ // expression exists
				// check if return type of a is same is expression return type
				// keep in mind that return types can be instance of each other
				if(!GlobalData.inheritanceGraph.isConforming(a.typeid, a.value.type)){
					// error types not conforming
					Semantic.reportError(GlobalData.filename, a.lineNo, "return type not conforming with attribute value type : "+a.typeid);
					// recover
					a.typeid = GlobalData.Const.ROOT_TYPE;
				}
			}
		}
	}

	public void visit(AST.method m){
		// check for undefined types
		if(!GlobalData.InheritanceGraph.containsClass(m.typeid)){
			// error type not defined
			Semantic.reportError(GlobalData.filename, m.lineNo, "return type not defined : "+m.typeid);
			// recover
			m.typeid = GlobalData.Const.ROOT_TYPE;
		}
		String globalMangledName;
		// check if already defined locally
		if(GlobalData.methodScopeTable.lookUpLocal(m.name) != null){
			// error method redefined
			Semantic.reportError(GlobalData.filename, m.lineNo, "method can not be redefined : "+m.name);
			// recover
			return;
		}
		// check if the method exists globally
		else if(globalMangledName = GlobalData.methodScopeTable.lookUpGlobal(m.name) && globalMangledName != null){
			String thisTypeMangledName = mangledNameWithType(this);
			if(!thisTypeMangledName.equals(globalMangledName)){
				// error unmatched methods globally
				Semantic.reportError(GlobalData.filename, m.lineNo, "method inconsistent with parent declaration : "+m.name);
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
				Semantic.reportError(GlobalData.filename, m.lineNo, "self can not be a formal name : "+fl.name);				
			}else if(flist.contains(fl.name)){
				// error formal redefined
				Semantic.reportError(GlobalData.filename, fl.lineNo, "formal can not be redefined : "+fl.name);				
			}else{
				flist.add(fl.name);
			}
			visit(fl);
		}
		// visit method body
		visit(m.body);

		// check conformance of method's return type and body return type
		if(!GlobalData.inheritanceGraph.isConforming(m.typeid, m.body.type)){
			// error types not conforming
			Semantic.reportError(GlobalData.filename, m.lineNo, "return type not conforming with method body type : "+m.typeid);
			// recover
			m.typeid = GlobalData.Const.ROOT_TYPE;
		}
		// exit scope
		GlobalData.attrScopeTable.exitScope();

	}

	public void visit(AST.formal f){
		// check for undefined type
		if(!GlobalData.inheritanceGraph.containsClass(f.typeid)){
			// error undefined type
			Semantic.reportError(GlobalData.filename, f.lineNo, "type not defined : "+f.typeid);
			// recover
			f.typeid = GlobalData.Const.ROOT_TYPE;				
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
			Semantic.reportError(GlobalData.filename, e.lineNo, "expected a boolean expression : "+e.e1.name);				
		}
		e.type = GlobalData.Const.BOOL_TYPE;
	}

	public void visit(AST.eq e){
		visit(e.e1);
		visit(e.e2);
		if(!e.e1.type.equals(e.e2.type)){
			//error : The types of expressions to be checked are not same.
			Semantic.reportError(GlobalData.filename, e.lineNo, "type not matched");
		}
		e.type = GlobalData.Const.BOOL_TYPE;
	}

	public void visit(AST.leq e){
		visit(e.e1);
		visit(e.e2);
		if(!e.e1.type.equals(GlobalData.Const.INT_TYPE) || !e.e2.type.equals(GlobalData.Const.INT_TYPE)){
			//error : NON INT types. Operation cannot be done.
			Semantic.reportError(GlobalData.filename, e.lineNo, "expected type INT");
		}
		e.type = GlobalData.Const.BOOL_TYPE;
	}

	public void visit(AST.lt e){
		visit(e.e1);
		visit(e.e2);
		if(!e.e1.type.equals(GlobalData.Const.INT_TYPE) || !e.e2.type.equals(GlobalData.Const.INT_TYPE)){
			//error : NON INT types. Operation cannot be done.
			Semantic.reportError(GlobalData.filename, e.lineNo, "expected type INT");
		}
		e.type = GlobalData.Const.BOOL_TYPE;
	}

	public void visit(AST.divide e){
		visit(e.e1);
		visit(e.e2);
		if(!e.e1.type.equals(GlobalData.Const.INT_TYPE) || !e.e2.type.equals(GlobalData.Const.INT_TYPE)){
			//error : NON INT types. Operation cannot be done.
			Semantic.reportError(GlobalData.filename, e.lineNo, "expected type INT");
		}
		e.type = GlobalData.Const.BOOL_TYPE;
	}

	public void visit(AST.mul e){
		visit(e.e1);
		visit(e.e2);
		if(!e.e1.type.equals(GlobalData.Const.INT_TYPE) || !e.e2.type.equals(GlobalData.Const.INT_TYPE)){
			//error : NON INT types. Operation cannot be done.
			Semantic.reportError(GlobalData.filename, e.lineNo, "expected type INT");
		}
		e.type = GlobalData.Const.BOOL_TYPE;
	}

	public void visit(AST.sub e){
		visit(e.e1);
		visit(e.e2);
		if(!e.e1.type.equals(GlobalData.Const.INT_TYPE) || !e.e2.type.equals(GlobalData.Const.INT_TYPE)){
			//error : NON INT types. Operation cannot be done.
			Semantic.reportError(GlobalData.filename, e.lineNo, "expected type INT");
		}
		e.type = GlobalData.Const.BOOL_TYPE;
	}

	public void visit(AST.plus e){
		visit(e.e1);
		visit(e.e2);
		if(!e.e1.type.equals(GlobalData.Const.INT_TYPE) || !e.e2.type.equals(GlobalData.Const.INT_TYPE)){
			//error : NON INT types. Operation cannot be done.
			Semantic.reportError(GlobalData.filename, e.lineNo, "expected type INT");
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
			Semantic.reportError(GlobalData.filename, e.lineNo, "Type not defined : "+e.typeid);
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
				Semantic.reportError(GlobalData.filename, e.lineNo, "attribute not declared : "+e.name);
			}
			else{
				//error : Types do not match for assignment
				Semantic.reportError(GlobalData.filename, e.lineNo, "types not matched : "+e.name);
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
			Semantic.reportError(GlobalData.filename, e.lineNo, "predicate is not of type boolean");
		}
		e.type = GlobalData.Const.ROOT_TYPE;
	}

	public void visit(AST.cond e){
		visit(e.predicate);
		visit(e.ifbody);
		visit(e.elsebody);
		if(!e.predicate.type.equals(GlobalData.Const.BOOL_TYPE)){
			//error: Predicate is not of type bool.
			Semantic.reportError(GlobalData.filename, e.lineNo, "predicate is not of type boolean");
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
			Semantic.reportError(GlobalData.filename, e.lineNo, "Type not defined : "+e.typeid);
			GlobalData.attrScopeTable.insert(e.name,GlobalData.Const.BOOL_TYPE);
		}
		else{
			GlobalData.attrScopeTable.insert(e.name,e.typeid);
		}
		if(!(e.value instanceof AST.no_expr)){
			if(!GlobalData.inheritanceGraph.isConforming(e.typeid,e.value.type)){
				//error : types not conforming for assignment.
				Semantic.reportError(GlobalData.filename, e.lineNo, "types not conforming"+e.typeid);
			}
		}
		e.type = e.body.type;
		GlobalData.attrScopeTable.exitScope();
	}

	public void visit(AST.dispatch e){
		visit(e.caller);
		for(AST.expression e1 : e.actuals){
			visit(e1);
		}
		String class = e.caller.type;
		String mangledName = GlobalData.mangledNameWithExpr(class,e.name,e.actuals);
		String type = GlobalData.methodReturnTable.get(mangledName);

		while(class != null){
			class = GlobalData.classTable.get(class)
			mangledName = GlobalData.mangledNameWithExpr(class,e.name,e.actuals);
			type = GlobalData.methodReturnTable.get(mangledName);
			if(type != null)
				break;
		}
		if(class == null){
			//error : method not defined.
			Semantic.reportError(GlobalData.filename, e.lineNo, "method not defined : "+e.name);
			e.type = GlobalData.Const.ROOT_TYPE;
		}
		else{
			e.type = type;
		}
	}

	public void visit(AST.static_dispatch e){
		visit(e.caller);
		for(AST.expression e1: e.actuals){
			visit(e1);
		}
		Stirng class = e.caller.type;
		if(!GlobalData.inheritanceGraph.containsClass(e.typeid)){
			//error : Type not defined.
			Semantic.reportError(GlobalData.filename, e.lineNo, "type not defined : "+e.typeid);
			e.typeid = GlobalData.Const.ROOT_TYPE;
			e.type = GlobalData.Const.ROOT_TYPE;
		}
		else if(!GlobalData.inheritanceGraph.isConforming(e.typeid,class)){
			//error: types not conforming.
			Semantic.reportError(GlobalData.filename, e.lineNo, "types not conforming : "+e.typeid);
			e.type = GlobalData.Const.ROOT_TYPE;
		}
		else{
			String mangledName = GlobalData.mangledNameWithExpr(class,e.name,e.actuals);
			String type = GlobalData.methodReturnTable.get(mangledName);
			if(type == null){
				//error : method not defined.
				Semantic.reportError(GlobalData.filename, e.lineNo, "method not defined : "+e.name);
				e.type = GlobalData.Const.ROOT_TYPE;
			}
			else{
				e.type = type;
			}
		}
	}

	public void visit(AST.typcase e){
		visit(e.predicate);
		visit(e.branches.get(0));
		e.type = e.branches.get(0).value.type;
		for(int i =1;i<e.branches.size();i++){
			visit(e.branches.get(i));
			e.type = GlobalData.inheritanceGraph.getSuperClass(e.type,e.branches.get(i));
		}
	}

	public void visit(AST.branch e){
		GlobalData.attrScopeTable.enterScope();

		if("self".equals(e.name)){
			//error : name cannot be self.
			Semantic.reportError(GlobalData.filename, e.lineNo, "name cannot be self : "+e.name);
		}
		else{
			if(!GlobalData.inheritanceGraph.containsClass(e.type)){
				//error : Type does not exist.
				Semantic.reportError(GlobalData.filename, e.lineNo, "types does not exist : "+e.typeid);
				e.type = GlobalData.Const.ROOT_TYPE;
			}
			GlobalData.attrScopeTable.insert(e.name,e.type);
		}
		visit(e.value);
		GlobalData.attrScopeTable.exitScope();
	}

	private boolean isStdType(AST.expression e){
		if(e.type.equals(GlobalData.Const.INT_TYPE) || e.type.equals(GlobalData.Const.BOOL_TYPE) || e.type.equals(GlobalData.Const.STRING_TYPE) )
			return true;
		return false;
	}

}
