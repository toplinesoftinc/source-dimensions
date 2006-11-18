-- Backtracking conflict resolutions:
-- Try shift first in shift/reduce conflicts on symbol LBRACK('<') between expressions and starting of argument list.


%Options lalr=4,prefix=TK_J5_,names=MAX,error-maps,noread-reduce,table=space,generate-parser=C++,states,list,nt-prefix=Nt_J5_,

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
	LTLT_EQ			
	EQEQ				
	NE			 	
	LE			 	
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
	ENUM
	WHITESPACE
	ELLIPSIS
	AT

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
	'<<=' ::= LTLT_EQ			
	'==' ::= EQEQ				
	'!=' ::= NE			 	
	'<=' ::= LE			 	
	'<' ::= LANGLE			
	'>' ::= RANGLE			
	'&&' ::= ANDAND			
	'||' ::= OROR				
	'++' ::= INCR				
	'--' ::= DECR				
	'...' ::= ELLIPSIS
	'@' ::= AT
	'_' ::= WHITESPACE
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
 
ClsIntfType ::= 
  QName TypeArgs |
  QName
 
ClsType ::=
  QName TypeArgs |
  QName
 
IntfType ::= 
  QName TypeArgs |
  QName

TypeVar ::= ID
 
ArrType ::= 
  ClsIntfType Dims |
  PrimitiveType Dims

TypeParam ::=
  TypeVar TypeBnd |
  TypeVar

TypeBnd ::=
  EXTENDS ClsIntfType BndList |
  EXTENDS ClsIntfType

BndList ::=
  Bnd |
  BndList Bnd

Bnd ::= '&' IntfType

TypeArgs ::= 
  '<' ActualTypeArgList '>' |
  '<' ActualTypeArgList '>' '_'

ActualTypeArgList ::=
  ActualTypeArg     |
  ActualTypeArgList ',' ActualTypeArg

ActualTypeArg ::=
  RefType            |
  SuperWildcardBnd   |
  ExtendsWildcardBnd |
  Wildcard

Wildcard ::= '?'

SuperWildcardBnd ::= '?' SUPER RefType

ExtendsWildcardBnd ::= '?' EXTENDS RefType

Modifiers ::=
  Modifier |
  Modifiers Modifier
 
Modifier ::=
  Annot        |
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
 
PackageDecl ::= 
  Modifiers PACKAGE QName ';' |
            PACKAGE QName ';'
 
ImpDecl ::=
  SingleTypeImpDecl   |
  TypeImpOnDemandDecl |
  SingleStaticImpDecl |
  StaticImpOnDemandDecl 
 
SingleTypeImpDecl ::= IMPORT QName ';'
 
TypeImpOnDemandDecl ::= IMPORT QName '.' '*' ';'

SingleStaticImpDecl ::= IMPORT STATIC QName '.' ID ';'

StaticImpOnDemandDecl ::= IMPORT STATIC QName '.' '*' ';'
 
TypeDecl ::=
  ClsDecl       |
  EnumDecl      |
  IntfDecl      |
  AnnotTypeDecl |
  ';'

 
-- *** Classes ***
 
ClsDecl ::=
            CLASS ID                           ClsBody |
            CLASS ID                     Intfs ClsBody |
            CLASS ID            SuperCls       ClsBody |
            CLASS ID            SuperCls Intfs ClsBody |
  Modifiers CLASS ID                           ClsBody |
  Modifiers CLASS ID                     Intfs ClsBody |
  Modifiers CLASS ID            SuperCls       ClsBody |
  Modifiers CLASS ID            SuperCls Intfs ClsBody |
            CLASS ID TypeParams                ClsBody |
            CLASS ID TypeParams          Intfs ClsBody |
            CLASS ID TypeParams SuperCls       ClsBody |
            CLASS ID TypeParams SuperCls Intfs ClsBody |
  Modifiers CLASS ID TypeParams                ClsBody |
  Modifiers CLASS ID TypeParams          Intfs ClsBody |
  Modifiers CLASS ID TypeParams SuperCls       ClsBody |
  Modifiers CLASS ID TypeParams SuperCls Intfs ClsBody
  
TypeParams ::= 
  '<' TypeParamList '>' |
  '<' TypeParamList '>' '_'

TypeParamList ::= 
  TypeParamList ',' TypeParam |
  TypeParam
 
SuperCls ::= EXTENDS ClsType
 
Intfs ::= IMPLEMENTS IntfTypeList
 
IntfTypeList ::=
  IntfType |
  IntfTypeList ',' IntfType
 
ClsBody ::= 
  '{' ClsBodyDecls '}' |
  '{'              '}'
 
