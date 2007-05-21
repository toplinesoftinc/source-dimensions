package com.sourcedimensions.server.sys.astimport;


import com.sourcedimensions.server.ast.*;
import com.sourcedimensions.server.ast.BinaryExpression.BinaryExprKind;
import com.sourcedimensions.server.ast.CheckedStatement.CheckedStmtKind;
import com.sourcedimensions.server.ast.ConstraintBound.ConstraintBoundKind;
import com.sourcedimensions.server.ast.DataMember.DataMemberKind;
import com.sourcedimensions.server.ast.DoWhileStatement.DoWhileStmtKind;
import com.sourcedimensions.server.ast.FunctionalMember.FuncMemberKind;
import com.sourcedimensions.server.ast.JumpStatement.JumpStmtKind;
import com.sourcedimensions.server.ast.LiteralExpression.LiteralExprKind;
import com.sourcedimensions.server.ast.Modifier.ModifierKind;
import com.sourcedimensions.server.ast.Parameter.ParamKind;
import com.sourcedimensions.server.ast.ResourceAcquisition.ResAcquisitionKind;
import com.sourcedimensions.server.ast.SelfReferenceExpression.SelfRefExprKind;
import com.sourcedimensions.server.ast.SimpleType.SimpleTypeKind;
import com.sourcedimensions.server.ast.SwitchLabel.SwitchLabelKind;
import com.sourcedimensions.server.ast.TypeArgument.TypeArgKind;
import com.sourcedimensions.server.ast.TypeDeclaration.TypeDeclKind;
import com.sourcedimensions.server.ast.TypeExpression.TypeExprKind;
import com.sourcedimensions.server.ast.UnaryExpression.UnaryExprKind;
import com.sourcedimensions.server.ast.UnaryTypeExpression.UnaryTypeExprKind;
import com.sourcedimensions.server.ast.YieldStatement.YieldStmtKind;


import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;


public class CsImport extends AstImport
{
	enum TypeTag
	{
		QNAME,
		SIMPLE_TYPE,
		ARR_TYPE,
		PTR_TYPE,
		NULL_TYPE,
		OBJECT,
		STRING,
		VOID
	}
	
	enum StmtTag
	{
		LBL_STMT,
		DECL_STMT,
		EMBED_STMT
	}
	
	enum EmbedStmtTag
	{
		BLOCK,
		EMPTY,
		EXPR,
		IF,
		SWITCH,
		WHILE,
		DO,
		FOR,
		FOREACH,
		BREAK,
		CONTINUE,
		GOTOID,
		GOTOCASE,
		GOTODEFAULT,
		RETURN,
		THROW,
		TRY,
		CHECKED,
		UNCHECKED,
		LOCK,
		USING,
		UNSAFE,
		FIXED,
		YIELD
	}
	
	enum ExprTag
	{
		ARR_CREAT,
		ARR_CREINIT,
		CHAR_LITERAL,
		STR_LITERAL,
		INT_LITERAL,
		REAL_LITERAL,
		TRUE,
		FALSE,
		NULL,    
		PAREN,        
		MEM_ACCESS,        
		INVOC,        
		ELEM_ACCESS,
		THIS_ACCESS,
		BASE_ACCESS,  
		POST_INCR,     
		POST_DECR,     
		OBJ_CREAT,     
		TYPEOF,       
		CHECKED,      
		UNCHECKED,    
		PTR_MEM_ACCESS,     
		SIZEOF,
		DEFAULT_VAL,
		ANONYM_METHOD, 
		MAKE_REF,      
		REF_TYPE,      
		REF_VALUE,
		QNAME,
		UNARY_PLUS,
		UNARY_MINUS,
		PLUS,      
		MINUS,       
		NOT,
		INV,
		PRE_INCR,   
		PRE_DECR,   
		CAST,      
		PTR_INDIR,
		ADDROF,		
		MULT, 
		DIV,
		MOD,
		LSHIFT,
		RSHIFT, 		
		LESS,
		GT,
		LESS_EQ,
		GT_EQ, 
		IS,   
		AS,
		EQ,
		NOT_EQ,
		AND,
		EXCL_OR,
		INCL_OR,
		COND_AND,
		COND_OR,     
		NULL_COALES,
		COND,   
		ASSN,     
		PLUS_ASSN,
		MINUS_ASSN,
		MULT_ASSN,
		DIV_ASSN,
		MOD_ASSN, 
		AND_ASSN, 
		OR_ASSN, 
		XOR_ASSN,  
		LSHIFT_ASSN,  
		RSHIFT_ASSN
	}
	
	enum ArgTag
	{
		VALUE,
		OUT,
		REF
	}
	
	enum InitTag
	{
		EXPR,
		ARRINIT,
		STACKALLOCINIT
	}
	
	enum TypeofTag
	{
		TYPE,
		VOID,
		UNBOUND_TYPE
	}
	
	protected static Hashtable<String, ModifierKind> m_modifiers;
	protected static Hashtable<String, ConstraintBoundKind> m_boundTypes;
	protected static Hashtable<String, SimpleTypeKind> m_simpleTypes;
	protected static Hashtable<String, TypeTag> m_typeTags;
	protected static Hashtable<String, FuncMemberKind> m_operTags;
	protected static Hashtable<String, StmtTag> m_stmtTags;
	protected static Hashtable<String, EmbedStmtTag> m_embedStmtTags;
	protected static Hashtable<String, ExprTag> m_exprTags;
	protected static Hashtable<String, ArgTag> m_argTags;
	protected static Hashtable<String, InitTag> m_initTags;
	protected static Hashtable<String, TypeofTag> m_typeofTags;
	protected static Hashtable<ExprTag, UnaryExprKind> m_unaryExpr;
	protected static Hashtable<ExprTag, BinaryExprKind> m_binaryExpr;
	protected static Hashtable<ExprTag, LiteralExprKind> m_literExpr;
	
