#include "javaparser5.h"
#include "javalexer5.h"
#include "java5def.h"
#include "java5prs.h"
#include "java5dcl.h"
#include "java5sym.h"
#include "../common/symbol.h"

#define BUFSIZE 128

static char strbuf[BUFSIZE];



char *CJavaParser5::GetTermName(int code)
{
	int len = CLASS_HEADER name_length[CLASS_HEADER terminal_index[code]];

	strncpy(strbuf, &CLASS_HEADER string_buffer[CLASS_HEADER name_start[CLASS_HEADER terminal_index[code]]], len);
	strbuf[len] = '\0';

	return strbuf;
}


char *CJavaParser5::GetNtName(int code)
{
	int len = CLASS_HEADER name_length[CLASS_HEADER non_terminal_index[code]];

	strncpy(strbuf, &CLASS_HEADER string_buffer[CLASS_HEADER name_start[CLASS_HEADER non_terminal_index[code]]], len);
	strbuf[len] = '\0';

	return strbuf;
}


bool CJavaParser5::FilterCallback(CSymbol *sym)
{
	if (sym->terminal)
	{
		switch (sym->code)
		{
			case TK_J5_ABSTRACT:
			case TK_J5_BOOLEAN:
			case TK_J5_BYTE:
			case TK_J5_CHAR:
			case TK_J5_CHAR_LITERAL:
			case TK_J5_DOUBLE:
			case TK_J5_FALSE:
			case TK_J5_FINAL:
			case TK_J5_FLOAT:
			case TK_J5_FLOAT_LITERAL:
			case TK_J5_ID:
			case TK_J5_INT:
			case TK_J5_INT_LITERAL:
			case TK_J5_LONG:
			case TK_J5_NATIVE:
			case TK_J5_NULL:
			case TK_J5_PRIVATE:
			case TK_J5_PROTECTED:
			case TK_J5_PUBLIC:
			case TK_J5_SHORT:
			case TK_J5_STATIC:
			case TK_J5_STRICTFP:
			case TK_J5_STR_LITERAL:
			case TK_J5_SYNCHRONIZED:
			case TK_J5_THIS:
			case TK_J5_TRANSIENT:
			case TK_J5_TRUE:
			case TK_J5_VOID:
			case TK_J5_VOLATILE:
				return true;

			default:
				return false;
		}
	}
	else
	{ 
		switch (sym->code)
		{
			case Nt_J5_AbstrMethodDecl:
			case Nt_J5_ActualTypeArg:
			case Nt_J5_AndAssnExpr:
			case Nt_J5_AndExpr:
			case Nt_J5_AnnotTypeBody:
			case Nt_J5_AnnotTypeDecl:
			case Nt_J5_AnnotTypeMethodDecl:
			case Nt_J5_ArrAccess:
			case Nt_J5_ArrCreatExpr:
			case Nt_J5_ArrInit:
			case Nt_J5_AssertStmt:
			case Nt_J5_AssnExpr:
			case Nt_J5_Blk:
			case Nt_J5_BlkStmt:
			case Nt_J5_BreakStmt:
			case Nt_J5_CastExpr:
			case Nt_J5_Catch:
			case Nt_J5_Class:
			case Nt_J5_ClsBody:
			case Nt_J5_ClsDecl:
			case Nt_J5_ClsIntfType:
			case Nt_J5_ClsType:
			case Nt_J5_CompUnit:
			case Nt_J5_CondAndExpr:
			case Nt_J5_CondExpr:
			case Nt_J5_CondOrExpr:
			case Nt_J5_ConstDecl:
			case Nt_J5_ConstExpr:
			case Nt_J5_ConstrBody:
			case Nt_J5_ConstrDecl:
			case Nt_J5_ContinueStmt:
			case Nt_J5_Dim:
			case Nt_J5_DivAssnExpr:
			case Nt_J5_DivExpr:
			case Nt_J5_DoStmt:
			case Nt_J5_ElemVal:
			case Nt_J5_ElemValArrInit:
			case Nt_J5_ElemValPair:
			case Nt_J5_EmptyStmt:
			case Nt_J5_EnhancedForStmt:
			case Nt_J5_EnumBody:
			case Nt_J5_EnumConst:
			case Nt_J5_EnumDecl:
			case Nt_J5_EqExpr:
			case Nt_J5_ExclOrExpr:
			case Nt_J5_ExplicitConstrInvoc:
			case Nt_J5_Expr:
			case Nt_J5_ExtendsWildcardBnd:
			case Nt_J5_Finally:
			case Nt_J5_FldAccess:
			case Nt_J5_FldDecl:
			case Nt_J5_ForInit:
			case Nt_J5_ForStmt:
			case Nt_J5_ForUpdate:
			case Nt_J5_FormalParam:
			case Nt_J5_GeExpr:
			case Nt_J5_GtExpr:
			case Nt_J5_IfThenStmt:
			case Nt_J5_InclOrExpr:
			case Nt_J5_InstCreatExpr:
			case Nt_J5_InstInit:
			case Nt_J5_InstOfExpr:
			case Nt_J5_IntfBody:
			case Nt_J5_IntfDecl:
			case Nt_J5_IntfType:
			case Nt_J5_InvExpr:
			case Nt_J5_LShiftAssnExpr:
			case Nt_J5_LShiftExpr:
			case Nt_J5_LblStmt:
			case Nt_J5_LeExpr:
			case Nt_J5_LessExpr:
			case Nt_J5_LocalVarDecl:
			case Nt_J5_MarkerAnnot:
			case Nt_J5_MethodDecl:
			case Nt_J5_MethodInvoc:
			case Nt_J5_MinusAssnExpr:
			case Nt_J5_MinusExpr:
			case Nt_J5_ModAssnExpr:
			case Nt_J5_ModExpr:
			case Nt_J5_MultAssnExpr:
			case Nt_J5_MultExpr:
			case Nt_J5_NeExpr:
			case Nt_J5_NormalAnnot:
			case Nt_J5_NotExpr:
			case Nt_J5_OrAssnExpr:
			case Nt_J5_PackageDecl:
			case Nt_J5_ParenExpr:
			case Nt_J5_PlusAssnExpr:
			case Nt_J5_PlusExpr:
			case Nt_J5_PostDecrExpr:
			case Nt_J5_PostIncrExpr:
			case Nt_J5_PreDecrExpr:
			case Nt_J5_PreIncrExpr:
			case Nt_J5_PrimitiveType:
			case Nt_J5_RShiftAssnExpr:
			case Nt_J5_RShiftExpr:
			case Nt_J5_RefType:
			case Nt_J5_ResultType:
			case Nt_J5_ReturnStmt:
			case Nt_J5_SingleElemAnnot:
			case Nt_J5_SingleStaticImpDecl:
			case Nt_J5_SingleTypeImpDecl:
			case Nt_J5_StaticImpOnDemandDecl:
			case Nt_J5_StaticInit:
			case Nt_J5_Stmt:
			case Nt_J5_StmtExpr:
			case Nt_J5_Super:
			case Nt_J5_SuperWildcardBnd:
			case Nt_J5_SwitchBlkStmtGr:
			case Nt_J5_SwitchLbl:
			case Nt_J5_SwitchStmt:
			case Nt_J5_SyncStmt:
			case Nt_J5_ThrowStmt:
			case Nt_J5_Throws:
			case Nt_J5_TryStmt:
			case Nt_J5_Type:
			case Nt_J5_TypeImpOnDemandDecl:
			case Nt_J5_TypeParam:
			case Nt_J5_UShiftAssnExpr:
			case Nt_J5_UShiftExpr:
			case Nt_J5_UnaryMinusExpr:
			case Nt_J5_UnaryPlusExpr:
			case Nt_J5_VarArityParam:
			case Nt_J5_VarDclr:
			case Nt_J5_VarInit:
			case Nt_J5_WhileStmt:
			case Nt_J5_Wildcard:
			case Nt_J5_XorAssnExpr:
				return true;

			case Nt_J5_BndList:
			case Nt_J5_Modifiers:
			case Nt_J5_QName:
				if (sym->parent->code == sym->code)
					return false;
				else
					return true;

			default:
				return false;
		}
	}
}


int CJavaParser5::GetAction(int state, int term, CLexer *lexer)
{
	return CLASS_HEADER t_action(state, term, lexer);
}


int CJavaParser5::GetGoto(int state, int nt)
{
	return CLASS_HEADER nt_action(state, nt);
}


int CJavaParser5::GetClass(int code)
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


int CJavaParser5::GetLHS(int prod)
{
	return CLASS_HEADER lhs[prod];
}


int CJavaParser5::GetRHS(int prod)
{
	return CLASS_HEADER rhs[prod];
}


int CJavaParser5::GetStartState()
{
	return START_STATE;
}


int CJavaParser5::GetOriginalStateCode(int state)
{
	return CLASS_HEADER original_state(state);
}


bool CJavaParser5::GetAltAction(CLexer *lexer, int state, int &cur, int &alt)
{
	if (lexer->GetCurToken() == lexer->GetTokenCode(IDX_J_LANGLE))
	{
		switch (state)
		{
			case 111:
				alt = 25;
				return true;

			case 186:
			case 369:
			case 464:
				alt = 469;
				return true;

			default:
				return false;
		}
	}
	else
		return false;
}


CLexer *CJavaParser5::CreateLexer()
{
	return new CJavaLexer5();
}
