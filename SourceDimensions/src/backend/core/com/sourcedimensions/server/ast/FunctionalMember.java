package com.sourcedimensions.server.ast;

import java.util.*;

public class FunctionalMember extends Member 
{
	public FunctionalMember() { }
	
	public FunctionalMember(FuncMemberKind kind)
	{
		m_kind = kind.value;
	}

	protected int m_kind;
	public List<Name> m_funcName = new AstArrayList<Name>(this, 2);
	public List<Parameter> m_parameters = new AstArrayList<Parameter>(this, 3);
	public List<TypeParameter> m_typeParams = new AstArrayList<TypeParameter>(this, 4);
	public Set<Constraint> m_constraints = new AstHashSet<Constraint>(this, 5);	
	public Set<Type> m_throwList = new AstHashSet<Type>(this, 6);
	public List<MethodInvocationExpression> m_constrInit = new AstArrayList<MethodInvocationExpression>(this, 7);
	protected BlockStatement m_block;
	
	public FuncMemberKind getKind()
	{
		return FuncMemberKind.values()[m_kind];
	}
	
	public enum FuncMemberKind
	{
		CONSTRUCTOR(0),
		DESTRUCTOR(1), 
		METHOD(2),
		ABSTRACT_METHOD(3),
		UPLUS_OPERATOR(4),
		UMINUS_OPERATOR(5),
		NOT_OPERATOR(6),
		INV_OPERATOR(7),
		INC_OPERATOR(8),
		DEC_OPERATOR(9),
		TRUE_OPERATOR(10),
		FALSE_OPERATOR(11),
		PLUS_OPERATOR(12),
		MINUS_OPERATOR(13),
		MULT_OPERATOR(14),
		DIV_OPERATOR(15),
		REM_OPERATOR(16),
		AND_OPERATOR(17),
		OR_OPERATOR(18),
		XOR_OPERATOR(19),
		LSHIFT_OPERATOR(20),
		RSHIFT_OPERATOR(21),
		EQUAL_OPERATOR(22),
		NOT_EQ_OPERATOR(23),
		LESS_OPERATOR(24),
		GT_OPERATOR(25),
		LESS_EQ_OPERATOR(26),
		GT_EQ_OPERATOR(27),
		IMP_CONV_OPERATOR(28),
		EXP_CONV_OPERATOR(29);
		
		FuncMemberKind(int val)
		{
			value = val;
		}
		
		private final int value;
		
		public int value()
		{
			return value;
		}
	}
	
	public BlockStatement getBlock()
	{
		return m_block;
	}
	
	public void setBlock(BlockStatement block)
	{
		m_block = block;
		addChild(block);
	}
	
	public void setKind(int kind) 
	{
		m_kind = kind;
	}
	
	public String toString()
	{
		return toString(getKind().toString());
	}
}
