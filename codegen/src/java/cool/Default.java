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

  public static void visitDefaultMethods(){
    GlobalData.loopCounter = 0;
    // = "%class.Object*"
    String ROOT_REG = GlobalData.makeClassTypeOrPointer(GlobalData.makeStructName(GlobalData.Const.ROOT_TYPE));
    GlobalData.out.println("define "+ ROOT_REG +" @_"+ GlobalData.mangledName(GlobalData.Const.ROOT_TYPE, "abort") +"("+ ROOT_REG +"%this) {");
    GlobalData.out.println("entry:");
    GlobalData.out.println("%0 = getelementptr inbounds %class.Object, %class.Object* %this, i32 0, i32 0")
    GlobalData.out.println("%1 = load i8*, i8** %0, align 8");
    GlobalData.loopCounter = 2;
    String arg1 = IRPrinter.createStringGEP("%s");
    String arg2 = IRPrinter.createStringGEP(Global.Constants.ABORT_MESSAGE);
    Global.out.println(IRPrinter.INDENT+"%"+Global.registerCounter+" = call i32 (i8*, ...) @printf(i8* "+arg1+", i8* "+arg2+")");
    Global.registerCounter++;
    Global.out.println(IRPrinter.INDENT+"%"+Global.registerCounter+" = call i32 (i8*, ...) @printf(i8* "+arg1+", i8* "+loadNameReg+")");
    Global.registerCounter++;
    arg2 = IRPrinter.createStringGEP("\n");
    Global.out.println(IRPrinter.INDENT+"%"+Global.registerCounter+" = call i32 (i8*, ...) @printf(i8* "+arg1+", i8* "+arg2+")");
    Global.registerCounter++;
  %3 = getelementptr inbounds [26 x i8], [26 x i8]* @.str.11, i32 0, i32 0
  %4 = call i32 (i8*, ...) @printf(i8* %2, i8* %3)
  %5 = call i32 (i8*, ...) @printf(i8* %2, i8* %1)
  %6 = getelementptr inbounds [2 x i8], [2 x i8]* @.str.4, i32 0, i32 0
  %7 = call i32 (i8*, ...) @printf(i8* %2, i8* %6)
  call void @exit(i32 0)
  %8 = call noalias i8* @malloc(i64 0)
  %9 = bitcast i8* %8 to %class.Object*
  call void @_CN6Object_FN6Object_(%class.Object* %9)
  ret %class.Object* %9
}

; Class: Object, Method: type_name
define i8* @_CN6Object_FN9type_name_(%class.Object* %this) {
entry:
  %0 = getelementptr inbounds %class.Object, %class.Object* %this, i32 0, i32 0
  %1 = load i8*, i8** %0, align 8
  ret i8* %1
}

; Class: IO, Method: out_string
define %class.IO* @_CN2IO_FN10out_string_(%class.IO* %this, i8* %s) {
entry:
  %0 = getelementptr inbounds [3 x i8], [3 x i8]* @.str.5, i32 0, i32 0
  %call = call i32 (i8*, ...) @printf(i8* %0, i8* %s)
  %1 = call noalias i8* @malloc(i64 8)
  %2 = bitcast i8* %1 to %class.IO*
  call void @_CN2IO_FN2IO_(%class.IO* %2)
  ret %class.IO* %2
}

; Class: IO, Method: out_int
define %class.IO* @_CN2IO_FN7out_int_(%class.IO* %this, i32 %d) {
entry:
  %0 = getelementptr inbounds [3 x i8], [3 x i8]* @.str.7, i32 0, i32 0
  %call = call i32 (i8*, ...) @printf(i8* %0, i32 %d)
  %1 = call noalias i8* @malloc(i64 8)
  %2 = bitcast i8* %1 to %class.IO*
  call void @_CN2IO_FN2IO_(%class.IO* %2)
  ret %class.IO* %2
}

; Class: IO, Method: in_int
define i32 @_CN2IO_FN6in_int_(%class.IO* %this) {
entry:
  %0 = alloca i32, align 8
  %1 = getelementptr inbounds [3 x i8], [3 x i8]* @.str.7, i32 0, i32 0
  %call = call i32 (i8*, ...) @scanf(i8* %1, i32* %0)
  %2 = load i32, i32* %0, align 4
  ret i32 %2
}

; Class: IO, Method: in_string
define i8* @_CN2IO_FN9in_string_(%class.IO* %this) {
entry:
  %0 = alloca i8*, align 8
  %1 = getelementptr inbounds [10 x i8], [10 x i8]* @.str.6, i32 0, i32 0
  %2 = load i8*, i8** %0, align 8
  %call = call i32 (i8*, ...) @scanf(i8* %1, i8* %2)
  %3 = load i8*, i8** %0, align 8
  ret i8* %3
}

; Class: String, Method: concat
define i8* @_CN6String_FN6concat_(i8* %s1, i8* %s2) {
entry:
  %0 = call i64 @strlen(i8* %s1)
  %1 = call i64 @strlen(i8* %s2)
  %2 = add nsw i64 %0, %1
  %3 = add nsw i64 %2, 1
  %4 = call noalias i8* @malloc(i64 %3)
  %5 = call i8* @strcpy(i8* %4, i8* %s1)
  %6 = call i8* @strcat(i8* %4, i8* %s2)
  ret i8* %4
}

; Class: String, Method: substr
define i8* @_CN6String_FN6substr_(i8* %s1, i32 %index, i32 %len) {
entry:
  %0 = zext i32 %len to i64
  %1 = call noalias i8* @malloc(i64 %0)
  %2 = getelementptr inbounds i8, i8* %s1, i32 %index
  %3 = call i8* @strncpy(i8* %1, i8* %2, i64 %0)
  ret i8* %1
}
  }

  public static void addDefaultStrings(){
    if(!GlobalData.strConsToRegister.containsKey("Int")){
      GlobalData.strConsToRegister.put("Int", GlobalData.strCounter);
      GlobalData.strCounter++;
    }
    if(!GlobalData.strConsToRegister.containsKey("Bool")){
      GlobalData.strConsToRegister.put("Bool", GlobalData.strCounter);
      GlobalData.strCounter++;
    }
    if(!GlobalData.strConsToRegister.containsKey("String")){
      GlobalData.strConsToRegister.put("String", GlobalData.strCounter);
      GlobalData.strCounter++;
    }
    if(!GlobalData.strConsToRegister.containsKey("Object")){
      GlobalData.strConsToRegister.put("Object", GlobalData.strCounter);
      GlobalData.strCounter++;
    }
    if(!GlobalData.strConsToRegister.containsKey("IO")){
      GlobalData.strConsToRegister.put("IO", GlobalData.strCounter);
      GlobalData.strCounter++;
    }
    if(!GlobalData.strConsToRegister.containsKey("Main")){
      GlobalData.strConsToRegister.put("Main", GlobalData.strCounter);
      GlobalData.strCounter++;
    }
  }
}
