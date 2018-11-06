package cool;
import java.util.*;
import java.io.PrintWriter;
public class Visitor{
	public static AST.class_ thisClass;
	public static AST.method thisMethod;
	public static void visit(AST.program p){
		GlobalData.inheritanceGraph = new InheritanceGraph(p);
		for(AST.class_ cl: p.classes) {
            if(!GlobalData.strConsToRegister.containsKey(cl.name)) {
                GlobalData.strConsToRegister.put(cl.name, GlobalData.strCounter);
                GlobalData.strCounter++;
            }
            GlobalData.inheritanceGraph.insert(cl);
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
			if(!GlobalData.Const.is_structable(at.typeid)){
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
			if(!GlobalData.Const.is_structable(at.typeid)){
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
						AST.new_ n = new AST.new_(GlobalData.Const.ROOT_TYPE, 0);
                        n.type = GlobalData.Const.ROOT_TYPE;
                        value = visit(n);
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
	public static void visitMethods(AST.class_ cl) {
		thisClass = cl;
        for(AST.feature f : cl.features) {
        	if(f instanceof AST.attr) {
        		AST.attr a = (AST.attr) f;
                GlobalData.attrScopeTable.insert(a.name, a.typeid);
            }
            else if(f instanceof AST.method) {
                visit((AST.method) f);
            }
        }
    }

    // visit for method
    public static void visit(AST.method m){
    	GlobalData.attrScopeTable.enterScope();
		  thisMethod = m;
    	GlobalData.loopCounter = 0;
			GlobalData.ifCounter = 0;
  		StringBuilder ir = new StringBuilder();
  		ir.append("define ").append(GlobalData.makeClassTypeOrPointer(m.typeid)).append(" ");
  		ir.append("@").append(GlobalData.mangledName(thisClass.name, m));
  		ir.append("(").append(GlobalData.makeClassTypeOrPointer(thisClass.name)).append(" %this");
  		for(AST.formal f : m.formals){
				GlobalData.formalsMangledList.add(GlobalData.mangledFormalName(thisClass.name,thisMethod.name,f.name));
  			ir.append(", ").append(GlobalData.makeClassTypeOrPointer(f.typeid)).append(" %").append(f.name);
  		}
  		ir.append("){");
  		GlobalData.out.println(ir.toString());
  		GlobalData.out.println();
			IRInstructions.add0ErrorLabel();
			GlobalData.out.println();
			IRInstructions.addVoidErrorLabel();
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
  			ret = IRInstructions.addConvertInstruction("bitcast", m.body.type, m.typeid, ret);
  		}
  		GlobalData.out.println("ret " + GlobalData.makeClassTypeOrPointer(m.typeid) + " " + ret);
  		GlobalData.out.println("}");
  		GlobalData.out.println();
    	GlobalData.attrScopeTable.exitScope();
    }

	public static String visit(AST.bool_const e){
	  if(e.value == true)
	    return "1";
	  return "0";
	}

	public static String visit(AST.string_const e){
	  GlobalData.strConsToRegister.put(e.value,GlobalData.strCounter);
	  GlobalData.strCounter++;
	  String retRegister = IRInstructions.addGEPInstruction(e.value);
	  return retRegister;
	}

	public static String visit(AST.int_const e){
	  return Integer.toString(e.value);
	}
	public static boolean isMethodParam(String name){
		if(GlobalData.formalsMangledList.contains(GlobalData.mangledFormalName(Visitor.thisClass.name,Visitor.thisMethod.name,name)))
			return true;
		return false;
	}
	public static String visit(AST.object e){
	  if(e.name.equals("self"))
	    return "%this";
	  String retRegister;
	  if(isMethodParam(e.name)){
	    retRegister = IRInstructions.addLoadInstruction(GlobalData.makeClassTypeOrPointer(e.type),GlobalData.makeAddressName(e.name));
	    return retRegister;
	  }
	  else{
	    String addr = IRInstructions.addGEPInstruction(e.type,"%this",e.name);
	    retRegister = IRInstructions.addLoadInstruction(GlobalData.makeClassTypeOrPointer(e.type),addr);
	    return retRegister;
	  }

	}

	public static String visit(AST.comp e){
	  String e1 = visit(e.e1);
	  String retRegister = IRInstructions.addBinaryInstruction("xor","bool",e1,"1");
		return retRegister;
	}

	public static String visit(AST.eq e){
	  String e1 = visit(e.e1);
	  String e2 = visit(e.e2);
	  String retRegister = IRInstructions.addIcmpInstruction("eq",GlobalData.makeClassTypeOrPointer(e.e1.type),e1,e2);
	  return retRegister;
	}

	public static String visit(AST.leq e){
	  String e1 = visit(e.e1);
	  String e2 = visit(e.e2);
	  String retRegister = IRInstructions.addIcmpInstruction("sle",GlobalData.makeClassTypeOrPointer(e.e1.type),e1,e2);
	  return retRegister;
	}

	public static String visit(AST.lt e){
	  String e1 = visit(e.e1);
	  String e2 = visit(e.e2);
	  String retRegister = IRInstructions.addIcmpInstruction("slt",GlobalData.makeClassTypeOrPointer(e.e1.type),e1,e2);
	  return retRegister;
	}

	public static String visit(AST.neg e){
	  String e1 = visit(e.e1);
	  String retRegister = IRInstructions.addBinaryInstruction("sub",e.e1.type,"0",e1);
	  return retRegister;
	}



	public static String visit(AST.divide e){
	  String e1 = visit(e.e1);
	  String e2 = visit(e.e2);
	  String check = IRInstructions.addIcmpInstruction("eq","i32",e2,"0");
	  IRInstructions.addBrInstruction(check,"%divide0true","%divide0false");
	  StringBuilder ir = new StringBuilder("\n");
	  ir.append("divide0false :\n");
	  String retRegister = IRInstructions.addBinaryInstruction("sdiv",e.e1.type,e1,e2);
	  return retRegister;
	}

	public static String visit(AST.mul e){
	  String e1 = visit(e.e1);
	  String e2 = visit(e.e2);
	  String retRegister = IRInstructions.addBinaryInstruction("mul",e.e1.type,e1,e2);
	  return retRegister;
	}

	public static String visit(AST.sub e){
	  String e1 = visit(e.e1);
	  String e2 = visit(e.e2);
	  String retRegister = IRInstructions.addBinaryInstruction("sub",e.e1.type,e1,e2);
	  return retRegister;
	}

	public static String visit(AST.plus e){
	  String e1 = visit(e.e1);
	  String e2 = visit(e.e2);
	  String retRegister = IRInstructions.addBinaryInstruction("add",e.e1.type,e1,e2);
	  return retRegister;
	}

	public static String visit(AST.isvoid e){
	  String e1 = visit(e.e1);
	  String retRegister = IRInstructions.addIcmpInstruction("eq","i1",e1,"null");
	  return retRegister;
	}

	public static String visit(AST.new_ e){
	  String retRegister;
	  if(e.typeid.equals("int"))
	    return "0";
	  if(e.typeid.equals("bool"))
	    return "0";
	  if(e.typeid.equals("string")){
	    retRegister = IRInstructions.addGEPInstruction("");
	    return retRegister;
	  }
	  retRegister = "%" + Integer.toString(GlobalData.Counter);
	  GlobalData.Counter++;
	  StringBuilder ir = new StringBuilder(retRegister);
	  ir.append(" = call noalias i8* @malloc(i64 ")
	  .append(GlobalData.classNameToSize.get(e.typeid)).append(" )");
	  GlobalData.out.println(ir.toString());
	  return retRegister;
	}

	public static String visit(AST.assign e){
	  String e1 = visit(e.e1);
	  String type = GlobalData.attrScopeTable.lookUpLocal(e.name);
	  if(!type.equals(e.e1.type)){
	    e1 = IRInstructions.addConvertInstruction("bitcast",e.e1.type,type,e1);
	  }
	  if(GlobalData.formalsMangledList.contains(GlobalData.mangledFormalName(Visitor.thisClass.name,Visitor.thisMethod.name,e.name))){
	    IRInstructions.addStoreInstruction(GlobalData.makeClassTypeOrPointer(type),e1,GlobalData.makeAddressName(e.name));
	  }
	  else{
	    String addr = IRInstructions.addGEPInstruction(Visitor.thisClass.name,"%this",e.name);
	    IRInstructions.addStoreInstruction(GlobalData.makeClassTypeOrPointer(type),e1,addr);
	  }
	  return null;
	}

		public static String visit(AST.block e){
		  String retRegister = null;
		  for(int i =0;i<e.l1.size();i++){
		    retRegister = visit(e.l1.get(i));
		  }
		  return retRegister;
		}

	public static String visit(AST.loop e){
	  e.counter = GlobalData.loopCounter;
	  GlobalData.loopCounter++;
	  IRInstructions.addBrInstruction("%loopCond"+Integer.toString(e.counter));
	  StringBuilder ir = new StringBuilder("\n");
	  ir.append("loopCond").append(e.counter).append(":\n");
	  GlobalData.out.println(ir.toString());
	  String pred = visit(e.predicate);
	  IRInstructions.addBrInstruction(pred,"%loopBody"+Integer.toString(e.counter),"%loopEnd"+Integer.toString(e.counter));
	  ir.setLength(0);
	  ir.append("\nloopBody"+Integer.toString(e.counter)+":");
	  GlobalData.out.println(ir.toString());
	  String retRegister = visit(e.body);
	  IRInstructions.addBrInstruction("%loopCond"+Integer.toString(e.counter));
	  ir.setLength(0);
	  ir.append("\nloopEnd"+Integer.toString(e.counter)+":");
	  GlobalData.out.println(ir.toString());
	  return retRegister;
	}

	public static String visit(AST.cond e){
	  e.counter = GlobalData.ifCounter;
	  GlobalData.ifCounter++;
	  String pred = visit(e.predicate);
	  String retRegister = IRInstructions.addAlloca(e.ifbody.type);
	  IRInstructions.addBrInstruction(pred,"%ifBody"+Integer.toString(e.counter),"%elseBody"+Integer.toString(e.counter));
	  StringBuilder ir = new StringBuilder("\n");
	  ir.append("ifBody").append(e.counter).append(":");
	  GlobalData.out.println(ir.toString());
	  String ifReg = visit(e.ifbody);
	  IRInstructions.addStoreInstruction(GlobalData.makeClassTypeOrPointer(e.ifbody.type),ifReg,retRegister);
	  IRInstructions.addBrInstruction("%ifEnd"+Integer.toString(e.counter));
	  ir.setLength(0);
	  ir.append("elseBody").append(e.counter).append(":");
	  GlobalData.out.println(ir.toString());
	  String elseReg = visit(e.ifbody);
	  IRInstructions.addStoreInstruction(GlobalData.makeClassTypeOrPointer(e.ifbody.type),elseReg,retRegister);
	  IRInstructions.addBrInstruction("%ifEnd"+Integer.toString(e.counter));
	  ir.setLength(0);
	  ir.append("ifEnd").append(e.counter).append(":");
	  GlobalData.out.println(ir.toString());
	  return retRegister;
	}

	public static String visit(AST.static_dispatch e){
	  String caller = visit(e.caller);
	  String alloca = IRInstructions.addAlloca(e.caller.type);
	  String type = GlobalData.makeClassTypeOrPointer(e.caller.type);
	  if(!IRInstructions.isPrim(type)){
	    type = type.substring(0,type.length()-1);
	  }
	  IRInstructions.addStoreInstruction(type,caller,alloca);
	  String comp = IRInstructions.addIcmpInstruction("eq",GlobalData.makeClassTypeOrPointer(e.caller.type),alloca,"null");
	  IRInstructions.addBrInstruction(comp,"%voidTrue","%voidFalse");
	  GlobalData.out.println("voidFalse :");
	  if(!e.caller.type.equals(e.typeid)){
	    caller = IRInstructions.addConvertInstruction("bitcast",e.caller.type,e.typeid,caller);
	  }
	  StringBuilder args = new StringBuilder(GlobalData.makeClassTypeOrPointer(e.typeid));
	  args.append(" ").append(caller);
	  for(int i =0;i<e.actuals.size();i++){
	    String actual = visit(e.actuals.get(i));
	    args.append(", ").append(GlobalData.makeClassTypeOrPointer(e.actuals.get(i).type))
	    .append(actual);
	  }
	  String retRegister = IRInstructions.addCallInstruction(e.typeid,GlobalData.mangledName(e.typeid,e.name),args.toString());
	  return retRegister;
	}

	public static String visit(AST.expression e){
		if(e == null) return null;
		if(e instanceof AST.no_expr)
				return visit((AST.no_expr)e);
		if(e instanceof AST.bool_const)
			return visit((AST.bool_const)e);
		if(e instanceof AST.string_const)
			return visit((AST.string_const)e);
		if(e instanceof AST.int_const)
			return visit((AST.int_const)e);
		if(e instanceof AST.object)
			return visit((AST.object)e);
		if(e instanceof AST.comp)
			return visit((AST.comp)e);
		if(e instanceof AST.eq)
			return visit((AST.eq)e);
		if(e instanceof AST.leq)
			return visit((AST.leq)e);
		if(e instanceof AST.lt)
			return visit((AST.lt)e);
		if(e instanceof AST.neg)
			return visit((AST.neg)e);
		if(e instanceof AST.divide)
			return visit((AST.divide)e);
		if(e instanceof AST.mul)
			return visit((AST.mul)e);
		if(e instanceof AST.sub)
			return visit((AST.sub)e);
		if(e instanceof AST.plus)
			return visit((AST.plus)e);
		if(e instanceof AST.isvoid)
			return visit((AST.isvoid)e);
		if(e instanceof AST.new_)
			return visit((AST.new_)e);
		if(e instanceof AST.assign)
			return visit((AST.assign)e);
		if(e instanceof AST.block)
			return visit((AST.block)e);
		if(e instanceof AST.loop)
			return visit((AST.loop)e);
		if(e instanceof AST.cond)
			return visit((AST.cond)e);
		if(e instanceof AST.static_dispatch)
			return visit((AST.static_dispatch)e);
		return null;
	}

}
