#include "csharpparser.h"
#include "csharplexer.h"
#include "cshardef.h"
#include "csharprs.h"
#include "cshardcl.h"
#include "csharsym.h"
#include "../common/symbol.h"

#define BUFSIZE 128

static char strbuf[BUFSIZE];



char *CSharpParser::GetTermName(int code)
{
	int len = CLASS_HEADER name_length[CLASS_HEADER terminal_index[code]];

	strncpy(strbuf, &CLASS_HEADER string_buffer[CLASS_HEADER name_start[CLASS_HEADER terminal_index[code]]], len);
	strbuf[len] = '\0';

	return strbuf;
}


char *CSharpParser::GetNtName(int code)
{
	int len = CLASS_HEADER name_length[CLASS_HEADER non_terminal_index[code]];

	strncpy(strbuf, &CLASS_HEADER string_buffer[CLASS_HEADER name_start[CLASS_HEADER non_terminal_index[code]]], len);
	strbuf[len] = '\0';

	return strbuf;
}


bool CSharpParser::FilterCallback(CSymbol *sym)
{
	if (sym->terminal)
	{
		switch (sym->code)
		{
			case TK_CS_BOOL:
			case TK_CS_BYTE:
			case TK_CS_CHAR:
			case TK_CS_CHAR_LITERAL:
			case TK_CS_DECIMAL:
			case TK_CS_DOUBLE:
			case TK_CS_FLOAT:
			case TK_CS_ID:
			case TK_CS_INT:
			case TK_CS_INT_LITERAL:
			case TK_CS_LONG:
			case TK_CS_NULL:
			case TK_CS_OBJECT:
			case TK_CS_OUT:
			case TK_CS_REAL_LITERAL:
			case TK_CS_REF:
			case TK_CS_SBYTE:
			case TK_CS_SHORT:
			case TK_CS_STRING:
			case TK_CS_STR_LITERAL:
			case TK_CS_UINT:
			case TK_CS_ULONG:
			case TK_CS_USHORT:
			case TK_CS_VOID:
				return true;

			default:
				return false;
		}
	}
	else
	{ 
		switch (sym->code)
		{
			case Nt_Cs_Abstract:
			case Nt_Cs_Add:
			case Nt_Cs_AddAcsrDecl:
			case Nt_Cs_AddExpr:
			case Nt_Cs_AddrofExpr:
			case Nt_Cs_AndAssn:
			case Nt_Cs_AndBinOper:
			case Nt_Cs_AndExpr:
			case Nt_Cs_Arg:
			case Nt_Cs_ArrCreatExpr:
			case Nt_Cs_ArrInit:
			case Nt_Cs_ArrType:
			case Nt_Cs_AsExpr:
			case Nt_Cs_Assembly:
			case Nt_Cs_Assn:
			case Nt_Cs_Attr:
			case Nt_Cs_AttrSec:
			case Nt_Cs_BaseAccess:
			case Nt_Cs_BaseInit:
			case Nt_Cs_Blk:
			case Nt_Cs_BreakStmt:
			case Nt_Cs_CastExpr:
			case Nt_Cs_CheckedExpr:
			case Nt_Cs_CheckedStmt:
			case Nt_Cs_ClsBody:
			case Nt_Cs_ClsDecl:
			case Nt_Cs_ClsIntfType:
			case Nt_Cs_ClsType:
			case Nt_Cs_CompUnit:
			case Nt_Cs_CondAndExpr:
			case Nt_Cs_CondExpr:
			case Nt_Cs_CondOrExpr:
			case Nt_Cs_ConstDclr:
			case Nt_Cs_ConstDecl:
			case Nt_Cs_ConstrDecl:
			case Nt_Cs_ConstrInit:
			case Nt_Cs_ContinueStmt:
			case Nt_Cs_DeclStmt:
			case Nt_Cs_DecrUnaryOper:
			case Nt_Cs_DelegateDecl:
			case Nt_Cs_DestrDecl:
			case Nt_Cs_DimSep:
			case Nt_Cs_DivAssn:
			case Nt_Cs_DivBinOper:
			case Nt_Cs_DivExpr:
			case Nt_Cs_DoStmt:
			case Nt_Cs_ElemAccess:
			case Nt_Cs_EmbedStmt:
			case Nt_Cs_EmptyStmt:
			case Nt_Cs_EnumBase:
			case Nt_Cs_EnumBody:
			case Nt_Cs_EnumDecl:
			case Nt_Cs_EnumMemDecl:
			case Nt_Cs_EqBinOper:
			case Nt_Cs_EqExpr:
			case Nt_Cs_EvDecl:
			case Nt_Cs_Event:
			case Nt_Cs_ExclOrExpr:
			case Nt_Cs_ExplConvOper:
			case Nt_Cs_Expr:
			case Nt_Cs_Extern:
			case Nt_Cs_False:
			case Nt_Cs_FalseUnaryOper:
			case Nt_Cs_Field:
			case Nt_Cs_FinallyBlk:
			case Nt_Cs_FixedParam:
			case Nt_Cs_FixedPtrDclr:
			case Nt_Cs_FixedStmt:
			case Nt_Cs_FldDecl:
			case Nt_Cs_ForInit:
			case Nt_Cs_ForIter:
			case Nt_Cs_ForStmt:
			case Nt_Cs_ForeachStmt:
			case Nt_Cs_GblAttrs:
			case Nt_Cs_GblAttrSec:
			case Nt_Cs_GeneralCatch:
			case Nt_Cs_Get:
			case Nt_Cs_GetAcsrDecl:
			case Nt_Cs_GotoCase:
			case Nt_Cs_GotoDefault:
			case Nt_Cs_GotoId:
			case Nt_Cs_GtBinOper:
			case Nt_Cs_GtEqBinOper:
			case Nt_Cs_GtEqExpr:
			case Nt_Cs_GtExpr:
			case Nt_Cs_Id:
			case Nt_Cs_IdxrDecl:
			case Nt_Cs_IfStmt:
			case Nt_Cs_ImplConvOper:
			case Nt_Cs_InclOrExpr:
			case Nt_Cs_IncrUnaryOper:
			case Nt_Cs_Internal:
			case Nt_Cs_IntfBody:
			case Nt_Cs_IntfDecl:
			case Nt_Cs_IntfEvDecl:
			case Nt_Cs_IntfGetAcsr:
			case Nt_Cs_IntfIdxrDecl:
			case Nt_Cs_IntfMethodDecl:
			case Nt_Cs_IntfPropDecl:
			case Nt_Cs_IntfSetAcsr:
			case Nt_Cs_IntfType:
			case Nt_Cs_InvExpr:
			case Nt_Cs_InvUnaryOper:
			case Nt_Cs_InvocExpr:
			case Nt_Cs_IsExpr:
			case Nt_Cs_LShAssn:
			case Nt_Cs_LShBinOper:
			case Nt_Cs_LShiftExpr:
			case Nt_Cs_LblStmt:
			case Nt_Cs_LessBinOper:
			case Nt_Cs_LessEqBinOper:
			case Nt_Cs_LessExpr:
			case Nt_Cs_LocalConstDecl:
			case Nt_Cs_LocalVarDclr:
			case Nt_Cs_LocalVarDecl:
			case Nt_Cs_LocalVarInit:
			case Nt_Cs_LockStmt:
			case Nt_Cs_LsEqExpr:
			case Nt_Cs_MemAccess:
			case Nt_Cs_Method:
			case Nt_Cs_MethodDecl:
			case Nt_Cs_MinusAssn:
			case Nt_Cs_MinusBinOper:
			case Nt_Cs_MinusExpr:
			case Nt_Cs_MinusUnaryOper:
			case Nt_Cs_ModAssn:
			case Nt_Cs_ModBinOper:
			case Nt_Cs_ModExpr:
			case Nt_Cs_Module:
			case Nt_Cs_MulBinOper:
			case Nt_Cs_MultAssn:
			case Nt_Cs_MultExpr:
			case Nt_Cs_NamedArg:
			case Nt_Cs_New:
			case Nt_Cs_NotEqBinOper:
			case Nt_Cs_NotEqExpr:
			case Nt_Cs_NotExpr:
			case Nt_Cs_NotUnaryOper:
			case Nt_Cs_NspBody:
			case Nt_Cs_NspDecl:
			case Nt_Cs_ObjCreatExpr:
			case Nt_Cs_OperDecl:
			case Nt_Cs_OrAssn:
			case Nt_Cs_OrBinOper:
			case Nt_Cs_OutArg:
			case Nt_Cs_Override:
			case Nt_Cs_Param:
			case Nt_Cs_ParamArr:
			case Nt_Cs_ParamModifier:
			case Nt_Cs_ParenExpr:
			case Nt_Cs_PlusAssn:
			case Nt_Cs_PlusBinOper:
			case Nt_Cs_PlusExpr:
			case Nt_Cs_PlusUnaryOper:
			case Nt_Cs_PosArg:
			case Nt_Cs_PostDecrExpr:
			case Nt_Cs_PostIncrExpr:
			case Nt_Cs_PreDecrExpr:
			case Nt_Cs_PreIncrExpr:
			case Nt_Cs_PredefType:
			case Nt_Cs_Private:
			case Nt_Cs_PropDecl:
			case Nt_Cs_Property:
			case Nt_Cs_Protected:
			case Nt_Cs_Ptr:
			case Nt_Cs_PtrIndirExpr:
			case Nt_Cs_PtrMemAccess:
			case Nt_Cs_PtrType:
			case Nt_Cs_Public:
			case Nt_Cs_RShAssn:
			case Nt_Cs_RShBinOper:
			case Nt_Cs_RShiftExpr:
			case Nt_Cs_RankSpec:
			case Nt_Cs_Readonly:
			case Nt_Cs_RefArg:
			case Nt_Cs_Remove:
			case Nt_Cs_RemoveAcsrDecl:
			case Nt_Cs_Return:
			case Nt_Cs_ReturnStmt:
			case Nt_Cs_Sealed:
			case Nt_Cs_Set:
			case Nt_Cs_SetAcsrDecl:
			case Nt_Cs_SimpleType:
			case Nt_Cs_SizeofExpr:
			case Nt_Cs_SpecificCatch:
			case Nt_Cs_StackallocInit:
			case Nt_Cs_Static:
			case Nt_Cs_Stmt:
			case Nt_Cs_StmtExpr:
			case Nt_Cs_StrBody:
			case Nt_Cs_StrDecl:
			case Nt_Cs_SubExpr:
			case Nt_Cs_SwitchLbl:
			case Nt_Cs_SwitchSec:
			case Nt_Cs_SwitchStmt:
			case Nt_Cs_ThisAccess:
			case Nt_Cs_ThisInit:
			case Nt_Cs_ThrowStmt:
			case Nt_Cs_True:
			case Nt_Cs_TrueUnaryOper:
			case Nt_Cs_TryStmt:
			case Nt_Cs_Typ:
			case Nt_Cs_Type:
			case Nt_Cs_TypeofExpr:
			case Nt_Cs_UncheckedExpr:
			case Nt_Cs_UncheckedStmt:
			case Nt_Cs_Unsafe:
			case Nt_Cs_UnsafeStmt:
			case Nt_Cs_UsingAliasDir:
			case Nt_Cs_UsingNspDir:
			case Nt_Cs_UsingStmt:
			case Nt_Cs_VarDclr:
			case Nt_Cs_VarInit:
			case Nt_Cs_Virtual:
			case Nt_Cs_Volatile:
			case Nt_Cs_WhileStmt:
			case Nt_Cs_XorAssn:
			case Nt_Cs_XorBinOper:
			case Nt_Cs_ArgLstParam:
			case Nt_Cs_ArgLstExpr:
			case Nt_Cs_MakeRefExpr:
			case Nt_Cs_RefTypeExpr:
			case Nt_Cs_RefValExpr:
			case Nt_Cs_ArrCreInitExpr:
				return true;

			case Nt_Cs_QName:
			case Nt_Cs_Modifiers:
				if (sym->parent->code == sym->code)
					return false;
				else
					return true;

			default:
				return false;
		}
	}
}


int CSharpParser::GetAction(int state, int term, CLexer *lexer)
{
	return CLASS_HEADER t_action(state, term, lexer);
}


int CSharpParser::GetGoto(int state, int nt)
{
	return CLASS_HEADER nt_action(state, nt);
}


int CSharpParser::GetClass(int code)
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


int CSharpParser::GetLHS(int prod)
{
	return CLASS_HEADER lhs[prod];
}


int CSharpParser::GetRHS(int prod)
{
	return CLASS_HEADER rhs[prod];
}


int CSharpParser::GetStartState()
{
	return START_STATE;
}


int CSharpParser::GetOriginalStateCode(int state)
{
	return CLASS_HEADER original_state(state);
}


bool CSharpParser::GetAltAction(CLexer *lexer, int state, int &cur, int &alt)
{
	if (state == 685)
	{
		int index = lexer->GetTokenIndex(lexer->GetCurToken());

		switch (index)
		{
			case IDX_CS_RPAREN:
			case IDX_CS_STAR:
				cur = 10;
				alt = 182;
				return true;
		}
		return false;
	}
	else
		return false;
}


CLexer* CSharpParser::CreateLexer()
{
	return new CSharpLexer();
}

