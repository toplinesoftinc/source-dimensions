-- Backtracking conflict resolutions:
-- 1. Try type first in reduce/reduce conflicts on RPAREN(')') between expression and type.
-- 2. Try type first in reduce/reduce conflicts on STAR('*') between expression and type.
-- 3. Try type first in reduce/reduce conflicts on QUESTION('?') between expression and type.
-- 4. Try type first in shift/reduce conflicts on LANGLE('<') between expression and type arguments.

%Options lalr=3,prefix=TK_CS2_,names=MAX,error-maps,noread-reduce,table=space,generate-parser=C++,states,list,file-prefix=CSha2,nt-prefix=Nt_Cs2_,

%Terminals
	ID

	CHAR_LITERAL
	STR_LITERAL
	INT_LITERAL
	REAL_LITERAL

	LANGLE
	RANGLE
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
	ANDAND
	OROR
	INCR
	DECR
	ARROW
	SCOPE
	QQ

	ARGLIST
	MAKEREF
	REFTYPE
	REFVALUE
	ABSTRACT
	ADD
	ALIAS
	AS
	ASSEMBLY
	BASE
	BOOL
	BREAK
	BYTE
	CASE
	CATCH
	CHAR
	CHECKED
	CLASS
	CONST
	CONTINUE
	DECIMAL
	DEFAULT
	DELEGATE
	DO
	DOUBLE
	ELSE
	ENUM
	EVENT
	EXPLICIT
	EXTERN
	FALSE
	FIELD
	FINALLY
	FIXED
	FLOAT
	FOR
	FOREACH
	GET
	GOTO
	IF
	IMPLICIT
	IN
	INT
	INTERFACE
	INTERNAL
	IS
	LOCK
	LONG
	METHOD
	MODULE
	NAMESPACE
	NEW
	NULL
	OBJECT
	OPERATOR
	OUT
	OVERRIDE
	PARAM
	PARAMS
	PARTIAL
	PRIVATE
	PROPERTY
	PROTECTED
	PUBLIC
	READONLY
	REF
	REMOVE
	RETURN
	SBYTE
	SEALED
	SET
	SHORT
	SIZEOF
	STACKALLOC
	STATIC
	STRING
	STRUCT
	SWITCH
	THIS
	THROW
	TRUE
	TRY
	TYPE
	TYPEOF
	UINT
	ULONG
	UNCHECKED
	UNSAFE
	USHORT
	USING
	VIRTUAL
	VOID
	VOLATILE
	WHILE
	WHERE
	YIELD
	WHITESPACE

	END_OF_FILE


%Alias
	'<' ::= LANGLE
	'>' ::= RANGLE
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
	'&&' ::= ANDAND
	'||' ::= OROR
	'++' ::= INCR
	'--' ::= DECR
	'->' ::= ARROW
	'::' ::= SCOPE
	'_' ::= WHITESPACE
	'??' ::= QQ
	%EOF ::= END_OF_FILE


%Rules


-- *** Start symbol ***

CompUnit ::=
  %empty                                |
                            GblAttrs    |  
  				  	  	    NspMemDecls |
  				  UsingDirs             |
  				  UsingDirs GblAttrs    |
  				  UsingDirs NspMemDecls |  				  
  ExternAliasDirs                       |
  ExternAliasDirs           GblAttrs    |  
  ExternAliasDirs	        NspMemDecls |
  ExternAliasDirs UsingDirs             |
  ExternAliasDirs UsingDirs GblAttrs    |  
  ExternAliasDirs UsingDirs NspMemDecls
    

-- *** Basic concepts ***

NspName ::= QName

TypeName ::= QName

NspTypeName ::= QName

QName ::=
  Id             |  
  Id TypeArgList |
  QAlias         |
  QName '.' Id   |
  QName '.' Id TypeArgList

QAlias ::= Id '::' Id
  
Id ::=
  ID       |
  Assembly |
  Add      |
  Field    |
  Get      |
  Method   |
  Param    |
  Property |
  Remove   |
  Set      |
  Typ      |
  Module   |
  Where    |
  Yield    |
  Partial  |
  Alias

 

-- *** Types ***

Type ::=
  TypeName   |
  SimpleType |
  ArrType    |
  OBJECT     |
  STRING     |
  PtrType    |
  NullType
  
NullType ::=  NonNullType '?'

NonNullType ::=
  TypeName   |
  SimpleType
  
SimpleType ::=
  NumType |
  BOOL
  
NumType ::=
  IntType     |
  FloatPtType |
  DECIMAL
  
IntType ::=
  SBYTE  |
  BYTE   |
  SHORT  |
  USHORT |
  INT    |
  UINT   |
  LONG   |
  ULONG  |
  CHAR
  
FloatPtType ::=
  FLOAT |
  DOUBLE
  
PtrType ::= NonPtrType Ptrs

NonPtrType ::= 
  TypeName   |
  SimpleType |
  ArrType    |
  OBJECT     |
  STRING     |
  NullType   |
  VOID
  
Ptrs ::=
  Ptr |
  Ptrs Ptr
  
Ptr ::= '*'		
  
UnmanagedType ::= Type
  
ClsType ::=
  TypeName |
  OBJECT   |
  STRING

ClsIntfType ::=
  TypeName |
  OBJECT   |
  STRING

IntfType ::= TypeName

ArrType ::= NonArrType RankSpecs

NonArrType ::=
  TypeName   |
  SimpleType |
  PtrType    |
  OBJECT     |
  STRING     |
  NullType

RankSpec ::=
  '['         ']' |
  '[' DimSeps ']'
  
DimSeps ::=
  DimSep |
  DimSeps DimSep
  
DimSep ::= ','

TypeParamList ::= 
  '<' TypeParams '>' |
  '<' TypeParams '>' '_'

TypeParams ::=
  TypeParam                |
  TypeParams ',' TypeParam
  
TypeParam ::= 
  Attrs Id |
        Id

TypeArgList ::= 
  '<' TypeArgs '>' |
  '<' TypeArgs '>' '_'