	static 
	{
		m_modifiers = new Hashtable<String,ModifierKind>();
		m_modifiers.put("Abstract", ModifierKind.ABSTRACT);
		m_modifiers.put("Extern", ModifierKind.EXTERN);
		m_modifiers.put("Internal", ModifierKind.INTERNAL);
		m_modifiers.put("New", ModifierKind.NEW);
		m_modifiers.put("Override", ModifierKind.OVERRIDE);
		m_modifiers.put("Private", ModifierKind.PRIVATE);
		m_modifiers.put("Protected", ModifierKind.PROTECTED);
		m_modifiers.put("Public", ModifierKind.PUBLIC);
		m_modifiers.put("Readonly", ModifierKind.READONLY);
		m_modifiers.put("Sealed", ModifierKind.FINAL);
		m_modifiers.put("Static", ModifierKind.STATIC);
		m_modifiers.put("Virtual", ModifierKind.VIRTUAL);
		m_modifiers.put("Volatile", ModifierKind.VOLATILE);
		m_modifiers.put("Unsafe", ModifierKind.UNSAFE);	
		m_modifiers.put("Partial", ModifierKind.PARTIAL);
		
		m_boundTypes = new Hashtable<String,ConstraintBoundKind>();
		m_boundTypes.put("Class", ConstraintBoundKind.CLASS);
		m_boundTypes.put("Struct", ConstraintBoundKind.STRUCT);
		m_boundTypes.put("QName", ConstraintBoundKind.TYPE);
		m_boundTypes.put("ConstrCnrt", ConstraintBoundKind.CONSTRUCTOR);
		
		m_simpleTypes = new Hashtable<String,SimpleTypeKind>();
		m_simpleTypes.put("VOID", SimpleTypeKind.VOID);
		m_simpleTypes.put("BOOL", SimpleTypeKind.BOOL);
		m_simpleTypes.put("SBYTE", SimpleTypeKind.BYTE);
		m_simpleTypes.put("BYTE", SimpleTypeKind.UBYTE);
		m_simpleTypes.put("SHORT", SimpleTypeKind.SHORT);
		m_simpleTypes.put("USHORT", SimpleTypeKind.USHORT);		
		m_simpleTypes.put("INT", SimpleTypeKind.INT);
		m_simpleTypes.put("UINT", SimpleTypeKind.UINT);		
		m_simpleTypes.put("LONG", SimpleTypeKind.LONG);
		m_simpleTypes.put("ULONG", SimpleTypeKind.ULONG);		
		m_simpleTypes.put("CHAR", SimpleTypeKind.CHAR);
		m_simpleTypes.put("FLOAT", SimpleTypeKind.FLOAT);
		m_simpleTypes.put("DOUBLE", SimpleTypeKind.DOUBLE);
		m_simpleTypes.put("DECIMAL", SimpleTypeKind.DECIMAL);
		m_simpleTypes.put("OBJECT", SimpleTypeKind.OBJECT);
		m_simpleTypes.put("STRING", SimpleTypeKind.STRING);		
		
		m_typeTags = new Hashtable<String,TypeTag>();
		m_typeTags.put("QName", TypeTag.QNAME);
		m_typeTags.put("SimpleType", TypeTag.SIMPLE_TYPE);
		m_typeTags.put("ArrType", TypeTag.ARR_TYPE);
		m_typeTags.put("PtrType", TypeTag.PTR_TYPE);
		m_typeTags.put("NullType", TypeTag.NULL_TYPE);
		m_typeTags.put("OBJECT", TypeTag.OBJECT);
		m_typeTags.put("STRING", TypeTag.STRING);
		m_typeTags.put("VOID", TypeTag.VOID);
		
		m_operTags = new Hashtable<String,FuncMemberKind>();
		m_operTags.put("PlusUnaryOper", FuncMemberKind.UPLUS_OPERATOR); 
		m_operTags.put("MinusUnaryOper", FuncMemberKind.UMINUS_OPERATOR);
		m_operTags.put("NotUnaryOper", FuncMemberKind.NOT_OPERATOR);
		m_operTags.put("InvUnaryOper", FuncMemberKind.INV_OPERATOR);
		m_operTags.put("IncrUnaryOper", FuncMemberKind.INC_OPERATOR);
		m_operTags.put("DecrUnaryOper", FuncMemberKind.DEC_OPERATOR);
		m_operTags.put("TrueUnaryOper", FuncMemberKind.TRUE_OPERATOR);
		m_operTags.put("FalseUnaryOper", FuncMemberKind.FALSE_OPERATOR);
		m_operTags.put("PlusBinOper", FuncMemberKind.PLUS_OPERATOR);
		m_operTags.put("MinusBinOper", FuncMemberKind.MINUS_OPERATOR);
		m_operTags.put("MulBinOper", FuncMemberKind.MULT_OPERATOR);
		m_operTags.put("DivBinOper", FuncMemberKind.DIV_OPERATOR);
		m_operTags.put("ModBinOper", FuncMemberKind.REM_OPERATOR);
		m_operTags.put("AndBinOper", FuncMemberKind.AND_OPERATOR);
		m_operTags.put("OrBinOper", FuncMemberKind.OR_OPERATOR);
		m_operTags.put("XorBinOper", FuncMemberKind.XOR_OPERATOR);
		m_operTags.put("LShBinOper", FuncMemberKind.LSHIFT_OPERATOR);
		m_operTags.put("RShBinOper", FuncMemberKind.RSHIFT_OPERATOR);
		m_operTags.put("EqBinOper", FuncMemberKind.EQUAL_OPERATOR);
		m_operTags.put("NotEqBinOper", FuncMemberKind.NOT_EQ_OPERATOR);
		m_operTags.put("GtBinOper", FuncMemberKind.GT_OPERATOR);
		m_operTags.put("LessBinOper", FuncMemberKind.LESS_OPERATOR);
		m_operTags.put("GtEqBinOper", FuncMemberKind.GT_EQ_OPERATOR);
		m_operTags.put("LessEqBinOper", FuncMemberKind.LESS_EQ_OPERATOR);
		m_operTags.put("ImplConvOper", FuncMemberKind.IMP_CONV_OPERATOR);
		m_operTags.put("ExplConvOper", FuncMemberKind.EXP_CONV_OPERATOR);
		
		m_stmtTags = new Hashtable<String,StmtTag>();
		m_stmtTags.put("LblStmt", StmtTag.LBL_STMT);
		m_stmtTags.put("DeclStmt", StmtTag.DECL_STMT);
		m_stmtTags.put("EmbedStmt", StmtTag.EMBED_STMT);
		
		m_embedStmtTags = new Hashtable<String,EmbedStmtTag>();
		m_embedStmtTags.put("Blk", EmbedStmtTag.BLOCK);
		m_embedStmtTags.put("EmptyStmt", EmbedStmtTag.EMPTY);
		m_embedStmtTags.put("StmtExpr", EmbedStmtTag.EXPR);
		m_embedStmtTags.put("IfStmt", EmbedStmtTag.IF);
		m_embedStmtTags.put("SwitchStmt", EmbedStmtTag.SWITCH);		
		m_embedStmtTags.put("WhileStmt", EmbedStmtTag.WHILE);
		m_embedStmtTags.put("DoStmt", EmbedStmtTag.DO);
		m_embedStmtTags.put("ForStmt", EmbedStmtTag.FOR);
		m_embedStmtTags.put("ForeachStmt", EmbedStmtTag.FOREACH);		
		m_embedStmtTags.put("BreakStmt", EmbedStmtTag.BREAK);
		m_embedStmtTags.put("ContinueStmt", EmbedStmtTag.CONTINUE);
		m_embedStmtTags.put("GotoId", EmbedStmtTag.GOTOID);
		m_embedStmtTags.put("GotoCase", EmbedStmtTag.GOTOCASE);
		m_embedStmtTags.put("GotoDefault", EmbedStmtTag.GOTODEFAULT);
		m_embedStmtTags.put("ReturnStmt", EmbedStmtTag.RETURN);
		m_embedStmtTags.put("ThrowStmt", EmbedStmtTag.THROW);		
		m_embedStmtTags.put("TryStmt", EmbedStmtTag.TRY);
		m_embedStmtTags.put("CheckedStmt", EmbedStmtTag.CHECKED);		
		m_embedStmtTags.put("UncheckedStmt", EmbedStmtTag.UNCHECKED);
		m_embedStmtTags.put("LockStmt", EmbedStmtTag.LOCK);		
		m_embedStmtTags.put("UsingStmt", EmbedStmtTag.USING);
		m_embedStmtTags.put("UnsafeStmt", EmbedStmtTag.UNSAFE);		
		m_embedStmtTags.put("FixedStmt", EmbedStmtTag.FIXED);
		m_embedStmtTags.put("YieldStmt", EmbedStmtTag.YIELD);
		
		m_exprTags = new Hashtable<String,ExprTag>();
		m_exprTags.put("ArrCreatExpr", ExprTag.ARR_CREAT);
		m_exprTags.put("ArrCreInitExpr", ExprTag.ARR_CREINIT);
		m_exprTags.put("CHAR_LITERAL", ExprTag.CHAR_LITERAL);
		m_exprTags.put("STR_LITERAL", ExprTag.STR_LITERAL);
		m_exprTags.put("INT_LITERAL", ExprTag.INT_LITERAL);
		m_exprTags.put("REAL_LITERAL", ExprTag.REAL_LITERAL);
		m_exprTags.put("True", ExprTag.TRUE);
		m_exprTags.put("False", ExprTag.FALSE);
		m_exprTags.put("NULL", ExprTag.NULL);  
		m_exprTags.put("ParenExpr", ExprTag.PAREN);
		m_exprTags.put("MemAccess", ExprTag.MEM_ACCESS);        
		m_exprTags.put("InvocExpr", ExprTag.INVOC);        
		m_exprTags.put("ElemAccess", ExprTag.ELEM_ACCESS);       
		m_exprTags.put("ThisAccess", ExprTag.THIS_ACCESS);
		m_exprTags.put("BaseAccess", ExprTag.BASE_ACCESS);
		m_exprTags.put("PostIncrExpr", ExprTag.POST_INCR);     
		m_exprTags.put("PostDecrExpr", ExprTag.POST_DECR);
		m_exprTags.put("ObjCreatExpr", ExprTag.OBJ_CREAT);
		m_exprTags.put("TypeofExpr", ExprTag.TYPEOF);
		m_exprTags.put("CheckedExpr", ExprTag.CHECKED);
		m_exprTags.put("UncheckedExpr", ExprTag.UNCHECKED);
		m_exprTags.put("PtrMemAccess", ExprTag.PTR_MEM_ACCESS);
		m_exprTags.put("SizeofExpr", ExprTag.SIZEOF);
		m_exprTags.put("DefaultValExpr", ExprTag.DEFAULT_VAL);
		m_exprTags.put("AnonymMethodExpr", ExprTag.ANONYM_METHOD); 
		m_exprTags.put("MakeRefExpr", ExprTag.MAKE_REF);
		m_exprTags.put("RefTypeExpr", ExprTag.REF_TYPE);
		m_exprTags.put("RefValExpr", ExprTag.REF_VALUE);
		m_exprTags.put("QName", ExprTag.QNAME);
		m_exprTags.put("PlusExpr", ExprTag.UNARY_PLUS);
		m_exprTags.put("MinusExpr", ExprTag.UNARY_MINUS);
		m_exprTags.put("NotExpr", ExprTag.NOT);
		m_exprTags.put("InvExpr", ExprTag.INV);
		m_exprTags.put("PreIncrExpr", ExprTag.PRE_INCR);
		m_exprTags.put("PreDecrExpr", ExprTag.PRE_DECR);
		m_exprTags.put("CastExpr", ExprTag.CAST);
		m_exprTags.put("PtrIndirExpr", ExprTag.PTR_INDIR);
		m_exprTags.put("AddrofExpr", ExprTag.ADDROF);
		m_exprTags.put("MultExpr", ExprTag.MULT);
		m_exprTags.put("DivExpr", ExprTag.DIV);
		m_exprTags.put("ModExpr", ExprTag.MOD);
		m_exprTags.put("AddExpr", ExprTag.PLUS);
		m_exprTags.put("SubExpr", ExprTag.MINUS);
		m_exprTags.put("LShiftExpr", ExprTag.LSHIFT);
		m_exprTags.put("RShiftExpr", ExprTag.RSHIFT);
		m_exprTags.put("LessExpr", ExprTag.LESS);
		m_exprTags.put("GtExpr", ExprTag.GT);
		m_exprTags.put("LsEqExpr", ExprTag.LESS_EQ);
		m_exprTags.put("GtEqExpr", ExprTag.GT_EQ);
		m_exprTags.put("IsExpr", ExprTag.IS);
		m_exprTags.put("AsExpr", ExprTag.AS);
		m_exprTags.put("EqExpr", ExprTag.EQ);
		m_exprTags.put("NotEqExpr", ExprTag.NOT_EQ);
		m_exprTags.put("AndExpr", ExprTag.AND);
		m_exprTags.put("ExclOrExpr", ExprTag.EXCL_OR);
		m_exprTags.put("InclOrExpr", ExprTag.INCL_OR);
		m_exprTags.put("CondAndExpr", ExprTag.COND_AND);
		m_exprTags.put("CondOrExpr", ExprTag.COND_OR);
		m_exprTags.put("NullCoalesExpr", ExprTag.NULL_COALES);
		m_exprTags.put("CondExpr", ExprTag.COND);
		m_exprTags.put("Assn", ExprTag.ASSN);
		m_exprTags.put("PlusAssn", ExprTag.PLUS_ASSN);
		m_exprTags.put("MinusAssn", ExprTag.MINUS_ASSN);
		m_exprTags.put("MultAssn", ExprTag.MULT_ASSN);
		m_exprTags.put("DivAssn", ExprTag.DIV_ASSN);
		m_exprTags.put("ModAssn", ExprTag.MOD_ASSN);
		m_exprTags.put("AndAssn", ExprTag.AND_ASSN);
		m_exprTags.put("OrAssn", ExprTag.OR_ASSN);
		m_exprTags.put("XorAssn", ExprTag.XOR_ASSN);
		m_exprTags.put("LShAssn", ExprTag.LSHIFT_ASSN);
		m_exprTags.put("RShAssn", ExprTag.RSHIFT_ASSN);
		
		m_argTags = new Hashtable<String,ArgTag>();
		m_argTags.put("Expr", ArgTag.VALUE);
		m_argTags.put("RefArg", ArgTag.REF);
		m_argTags.put("OutArg", ArgTag.OUT);
		
		m_initTags = new Hashtable<String,InitTag>();
		m_initTags.put("Expr", InitTag.EXPR);
		m_initTags.put("ArrInit", InitTag.ARRINIT);
		m_initTags.put("StackallocInit", InitTag.STACKALLOCINIT);
	
		m_typeofTags = new Hashtable<String,TypeofTag>();
		m_typeofTags.put("Type", TypeofTag.TYPE);
		m_typeofTags.put("VOID", TypeofTag.VOID);
		m_typeofTags.put("UnboundTypeName", TypeofTag.UNBOUND_TYPE);
		
		m_unaryExpr = new Hashtable<ExprTag,UnaryExprKind>();
		m_unaryExpr.put(ExprTag.UNARY_PLUS, UnaryExprKind.PLUS);
		m_unaryExpr.put(ExprTag.UNARY_MINUS, UnaryExprKind.MINUS);
		m_unaryExpr.put(ExprTag.NOT, UnaryExprKind.NOT);		
		m_unaryExpr.put(ExprTag.INV, UnaryExprKind.INVERSION);
		m_unaryExpr.put(ExprTag.PRE_INCR, UnaryExprKind.PRE_INCR);
		m_unaryExpr.put(ExprTag.PRE_DECR, UnaryExprKind.PRE_DECR);
		m_unaryExpr.put(ExprTag.POST_INCR, UnaryExprKind.POST_INCR);
		m_unaryExpr.put(ExprTag.POST_DECR, UnaryExprKind.POST_DECR);
		m_unaryExpr.put(ExprTag.PAREN, UnaryExprKind.PARENTHESIZED);
		m_unaryExpr.put(ExprTag.PTR_INDIR, UnaryExprKind.PTR_INDIRECTION);
		m_unaryExpr.put(ExprTag.ADDROF, UnaryExprKind.ADDRESSOF);
		m_unaryExpr.put(ExprTag.CHECKED, UnaryExprKind.CHECKED);
		m_unaryExpr.put(ExprTag.UNCHECKED, UnaryExprKind.UNCHECKED);
		m_unaryExpr.put(ExprTag.MAKE_REF, UnaryExprKind.MAKE_REF);
		m_unaryExpr.put(ExprTag.REF_TYPE, UnaryExprKind.REF_TYPE);
		
		m_binaryExpr = new Hashtable<ExprTag,BinaryExprKind>();
		m_binaryExpr.put(ExprTag.NULL_COALES, BinaryExprKind.NULL_COALESCE);
		m_binaryExpr.put(ExprTag.MEM_ACCESS, BinaryExprKind.MEMBER_ACCESS);
		m_binaryExpr.put(ExprTag.PTR_MEM_ACCESS, BinaryExprKind.PTR_MEM_ACCESS);	
		m_binaryExpr.put(ExprTag.MOD, BinaryExprKind.REM);
		m_binaryExpr.put(ExprTag.DIV, BinaryExprKind.DIV);
		m_binaryExpr.put(ExprTag.MULT, BinaryExprKind.MULT);
		m_binaryExpr.put(ExprTag.MINUS, BinaryExprKind.MINUS);
		m_binaryExpr.put(ExprTag.PLUS, BinaryExprKind.PLUS);
		m_binaryExpr.put(ExprTag.LSHIFT, BinaryExprKind.LSHIFT);
		m_binaryExpr.put(ExprTag.RSHIFT, BinaryExprKind.RSHIFT);
		m_binaryExpr.put(ExprTag.GT_EQ, BinaryExprKind.GT_EQUAL);
		m_binaryExpr.put(ExprTag.LESS_EQ, BinaryExprKind.LESS_EQUAL);
		m_binaryExpr.put(ExprTag.GT, BinaryExprKind.GREATER);
		m_binaryExpr.put(ExprTag.LESS, BinaryExprKind.LESS);		
		m_binaryExpr.put(ExprTag.NOT_EQ, BinaryExprKind.NOT_EQUAL);
		m_binaryExpr.put(ExprTag.EQ, BinaryExprKind.EQUAL);		
		m_binaryExpr.put(ExprTag.AND, BinaryExprKind.BITWISE_AND);		
		m_binaryExpr.put(ExprTag.EXCL_OR, BinaryExprKind.XOR);
		m_binaryExpr.put(ExprTag.INCL_OR, BinaryExprKind.BITWISE_OR);
		m_binaryExpr.put(ExprTag.COND_AND, BinaryExprKind.AND);
		m_binaryExpr.put(ExprTag.COND_OR, BinaryExprKind.OR);
		m_binaryExpr.put(ExprTag.ASSN, BinaryExprKind.ASSIGNMENT);		
		m_binaryExpr.put(ExprTag.PLUS_ASSN, BinaryExprKind.PLUS_ASSIGNMENT);
		m_binaryExpr.put(ExprTag.MINUS_ASSN, BinaryExprKind.MINUS_ASSIGNMENT);		
		m_binaryExpr.put(ExprTag.MULT_ASSN, BinaryExprKind.MULT_ASSIGNMENT);
		m_binaryExpr.put(ExprTag.DIV_ASSN, BinaryExprKind.DIV_ASSIGNMENT);
		m_binaryExpr.put(ExprTag.AND_ASSN, BinaryExprKind.AND_ASSIGNMENT);
		m_binaryExpr.put(ExprTag.OR_ASSN, BinaryExprKind.OR_ASSIGNMENT);
		m_binaryExpr.put(ExprTag.XOR_ASSN, BinaryExprKind.XOR_ASSIGNMENT);		
		m_binaryExpr.put(ExprTag.MOD_ASSN, BinaryExprKind.REM_ASSIGNMENT);
		m_binaryExpr.put(ExprTag.LSHIFT_ASSN, BinaryExprKind.LSHIFT_ASSIGNMENT);		
		m_binaryExpr.put(ExprTag.RSHIFT_ASSN, BinaryExprKind.RSHIFT_ASSIGNMENT);	
		m_binaryExpr.put(ExprTag.ELEM_ACCESS, BinaryExprKind.ARRAY_ACCESS);
		
		m_literExpr = new Hashtable<ExprTag, LiteralExprKind>();
		m_literExpr.put(ExprTag.INT_LITERAL, LiteralExprKind.INT);
		m_literExpr.put(ExprTag.REAL_LITERAL, LiteralExprKind.FLOAT);
		m_literExpr.put(ExprTag.CHAR_LITERAL, LiteralExprKind.CHAR);
		m_literExpr.put(ExprTag.STR_LITERAL, LiteralExprKind.STRING);
		m_literExpr.put(ExprTag.NULL, LiteralExprKind.NULL);
		m_literExpr.put(ExprTag.TRUE, LiteralExprKind.TRUE);
		m_literExpr.put(ExprTag.FALSE, LiteralExprKind.FALSE);				
	}
	
	
	public void runProcess(XmlNode rootXml, CompilationUnit unit) throws Exception
	{
		parseNamespaceBody(rootXml, unit.m_attributes, unit.m_directives, unit.m_declarations, null);
	}
	
	
	protected <T extends Collection> void parseNamespaceBody(XmlNode node, Set<AttributeBlock> attrs, 
			Set<Directive> dirs, T decls, ITypeDeclWrapperFactory factory) throws Exception 
	{
		parseExternAliasDirs(node, dirs);
		parseUsingAliasDirs(node, dirs);
		parseUsingDirs(node, dirs);
		
		XmlNode atr = node.getNode("GblAttrs");

		if (atr != null)
			parseAttributes(atr, attrs);
		
		parseTypeDecls(node, decls, factory);
		parseEnumDecls(node, decls, factory);
		parseDelegateDecls(node, decls, factory);
		parseNamespaceDecls(node, decls, factory);	
	}
	
		
	protected void parseExternAliasDirs(XmlNode node, Set<Directive> dirs) throws Exception
	{
		List<XmlNode> list = node.getNodeList("ExternAliasDir");
		
		for (XmlNode n : list)
		{
			ExternAliasDirective dir = createAstNode(ExternAliasDirective.class);
			parseTextPos(n, dir);
			dir.m_identifier = parseId(n.getNode("Id"));
			dirs.add(dir);
		}
	}
	
	
	protected void parseUsingAliasDirs(XmlNode node, Set<Directive> dirs) throws Exception
	{
		List<XmlNode> list = node.getNodeList("UsingAliasDir");

		for (XmlNode n : list)
		{
			UsingAliasDirective dir = createAstNode(UsingAliasDirective.class);
			parseTextPos(n, dir);
			dir.m_alias = parseId(n.getNode("Id"));
			parseQName(n.getNode("QName"), dir.m_name);
			dirs.add(dir);
		}
	}
	
	
	protected void parseUsingDirs(XmlNode root, Set<Directive> dirs) throws Exception
	{
		List<XmlNode> list = root.getNodeList("UsingNspDir");

		for (XmlNode node : list)
		{
			UsingDirective dir = createAstNode(UsingDirective.class);
			parseTextPos(node, dir);
			parseQName(node.getNode("QName"), dir.m_name);
			dirs.add(dir);
		}
	}
	
	
	protected void parseAttributes(XmlNode node, Set<AttributeBlock> attributes) throws Exception
	{
		String[] tag = {"AttrSec", "GblAttrSec"};
		
		for (String s : tag)
		{
			List<XmlNode> list = node.getNodeList(s);
			
			for (XmlNode nd : list)
			{
				AttributeBlock block = createAstNode(AttributeBlock.class);
				parseTextPos(nd, block);
				
				XmlNode first = nd.getFirstChild();
				
				if (!first.getName().equals("Attr"))
				{
					if (first.getName().equals("ID"))
						block.m_target = getTermValue(first);
					else
						block.m_target = first.getName();
				}
				
				List<XmlNode> lst = nd.getNodeList("Attr");
				
				for (XmlNode n : lst)
				{
					AttributeItem item = createAstNode(AttributeItem.class);
					parseTextPos(n, item);
	
					XmlNode qn = n.getNode("QName");
					
					UserDefinedType type = createAstNode(UserDefinedType.class);
					parseTextPos(qn, type);
					parseQName(qn, type.m_name);
					
					item.setType(type);
					
					List<XmlNode> l = n.getNodeList("PosArg");
					
					for (XmlNode arg : l)
					{
						AttributeArgument a = createAstNode(AttributeArgument.class);
						parseTextPos(arg, a);
						
						ExpressionElementValue v = createAstNode(ExpressionElementValue.class);
						parseTextPos(arg, v);
						v.setExpression(parseExprNode(arg.getFirstChild()));
						
						a.setValue(v);
						
						item.m_arguments.add(a);
					}
					
					l = n.getNodeList("NamedArg");
	
					for (XmlNode arg : l)
					{
						NamedAttributeArgument a = createAstNode(NamedAttributeArgument.class);
						parseTextPos(arg, a);
						
						ExpressionElementValue v = createAstNode(ExpressionElementValue.class);
						parseTextPos(arg, v);
						v.setExpression(parseExpr(arg));
						
						a.m_name = parseId(arg.getNode("Id"));
						
						a.setValue(v);
						
						item.m_arguments.add(a);
					}
					
					block.m_items.add(item);
				}
				
				attributes.add(block);
			}
		}
	}
	
	
	protected <T extends Collection> void parseTypeDecls(XmlNode node, T decls, ITypeDeclWrapperFactory factory) throws Exception
	{
		final TypeDeclKind[] kind = {TypeDeclKind.CLASS, TypeDeclKind.STRUCT, TypeDeclKind.INTERFACE};
		final String[] decltag = {"ClsDecl", "StrDecl", "IntfDecl"};
		final String[] bodytag = {"ClsBody", "StrBody", "IntfBody"};
		
		for (int k = 0; k < kind.length; k++)
		{
			List<XmlNode> list = node.getNodeList(decltag[k]);
			
			for (XmlNode n : list)
			{
				TypeDeclaration decl = createAstNode(TypeDeclaration.class, kind[k].value());
				parseTextPos(n, decl);
				
				parseAttributes(n, decl.m_attributes);
				parseModifiers(n, decl.m_modifiers);				
				decl.m_name = parseId(n.getNode("Id"));
				parseTypeParams(n, decl.m_typeParams);
				parseBaseTypes(n, decl.m_baseTypes);
				parseConstraints(n, decl.m_constraints);
				parseMembers(n.getNode(bodytag[k]), decl.m_members);
								
				if (factory == null)
					decls.add(decl);
				else
					decls.add(factory.wrapTypeDecl(decl));						
			}
		}
	}

	
	protected <T extends Collection> void parseEnumDecls(XmlNode node, T decls, ITypeDeclWrapperFactory factory) throws Exception
	{
		List<XmlNode> list = node.getNodeList("EnumDecl");

		for (XmlNode n : list)
		{
			TypeDeclaration decl = createAstNode(TypeDeclaration.class, TypeDeclKind.ENUM.value());
			parseTextPos(n, decl);
		
			parseAttributes(n, decl.m_attributes);
			parseModifiers(n, decl.m_modifiers);				
			decl.m_name = parseId(n.getNode("Id"));
			
			XmlNode base = n.getNode("EnumBase");
			if (base != null)
			{
				SimpleType type = createAstNode(SimpleType.class, m_simpleTypes.get(base.getFirstChild().getName()).value());
				parseTextPos(base, type);
				decl.m_baseTypes.add(type);
			}
			
			if (factory == null)
				decls.add(decl);
			else
				decls.add(factory.wrapTypeDecl(decl));
			
			List<XmlNode> members = n.getNode("EnumBody").getNodeList("EnumMemDecl");
			
			for (XmlNode m : members)
			{
				EnumConstMember em = createAstNode(EnumConstMember.class, null);
				parseTextPos(m, em);
				
				parseAttributes(m, em.m_attributes);
				em.m_name = parseId(m.getNode("Id"));
				
				if (m.getNode("Expr") != null)
					em.m_arguments.add(parseExpr(m));
				
				decl.m_members.add(em);
			}
		}
	}

	
	protected <T extends Collection> void parseDelegateDecls(XmlNode node, T decls, ITypeDeclWrapperFactory factory) throws Exception
	{
		List<XmlNode> list = node.getNodeList("DelegateDecl");
		
		for (XmlNode n : list)
		{
			DelegateDeclaration delegate = createAstNode(DelegateDeclaration.class);
			parseTextPos(n, delegate);
			
			parseAttributes(n, delegate.m_attributes);
			parseModifiers(n, delegate.m_modifiers);			
			delegate.m_name = parseId(n.getNode("Id"));
			parseTypeParams(n, delegate.m_typeParams);
			parseConstraints(n, delegate.m_constraints);
			
			if (n.getNode("VOID") != null)
			{
				SimpleType type = createAstNode(SimpleType.class, SimpleTypeKind.VOID.value());
				parseTextPos(n.getNode("VOID"), type);
				delegate.setType(type);				
			}
			else
			{
				delegate.setType(parseType(n.getNode("Type")));
			}
			
			parseFormalParams(n, delegate.m_parameters);
			
			
			if (factory == null)
				decls.add(delegate);
			else
				decls.add(factory.wrapTypeDecl(delegate));					
		}
	}
	
	
	protected <T extends Collection> void parseNamespaceDecls(XmlNode node, T decls, ITypeDeclWrapperFactory factory) throws Exception
	{
		List<XmlNode> list = node.getNodeList("NspDecl");
		
		for (XmlNode nd : list)
		{
			List<XmlNode> lst = nd.getNode("QName").getNodeList("Id");
			String name = "";
			
			for (XmlNode n : lst)
			{
				if (name.length() > 0)
					name += ".";
				
				name += parseId(n);
			}
			
			TypeDeclaration decl = createAstNode(TypeDeclaration.class, TypeDeclKind.NAMESPACE.value());
			parseTextPos(nd, decl);
			
			decl.m_name = name;
						
			parseNamespaceBody(nd.getNode("NspBody"), decl.m_attributes, decl.m_directives, decl.m_members, new MemberTypeDeclWrapperFactory());
			
			if (factory == null)
				decls.add(decl);
			else
				decls.add(factory.wrapTypeDecl(decl));
		}
	}

	
	protected void parseFormalParams(XmlNode node, List<Parameter> params) throws Exception
	{
		String[] tag = {"FixedParam", "AnonymMethodParam"};
		
		for (String p : tag)
		{
			List<XmlNode> list = node.getNodeList(p);
			
			for (XmlNode n : list)
			{
				Parameter param = createAstNode(Parameter.class);
				parseTextPos(n, param);
				
				param.m_name = parseId(n.getNode("Id"));
				parseAttributes(n, param.m_attributes);
				param.setType(parseType(n.getNode("Type")));
				
				XmlNode m = n.getNode("ParamModifier");
				if (m == null)
				{
					param.setKind(ParamKind.VALUE.value());
				}
				else
				{
					if (m.getNode("REF") != null)
						param.setKind(ParamKind.REF.value());
					else
						param.setKind(ParamKind.OUT.value());
				}
				
				params.add(param);
			}
			
			XmlNode vparam = node.getNode("ParamArr");
			if (vparam != null)
			{
				Parameter param = createAstNode(Parameter.class, ParamKind.VALUE.value());
				parseTextPos(vparam, param);
	
				param.m_name = parseId(vparam.getNode("Id"));
				parseAttributes(vparam, param.m_attributes);
				param.setType(parseArrayType(vparam.getNode("ArrType")));
				
				params.add(param);
			}
			
			XmlNode arglist = node.getNode("ArgListParam");
			if (arglist != null)
			{
				Parameter param = createAstNode(Parameter.class, ParamKind.ARGLIST.value());
				parseTextPos(arglist, param);
				params.add(param);
			}
		}
	}
	
	
	protected Type parseType(XmlNode node) throws Exception
	{
		return parseTypeNode(node.getFirstChild());
	}

	
	protected Type parseTypeNode(XmlNode node) throws Exception
	{
		switch (m_typeTags.get(node.getName()))
		{
			case QNAME:
				{
					UserDefinedType type = createAstNode(UserDefinedType.class);
					parseTextPos(node, type);
					parseQName(node, type.m_name);
					
					return type;
				}
				
			case SIMPLE_TYPE:
				{
					SimpleType type = createAstNode(SimpleType.class, m_simpleTypes.get(node.getFirstChild().getName()).value());
					parseTextPos(node, type);
					
					return type;
				}

			case ARR_TYPE:
					return parseArrayType(node);
				
			case PTR_TYPE:
				{
					Type type = parseTypeNode(node.getFirstChild());
					type.m_ptrIndirection = node.getNodeList("Ptr").size();
					
					return type;
				}

			case NULL_TYPE:
				{
					Type type = parseTypeNode(node.getFirstChild());
					type.m_nullable = true;
					
					return type;
				}
				
			case STRING:
			case OBJECT:
			case VOID:
				{
					SimpleType type = createAstNode(SimpleType.class, m_simpleTypes.get(node.getName()).value());
					parseTextPos(node, type);
					
					return type;
				}
				
			default:
				return null;
		}		
	}
	

