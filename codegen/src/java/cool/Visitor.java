package cool;
import java.util.*;
public class Visitor{
	public AST.class_ thisClass;

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
        // TODO : generate all default methods
        VisitorUtils.visitAllClassesDFS(GlobalData.ROOT_CLASS);
	}

	// visit attribute
	public static void visit(AST.attr at){
		String gepRegister = IRPrinter.createClassAttrGEP(Global.currentClass, "%this", at.name);
        String valueRegister = at.value.accept(this);


	}

	// visits all methods of class
	public void visitMethods(AST.class_ cl) {
		thisClass = cl;
        for(AST.feature f : cl.features) {
            if(f instanceof AST.method) {
                visit((AST.method) f);
            }
        }
    }

    // visit for method
    public void visit(AST.method m){
  		StringBuilder ir = new StringBuilder();
  		ir.append("define ").append(GlobalData.makeClassTypeOrPointer(m.typeid)).append(" ");
  		ir.append("@").append(GlobalData.mangledName(thisClass.name, m));
  		ir.append("(").append(GlobalData.makeClassTypeOrPointer(thisClass.name)).append(" %this");
  		for(AST.formal f : m.formals){
  			ir.append(", ").append(GlobalData.makeClassTypeOrPointer(f.typeid)).append(" %").append(f.name);
  		}
  		ir.append("){");
  		GlobalData.out.println(ir.toString());
  		GlobalData.out.println();
  		GlobalData.out.println("entry:");
  		// allocating address for formals
  		for(AST.formal f : m.formals){
  			IRInstructions.addAlloca(GlobalData.makeClassTypeOrPointer(f.typeid), GlobalData.makeAddressName(f.name));
  			IRInstructions.addStoreInstruction(GlobalData.makeClassTypeOrPointer(f.typeid), "%"+f.name, GlobalData.makeAddressName(f.name));
  		}
  		String ret = visit(m.body);
  		// bit casting if type mismatch
  		if(!m.typeid.equals(m.body.type)){
  			// TODO : bitcast
  		}
  		GlobalData.out.println("ret " + GlobalData.makeClassTypeOrPointer(m.typeid) + " " + ret);
  		GlobalData.out.println("}");
  		GlobalData.out.println();
    }
}