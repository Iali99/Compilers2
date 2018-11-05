package cool;

public class IRInstrucions{

  public static int alignment(String type){
    if(type.substring(type.length()-1).equals("*"))
       return 8;
    else if(type.length()>0)
      return 4;
    else
      return -1;
  }

  /* Terminator Instrucions.
  */
  //return instruction
  public static void addRetInstruction(String type,String value){
    StringBuilder builder = new StringBuilder("ret ");
    builder.append(type);
    if(!type.equals("void"))
      builder.append(" ").append(value);
    GlobalData.out.println(builder.toString());
  }

  //break instruction for if else.
  public static void addBrInstruction(String cond,String trueLabel,String falseLabel){
    StringBuilder builder = new StringBuilder("br i1 ");
    builder.append(cond).append(" ,label ").append(trueLabel).append(" ,label ")
    .append(falseLabel);
    GlobalData.out.println(builder.toString());
  }

  //unconditional break instruction.
  public static void addBrInstruction(String label){
    StringBuilder builder = new StringBuilder("br label ");
    builder.append(label);
    GlobalData.out.println(builder.toString());
  }

  /* Binary Instructions.
  */

  //binary instruction based on arguments.
  public static String addBinaryInstruction(String operation,String type, String op1, String op2){
    StringBuilder builder = new StringBuilder("");
    type = GlobalData.makeClassTypeOrPointer(type);
    String retRegister = "%"+GlobalData.Counter;
    GlobalData.Counter++;
    builder.append(retRegister).append(" = ").append(operation).append(" nsw ")
    .append(type).append(" ").append(op1).append(" , ").append(op2);
    GlobalData.out.println(builder.toString());
    return retRegister;
  }

  /* Memory Operation Instructions.
  */
  //alloca instruction
  public static String addAlloca(String type){
    StringBuilder builder = new StringBuilder("");
    String retRegister = "%"+GlobalData.Counter;
    GlobalData.Counter++;
    type = getType(type);
    builder.append(retRegister).append(" = alloca ").append(type).append(" , align 8");
    GlobalData.out.println(builder.toString());
    return retRegister;
  }

  public static String addAlloca(String type,String name){
    StringBuilder builder = new StringBuilder("");
    String retRegister = "%"+name;
    GlobalData.Counter++;
    builder.append(retRegister).append(" = alloca ").append(type).append(" , align 8");
    GlobalData.out.println(builder.toString());
    return retRegister;
  }

  //getelementptr instruction
  public static String addGEPInstruction(String str){
    if(!GlobalData.strConsToRegister.containsKey(str))
      return null;
    StringBuilder builder = new StringBuilder("");
    String retRegister = "%"+GlobalData.Counter;
    GlobalData.Counter++;
    builder.append(retRegister).append(" = getelementptr inbounds [")
    .append(str.length()+1).append(" X i8], [").append(str.length()+1)
    .append(" X i8]* @globalstring").append(GlobalData.strConsToRegister.get(str))
    .append(" , i32 0 , i32 0");
    GlobalData.out.println(builder.toString());
    return retRegister;
  }

  public static String addGEPInstruction(String cl, String value, String attr){
    StringBuilder builder = new StringBuilder("");
    String retRegister = "%" + GlobalData.Counter;
    builder.append(retRegister).append(" = getelementptr inbounds ")
    .append(GlobalData.makeStructName(cl)).append(", ")
    .append(GlobalData.makeStructName(cl)).append("* ")
    .append(value).append(", i32 0");
    if(attr.equals("")){
      builder.append(", 0");
    }
    else{
	  // iterating through classes and checking if attr is contained or not
	  	String it = cl;
	  	while(!it.equals(GlobalData.Const.ROOT_TYPE)){
      		if(GlobalData.attrIndexMap.containsKey(mangledName(it, attr))){
      			builder.append(", i32 ").append(GlobalData.attrIndexMap.get(mangledName(it, attr)));
      			break;
    		}
    		builder.append(", i32 0");
    		// calling on parent of it
    		it = GlobalData.classTable.get(it);
  	  	}
    }
    GlobalData.Counter++;
    GlobalData.out.println(builder.toString());
    return retRegister;
  }

  //public static String addAttrGEP(String type,)

  //load instruction
  public static String addLoadInstruction(String type, String addr){
    StringBuilder builder = new StringBuilder("");
    String retRegister = "%"+GlobalData.Counter;
    GlobalData.Counter++;
    builder.append(retRegister).append(" = load ").append(type)
    .append(" , ").append(type).append("* ").append(addr).append(" , align ")
    .append(alignment(type));
    GlobalData.out.println(builder.toString());
    return retRegister;
  }

  //store instruction
  public static void addStoreInstruction(String type, String value, String addr){
    StringBuilder builder = new StringBuilder("store ");
    builder.append(type).append(" ").append(value).append(" , ")
    .append(type).append("* ").append(addr).append(" , align ")
    .append(alignment(type));
    GlobalData.out.println(builder.toString());
  }

  //store instruction for double pointers
  public static void addDPStoreInstruction(String type, String value, String addr){
    type = GlobalData.makeStructName(type);
    int align = alignment(type);
    type = type + "*";
    StringBuilder builder = new StringBuilder("store ");
    builder.append(type).append(" ").append(value).append(" , ")
    .append(type).append("* ").append(addr).append(" , align ")
    .append(align);
    GlobalData.out.println(builder.toString());
  }

  /* Conversion Instructions
  */
  public static boolean isPrim(String type){
    if(type.equals("i32") || type.equals("i64") || type.equals("i1") || type.equals("i8") || type.equals("i8*") )
      return true;
    return false;
  }

  public static String addConvertInstruction(String operation, String type1, String type2, String value){
    StringBuilder builder = new StringBuilder("");
    String retRegister = "%"+GlobalData.Counter;
    GlobalData.Counter++;
    if(!isPrim(type1))
      type1 = GlobalData.makeStructName(type1) + "*";
    if(!isPrim(type2))
      type2 = GlobalData.makeStructName(type2) + "*";
    builder.append(retRegister).append(" = ").append(operation)
    .append(" ").append(type1).append(" ")
    .append(value).append(" to ").append(type2);
    GlobalData.out.println(builder.toString());
    return retRegister;
  }

  /* Other IR Instructions
  */

  //icmp instruction
  public static String addIcmpInstruction(String cond, String type, String op1, String op2){
    StringBuilder builder = new StringBuilder("");
    String retRegister = "%"+GlobalData.Counter;
    if(!isPrim(type))
      type = GlobalData.makeStructName(type1) + "*";
    builder.append(retRegister).append(" = icmp ").append(cond)
    .append(type).append(" ").append(op1).append(", ").append(op2);
    GlobalData.out.println(builder.toString());
    return retRegister;
  }

  //call instruction
  public static String addCallInstruction(String type, String func, String args ){
    StringBuilder builder = new StringBuilder("call ");
    if(!type.equals("void"))
      type = GlobalData.makeClassTypeOrPointer(type);
    builder.append(type).append(" @").append(func).append("(")
    .append(args).append(")");
    String retRegister = "";
    if(!type.equals("void")){
      retRegister = "%" + GlobalData.Counter;
      builder.insert(0,retRegister+" ")
    }
    GlobalData.out.println(builder.toString());
    return retRegister;
  }
}
