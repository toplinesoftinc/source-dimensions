package com.sourcedimensions.server.sys.astimport;

import java.util.*;
import com.sourcedimensions.server.ast.*;
import com.sourcedimensions.server.ast.TypeDeclaration.*;
import com.sourcedimensions.server.ast.TypeArgument.*;
import com.sourcedimensions.server.ast.SimpleType.*;
import com.sourcedimensions.server.ast.FunctionalMember.*;
import com.sourcedimensions.server.ast.Parameter.*;
import com.sourcedimensions.server.ast.Modifier.*;
import com.sourcedimensions.server.ast.DataMember.*;
import com.sourcedimensions.server.ast.DoWhileStatement.*;
import com.sourcedimensions.server.ast.JumpStatement.*;
import com.sourcedimensions.server.ast.SwitchLabel.*;
import com.sourcedimensions.server.ast.UnaryExpression.*;
import com.sourcedimensions.server.ast.BinaryExpression.*;
import com.sourcedimensions.server.ast.UnaryTypeExpression.*;
import com.sourcedimensions.server.ast.LiteralExpression.*;
import com.sourcedimensions.server.ast.SelfReferenceExpression.*;


public class JavaImport extends AstImport
{
	protected enum BlkTag
	{
		VAR_DECL,
		TYPE_DECL,
		STATEMENT
	}
	
	protected enum StmtTag
	{
		BLOCK,
		ASSERT,
		EXPR,
		SWITCH,
		DO,
		BREAK,
		CONTINUE,
		RETURN,
		SYNC,
		THROW,
		TRY,
		LABEL,
		IF,
		WHILE,
		FOR,
		ENHANCED_FOR,
		EMPTY
	}
	
	protected enum ExprTag
	{
		INT_LITERAL,
		FLOAT_LITERAL,
		CHAR_LITERAL,
		STR_LITERAL,
		NULL,
		TRUE,
		FALSE,
		THIS,
		SUPER,
		CLASS,
		PAREN,
		INST_CREAT,
		FLD_ACCESS,
		METHOD_INVOC,
		ARR_ACCESS,
		ARR_CREAT,
		PRE_INCR,
		PRE_DECR,
		UNARY_PLUS,
		UNARY_MINUS,
		INV,
		NOT,
		CAST,
		QNAME,
		ID,
		POST_INCR,
		POST_DECR,
		MULT,
		DIV,
		MOD,
		PLUS,
		MINUS,
		LSHIFT,
		RSHIFT,
		USHIFT,
		LESS,
		GT,
		LE,
		GE,
		INSTANCEOF,
		EQ,
		NE,
		AND,
		EXCL_OR,
		INCL_OR,
		COND_AND,
		COND_OR,
		COND,
		ASSN,
		MULT_ASSN,
		DIV_ASSN,
		MOD_ASSN,
		PLUS_ASSN,
		MINUS_ASSN,
		LSHIFT_ASSN,
		RSHIFT_ASSN,
		USHIFT_ASSN,
		AND_ASSN,
		OR_ASSN,
		XOR_ASSN
	}
	
	protected static Hashtable<String, ModifierKind> m_modifiers;
	protected static Hashtable<String, TypeArgKind> m_typeArgs;
	protected static Hashtable<String, SimpleTypeKind> m_simpleTypes;
	protected static Hashtable<String, BlkTag> m_blockTags;
	protected static Hashtable<String, StmtTag> m_stmtTags;
	protected static Hashtable<String, ExprTag> m_exprTags;
	protected static Hashtable<ExprTag, UnaryExprKind> m_unaryExpr;
	protected static Hashtable<ExprTag, BinaryExprKind> m_binaryExpr;
	protected static Hashtable<ExprTag, LiteralExprKind> m_literExpr;
		
