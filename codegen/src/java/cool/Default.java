package cool;

public class Default{

  public static void addMethods(){

  }

  public static void setDefualtClassSizes(){
    GlobalData.classNameToSize.put("Object",0);
    GlobalData.classNameToSize.put("IO",0);
    GlobalData.classNameToSize.put("Int",4);
    GlobalData.classNameToSize.put("Bool",1);
    GlobalData.classNameToSize.put("String",8);
  }

  /*public static void setDefaultMethodNames(){

  }*/

  public static void addDefaultStrings(){
    if(!GlobalData.strConsToRegister.containsKey("Int")){
      GlobalData.strConsToRegister.put("Int",strCounter);
      strCounter++;
    }
    if(!GlobalData.strConsToRegister.containsKey("Bool")){
      GlobalData.strConsToRegister.put("Bool",strCounter);
      strCounter++;
    }
    if(!GlobalData.strConsToRegister.containsKey("String")){
      GlobalData.strConsToRegister.put("String",strCounter);
      strCounter++;
    }
    if(!GlobalData.strConsToRegister.containsKey("Object")){
      GlobalData.strConsToRegister.put("Object",strCounter);
      strCounter++;
    }
    if(!GlobalData.strConsToRegister.containsKey("IO")){
      GlobalData.strConsToRegister.put("IO",strCounter);
      strCounter++;
    }
    if(!GlobalData.strConsToRegister.containsKey("Main")){
      GlobalData.strConsToRegister.put("Main",strCounter);
      strCounter++;
    }

  }
}