TypeArgs ::=
  TypeArg |
  TypeArgs ',' TypeArg
  
TypeArg ::= Type

Constraints ::=
  Constraint |
  Constraints Constraint
  
Constraint ::= WHERE TypeParam ':' TypeParamCnrts

TypeParamCnrts ::=
  PrimaryCnrt                |
  ConstrCnrt                 |
  PrimaryCnrt ',' SecdCnrts  |
  PrimaryCnrt ',' ConstrCnrt |
  PrimaryCnrt ',' SecdCnrts ',' ConstrCnrt

PrimaryCnrt ::= 
  TypeName |
  Class    |
  Struct
  
SecdCnrts ::=
  TypeName |
  SecdCnrts ',' TypeName
  
ConstrCnrt ::= NEW '(' ')'


-- *** Expressions

VarRef ::= Expr

ArgList ::=
  Arg             |
  ArgLstExpr      |
  ArgList ',' Arg |
  ArgList ',' ArgLstExpr
  
Arg ::=
  Expr   |
  RefArg |
  OutArg
  
ArgLstExpr ::= 
  ARGLIST '(' ExprList ')' |
  ARGLIST '('          ')'

RefArg ::= REF VarRef

OutArg ::= OUT VarRef
  
PrimaryExpr ::=
  PrimaryNoArrCreatExpr |
  ArrCreInitExpr        |
  ArrCreatExpr
  
PrimaryNoArrCreatExpr ::=
  Literal          |
  ParenExpr        |
  MemAccess        |
  InvocExpr        |
  ElemAccess       |
  ThisAccess       |
  BaseAccess       |
  PostIncrExpr     |
  PostDecrExpr     |
  ObjCreatExpr     |
  TypeofExpr       |
  CheckedExpr      |
  UncheckedExpr    |
  PtrMemAccess     |
  SizeofExpr       |
  DefaultValExpr   |
  AnonymMethodExpr |
  MakeRefExpr      |
  RefTypeExpr      |
  RefValExpr

MakeRefExpr ::= MAKEREF '(' Expr ')'

RefTypeExpr ::= REFTYPE '(' Expr ')'

RefValExpr ::= REFVALUE '(' Expr ',' Type ')'

Literal ::=
  CHAR_LITERAL |
  STR_LITERAL  |
  INT_LITERAL  |
  REAL_LITERAL |
  True         |
  False        |
  NULL

ParenExpr ::= '(' Expr ')' 

MemAccess ::= 
  PrimaryExpr '.' Id             |
  PrimaryExpr '.' Id TypeArgList |
  PredefType '.' Id              |
  PredefType '.' Id TypeArgList
  
PredefType ::=
  BOOL    |
  BYTE    |
  CHAR    |
  DECIMAL |
  DOUBLE  |
  FLOAT   |
  INT     |
  LONG    |
  OBJECT  |
  SBYTE   |
  SHORT   |
  STRING  |
  UINT    |
  ULONG   |
  USHORT
  
InvocExpr ::=
  PrimaryExpr '(' ArgList ')' |
  PrimaryExpr '('         ')' |
  QName '(' ArgList ')'       |
  QName '('         ')'	
  
ElemAccess ::= 
  PrimaryNoArrCreatExpr '[' ExprList ']' |
  ArrCreInitExpr '[' ExprList ']' |
  QName '[' ExprList ']'

ExprList ::= 
  Expr |
  ExprList ',' Expr
  
ThisAccess ::= THIS

BaseAccess ::=
  BASE '.' Id             |
  BASE '.' Id TypeArgList |
  BASE '[' ExprList ']'
  
PostIncrExpr ::= 
  PrimaryExpr '++' |
  QName '++'

PostDecrExpr ::= 
  PrimaryExpr '--' |
  QName '--'

ObjCreatExpr ::= 
  NEW Type '(' ArgList ')' |
  NEW Type '(' ARGLIST ')' |
  NEW Type '('         ')'
  
ArrCreatExpr ::=
  NEW Type '[' ExprList ']'           |
  NEW Type '[' ExprList ']' RankSpecs

ArrCreInitExpr ::=
  NEW Type '[' ExprList ']'           ArrInit |
  NEW Type '[' ExprList ']' RankSpecs ArrInit |
  NEW ArrType ArrInit
    
RankSpecs ::= 
  RankSpec |
  RankSpecs RankSpec	
  
TypeofExpr ::=
  TYPEOF '(' Type ')'            |
  TYPEOF '(' UnboundTypeName ')' |
  TYPEOF '(' VOID ')'
  
UnboundTypeName ::=
  QName GenericDimSpec   |
  UnboundTypeName '.' Id |
  UnboundTypeName '.' Id GenericDimSpec
  
GenericDimSpec ::=
  '<'        '>'     |
  '<'        '>' '_' |
  '<' Commas '>'     |
  '<' Commas '>' '_'
  
Commas ::=
  Comma |
  Commas Comma
  
Comma ::= ','
  
CheckedExpr ::= CHECKED '(' Expr ')'

UncheckedExpr ::= UNCHECKED '(' Expr ')'

UnaryExpr ::=
  PrimaryExpr  |
  QName        |	
  PlusExpr     |
  MinusExpr    | 
  NotExpr      |
  InvExpr      |
  PreIncrExpr  |
  PreDecrExpr  |
  CastExpr     |
  PtrIndirExpr |
  AddrofExpr
  
PlusExpr ::= '+' UnaryExpr

MinusExpr ::= '-' UnaryExpr

NotExpr ::= '!' UnaryExpr

InvExpr ::= '~' UnaryExpr
  
PreIncrExpr ::= '++' UnaryExpr
  
PreDecrExpr ::= '--' UnaryExpr

CastExpr ::= '(' Type ')' UnaryExpr

PtrIndirExpr ::= '*' UnaryExpr

PtrMemAccess ::= 
  PrimaryExpr '->' Id TypeArgList |
  PrimaryExpr '->' Id             |
  QName '->' Id TypeArgList       |
  QName '->' Id

AddrofExpr ::= '&' UnaryExpr

SizeofExpr ::= SIZEOF '(' UnmanagedType ')' 

