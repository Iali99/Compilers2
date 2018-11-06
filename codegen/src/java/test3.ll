; I am a comment in LLVM-IR. Feel free to remove me.
declare i32 @printf(i8*, ...)


declare i32 @scanf(i8*, ...)


declare void @exit(i32)


declare i8* @malloc(i64)


declare i64 @strlen(i8*)


declare i32 @strcmp(i8*, i8*)


declare i8* @strcat(i8*, i8*)


declare i8* @strcpy(i8*, i8*)


declare i8* @strncpy(i8*, i8*, i32)



; Struct declarations
%class.Object = type {i8*}
%class.A = type { %class.Object}
%class.B = type { %class.Object}
%class.IO = type { %class.Object}
%class.Main = type { %class.Object}

define i32 @A$f1$(%class.A* %this, i8 %x, i8 %y){

divide0true:
call void @reportError0()
call void @exit(i32 1)


voidTrue:
call void @reportErrorVoid()
call void @exit(i32 1)


entry:
%%x.a_ = alloca i8 , align 8
store i8 %x , i8* %x.a_ , align 4
%%y.a_ = alloca i8 , align 8
store i8 %y , i8* %y.a_ , align 4
%2 = load i8 , i8* %x.a_ , align 4
%3 = alloca i32 , align 8
br i1 %2 ,label %ifBody0 ,label %elseBody0

ifBody0:
store i32 0 , i32* %3 , align 4
br label %ifEnd0
elseBody0:
store i32 0 , i32* %3 , align 4
br label %ifEnd0
ifEnd0:
%4 = load i8 , i8* %y.a_ , align 4
%5 = alloca i32 , align 8
br i1 %4 ,label %ifBody1 ,label %elseBody1

ifBody1:
store i32 0 , i32* %5 , align 4
br label %ifEnd1
elseBody1:
store i32 0 , i32* %5 , align 4
br label %ifEnd1
ifEnd1:
ret i32 0
}

define i32 @B$f2$(%class.B* %this, i8 %x, i8 %y, i32 %z){

divide0true:
call void @reportError0()
call void @exit(i32 1)


voidTrue:
call void @reportErrorVoid()
call void @exit(i32 1)


entry:
%%x.a_ = alloca i8 , align 8
store i8 %x , i8* %x.a_ , align 4
%%y.a_ = alloca i8 , align 8
store i8 %y , i8* %y.a_ , align 4
%%z.a_ = alloca i32 , align 8
store i32 %z , i32* %z.a_ , align 4
br label %loopCond0

loopCond0:

%3 = load i8 , i8* %x.a_ , align 4
br i1 %3 ,label %loopBody0 ,label %loopEnd0

loopBody0:
%4 = load i32 , i32* %z.a_ , align 4
%5 = add nsw i32 %4 , 1
store i32 %5 , i32* %z.a_ , align 4
br label %loopCond0

loopEnd0:
ret i32 0
}

define i32 @Main$main$(%class.Main* %this){

divide0true:
call void @reportError0()
call void @exit(i32 1)


voidTrue:
call void @reportErrorVoid()
call void @exit(i32 1)


entry:
ret i32 0
}

define void @Object$Object$(%class.Object* %this) {
entry:
ret void 
}

define void @A$A$(%class.A* %this){
entry:
%0 = bitcast %class.A* %this to %class.Object*
call void @Object$Object$(%class.Object* %0)
ret void 
}

define void @B$B$(%class.B* %this){
entry:
%1 = bitcast %class.B* %this to %class.Object*
call void @Object$Object$(%class.Object* %1)
ret void 
}

define void @Bool$Bool$(%class.Bool* %this){
entry:
%2 = bitcast i8 %this to %class.Object*
call void @Object$Object$(%class.Object* %2)
ret void 
}

define void @IO$IO$(%class.IO* %this){
entry:
%3 = bitcast %class.IO* %this to %class.Object*
call void @Object$Object$(%class.Object* %3)
ret void 
}

define void @String$String$(%class.String* %this){
entry:
%4 = bitcast i8* %this to %class.Object*
call void @Object$Object$(%class.Object* %4)
ret void 
}

define void @Main$Main$(%class.Main* %this){
entry:
%5 = bitcast %class.Main* %this to %class.Object*
call void @Object$Object$(%class.Object* %5)
ret void 
}

define void @Int$Int$(%class.Int* %this){
entry:
%6 = bitcast i32 %this to %class.Object*
call void @Object$Object$(%class.Object* %6)
ret void 
}

define %class.Object* @Object$abort$(%class.Object* %this) {
entry:
%0 = getelementptr inbounds %class.Object, %class.Object* %this, i32 0, i32 0
%1 = load i8*, i8** %0, align 8
%2 = getelementptr inbounds [3 x i8], [3 x i8]* @globalstring6, i32 0, i32 0
%3 = getelementptr inbounds [26 x i8], [26 x i8]* @globalstring16, i32 0, i32 0
%4 = call i32 (i8*, ...) @printf(i8* %2, i8* %3)
%5 = call i32 (i8*, ...) @printf(i8* %2, i8* %1)
%6 = getelementptr inbounds [2 x i8], [2 x i8]* @globalstring7, i32 0, i32 0
%7 = call i32 (i8*, ...) @printf(i8* %2, i8* %6)
call void @exit(i32 0)
%8 = call noalias i8* @malloc(i64 0)
%9 = bitcast i8* %8 to %class.Object*
call void @Object$Object$(%class.Object* %9)
ret %class.Object* %9
}