	protected Type parseArrayType(XmlNode node) throws Exception
	{
		Type type = parseTypeNode(node.getFirstChild());
		type.m_rank = node.getNodeList("RankSpec").size();
		
		return type;		
	}
	
	
	protected void parseBaseTypes(XmlNode node, Set<Type> baseTypes) throws Exception
	{
		String[] tag = {"ClsType", "ClsIntfType"};
		
		for (String t : tag)
		{
			List<XmlNode> list = node.getNodeList(t);
			
			for (XmlNode n : list)
			{
				baseTypes.add(parseClsIntfType(n));
			}
		}
	}
	

	protected Type parseClsIntfType(XmlNode node) throws Exception
	{
		XmlNode child = node.getFirstChild();
		
		if (child.getName().equals("QName"))
		{
			UserDefinedType type = createAstNode(UserDefinedType.class);
			parseQName(child, type.m_name);
			parseTextPos(child, type);
			
			return type;
		}
		else
		{
			SimpleTypeKind kind;
			
			if (child.getName().equals("STRING"))
				kind = SimpleTypeKind.STRING;
			else
				kind = SimpleTypeKind.OBJECT;
			
			SimpleType type = createAstNode(SimpleType.class, kind.value());
			parseTextPos(node, type);
			
			return type;
		}
	}

	
	protected void parseConstraints(XmlNode node, Set<Constraint> constraints) throws Exception
	{
		List<XmlNode> list = node.getNodeList("Constraint");
		
		for (XmlNode n : list)
		{
			Constraint con = createAstNode(Constraint.class);
			parseTextPos(n, con);
			
			con.m_paramName = parseId(n.getFirstChild().getNode("Id"));
					
			for (XmlNode c = n.getFirstChild().getNextSibling(); c.getNextSibling() != null; c = c.getNextSibling())
			{
				ConstraintBound bound = createAstNode(ConstraintBound.class, m_boundTypes.get(c.getName()).value());
				parseTextPos(c, bound);
				
				if (bound.getKind() == ConstraintBoundKind.TYPE)
				{
					UserDefinedType type = createAstNode(UserDefinedType.class);
					parseTextPos(c, bound);
					parseQName(c, type.m_name);
					bound.setType(type);
					con.m_bounds.add(bound);
				}

			}
			
			constraints.add(con);
		}
	}
	
	
	protected void parseTypeParams(XmlNode node, List<TypeParameter> typeParams) throws Exception
	{
		List<XmlNode> list = node.getNodeList("TypeParam");
		
		for (XmlNode n : list)
		{
			TypeParameter param = createAstNode(TypeParameter.class);
			
			param.m_name = parseId(n.getNode("Id"));
			parseTextPos(n, param);
			
			parseAttributes(n, param.m_attributes);
			
			typeParams.add(param);			
		}
	}
	

