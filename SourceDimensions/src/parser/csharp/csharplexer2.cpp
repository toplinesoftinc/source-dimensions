#include "csharplexer2.h"
#include "csha2sym.h"


static const int token_map[]= 
{
	TK_CS2_ARGLIST,
	TK_CS2_MAKEREF,
	TK_CS2_REFTYPE,
	TK_CS2_REFVALUE,
	TK_CS2_ABSTRACT,
	TK_CS2_ADD,
	TK_CS2_AS,
	TK_CS2_ASSEMBLY,
	TK_CS2_BASE,
	TK_CS2_BOOL,
	TK_CS2_BREAK,
	TK_CS2_BYTE,
	TK_CS2_CASE,
	TK_CS2_CATCH,
	TK_CS2_CHAR,
	TK_CS2_CHECKED,
	TK_CS2_CLASS,
	TK_CS2_CONST,
	TK_CS2_CONTINUE,
	TK_CS2_DECIMAL,
	TK_CS2_DEFAULT,
	TK_CS2_DELEGATE,
	TK_CS2_DO,
	TK_CS2_DOUBLE,
	TK_CS2_ELSE,
	TK_CS2_ENUM,
	TK_CS2_EVENT,
	TK_CS2_EXPLICIT,
	TK_CS2_EXTERN,
	TK_CS2_FALSE,
	TK_CS2_FIELD,
	TK_CS2_FINALLY,
	TK_CS2_FIXED,
	TK_CS2_FLOAT,
	TK_CS2_FOR,
	TK_CS2_FOREACH,
	TK_CS2_GET,
	TK_CS2_GOTO,
	TK_CS2_IF,
	TK_CS2_IMPLICIT,
	TK_CS2_IN,
	TK_CS2_INT,
	TK_CS2_INTERFACE,
	TK_CS2_INTERNAL,
	TK_CS2_IS,
	TK_CS2_LOCK,
	TK_CS2_LONG,
	TK_CS2_METHOD,
	TK_CS2_MODULE,
	TK_CS2_NAMESPACE,
	TK_CS2_NEW,
	TK_CS2_NULL,
	TK_CS2_OBJECT,
	TK_CS2_OPERATOR,
	TK_CS2_OUT,
	TK_CS2_OVERRIDE,
	TK_CS2_PARAM,
	TK_CS2_PARAMS,
	TK_CS2_PRIVATE,
	TK_CS2_PROPERTY,
	TK_CS2_PROTECTED,
	TK_CS2_PUBLIC,
	TK_CS2_READONLY,
	TK_CS2_REF,
	TK_CS2_REMOVE,
	TK_CS2_RETURN,
	TK_CS2_SBYTE,
	TK_CS2_SEALED,
	TK_CS2_SET,
	TK_CS2_SHORT,
	TK_CS2_SIZEOF,
	TK_CS2_STACKALLOC,
	TK_CS2_STATIC,
	TK_CS2_STRING,
	TK_CS2_STRUCT,
	TK_CS2_SWITCH,
	TK_CS2_THIS,
	TK_CS2_THROW,
	TK_CS2_TRUE,
	TK_CS2_TRY,
	TK_CS2_TYPE,
	TK_CS2_TYPEOF,
	TK_CS2_UINT,
	TK_CS2_ULONG,
	TK_CS2_UNCHECKED,
	TK_CS2_UNSAFE,
	TK_CS2_USHORT,
	TK_CS2_USING,
	TK_CS2_VIRTUAL,
	TK_CS2_VOID,
	TK_CS2_VOLATILE,
	TK_CS2_WHILE,
	TK_CS2_ID,
	TK_CS2_CHAR_LITERAL,
	TK_CS2_STR_LITERAL,
	TK_CS2_INT_LITERAL,
	TK_CS2_REAL_LITERAL,
	TK_CS2_LANGLE,
	TK_CS2_RANGLE,
	TK_CS2_LPAREN,
	TK_CS2_RPAREN,
	TK_CS2_COMMA,
	TK_CS2_LBRACK,
	TK_CS2_RBRACK,
	TK_CS2_LBRACE,
	TK_CS2_RBRACE,
	TK_CS2_EQ,
	TK_CS2_SEMIC,
	TK_CS2_COLON,
	TK_CS2_QUESTION,
	TK_CS2_DOT,
	TK_CS2_PLUS,
	TK_CS2_MINUS,
	TK_CS2_STAR,
	TK_CS2_SLASH,
	TK_CS2_PERCENT,
	TK_CS2_HAT,
	TK_CS2_AND,
	TK_CS2_OR,
	TK_CS2_TILDE,
	TK_CS2_NOT,
	TK_CS2_PLUS_EQ,
	TK_CS2_MINUS_EQ,
	TK_CS2_STAR_EQ,
	TK_CS2_SLASH_EQ,
	TK_CS2_PERCENT_EQ,
	TK_CS2_HAT_EQ,
	TK_CS2_AND_EQ,
	TK_CS2_OR_EQ,
	TK_CS2_LTLT,
	0,
	0,
	TK_CS2_LTLT_EQ,
	TK_CS2_EQEQ,
	TK_CS2_NE,
	TK_CS2_LE,
	0,
	TK_CS2_ANDAND,
	TK_CS2_OROR,
	TK_CS2_INCR,
	TK_CS2_DECR,
	TK_CS2_ARROW,
	TK_CS2_END_OF_FILE,
	TK_CS2_WHITESPACE,
	TK_CS2_PARTIAL,
	TK_CS2_ALIAS,
	TK_CS2_WHERE,
	TK_CS2_YIELD,
	TK_CS2_SCOPE,
	TK_CS2_QQ
};