ClsBodyDecls ::=
  ClsBodyDecl |
  ClsBodyDecls ClsBodyDecl
 
ClsBodyDecl ::=
  ClsMemDecl |
  InstInit   |
  StaticInit |
  ConstrDecl
 
ClsMemDecl ::=
  FldDecl       |
  MethodDecl    |
  ClsDecl       |
  EnumDecl      | 
  IntfDecl      |
  AnnotTypeDecl |
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
  Modifiers            ResultType MethodDclr        |
  Modifiers            ResultType MethodDclr Throws |
            TypeParams ResultType MethodDclr        |
            TypeParams ResultType MethodDclr Throws |
  Modifiers TypeParams ResultType MethodDclr        |
  Modifiers TypeParams ResultType MethodDclr Throws
   
ResultType ::=
  Type |
  VOID
 
MethodDclr ::=
  ID '(' FormalParamList ')' |
  ID '('                 ')' |
  MethodDclr Dim
 
FormalParamList ::=
  LastFormalParam |
  FormalParams ',' LastFormalParam 
 
FormalParams ::=
  FormalParam |
  FormalParams ',' FormalParam
 
FormalParam ::=
  Modifiers Type VarDclrId |
            Type VarDclrId 
 
LastFormalParam ::=
  VarArityParam |
  FormalParam

VarArityParam ::=
  Modifiers Type '...' VarDclrId |
            Type '...' VarDclrId
 
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
             ID '('                 ')' |
  TypeParams ID '(' FormalParamList ')' |
  TypeParams ID '('                 ')'
  
ConstrBody ::=
  '{'                              '}' |
  '{'                     BlkStmts '}' |
  '{' ExplicitConstrInvoc          '}' |
  '{' ExplicitConstrInvoc BlkStmts '}'
 
ExplicitConstrInvoc ::=
                  THIS '(' ArgList ')' ';'              |
                  THIS '('         ')' ';'              |
  NonWildTypeArgs THIS '(' ArgList ')' ';'              |
  NonWildTypeArgs THIS '('         ')' ';'              |
                  Super '(' ArgList ')' ';'             |
                  Super '('         ')' ';'             |
  NonWildTypeArgs Super '(' ArgList ')' ';'             |
  NonWildTypeArgs Super '('         ')' ';'             |
  Primary '.' Super '(' ArgList ')' ';'                 |
  Primary '.' Super '('         ')' ';'                 |
  Primary '.' NonWildTypeArgs Super '(' ArgList ')' ';' |
  Primary '.' NonWildTypeArgs Super '('         ')' ';'
 
NonWildTypeArgs ::= 
  '<' RefTypeList '>' |
  '<' RefTypeList '>' '_'

RefTypeList ::= 
  RefType |
  RefTypeList ',' RefType
 
 
-- *** Enums ***

EnumDecl ::=
            ENUM ID                |
            ENUM ID       EnumBody |
  Modifiers ENUM ID                |  
  Modifiers ENUM ID       EnumBody |
            ENUM ID Intfs          |
            ENUM ID Intfs EnumBody |
  Modifiers ENUM ID Intfs          |  
  Modifiers ENUM ID Intfs EnumBody

EnumBody ::=
  '{'                              '}' |
  '{'                EnumBodyDecls '}' |
  '{'            ','               '}' |
  '{'            ',' EnumBodyDecls '}' |
  '{' EnumConsts                   '}' |
  '{' EnumConsts     EnumBodyDecls '}' |
  '{' EnumConsts ','               '}' |
  '{' EnumConsts ',' EnumBodyDecls '}'

EnumConsts ::=
  EnumConst |
  EnumConsts ',' EnumConst
  
EnumConst ::=
            ID              |
            ID      ClsBody |
            ID Args         |
            ID Args ClsBody |
  Modifiers ID              |
  Modifiers ID      ClsBody |
  Modifiers ID Args         |
  Modifiers ID Args ClsBody
  
Args ::=
  '('         ')' |
  '(' ArgList ')'
  
EnumBodyDecls ::=
  ';' |
  ';' ClsBodyDecls

 
-- *** Interfaces ***
  
IntfDecl ::=
            INTERFACE ID                         IntfBody |
            INTERFACE ID            ExtendsIntfs IntfBody |
  Modifiers INTERFACE ID                         IntfBody |
  Modifiers INTERFACE ID            ExtendsIntfs IntfBody |
            INTERFACE ID TypeParams              IntfBody |
            INTERFACE ID TypeParams ExtendsIntfs IntfBody |
  Modifiers INTERFACE ID TypeParams              IntfBody |
  Modifiers INTERFACE ID TypeParams ExtendsIntfs IntfBody
 
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
  EnumDecl        |
  IntfDecl        |
  AnnotTypeDecl   |
  ';'       
 
