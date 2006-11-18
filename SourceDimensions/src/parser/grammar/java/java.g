%Options lalr=2,prefix=TK_J_,names=MAX,error-maps,noread-reduce,table=space,generate-parser=C++,states,list,nt-prefix=Nt_J_,

%Terminals
	ID

	INT_LITERAL
	FLOAT_LITERAL
	CHAR_LITERAL
	STR_LITERAL

	LPAREN			
	RPAREN			
	COMMA				
	LBRACK			
	RBRACK			
	LBRACE			
	RBRACE			
	EQ			 	
	SEMIC				
	COLON				
	QUESTION			
	DOT				
	PLUS				
	MINUS				
	STAR				
	SLASH				
	PERCENT
	HAT				
	AND				
	OR			 	
	TILDE				
	NOT				
	PLUS_EQ			
	MINUS_EQ			
	STAR_EQ			
	SLASH_EQ			
	PERCENT_EQ
	HAT_EQ			
	AND_EQ			
	OR_EQ				
	LTLT				
	GTGT				
	GTGTGT			
	GTGT_EQ			
	LTLT_EQ			
	GTGTGT_EQ			
	EQEQ				
	NE			 	
	LE			 	
	GE			 	
	LANGLE			
	RANGLE			
	ANDAND			
	OROR				
	INCR				
	DECR				

	ABSTRACT
	ASSERT
	BOOLEAN
	BREAK
	BYTE
	CASE
	CATCH
	CHAR
	CLASS
	CONTINUE
	DEFAULT
	DO
	DOUBLE
	ELSE
	EXTENDS
	FALSE
	FINAL
	FINALLY
	FLOAT
	FOR
	IF
	IMPLEMENTS
	IMPORT
	INSTANCEOF
	INT
	INTERFACE
	LONG
	NATIVE
	NEW
	NULL
	PACKAGE
	PRIVATE
	PROTECTED
	PUBLIC
	RETURN
	SHORT
	STATIC
	STRICTFP
	SUPER
	SWITCH
	SYNCHRONIZED
	THIS
	THROW
	THROWS
	TRANSIENT
	TRUE
	TRY
	VOID
	VOLATILE
	WHILE

	END_OF_FILE


%Alias
	'(' ::= LPAREN			
	')' ::= RPAREN			
	',' ::= COMMA				
	'[' ::= LBRACK			
	']' ::= RBRACK			
	'{' ::= LBRACE			
	'}' ::= RBRACE			
	'=' ::= EQ			 	
	';' ::= SEMIC				
	':' ::= COLON				
	'?' ::= QUESTION			
	'.' ::= DOT				
	'+' ::= PLUS				
	'-' ::= MINUS				
	'*' ::= STAR				
	'/' ::= SLASH				
	'%' ::= PERCENT
	'^' ::= HAT				
	'&' ::= AND				
	'|' ::= OR			 	
	'~' ::= TILDE				
	'!' ::= NOT				
	'+=' ::= PLUS_EQ			
	'-=' ::= MINUS_EQ			
	'*=' ::= STAR_EQ			
	'/=' ::= SLASH_EQ			
	'%=' ::= PERCENT_EQ
	'^=' ::= HAT_EQ			
	'&=' ::= AND_EQ			
	'|=' ::= OR_EQ				
	'<<' ::= LTLT				
	'>>' ::= GTGT				
	'>>>' ::= GTGTGT			
	'>>=' ::= GTGT_EQ			
	'<<=' ::= LTLT_EQ			
	'>>>=' ::= GTGTGT_EQ			
	'==' ::= EQEQ				
	'!=' ::= NE			 	
	'<=' ::= LE			 	
	'>=' ::= GE			 	
	'<' ::= LANGLE			
	'>' ::= RANGLE			
	'&&' ::= ANDAND			
	'||' ::= OROR				
	'++' ::= INCR				
	'--' ::= DECR				
	%EOF ::= END_OF_FILE


%Rules

-- *** Start symbol ***	
CompUnit ::=
  %empty                         |
                       TypeDecls |
              ImpDecls           |
              ImpDecls TypeDecls |
  PackageDecl                    |
  PackageDecl          TypeDecls |
  PackageDecl ImpDecls           |
  PackageDecl ImpDecls TypeDecls                
	
	
-- *** Types, values and variables ***

Type ::=
  PrimitiveType |
  RefType
 
PrimitiveType ::=
  NumType |
  BOOLEAN
 
NumType ::=
  IntType |
  FloatPtType
 
IntType ::=
  BYTE  |
  SHORT |
  INT   |
  LONG  |
  CHAR
 
FloatPtType ::=
  FLOAT |
  DOUBLE
 