DefaultValExpr ::= DEFAULT '(' Type ')'

AnonymMethodExpr ::=
  DELEGATE                 Blk |
  DELEGATE AnonymMethodSig Blk
  
AnonymMethodSig ::=
  '('                       ')' |
  '(' AnonymMethodParamList ')'
  
AnonymMethodParamList ::=
  AnonymMethodParam |
  AnonymMethodParamList ',' AnonymMethodParam
  
AnonymMethodParam ::=
                Type Id |
  ParamModifier Type Id	
  
MultExprGr ::=
  UnaryExpr |
  MultExpr  |
  DivExpr   |
  ModExpr
  
MultExpr ::= MultExprGr '*' UnaryExpr

DivExpr ::= MultExprGr '/' UnaryExpr

ModExpr ::= MultExprGr '%' UnaryExpr
  
AddExprGr ::=
  MultExprGr |
  AddExpr    |
  SubExpr
  
AddExpr ::= AddExprGr '+' MultExprGr

SubExpr ::= AddExprGr '-' MultExprGr
  
ShiftExprGr ::=
  AddExprGr  |
  LShiftExpr |
  RShiftExpr 
  
LShiftExpr ::= ShiftExprGr '<<' AddExprGr

RShiftExpr ::= 
  ShiftExprGr '>' '>' AddExprGr |
  ShiftExprGr '>' '>' '_' AddExprGr

RelExprGr ::=
  ShiftExprGr |
  LessExpr    |
  GtExpr      |
  LsEqExpr    |
  GtEqExpr    |
  IsExpr      |
  AsExpr
  
LessExpr ::= RelExprGr '<' ShiftExprGr

GtExpr ::= 
	RelExprGr '>' ShiftExprGr |
	RelExprGr '>' '_' ShiftExprGr

LsEqExpr ::= RelExprGr '<=' ShiftExprGr

GtEqExpr ::= RelExprGr '>' '=' ShiftExprGr

IsExpr ::= RelExprGr IS Type

AsExpr ::= RelExprGr AS Type
   
EqExprGr ::=
  RelExprGr  | 
  EqExpr     |
  NotEqExpr
  
EqExpr ::= EqExprGr '==' RelExprGr

NotEqExpr ::= EqExprGr '!=' RelExprGr
  
AndExprGr ::=
  EqExprGr |
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

NullCoalesExprGr ::=
  CondOrExprGr |
  NullCoalesExpr
  
NullCoalesExpr ::= CondOrExprGr '??' NullCoalesExprGr
  
CondExprGr ::=
  NullCoalesExprGr |
  CondExpr
  
CondExpr ::= NullCoalesExprGr '?' Expr ':' Expr
  
AssnExpr ::= 
  Assn      |
  PlusAssn  |
  MinusAssn |
  MultAssn  |
  DivAssn   |
  ModAssn   |
  AndAssn   |
  OrAssn    |
  XorAssn   |
  LShAssn   |
  RShAssn
	
Assn ::= UnaryExpr '=' Expr

PlusAssn ::= UnaryExpr '+=' Expr

MinusAssn ::= UnaryExpr '-=' Expr

MultAssn ::= UnaryExpr '*=' Expr

DivAssn ::= UnaryExpr '/=' Expr

ModAssn ::= UnaryExpr '%=' Expr

AndAssn ::= UnaryExpr '&=' Expr

OrAssn ::= UnaryExpr '|=' Expr

XorAssn ::= UnaryExpr '^=' Expr

LShAssn ::= UnaryExpr '<<=' Expr

RShAssn ::= UnaryExpr '>' '>' '=' Expr

Expr ::= 
  CondExprGr |
  AssnExpr
  
ConstExpr ::= Expr

BoolExpr ::= Expr


-- *** Statements ***

Stmt ::=
  LblStmt  |
  DeclStmt |
  EmbedStmt

EmbedStmt ::=
  Blk           |
  EmptyStmt     |
  ExprStmt      |
  SelStmt       |
  IterStmt      |
  JumpStmt      |
  TryStmt       |
  CheckedStmt   |
  UncheckedStmt |
  LockStmt      |
  UsingStmt     |
  UnsafeStmt    |
  FixedStmt     |
  YieldStmt

Blk ::=
  '{' StmtList '}' |
  '{'          '}'
  
StmtList ::=
  Stmt |
  StmtList Stmt

EmptyStmt ::= ';'

LblStmt ::= Id ':' Stmt

DeclStmt ::=
  LocalVarDecl ';' |
  LocalConstDecl ';'

LocalVarDecl ::= Type LocalVarDclrs

LocalVarDclrs ::=
  LocalVarDclr |
  LocalVarDclrs ',' LocalVarDclr

LocalVarDclr ::=
  Id |
  Id '=' LocalVarInit

LocalVarInit ::=
  Expr    |
  ArrInit |
  StackallocInit

StackallocInit ::= STACKALLOC UnmanagedType '[' Expr ']'	

LocalConstDecl ::= CONST Type ConstDclrs

ConstDclrs ::=
  ConstDclr |
  ConstDclrs ',' ConstDclr
  
ConstDclr ::= Id '=' ConstExpr

ExprStmt ::= StmtExpr ';'

StmtExpr ::=
  InvocExpr    |
  ObjCreatExpr |
  AssnExpr     |
  PostIncrExpr |
  PostDecrExpr |
  PreIncrExpr  |
  PreDecrExpr

SelStmt ::=
  IfStmt |
  SwitchStmt

IfStmt ::=
  IF '(' BoolExpr ')' EmbedStmt |
  IF '(' BoolExpr ')' EmbedStmt ELSE EmbedStmt

SwitchStmt ::= SWITCH '(' Expr ')' SwitchBlk

SwitchBlk ::=
  '{' SwitchSecs '}' |
  '{'            '}'

SwitchSecs ::=
  SwitchSec |
  SwitchSecs SwitchSec

SwitchSec ::= SwitchLbls StmtList

SwitchLbls ::= 
  SwitchLbl |
  SwitchLbls SwitchLbl

