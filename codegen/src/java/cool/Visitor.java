package cool;
import java.util.*;
public class Visitor{
	public void visit(AST.program p){
		GlobalData.inheritanceGraph = new inheritanceGraph(p);
		for(AST.class_ cl: prog.classes) {
            if(!GlobalData.strConsToRegister.containsKey(cl.name)) {
                GlobalData.strConsToRegister.put(cl.name, Global.strCounter);
                GlobalData.strCounter++;
            }
            GlobalData.inheritanceGraph.addClass(cl);
        }
        Default.addDefaultStrings();
        Default.setDefaultClassSizes();
        GlobalData.addStringsAsGlobal();
        
	}
}