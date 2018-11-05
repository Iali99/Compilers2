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

//TODO : Add visit method for object
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
  //TODO : add instructions to comp
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

  //TODO : add check for division by zero
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

//TODO : add visit for isVoid expr

public String visit(AST.new_ e){
  //TODO : add visit for new_
}

public String visit(AST.assign e){
  String e1 = visit(e.e1);
  // TODO: add the visit code.

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

//TODO : add visit for Static dispatch.
