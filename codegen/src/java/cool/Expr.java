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
  StringBuilder builder = new StringBuilder("\n");
  builder.append("divide0false :\n");
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
  StringBuilder builder = new StringBuilder(retRegister);
  builder.append(" = call noalias i8* @malloc(i64 ")
  .append(GlobalData.classNameToSize(e.typeid)).append(" )");
  GlobalData.out.println(builder.toString());
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
  StringBuilder builder = new StringBuilder("\n");
  builder.append("loopCond").append(e.counter).append(":\n");
  GlobalData.out.println(builder.toString());
  String pred = visit(e.predicate);
  IRInstrucions.addBrInstruction(pred,"%loopBody"+Integer.toString(e.counter),"%loopEnd"+Integer.toString(e.counter));
  builder.setLength(0);
  builder.append("\nloopBody"+Integer.toString(e.counter)+":");
  GlobalData.out.println(builder.toString());
  String retRegister = visit(body);
  IRInstrucions.addBrInstruction("%loopCond"+Integer.toString(e.counter));
  builder.setLength(0);
  builder.append("\nloopEnd"+Integer.toString(e.counter)+":");
  GlobalData.out.println(builder.toString());
  return retRegister;
}

public String visit(AST.cond e){
  e.counter = ifCounter;
  ifCounter++;
  String pred = visit(e.predicate);
  String retRegister = IRInstrucions.addAlloca(e.ifbody.type);
  IRInstrucions.addBrInstruction(pred,"%ifBody"+Integer.toString(e.counter),"%elseBody"+Integer.toString(e.counter));
  StringBuilder builder = new StringBuilder("\n");
  builder.append("ifBody").append(e.counter).append(":");
  GlobalData.out.println(builder.toString());
  String ifReg = visit(e.ifbody);
  IRInstrucions.addStoreInstruction(GlobalData.makeClassTypeOrPointer(e.ifBody.type),ifReg,retRegister);
  IRInstrucions.addBrInstruction("%ifEnd"+Integer.toString(e.counter));
  builder.setLength(0);
  builder.append("elseBody").append(e.counter).append(":");
  GlobalData.out.println(builder.toString());
  String elseReg = visit(e.ifbody);
  IRInstrucions.addStoreInstruction(GlobalData.makeClassTypeOrPointer(e.ifBody.type),elseReg,retRegister);
  IRInstrucions.addBrInstruction("%ifEnd"+Integer.toString(e.counter));
  builder.setLength(0);
  builder.append("ifEnd").append(e.counter).append(":");
  GlobalData.out.println(builder.toString());
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
