#include "javalexer5.h"
#include "java5sym.h"
#include "../common/exceptions.h"



static const int token_map[] =
{
	TK_J5_ABSTRACT,
	TK_J5_ASSERT,
	TK_J5_BOOLEAN,
	TK_J5_BREAK,
	TK_J5_BYTE,
	TK_J5_CASE,
	TK_J5_CATCH,
	TK_J5_CHAR,
	TK_J5_CLASS,
	TK_J5_CONTINUE,
	TK_J5_DEFAULT,
	TK_J5_DO,
	TK_J5_DOUBLE,
	TK_J5_ELSE,
	TK_J5_EXTENDS,
	TK_J5_FALSE,
	TK_J5_FINAL,
	TK_J5_FINALLY,
	TK_J5_FLOAT,
	TK_J5_FOR,
	TK_J5_IF,
	TK_J5_IMPLEMENTS,
	TK_J5_IMPORT,
	TK_J5_INSTANCEOF,
	TK_J5_INT,
	TK_J5_INTERFACE,
	TK_J5_LONG,
	TK_J5_NATIVE,
	TK_J5_NEW,
	TK_J5_NULL,
	TK_J5_PACKAGE,
	TK_J5_PRIVATE,
	TK_J5_PROTECTED,
	TK_J5_PUBLIC,
	TK_J5_RETURN,
	TK_J5_SHORT,
	TK_J5_STATIC,
	TK_J5_STRICTFP,
	TK_J5_SUPER,
	TK_J5_SWITCH,
	TK_J5_SYNCHRONIZED,
	TK_J5_THIS,
	TK_J5_THROW,
	TK_J5_THROWS,
	TK_J5_TRANSIENT,
	TK_J5_TRUE,
	TK_J5_TRY,
	TK_J5_VOID,
	TK_J5_VOLATILE,
	TK_J5_WHILE,
	TK_J5_ID,
	TK_J5_INT_LITERAL,
	TK_J5_FLOAT_LITERAL,
	TK_J5_CHAR_LITERAL,
	TK_J5_STR_LITERAL,
	TK_J5_LPAREN,			
	TK_J5_RPAREN,			
	TK_J5_COMMA,				
	TK_J5_LBRACK,			
	TK_J5_RBRACK,			
	TK_J5_LBRACE,			
	TK_J5_RBRACE,			
	TK_J5_EQ,			 	
	TK_J5_SEMIC,				
	TK_J5_COLON,				
	TK_J5_QUESTION,			
	TK_J5_DOT,				
	TK_J5_PLUS,				
	TK_J5_MINUS,				
	TK_J5_STAR,				
	TK_J5_SLASH,				
	TK_J5_PERCENT,
	TK_J5_HAT,				
	TK_J5_AND,				
	TK_J5_OR,			 	
	TK_J5_TILDE,				
	TK_J5_NOT,				
	TK_J5_PLUS_EQ,			
	TK_J5_MINUS_EQ,			
	TK_J5_STAR_EQ,			
	TK_J5_SLASH_EQ,			
	TK_J5_PERCENT_EQ,
	TK_J5_HAT_EQ,			
	TK_J5_AND_EQ,			
	TK_J5_OR_EQ,				
	TK_J5_LTLT,				
	0,				
	0,			
	0,			
	TK_J5_LTLT_EQ,			
	0,			
	TK_J5_EQEQ,				
	TK_J5_NE,			 	
	TK_J5_LE,			 	
	0,			 	
	TK_J5_LANGLE,			
	TK_J5_RANGLE,			
	TK_J5_ANDAND,			
	TK_J5_OROR,				
	TK_J5_INCR,				
	TK_J5_DECR,
	TK_J5_END_OF_FILE,
	TK_J5_WHITESPACE,
	TK_J5_ELLIPSIS,
	TK_J5_AT,
	TK_J5_ENUM
};


static int index_map[sizeof(token_map)/sizeof(token_map[0])];


void CJavaLexer5::InitLexer()
{
	m_nSlashCount = 0;

	if (!m_bIndexMapInit)
	{	
		for (int i = 0; i < sizeof(token_map)/sizeof(token_map[0]); i++)
			index_map[GetTokenCode(i)] = i;				

		m_bIndexMapInit = true;
	}
}


int CJavaLexer5::GetTokenCode(int index)
{
	return token_map[index];
}


int CJavaLexer5::GetTokenIndex(int token)
{
	return index_map[token];
}


bool CJavaLexer5::PreProcess()
{
	if (rangle)
	{
		long pos = mContext.cur;

		rangle = false;
		SkipWhiteSpaces();
		if (mContext.cur > pos)
		{
			mContext.token = GetTokenCode(IDX_J_WHITESPACE);	
			return true;
		}
	}

	switch (ReadChar())
	{
		case '@':
			mContext.token = GetTokenCode(IDX_J_AT);
			return true;

		case '>':
			rangle = true;
			mContext.token = GetTokenCode(IDX_J_RANGLE);
			return true;

		case '0':
			{
				char c = ReadChar();

				if (c == 'x' || c == 'X')
				{
					HexLiteral();
					return true;
				}
				else
				{
					PutBack();
					PutBack();
					return false;
				}
			}
			break;

		default:
			PutBack();
			return false;
	}
}


void CJavaLexer5::PostProcess()
{
	if (mContext.token == GetTokenCode(IDX_J_DOT))
	{
		if (ReadChar() == '.')
		{
			if (ReadChar() == '.')
				mContext.token = GetTokenCode(IDX_J_ELLIPSIS);
			else
			{
				PutBack();
				PutBack();
			}
		}
		else
			PutBack();
	}
	else if (mContext.token == GetTokenCode(IDX_J_ID))
	{
		if (mContext.value_buffer == "enum")
		{
			mContext.token = GetTokenCode(IDX_J_ENUM);
			mContext.token_value = "";
		}
	}
}


void CJavaLexer5::HexLiteral()
{
	do
		ReadChar();
	while (HexDigit());

	if (mContext.ch != '.')
	{
		if (mContext.ch != 'L' && mContext.ch != 'l')
			PutBack();

		mContext.token = GetTokenCode(IDX_J_INT_LITERAL);
		mContext.token_value = mContext.value_buffer;

		return;
	}

	do
		ReadChar();
	while (HexDigit());

	if (mContext.ch != 'p' && mContext.ch != 'P')
		throw new CBadTokenException(this);

	ReadChar();

	if (mContext.ch == '-' || mContext.ch == '+')
		ReadChar();

	if (!DecDigit())
		throw new CBadTokenException(this);

	do
		ReadChar();
	while (DecDigit());

	switch (mContext.ch)
	{
		case 'f':
		case 'F':
		case 'd':
		case 'D':
			break;

		default:
			PutBack();
	}

	mContext.token = GetTokenCode(IDX_J_FLOAT_LITERAL);
	mContext.token_value = mContext.value_buffer;
	return;
}