package cool;
import java.util.*;
import java.io.PrintWriter;
public class VisitorUtils{
	// adds structs for all classes staring from root in dfs fashion
	public static void addStructsAllClasses(){
		GlobalData.out.println();
        GlobalData.out.println("; Struct declarations");
        GlobalData.out.println(GlobalData.makeStructName(GlobalData.Const.ROOT_TYPE) + " = type {i8*}");

        for(String cl: GlobalData.ROOT_CLASS.children) {
            addStructsAllClassesDFS(GlobalData.inheritanceGraph.getClass(cl));
        }
        GlobalData.out.println();
	}

	public static void addStructsAllClassesDFS(AST.class_ cl){
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
    	for(String child: cl.children){
            addStructsAllClassesDFS(GlobalData.inheritanceGraph.getClass(child));
        }
	}

	public static void addConstructorAllClassesDFS(AST.class_ cl){
		Visitor.thisClass = cl;
		// make constructor for cl
		StringBuilder ir = new StringBuilder("define void ");
		// add mangled name for method
		ir.append("@" + GlobalData.mangledName(cl.name, cl.name));
		ir.append("(").append(GlobalData.makeStructName(cl.name)).append("* %this){");
		GlobalData.out.println(ir.toString());
		GlobalData.out.println("entry:");
		// add parent constructors
        if(cl.parent!=null) {
            String regNew = IRInstructions.addConvertInstruction("bitcast", Visitor.thisClass.name, cl.parent, "%this");
            IRInstructions.callConstructorInstruction(cl.parent, regNew);
        }
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
		for(String c : cl.children){
			addConstructorAllClassesDFS(GlobalData.inheritanceGraph.getClass(c));
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
		for(String cl : GlobalData.ROOT_CLASS.children){
			addConstructorAllClassesDFS(GlobalData.inheritanceGraph.getClass(cl));
		}
	}

	public static void visitAllClassesDFS(AST.class_ node){
		GlobalData.attrScopeTable.enterScope();
        if(GlobalData.Const.is_structable(node.name)){
        	Visitor.visitMethods(node);
        }
        // iterate through all the child nodes
        for(String child: node.children) {
            visitAllClassesDFS(GlobalData.inheritanceGraph.getClass(child));
        }
        GlobalData.attrScopeTable.exitScope();
    }

}