RefType ::=
  ClsIntfType |
  ArrType
 
ClsIntfType ::= QName
 
ClsType ::= QName
 
IntfType ::= QName
 
ArrType ::= 
  ClsIntfType Dims |
  PrimitiveType Dims

Modifiers ::=
  Modifier |
  Modifiers Modifier
 
Modifier ::=
  PUBLIC       |
  PROTECTED    |
  PRIVATE      |
  STATIC       |
  ABSTRACT     |
  FINAL        |
  NATIVE       |
  SYNCHRONIZED |
  TRANSIENT    |
  VOLATILE     |
  STRICTFP
 
 
-- *** Names ***

QName ::=
	ID |
	QName '.' ID
	
 
-- *** Packages ***
ImpDecls ::=
  ImpDecl |
  ImpDecls ImpDecl
 
TypeDecls ::=
  TypeDecl |
  TypeDecls TypeDecl
 
PackageDecl ::= PACKAGE QName ';'
 
ImpDecl ::=
  SingleTypeImpDecl |
  TypeImpOnDemandDecl
 
SingleTypeImpDecl ::= IMPORT QName ';'
 
TypeImpOnDemandDecl ::= IMPORT QName '.' '*' ';'
 
TypeDecl ::=
  ClsDecl  |
  IntfDecl |
  ';'

 
-- *** Classes ***

ClsDecl ::= 
            CLASS ID                ClsBody |
            CLASS ID          Intfs ClsBody |
            CLASS ID SuperCls       ClsBody |
            CLASS ID SuperCls Intfs ClsBody |
  Modifiers CLASS ID                ClsBody |
  Modifiers CLASS ID          Intfs ClsBody |
  Modifiers CLASS ID SuperCls       ClsBody |
  Modifiers CLASS ID SuperCls Intfs ClsBody
 
SuperCls ::= EXTENDS ClsType
 
Intfs ::= IMPLEMENTS IntfTypeList
 
IntfTypeList ::=
  IntfType |
  IntfTypeList ',' IntfType
 
ClsBody ::= 
  '{' ClsBodyDecls '}' |
  '{'                '}'
 
ClsBodyDecls ::=
  ClsBodyDecl |
  ClsBodyDecls ClsBodyDecl
 
ClsBodyDecl ::=
  ClsMemDecl |
  InstInit   |
  StaticInit |
  ConstrDecl
 
ClsMemDecl ::=
  FldDecl  |
  MethodDecl |
  ClsDecl    |                                       
  IntfDecl   |
  ';'
 
FldDecl ::= 
  Modifiers Type VarDclrs ';' |
            Type VarDclrs ';'
 
VarDclrId ::=
  ID |
  VarDclrId Dim
 
VarInit ::=
  Expr |
  ArrInit
 
MethodDecl ::= MethodHdr MethodBody
 
MethodHdr ::=
            ResultType MethodDclr        |
            ResultType MethodDclr Throws |
  Modifiers ResultType MethodDclr        |
  Modifiers ResultType MethodDclr Throws
  
 
ResultType ::=
  Type |
  VOID
 
MethodDclr ::=
  ID '(' FormalParamList ')' |
  ID '('                 ')' |
  MethodDclr Dim
 
FormalParamList ::=
  FormalParam |
  FormalParamList ',' FormalParam
 
FormalParam ::=
  Modifiers Type VarDclrId |
            Type VarDclrId 
 
Throws ::= THROWS ClsTypeList
 
ClsTypeList ::=
  ClsType |
  ClsTypeList ',' ClsType
 
MethodBody ::=
  Blk | 
  ';'
  
InstInit ::= Blk
 
StaticInit ::= STATIC Blk
 
ConstrDecl ::=
            ConstrDclr        ConstrBody |
            ConstrDclr Throws ConstrBody |
  Modifiers ConstrDclr        ConstrBody |
  Modifiers ConstrDclr Throws ConstrBody
 
ConstrDclr ::=
  ID '(' FormalParamList ')' |
  ID '('                 ')'
   
ConstrBody ::=
  '{'                              '}' |
  '{'                     BlkStmts '}' |
  '{' ExplicitConstrInvoc          '}' |
  '{' ExplicitConstrInvoc BlkStmts '}'
 
ExplicitConstrInvoc ::=
  THIS '(' ArgList ')' ';'              |
  THIS '('         ')' ';'              |
  Super '(' ArgList ')' ';'             |
  Super '('         ')' ';'             |
  Primary '.' Super '(' ArgList ')' ';' |
  Primary '.' Super '('         ')' ';'
 
 
-- *** Interfaces ***