define i8* @Object$type_name$(%class.Object* %this) {
entry:
%0 = getelementptr inbounds %class.Object, %class.Object* %this, i32 0, i32 0
%1 = load i8*, i8** %0, align 8
ret i8* %1
}

define %class.IO* @IO$out_string$(%class.IO* %this, i8* %s) {
entry:
%0 = getelementptr inbounds [3 x i8], [3 x i8]* @globalstring6, i32 0, i32 0
%call = call i32 (i8*, ...) @printf(i8* %0, i8* %s)
%1 = call noalias i8* @malloc(i64 8)
%2 = bitcast i8* %1 to %class.IO*
call void @IO$IO$(%class.IO* %2)
ret %class.IO* %2
}

define %class.IO* @IO$out_int$(%class.IO* %this, i32 %d) {
entry:
%0 = getelementptr inbounds [3 x i8], [3 x i8]* @globalstring4, i32 0, i32 0
%call = call i32 (i8*, ...) @printf(i8* %0, i32 %d)
%1 = call noalias i8* @malloc(i64 8)
%2 = bitcast i8* %1 to %class.IO*
call void @IO$IO$(%class.IO* %2)
ret %class.IO* %2
}

define i32 @IO$in_int$(%class.IO* %this) {
entry:
%0 = alloca i32, align 8
%1 = getelementptr inbounds [3 x i8], [3 x i8]* @globalstring4, i32 0, i32 0
%call = call i32 (i8*, ...) @scanf(i8* %1, i32* %0)
%2 = load i32, i32* %0, align 4
ret i32 %2
}

define i8* @IO$in_string$(%class.IO* %this) {
entry:
%0 = alloca i8*, align 8
%1 = getelementptr inbounds [10 x i8], [10 x i8]*@globalstring3, i32 0, i32 0
%2 = load i8*, i8** %0, align 8
%call = call i32 (i8*, ...) @scanf(i8* %1, i8* %2)
%3 = load i8*, i8** %0, align 8
ret i8* %3
}

define i8* @String$concat$(i8* %s1, i8* %s2) {
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

define i8* @String$substr$(i8* %s1, i32 %index, i32 %len) {
entry:
%0 = zext i32 %len to i64
%1 = call noalias i8* @malloc(i64 %0)
%2 = getelementptr inbounds i8, i8* %s1, i32 %index
%3 = call i8* @strncpy(i8* %1, i8* %2, i64 %0)
ret i8* %1
}

define void @reportError0(){
entry:
%7 = getelementptr inbounds [42 X i8], [42 X i8]* @globalstring14 , i32 0 , i32 0
%8 = getelementptr inbounds [3 X i8], [3 X i8]* @globalstring6 , i32 0 , i32 0
%r = call i32 (i8*, ...) @printf(i8* %8, i8* %7)
ret void
}

define void @reportErrorVoid(){
entry:
%9 = getelementptr inbounds [45 X i8], [45 X i8]* @globalstring15 , i32 0 , i32 0
%10 = getelementptr inbounds [3 X i8], [3 X i8]* @globalstring6 , i32 0 , i32 0
%r = call i32 (i8*, ...) @printf(i8* %10, i8* %9)
ret void
}


@globalstring8 = private unnamed_addr constant [1 x i8] c"\00", align 1
@globalstring15 = private unnamed_addr constant [45 x i8] c"
Error : Dispatch on void is not permissible\00", align 1
@globalstring0 = private unnamed_addr constant [2 x i8] c"A\00", align 1
@globalstring1 = private unnamed_addr constant [2 x i8] c"B\00", align 1
@globalstring13 = private unnamed_addr constant [3 x i8] c"IO\00", align 1
@globalstring16 = private unnamed_addr constant [26 x i8] c"
Abort called from class \00", align 1
@globalstring7 = private unnamed_addr constant [2 x i8] c"
\00", align 1
@globalstring5 = private unnamed_addr constant [4 x i8] c"%d
\00", align 1
@globalstring11 = private unnamed_addr constant [7 x i8] c"String\00", align 1
@globalstring6 = private unnamed_addr constant [3 x i8] c"%s\00", align 1
@globalstring9 = private unnamed_addr constant [4 x i8] c"Int\00", align 1
@globalstring10 = private unnamed_addr constant [5 x i8] c"Bool\00", align 1
@globalstring14 = private unnamed_addr constant [42 x i8] c"
Error : Division by 0 is not permissible\00", align 1
@globalstring12 = private unnamed_addr constant [7 x i8] c"Object\00", align 1
@globalstring2 = private unnamed_addr constant [5 x i8] c"Main\00", align 1
@globalstring3 = private unnamed_addr constant [10 x i8] c"%1024[^
]\00", align 1
@globalstring4 = private unnamed_addr constant [3 x i8] c"%d\00", align 1