	static 
	{
		m_modifiers = new Hashtable<String,ModifierKind>();
		m_modifiers.put("PUBLIC", ModifierKind.PUBLIC);
		m_modifiers.put("PROTECTED", ModifierKind.PROTECTED);
		m_modifiers.put("PRIVATE", ModifierKind.PRIVATE);
		m_modifiers.put("STATIC", ModifierKind.STATIC);
		m_modifiers.put("ABSTRACT", ModifierKind.ABSTRACT);
		m_modifiers.put("FINAL", ModifierKind.FINAL);
		m_modifiers.put("NATIVE", ModifierKind.NATIVE);
		m_modifiers.put("SYNCHRONIZED", ModifierKind.SYNCHRONIZED);
		m_modifiers.put("TRANSIENT", ModifierKind.TRANSIENT);
		m_modifiers.put("VOLATILE", ModifierKind.VOLATILE);
		m_modifiers.put("STRICTFP", ModifierKind.STRICTFP);
		
		m_typeArgs = new Hashtable<String,TypeArgKind>();
		m_typeArgs.put("RefType", TypeArgKind.EXACT);
		m_typeArgs.put("SuperWildcardBnd", TypeArgKind.SUPER);
		m_typeArgs.put("ExtendsWildcardBnd", TypeArgKind.EXTENDS);
		m_typeArgs.put("Wildcard", TypeArgKind.WILDCARD);			

		m_simpleTypes = new Hashtable<String,SimpleTypeKind>();
		m_simpleTypes.put("BOOLEAN", SimpleTypeKind.BOOL);
		m_simpleTypes.put("BYTE", SimpleTypeKind.BYTE);
		m_simpleTypes.put("SHORT", SimpleTypeKind.SHORT);
		m_simpleTypes.put("INT", SimpleTypeKind.INT);
		m_simpleTypes.put("LONG", SimpleTypeKind.LONG);
		m_simpleTypes.put("CHAR", SimpleTypeKind.CHAR);
		m_simpleTypes.put("FLOAT", SimpleTypeKind.FLOAT);
		m_simpleTypes.put("DOUBLE", SimpleTypeKind.DOUBLE);
		
		m_blockTags = new Hashtable<String,BlkTag>();
		m_blockTags.put("LocalVarDecl", BlkTag.VAR_DECL);
		m_blockTags.put("ClsDecl", BlkTag.TYPE_DECL);
		m_blockTags.put("EnumDecl", BlkTag.TYPE_DECL);
		m_blockTags.put("Stmt", BlkTag.STATEMENT);
		
		m_stmtTags = new Hashtable<String,StmtTag>();
		m_stmtTags.put("Blk", StmtTag.BLOCK);
		m_stmtTags.put("AssertStmt", StmtTag.ASSERT);
		m_stmtTags.put("StmtExpr", StmtTag.EXPR);
		m_stmtTags.put("SwitchStmt", StmtTag.SWITCH);
		m_stmtTags.put("DoStmt", StmtTag.DO);
		m_stmtTags.put("BreakStmt", StmtTag.BREAK);
		m_stmtTags.put("ContinueStmt", StmtTag.CONTINUE);
		m_stmtTags.put("ReturnStmt", StmtTag.RETURN);
		m_stmtTags.put("SyncStmt", StmtTag.SYNC);
		m_stmtTags.put("ThrowStmt", StmtTag.THROW);
		m_stmtTags.put("TryStmt", StmtTag.TRY);
		m_stmtTags.put("LblStmt", StmtTag.LABEL);
		m_stmtTags.put("IfThenStmt", StmtTag.IF);
		m_stmtTags.put("WhileStmt", StmtTag.WHILE);
		m_stmtTags.put("ForStmt", StmtTag.FOR);
		m_stmtTags.put("EnhancedForStmt", StmtTag.ENHANCED_FOR);
		m_stmtTags.put("EmptyStmt", StmtTag.EMPTY);
		
		m_exprTags = new Hashtable<String,ExprTag>();
		m_exprTags.put("INT_LITERAL", ExprTag.INT_LITERAL);
		m_exprTags.put("FLOAT_LITERAL", ExprTag.FLOAT_LITERAL);
		m_exprTags.put("CHAR_LITERAL", ExprTag.CHAR_LITERAL);		
		m_exprTags.put("STR_LITERAL", ExprTag.STR_LITERAL);
		m_exprTags.put("NULL", ExprTag.NULL);
		m_exprTags.put("TRUE", ExprTag.TRUE);
		m_exprTags.put("FALSE", ExprTag.FALSE);
		m_exprTags.put("THIS", ExprTag.THIS);
		m_exprTags.put("Super", ExprTag.SUPER);
		m_exprTags.put("Class", ExprTag.CLASS);
		m_exprTags.put("ParenExpr", ExprTag.PAREN);
		m_exprTags.put("InstCreatExpr", ExprTag.INST_CREAT);
		m_exprTags.put("FldAccess", ExprTag.FLD_ACCESS);
		m_exprTags.put("MethodInvoc", ExprTag.METHOD_INVOC);
		m_exprTags.put("ArrAccess", ExprTag.ARR_ACCESS);
		m_exprTags.put("ArrCreatExpr", ExprTag.ARR_CREAT);
		m_exprTags.put("PreIncrExpr", ExprTag.PRE_INCR);
		m_exprTags.put("PreDecrExpr", ExprTag.PRE_DECR);
		m_exprTags.put("UnaryPlusExpr", ExprTag.UNARY_PLUS);
		m_exprTags.put("UnaryMinusExpr", ExprTag.UNARY_MINUS);
		m_exprTags.put("InvExpr", ExprTag.INV);
		m_exprTags.put("NotExpr", ExprTag.NOT);
		m_exprTags.put("CastExpr", ExprTag.CAST);
		m_exprTags.put("QName", ExprTag.QNAME);
		m_exprTags.put("ID", ExprTag.ID);		
		m_exprTags.put("PostIncrExpr", ExprTag.POST_INCR);
		m_exprTags.put("PostDecrExpr", ExprTag.POST_DECR);
		m_exprTags.put("MultExpr", ExprTag.MULT);
		m_exprTags.put("DivExpr", ExprTag.DIV);
		m_exprTags.put("ModExpr", ExprTag.MOD);
		m_exprTags.put("PlusExpr", ExprTag.PLUS);
		m_exprTags.put("MinusExpr", ExprTag.MINUS);
		m_exprTags.put("LShiftExpr", ExprTag.LSHIFT);
		m_exprTags.put("RShiftExpr", ExprTag.RSHIFT);
		m_exprTags.put("UShiftExpr", ExprTag.USHIFT);
		m_exprTags.put("LessExpr", ExprTag.LESS);
		m_exprTags.put("GtExpr", ExprTag.GT);
		m_exprTags.put("LeExpr", ExprTag.LE);
		m_exprTags.put("GeExpr", ExprTag.GE);
		m_exprTags.put("InstOfExpr", ExprTag.INSTANCEOF);
		m_exprTags.put("EqExpr", ExprTag.EQ);
		m_exprTags.put("NeExpr", ExprTag.NE);
		m_exprTags.put("AndExpr", ExprTag.AND);
		m_exprTags.put("ExclOrExpr", ExprTag.EXCL_OR);
		m_exprTags.put("InclOrExpr", ExprTag.INCL_OR);
		m_exprTags.put("CondAndExpr", ExprTag.COND_AND);
		m_exprTags.put("CondOrExpr", ExprTag.COND_OR);
		m_exprTags.put("CondExpr", ExprTag.COND);
		m_exprTags.put("AssnExpr", ExprTag.ASSN);
		m_exprTags.put("MultAssnExpr", ExprTag.MULT_ASSN);
		m_exprTags.put("DivAssnExpr", ExprTag.DIV_ASSN);
		m_exprTags.put("ModAssnExpr", ExprTag.MOD_ASSN);
		m_exprTags.put("PlusAssnExpr", ExprTag.PLUS_ASSN);
		m_exprTags.put("MinusAssnExpr", ExprTag.MINUS_ASSN);
		m_exprTags.put("LShiftAssnExpr", ExprTag.LSHIFT_ASSN);
		m_exprTags.put("RShiftAssnExpr", ExprTag.RSHIFT_ASSN);
		m_exprTags.put("UShiftAssnExpr", ExprTag.USHIFT_ASSN);
		m_exprTags.put("AndAssnExpr", ExprTag.AND_ASSN);
		m_exprTags.put("OrAssnExpr", ExprTag.OR_ASSN);
		m_exprTags.put("XorAssnExpr", ExprTag.XOR_ASSN);

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
		
		m_binaryExpr = new Hashtable<ExprTag,BinaryExprKind>();
		m_binaryExpr.put(ExprTag.MOD, BinaryExprKind.REM);
		m_binaryExpr.put(ExprTag.DIV, BinaryExprKind.DIV);
		m_binaryExpr.put(ExprTag.MULT, BinaryExprKind.MULT);
		m_binaryExpr.put(ExprTag.MINUS, BinaryExprKind.MINUS);
		m_binaryExpr.put(ExprTag.PLUS, BinaryExprKind.PLUS);
		m_binaryExpr.put(ExprTag.USHIFT, BinaryExprKind.USHIFT);
		m_binaryExpr.put(ExprTag.LSHIFT, BinaryExprKind.LSHIFT);
		m_binaryExpr.put(ExprTag.RSHIFT, BinaryExprKind.RSHIFT);
		m_binaryExpr.put(ExprTag.GE, BinaryExprKind.GT_EQUAL);
		m_binaryExpr.put(ExprTag.LE, BinaryExprKind.LESS_EQUAL);
		m_binaryExpr.put(ExprTag.GT, BinaryExprKind.GREATER);
		m_binaryExpr.put(ExprTag.LESS, BinaryExprKind.LESS);		
		m_binaryExpr.put(ExprTag.NE, BinaryExprKind.NOT_EQUAL);
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
		m_binaryExpr.put(ExprTag.USHIFT_ASSN, BinaryExprKind.USHIFT_ASSIGNMENT);
		m_binaryExpr.put(ExprTag.ARR_ACCESS, BinaryExprKind.ARRAY_ACCESS);
				
		m_literExpr = new Hashtable<ExprTag, LiteralExprKind>();
		m_literExpr.put(ExprTag.INT_LITERAL, LiteralExprKind.INT);
		m_literExpr.put(ExprTag.FLOAT_LITERAL, LiteralExprKind.FLOAT);
		m_literExpr.put(ExprTag.CHAR_LITERAL, LiteralExprKind.CHAR);
		m_literExpr.put(ExprTag.STR_LITERAL, LiteralExprKind.STRING);
		m_literExpr.put(ExprTag.NULL, LiteralExprKind.NULL);
		m_literExpr.put(ExprTag.TRUE, LiteralExprKind.TRUE);
		m_literExpr.put(ExprTag.FALSE, LiteralExprKind.FALSE);		
	}		
	
	
	public void runProcess(XmlNode rootXml, CompilationUnit unit) throws Exception
	{
		parseImportDecl(rootXml, unit.m_directives);			
		
		TypeDeclaration packageDecl = parsePackageDecl(rootXml, unit);
		
		if (packageDecl != null)
			parseTypeDecl(rootXml, packageDecl.m_members, new MemberTypeDeclWrapperFactory());
		else
			parseTypeDecl(rootXml, unit.m_declarations, null);		
	}
	
	
	protected TypeDeclaration parsePackageDecl(XmlNode node, CompilationUnit unit) throws Exception
	{
		XmlNode nd = node.getNode("PackageDecl");

		if (nd != null)
		{
			List<XmlNode> list = nd.getNode("QName").getNodeList("ID");
			String name = "";
			
			for (XmlNode n : list)
			{
				if (name.length() > 0)
					name += ".";
				
				name += getTermValue(n);
			}

			TypeDeclaration decl = createAstNode(TypeDeclaration.class, TypeDeclKind.NAMESPACE.value());
			parseTextPos(nd, decl);
			
			decl.m_name = name;

			unit.m_declarations.add(decl);
						
			return decl;
		}
		else
			return null;
	}
	