	protected void parseMembers(XmlNode node, Set<AbstractMember> members) throws Exception
	{
		parseFuncMembers(node, members);
		parseDataMembers(node, members);
		parsePropMembers(node, members);
		parseEventMembers(node, members);
		parseIndexerMembers(node, members);
		parseOperatorMembers(node, members);
		parseConstrMembers(node, members);
		parseFixedSizeBufMembers(node, members);
		
		MemberTypeDeclWrapperFactory factory = new MemberTypeDeclWrapperFactory();
		parseTypeDecls(node, members, factory);
		parseDelegateDecls(node, members, factory);
		parseEnumDecls(node, members, factory);
	}
	
	
	protected void parseFuncMembers(XmlNode node, Set<AbstractMember> members) throws Exception
	{
		String[] tag = {"MethodDecl", "IntfMethodDecl"};
		
		for (String m : tag)
		{
			List<XmlNode> list = node.getNodeList(m);
			
			for (XmlNode n : list)
			{
				FunctionalMember method = createAstNode(FunctionalMember.class, FuncMemberKind.METHOD.value());
				parseTextPos(n, method);
				
				parseAttributes(n, method.m_attributes);
				parseModifiers(n, method.m_modifiers);
				if (n.getName().equals("MethodDecl"))
					parseQName(n.getNode("QName"), method.m_name);
				else
					method.m_name.add(parseName(n.getNode("Id")));
				parseTypeParams(n, method.m_typeParams);
				parseConstraints(n, method.m_constraints);
				
				if (n.getNode("VOID") != null)
				{
					SimpleType type = createAstNode(SimpleType.class, SimpleTypeKind.VOID.value());
					parseTextPos(n.getNode("VOID"), type);
					method.setType(type);				
				}
				else
				{
					method.setType(parseType(n.getNode("Type")));
				}
				
				parseFormalParams(n, method.m_parameters);
				method.setBlock(parseBlock(n));
				
				members.add(method);
			}
		}
	}
	
	
	protected void parseDataMembers(XmlNode node, Set<AbstractMember> members) throws Exception
	{
		final String[] tag = {"FldDecl", "ConstDecl"};
		
		for (String t : tag)
		{
			List<XmlNode> list = node.getNodeList(t);

			for (XmlNode n : list)
			{
				DataMember member;
				
				if (n.getName().equals("FldDecl"))
					member = createAstNode(DataMember.class, DataMemberKind.FIELD.value());
				else
					member = createAstNode(DataMember.class, DataMemberKind.CONST.value());
				
				parseTextPos(n, member);
				parseModifiers(n, member.m_modifiers);		
				member.setType(parseType(n.getNode("Type")));
				parseDeclarators(n, member.m_declarators);
							
				members.add(member);
			}
		}	
	}
		
	
	protected void parseDeclarators(XmlNode node, Set<Declarator> declarators) throws Exception
	{
		String[] tag = {"LocalVarDclr", "VarDclr", "ConstDclr"};
		
		for (String dclr : tag)
		{
			List<XmlNode> list = node.getNodeList(dclr);
			
			for (XmlNode n : list)
			{
				Declarator d = createAstNode(Declarator.class);
				parseTextPos(n, d);
				
				d.m_name = parseId(n.getNode("Id"));
	
				if (n.getName().equals("ConstDclr"))
				{
					ExpressionInitializer init = createAstNode(ExpressionInitializer.class);
					parseTextPos(n, init);
					init.setExpression(parseExpr(n));
					d.setInitializer(init);
				}
				else
				{
					if (n.getChildCount() > 1)
					{
						d.setInitializer(parseVarInit(n.getLastChild()));
					}
				}
				
				declarators.add(d);
			}
		}
	}
	
	
	protected void parsePropMembers(XmlNode node, Set<AbstractMember> members) throws Exception
	{
		String[] tag = {"PropDecl", "IntfPropDecl"};
		
		for (String s : tag)
		{
			List<XmlNode> list = node.getNodeList(s);
				
			for (XmlNode n : list)
			{
				PropertyMember prop = createAstNode(PropertyMember.class);
				parseTextPos(n, prop);
				
				parseAttributes(n, prop.m_attributes);
				parseModifiers(n, prop.m_modifiers);
				prop.setType(parseType(n.getNode("Type")));
				
				if (n.getName().equals("PropDecl"))
					parseQName(n.getNode("QName"), prop.m_name);
				else
					prop.m_name.add(parseName(n.getNode("Id")));
				
				XmlNode accessor = n.getNode("GetAcsrDecl");
				
				if (accessor == null)
					accessor = n.getNode("IntfGetAcsr");
				
				prop.setGetAccessor(parseAccessor(accessor));
				
				
				accessor = n.getNode("SetAcsrDecl");
				
				if (accessor == null)
					accessor = n.getNode("IntfSetAcsr");
				
				prop.setSetAccessor(parseAccessor(accessor));
				
				members.add(prop);
			}
		}
	}
	
	
	protected void parseEventMembers(XmlNode node, Set<AbstractMember> members) throws Exception
	{
		String[] tag = {"EvDecl", "IntfEvDecl"};
		
		for (String t : tag)
		{
			List<XmlNode> list = node.getNodeList(t);
			
			for (XmlNode n : list)
			{
				EventMember event = createAstNode(EventMember.class);
				parseTextPos(n, event);
				
				parseAttributes(n, event.m_attributes);
				parseModifiers(n, event.m_modifiers);
				
				if (n.getName().equals("EvDecl"))
				{
					if (n.getNode("QName") != null)
					{
						parseQName(n.getNode("QName"), event.m_name);
						event.setAddAccessor(parseAccessor(n.getNode("AddAcsrDecl")));
						event.setRemoveAccessor(parseAccessor(n.getNode("RemoveAcsrDecl")));
					}
					else
					{
						parseDeclarators(n, event.m_declarators);
					}
					
					event.setType(parseType(n.getNode("Type")));				
				}
				else
				{
					event.setType(parseType(n.getNode("Type")));
					event.m_name.add(parseName(n.getNode("Id")));
				}
				
				members.add(event);
			}
		}
	}
	
	
	protected void parseIndexerMembers(XmlNode node, Set<AbstractMember> members) throws Exception
	{
		String[] tag = {"IdxrDecl", "IntfIdxrDecl"};
		
		for (String s : tag)
		{
			List<XmlNode> list = node.getNodeList(s);
			
			for (XmlNode n : list)
			{
				IndexerMember m = createAstNode(IndexerMember.class);
				parseTextPos(n, m);
				
				parseAttributes(n, m.m_attributes);
				parseModifiers(n, m.m_modifiers);
				m.setType(parseType(n.getNode("Type")));
				XmlNode t = n.getNode("IntfType");
				
				if (t != null)
					m.setInterfaceType(parseClsIntfType(t));
				
				parseFormalParams(n, m.m_parameters);
				
				XmlNode accessor = n.getNode("GetAcsrDecl");
				
				if (accessor == null)
					accessor = n.getNode("IntfGetAcsr");
				
				m.setGetAccessor(parseAccessor(accessor));
				
				
				accessor = n.getNode("SetAcsrDecl");
				
				if (accessor == null)
					accessor = n.getNode("IntfSetAcsr");
				
				m.setSetAccessor(parseAccessor(accessor));
				
				members.add(m);
			}
		}
 	}
	
	
	protected void parseOperatorMembers(XmlNode node, Set<AbstractMember> members) throws Exception
	{
		List<XmlNode> list = node.getNodeList("OperDecl");
		
		for (XmlNode n : list)
		{
			FunctionalMember oper = createAstNode(FunctionalMember.class);
			parseTextPos(n, oper);
			
			XmlNode oper_node = n.getLastChild().getPrevSibling();
			
			parseAttributes(n, oper.m_attributes);
			parseModifiers(n, oper.m_modifiers);
			oper.setKind(m_operTags.get(oper_node.getName()).value());
			
			oper.setType(parseType(oper_node.getFirstChild()));
			
			Parameter par = createAstNode(Parameter.class, ParamKind.VALUE.value());
			
			XmlNode type_node = oper_node.getFirstChild().getNextSibling();
			XmlNode id_node = type_node.getNextSibling();

			par.setType(parseType(type_node));
			par.m_name = parseId(id_node);
			
			par.m_left = Integer.parseInt(type_node.getAttribute("l"));
			par.m_right = Integer.parseInt(id_node.getAttribute("r"));
			
			oper.m_parameters.add(par);
			
			if (oper_node.getChildCount() == 5)
			{
				par = createAstNode(Parameter.class, ParamKind.VALUE.value());
				
				id_node = oper_node.getLastChild();
				type_node = id_node.getPrevSibling();
				
				par.setType(parseType(type_node));
				par.m_name = parseId(id_node);
				
				par.m_left = Integer.parseInt(type_node.getAttribute("l"));
				par.m_right = Integer.parseInt(id_node.getAttribute("r"));
			
				oper.m_parameters.add(par);
			}
			
			oper.setBlock(parseBlock(n));

			members.add(oper);
		}
	}
	
	
	protected void parseConstrMembers(XmlNode node, Set<AbstractMember> members) throws Exception
	{
		List<XmlNode> list = node.getNodeList("ConstrDecl");
		
		for (XmlNode n : list)
		{
			FunctionalMember func = createAstNode(FunctionalMember.class, FuncMemberKind.CONSTRUCTOR.value());
			parseTextPos(n, func);

			parseAttributes(n, func.m_attributes);
			parseModifiers(n, func.m_modifiers);
			func.m_name.add(parseName(n.getNode("Id")));
			parseFormalParams(n, func.m_parameters);
			
			XmlNode init = n.getNode("ConstrInit");
			
			if (init != null)
			{
				MethodInvocationExpression invoc = createAstNode(MethodInvocationExpression.class);
				parseTextPos(init, invoc);
				XmlNode c = init.getFirstChild();
				
				if (c.getName().equals("BaseInit"))
					invoc.setMethodRef(createAstNode(SelfReferenceExpression.class, SelfRefExprKind.BASE.value()));
				else
					invoc.setMethodRef(createAstNode(SelfReferenceExpression.class, SelfRefExprKind.THIS.value()));
				
				invoc.getMethodRef().m_left = invoc.m_left;
				invoc.getMethodRef().m_right = invoc.m_left + 6;
				
				parseArguments(c, invoc.m_arguments);
				
				XmlNode argLst = c.getNode("ArgLstExpr");
				
				if (argLst != null)
					parseExprList(argLst, invoc.m_argList);
				
				func.m_constrInit.add(invoc);
			}

			func.setBlock(parseBlock(n));
			
			members.add(func);
		}
	}
	
	
	protected void parseFixedSizeBufMembers(XmlNode node, Set<AbstractMember> members) throws Exception
	{
		List<XmlNode> list = node.getNodeList("FixedSizeBufDecl");
		
		for (XmlNode nd : list)
		{
			FixedSizeBufMember m = createAstNode(FixedSizeBufMember.class);
			parseTextPos(nd, m);
			
			parseAttributes(nd, m.m_attributes);
			parseModifiers(nd, m.m_modifiers);
			
			List<XmlNode> lst = nd.getNodeList("FixedSizeBufDclr");
			
			for (XmlNode n : lst)
			{
				FixedSizeBufDeclarator decl = createAstNode(FixedSizeBufDeclarator.class);
				decl.m_name = parseId(n.getNode("Id"));
				decl.setExpression(parseExpr(n));

				m.m_declarators.add(decl);
			}
			
			members.add(m);
		}
	}
	
	
	protected void parseModifiers(XmlNode node, Set<Modifier> modifiers) throws Exception
	{
		XmlNode mod = node.getNode("Modifiers");
		
		if (mod == null)
			return;
		
		List<XmlNode> list = mod.getAllChildren();
		
		XmlNode p = node.getNode("Partial");
		if (p != null)
			list.add(p);

		for (XmlNode n : list)
		{
			Modifier m = null;
			ModifierKind kind = m_modifiers.get(n.getName());
			
			if (modifiers != null && kind != null)
			{
				m = createAstNode(Modifier.class, kind.value());
				parseTextPos(n, m);
				modifiers.add(m);
			}
		}			
	}	
	
	
	protected void parseQName(XmlNode node, List<Name> names) throws Exception
	{
		XmlNode child = node.getFirstChild();
		
		if (child.getName().equals("QAlias"))
		{
			names.add(parseName(child.getFirstChild()));
			names.add(parseName(child.getLastChild()));
		}
		
		while (child != null)
		{
			if (child.getName().equals("Id"))
			{
				names.add(parseName(child));
			}
			else if (child.getName().equals("Type"))
			{
				TypeArgument arg = createAstNode(TypeArgument.class, TypeArgKind.EXACT.value());
				parseTextPos(child, arg);
				arg.setRefType(parseType(child));
				names.get(names.size() - 1).m_arguments.add(arg);
			}
			
			child = child.getNextSibling();
		}
	}
	
	
	protected Accessor parseAccessor(XmlNode node) throws Exception
	{
		if (node == null)
			return null;
		
		Accessor accessor = createAstNode(Accessor.class);
		parseTextPos(node, accessor);
		
		parseAttributes(node, accessor.m_attributes);
		parseModifiers(node, accessor.m_modifiers);
		
		accessor.setBlock(parseBlock(node));
		
		return accessor;
	}
	
	
	protected BlockStatement parseBlock(XmlNode node) throws Exception
	{
		XmlNode n = node.getNode("Blk");
		
		if (n != null)
		{
			BlockStatement blk = createAstNode(BlockStatement.class);
			parseTextPos(n, blk);
			
			blk.m_statements.addAll(parseStatements(n));
			
			return blk;
		}
		else
			return null;
	}
	
	
	protected List<AbstractStatement> parseStatements(XmlNode node) throws Exception
	{
		List<XmlNode> list = node.getNodeList("Stmt");
		TolerantList<AbstractStatement> output = new TolerantList<AbstractStatement>();
		
		for (XmlNode n : list)
		{
			XmlNode child = n.getFirstChild();
			
			switch (m_stmtTags.get(child.getName()))
			{
				case LBL_STMT:
					{
						LabelStatement lbl = createAstNode(LabelStatement.class);
						parseTextPos(child, lbl);
						
						lbl.m_label = parseId(child.getNode("Id"));
						lbl.setStatement(parseStatements(child).get(0));
						
						output.add(lbl);
					}
					break;
					
				case DECL_STMT:
					{
						LocalVarDeclStatement stmt = createAstNode(LocalVarDeclStatement.class);
						parseTextPos(child, stmt);
						
						stmt.setDeclaration(parseLocalVarDecl(child));
						
						output.add(stmt);
					}
					break;
					
				case EMBED_STMT:
					output.add(parseEmbedStatement(child));
			}
		}
		
		return output;
	}

	
	protected EmbeddedStatement parseEmbedStatement(XmlNode node) throws Exception
	{
		EmbeddedStatement stmt = null;
		XmlNode child = node.getFirstChild();
		EmbedStmtTag tag = m_embedStmtTags.get(child.getName()); 

		switch (tag)
		{
			case BLOCK:
				stmt = parseBlock(node);
				break;
				
			case EMPTY:
				break;
				
			case EXPR:
				{
					ExpressionStatement e = createAstNode(ExpressionStatement.class);
					e.setExpression(parseExprNode(child.getFirstChild()));
					
					stmt = e;
				}
				break;
				
			case IF:
				{
					IfStatement is = createAstNode(IfStatement.class);
					is.setCondition(parseExpr(child));
					is.setIfStmt(parseEmbedStatement(child.getFirstChild().getNextSibling()));
					if (child.getChildCount() > 2)
						is.setElseStmt(parseEmbedStatement(child.getLastChild()));

					stmt = is;
				}
				break;
				
			case SWITCH:
				{
					SwitchStatement ss = createAstNode(SwitchStatement.class);
					ss.setSwitchExpr(parseExpr(child));
					
					List<XmlNode> list = child.getNodeList("SwitchSec");
					
					for (XmlNode n : list)
					{
						SwitchSection sec = createAstNode(SwitchSection.class);
						parseTextPos(n, sec);
						
						List<XmlNode> lst = n.getNodeList("SwitchLbl");
						
						for (XmlNode lbl : lst)
						{
							SwitchLabel sl = createAstNode(SwitchLabel.class);
							parseTextPos(lbl, sl);
							
							if (lbl.getChildCount() > 0)
							{
								sl.setKind(SwitchLabelKind.CASE.value());
								sl.setLabelExpr(parseExpr(lbl));
							}
							else
								sl.setKind(SwitchLabelKind.DEFAULT.value());
								
							sec.m_labels.add(sl);
						}
						
						sec.m_statements.addAll(parseStatements(n));
						
						ss.m_sections.add(sec);
					}
					
					stmt = ss;
				}
				break;
				
			case WHILE:
			case DO:
				{
					DoWhileStatement ds = createAstNode(DoWhileStatement.class, 
							tag == EmbedStmtTag.DO ? DoWhileStmtKind.DO.value() : DoWhileStmtKind.WHILE.value());
					
					ds.setCondition(parseExpr(child));
					ds.setBody(parseEmbedStatement(child.getNode("EmbedStmt")));
					
					stmt = ds;
				}
				break;
				
			case FOR:
				{
					ForStatement fs = createAstNode(ForStatement.class);
					
					XmlNode n = child.getNode("ForInit");

					if (n != null)
					{
						if (n.getFirstChild().getName().equals("LocalVarDecl"))
							fs.setInitVarDecl(parseLocalVarDecl(n));
						else
						{
							List<XmlNode> list = n.getNodeList("StmtExpr");
							
							for (XmlNode e : list)
								fs.m_initExprList.add(parseExprNode(e.getFirstChild()));
						}
					}
					
					n = child.getNode("Expr");

					if (n != null)
						fs.setCondition(parseExpr(child));
					
					n = child.getNode("ForIter");
					
					if (n != null)
					{
						List<XmlNode> list = n.getNodeList("StmtExpr");
						
						for (XmlNode e : list)
							fs.m_iteration.add(parseExprNode(e.getFirstChild()));
					}						

					fs.setForStmt(parseEmbedStatement(child.getNode("EmbedStmt")));
					
					stmt = fs;
				}
				break;

			case FOREACH:
				{
					ForEachStatement fs = createAstNode(ForEachStatement.class);
					
					fs.setType(parseType(child.getFirstChild()));
					fs.m_identifier = parseId(child.getNode("Id"));
					fs.setInitExpr(parseExpr(child));
					fs.setForEachStmt(parseEmbedStatement(child.getLastChild()));
					
					stmt = fs;
				}
				break;
				
			case BREAK:
			case CONTINUE:
				stmt = createAstNode(JumpStatement.class,
						tag == EmbedStmtTag.BREAK ?	JumpStmtKind.BREAK.value() : JumpStmtKind.CONTINUE.value());
				break;
				
			case GOTOID:
				{
					JumpStatement js = createAstNode(JumpStatement.class, JumpStmtKind.GOTO_ID.value());
					js.m_label = parseId(child.getFirstChild());
					
					stmt = js;
				}
				break;
				
			case GOTOCASE:
				{
					JumpStatement js = createAstNode(JumpStatement.class, JumpStmtKind.GOTO_CASE.value());
					js.setJumpExpr(parseExpr(child));
					
					stmt = js;
				}				
				break;
				
			case GOTODEFAULT:
				stmt = createAstNode(JumpStatement.class, JumpStmtKind.GOTO_DEFAULT.value());
				break;
				
			case RETURN:
			case THROW:
				{
					JumpStatement js = createAstNode(JumpStatement.class, 
								tag == EmbedStmtTag.RETURN ? JumpStmtKind.RETURN.value() : JumpStmtKind.THROW.value());
					
					if (child.getNode("Expr") != null)
						js.setJumpExpr(parseExpr(child));
					
					stmt = js;
				}
				break;

			case CHECKED:
			case UNCHECKED:
				{
					CheckedStatement cs = createAstNode(CheckedStatement.class,
							tag == EmbedStmtTag.CHECKED ? CheckedStmtKind.CHECKED.value() : CheckedStmtKind.UNCHECKED.value());
					
					cs.setCheckedBlock(parseBlock(child));
					
					stmt = cs;
				}
				break;

			case LOCK:
				{
					SyncStatement ss = createAstNode(SyncStatement.class);
					
					ss.setLockExpr(parseExpr(child));
					ss.setStatement(parseEmbedStatement(child.getLastChild()));
					
					stmt = ss;
				}
				break;
				
			case UNSAFE:
				{
					UnsafeStatement us = createAstNode(UnsafeStatement.class);
					us.setUnsafeBlock(parseBlock(child));
					
					stmt = us;
				}
				break;

				
			case YIELD:
				{
					YieldStatement ys = createAstNode(YieldStatement.class);
					
					if (child.getNode("Expr") == null)
						ys.setKind(YieldStmtKind.BREAK.value());
					else
					{
						ys.setKind(YieldStmtKind.RETURN.value());
						ys.setYieldExpr(parseExpr(child));
					}
					
					stmt = ys;
				}
				break;
				
			case TRY:
				{
					TryStatement ts = createAstNode(TryStatement.class);
					
					ts.setTryBlock(parseBlock(child));
					
					XmlNode n = child.getNode("GeneralCatch");
					
					if (n != null)
						ts.setGeneralCatch(parseBlock(n));
					
					n = child.getNode("FinallyBlk");
					
					if (n != null)
						ts.setFinallyBlock(parseBlock(n));
					
					List<XmlNode> list = child.getNodeList("SpecificCatch");
					
					for (XmlNode c : list)
					{
						CatchBlock b = createAstNode(CatchBlock.class);
						parseTextPos(c, b);
						
						Parameter par = createAstNode(Parameter.class);
						par.m_left = Integer.parseInt(c.getFirstChild().getAttribute("l"));
						
						par.setType(parseClsIntfType(c.getNode("ClsType")));
						
						XmlNode id_node = c.getNode("Id");
						
						if (id_node == null)
							par.m_right = Integer.parseInt(c.getFirstChild().getAttribute("r"));
						else
						{
							par.m_right = Integer.parseInt(id_node.getAttribute("r"));
							par.m_name = parseId(id_node);
						}
						
						b.setParameter(par);
						
						ts.m_catchSet.add(b);
					}
					
					stmt = ts;
				}
				break;
				
			case USING:
				{
					UsingStatement us = createAstNode(UsingStatement.class);
					
					ResourceAcquisition r = createAstNode(ResourceAcquisition.class);
					parseTextPos(child.getFirstChild(), r);
					us.setResAcquisition(r);
					
					if (child.getFirstChild().getName().equals("LocalVarDecl"))
					{
						r.setKind(ResAcquisitionKind.VAR_DECL.value());
						r.setDeclaration(parseLocalVarDecl(child));
					}
					else
					{
						r.setKind(ResAcquisitionKind.EXPR.value());
						r.setExpression(parseExpr(child));
					}
					
					us.setStatement(parseEmbedStatement(child.getLastChild()));
					
					stmt = us;
				}
				break;
				
			case FIXED:
				{
					FixedStatement fs = createAstNode(FixedStatement.class);

					fs.setFixedStatement(parseEmbedStatement(child.getLastChild()));
					fs.setType(parseTypeNode(child.getFirstChild()));
					
					List<XmlNode> list = child.getNodeList("FixedPtrDclr");
					
					for (XmlNode n : list)
					{
						Declarator d = createAstNode(Declarator.class);
						parseTextPos(n, d);
						
						ExpressionInitializer init = createAstNode(ExpressionInitializer.class);
						parseTextPos(n.getLastChild(), init);
						init.setExpression(parseExpr(n));
						
						d.m_name = parseId(n.getFirstChild());

						fs.m_declarators.add(d);
					}
					
					stmt = fs;
				}
		}
		
		if (stmt != null)
			parseTextPos(child, stmt);
		
		return stmt;
	}

	
	protected Expression parseExpr(XmlNode node) throws Exception
	{
		return parseExprNode(node.getNode("Expr").getFirstChild());
	}
	
	
	protected Expression parseExprNode(XmlNode node) throws Exception
	{
		Expression expr = null;
		ExprTag tag = m_exprTags.get(node.getName());
				
		switch (tag)
		{
			case NULL_COALES:
			case MOD:
			case DIV:
			case MULT:
			case MINUS:
			case PLUS:
			case LSHIFT:
			case RSHIFT:
			case GT_EQ:
			case LESS_EQ:
			case GT:
			case LESS:		
			case NOT_EQ:
			case EQ:		
			case AND:		
			case EXCL_OR:
			case INCL_OR:
			case COND_AND:
			case COND_OR:
				{
					BinaryExpression e = createAstNode(BinaryExpression.class, m_binaryExpr.get(tag).value());
					e.setLeftOperand(parseExprNode(node.getFirstChild()));
					e.setRightOperand(parseExprNode(node.getLastChild()));
					expr = e;
				}
				break;

			case MEM_ACCESS:
			case PTR_MEM_ACCESS:	
				{
					IdentifierExpression ie = createAstNode(IdentifierExpression.class);
					parseTextPos(node.getLastChild(), ie);
					ie.m_name.add(parseName(node.getLastChild()));
				
					XmlNode child = node.getFirstChild();
					
					if (child.getName().equals("PredefType"))
					{
						UnaryTypeExpression e = createAstNode(UnaryTypeExpression.class, UnaryTypeExprKind.TYPE_MEMBER_ACCESS.value());

						SimpleType type = createAstNode(SimpleType.class, m_simpleTypes.get(child.getFirstChild().getName()).value());
						parseTextPos(node, type);
						e.setType(type);
						
						e.setOperand(ie);
						expr = e;
					}
					else
					{
						BinaryExpression e = createAstNode(BinaryExpression.class, m_binaryExpr.get(tag).value());
						e.setLeftOperand(parseExprNode(node.getFirstChild()));
						
						e.setRightOperand(ie);
						expr = e;
					}
				}
				break;
				
			case ASSN:		
			case PLUS_ASSN:
			case MINUS_ASSN:		
			case MULT_ASSN:
			case DIV_ASSN:
			case AND_ASSN:
			case OR_ASSN:
			case XOR_ASSN:		
			case MOD_ASSN:
			case LSHIFT_ASSN:		
			case RSHIFT_ASSN:	
				{
					BinaryExpression e = createAstNode(BinaryExpression.class, m_binaryExpr.get(tag).value());
					e.setLeftOperand(parseExprNode(node.getFirstChild()));
					e.setRightOperand(parseExpr(node));
					expr = e;
				}
				break;
				
			case UNARY_PLUS:
			case UNARY_MINUS:
			case NOT:		
			case INV:
			case PRE_INCR:
			case PRE_DECR:
			case POST_INCR:
			case POST_DECR:
			case PTR_INDIR:
			case ADDROF:
				{
					UnaryExpression e = createAstNode(UnaryExpression.class, m_unaryExpr.get(tag).value());
					e.setOperand(parseExprNode(node.getFirstChild()));
					expr = e;
				}
				break;
				
			case SIZEOF:
				{
					TypeExpression e = createAstNode(TypeExpression.class, TypeExprKind.SIZEOF.value());
					e.setType(parseType(node.getNode("Type")));
					expr = e;
				}
				break;

			case MAKE_REF:
			case REF_TYPE:
				{
					UnaryExpression e = createAstNode(UnaryExpression.class, m_unaryExpr.get(tag).value());
					e.setOperand(parseExpr(node));
					expr = e;
				}
				break;
				
				
			case CHECKED:
			case UNCHECKED:				
			case PAREN:
				{
					UnaryExpression e = createAstNode(UnaryExpression.class, m_unaryExpr.get(tag).value());
					e.setOperand(parseExpr(node));
					expr = e;
				}
				break;
				
			case INT_LITERAL:
			case REAL_LITERAL:
			case CHAR_LITERAL:
			case STR_LITERAL:
				{
					LiteralExpression e = createAstNode(LiteralExpression.class, m_literExpr.get(tag).value());
					e.m_value = getTermValue(node);
					expr = e;
				}
				break;
				
			case NULL:
			case TRUE:
			case FALSE:
				expr = createAstNode(LiteralExpression.class, m_literExpr.get(tag).value());
				break;
				
			case THIS_ACCESS:
				expr = createAstNode(SelfReferenceExpression.class, SelfRefExprKind.THIS.value());
				break;
				
			case BASE_ACCESS:
				{
					XmlNode child = node.getFirstChild();
					
					if (child.getName().equals("Id"))
					{
						BinaryExpression b = createAstNode(BinaryExpression.class, BinaryExprKind.MEMBER_ACCESS.value());
						
						SelfReferenceExpression s = createAstNode(SelfReferenceExpression.class, SelfRefExprKind.BASE.value());
						parseTextPos(node, s);
						b.setLeftOperand(s);
						
						IdentifierExpression id = createAstNode(IdentifierExpression.class);
						id.m_name.add(parseName(node.getLastChild()));
						b.setRightOperand(id);
						
						expr = b;
					}
					else
					{
						BinaryExpression e = createAstNode(BinaryExpression.class, BinaryExprKind.ARRAY_ACCESS.value());
						BinaryExpression cur = e;
						List<XmlNode> list = node.getNodeList("Expr");
						for (int i = list.size() - 1; i >= 0; i--)
						{
							XmlNode n = list.get(i);
							cur.setRightOperand(parseExprNode(n.getFirstChild()));
							if (i > 1)
							{
								BinaryExpression l = createAstNode(BinaryExpression.class, BinaryExprKind.ARRAY_ACCESS.value());
								l.m_left = Integer.parseInt(node.getFirstChild().getAttribute("l"));
								l.m_right = Integer.parseInt(n.getAttribute("r"));
								cur.setLeftOperand(l);
								cur = l;
							}
							else
							{
								SelfReferenceExpression s = createAstNode(SelfReferenceExpression.class, SelfRefExprKind.BASE.value());
								parseTextPos(node, s);
								cur.setLeftOperand(s);
							}							
						}
						
						expr = e;						
					}
				}
				break;

			case QNAME:
				{
					IdentifierExpression e = createAstNode(IdentifierExpression.class);
					parseQName(node, e.m_name);
					expr = e;
				}
				break;
				
			case COND:
				{
					ConditionExpression e = createAstNode(ConditionExpression.class);
					e.setCondition(parseExprNode(node.getFirstChild()));
					e.setTrueExpr(parseExprNode(node.getFirstChild().getNextSibling().getFirstChild()));
					e.setFalseExpr(parseExprNode(node.getLastChild().getFirstChild()));
					expr = e;										
				}
				break;

			case IS: 
				{
					UnaryTypeExpression e = createAstNode(UnaryTypeExpression.class, UnaryTypeExprKind.IS.value());
					e.setOperand(parseExprNode(node.getFirstChild()));
					e.setType(parseType(node.getLastChild()));
					expr = e;
				}
				break;
				
			case AS:
				{
					UnaryTypeExpression e = createAstNode(UnaryTypeExpression.class, UnaryTypeExprKind.AS.value());
					e.setOperand(parseExprNode(node.getFirstChild()));
					e.setType(parseType(node.getLastChild()));
					expr = e;					
				}
				break;

			case CAST:
				{
					UnaryTypeExpression e = createAstNode(UnaryTypeExpression.class, UnaryTypeExprKind.CAST.value());
					e.setType(parseType(node.getFirstChild()));
					e.setOperand(parseExprNode(node.getLastChild()));
					expr = e;
				}
				break;

			case REF_VALUE:
				{
					UnaryTypeExpression e = createAstNode(UnaryTypeExpression.class, UnaryTypeExprKind.REF_VALUE.value());
					e.setOperand(parseExprNode(node.getFirstChild()));
					e.setType(parseType(node.getLastChild()));
					expr = e;					
				}
				break;

			case DEFAULT_VAL:
				{
					TypeExpression e = createAstNode(TypeExpression.class, TypeExprKind.DEFAULT_VALUE.value());
					e.setType(parseType(node.getFirstChild()));
					expr = e;
				}
				break;

			case ANONYM_METHOD:
				{
					AnonymousMethodExpression e = createAstNode(AnonymousMethodExpression.class);
					parseFormalParams(node, e.m_parameters);
					e.setBlock(parseBlock(node));
					expr = e;
				}
				break;

			case INVOC:
				{
					MethodInvocationExpression e = createAstNode(MethodInvocationExpression.class);
					e.setMethodRef(parseExprNode(node.getFirstChild()));
					parseArguments(node, e.m_arguments);
					
					XmlNode argLst = node.getNode("ArgLstExpr");
					
					if (argLst != null)
						parseExprList(argLst, e.m_argList);
					
					expr = e;
				}
				break;

			case OBJ_CREAT:
				{
					InstanceCreationExpression e = createAstNode(InstanceCreationExpression.class);
					e.setType(parseType(node.getNode("Type")));
					parseArguments(node, e.m_arguments);
					expr = e;
				}
				break;

			case ELEM_ACCESS:	
				{
					BinaryExpression e = createAstNode(BinaryExpression.class, BinaryExprKind.ARRAY_ACCESS.value());
					BinaryExpression cur = e;
					List<XmlNode> list = node.getNodeList("Expr");
					for (int i = list.size() - 1; i >= 0; i--)
					{
						XmlNode n = list.get(i);
						cur.setRightOperand(parseExprNode(n.getFirstChild()));
						if (i > 1)
						{
							BinaryExpression l = createAstNode(BinaryExpression.class, BinaryExprKind.ARRAY_ACCESS.value());
							l.m_left = Integer.parseInt(node.getFirstChild().getAttribute("l"));
							l.m_right = Integer.parseInt(n.getAttribute("r"));
							cur.setLeftOperand(l);
							cur = l;
						}
						else
						{
							cur.setLeftOperand(parseExprNode(node.getFirstChild()));
						}							
					}
					
					expr = e;
				}
				break;
				
			case ARR_CREAT:
			case ARR_CREINIT:
				{
					ArrayCreationExpression e = createAstNode(ArrayCreationExpression.class);
					XmlNode child = node.getFirstChild();
					
					if (child.getName().equals("Type"))
					{
						e.setType(parseType(child));
						parseExprList(node, e.m_dimExpr);
						e.m_rank = node.getNodeList("RankSpec").size() + node.getNodeList("Expr").size();
					}
					else
					{
						e.setType(parseArrayType(child));
						e.m_rank = node.getNodeList("RankSpec").size();
					}
					
					if (node.getLastChild().getName().equals("ArrInit"))
					{
						e.setInitializer(parseArrayInit(node.getLastChild()));
					}
					
					expr = e;
				}
				break;
				
			case TYPEOF:
				{
					TypeExpression te = createAstNode(TypeExpression.class, TypeExprKind.TYPEOF.value());
					XmlNode child = node.getFirstChild();
					
					switch (m_typeofTags.get(child.getName()))
					{
						case TYPE:
							te.setType(parseType(child));
							break;
							
						case VOID:
							{
								SimpleType t = createAstNode(SimpleType.class, SimpleTypeKind.VOID.value());
								parseTextPos(child, t);
								te.setType(t);
							}
							break;
							
						case UNBOUND_TYPE:
							{
								UserDefinedType t = createAstNode(UserDefinedType.class);
								parseTextPos(node, t);
								
								List<XmlNode> list= child.getNodeList("QName");
								
								for (XmlNode n : list)
								{
									parseQName(n, t.m_name);
								}
								
								XmlNode id = child.getNode("Id");
								
								if (id != null)
									t.m_name.add(parseName(id));
								
								te.setType(t);
							}
					}
					
					expr = te;
				}
		}
		
		if (expr != null)
		{
			parseTextPos(node, (AstNode)expr);
		}

		return expr;		
	}
	
	
	protected void parseArguments(XmlNode node, List<Expression> arguments) throws Exception
	{
		List<XmlNode> list = node.getNodeList("Arg");
		
		for (XmlNode n : list)
		{
			XmlNode child = n.getFirstChild();
			switch(m_argTags.get(child.getName()))
			{
				case VALUE:
					arguments.add(parseExpr(n));
					break;
					
				case REF:
					{
						UnaryExpression e = createAstNode(UnaryExpression.class, UnaryExprKind.REF_ARG.value());
						parseTextPos(child, e);
						e.setOperand(parseExpr(child));
						arguments.add(e);
					}
					break;
					
				case OUT:
					{
						UnaryExpression e = createAstNode(UnaryExpression.class, UnaryExprKind.OUT_ARG.value());
						parseTextPos(child, e);
						e.setOperand(parseExpr(child));
						arguments.add(e);
					}
			}
		}
	}
	
	
	protected void parseExprList(XmlNode node, List<Expression> exprs) throws Exception
	{
		List<XmlNode> list = node.getNodeList("Expr");
		
		for (XmlNode n : list)
		{
			exprs.add(parseExprNode(n.getFirstChild()));
		}
	}
	
	
	protected LocalVariableDeclaration parseLocalVarDecl(XmlNode node) throws Exception
	{
		XmlNode n = node.getNode("LocalVarDecl");
		if (n == null)
			n = node.getNode("LocalConstDecl");

		LocalVariableDeclaration decl = createAstNode(LocalVariableDeclaration.class);
		parseTextPos(n, decl);
		
		decl.setType(parseType(n.getNode("Type")));
		parseDeclarators(n, decl.m_declarators);
		
		if (n.getName().equals("LocalConstDecl"))
			decl.m_const = true;
		
		return decl;
	}


