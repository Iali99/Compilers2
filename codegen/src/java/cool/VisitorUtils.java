package cool;

public class VisitorUtils{
	// adds structs for all classes staring from root in dfs fashion
	public static void addStructsAllClasses(){
		GlobalData.out.println();
        GlobalData.out.println("; Struct declarations");
        GlobalData.out.println(GlobalData.makeStructName(GlobalData.Const.ROOT_TYPE) + " = type {i8*}");
        // GlobalData.classToVariableToIndexListMap.put(GlobalData.Const.ROOT_TYPE, new HashMap<>());

        for(AST.class_ cl: GlobalData.ROOT_CLASS.children) {
            addStructsAllClassesDFS(cl);
        }
        Global.out.println();
	}

	public static void addStructsAllClassesDFS(AST_class_ cl){
		if(!GlobalData.Const.is_structable(cl.name))
        	return;
        StringBuilder ir = new StringBuilder();
        ir.append(GlobalData.makeStructName(cl.name)).append(" = type { ");
        ir.append(GlobalData.makeStructName(cl.parent));
        for(AST.feature f : cl.features) {
            if(f instanceof AST.attr) {
                AST.attr a = (AST.attr) f;	               
                ir.append(", ").append(GlobalData.makeClassTypeOrPointer(a.typeid));
            }
    	}
    	ir.append("}");
    	GlobalData.out.println(ir.toString());
    	for(AST.class_ child: cl.children) {
            addStructsAllClassesDFS(child);
        }
	}

	public static void visitAllClassesDFS(AST.class_ node){
		GlobalData.scopeTable.enterScope();

        // visit the class
        if(!GlobalData.Const.isStandardClassName())
            visit(node);

        // iterate through all the child nodes
        for(AST.class_ child: node.children) {
            visitAllClassesDFS(child);
        }

        // exit scope
        GlobalData.scopeTable.exitScope();
    }

}