SwitchLbl ::= 
  CASE ConstExpr ':' |
  DEFAULT ':'

IterStmt ::=
  WhileStmt |
  DoStmt    |
  ForStmt   |
  ForeachStmt
  
WhileStmt ::= WHILE '(' BoolExpr ')' EmbedStmt

DoStmt ::= DO EmbedStmt WHILE '(' BoolExpr ')' ';'

ForStmt ::= 
  FOR '('         ';'         ';'         ')' EmbedStmt |
  FOR '('         ';'         ';' ForIter ')' EmbedStmt |
  FOR '('         ';' ForCond ';'         ')' EmbedStmt |
  FOR '('         ';' ForCond ';' ForIter ')' EmbedStmt |
  FOR '(' ForInit ';'         ';'         ')' EmbedStmt |
  FOR '(' ForInit ';'         ';' ForIter ')' EmbedStmt |
  FOR '(' ForInit ';' ForCond ';'         ')' EmbedStmt |
  FOR '(' ForInit ';' ForCond ';' ForIter ')' EmbedStmt

ForInit ::=
  LocalVarDecl |
  StmtExprList

ForCond ::= BoolExpr

ForIter ::= StmtExprList

StmtExprList ::=
  StmtExpr |
  StmtExprList ',' StmtExpr

ForeachStmt ::= FOREACH '(' Type Id IN Expr ')' EmbedStmt

JumpStmt ::=
  BreakStmt    |
  ContinueStmt |
  GotoStmt     |
  ReturnStmt   |
  ThrowStmt

BreakStmt ::= BREAK ';'

ContinueStmt ::= CONTINUE ';'

GotoStmt ::=
  GotoId   |
  GotoCase |
  GotoDefault
  
GotoId ::= GOTO Id ';'

GotoCase ::= GOTO CASE ConstExpr ';'

GotoDefault ::= GOTO DEFAULT ';'

ReturnStmt ::=
  RETURN      ';' |
  RETURN Expr ';'
  
ThrowStmt ::= 
  THROW      ';' |
  THROW Expr ';'

TryStmt ::=
  TRY Blk Catches    |
  TRY Blk FinallyBlk |
  TRY Blk Catches FinallyBlk

Catches ::=
  SpecificCatches              |
                  GeneralCatch |
  SpecificCatches GeneralCatch
                       
SpecificCatches ::=
  SpecificCatch |
  SpecificCatches SpecificCatch

SpecificCatch ::=
  CATCH '(' ClsType    ')' Blk |
  CATCH '(' ClsType Id ')' Blk
  
GeneralCatch ::= CATCH Blk

FinallyBlk ::= FINALLY Blk

CheckedStmt ::= CHECKED Blk

UncheckedStmt ::= UNCHECKED Blk

LockStmt ::= LOCK '(' Expr ')' EmbedStmt

UsingStmt ::= USING '(' ResAcquisition ')' EmbedStmt

UnsafeStmt ::= UNSAFE Blk

FixedStmt ::= FIXED '(' PtrType FixedPtrDclrs ')' EmbedStmt

FixedPtrDclrs ::= 
  FixedPtrDclr |
  FixedPtrDclrs ',' FixedPtrDclr
  
FixedPtrDclr ::= Id '=' FixedPtrInit

FixedPtrInit ::= Expr

ResAcquisition ::=
  LocalVarDecl |
  Expr
  
YieldStmt ::=
  YIELD RETURN Expr ';' |
  YIELD BREAK ';'	


-- *** Namespaces ***

NspDecl ::=
  NAMESPACE NspName NspBody |
  NAMESPACE NspName NspBody ';'

NspBody ::=
  '{'                                       '}' |
  '{'                           NspMemDecls '}' |
  '{'                 UsingDirs             '}' |
  '{'                 UsingDirs NspMemDecls '}' |
  '{' ExternAliasDirs                       '}' |
  '{' ExternAliasDirs           NspMemDecls '}' |
  '{' ExternAliasDirs UsingDirs             '}' |
  '{' ExternAliasDirs UsingDirs NspMemDecls '}'
  
ExternAliasDirs ::= 
  ExternAliasDir |
  ExternAliasDirs ExternAliasDir

ExternAliasDir ::= EXTERN ALIAS Id ';'	

UsingDirs ::=
  UsingDir |
  UsingDirs UsingDir

UsingDir ::=
  UsingAliasDir |
  UsingNspDir

UsingAliasDir ::= USING Id '=' NspTypeName ';'

UsingNspDir ::= USING NspName ';'

NspMemDecls ::=
  GblAttrs NspDecl    |
  NspDecl             |
  TypeDecl            |
  NspMemDecls NspDecl |
  NspMemDecls TypeDecl

TypeDecl ::=
  ClsDecl  |
  StrDecl  |
  IntfDecl |
  EnumDecl |
  DelegateDecl
  
  
-- *** Classes ***

ClsDecl ::=
                          ClsDesc ClsBody     |
                          ClsDesc ClsBody ';' |
                  Partial ClsDesc ClsBody     |
                  Partial ClsDesc ClsBody ';' |
        Modifiers         ClsDesc ClsBody     |
        Modifiers         ClsDesc ClsBody ';' |
        Modifiers Partial ClsDesc ClsBody     |
        Modifiers Partial ClsDesc ClsBody ';' |
  Attrs                   ClsDesc ClsBody     |
  Attrs                   ClsDesc ClsBody ';' |
  Attrs           Partial ClsDesc ClsBody     |
  Attrs           Partial ClsDesc ClsBody ';' |
  Attrs Modifiers         ClsDesc ClsBody     |
  Attrs Modifiers         ClsDesc ClsBody ';' |
  Attrs Modifiers Partial ClsDesc ClsBody     |
  Attrs Modifiers Partial ClsDesc ClsBody ';'

ClsDesc ::= 
  CLASS Id                                   |
  CLASS Id                       Constraints |
  CLASS Id               ClsBase             |
  CLASS Id               ClsBase Constraints |
  CLASS Id TypeParamList                     |
  CLASS Id TypeParamList         Constraints |
  CLASS Id TypeParamList ClsBase             |
  CLASS Id TypeParamList ClsBase Constraints

