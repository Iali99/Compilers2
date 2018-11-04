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
    type = GlobalData.getTypeOrPtr(type);
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
  public static String addStrGEP(String str){
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
}