IntfDecl ::=
            INTERFACE ID              IntfBody |
            INTERFACE ID ExtendsIntfs IntfBody |
  Modifiers INTERFACE ID              IntfBody |
  Modifiers INTERFACE ID ExtendsIntfs IntfBody
 
ExtendsIntfs ::=
  EXTENDS IntfType |
  ExtendsIntfs ',' IntfType
 
IntfBody ::=
  '{' IntfMemDecls '}' |
  '{'              '}'
 
IntfMemDecls ::=
  IntfMemDecl |
  IntfMemDecls IntfMemDecl
 
IntfMemDecl ::=
  ConstDecl       |
  AbstrMethodDecl |
  ClsDecl         |
  IntfDecl        |
  ';'       
 
ConstDecl ::=
  Modifiers Type VarDclrs ';' |
            Type VarDclrs ';'
 
AbstrMethodDecl ::=
            ResultType MethodDclr        ';' |
            ResultType MethodDclr Throws ';' |
  Modifiers ResultType MethodDclr        ';' |
  Modifiers ResultType MethodDclr Throws ';'
 

-- *** Arrays ***

ArrInit ::=
  '{'              '}' |
  '{'          ',' '}' |
  '{' VarInits     '}' |
  '{' VarInits ',' '}'
 
VarInits ::=
  VarInit |
  VarInits ',' VarInit
 
 
-- *** Blocks and statements ***

Blk ::=
  '{' BlkStmts '}' |
  '{'          '}'
 
BlkStmts ::=
  BlkStmt |
  BlkStmts BlkStmt
 
BlkStmt ::=
  LocalVarDeclStmt |
  ClsDecl          |
  Stmt
 
LocalVarDeclStmt ::= LocalVarDecl ';'
 
LocalVarDecl ::=
  Modifiers Type VarDclrs |
            Type VarDclrs
 
VarDclrs ::=
  VarDclr |
  VarDclrs ',' VarDclr
 
VarDclr ::=
  VarDclrId |
  VarDclrId '=' VarInit
 
Stmt ::=
  Blk          |
  EmptyStmt    |
  AssertStmt   |
  ExprStmt     |
  SwitchStmt   | 
  DoStmt       |
  BreakStmt    |
  ContinueStmt |
  ReturnStmt   |
  SyncStmt     |
  ThrowStmt    |
  TryStmt      |
  LblStmt      |
  IfThenStmt   |
  WhileStmt    |
  ForStmt
 
IfThenStmt ::= 
  IF '(' Expr ')' Stmt |
  IF '(' Expr ')' Stmt ELSE Stmt
 
EmptyStmt ::= ';'

AssertStmt ::= 
  ASSERT Expr ';' |
  ASSERT Expr ':' Expr ';'
 
LblStmt ::= ID ':' Stmt
 
ExprStmt ::= StmtExpr ';'
 
StmtExpr ::=
  Assignment   |
  PreIncrExpr  |
  PreDecrExpr  |
  PostIncrExpr |
  PostDecrExpr |
  MethodInvoc  |
  InstCreatExpr
 
SwitchStmt ::= SWITCH '(' Expr ')' SwitchBlk
 
SwitchBlk ::= 
  '{'                             '}' |
  '{'                  SwitchLbls '}' |
  '{' SwitchBlkStmtGrs            '}' |
  '{' SwitchBlkStmtGrs SwitchLbls '}'
 
SwitchBlkStmtGrs ::=
  SwitchBlkStmtGr |
  SwitchBlkStmtGrs SwitchBlkStmtGr
 
SwitchBlkStmtGr ::= SwitchLbls BlkStmts
 
SwitchLbls ::=
  SwitchLbl |
  SwitchLbls SwitchLbl
 
SwitchLbl ::=
  CASE ConstExpr ':' |
  DEFAULT ':'
   
WhileStmt ::= WHILE '(' Expr ')' Stmt
 
DoStmt ::= DO Stmt WHILE '(' Expr ')' ';'
 
ForStmt ::=
  FOR '('         ';'            ';'           ')' Stmt |
  FOR '('         ';'            ';' ForUpdate ')' Stmt |
  FOR '('         ';' Expr ';'           ')' Stmt       |
  FOR '('         ';' Expr ';' ForUpdate ')' Stmt       |      
  FOR '(' ForInit ';'            ';'           ')' Stmt |
  FOR '(' ForInit ';'            ';' ForUpdate ')' Stmt |  
  FOR '(' ForInit ';' Expr ';'           ')' Stmt       |
  FOR '(' ForInit ';' Expr ';' ForUpdate ')' Stmt
 
ForInit ::=
  StmtExprList |
  LocalVarDecl
 
