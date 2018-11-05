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
