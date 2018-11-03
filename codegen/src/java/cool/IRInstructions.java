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

  
}