Modifiers ::=
  Modifier |
  Modifiers Modifier
  
Modifier ::=
  Abstract  |
  Extern    | 
  Internal  |
  New       |
  Override  |
  Private   |
  Protected |
  Public    |
  Readonly  |
  Sealed    |
  Static    |
  Virtual   |
  Volatile  |
  Unsafe

ClsBase ::=
  ':' ClsIntfType  |
  ':' ClsIntfType ',' IntfTypeList

IntfTypeList ::=
  IntfType |
  IntfTypeList ',' IntfType

ClsBody ::=
  '{'             '}' |
  '{' ClsMemDecls '}'

ClsMemDecls ::=
  ClsMemDecl |
  ClsMemDecls ClsMemDecl

ClsMemDecl ::=  
  ConstDecl  |
  FldDecl    |
  MethodDecl |
  PropDecl   |
  EvDecl     |
  IdxrDecl   |
  OperDecl   |
  ConstrDecl |
  DestrDecl  |
  TypeDecl

ConstDecl ::=
                  CONST Type ConstDclrs ';' |
        Modifiers CONST Type ConstDclrs ';' |
  Attrs           CONST Type ConstDclrs ';' |
  Attrs Modifiers CONST Type ConstDclrs ';'


FldDecl ::=
                  Type VarDclrs ';' |
        Modifiers Type VarDclrs ';' |
  Attrs           Type VarDclrs ';' |
  Attrs Modifiers Type VarDclrs ';'
  
VarDclrs ::=
  VarDclr |
  VarDclrs ',' VarDclr
  
VarDclr ::=
  Id |
  Id '=' VarInit

VarInit ::=
  Expr |
  ArrInit

MethodDecl ::= MethodHdr MethodBody

MethodHdr ::= 
                  VOID MethodDesc |
                  Type MethodDesc |
        Modifiers VOID MethodDesc |
        Modifiers Type MethodDesc |
  Attrs           VOID MethodDesc |
  Attrs           Type MethodDesc |
  Attrs Modifiers VOID MethodDesc |
  Attrs Modifiers Type MethodDesc

MethodDesc ::=   
  MemName               '('                 ')'             |
  MemName               '(' FormalParamList ')'             |
  MemName TypeParamList '('                 ')'             |
  MemName TypeParamList '('                 ')' Constraints |
  MemName TypeParamList '(' FormalParamList ')'             |
  MemName TypeParamList '(' FormalParamList ')' Constraints
    
MemName ::= QName

MethodBody ::=
  Blk |
  ';'

FormalParamList ::=
  FixedParams                  |
  FixedParams ',' ParamArr     |
  ParamArr                     |
  FixedParams ',' ArgLstParam  |
  ArgLstParam

FixedParams ::=
  FixedParam |
  FixedParams ',' FixedParam

FixedParam ::=
                      Type Id |
        ParamModifier Type Id |
  Attrs               Type Id |
  Attrs ParamModifier Type Id
  
ParamModifier ::=
  REF |
  OUT

ParamArr ::=
        PARAMS ArrType Id |
  Attrs PARAMS ArrType Id

ArgLstParam ::= ARGLIST

PropDecl ::=
                  Type MemName '{' AcsrDecls '}' |
        Modifiers Type MemName '{' AcsrDecls '}' |
  Attrs           Type MemName '{' AcsrDecls '}' |
  Attrs Modifiers Type MemName '{' AcsrDecls '}'

AcsrDecls ::=
  GetAcsrDecl             |
  GetAcsrDecl SetAcsrDecl |
  SetAcsrDecl             |
  SetAcsrDecl GetAcsrDecl

GetAcsrDecl ::=
                  GET AcsrBody |
  Attrs           GET AcsrBody |
        Modifiers GET AcsrBody |
  Attrs Modifiers GET AcsrBody
  
SetAcsrDecl ::=
                  SET AcsrBody |
  Attrs           SET AcsrBody |
        Modifiers SET AcsrBody |
  Attrs Modifiers SET AcsrBody
  
AcsrBody ::=
  Blk |
  ';'

EvDecl ::=
                  EVENT Type VarDclrs ';'                |
        Modifiers EVENT Type VarDclrs ';'                | 
  Attrs           EVENT Type VarDclrs ';'                |
  Attrs Modifiers EVENT Type VarDclrs ';'                |
                  EVENT Type MemName '{' EvAcsrDecls '}' |
        Modifiers EVENT Type MemName '{' EvAcsrDecls '}' |
  Attrs           EVENT Type MemName '{' EvAcsrDecls '}' |
  Attrs Modifiers EVENT Type MemName '{' EvAcsrDecls '}' 
  
EvAcsrDecls ::=
  AddAcsrDecl RemoveAcsrDecl |
  RemoveAcsrDecl AddAcsrDecl

AddAcsrDecl ::=
        ADD Blk |
  Attrs ADD Blk
  
RemoveAcsrDecl ::=
        REMOVE Blk |
  Attrs REMOVE Blk
  
IdxrDecl ::=
                  IdxrDclr '{' AcsrDecls '}' |
        Modifiers IdxrDclr '{' AcsrDecls '}' |
  Attrs           IdxrDclr '{' AcsrDecls '}' |
  Attrs Modifiers IdxrDclr '{' AcsrDecls '}'
  
IdxrDclr ::=
  Type THIS '[' FormalParamList ']' |
  Type IntfType '.' THIS '[' FormalParamList ']'

OperDecl ::=
                  OperDclr OperBody |
        Modifiers OperDclr OperBody |
  Attrs           OperDclr OperBody |
  Attrs Modifiers OperDclr OperBody
  
OperDclr ::=
  UnaryOperDclr |
  BinOperDclr   |
  ConvOperDclr

UnaryOperDclr ::= 
  PlusUnaryOper  |
  MinusUnaryOper |
  NotUnaryOper   |
  InvUnaryOper   |
  IncrUnaryOper  |
  DecrUnaryOper  |
  TrueUnaryOper  |
  FalseUnaryOper
  
