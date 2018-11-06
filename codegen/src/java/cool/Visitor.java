package cool;
import java.util.*;
public class Visitor{
	public static AST.class_ thisClass;

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
        Default.visitDefaultMethods();
        VisitorUtils.visitAllClassesDFS(GlobalData.ROOT_CLASS);
	}

	// visit attribute
	public static void visit(AST.attr at){
		String value = visit(at.value);
		String gep = IRInstructions.addGEPInstruction(thisClass.name, "%this", at.name);
        
		if(value == null){
			if(!GlobalData.Conts.is_structable(at.typeid)){
				// is not structable hence, store default value
				value = GlobalData.getDefaultValue(at.typeid);
				IRInstructions.addStoreInstruction(GlobalData.makeClassTypeOrPointer(at.typeid), value, gep);
			}
			else{
				// no assignment
				value = "null";
				IRInstructions.addDPStoreInstruction(at.typeid, value, gep);
			}
		}
		else{
			if(!GlobalData.Conts.is_structable(at.typeid)){
				// simply store value
				IRInstructions.addStoreInstruction(GlobalData.makeClassTypeOrPointer(at.typeid), value, gep);
			}
			else{
				if(at.value.type.equals(at.typeid)){
					IRInstructions.addDPStoreInstruction(at.typeid, value, gep);
				}
				else{
					if(!GlobalData.Const.is_structable(at.value.type)){
						// at.value to be casted to object
						AST.new_ n = new AST.new_(Global.Constants.ROOT_TYPE, 0);
                        n.type = GlobalData.Const.ROOT_TYPE;
                        value = visit(newObj);
                        // typename fixing
                        String gep_ = IRInstructions.addGEPInstruction(GlobalData.Const.ROOT_TYPE, value, "");
                        String value_ = IRInstructions.addGEPInstruction(at.value.type);
                        IRInstructions.addStoreInstruction(GlobalData.makeClassTypeOrPointer(GlobalData.Const.STRING_TYPE), value_, gep_);
						IRInstructions.addDPStoreInstruction(at.typeid, value, gep);
					}
					else{
						String type1 = at.value.type;
						String type2 = GlobalData.classTable.get(type1);
						while(!type1.equals(at.typeid)){
							value = IRInstructions.addConvertInstruction("bitcast", type1, type2, value);
							type1 = type2;
							type2 = GlobalData.classTable.get(type1);
						}
						IRInstructions.addDPStoreInstruction(at.typeid, value, gep);
					}
				}
			}
		}
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
    	GlobalData.loopCounter = 0;
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