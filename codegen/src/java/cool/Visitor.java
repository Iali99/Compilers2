package cool;
import java.util.*;
public class Visitor{
	public static AST.class_ thisClass;
	public static AST.method thisMethod;
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
				Default.addCdecls();
        Default.setDefaultClassSizes();
        VisitorUtils.addStructsAllClasses();
        VisitorUtils.addConstructorAllClasses();
        Default.visitDefaultMethods();
        VisitorUtils.visitAllClassesDFS(GlobalData.ROOT_CLASS);
				GlobalData.addStringsAsGlobal();
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
        	if(f instanceof AST.attr) {
        		a = (AST.attr) f;
                GlobalData.attrScopeTable.insert(a.name, a.typeid);
            }
            else if(f instanceof AST.method) {
                visit((AST.method) f);
            }
        }
    }

    // visit for method
    public void visit(AST.method m){
    	GlobalData.attrScopeTable.enterScope();
		  thisMethod = m;
    	GlobalData.loopCounter = 0;
			GLobalData.ifCounter = 0;
  		StringBuilder ir = new StringBuilder();
  		ir.append("define ").append(GlobalData.makeClassTypeOrPointer(m.typeid)).append(" ");
  		ir.append("@").append(GlobalData.mangledName(thisClass.name, m));
  		ir.append("(").append(GlobalData.makeClassTypeOrPointer(thisClass.name)).append(" %this");
  		for(AST.formal f : m.formals){
				GlobalData.formalsMangledList.put(GlobalData.mangledFormalName(thisClass.name,thisMethod.name,f.name));
  			ir.append(", ").append(GlobalData.makeClassTypeOrPointer(f.typeid)).append(" %").append(f.name);
  		}
  		ir.append("){");
  		GlobalData.out.println(ir.toString());
  		GlobalData.out.println();
			IRInstrucions.add0ErrorLabel();
			GlobalData.out.println();
			IRInstrucions.addVoidErrorLabel();
			GlobalData.out.println();
  		GlobalData.out.println("entry:");
  		// allocating address for formals
  		for(AST.formal f : m.formals){
  			GlobalData.attrScopeTable.insert(f.name, f.typeid);
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
    	GlobalData.attrScopeTable.exitScope();
    }

		public String visit(AST.bool_const e){
		  if(e.value == true)
		    return "1";
		  return "0";
		}

		public String visit(AST.string_const e){
		  GlobalData.strConsToRegister.put(e.value,strCounter);
		  strCounter++;
		  String retRegister = IRInstrucions.addGEPInstruction(e.value);
		  return retRegister;
		}

		public String visit(AST.int_const e){
		  return Integer.toString(e.value);
		}

		public String visit(AST.object e){
		  if(e.name.equals("self"))
		    return "%this";
		  String retRegister;
		  if(isMethodParam(e.name)){
		    retRegister = IRInstrucions.addLoadInstruction(GlobalData.makeClassTypeOrPointer(e.type),GlobalData.makeAddressName(e.name));
		    return retRegister;
		  }
		  else{
		    String addr = IRInstrucions.addGEPInstruction(e.type,"%this",e.name);
		    retRegister = IRInstrucions.addLoadInstruction(GlobalData.makeClassTypeOrPointer(e.type),addr);
		    return retRegister;
		  }

		}

		public String visit(AST.comp e){
		  String e1 = visit(e.e1);
		  String retRegister = IRInstrucions.addBinaryInstruction("xor","bool",e1,"1");
		}

		public String visit(AST.eq e){
		  String e1 = visit(e.e1);
		  String e2 = visit(e.e2);
		  String retRegister = IRInstrucions.addIcmpInstruction("eq",Global.makeClassTypeOrPointer(e1.type),e1,e2);
		  return retRegister;
		}

		public String visit(AST.leq e){
		  String e1 = visit(e.e1);
		  String e2 = visit(e.e2);
		  String retRegister = IRInstrucions.addIcmpInstruction("sle",Global.makeClassTypeOrPointer(e1.type),e1,e2);
		  return retRegister;
		}

		public String visit(AST.lt e){
		  String e1 = visit(e.e1);
		  String e2 = visit(e.e2);
		  String retRegister = IRInstrucions.addIcmpInstruction("slt",Global.makeClassTypeOrPointer(e1.type),e1,e2);
		  return retRegister;
		}

		public String visit(AST.neg e){
		  String e1 = visit(e.e1);
		  String retRegister = IRInstrucions.addBinaryInstruction("sub",e1.type,"0",r);
		  return retRegister;
		}



		public Strign visit(AST.divide e){
		  String e1 = visit(e.e1);
		  String e2 = visit(e.e2);
		  String check = IRInstrucions.addIcmpInstruction("eq","i32",e2,"0");
		  IRInstrucions.addBrInstruction(check,"%divide0true","%divide0false");
		  StringBuilder ir = new StringBuilder("\n");
		  ir.append("divide0false :\n");
		  String retRegister = IRInstrucions.addBinaryInstruction("sdiv",e1.type,e1,e2);
		  return retRegister;
		}

		public Strign visit(AST.mul e){
		  String e1 = visit(e.e1);
		  String e2 = visit(e.e2);
		  String retRegister = IRInstrucions.addBinaryInstruction("mul",e1.type,e1,e2);
		  return retRegister;
		}

		public Strign visit(AST.sub e){
		  String e1 = visit(e.e1);
		  String e2 = visit(e.e2);
		  String retRegister = IRInstrucions.addBinaryInstruction("sub",e1.type,e1,e2);
		  return retRegister;
		}

		public Strign visit(AST.plus e){
		  String e1 = visit(e.e1);
		  String e2 = visit(e.e2);
		  String retRegister = IRInstrucions.addBinaryInstruction("add",e1.type,e1,e2);
		  return retRegister;
		}

		public String visit(AST.isVoid e){
		  String e1 = visit(e.e1);
		  String retRegister = IRInstrucions.addIcmpInstruction("eq","i1",e1,"null");
		  return retRegister;
		}

		public String visit(AST.new_ e){
		  String retRegister;
		  if(e.typeid.equals("int"))
		    return "0";
		  if(e.typeid.equals("bool"))
		    return "0";
		  if(e.typeid.equals("string")){
		    retRegister = IRInstrucions.addGEPInstruction("");
		    return retRegister;
		  }
		  retRegister = "%" + Integer.toString(GlobalData.Counter);
		  GlobalData.Counter++;
		  StringBuilder ir = new StringBuilder(retRegister);
		  ir.append(" = call noalias i8* @malloc(i64 ")
		  .append(GlobalData.classNameToSize(e.typeid)).append(" )");
		  GlobalData.out.println(ir.toString());
		  return retRegister;
		}

		public String visit(AST.assign e){
		  String e1 = visit(e.e1);
		  String type = GlobalData.attrScopeTable.get(e.name);
		  if(!type.equals(e.e1.type)){
		    e1 = IRInstrucions.addConvertInstruction("bitcast",e.e1.type,type,e1);
		  }
		  if(GlobalData.formalsMangledList.contains(GlobalData.mangledFormalName(Visitor.thisClass.name,Visitor.thisMethod.name,e.name))){
		    IRInstrucions.addStoreInstruction(GlobalData.makeClassTypeOrPointer(type),e1,GlobalData.makeAddressName(e.name));
		  }
		  else{
		    String addr = IRInstrucions.addGEPInstruction(Visitor.thisClass.name,"%this",e.name);
		    IRInstrucions.addStoreInstruction(GlobalData.makeClassTypeOrPointer(type),e1,addr);
		  }
		  return null;
		}

		public String visit(AST.block e){
		  String retRegister;
		  for(int i =0;i<e.l1.size();i++){
		    retRegister = visit(e.l1.get(i));
		  }
		  return retRegister;
		}

		public String visit(AST.loop e){
		  e.counter = loopCounter;
		  loopCounter++;
		  IRInstrucions.addBrInstruction("%loopCond"+Integer.toString(e.counter));
		  StringBuilder ir = new StringBuilder("\n");
		  ir.append("loopCond").append(e.counter).append(":\n");
		  GlobalData.out.println(ir.toString());
		  String pred = visit(e.predicate);
		  IRInstrucions.addBrInstruction(pred,"%loopBody"+Integer.toString(e.counter),"%loopEnd"+Integer.toString(e.counter));
		  ir.setLength(0);
		  ir.append("\nloopBody"+Integer.toString(e.counter)+":");
		  GlobalData.out.println(ir.toString());
		  String retRegister = visit(body);
		  IRInstrucions.addBrInstruction("%loopCond"+Integer.toString(e.counter));
		  ir.setLength(0);
		  ir.append("\nloopEnd"+Integer.toString(e.counter)+":");
		  GlobalData.out.println(ir.toString());
		  return retRegister;
		}

		public String visit(AST.cond e){
		  e.counter = ifCounter;
		  ifCounter++;
		  String pred = visit(e.predicate);
		  String retRegister = IRInstrucions.addAlloca(e.ifbody.type);
		  IRInstrucions.addBrInstruction(pred,"%ifBody"+Integer.toString(e.counter),"%elseBody"+Integer.toString(e.counter));
		  StringBuilder ir = new StringBuilder("\n");
		  ir.append("ifBody").append(e.counter).append(":");
		  GlobalData.out.println(ir.toString());
		  String ifReg = visit(e.ifbody);
		  IRInstrucions.addStoreInstruction(GlobalData.makeClassTypeOrPointer(e.ifBody.type),ifReg,retRegister);
		  IRInstrucions.addBrInstruction("%ifEnd"+Integer.toString(e.counter));
		  ir.setLength(0);
		  ir.append("elseBody").append(e.counter).append(":");
		  GlobalData.out.println(ir.toString());
		  String elseReg = visit(e.ifbody);
		  IRInstrucions.addStoreInstruction(GlobalData.makeClassTypeOrPointer(e.ifBody.type),elseReg,retRegister);
		  IRInstrucions.addBrInstruction("%ifEnd"+Integer.toString(e.counter));
		  ir.setLength(0);
		  ir.append("ifEnd").append(e.counter).append(":");
		  GlobalData.out.println(ir.toString());
		  return retRegister;
		}

		public String visit(AST.static_dispatch e){
		  String caller = visit(e.caller);
		  String alloca = IRInstrucions.addAlloca(e.caller.type);
		  String type = GlobalData.makeClassTypeOrPointer(e.caller.type);
		  if(!isPrim(type)){
		    type = type.substring(0,type.length()-1);
		  }
		  IRInstrucions.addStoreInstruction(type,caller,alloca);
		  String comp = IRInstrucions.addIcmpInstruction("eq",GLobalData.makeClassTypeOrPointer(e.caller.type),alloca,"null");
		  IRInstrucions.addBrInstruction(comp,"%voidTrue","%voidFalse");
		  GLobalData.out.println("voidFalse :");
		  if(!e.caller.type.equals(e.typeid)){
		    caller = IRInstrucions.addConvertInstruction("bitcast",e.caller.type,e.typeid,caller)
		  }
		  StringBuilder args = new StringBuilder(GlobalData.makeClassTypeOrPointer(e.typeid));
		  args.append(" ").append(caller);
		  for(int i =0;i<e.actuals.size();i++){
		    String actual = visit(e.actual.get(i));
		    args.append(", ").append(GlobalData.makeClassTypeOrPointer(e.actuals.get(i).type))
		    .append(actual);
		  }
		  String retRegister = IRInstrucions.addCallInstruction(e.typeid,GlobalData.mangledName(e.name),args.toString());
		  return retRegister;
		}

}