PlusUnaryOper ::= Type OPERATOR '+' '(' Type Id ')'  

MinusUnaryOper ::= Type OPERATOR '-' '(' Type Id ')'  

NotUnaryOper ::= Type OPERATOR '!' '(' Type Id ')'  

InvUnaryOper ::= Type OPERATOR '~' '(' Type Id ')'  

IncrUnaryOper ::= Type OPERATOR '++' '(' Type Id ')' 

DecrUnaryOper ::= Type OPERATOR '--' '(' Type Id ')' 

TrueUnaryOper ::= Type OPERATOR TRUE '(' Type Id ')' 

FalseUnaryOper ::= Type OPERATOR FALSE '(' Type Id ')'

BinOperDclr ::= 
  PlusBinOper  |
  MinusBinOper |
  MulBinOper   |
  DivBinOper   |
  ModBinOper   |
  AndBinOper   |
  OrBinOper    |
  XorBinOper   |
  LShBinOper   |
  RShBinOper   |
  EqBinOper    |
  NotEqBinOper |
  GtBinOper    |
  LessBinOper  |
  GtEqBinOper  |
  LessEqBinOper
    
PlusBinOper ::= Type OPERATOR '+' '(' Type Id ',' Type Id ')'         

MinusBinOper ::= Type OPERATOR '-' '(' Type Id ',' Type Id ')'         

MulBinOper ::= Type OPERATOR '*' '(' Type Id ',' Type Id ')'         

DivBinOper ::= Type OPERATOR '/' '(' Type Id ',' Type Id ')'         

ModBinOper ::= Type OPERATOR '%' '(' Type Id ',' Type Id ')'         

AndBinOper ::= Type OPERATOR '&' '(' Type Id ',' Type Id ')'         

OrBinOper ::= Type OPERATOR '|' '(' Type Id ',' Type Id ')'         

XorBinOper ::= Type OPERATOR '^' '(' Type Id ',' Type Id ')'         

LShBinOper ::= Type OPERATOR '<<' '(' Type Id ',' Type Id ')'         

RShBinOper ::= 
  Type OPERATOR '>' '>' '(' Type Id ',' Type Id ')'     |
  Type OPERATOR '>' '>' '_' '(' Type Id ',' Type Id ')' 

EqBinOper ::= Type OPERATOR '==' '(' Type Id ',' Type Id ')'        

NotEqBinOper ::= Type OPERATOR '!=' '(' Type Id ',' Type Id ')'        

GtBinOper ::= 
  Type OPERATOR '>' '(' Type Id ',' Type Id ')'    |
  Type OPERATOR '>' '_' '(' Type Id ',' Type Id ')'     

LessBinOper ::= Type OPERATOR '<' '(' Type Id ',' Type Id ')'         

GtEqBinOper ::= Type OPERATOR '>' '=' '(' Type Id ',' Type Id ')'     

LessEqBinOper ::= Type OPERATOR '<=' '(' Type Id ',' Type Id ')'

ConvOperDclr ::=
  ImplConvOper |
  ExplConvOper
  
ImplConvOper ::= IMPLICIT OPERATOR Type '(' Type Id ')'

ExplConvOper ::= EXPLICIT OPERATOR Type '(' Type Id ')'

OperBody ::=
  Blk |
  ';'

ConstrDecl ::=
                  ConstrDclr ConstrBody |
        Modifiers ConstrDclr ConstrBody |
  Attrs           ConstrDclr ConstrBody |
  Attrs Modifiers ConstrDclr ConstrBody
  
ConstrDclr ::=
  Id '('                 ')'            |
  Id '('                 ')' ConstrInit |
  Id '(' FormalParamList ')'            |
  Id '(' FormalParamList ')' ConstrInit

ConstrInit ::=
  BaseInit |
  ThisInit
  
BaseInit ::=
  ':' BASE '(' ArgList ')' |
  ':' BASE '('         ')'

ThisInit ::=
  ':' THIS '(' ArgList ')' |
  ':' THIS '('         ')'

ConstrBody ::=
  Blk |
  ';'

DestrDecl ::=
                  '~' Id '(' ')' DestrBody |
        Modifiers '~' Id '(' ')' DestrBody |
  Attrs           '~' Id '(' ')' DestrBody |
  Attrs Modifiers '~' Id '(' ')' DestrBody

DestrBody ::=
  Blk |
  ';'


-- *** Structs ***

StrDecl ::=
                          StrDesc StrBody     |
                          StrDesc StrBody ';' |                          
                  Partial StrDesc StrBody     |
                  Partial StrDesc StrBody ';' |
        Modifiers         StrDesc StrBody     |
        Modifiers         StrDesc StrBody ';' |
        Modifiers Partial StrDesc StrBody     |
        Modifiers Partial StrDesc StrBody ';' |
  Attrs                   StrDesc StrBody     |
  Attrs                   StrDesc StrBody ';' |
  Attrs           Partial StrDesc StrBody     |
  Attrs           Partial StrDesc StrBody ';' |
  Attrs Modifiers         StrDesc StrBody     |
  Attrs Modifiers         StrDesc StrBody ';' |
  Attrs Modifiers Partial StrDesc StrBody     |
  Attrs Modifiers Partial StrDesc StrBody ';'

StrDesc ::=
  STRUCT Id                                    |
  STRUCT Id               StrIntfs             |
  STRUCT Id TypeParamList                      |
  STRUCT Id TypeParamList          Constraints |
  STRUCT Id TypeParamList StrIntfs             |
  STRUCT Id TypeParamList StrIntfs Constraints
  
StrIntfs ::= ':' IntfTypeList

StrBody ::=
  '{'             '}' |
  '{' StrMemDecls '}'

StrMemDecls ::=
  StrMemDecl |
  StrMemDecls StrMemDecl

StrMemDecl ::=
  ConstDecl  |
  FldDecl    |
  MethodDecl |
  PropDecl   |
  EvDecl     |
  IdxrDecl   |
  OperDecl   |
  ConstrDecl |
  TypeDecl   |
  FixedSizeBufDecl
  