ForUpdate ::= StmtExprList
 
StmtExprList ::=
  StmtExpr |
  StmtExprList ',' StmtExpr
 
BreakStmt ::=
  BREAK ID ';' |
  BREAK    ';'   

ContinueStmt ::=
  CONTINUE ID ';' |
  CONTINUE    ';'
 
ReturnStmt ::=
  RETURN Expr ';' |
  RETURN      ';'
 
ThrowStmt ::= THROW Expr ';'
 
SyncStmt ::= SYNCHRONIZED '(' Expr ')' Blk
 
TryStmt ::=
  TRY Blk Catches         |
  TRY Blk Catches Finally |
  TRY Blk Finally
 
Catches ::=
  Catch |
  Catches Catch
 
Catch ::= CATCH '(' FormalParam ')' Blk
 
Finally ::= FINALLY Blk
 

-- *** Expressions ***

Primary ::=
  PrimaryNoNewArr |
  ArrCreatExpr
 
PrimaryNoNewArr ::=
  Literal          |
  THIS             |
  ParenExpr        |
  InstCreatExpr    |
  FldAccess        |
  MethodInvoc      |
  ArrAccess
 
Literal ::= 
  INT_LITERAL   |
  FLOAT_LITERAL |
  CHAR_LITERAL  |
  STR_LITERAL   |
  NULL          |
  TRUE          |
  FALSE
  
ParenExpr ::= '(' Expr ')'  
 
InstCreatExpr ::=
  NEW ClsIntfType '('         ')'            |
  NEW ClsIntfType '('         ')' ClsBody    |
  NEW ClsIntfType '(' ArgList ')'            |
  NEW ClsIntfType '(' ArgList ')' ClsBody    |
  Primary '.' NEW ID '('         ')'         | 
  Primary '.' NEW ID '('         ')' ClsBody | 
  Primary '.' NEW ID '(' ArgList ')'         |   
  Primary '.' NEW ID '(' ArgList ')' ClsBody |
  QName '.' NEW ID '('         ')'          | 
  QName '.' NEW ID '('         ')' ClsBody  | 
  QName '.' NEW ID '(' ArgList ')'          |   
  QName '.' NEW ID '(' ArgList ')' ClsBody
 
ArgList ::=
  Expr |
  ArgList ',' Expr
 
ArrCreatExpr ::=
  NEW PrimitiveType DimExprs      | 
  NEW PrimitiveType DimExprs Dims |
  NEW QName DimExprs             |
  NEW QName DimExprs Dims        |
  NEW PrimitiveType Dims ArrInit  |
  NEW QName Dims ArrInit
  
DimExprs ::=
  DimExpr |
  DimExprs DimExpr
 
DimExpr ::= '[' Expr ']'
 
Dims ::=
  Dim |
  Dims Dim
  
Dim ::= '[' ']'
 
FldAccess ::= 
  ClsType '.' THIS | 
  Type '.' Class   |
  VOID '.' Class   |
  Primary '.' ID   |
  Super '.' ID     |
  QName '.' Super '.' ID
 
Class ::= CLASS

Super ::= SUPER
 
MethodInvoc ::=
  QName '('         ')'                  |
  QName '(' ArgList ')'                  |
  Primary '.' ID '('         ')'          |
  Primary '.' ID '(' ArgList ')'          |
  Super '.' ID '('         ')'            |
  Super '.' ID '(' ArgList ')'            |
  QName '.' Super '.' ID '(' ArgList ')' |
  QName '.' Super '.' ID '('         ')'
 
ArrAccess ::=
  QName '[' Expr ']' |
  PrimaryNoNewArr '[' Expr ']'
 
PostfixExpr ::= 
  Primary      |
  QName       |
  PostIncrExpr |
  PostDecrExpr
 
PostIncrExpr ::= PostfixExpr '++'
 
PostDecrExpr ::= PostfixExpr '--'
 
UnaryExpr ::=
  PreIncrExpr    |
  PreDecrExpr    | 
  UnaryPlusExpr  |
  UnaryMinusExpr |
  UnaryExprNotPlusMinus
  
UnaryPlusExpr ::= '+' UnaryExpr

UnaryMinusExpr ::= '-' UnaryExpr
 
PreIncrExpr ::= '++' UnaryExpr
 
PreDecrExpr ::= '--' UnaryExpr
 
UnaryExprNotPlusMinus ::=
  PostfixExpr   |
  InvExpr       |
  NotExpr       |
  CastExpr
 
InvExpr ::= '~' UnaryExpr

NotExpr ::= '!' UnaryExpr
 
