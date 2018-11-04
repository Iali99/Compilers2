package cool;
import java.util.*;
public class Visitor{
	public void visit(AST.program p){
		GlobalData.inheritanceGraph = new inheritanceGraph(p);
		for(AST.class_ cl: prog.classes) {
            if(!GlobalData.strConsToRegister.containsKey(cl.name)) {
                GlobalData.strConsToRegister.put(cl.name, GlobalData.strCounter);
                GlobalData.strCounter++;
            }
            GlobalData.inheritanceGraph.addClass(cl);
        }
        Default.addDefaultStrings();
        Default.setDefaultClassSizes();
        GlobalData.addStringsAsGlobal();
        VisitorUtils.addStructsAllClasses();
        VisitorUtils.addConstructorAllClasses();
        VisitorUtils.visitAllClassesDFS(GlobalData.ROOT_CLASS);
	}

	// visit attribute
	public static void visit(AST.attr at){
		
	}

	// visits all methods of class
	public void visitMethods(AST.class_ cl) {
        for(AST.feature f : cl.features) {
            if(f instanceof AST.method) {
                visit((AST.method) f);
            }
        }
    }

    // visit for method
    public void visit(AST.method m){

    }
}