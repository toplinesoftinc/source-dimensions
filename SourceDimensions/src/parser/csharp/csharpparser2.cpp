#include "csharpparser2.h"
#include "csharplexer2.h"
#include "csha2def.h"
#include "csha2prs.h"
#include "csha2dcl.h"
#include "csha2sym.h"
#include "../common/symbol.h"


#define BUFSIZE 128

static char strbuf[BUFSIZE];



char *CSharpParser2::GetTermName(int code)
{
	int len = CLASS_HEADER name_length[CLASS_HEADER terminal_index[code]];

	strncpy(strbuf, &CLASS_HEADER string_buffer[CLASS_HEADER name_start[CLASS_HEADER terminal_index[code]]], len);
	strbuf[len] = '\0';

	return strbuf;
}


char *CSharpParser2::GetNtName(int code)
{
	int len = CLASS_HEADER name_length[CLASS_HEADER non_terminal_index[code]];

	strncpy(strbuf, &CLASS_HEADER string_buffer[CLASS_HEADER name_start[CLASS_HEADER non_terminal_index[code]]], len);
	strbuf[len] = '\0';

	return strbuf;
}


bool CSharpParser2::FilterCallback(CSymbol *sym)
{
	if (sym->terminal)
	{
		switch (sym->code)
		{
			case TK_CS2_BOOL:
			case TK_CS2_BYTE:
			case TK_CS2_CHAR:
			case TK_CS2_CHAR_LITERAL:
			case TK_CS2_DECIMAL:
			case TK_CS2_DOUBLE:
			case TK_CS2_FLOAT:
			case TK_CS2_ID:
			case TK_CS2_INT:
			case TK_CS2_INT_LITERAL:
			case TK_CS2_LONG:
			case TK_CS2_NULL:
			case TK_CS2_OBJECT:
			case TK_CS2_OUT:
			case TK_CS2_REAL_LITERAL:
			case TK_CS2_REF:
			case TK_CS2_SBYTE:
			case TK_CS2_SHORT:
			case TK_CS2_STRING:
			case TK_CS2_STR_LITERAL:
			case TK_CS2_UINT:
			case TK_CS2_ULONG:
			case TK_CS2_USHORT:
			case TK_CS2_VOID:
				return true;

			default:
				return false;
		}
	}
	else
	{ 
		switch (sym->code)
		{
			case Nt_Cs2_Abstract:
			case Nt_Cs2_Add:
			case Nt_Cs2_AddAcsrDecl:
			case Nt_Cs2_AddExpr:
			case Nt_Cs2_AddrofExpr:
			case Nt_Cs2_Alias:
			case Nt_Cs2_AndAssn:
			case Nt_Cs2_AndBinOper:
			case Nt_Cs2_AndExpr:
			case Nt_Cs2_AnonymMethodExpr:
			case Nt_Cs2_AnonymMethodParam:
			case Nt_Cs2_Arg:
			case Nt_Cs2_ArrCreatExpr:
			case Nt_Cs2_ArrInit:
			case Nt_Cs2_ArrType:
			case Nt_Cs2_As:
			case Nt_Cs2_AsExpr:
			case Nt_Cs2_Assembly:
			case Nt_Cs2_Assn:
			case Nt_Cs2_Attr:
			case Nt_Cs2_AttrSec:
			case Nt_Cs2_Base:
			case Nt_Cs2_BaseAccess:
			case Nt_Cs2_BaseInit:
			case Nt_Cs2_Blk:
			case Nt_Cs2_Bool:
			case Nt_Cs2_Break:
			case Nt_Cs2_BreakStmt:
			case Nt_Cs2_Byte:
			case Nt_Cs2_Case:
			case Nt_Cs2_CastExpr:
			case Nt_Cs2_Catch:
			case Nt_Cs2_Char:
			case Nt_Cs2_Checked:
			case Nt_Cs2_CheckedExpr:
			case Nt_Cs2_CheckedStmt:
			case Nt_Cs2_Class:
			case Nt_Cs2_ClsBody:
			case Nt_Cs2_ClsDecl:
			case Nt_Cs2_ClsIntfType:
			case Nt_Cs2_ClsType:
			case Nt_Cs2_Comma:
			case Nt_Cs2_CompilationUnit:
			case Nt_Cs2_CondAndExpr:
			case Nt_Cs2_CondExpr:
			case Nt_Cs2_CondOrExpr:
			case Nt_Cs2_Const:
			case Nt_Cs2_ConstDclr:
			case Nt_Cs2_ConstDecl:
			case Nt_Cs2_ConstrCnrt:
			case Nt_Cs2_ConstrDecl:
			case Nt_Cs2_ConstrInit:
			case Nt_Cs2_Constraint:
			case Nt_Cs2_Continue:
			case Nt_Cs2_ContinueStmt:
			case Nt_Cs2_Decimal:
			case Nt_Cs2_DeclStmt:
			case Nt_Cs2_DecrUnaryOper:
			case Nt_Cs2_Default:
			case Nt_Cs2_DefaultValExpr:
			case Nt_Cs2_Delegate:
			case Nt_Cs2_DelegateDecl:
			case Nt_Cs2_DestrDecl:
			case Nt_Cs2_DimSep:
			case Nt_Cs2_DivAssn:
			case Nt_Cs2_DivBinOper:
			case Nt_Cs2_DivExpr:
			case Nt_Cs2_Do:
			case Nt_Cs2_DoStmt:
			case Nt_Cs2_Double:
			case Nt_Cs2_ElemAccess:
			case Nt_Cs2_Else:
			case Nt_Cs2_EmbedStmt:
			case Nt_Cs2_EmptyStmt:
			case Nt_Cs2_Enum:
			case Nt_Cs2_EnumBase:
			case Nt_Cs2_EnumBody:
			case Nt_Cs2_EnumDecl:
			case Nt_Cs2_EnumMemDecl:
			case Nt_Cs2_EqBinOper:
			case Nt_Cs2_EqExpr:
			case Nt_Cs2_EvDecl:
			case Nt_Cs2_Event:
			case Nt_Cs2_ExclOrExpr:
			case Nt_Cs2_ExplConvOper:
			case Nt_Cs2_Explicit:
			case Nt_Cs2_Expr:
			case Nt_Cs2_Extern:
			case Nt_Cs2_ExternAliasDir:
			case Nt_Cs2_False:
			case Nt_Cs2_FalseUnaryOper:
			case Nt_Cs2_Field:
			case Nt_Cs2_Finally:
			case Nt_Cs2_FinallyBlk:
			case Nt_Cs2_Fixed:
			case Nt_Cs2_FixedParam:
			case Nt_Cs2_FixedPtrDclr:
			case Nt_Cs2_FixedSizeBufDclr:
			case Nt_Cs2_FixedSizeBufDecl:
			case Nt_Cs2_FixedStmt:
			case Nt_Cs2_FldDecl:
			case Nt_Cs2_Float:
			case Nt_Cs2_For:
			case Nt_Cs2_ForInit:
			case Nt_Cs2_ForIter:
			case Nt_Cs2_ForStmt:
			case Nt_Cs2_Foreach:
			case Nt_Cs2_ForeachStmt:
			case Nt_Cs2_GblAttrs:
			case Nt_Cs2_GeneralCatch:
			case Nt_Cs2_Get:
			case Nt_Cs2_GetAcsrDecl:
			case Nt_Cs2_Goto:
			case Nt_Cs2_GotoCase:
			case Nt_Cs2_GotoDefault:
			case Nt_Cs2_GotoId:
			case Nt_Cs2_GtBinOper:
			case Nt_Cs2_GtEqBinOper:
			case Nt_Cs2_GtEqExpr:
			case Nt_Cs2_GtExpr:
			case Nt_Cs2_Id:
			case Nt_Cs2_IdxrDecl:
			case Nt_Cs2_If:
			case Nt_Cs2_IfStmt:
			case Nt_Cs2_ImplConvOper:
			case Nt_Cs2_Implicit:
			case Nt_Cs2_In:
			case Nt_Cs2_InclOrExpr:
			case Nt_Cs2_IncrUnaryOper:
			case Nt_Cs2_Int:
			case Nt_Cs2_Interface:
			case Nt_Cs2_Internal:
			case Nt_Cs2_IntfBody:
			case Nt_Cs2_IntfDecl:
			case Nt_Cs2_IntfEvDecl:
			case Nt_Cs2_IntfGetAcsr:
			case Nt_Cs2_IntfIdxrDecl:
			case Nt_Cs2_IntfMethodDecl:
			case Nt_Cs2_IntfPropDecl:
			case Nt_Cs2_IntfSetAcsr:
			case Nt_Cs2_IntfType:
			case Nt_Cs2_InvExpr:
			case Nt_Cs2_InvUnaryOper:
			case Nt_Cs2_InvocExpr:
			case Nt_Cs2_Is:
			case Nt_Cs2_IsExpr:
			case Nt_Cs2_LShAssn:
			case Nt_Cs2_LShBinOper:
			case Nt_Cs2_LShiftExpr:
			case Nt_Cs2_LblStmt:
			case Nt_Cs2_LessBinOper:
			case Nt_Cs2_LessEqBinOper:
			case Nt_Cs2_LessExpr:
			case Nt_Cs2_LocalConstDecl:
			case Nt_Cs2_LocalVarDclr:
			case Nt_Cs2_LocalVarDecl:
			case Nt_Cs2_LocalVarInit:
			case Nt_Cs2_Lock:
			case Nt_Cs2_LockStmt:
			case Nt_Cs2_Long:
			case Nt_Cs2_LsEqExpr:
			case Nt_Cs2_MemAccess:
			case Nt_Cs2_Method:
			case Nt_Cs2_MethodDecl:
			case Nt_Cs2_MinusAssn:
			case Nt_Cs2_MinusBinOper:
			case Nt_Cs2_MinusExpr:
			case Nt_Cs2_MinusUnaryOper:
			case Nt_Cs2_ModAssn:
			case Nt_Cs2_ModBinOper:
			case Nt_Cs2_ModExpr:
			case Nt_Cs2_Module:
			case Nt_Cs2_MulBinOper:
			case Nt_Cs2_MultAssn:
			case Nt_Cs2_MultExpr:
			case Nt_Cs2_NamedArg:
			case Nt_Cs2_Namespace:
			case Nt_Cs2_New:
			case Nt_Cs2_NotEqBinOper:
			case Nt_Cs2_NotEqExpr:
			case Nt_Cs2_NotExpr:
			case Nt_Cs2_NotUnaryOper:
			case Nt_Cs2_NspBody:
			case Nt_Cs2_NspDecl:
			case Nt_Cs2_Null:
			case Nt_Cs2_NullCoalesExpr:
			case Nt_Cs2_NullType:
			case Nt_Cs2_ObjCreatExpr:
			case Nt_Cs2_Object:
			case Nt_Cs2_OperDecl:
			case Nt_Cs2_Operator:
			case Nt_Cs2_OrAssn:
			case Nt_Cs2_OrBinOper:
			case Nt_Cs2_Out:
			case Nt_Cs2_OutArg:
			case Nt_Cs2_Override:
			case Nt_Cs2_Param:
			case Nt_Cs2_ParamArr:
			case Nt_Cs2_ParamModifier:
			case Nt_Cs2_Params:
			case Nt_Cs2_ParenExpr:
			case Nt_Cs2_Partial:
			case Nt_Cs2_PlusAssn:
			case Nt_Cs2_PlusBinOper:
			case Nt_Cs2_PlusExpr:
			case Nt_Cs2_PlusUnaryOper:
			case Nt_Cs2_PosArg:
			case Nt_Cs2_PostDecrExpr:
			case Nt_Cs2_PostIncrExpr:
			case Nt_Cs2_PreDecrExpr:
			case Nt_Cs2_PreIncrExpr:
			case Nt_Cs2_PredefType:
			case Nt_Cs2_Private:
			case Nt_Cs2_PropDecl:
			case Nt_Cs2_Property:
			case Nt_Cs2_Protected:
			case Nt_Cs2_Ptr:
			case Nt_Cs2_PtrIndirExpr:
			case Nt_Cs2_PtrMemAccess:
			case Nt_Cs2_PtrType:
			case Nt_Cs2_Public:
			case Nt_Cs2_QAlias:
			case Nt_Cs2_RShAssn:
			case Nt_Cs2_RShBinOper:
			case Nt_Cs2_RShiftExpr:
			case Nt_Cs2_RankSpec:
			case Nt_Cs2_Readonly:
			case Nt_Cs2_Ref:
			case Nt_Cs2_RefArg:
			case Nt_Cs2_Remove:
			case Nt_Cs2_RemoveAcsrDecl:
			case Nt_Cs2_Return:
			case Nt_Cs2_ReturnStmt:
			case Nt_Cs2_Sbyte:
			case Nt_Cs2_Sealed:
			case Nt_Cs2_Set:
			case Nt_Cs2_SetAcsrDecl:
			case Nt_Cs2_Short:
			case Nt_Cs2_SimpleType:
			case Nt_Cs2_Sizeof:
			case Nt_Cs2_SizeofExpr:
			case Nt_Cs2_SpecificCatch:
			case Nt_Cs2_Stackalloc:
			case Nt_Cs2_StackallocInit:
			case Nt_Cs2_Static:
			case Nt_Cs2_Stmt:
			case Nt_Cs2_StmtExpr:
			case Nt_Cs2_StrBody:
			case Nt_Cs2_StrDecl:
			case Nt_Cs2_String:
			case Nt_Cs2_Struct:
			case Nt_Cs2_SubExpr:
			case Nt_Cs2_Switch:
			case Nt_Cs2_SwitchLbl:
			case Nt_Cs2_SwitchSec:
			case Nt_Cs2_SwitchStmt:
			case Nt_Cs2_This:
			case Nt_Cs2_ThisAccess:
			case Nt_Cs2_ThisInit:
			case Nt_Cs2_Throw:
			case Nt_Cs2_ThrowStmt:
			case Nt_Cs2_True:
			case Nt_Cs2_TrueUnaryOper:
			case Nt_Cs2_Try:
			case Nt_Cs2_TryStmt:
			case Nt_Cs2_Typ:
			case Nt_Cs2_Type:
			case Nt_Cs2_TypeParam:
			case Nt_Cs2_Typeof:
			case Nt_Cs2_TypeofExpr:
			case Nt_Cs2_Uint:
			case Nt_Cs2_Ulong:
			case Nt_Cs2_Unchecked:
			case Nt_Cs2_UncheckedExpr:
			case Nt_Cs2_UncheckedStmt:
			case Nt_Cs2_Unsafe:
			case Nt_Cs2_UnsafeStmt:
			case Nt_Cs2_Ushort:
			case Nt_Cs2_Using:
			case Nt_Cs2_UsingAliasDir:
			case Nt_Cs2_UsingNspDir:
			case Nt_Cs2_UsingStmt:
			case Nt_Cs2_VarDclr:
			case Nt_Cs2_VarInit:
			case Nt_Cs2_Virtual:
			case Nt_Cs2_Volatile:
			case Nt_Cs2_Where:
			case Nt_Cs2_While:
			case Nt_Cs2_WhileStmt:
			case Nt_Cs2_XorAssn:
			case Nt_Cs2_XorBinOper:
			case Nt_Cs2_Yield:
			case Nt_Cs2_YieldStmt:
			case Nt_Cs2_ArgLstParam:
			case Nt_Cs2_ArgLstExpr:
			case Nt_Cs2_MakeRefExpr:
			case Nt_Cs2_RefTypeExpr:
			case Nt_Cs2_RefValExpr:
			case Nt_Cs2_ArrCreInitExpr:
				return true;

			case Nt_Cs2_QName:
			case Nt_Cs2_Modifiers:
			case Nt_Cs2_UnboundTypeName:
				if (sym->parent->code == sym->code)
					return false;
				else
					return true;

			default:
				return false;
		}
	}
}