	protected void parseQName(XmlNode node, List<Name> qname) throws Exception
	{
		XmlNode n = node.getNode("QName");
		
		if (n != null)
			parseQNameNode(n, qname);
	}
	
	
	protected void parseQNameNode(XmlNode node, List<Name> qname) throws Exception
	{
		List<XmlNode> list = node.getNodeList("ID");
		
		for (XmlNode n : list)
			qname.add(parseName(n));
	}

	protected Name parseName(XmlNode node) throws Exception
	{
		Name name = createAstNode(Name.class);
		parseTextPos(node, name);
		name.m_name = getTermValue(node);
		
		return name;
	}
		
	protected void parseImportDecl(XmlNode node, Set<Directive> dirs) throws Exception
	{
		UsingDirective dir;
		final String[] tag = {"SingleTypeImpDecl", "TypeImpOnDemandDecl", "SingleStaticImpDecl", "StaticImpOnDemandDecl"};
		final boolean[] staticFlag = {false, false, true, true};
		final boolean[] ondemandFlag = {false, true, false, true};

		for (int k = 0; k < tag.length; k++)
		{
			List<XmlNode> list = node.getNodeList(tag[k]);
			
			for (XmlNode n : list)
			{							
				dir = createAstNode(UsingDirective.class);
				dir.m_isStatic = staticFlag[k];
				dir.m_isOnDemand = ondemandFlag[k];
				parseTextPos(n, dir);
				parseQName(n, dir.m_name);
				if (staticFlag [k]&& !ondemandFlag[k])
				{
					dir.m_name.add(parseName(n.getNode("ID")));
				}
				dirs.add(dir);
			}
		}
	}
	
	
	protected void parseModifiers(XmlNode node, Set<Modifier> modifiers, Set<AttributeBlock> attributes) throws Exception
	{
		XmlNode mod = node.getNode("Modifiers");
		
		if (mod == null)
			return;
		
		List<XmlNode> list = mod.getAllChildren();

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
		
		if (attributes != null)
		{
			parseAttributes(mod, attributes);
		}		
	}	

	
	protected void parseMembers(XmlNode node, Set<AbstractMember> members) throws Exception
	{
		parseFuncMembers(node, members);
		parseDataMembers(node, members);
		parseAttrMethodMember(node, members);
		parseTypeDecl(node, members, new MemberTypeDeclWrapperFactory());
		parseEnumConst(node, members);
		parseStaticInit(node, members);
	}
	
	
	protected void parseFuncMembers(XmlNode node, Set<AbstractMember> members) throws Exception
	{
		final FuncMemberKind[] kind = {FuncMemberKind.METHOD, FuncMemberKind.CONSTRUCTOR, FuncMemberKind.ABSTRACT_METHOD};
		final String[] tag = {"MethodDecl", "ConstrDecl", "AbstrMethodDecl"};
		
		for (int k = 0; k < kind.length; k++)
		{
			List<XmlNode> list = node.getNodeList(tag[k]);
			
			for (XmlNode n : list)
			{
				FunctionalMember func = createAstNode(FunctionalMember.class, kind[k].value());
				
				parseTextPos(n, func);
				parseModifiers(n, func.m_modifiers, func.m_attributes);
				parseTypeParams(n, func.m_typeParams);
				
				if (kind[k] != FuncMemberKind.CONSTRUCTOR)
				{
					XmlNode resType = n.getNode("ResultType");
					
					if (resType.getFirstChild().getName().equals("VOID"))
					{
						SimpleType type = createAstNode(SimpleType.class, SimpleTypeKind.VOID.value());
						parseTextPos(resType.getFirstChild(), type);
						func.setType(type);
					}
					else
						func.setType(parseType(resType));
					
					func.getType().m_rank += n.getNodeList("Dim").size();					
				}
				
				func.m_funcName.add(parseName(n.getNode("ID")));
				func.m_parameters.addAll(parseFormalParams(n));
				
				XmlNode throwNode = n.getNode("Throws");
				if (throwNode != null)
				{
					List<XmlNode> lst = throwNode.getNodeList("ClsType");
					
					for (XmlNode nd : lst)
					{
						UserDefinedType type = createAstNode(UserDefinedType.class);
						
						parseTextPos(nd, type);
						parseQName(nd, type.m_name);
						parseTypeArgs(nd, type.m_arguments);

						func.m_throwList.add(type);
					}
				}
				
				switch (kind[k])
				{
					case METHOD:
						func.setBlock(parseBlockStatement(n));						
						break;
						
					case CONSTRUCTOR:
						{
							XmlNode body = n.getNode("ConstrBody");
							if (body != null)
							{
								BlockStatement blk = createAstNode(BlockStatement.class);
								XmlNode inode = body.getNode("ExplicitConstrInvoc");

								if (inode != null)
								{
									boolean compound;
									MethodInvocationExpression invoc = createAstNode(MethodInvocationExpression.class);
									parseTextPos(inode, invoc);
									String first = inode.getFirstChild().getName();
									
									if (first.equals("RefType") || first.equals("Super") || inode.getChildCount() == 1)
										compound = false;
									else
									{
										String next = inode.getFirstChild().getNextSibling().getName();
										if (next.equals("RefType") || next.equals("Super"))
											compound = true;
										else
											compound = false;		
									}
									
									if (compound)
									{
										Expression le = parseExprNode(inode.getFirstChild());
										Expression re = parseExprNode(inode.getNode("Super"));
										BinaryExpression be = createAstNode(BinaryExpression.class, BinaryExprKind.MEMBER_ACCESS.value());
										be.m_left = ((AstNode)le).m_left;
										be.m_right = ((AstNode)re).m_right;
										be.setLeftOperand(le);
										be.setRightOperand(re);
										invoc.setMethodRef(be);
									}
									else
									{
										XmlNode nd = inode.getNode("THIS");
										
										if (nd == null)											
											nd = inode.getNode("Super");
										
										invoc.setMethodRef(parseExprNode(nd));
									}
									
									parseNonWildTypeArgs(inode, invoc.m_typeArguments);
									invoc.m_arguments.addAll(parseExpressions(inode));
									
									func.m_constrInit.add(invoc);
								}
								
								blk.m_statements.addAll(parseStatements(body));
								func.setBlock(blk);
								parseTextPos(body, blk);
							}
						}						
				}
				
				members.add(func);
			}
		}
	}
	
	
	protected void parseDataMembers(XmlNode node, Set<AbstractMember> members) throws Exception
	{
		final String[] tag = {"FldDecl", "ConstDecl"};
		
		for (String t : tag)
		{
			List<XmlNode> list = node.getNodeList(t);

			for (XmlNode nd : list)
			{
				DataMember mbr;
				
				if (nd.getName().equals("FldDecl"))
					mbr = createAstNode(DataMember.class, DataMemberKind.FIELD.value());
				else
					mbr = createAstNode(DataMember.class, DataMemberKind.CONST.value());
				
				parseTextPos(nd, mbr);
				parseModifiers(nd, mbr.m_modifiers, mbr.m_attributes);

				Type type = parseType(nd);				
				mbr.setType(type);
				
				List<XmlNode> lst = nd.getNodeList("VarDclr");
				for (XmlNode n : lst)
				{
					Declarator d = createAstNode(Declarator.class);
					
					parseTextPos(n, d);
					d.m_name = getTermValue(n.getNode("ID"));
					type.m_rank += n.getNodeList("Dim").size();
					d.setInitializer(parseInits(n).get(0));
					
					mbr.m_declarators.add(d);
				}
				
				members.add(mbr);
			}
		}	
	}

	
	protected void parseAttrMethodMember(XmlNode node, Set<AbstractMember> members) throws Exception
	{
		List<XmlNode> list = node.getNodeList("AnnotTypeMethodDecl");
		
		for (XmlNode n : list)
		{
			AttributeMethodMember mb = createAstNode(AttributeMethodMember.class);

			parseTextPos(n, mb);				
			parseModifiers(n, mb.m_modifiers, mb.m_attributes);
			mb.setType(parseType(n));
			mb.m_name = getTermValue(n.getNode("ID"));
			mb.setDefaultValue(parseElemValues(n).get(0));
		}
	}
	
	
	protected <T extends Collection> void parseTypeDecl(XmlNode node, T decls, ITypeDeclWrapperFactory factory) throws Exception
	{
		final TypeDeclKind[] kind = {TypeDeclKind.CLASS, TypeDeclKind.INTERFACE, TypeDeclKind.ENUM, TypeDeclKind.ANNOT_TYPE};
		final String[] decltag = {"ClsDecl", "IntfDecl", "EnumDecl", "AnnotTypeDecl"};
		final String[] bodytag = {"ClsBody", "IntfBody", "EnumBody", "AnnotTypeBody"};
		
		for (int k = 0; k < kind.length; k++)
		{
			List<XmlNode> list = node.getNodeList(decltag[k]);
			
			for (XmlNode n : list)
			{
				TypeDeclaration decl = createAstNode(TypeDeclaration.class, kind[k].value());

				parseTextPos(n, decl);				
				parseModifiers(n, decl.m_modifiers, decl.m_attributes);
				decl.m_name = getTermValue(n.getNode("ID"));		
				parseTypeParams(n, decl.m_typeParams);
				parseBaseTypes(n, decl.m_baseTypes, decl.m_baseInterfaces);
				parseMembers(n.getNode(bodytag[k]), decl.m_members);
				
				if (factory == null)
					decls.add(decl);
				else
					decls.add(factory.wrapTypeDecl(decl));
			}
		}
	}
	
	
	protected void parseEnumConst(XmlNode node, Set<AbstractMember> members) throws Exception
	{
		List<XmlNode> list = node.getNodeList("EnumConst");
		
		for (XmlNode n : list)
		{
			EnumConstMember em = createAstNode(EnumConstMember.class);
			
			parseTextPos(n, em);				
			parseModifiers(n, null, em.m_attributes);
			em.m_name = getTermValue(n.getNode("ID"));
			em.m_arguments.addAll(parseExpressions(n));

			XmlNode b = n.getNode("ClsBody");
			if (b != null)
				parseMembers(b, em.m_members);
			
			members.add(em);
		}
	}
	
	
	protected void parseStaticInit(XmlNode node, Set<AbstractMember> members) throws Exception
	{
		String[] tag = {"InstInit", "StaticInit"};
		
		for (String t : tag)
		{
			List<XmlNode> list = node.getNodeList(t);
			
			for (XmlNode n : list)
			{
				InitBlockMember blk = createAstNode(InitBlockMember.class);
				
				parseTextPos(n, blk);
				blk.setBlock(parseBlockStatement(n));
				members.add(blk);
			}
		}
	}
	
	
	protected void parseTypeParams(XmlNode node, List<TypeParameter> typeParams) throws Exception
	{
		List<XmlNode> list = node.getNodeList("TypeParam");
		
		for (XmlNode n : list)
		{
			TypeParameter param = createAstNode(TypeParameter.class);
			
			param.m_name = getTermValue(n.getNode("ID"));
			parseTextPos(n, param);
			typeParams.add(param);
		}
	}

	
	protected void parseBaseTypes(XmlNode node, Set<Type> baseTypes, 
		Set<UserDefinedType> baseInterfaces) throws Exception
	{
		String[] tag = {"ClsType", "IntfType"};
		
		for (String t : tag)
		{
			List<XmlNode> list = node.getNodeList(t);
			
			for (XmlNode n : list)
			{
				UserDefinedType type = createAstNode(UserDefinedType.class);
				
				parseTextPos(n, type);
				parseQName(n, type.m_name);
				parseTypeArgs(n, type.m_arguments);
				
				if (t.equals("ClsType"))
					baseTypes.add(type);
				else
					baseInterfaces.add(type);
			}
		}
	}
	
	
	protected UserDefinedType parseClsIntfType(XmlNode node) throws Exception
	{
		final String[] tag = {"ClsType", "IntfType", "ClsIntfType"};
		XmlNode n = null;
		
		for (String t : tag)
		{
			n = node.getNode(t);
			
			if (n != null)
				break;
		}

		UserDefinedType type = createAstNode(UserDefinedType.class);
		
		parseTextPos(n, type);
		parseQName(n, type.m_name);
		parseTypeArgs(n, type.m_arguments);
		
		return type;
	}
	
	
	protected void parseTypeArgs(XmlNode node, List<TypeArgument> args) throws Exception
	{
		List<XmlNode> list = node.getNodeList("ActualTypeArg");
		
		for (XmlNode n : list)
		{
			TypeArgKind kind = m_typeArgs.get(n.getFirstChild().getName());
			TypeArgument a = createAstNode(TypeArgument.class, kind.value());
			
			parseTextPos(n, a);
			if (kind != TypeArgKind.WILDCARD)
				a.setRefType(parseRefTypes(n).get(0));
			args.add(a);
		}
	}
	
	
	protected Type parseType(XmlNode node) throws Exception
	{
		XmlNode n = node.getNode("Type");
		String tag = n.getFirstChild().getName();
		
		if (tag.equals("RefType"))
			return parseRefTypes(n).get(0);
		else
			return parsePrimitiveType(n);
	}
	
	
	protected List<Type> parseRefTypes(XmlNode node) throws Exception
	{
		List<XmlNode> list = node.getNodeList("RefType");
		List<Type> output = new TolerantList<Type>();
		
		for (XmlNode n : list)
		{
			List<XmlNode> dims = n.getNodeList("Dim");
			String tag = n.getFirstChild().getName();
			
			if (tag.equals("ClsIntfType"))
			{
				UserDefinedType type = parseClsIntfType(n);
				type.m_rank = dims.size();
				output.add(type);
			}
			else
			{
				SimpleType type = parsePrimitiveType(n);
				type.m_rank = dims.size();
				output.add(type);
			}
		}
		
		return output;
	}
	
	
	protected SimpleType parsePrimitiveType(XmlNode node) throws Exception
	{
		SimpleType type = null;
		XmlNode n = node.getNode("PrimitiveType");

		if (n != null)
		{
			type = createAstNode(SimpleType.class, m_simpleTypes.get(n.getFirstChild().getName()).value());
			
			parseTextPos(n, type);
		}
		
		return type;
	}

	
	protected List<Parameter> parseFormalParams(XmlNode node) throws Exception
	{
		String[] tag = {"FormalParam", "VarArityParam"};
		List<Parameter> output = new TolerantList<Parameter>();
		
		for (String t : tag)
		{
			List<XmlNode> lst = node.getNodeList(t);
			
			for (XmlNode n : lst)
			{
				Parameter par = createAstNode(Parameter.class, ParamKind.VALUE.value());
				parseTextPos(n, par);
				
				parseModifiers(n, par.m_modifiers, par.m_attributes);
				
				Type type = parseType(n);
				type.m_rank += node.getNodeList("Dim").size();
				par.setType(type);
				
				par.m_name = getTermValue(n.getNode("ID"));
				
				if (n.getName().equals("VarArityParam"))
					par.m_varParam = true;
				
				output.add(par);
			}
		}
		
		return output;
	}
	

