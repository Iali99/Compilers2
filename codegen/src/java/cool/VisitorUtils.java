package cool;

public class VisitorUtils{
	// adds structs for all classes staring from root in dfs fashion
	public static void addStructsAllClasses(){
		GlobalData.out.println();
        GlobalData.out.println("; Struct declarations");
        GlobalData.out.println(GlobalData.makeStructName(GlobalData.Const.ROOT_TYPE) + " = type {i8*}");

        for(AST.class_ cl: GlobalData.ROOT_CLASS.children) {
            addStructsAllClassesDFS(cl);
        }
        Global.out.println();
	}

	public static void addStructsAllClassesDFS(AST_class_ cl){
		int attrCounter = 1;
		if(!GlobalData.Const.is_structable(cl.name))
        	return;
        StringBuilder ir = new StringBuilder();
        ir.append(GlobalData.makeStructName(cl.name)).append(" = type { ");
        ir.append(GlobalData.makeStructName(cl.parent));
        for(AST.feature f : cl.features) {
            if(f instanceof AST.attr) {
                AST.attr a = (AST.attr) f;
                GlobalData.attrIndexMap.put(GlobalData.mangledName(cl.name, a.name), attrCounter);
                attrCounter++;	               
                ir.append(", ").append(GlobalData.makeClassTypeOrPointer(a.typeid));
            }
    	}
    	ir.append("}");
    	GlobalData.out.println(ir.toString());
    	for(AST.class_ child: cl.children) {
            addStructsAllClassesDFS(child);
        }
	}

	public void addParentConstructor(String parent, String cl){
		// TODO     
	}

	public void addConstructorAllClassesDFS(AST.class_ cl){
		Visitor.thisClass = cl;
		// make constructor for cl
		StringBuilder ir = new StringBuilder("define void ");
		// add mangled name for method
		ir.append("@" + GlobalData.mangledName(cl.name, cl.name));
		ir.append("(").append(GlobalData.makeStructName(cl.name)).append("* %this){");
		GlobalData.out.println(ir.toString());
		GlobalData.out.println("entry:");
		// add parent constructors
		addParentConstructor(cl.parent, "%this");
		// visit attributes
		for(AST.feature f : cl.features) {
            if(f instanceof AST.attr) {
                AST.attr a = (AST.attr) f;	               
                Visitor.visit(a);
            }
    	}
		GlobalData.out.println("ret void ");
		GlobalData.out.println("}");
		GlobalData.out.println();
		// make constructor for all other classes DFS
		for(AST.class_ c : cl.children){
			addConstructorAllClassesDFS(c);
		}
	}

	public static void addConstructorAllClasses(){
		// make constructor for ROOT
		StringBuilder ir = new StringBuilder("define void ");
		// add mangled name for method
		ir.append("@" + GlobalData.mangledName(GlobalData.Const.ROOT_TYPE, GlobalData.Const.ROOT_TYPE));
		ir.append("(").append(GlobalData.makeStructName(GlobalData.Const.ROOT_TYPE)).append("* %this) {");
		GlobalData.out.println(ir.toString());
		GlobalData.out.println("entry:");
		GlobalData.out.println("ret void ");
		GlobalData.out.println("}");
		GlobalData.out.println();
		// make constructor for all other classes DFS
		for(AST.class_ cl : GlobalData.ROOT_CLASS.children){
			addConstructorAllClassesDFS(cl);
		}
	}

	public static void visitAllClassesDFS(AST.class_ node){
		GlobalData.attrScopeTable.enterScope();
        if(GlobalData.Const.is_structable(node.name)){
        	Visitor.visitMethods(node);
        }
        // iterate through all the child nodes
        for(AST.class_ child: node.children) {
            visitAllClassesDFS(child);
        }
        GlobalData.attrScopeTable.exitScope();
    }

}