FixedSizeBufDecl ::=
                  FIXED BufElemType FixedSizeBufDclrs ';' |
        Modifiers FIXED BufElemType FixedSizeBufDclrs ';' |
  Attrs           FIXED BufElemType FixedSizeBufDclrs ';' |
  Attrs Modifiers FIXED BufElemType FixedSizeBufDclrs ';'	
  
BufElemType ::= Type

FixedSizeBufDclrs ::=
  FixedSizeBufDclr |
  FixedSizeBufDclr ',' FixedSizeBufDclrs
  
FixedSizeBufDclr ::= Id '[' ConstExpr ']'	

  
-- *** Arrays ***

ArrInit ::=
  '{'             '}' |
  '{' VarInitList '}' |
  '{' VarInitList ',' '}'

VarInitList ::=
  VarInit |
  VarInitList ',' VarInit
  
  
-- *** Interfaces ***

IntfDecl ::=
                          IntfDesc IntfBody     |
                          IntfDesc IntfBody ';' |
                  Partial IntfDesc IntfBody     |
                  Partial IntfDesc IntfBody ';' |
        Modifiers         IntfDesc IntfBody     |
        Modifiers         IntfDesc IntfBody ';' |
        Modifiers Partial IntfDesc IntfBody     |
        Modifiers Partial IntfDesc IntfBody ';' |
  Attrs                   IntfDesc IntfBody     |
  Attrs                   IntfDesc IntfBody ';' |
  Attrs           Partial IntfDesc IntfBody     |
  Attrs           Partial IntfDesc IntfBody ';' |
  Attrs Modifiers         IntfDesc IntfBody     |
  Attrs Modifiers         IntfDesc IntfBody ';' |
  Attrs Modifiers Partial IntfDesc IntfBody     |
  Attrs Modifiers Partial IntfDesc IntfBody ';'

IntfDesc ::=
  INTERFACE Id                                      |
  INTERFACE Id               IntfBase               |
  INTERFACE Id TypeParamList                        |
  INTERFACE Id TypeParamList          Constraints   |
  INTERFACE Id TypeParamList IntfBase               |
  INTERFACE Id TypeParamList IntfBase Constraints
  
IntfBase ::= ':' IntfTypeList

IntfBody ::=
  '{'              '}' |
  '{' IntfMemDecls '}'
  
IntfMemDecls ::=
  IntfMemDecl |
  IntfMemDecls IntfMemDecl

IntfMemDecl ::=
  IntfMethodDecl |
  IntfPropDecl   |
  IntfEvDecl     |
  IntfIdxrDecl

IntfMethodDecl ::=
                  VOID IntfMethodDesc ';' |
                  Type IntfMethodDesc ';' |
        Modifiers VOID IntfMethodDesc ';' |
        Modifiers Type IntfMethodDesc ';' |
  Attrs           VOID IntfMethodDesc ';' |
  Attrs           Type IntfMethodDesc ';' |
  Attrs Modifiers VOID IntfMethodDesc ';' |
  Attrs Modifiers Type IntfMethodDesc ';'
  
IntfMethodDesc ::=
	Id               '('                 ')'             |
	Id               '(' FormalParamList ')'             |
	Id TypeParamList '('                 ')'             |
	Id TypeParamList '('                 ')' Constraints |
	Id TypeParamList '(' FormalParamList ')'             |
	Id TypeParamList '(' FormalParamList ')' Constraints

IntfPropDecl ::=
                  Type Id '{' IntfAcsrs '}' |
        Modifiers Type Id '{' IntfAcsrs '}' |
  Attrs           Type Id '{' IntfAcsrs '}' |
  Attrs Modifiers Type Id '{' IntfAcsrs '}'

IntfAcsrs ::=
  IntfGetAcsr             |
  IntfSetAcsr             |
  IntfGetAcsr IntfSetAcsr |
  IntfSetAcsr IntfGetAcsr

IntfGetAcsr ::=
  Attrs GET ';' |
        GET ';'
        
IntfSetAcsr ::=
  Attrs SET ';' |
 	    SET ';'

IntfEvDecl ::=
                  EVENT Type Id ';' |
        Modifiers EVENT Type Id ';' |
  Attrs           EVENT Type Id ';' |
  Attrs Modifiers EVENT Type Id ';'
  
IntfIdxrDecl ::=
                  Type THIS '[' FormalParamList ']' '{' IntfAcsrs '}' |
        Modifiers Type THIS '[' FormalParamList ']' '{' IntfAcsrs '}' |
  Attrs           Type THIS '[' FormalParamList ']' '{' IntfAcsrs '}' |
  Attrs Modifiers Type THIS '[' FormalParamList ']' '{' IntfAcsrs '}'
  

-- *** Enums ***

EnumDecl ::=
                  EnumDesc EnumBody     |
                  EnumDesc EnumBody ';' |                  
        Modifiers EnumDesc EnumBody     |
        Modifiers EnumDesc EnumBody ';' |
  Attrs           EnumDesc EnumBody     |
  Attrs           EnumDesc EnumBody ';' |
  Attrs Modifiers EnumDesc EnumBody     |
  Attrs Modifiers EnumDesc EnumBody ';'
  
EnumDesc ::=
  ENUM Id |
  ENUM Id EnumBase  
  
EnumBase ::= ':' IntType

EnumBody ::=
  '{'              '}' |
  '{' EnumMemDecls '}' |
  '{' EnumMemDecls ',' '}'

EnumMemDecls ::=
  EnumMemDecl |
  EnumMemDecls ',' EnumMemDecl
  
EnumMemDecl ::=
        Id               |
        Id '=' ConstExpr |
  Attrs Id               |
  Attrs Id '=' ConstExpr


-- *** Delegates ***

DelegateDecl ::=
                  DELEGATE VOID DelegateDesc ';' |
                  DELEGATE Type DelegateDesc ';' |
        Modifiers DELEGATE VOID DelegateDesc ';' |
        Modifiers DELEGATE Type DelegateDesc ';' |
  Attrs           DELEGATE VOID DelegateDesc ';' |
  Attrs           DELEGATE Type DelegateDesc ';' |
  Attrs Modifiers DELEGATE VOID DelegateDesc ';' |
  Attrs Modifiers DELEGATE Type DelegateDesc ';'