ConstDecl ::=
  Modifiers Type VarDclrs ';' |
            Type VarDclrs ';'
 
AbstrMethodDecl ::=
                       ResultType MethodDclr        ';' |
                       ResultType MethodDclr Throws ';' |
  Modifiers            ResultType MethodDclr        ';' |
  Modifiers            ResultType MethodDclr Throws ';' |
            TypeParams ResultType MethodDclr        ';' | 
            TypeParams ResultType MethodDclr Throws ';' |
  Modifiers TypeParams ResultType MethodDclr        ';' |
  Modifiers TypeParams ResultType MethodDclr Throws ';'
 
AnnotTypeDecl ::=
  Modifiers '@' INTERFACE ID AnnotTypeBody |
            '@' INTERFACE ID AnnotTypeBody
            
AnnotTypeBody ::=
  '{' AnnotTypeElemDecls '}' |
  '{'                    '}'

AnnotTypeElemDecls ::=
  AnnotTypeElemDecl |
  AnnotTypeElemDecls AnnotTypeElemDecl
  
AnnotTypeElemDecl ::=
  AnnotTypeMethodDecl |
  ConstDecl           |
  ClsDecl             |
  EnumDecl            |
  IntfDecl            |
  AnnotTypeDecl       |
  ';'

AnnotTypeMethodDecl ::=
            Type ID '(' ')'            ';' |
            Type ID '(' ')' DefaultVal ';' |
  Modifiers Type ID '(' ')'            ';' |  
  Modifiers Type ID '(' ')' DefaultVal ';'


DefaultVal ::= DEFAULT ElemVal


-- *** Arrays ***

ArrInit ::=
  '{'              '}' |
  '{'          ',' '}' |
  '{' VarInits     '}' |
  '{' VarInits ',' '}'
 
VarInits ::=
  VarInit |
  VarInits ',' VarInit


-- *** Annotations ***
  
Annot ::=
  NormalAnnot |
  MarkerAnnot |
  SingleElemAnnot
  
NormalAnnot ::= 
  '@' QName '(' ElemValPairs ')' |
  '@' QName '('              ')'
  
ElemValPairs ::=
  ElemValPair |
  ElemValPairs ',' ElemValPair
  
ElemValPair ::= ID '=' ElemVal
  
ElemVal ::=
  CondExprGr |
  Annot      |
  ElemValArrInit
  
ElemValArrInit ::=
  '{'              '}' |
  '{'          ',' '}' |
  '{' ElemVals     '}' |
  '{' ElemVals ',' '}'

ElemVals ::=
  ElemVal |
  ElemVals ',' ElemVal
  
MarkerAnnot ::= '@' QName

SingleElemAnnot ::= '@' QName '(' ElemVal ')' 

 
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
  EnumDecl         |
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
  ForStmt      |
  EnhancedForStmt
 
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
  FOR '('         ';'      ';'           ')' Stmt |
  FOR '('         ';'      ';' ForUpdate ')' Stmt |
  FOR '('         ';' Expr ';'           ')' Stmt |
  FOR '('         ';' Expr ';' ForUpdate ')' Stmt |      
  FOR '(' ForInit ';'      ';'           ')' Stmt |
  FOR '(' ForInit ';'      ';' ForUpdate ')' Stmt |  
  FOR '(' ForInit ';' Expr ';'           ')' Stmt |
  FOR '(' ForInit ';' Expr ';' ForUpdate ')' Stmt
 
ForInit ::=
  StmtExprList |
  LocalVarDecl
 
ForUpdate ::= StmtExprList
 
StmtExprList ::=
  StmtExpr |
  StmtExprList ',' StmtExpr
 
EnhancedForStmt ::=
  FOR '(' Modifiers Type ID ':' Expr ')' Stmt |
  FOR '('           Type ID ':' Expr ')' Stmt
 
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
 