int CSharpParser2::GetAction(int state, int term, CLexer *lexer)
{
	return CLASS_HEADER t_action(state, term, lexer);
}


int CSharpParser2::GetGoto(int state, int nt)
{
	return CLASS_HEADER nt_action(state, nt);
}


int CSharpParser2::GetClass(int code)
{
	switch (code)
	{
		case ERROR_ACTION:	
			return CCS_ERROR;

		case ACCEPT_ACTION:
			return CCS_ACCEPT;

		default:
			if (code <= NUM_RULES)
				return CCS_REDUCE;		
			else
				return CCS_SHIFT;
	}
}


int CSharpParser2::GetLHS(int prod)
{
	return CLASS_HEADER lhs[prod];
}


int CSharpParser2::GetRHS(int prod)
{
	return CLASS_HEADER rhs[prod];
}


int CSharpParser2::GetStartState()
{
	return START_STATE;
}


int CSharpParser2::GetOriginalStateCode(int state)
{
	return CLASS_HEADER original_state(state);
}


bool CSharpParser2::GetAltAction(CLexer *lexer, int state, int &cur, int &alt)
{
	switch (state)
	{

		case 108:
		case 576:
		case 756:
			if (lexer->GetCurToken() == lexer->GetTokenCode(IDX_CS_LANGLE))
			{
				alt = 16;
				return true;
			}
			break;

		case 117:
			if (lexer->GetCurToken() == lexer->GetTokenCode(IDX_CS_QUESTION))
			{
				cur = 47;
				alt = 39;
				return true;
			}
			break;

		case 118:
			if (lexer->GetCurToken() == lexer->GetTokenCode(IDX_CS_QUESTION))
			{
				cur = 46;
				alt = 38;
				return true;
			}
			break;

		case 498:
			if (lexer->GetCurToken() == lexer->GetTokenCode(IDX_CS_LANGLE))
			{
				alt = 19;
				return true;
			}
			break;

		case 927:
			switch (lexer->GetTokenIndex(lexer->GetCurToken()))
			{
				case IDX_CS_RPAREN:
				case IDX_CS_QUESTION:
				case IDX_CS_STAR:
					cur = 14;
					alt = 224;
					return true;
			}
	}

	return false;
}


CLexer* CSharpParser2::CreateLexer()
{
	return new CSharpLexer2();
}