DelegateDesc ::=
  Id               '('                 ')'             |
  Id               '(' FormalParamList ')'             |
  Id TypeParamList '('                 ')'             |
  Id TypeParamList '(' FormalParamList ')'             |
  Id TypeParamList '('                 ')' Constraints |
  Id TypeParamList '(' FormalParamList ')' Constraints



-- *** Attributes ***

GblAttrs ::= Attrs
  
Attrs ::= AttrSecs

AttrSecs ::=
  AttrSec |
  AttrSecs AttrSec

AttrSec ::=
  '['                AttrList     ']' |
  '[' AttrTargetSpec AttrList     ']' |
  '['                AttrList ',' ']' |
  '[' AttrTargetSpec AttrList ',' ']'

AttrTargetSpec ::= AttrTarget ':'

AttrTarget ::=
    ID         |
	Abstract   |
	Add        |
	Alias      |
	As         |
	Assembly   |
	Base       |
	Bool       |
	Break      |
	Byte       |
	Case       |
	Catch      |
	Char       |
	Checked    |
	Class      |
	Const      |
	Continue   |
	Decimal    |
	Default    |
	Delegate   |
	Do         |
	Double     | 
	Else       |
	Enum       |
	Event      |
	Explicit   |
	Extern     |
	False      |
	Field      |
	Finally    |
	Fixed      |
	Float      |
	For        |
	Foreach    |
	Get        |
	Goto       |
	If         |
	Implicit   |
	In         |
	Int        |
	Interface  |
	Internal   |
	Is         |
	Lock       |
	Long       |
	Method     |
	Module     |
	Namespace  |
	New        |
	Null       |
	Object     |
	Operator   |
	Out        |
	Override   |
	Param      |
	Params     |
	Partial    |
	Private    |
	Property   |
	Protected  |
	Public     |
	Readonly   |
	Ref        |
	Remove     |
	Return     |
	Sbyte      |
	Sealed     |
	Set        |
	Short      | 
	Sizeof     |
	Stackalloc |
	Static     |
	String     |
	Struct     |
	Switch     |
 	This       |
	Throw      |
	True       |
	Try        |
	Typ        |
	Typeof     |
	Uint       |
	Ulong      |
	Unchecked  |
	Unsafe     |
	Ushort     |
	Using      |
	Virtual    |
 	Void       |
	Volatile   |
	While      |
	Where      |
	Yield

Abstract ::= ABSTRACT   

Add ::= ADD        

Alias ::= ALIAS      

As ::= AS         

Assembly ::= ASSEMBLY   

Base ::= BASE       

Bool ::= BOOL       

Break ::= BREAK      

Byte ::= BYTE       

Case ::= CASE       

Catch ::= CATCH      

Char ::= CHAR       

Checked ::= CHECKED    

Class ::= CLASS      

Const ::= CONST      

Continue ::= CONTINUE   

Decimal ::= DECIMAL    

Default ::= DEFAULT    

Delegate ::= DELEGATE   

Do ::= DO         

Double ::= DOUBLE      

Else ::= ELSE       

Enum ::= ENUM       

Event ::= EVENT      

Explicit ::= EXPLICIT   

Extern ::= EXTERN     

False ::= FALSE      

Field ::= FIELD      

Finally ::= FINALLY    

Fixed ::= FIXED      

Float ::= FLOAT      

For ::= FOR        

Foreach ::= FOREACH    

Get ::= GET        

Goto ::= GOTO       

If ::= IF         

Implicit ::= IMPLICIT   

In ::= IN         

Int ::= INT        

Interface ::= INTERFACE  

Internal ::= INTERNAL   

Is ::= IS         

Lock ::= LOCK       

Long ::= LONG       

Method ::= METHOD     

Module ::= MODULE     

Namespace ::= NAMESPACE  

New ::= NEW        

Null ::= NULL       

Object ::= OBJECT     

Operator ::= OPERATOR   

Out ::= OUT        

Override ::= OVERRIDE   

Param ::= PARAM      

Params ::= PARAMS     

Partial ::= PARTIAL    

Private ::= PRIVATE    

Property ::= PROPERTY   

Protected ::= PROTECTED  

Public ::= PUBLIC     

Readonly ::= READONLY   

Ref ::= REF        

Remove ::= REMOVE     

Return ::= RETURN     

Sbyte ::= SBYTE      

Sealed ::= SEALED     

Set ::= SET        

Short ::= SHORT       

Sizeof ::= SIZEOF     

Stackalloc ::= STACKALLOC 

Static ::= STATIC     

String ::= STRING     

Struct ::= STRUCT     

Switch ::= SWITCH     

This ::= THIS       

Throw ::= THROW      

True ::= TRUE       

Try ::= TRY        

Typ ::= TYPE       

Typeof ::= TYPEOF     

Uint ::= UINT       

Ulong ::= ULONG      

Unchecked ::= UNCHECKED  

Unsafe ::= UNSAFE     

Ushort ::= USHORT     

Using ::= USING      

Virtual ::= VIRTUAL    

Void ::= VOID       

Volatile ::= VOLATILE   

While ::= WHILE      

Where ::= WHERE      

Yield ::= YIELD

AttrList ::=
  Attr |
  AttrList ',' Attr

Attr ::=
  AttrName |
  AttrName AttrArgs

AttrName ::= TypeName

AttrArgs ::=
  '('             ')'                 | 
  '(' PosArgList  ')'                 |
  '(' PosArgList ',' NamedArgList ')' |
  '(' NamedArgList ')'

PosArgList ::=
  PosArg |
  PosArgList ',' PosArg
  
PosArg ::= AttrArgExpr
  
NamedArgList ::=
  NamedArg |
  NamedArgList ',' NamedArg

NamedArg ::= Id '=' Expr

AttrArgExpr ::= CondExprGr