static int index_map[sizeof(token_map)/sizeof(token_map[0])];


void CSharpLexer2::InitLexer()
{
	m_bFirstToken = true;

	if (!m_bIndexMapInit)
	{	
		for (int i = 0; i < sizeof(token_map)/sizeof(token_map[0]); i++)
			index_map[GetTokenCode(i)] = i;				

		m_bIndexMapInit = true;
	}
}


int CSharpLexer2::GetTokenCode(int index)
{
	return token_map[index];
}


int CSharpLexer2::GetTokenIndex(int token)
{
	return index_map[token];
}


void CSharpLexer2::ParsePP()
{
	if (mContext.value_buffer == "pragma")
	{
		while (!NewLine() && !IsEOF())
			ReadChar(true);
	}
	else
		CSharpLexer::ParsePP();
}


bool CSharpLexer2::PreProcess()
{
	if (rangle)
	{
		long pos = mContext.cur;

		rangle = false;

		SkipWhiteSpaces();
		if (mContext.cur > pos)
		{
			mContext.token = GetTokenCode(IDX_CS_WHITESPACE);	
			return true;
		}
	}

	if (ReadChar() == '>')
	{
		rangle = true;
		mContext.token = GetTokenCode(IDX_CS_RANGLE);
		return true;
	}
	else
	{
		PutBack();
		return false;
	}
}


void CSharpLexer2::PostProcess()
{
	switch (GetTokenIndex(mContext.token))
	{
		case IDX_CS_QUESTION:
			if (ReadChar() == '?')
				mContext.token = GetTokenCode(IDX_CS_QQ);
			else
				PutBack();
			break;
	
		case IDX_CS_ID:
			switch (mContext.value_buffer.char32At(0))
			{
				case 'a':
					if (mContext.value_buffer == "alias")
					{
						mContext.token = GetTokenCode(IDX_CS_ALIAS);
						mContext.token_value = "";
					}
					break;

				case 'p':
					if (mContext.value_buffer == "partial")
					{
						mContext.token = GetTokenCode(IDX_CS_PARTIAL);
						mContext.token_value = "";
					}
					break;

				case 'w':
					if (mContext.value_buffer == "where")
					{
						mContext.token = GetTokenCode(IDX_CS_WHERE);
						mContext.token_value = "";
					}
					break;

				case 'y':
					if (mContext.value_buffer == "yield")
					{
						mContext.token = GetTokenCode(IDX_CS_YIELD);
						mContext.token_value = "";
					}
					break;

				default:
					return;
			}
			break;


		case IDX_CS_COLON:
			if (ReadChar() == ':')
				mContext.token = GetTokenCode(IDX_CS_SCOPE);
			else
				PutBack();
			break;
	}
}