	protected void parseAttributes(XmlNode node, Set<AttributeBlock> attributes) throws Exception
	{
		String[] tag = {"NormalAnnot", "MarkerAnnot", "SingleElemAnnot"};
		
		for (String t : tag)
		{
			List<XmlNode> list = node.getNodeList(t);
			
			for (XmlNode n : list)
			{
				attributes.add(parseAttrBlock(n));
			}
		}		
	}
	
	
	protected AttributeBlock parseAttrBlock(XmlNode node) throws Exception
	{
		AttributeBlock block = createAstNode(AttributeBlock.class);
		parseTextPos(node, block);
		
		AttributeItem item = createAstNode(AttributeItem.class);
		item.m_left = block.m_left;
		item.m_right = block.m_right;
		block.m_items.add(item);
		
		UserDefinedType type = createAstNode(UserDefinedType.class);
		parseTextPos(node, type);
		item.setType(type);
		parseQName(node, item.getType().m_name);
		
		List<XmlNode> lst = node.getNodeList("ElemValPair");
		
		for (XmlNode n : lst)
		{
			NamedAttributeArgument arg = createAstNode(NamedAttributeArgument.class);
			parseTextPos(n, arg);
			item.m_arguments.add(arg);
			arg.m_name = getTermValue(n.getNode("ID"));
			arg.setValue(parseElemValues(n).get(0));
		}
		
		XmlNode v = node.getNode("ElemVal");
		
		if (v != null)
		{
			AttributeArgument arg = createAstNode(AttributeArgument.class);
			parseTextPos(v, arg);
			item.m_arguments.add(arg);
			arg.setValue(parseElemValues(node).get(0));				
		}
		
		return block;
	}

	
	protected List<ElementValue> parseElemValues(XmlNode node) throws Exception
	{
		List<ElementValue> output = new TolerantList<ElementValue>();
		List<XmlNode> list = node.getNodeList("ElemVal");

		for (XmlNode n : list)
		{
			XmlNode child = n.getFirstChild();
			String childName = child.getName();
			
			if (childName.equals("NormalAnnot") || childName.equals("MarkerAnnot") || childName.equals("SingleElemAnnot"))
			{
				AttributeElementValue value = createAstNode(AttributeElementValue.class);
				value.setBlock(parseAttrBlock(child));
				parseTextPos(n, value);
				output.add(value);
			}
			else if (childName.equals("ElemValArrInit"))
			{			
				ArrayElementValue value = createAstNode(ArrayElementValue.class);
				ElementValueArrayInitializer init = createAstNode(ElementValueArrayInitializer.class);
				value.setInitializer(init);
				value.getInitializer().m_elementValues.addAll(parseElemValues(child));
				parseTextPos(n, value);
				output.add(value);
			}
			else 
			{
				ExpressionElementValue value = createAstNode(ExpressionElementValue.class);
				value.setExpression(parseExprNode(child));
				parseTextPos(n, value);
				output.add(value);				
			}
		}
		
		return output;
	}
	
	
	protected LocalVariableDeclaration parseVarDecl(XmlNode node) throws Exception
	{
		XmlNode nd = node.getNode("LocalVarDecl");
		
		LocalVariableDeclaration var = createAstNode(LocalVariableDeclaration.class);
	
		parseTextPos(nd, var);
		parseModifiers(nd, var.m_modifiers, null);
		Type type = parseType(nd);
		var.setType(type);		
		
		List<XmlNode> lst = nd.getNodeList("VarDclr");
		for (XmlNode n : lst)
		{
			Declarator d = createAstNode(Declarator.class);
			
			parseTextPos(n, d);
			d.m_name = getTermValue(n.getNode("ID"));	
			type.m_rank += n.getNodeList("Dim").size();
			d.setInitializer(parseInits(n).get(0));
			
			var.m_declarators.add(d);
		}

		return var;
	}

	
	protected BlockStatement parseBlockStatement(XmlNode node) throws Exception
	{
		XmlNode blk = node.getNode("Blk");
		
		if (blk != null)
		{
			BlockStatement stmt = createAstNode(BlockStatement.class);
			
			stmt.m_statements.addAll(parseStatements(blk));
			parseTextPos(blk, stmt);
			return stmt;
		}
		else
			return null;
	}
	
	
	protected List<AbstractStatement> parseStatements(XmlNode node) throws Exception
	{
		List<XmlNode> list = node.getNodeList("BlkStmt");
		List<AbstractStatement> output = new TolerantList<AbstractStatement>();
		
		for (XmlNode n : list)
		{
			switch (m_blockTags.get(n.getFirstChild().getName()))
			{
				case VAR_DECL:
					{
						LocalVarDeclStatement vs = createAstNode(LocalVarDeclStatement.class);
						
						vs.setDeclaration(parseVarDecl(n));
						parseTextPos(n, vs);
						output.add(vs);
					}
					break;
					
				case TYPE_DECL:
					parseTypeDecl(n, output, new StmtTypeDeclWrapperFactory());
					break;
					
				case STATEMENT:
					output.addAll(parseEmbedStatements(n));
					break;
			}
		}
		
		return output;
	}

	
	protected List<EmbeddedStatement> parseEmbedStatements(XmlNode node) throws Exception
	{
		List<XmlNode> list = node.getNodeList("Stmt");
		List<EmbeddedStatement> output = new TolerantList<EmbeddedStatement>();
		
		for (XmlNode nd : list)
		{
			XmlNode n = nd.getFirstChild();
			StmtTag tag = m_stmtTags.get(n.getName()); 
			switch (tag)
			{
				case BLOCK:
					{
						BlockStatement bs = parseBlockStatement(nd);
						if (bs != null)
							output.add(bs);
					}
					break;
					
				case ASSERT:
					{
						AssertStatement as = createAstNode(AssertStatement.class);
						List<Expression> lst = parseExpressions(n);
						as.setEvalExpr(lst.get(0));
						if (lst.size() > 1)
							as.setMsgDetailExpr(lst.get(1));
						parseTextPos(n, as);
						output.add(as);
					}
					break;
					
				case EXPR:
					{
						ExpressionStatement es = createAstNode(ExpressionStatement.class);
						es.setExpression(parseExprNode(n.getFirstChild()));
						parseTextPos(n, es);
						output.add(es);
					}
					break;
					
				case SWITCH:
					{
						SwitchStatement ss = createAstNode(SwitchStatement.class);
						ss.setSwitchExpr(parseExpressions(n).get(0));
						
						List<XmlNode> lst = n.getNodeList("SwitchBlkStmtGr");
						for (XmlNode gr : lst)
						{
							SwitchSection sec = createAstNode(SwitchSection.class);
							
							List<XmlNode> lbls = gr.getNodeList("SwitchLbl");
							for (XmlNode lb : lbls)
							{
								SwitchLabel sl;
								
								if (lb.getChildCount() == 0)
									sl = createAstNode(SwitchLabel.class, SwitchLabelKind.DEFAULT.value());
								else
								{
									sl = createAstNode(SwitchLabel.class, SwitchLabelKind.CASE.value());
									sl.setLabelExpr(parseExpressions(lb.getFirstChild()).get(0));
								}
								
								parseTextPos(lb, sl);
								sec.m_labels.add(sl);
							}
							
							sec.m_statements.addAll(parseStatements(gr));							
							
							parseTextPos(gr, sec);
							ss.m_sections.add(sec);
						}
						
						parseTextPos(n, ss);
						output.add(ss);
					}
					break;
					
				case DO:
				case WHILE:					
					{
						DoWhileStatement ds;
						
						if (tag == StmtTag.DO)
							ds = createAstNode(DoWhileStatement.class, DoWhileStmtKind.DO.value());
						else
							ds = createAstNode(DoWhileStatement.class, DoWhileStmtKind.WHILE.value());
						
						ds.setCondition(parseExpressions(n).get(0));
						ds.setBody(parseEmbedStatements(n).get(0));
						parseTextPos(n, ds);
						output.add(ds);
					}
					break;
					
				case BREAK:
				case CONTINUE:
				case RETURN:
				case THROW:
					{
						JumpStatement js;
						
						switch (tag)
						{
							case BREAK:
								js = createAstNode(JumpStatement.class, JumpStmtKind.BREAK.value());
								if (n.getChildCount() > 0)
									js.m_label = getTermValue(n.getNode("ID"));
								break;
								
							case CONTINUE:
								js = createAstNode(JumpStatement.class, JumpStmtKind.CONTINUE.value());
								if (n.getChildCount() > 0)								
									js.m_label = getTermValue(n.getNode("ID"));
								break;
								
							case RETURN:
								js = createAstNode(JumpStatement.class ,JumpStmtKind.RETURN.value());
								js.setJumpExpr(parseExpressions(n).get(0));
								break;
								
							default:
								js = createAstNode(JumpStatement.class, JumpStmtKind.THROW.value());
								js.setJumpExpr(parseExpressions(n).get(0));
						}
					
						parseTextPos(n, js);
						output.add(js);
					}
					break;
									
				case SYNC:
					{
						SyncStatement sync = createAstNode(SyncStatement.class);
						sync.setLockExpr(parseExpressions(n).get(0));
						sync.setStatement(parseBlockStatement(n));
						parseTextPos(n, sync);
						output.add(sync);
					}
					break;
										
				case TRY:
					{
						TryStatement ts = createAstNode(TryStatement.class);
						ts.setTryBlock(parseBlockStatement(n));
						List<XmlNode> lst = n.getNodeList("Catch");
						for (XmlNode c : lst)
						{
							CatchBlock cb = createAstNode(CatchBlock.class);

							cb.setCatchBlock(parseBlockStatement(c));
							cb.setParameter(parseFormalParams(c).get(0));
							
							parseTextPos(c, cb);
							ts.m_catchSet.add(cb);
						}
						
						XmlNode f = n.getNode("Finally");
						if (f != null)
						{
							ts.setFinallyBlock(parseBlockStatement(f));
						}
						
						parseTextPos(n, ts);
						output.add(ts);
					}
					break;
					
				case LABEL:
					{
						LabelStatement ls = createAstNode(LabelStatement.class);
						ls.m_label = getTermValue(n.getNode("ID"));
						ls.setStatement(parseEmbedStatements(n).get(0));
						parseTextPos(n, ls);
						output.add(ls);
					}
					break;
					
				case IF:
					{
						IfStatement is = createAstNode(IfStatement.class);
						is.setCondition(parseExpressions(n).get(0));
						List<EmbeddedStatement> stmts = parseEmbedStatements(n);
						is.setIfStmt(stmts.get(0));
						if (stmts.size() > 1)
							is.setElseStmt(stmts.get(1));
						parseTextPos(n, is);
						output.add(is);
					}
					break;
									
				case FOR:
					{
						ForStatement fs = createAstNode(ForStatement.class);
						
						XmlNode d = n.getNode("ForInit");
						if (d != null)
						{
							if (d.getFirstChild().getName().equals("LocalVarDecl"))
								fs.setInitVarDecl(parseVarDecl(d));
							else
								fs.m_initExprList.addAll(parseExpressions(d));
						}
						
						fs.setCondition(parseExpressions(n).get(0));
						
						d = n.getNode("ForUpdate");
						if (d != null)
						{
							fs.m_iteration.addAll(parseExpressions(d));
						}
						
						fs.setForStmt(parseEmbedStatements(n).get(0));
						parseTextPos(n, fs);
						output.add(fs);
					}
					break;
					
				case ENHANCED_FOR:
					{
						ForEachStatement fs = createAstNode(ForEachStatement.class);
						parseModifiers(n, fs.m_modifiers, fs.m_attributes);
						fs.setType(parseType(n));
						fs.m_identifier = getTermValue(n.getNode("ID"));
						fs.setInitExpr(parseExpressions(n).get(0));
						fs.setForEachStmt(parseEmbedStatements(n).get(0));
						parseTextPos(n, fs);
						output.add(fs);
					}
					break;
					
				case EMPTY:
			}
		}
		
		return output;
	}
	
	
	protected List<Expression> parseExpressions(XmlNode node) throws Exception
	{
		List<Expression> output = new TolerantList<Expression>();
		String tag[] = {"Expr", "ExprStmt"};
		
		for (String t : tag)
		{
			List<XmlNode> list = node.getNodeList(t);
			
			for (XmlNode n : list)
			{
				output.add(parseExprNode(n.getFirstChild()));
			}
		}
		
		return output;
	}
	
	
	protected Expression parseExprNode(XmlNode node) throws Exception
	{
		Expression expr = null;
		ExprTag tag = m_exprTags.get(node.getName());
				
		switch (tag)
		{
			case ARR_ACCESS:
				{
					BinaryExpression e = createAstNode(BinaryExpression.class, m_binaryExpr.get(tag).value());
					e.setLeftOperand(parseExprNode(node.getFirstChild()));
					e.setRightOperand(parseExpressions(node).get(0));
					expr = e;
				}
				break;
				
			case COND:
				{
					ConditionExpression e = createAstNode(ConditionExpression.class);
					e.setCondition(parseExprNode(node.getFirstChild()));
					e.setTrueExpr(parseExpressions(node).get(0));
					e.setFalseExpr(parseExprNode(node.getLastChild()));
					expr = e;					
				}
				break;
				
			case MULT:
			case DIV:
			case MOD:
			case PLUS:
			case MINUS:
			case LSHIFT:
			case RSHIFT:
			case USHIFT:
			case LESS:
			case GT:
			case LE:
			case GE:
			case EQ:
			case NE:
			case AND:
			case EXCL_OR:
			case INCL_OR:
			case COND_AND:
			case COND_OR:
			case ASSN:
			case MULT_ASSN:
			case DIV_ASSN:
			case MOD_ASSN:
			case PLUS_ASSN:
			case MINUS_ASSN:
			case LSHIFT_ASSN:
			case RSHIFT_ASSN:
			case USHIFT_ASSN:
			case AND_ASSN:
			case OR_ASSN:
			case XOR_ASSN:
				{
					BinaryExpression e = createAstNode(BinaryExpression.class, m_binaryExpr.get(tag).value());
					e.setLeftOperand(parseExprNode(node.getFirstChild()));
					e.setRightOperand(parseExprNode(node.getLastChild()));
					expr = e;
				}
				break;
				
			case POST_INCR:
			case POST_DECR:
			case PRE_INCR:
			case PRE_DECR:
			case UNARY_PLUS:
			case UNARY_MINUS:
			case INV:
			case NOT:
				{
					UnaryExpression e = createAstNode(UnaryExpression.class, m_unaryExpr.get(tag).value());
					e.setOperand(parseExprNode(node.getFirstChild()));
					expr = e;
				}
				break;
				
			case PAREN:
				{
					UnaryExpression e = createAstNode(UnaryExpression.class, m_unaryExpr.get(tag).value());
					e.setOperand(parseExpressions(node).get(0));
					expr = e;
				}
				break;
				
			case INSTANCEOF:
				{
					UnaryTypeExpression e = createAstNode(UnaryTypeExpression.class, UnaryTypeExprKind.IS.value());
					e.setOperand(parseExprNode(node.getFirstChild()));
					e.setType(parseRefTypes(node).get(0));
					expr = e;
				}
				break;
				
			case QNAME:
				{
					IdentifierExpression e = createAstNode(IdentifierExpression.class);
					parseQNameNode(node, e.m_name);
					expr = e;
				}
				break;
				
			case ID:
				{
					IdentifierExpression e = createAstNode(IdentifierExpression.class);
					e.m_name.add(parseName(node));
					expr = e;
				}
				break;
								
			case INT_LITERAL:
			case FLOAT_LITERAL:
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
				
			case THIS:
				expr = createAstNode(SelfReferenceExpression.class, SelfRefExprKind.THIS.value());
				break;
				
			case SUPER:
				expr = createAstNode(SelfReferenceExpression.class, SelfRefExprKind.BASE.value());
				break;

			case CLASS:
				expr = createAstNode(SelfReferenceExpression.class, SelfRefExprKind.CLASS.value());
				break;
								
			case INST_CREAT:
				{
					InstanceCreationExpression ic = createAstNode(InstanceCreationExpression.class);
					XmlNode id = node.getNode("ID");
					
					if (id == null)
					{	
						UserDefinedType type = parseClsIntfType(node);
						parseTypeArgs(node, type.m_arguments);
						ic.setType(type);
						expr = ic;
					}
					else
					{						
						Expression e = parseExprNode(node.getFirstChild());						
						BinaryExpression be = createAstNode(BinaryExpression.class, BinaryExprKind.MEMBER_ACCESS.value());
						be.setLeftOperand(e);
						be.setRightOperand(ic);
						
						UserDefinedType type = createAstNode(UserDefinedType.class);
						type.m_name.add(parseName(id));
						parseTextPos(id, type);
						parseTypeArgs(node, type.m_arguments);
						ic.setType(type);
						expr = be;
					}
					
					ic.m_arguments.addAll(parseExpressions(node));
					
					XmlNode body = node.getNode("ClsBody");
					
					if (body != null)
						parseMembers(body, ic.m_members);
				}
				break;
				
			case METHOD_INVOC:
				{
					MethodInvocationExpression mi = createAstNode(MethodInvocationExpression.class);
					IdentifierExpression ie = createAstNode(IdentifierExpression.class);
					XmlNode id = node.getNode("ID");
					
					if (id == null)
					{
						parseTextPos(node, ie);
						parseQName(node, ie.m_name);
						mi.setMethodRef(ie);
					}
					else
					{					
						parseTextPos(id, ie);
						ie.m_name.add(parseName(id));
						Expression e = parseExprNode(node.getFirstChild());
						BinaryExpression b = createAstNode(BinaryExpression.class, BinaryExprKind.MEMBER_ACCESS.value());
						XmlNode sp = node.getNode("Super");
						if (sp == null || sp == node.getFirstChild())
						{
							b.setLeftOperand(e);
							b.setRightOperand(ie);
							b.m_left = ((AstNode)e).m_left;
							b.m_right = ((AstNode)ie).m_right;
							mi.setMethodRef(b);
						}
						else
						{
							SelfReferenceExpression s = createAstNode(SelfReferenceExpression.class, SelfRefExprKind.BASE.value());
							parseTextPos(sp, s);
							BinaryExpression b2 = createAstNode(BinaryExpression.class, BinaryExprKind.MEMBER_ACCESS.value());
							b2.m_left = ((AstNode)e).m_left;
							b2.m_right = ((AstNode)ie).m_right;
							b.setLeftOperand(e);
							b.setRightOperand(s);
							b2.setLeftOperand(b);
							b2.setRightOperand(ie);
							mi.setMethodRef(b2);
						}
					}
					
					parseNonWildTypeArgs(node, mi.m_typeArguments);
					mi.m_arguments.addAll(parseExpressions(node));
					
					expr = mi;
				}
				break;
				
			case FLD_ACCESS:
				{
					
					if (node.getChildCount() > 2)
					{
						BinaryExpression be = createAstNode(BinaryExpression.class, BinaryExprKind.MEMBER_ACCESS.value());
						be.setRightOperand(parseExprNode(node.getLastChild()));						
						BinaryExpression b2 = createAstNode(BinaryExpression.class, BinaryExprKind.MEMBER_ACCESS.value());
						Expression le = parseExprNode(node.getFirstChild());
						Expression re = parseExprNode(node.getFirstChild().getNextSibling());
						b2.m_left = ((AstNode)le).m_left;
						b2.m_right = ((AstNode)re).m_right;
						b2.setLeftOperand(le);
						b2.setRightOperand(re);
						be.setLeftOperand(b2);
						expr = be;
					}
					else
					{
						String first = node.getFirstChild().getName();
						
						if (first.equals("Type") || first.equals("ClsType") || first.equals("VOID"))
						{
							UnaryTypeExpression te = createAstNode(UnaryTypeExpression.class, UnaryTypeExprKind.TYPE_MEMBER_ACCESS.value());
							
							if (first.equals("Type"))
								te.setType(parseType(node));
							else if (first.equals("ClsType"))
								te.setType(parseClsIntfType(node));
							else
							{
								SimpleType type = createAstNode(SimpleType.class, SimpleTypeKind.VOID.value());
								parseTextPos(node.getFirstChild(), type);
								te.setType(type);								
							}
							
							te.setOperand(parseExprNode(node.getLastChild()));
							expr = te;							
						}
						else
						{
							BinaryExpression be = createAstNode(BinaryExpression.class, BinaryExprKind.MEMBER_ACCESS.value());
							be.setLeftOperand(parseExprNode(node.getFirstChild()));
							be.setRightOperand(parseExprNode(node.getLastChild()));
							expr = be;
						}
					}
				}
				break;
				
			case ARR_CREAT:
				{
					ArrayCreationExpression ac = createAstNode(ArrayCreationExpression.class);
					
					if (node.getFirstChild().getName().equals("PrimitiveType"))
					{
						ac.setType(parsePrimitiveType(node));
					}
					else
					{
						UserDefinedType type = createAstNode(UserDefinedType.class);
						parseTextPos(node.getFirstChild(), type);
						parseQName(node, type.m_name);
						parseTypeArgs(node, type.m_arguments);
						ac.setType(type);
					}

					List<Expression> elist = null;
					elist = parseExpressions(node);
					ac.m_dimExpr.addAll(elist);
					ac.m_rank = elist.size() + node.getNodeList("Dim").size();
					
					XmlNode ai = node.getNode("ArrInit");
					if (ai != null)
					{
						ArrayInitializer init = createAstNode(ArrayInitializer.class);
						parseTextPos(ai, init);
						init.m_arrayInit.addAll(parseInits(ai));
						ac.setInitializer(init);
					}

					expr = ac;
				}
				break;
				
			case CAST:
				{
					UnaryTypeExpression ce = createAstNode(UnaryTypeExpression.class, UnaryTypeExprKind.CAST.value());
					XmlNode first = node.getFirstChild();
					
					if (first.getName().equals("PrimitiveType"))
						ce.setType(parsePrimitiveType(node));
					else if (first.getName().equals("ClsIntfType"))
						ce.setType(parseClsIntfType(node));
					else
					{
						UserDefinedType type = createAstNode(UserDefinedType.class);
						parseTextPos(node.getNode("QName"), type);
						parseQName(node, type.m_name);
						parseTypeArgs(node, type.m_arguments);
						ce.setType(type);
					}
					
					ce.getType().m_rank = node.getNodeList("Dim").size();
					ce.setOperand(parseExprNode(node.getLastChild()));
					
					expr = ce;
				}
		}

		if (expr != null)
		{
			parseTextPos(node, (AstNode)expr);
		}

		return expr;
	}
	
	
	protected void parseNonWildTypeArgs(XmlNode node, List<TypeArgument> args) throws Exception
	{
		List<Type> types = parseRefTypes(node);
		
		for (Type t : types)
		{
			TypeArgument a = createAstNode(TypeArgument.class, TypeArgKind.EXACT.value());
			a.m_left = t.m_left;
			a.m_right = t.m_right;
			a.setRefType(t);
			args.add(a);
		}
	}

	
	protected List<Initializer> parseInits(XmlNode node) throws Exception
	{
		List<XmlNode> list = node.getNodeList("VarInit");
		List<Initializer> output = new TolerantList<Initializer>();
		
		for (XmlNode n : list)
		{
			if (n.getFirstChild().getName().equals("Expr"))
			{
				ExpressionInitializer init = createAstNode(ExpressionInitializer.class);
				parseTextPos(n, init);
				init.setExpression(parseExpressions(n).get(0));
				output.add(init);
			}
			else
			{
				ArrayInitializer init = createAstNode(ArrayInitializer.class);
				parseTextPos(n, init);
				init.m_arrayInit.addAll(parseInits(n));
				output.add(init);
			}
		}
		
		return output;
	}

}
