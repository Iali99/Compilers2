package cool;

public class VisitorUtils{
	// adds structs for all classes staring from root in dfs fashion
	public static void addStructsAllClasses(){
		GlobalData.out.println();
        GlobalData.out.println("; Struct declarations");
        GlobalData.out.println(GlobalData.makeStructName(GlobalData.Const.ROOT_TYPE) + " = type {i8*}");
        // GlobalData.classToVariableToIndexListMap.put(GlobalData.Const.ROOT_TYPE, new HashMap<>());

        for(AST.class_ cl: GlobalData.ROOT_CLASS.children) {
            if(!GlobalData.Const.is_structable(cl.name))
            	continue;
            StringBuilder ir = new StringBuilder();
            ir.append(GlobalData.makeStructName(cl.name)).append(" = type { ");
            ir.append(GlobalData.makeStructName(cl.parent));
            
        }
        Global.out.println();
	}

    // DFS helper for generateStructsAndCalculateSize
    private void generateStructsAndCalculateSizeDFS(InheritanceGraph.Node node) {
        AST.class_ cl = node.getAstClass();
        int size = 8; // initial 8 bytes for the type name in Object
        
        // Primitive types are i32, i8, i8*. No need of structs
        if(Utils.isPrimitiveType(cl.name))
            return;
        
        StringBuilder builder = new StringBuilder(Utils.getStructName(cl.name));
        size += Global.classSizeMap.get(node.getParent().getAstClass().name);
        builder.append(" = type { ").append(Utils.getStructName(node.getParent().getAstClass().name));
        
        // Updating the index map for the varaibles
        Map<String, String> variableToIndexListMap = new HashMap<>();
        
        // variables present in the parent
        Map<String, String> parentMap = Global.classToVariableToIndexListMap.get(node.getParent().getAstClass().name);
        for(Map.Entry<String, String> entry : parentMap.entrySet()) {
            variableToIndexListMap.put(entry.getKey(), " i32 0,"+entry.getValue());
        }

        // variables declared inside the class
        int index = 0;
        for(AST.feature f : cl.features) {
            if(f instanceof AST.attr) {
                index++;
                AST.attr a = (AST.attr) f;
                size += Utils.getSizeForStruct(a.typeid);
                builder.append(", ").append(Utils.getBasicTypeOrPointer(a.typeid));
                variableToIndexListMap.put(a.name, " i32 0, i32 "+index);
            } else {
                // updating the function mangled names
                AST.method m = (AST.method) f;
                Global.functionMangledNames.add(Utils.getMangledName(cl.name, m.name));
            }
        }

        builder.append(" }");
        Global.out.println(builder.toString());

        Global.classToVariableToIndexListMap.put(cl.name, variableToIndexListMap);
        Global.classSizeMap.put(cl.name, size);
        
        // Depth first call        
        for(InheritanceGraph.Node child: node.getChildren()) {
            generateStructsAndCalculateSizeDFS(child);
        }
    }

}