-- *** Expessions ***

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
  NEW          ClsIntfType '('         ')'            |
  NEW          ClsIntfType '('         ')' ClsBody    |
  NEW          ClsIntfType '(' ArgList ')'            |
  NEW          ClsIntfType '(' ArgList ')' ClsBody    |
  NEW TypeArgs ClsIntfType '('         ')'            | 
  NEW TypeArgs ClsIntfType '('         ')' ClsBody    |
  NEW TypeArgs ClsIntfType '(' ArgList ')'            |
  NEW TypeArgs ClsIntfType '(' ArgList ')' ClsBody    |
  Primary '.' NEW ID '('         ')'                  | 
  Primary '.' NEW ID '('         ')' ClsBody          | 
  Primary '.' NEW ID '(' ArgList ')'                  |   
  Primary '.' NEW ID '(' ArgList ')' ClsBody          |
  Primary '.' NEW TypeArgs ID '('         ')'         |  
  Primary '.' NEW TypeArgs ID '('         ')' ClsBody | 
  Primary '.' NEW TypeArgs ID '(' ArgList ')'         |   
  Primary '.' NEW TypeArgs ID '(' ArgList ')' ClsBody |
  QName '.' NEW ID '('              ')'               | 
  QName '.' NEW ID '('              ')' ClsBody       | 
  QName '.' NEW ID '(' ArgList ')'                    |   
  QName '.' NEW ID '(' ArgList ')' ClsBody            |
  QName '.' NEW TypeArgs ID '('         ')'           | 
  QName '.' NEW TypeArgs ID '('         ')' ClsBody   | 
  QName '.' NEW TypeArgs ID '(' ArgList ')'           |   
  QName '.' NEW TypeArgs ID '(' ArgList ')' ClsBody
 
ArgList ::=
  Expr |
  ArgList ',' Expr
 
ArrCreatExpr ::=
  NEW PrimitiveType DimExprs       | 
  NEW PrimitiveType DimExprs Dims  |
  NEW QName          DimExprs      |
  NEW QName          DimExprs Dims |
  NEW QName TypeArgs DimExprs      |
  NEW QName TypeArgs DimExprs Dims |
  NEW PrimitiveType Dims ArrInit   |
  NEW QName          Dims ArrInit  |
  NEW QName TypeArgs Dims ArrInit

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
  QName '('         ')'                                  |
  QName '(' ArgList ')'                                  |
  QName '.' NonWildTypeArgs ID '('         ')'           |
  QName '.' NonWildTypeArgs ID '(' ArgList ')'           |
  Primary '.'                 ID '('         ')'          |
  Primary '.'                 ID '(' ArgList ')'          |
  Primary '.' NonWildTypeArgs ID '('         ')'          |
  Primary '.' NonWildTypeArgs ID '(' ArgList ')'          |
  Super '.'                 ID '('         ')'            |
  Super '.'                 ID '(' ArgList ')'            |
  Super '.' NonWildTypeArgs ID '('         ')'            |
  Super '.' NonWildTypeArgs ID '(' ArgList ')'            |
  QName '.' Super '.'                 ID '(' ArgList ')' |
  QName '.' Super '.' NonWildTypeArgs ID '(' ArgList ')' |
  QName '.' Super '.'                 ID '('         ')' |
  QName '.' Super '.' NonWildTypeArgs ID '('         ')'
 
ArrAccess ::=
  QName '[' Expr ']' |
  PrimaryNoNewArr '[' Expr ']'
 
PostfixExpr ::= 
  Primary      |
  QName        |
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
  '(' PrimitiveType ')' UnaryExpr              |
  '(' QName   ')' UnaryExprNotPlusMinus        |
  '(' QName TypeArgs ')' UnaryExprNotPlusMinus |
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

RShiftExpr ::=
  ShiftExprGr '>' '>' AdditiveExprGr     |
  ShiftExprGr '>' '>' '_' AdditiveExprGr
  
UShiftExpr ::=
  ShiftExprGr '>' '>' '>' AdditiveExprGr |
  ShiftExprGr '>' '>' '>' '_' AdditiveExprGr 
 
RelExprGr ::=
  ShiftExprGr |
  LessExpr    |
  GtExpr      |
  LeExpr      | 
  GeExpr      |
  InstOfExpr
  
LessExpr ::= RelExprGr '<' ShiftExprGr

GtExpr ::= 
  RelExprGr '>' ShiftExprGr |
  RelExprGr '>' '_' ShiftExprGr
  
LeExpr ::= RelExprGr '<=' ShiftExprGr

GeExpr ::= RelExprGr '>' '=' ShiftExprGr

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

RShiftAssnExpr ::= LeftHandSide '>' '>' '=' AssnExprGr

UShiftAssnExpr ::= LeftHandSide '>' '>' '>' '=' AssnExprGr

AndAssnExpr ::= LeftHandSide '&=' AssnExprGr

XorAssnExpr ::= LeftHandSide '^=' AssnExprGr

OrAssnExpr ::= LeftHandSide '|=' AssnExprGr

LeftHandSide ::=
  QName     |
  FldAccess |
  ArrAccess
  
Expr ::= AssnExprGr
 
ConstExpr ::= Expr

