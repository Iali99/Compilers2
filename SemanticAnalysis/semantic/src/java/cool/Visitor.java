package cool;
import java.util.*;
public class Visitor{

	public void visitProgram(AST.program p){
		// init classTable
		GlobalData.classTable = new HashMap<String, String>();
		GlobalData.inheritanceGraph = new InheritanceGraph(p);
		GlobalData.inheritanceGraph.printGraph();
	}
}
