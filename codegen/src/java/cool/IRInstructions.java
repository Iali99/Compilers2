package cool;
import java.util.*;
import java.io.PrintWriter;
public class IRInstructions{

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
    StringBuilder ir = new StringBuilder("ret ");
    ir.append(type);
    if(!type.equals("void"))
      ir.append(" ").append(value);
    GlobalData.out.println(ir.toString());
  }

  //break instruction for if else.
  public static void addBrInstruction(String cond,String trueLabel,String falseLabel){
    StringBuilder ir = new StringBuilder("br i1 ");
    ir.append(cond).append(" ,label ").append(trueLabel).append(" ,label ")
    .append(falseLabel);
    GlobalData.out.println(ir.toString());
  }

  //unconditional break instruction.
  public static void addBrInstruction(String label){
    StringBuilder ir = new StringBuilder("br label ");
    ir.append(label);
    GlobalData.out.println(ir.toString());
  }

  /* Binary Instructions.
  */

  //binary instruction based on arguments.
  public static String addBinaryInstruction(String operation,String type, String op1, String op2){
    StringBuilder ir = new StringBuilder("");
    type = GlobalData.makeClassTypeOrPointer(type);
    String retRegister = "%"+GlobalData.Counter;
    GlobalData.Counter++;
    ir.append(retRegister).append(" = ").append(operation).append(" nsw ")
    .append(type).append(" ").append(op1).append(" , ").append(op2);
    GlobalData.out.println(ir.toString());
    return retRegister;
  }

  /* Memory Operation Instructions.
  */
  //alloca instruction
  public static String addAlloca(String type){
    StringBuilder ir = new StringBuilder("");
    String retRegister = "%"+GlobalData.Counter;
    GlobalData.Counter++;
    type = GlobalData.makeClassTypeOrPointer(type);
    if(!isPrim(type)){
      type = type.substring(0,type.length()-1);
    }
    ir.append(retRegister).append(" = alloca ").append(type).append(" , align 8");
    GlobalData.out.println(ir.toString());
    return retRegister;
  }

  public static String addAlloca(String type,String name){
    StringBuilder ir = new StringBuilder("");
    String retRegister = "%"+name;
    GlobalData.Counter++;
    ir.append(retRegister).append(" = alloca ").append(type).append(" , align 8");
    GlobalData.out.println(ir.toString());
    return retRegister;
  }

  //getelementptr instruction
  public static String addGEPInstruction(String str){
    if(!GlobalData.strConsToRegister.containsKey(str))
      return null;
    StringBuilder ir = new StringBuilder("");
    String retRegister = "%"+GlobalData.Counter;
    GlobalData.Counter++;
    ir.append(retRegister).append(" = getelementptr inbounds [")
    .append(str.length()+1).append(" X i8], [").append(str.length()+1)
    .append(" X i8]* @globalstring").append(GlobalData.strConsToRegister.get(str))
    .append(" , i32 0 , i32 0");
    GlobalData.out.println(ir.toString());
    return retRegister;
  }

  public static String addGEPInstruction(String cl, String value, String attr){
    StringBuilder ir = new StringBuilder("");
    String retRegister = "%" + GlobalData.Counter;
    ir.append(retRegister).append(" = getelementptr inbounds ")
    .append(GlobalData.makeStructName(cl)).append(", ")
    .append(GlobalData.makeStructName(cl)).append("* ")
    .append(value).append(", i32 0");
    if(attr.equals("")){
      ir.append(", 0");
    }
    else{
	  // iterating through classes and checking if attr is contained or not
	  	String it = cl;
	  	while(!it.equals(GlobalData.Const.ROOT_TYPE)){
      		if(GlobalData.attrIndexMap.containsKey(GlobalData.mangledName(it, attr))){
      			ir.append(", i32 ").append(GlobalData.attrIndexMap.get(GlobalData.mangledName(it, attr)));
      			break;
    		}
    		ir.append(", i32 0");
    		// calling on parent of it
    		it = GlobalData.classTable.get(it);
  	  	}
    }
    GlobalData.Counter++;
    GlobalData.out.println(ir.toString());
    return retRegister;
  }

  //public static String addAttrGEP(String type,)

  //load instruction
  public static String addLoadInstruction(String type, String addr){
    StringBuilder ir = new StringBuilder("");
    String retRegister = "%"+GlobalData.Counter;
    GlobalData.Counter++;
    ir.append(retRegister).append(" = load ").append(type)
    .append(" , ").append(type).append("* ").append(addr).append(" , align ")
    .append(alignment(type));
    GlobalData.out.println(ir.toString());
    return retRegister;
  }

  //store instruction
  public static void addStoreInstruction(String type, String value, String addr){
    StringBuilder ir = new StringBuilder("store ");
    ir.append(type).append(" ").append(value).append(" , ")
    .append(type).append("* ").append(addr).append(" , align ")
    .append(alignment(type));
    GlobalData.out.println(ir.toString());
  }

  //store instruction for double pointers
  public static void addDPStoreInstruction(String type, String value, String addr){
    type = GlobalData.makeStructName(type);
    int align = alignment(type);
    type = type + "*";
    StringBuilder ir = new StringBuilder("store ");
    ir.append(type).append(" ").append(value).append(" , ")
    .append(type).append("* ").append(addr).append(" , align ")
    .append(align);
    GlobalData.out.println(ir.toString());
  }

  /* Conversion Instructions
  */
  public static boolean isPrim(String type){
    if(type.equals("i32") || type.equals("i64") || type.equals("i1") || type.equals("i8") || type.equals("i8*") )
      return true;
    return false;
  }

  public static String addConvertInstruction(String operation, String type1, String type2, String value){
    StringBuilder ir = new StringBuilder("");
    String retRegister = "%"+GlobalData.Counter;
    GlobalData.Counter++;
    type1 = GlobalData.makeClassTypeOrPointer(type1);
    type2 = GlobalData.makeClassTypeOrPointer(type2);
    ir.append(retRegister).append(" = ").append(operation)
    .append(" ").append(type1).append(" ")
    .append(value).append(" to ").append(type2);
    GlobalData.out.println(ir.toString());
    return retRegister;
  }

  /* Other IR Instructions
  */

  //icmp instruction
  public static String addIcmpInstruction(String cond, String type, String op1, String op2){
    StringBuilder ir = new StringBuilder("");
    String retRegister = "%"+GlobalData.Counter;
    // if(!isPrim(type))
    //   type = GlobalData.makeStructName(type1) + "*";
    ir.append(retRegister).append(" = icmp ").append(cond)
    .append(type).append(" ").append(op1).append(", ").append(op2);
    GlobalData.out.println(ir.toString());
    return retRegister;
  }

  //call instruction
  public static String addCallInstruction(String type, String func, String args ){
    StringBuilder ir = new StringBuilder("call ");
    if(!type.equals("void"))
      type = GlobalData.makeClassTypeOrPointer(type);
    ir.append(type).append(" @").append(func).append("(")
    .append(args).append(")");
    String retRegister = "";
    if(!type.equals("void")){
      retRegister = "%" + GlobalData.Counter;
      ir.insert(0,retRegister+" ");
    }
    GlobalData.out.println(ir.toString());
    return retRegister;
  }

  //add label for division by 0 error
  public static void add0ErrorLabel(){
    StringBuilder ir = new StringBuilder("divide0true:\n");
    ir.append("call void @reportError0()\n")
    .append("call void @exit(i32 1)\n");
    GlobalData.out.println(ir.toString());
  }

  //add label for dispatch on void error
  public static void addVoidErrorLabel(){
    StringBuilder ir = new StringBuilder("voidTrue:\n");
    ir.append("call void @reportErrorVoid()\n")
    .append("call void @exit(i32 1)\n");
    GlobalData.out.println(ir.toString());
  }

  /* Constructor Instructions
  */

  public static void callConstructorInstruction(String cl, String args){
    StringBuilder a = new StringBuilder("");
    a.append(GlobalData.makeClassTypeOrPointer(cl)).append(" ").append(args);
    String r = IRInstructions.addCallInstruction("void",GlobalData.mangledName(cl,cl),a.toString());
  }
}