	protected Initializer parseVarInit(XmlNode node) throws Exception
	{
		XmlNode child = node.getFirstChild();
		
		switch (m_initTags.get(child.getName()))
		{
			case EXPR:
				{
					ExpressionInitializer init = createAstNode(ExpressionInitializer.class);
					parseTextPos(child, init);
					init.setExpression(parseExpr(node));
					
					return init;
				}
				
			case ARRINIT:
				return parseArrayInit(child);
				
			case STACKALLOCINIT:
				{
					StackallocInitializer init = createAstNode(StackallocInitializer.class);
					parseTextPos(child, init);
					init.setType(parseType(child.getFirstChild()));
					init.setExpression(parseExpr(child));
					
					return init;
				}
				
			default:
				return null;
		}
	}
	
	
	protected ArrayInitializer parseArrayInit(XmlNode node) throws Exception
	{
		List<XmlNode> list = node.getNodeList("VarInit");
		ArrayInitializer init = createAstNode(ArrayInitializer.class);
		parseTextPos(node, init);
		
		for (XmlNode n : list)
		{
			init.m_arrayInit.add(parseVarInit(n));
		}
		
		return init;
	}

	
	protected String parseId(XmlNode node)
	{
		XmlNode child = node.getFirstChild();

		if (child.getName().equals("ID"))
			return getTermValue(child);
		else
		{
			String val = child.getName();
			
			if (val.equals("Typ"))
				val = "Type";
			
			return val;
		}			
	}
	
	protected Name parseName(XmlNode node) throws Exception
	{
		Name name = createAstNode(Name.class);
		parseTextPos(node, name);
		name.m_name = parseId(node);
		
		return name;
	}
}