CastExpr ::=
  '(' PrimitiveType ')' UnaryExpr      |
  '(' QName ')' UnaryExprNotPlusMinus |
  '(' ArrType ')' UnaryExprNotPlusMinus
 
MultExprGr ::=
  UnaryExpr |
  MultExpr  |
  DivExpr   |
  ModExpr
  
MultExpr ::= MultExprGr '*' UnaryExpr

DivExpr ::= MultExprGr '/' UnaryExpr

ModExpr ::= MultExprGr '%' UnaryExpr
 
AdditiveExprGr ::=
  MultExprGr |
  PlusExpr   |
  MinusExpr
  
PlusExpr ::= AdditiveExprGr '+' MultExprGr

MinusExpr ::= AdditiveExprGr '-' MultExprGr
 
ShiftExprGr ::=
  AdditiveExprGr |
  LShiftExpr     |
  RShiftExpr     |
  UShiftExpr
  
LShiftExpr ::= ShiftExprGr '<<' AdditiveExprGr

RShiftExpr ::= ShiftExprGr '>>' AdditiveExprGr

UShiftExpr ::= ShiftExprGr '>>>' AdditiveExprGr
 
RelExprGr ::=
  ShiftExprGr |
  LessExpr    |
  GtExpr      |
  LeExpr      | 
  GeExpr      |
  InstOfExpr
  
LessExpr ::= RelExprGr '<' ShiftExprGr

GtExpr ::= RelExprGr '>' ShiftExprGr
  
LeExpr ::= RelExprGr '<=' ShiftExprGr

GeExpr ::= RelExprGr '>=' ShiftExprGr

InstOfExpr ::= RelExprGr INSTANCEOF RefType
  
EqExprGr ::=
  RelExprGr |
  EqExpr    |
  NeExpr 
  
EqExpr ::= EqExprGr '==' RelExprGr

NeExpr ::= EqExprGr '!=' RelExprGr
 
AndExprGr ::=
  EqExprGr  |
  AndExpr
  
AndExpr ::= AndExprGr '&' EqExprGr
 
ExclOrExprGr ::=
  AndExprGr |
  ExclOrExpr 
  
ExclOrExpr ::= ExclOrExprGr '^' AndExprGr
 
InclOrExprGr ::=
  ExclOrExprGr |
  InclOrExpr
  
InclOrExpr ::= InclOrExprGr '|' ExclOrExprGr
 
CondAndExprGr ::=
  InclOrExprGr |
  CondAndExpr

CondAndExpr ::= CondAndExprGr '&&' InclOrExprGr
 
CondOrExprGr ::=
  CondAndExprGr |
  CondOrExpr
  
CondOrExpr ::= CondOrExprGr '||' CondAndExprGr
 
CondExprGr ::=
  CondOrExprGr |
  CondExpr
  
CondExpr ::= CondOrExprGr '?' Expr ':' CondExprGr
 
AssnExprGr ::=
  CondExprGr |
  Assignment
 
Assignment ::= 
  AssnExpr        | 
  MultAssnExpr    |
  DivAssnExpr     |
  ModAssnExpr     |
  PlusAssnExpr    |
  MinusAssnExpr   |
  LShiftAssnExpr  |
  RShiftAssnExpr  |
  UShiftAssnExpr  |
  AndAssnExpr     |
  OrAssnExpr      |
  XorAssnExpr

AssnExpr ::= LeftHandSide '=' AssnExprGr

MultAssnExpr ::= LeftHandSide '*=' AssnExprGr

DivAssnExpr ::= LeftHandSide '/=' AssnExprGr

ModAssnExpr ::= LeftHandSide '%=' AssnExprGr

PlusAssnExpr ::= LeftHandSide '+=' AssnExprGr

MinusAssnExpr ::= LeftHandSide '-=' AssnExprGr

LShiftAssnExpr ::= LeftHandSide '<<=' AssnExprGr

RShiftAssnExpr ::= LeftHandSide '>>=' AssnExprGr

UShiftAssnExpr ::= LeftHandSide '>>>=' AssnExprGr

AndAssnExpr ::= LeftHandSide '&=' AssnExprGr

XorAssnExpr ::= LeftHandSide '^=' AssnExprGr

OrAssnExpr ::= LeftHandSide '|=' AssnExprGr
 
LeftHandSide ::=
  QName    |
  FldAccess |
  ArrAccess
 
AssnOper ::=
  '='    |
  '*='   |
  '/='   |
  '%='   |
  '+='   |
  '-='   |
  '<<='  |
  '>>='  |
  '>>>=' |
  '&='   |
  '^='   |
  '|='
 
Expr ::= AssnExprGr
 
ConstExpr ::= Expr
