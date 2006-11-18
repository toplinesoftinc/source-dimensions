#include "javaparser.h"
#include "javalexer.h"
#include "javadef.h"
#include "javaprs.h"
#include "javadcl.h"
#include "javasym.h"
#include "../common/symbol.h"

#define BUFSIZE 128

static char strbuf[BUFSIZE];


char *CJavaParser::GetTermName(int code)
{
	int len = CLASS_HEADER name_length[CLASS_HEADER terminal_index[code]];

	strncpy(strbuf, &CLASS_HEADER string_buffer[CLASS_HEADER name_start[CLASS_HEADER terminal_index[code]]], len);
	strbuf[len] = '\0';

	return strbuf;
}


char *CJavaParser::GetNtName(int code)
{
	int len = CLASS_HEADER name_length[CLASS_HEADER non_terminal_index[code]];

	strncpy(strbuf, &CLASS_HEADER string_buffer[CLASS_HEADER name_start[CLASS_HEADER non_terminal_index[code]]], len);
	strbuf[len] = '\0';

	return strbuf;
}


bool CJavaParser::FilterCallback(CSymbol *sym)
{
	if (sym->terminal)
	{
		switch (sym->code)
		{
			case TK_J_ABSTRACT:
			case TK_J_BOOLEAN:
			case TK_J_BYTE:
			case TK_J_CHAR:
			case TK_J_CHAR_LITERAL:
			case TK_J_DOUBLE:
			case TK_J_FALSE:
			case TK_J_FINAL:
			case TK_J_FLOAT:
			case TK_J_FLOAT_LITERAL:
			case TK_J_ID:
			case TK_J_INT:
			case TK_J_INT_LITERAL:
			case TK_J_LONG:
			case TK_J_NATIVE:
			case TK_J_NULL:
			case TK_J_PRIVATE:
			case TK_J_PROTECTED:
			case TK_J_PUBLIC:
			case TK_J_SHORT:
			case TK_J_STATIC:
			case TK_J_STRICTFP:
			case TK_J_STR_LITERAL:
			case TK_J_SYNCHRONIZED:
			case TK_J_THIS:
			case TK_J_TRANSIENT:
			case TK_J_TRUE:
			case TK_J_VOID:
			case TK_J_VOLATILE:
				return true;

			default:
				return false;
		}
	}
	else
	{ 
		switch (sym->code)
		{
			case Nt_J_AbstrMethodDecl:
			case Nt_J_AndAssnExpr:
			case Nt_J_AndExpr:
			case Nt_J_ArrAccess:
			case Nt_J_ArrCreatExpr:
			case Nt_J_ArrInit:
			case Nt_J_AssertStmt:
			case Nt_J_AssnExpr:
			case Nt_J_Blk:
			case Nt_J_BlkStmt:
			case Nt_J_BreakStmt:
			case Nt_J_CastExpr:
			case Nt_J_Catch:
			case Nt_J_Class:
			case Nt_J_ClsBody:
			case Nt_J_ClsDecl:
			case Nt_J_ClsIntfType:
			case Nt_J_ClsType:
			case Nt_J_CompUnit:
			case Nt_J_CondAndExpr:
			case Nt_J_CondExpr:
			case Nt_J_CondOrExpr:
			case Nt_J_ConstDecl:
			case Nt_J_ConstExpr:
			case Nt_J_ConstrBody:
			case Nt_J_ConstrDecl:
			case Nt_J_ContinueStmt:
			case Nt_J_Dim:
			case Nt_J_DivAssnExpr:
			case Nt_J_DivExpr:
			case Nt_J_DoStmt:
			case Nt_J_EmptyStmt:
			case Nt_J_EqExpr:
			case Nt_J_ExclOrExpr:
			case Nt_J_ExplicitConstrInvoc:
			case Nt_J_Expr:
			case Nt_J_Finally:
			case Nt_J_FldAccess:
			case Nt_J_FldDecl:
			case Nt_J_ForInit:
			case Nt_J_ForStmt:
			case Nt_J_ForUpdate:
			case Nt_J_FormalParam:
			case Nt_J_GeExpr:
			case Nt_J_GtExpr:
			case Nt_J_IfThenStmt:
			case Nt_J_InclOrExpr:
			case Nt_J_InstCreatExpr:
			case Nt_J_InstInit:
			case Nt_J_InstOfExpr:
			case Nt_J_IntfBody:
			case Nt_J_IntfDecl:
			case Nt_J_IntfType:
			case Nt_J_InvExpr:
			case Nt_J_LShiftAssnExpr:
			case Nt_J_LShiftExpr:
			case Nt_J_LblStmt:
			case Nt_J_LeExpr:
			case Nt_J_LessExpr:
			case Nt_J_LocalVarDecl:
			case Nt_J_MethodDecl:
			case Nt_J_MethodInvoc:
			case Nt_J_MinusAssnExpr:
			case Nt_J_MinusExpr:
			case Nt_J_ModAssnExpr:
			case Nt_J_ModExpr:
			case Nt_J_MultAssnExpr:
			case Nt_J_MultExpr:
			case Nt_J_NeExpr:
			case Nt_J_NotExpr:
			case Nt_J_OrAssnExpr:
			case Nt_J_PackageDecl:
			case Nt_J_ParenExpr:
			case Nt_J_PlusAssnExpr:
			case Nt_J_PlusExpr:
			case Nt_J_PostDecrExpr:
			case Nt_J_PostIncrExpr:
			case Nt_J_PreDecrExpr:
			case Nt_J_PreIncrExpr:
			case Nt_J_PrimitiveType:
			case Nt_J_RShiftAssnExpr:
			case Nt_J_RShiftExpr:
			case Nt_J_RefType:
			case Nt_J_ResultType:
			case Nt_J_ReturnStmt:
			case Nt_J_SingleTypeImpDecl:
			case Nt_J_StaticInit:
			case Nt_J_Stmt:
			case Nt_J_StmtExpr:
			case Nt_J_Super:
			case Nt_J_SwitchBlkStmtGr:
			case Nt_J_SwitchLbl:
			case Nt_J_SwitchStmt:
			case Nt_J_SyncStmt:
			case Nt_J_ThrowStmt:
			case Nt_J_Throws:
			case Nt_J_TryStmt:
			case Nt_J_Type:
			case Nt_J_TypeImpOnDemandDecl:
			case Nt_J_UShiftAssnExpr:
			case Nt_J_UShiftExpr:
			case Nt_J_UnaryMinusExpr:
			case Nt_J_UnaryPlusExpr:
			case Nt_J_VarDclr:
			case Nt_J_VarInit:
			case Nt_J_WhileStmt:
			case Nt_J_XorAssnExpr:
				return true;

			case Nt_J_Modifiers:
			case Nt_J_QName:
				if (sym->parent->code == sym->code)
					return false;
				else
					return true;

			default:
				return false;
		}
	}
}


int CJavaParser::GetAction(int state, int term, CLexer *lexer)
{
	return CLASS_HEADER t_action(state, term, lexer);
}


int CJavaParser::GetGoto(int state, int nt)
{
	return CLASS_HEADER nt_action(state, nt);
}


int CJavaParser::GetClass(int code)
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


int CJavaParser::GetLHS(int prod)
{
	return CLASS_HEADER lhs[prod];
}


int CJavaParser::GetRHS(int prod)
{
	return CLASS_HEADER rhs[prod];
}


int CJavaParser::GetStartState()
{
	return START_STATE;
}


int CJavaParser::GetOriginalStateCode(int state)
{
	return CLASS_HEADER original_state(state);
}


CLexer* CJavaParser::CreateLexer()
{
	return new CJavaLexer();
}
