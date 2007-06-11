#include "csharplexer.h"
#include "../common/parser.h"
#include "../common/exceptions.h"
#include "unicode/uchar.h"
#include "unicode/normlzr.h"
#include "csharsym.h"
#include <stack>


#define IF_STATE	0
#define ELSE_STATE	1


bool CSharpLexer::m_bIndexMapInit = false;


static const int token_map[]= 
{
	TK_CS_ARGLIST,
	TK_CS_MAKEREF,
	TK_CS_REFTYPE,
	TK_CS_REFVALUE,
	TK_CS_ABSTRACT,
	TK_CS_ADD,
	TK_CS_AS,
	TK_CS_ASSEMBLY,
	TK_CS_BASE,
	TK_CS_BOOL,
	TK_CS_BREAK,
	TK_CS_BYTE,
	TK_CS_CASE,
	TK_CS_CATCH,
	TK_CS_CHAR,
	TK_CS_CHECKED,
	TK_CS_CLASS,
	TK_CS_CONST,
	TK_CS_CONTINUE,
	TK_CS_DECIMAL,
	TK_CS_DEFAULT,
	TK_CS_DELEGATE,
	TK_CS_DO,
	TK_CS_DOUBLE,
	TK_CS_ELSE,
	TK_CS_ENUM,
	TK_CS_EVENT,
	TK_CS_EXPLICIT,
	TK_CS_EXTERN,
	TK_CS_FALSE,
	TK_CS_FIELD,
	TK_CS_FINALLY,
	TK_CS_FIXED,
	TK_CS_FLOAT,
	TK_CS_FOR,
	TK_CS_FOREACH,
	TK_CS_GET,
	TK_CS_GOTO,
	TK_CS_IF,
	TK_CS_IMPLICIT,
	TK_CS_IN,
	TK_CS_INT,
	TK_CS_INTERFACE,
	TK_CS_INTERNAL,
	TK_CS_IS,
	TK_CS_LOCK,
	TK_CS_LONG,
	TK_CS_METHOD,
	TK_CS_MODULE,
	TK_CS_NAMESPACE,
	TK_CS_NEW,
	TK_CS_NULL,
	TK_CS_OBJECT,
	TK_CS_OPERATOR,
	TK_CS_OUT,
	TK_CS_OVERRIDE,
	TK_CS_PARAM,
	TK_CS_PARAMS,
	TK_CS_PRIVATE,
	TK_CS_PROPERTY,
	TK_CS_PROTECTED,
	TK_CS_PUBLIC,
	TK_CS_READONLY,
	TK_CS_REF,
	TK_CS_REMOVE,
	TK_CS_RETURN,
	TK_CS_SBYTE,
	TK_CS_SEALED,
	TK_CS_SET,
	TK_CS_SHORT,
	TK_CS_SIZEOF,
	TK_CS_STACKALLOC,
	TK_CS_STATIC,
	TK_CS_STRING,
	TK_CS_STRUCT,
	TK_CS_SWITCH,
	TK_CS_THIS,
	TK_CS_THROW,
	TK_CS_TRUE,
	TK_CS_TRY,
	TK_CS_TYPE,
	TK_CS_TYPEOF,
	TK_CS_UINT,
	TK_CS_ULONG,
	TK_CS_UNCHECKED,
	TK_CS_UNSAFE,
	TK_CS_USHORT,
	TK_CS_USING,
	TK_CS_VIRTUAL,
	TK_CS_VOID,
	TK_CS_VOLATILE,
	TK_CS_WHILE,
	TK_CS_ID,
	TK_CS_CHAR_LITERAL,
	TK_CS_STR_LITERAL,
	TK_CS_INT_LITERAL,
	TK_CS_REAL_LITERAL,
	TK_CS_LANGLE,
	TK_CS_RANGLE,
	TK_CS_LPAREN,
	TK_CS_RPAREN,
	TK_CS_COMMA,
	TK_CS_LBRACK,
	TK_CS_RBRACK,
	TK_CS_LBRACE,
	TK_CS_RBRACE,
	TK_CS_EQ,
	TK_CS_SEMIC,
	TK_CS_COLON,
	TK_CS_QUESTION,
	TK_CS_DOT,
	TK_CS_PLUS,
	TK_CS_MINUS,
	TK_CS_STAR,
	TK_CS_SLASH,
	TK_CS_PERCENT,
	TK_CS_HAT,
	TK_CS_AND,
	TK_CS_OR,
	TK_CS_TILDE,
	TK_CS_NOT,
	TK_CS_PLUS_EQ,
	TK_CS_MINUS_EQ,
	TK_CS_STAR_EQ,
	TK_CS_SLASH_EQ,
	TK_CS_PERCENT_EQ,
	TK_CS_HAT_EQ,
	TK_CS_AND_EQ,
	TK_CS_OR_EQ,
	TK_CS_LTLT,
	TK_CS_GTGT,
	TK_CS_GTGT_EQ,
	TK_CS_LTLT_EQ,
	TK_CS_EQEQ,
	TK_CS_NE,
	TK_CS_LE,
	TK_CS_GE,
	TK_CS_ANDAND,
	TK_CS_OROR,
	TK_CS_INCR,
	TK_CS_DECR,
	TK_CS_ARROW,
	TK_CS_END_OF_FILE
};


static int index_map[sizeof(token_map)/sizeof(token_map[0])];


void CSharpLexer::InitLexer()
{
	m_bFirstToken = true;

	if (!m_bIndexMapInit)
	{	
		for (int i = 0; i < sizeof(token_map)/sizeof(token_map[0]); i++)
			index_map[GetTokenCode(i)] = i;				

		m_bIndexMapInit = true;
	}
}


int CSharpLexer::GetTokenCode(int index)
{
	return token_map[index];
}


int CSharpLexer::GetTokenIndex(int token)
{
	return index_map[token];
}


void CSharpLexer::DefinePPSymbol(const UChar* SymbolName)
{
	UnicodeString *str = new UnicodeString(SymbolName);
	mPPSymbols.insert(*str);
	delete str;
}


void CSharpLexer::UndefPPSymbol(const UChar* SymbolName)
{
	UnicodeString *str = new UnicodeString(SymbolName);
	mPPSymbols.erase(*str);
	delete str;
}


void CSharpLexer::Rollback()
{
	if (!mCtxStack.empty() && !mNestStack.empty())
	{
		long cur = mCtxStack.top().cur;
		while (mNestStack.top().pos > cur)
		{
			mNestStack.pop();
			if (mNestStack.empty())
				break;
		}
	}

	CLexer::Rollback();
}


int CSharpLexer::ReadToken()
{
	m_bFirstToken = false;

	switch (ReadChar())
	{
		case '<':
			switch (ReadChar())
			{
				case '<':
					if (ReadChar() == '=')
						mContext.token = GetTokenCode(IDX_CS_LTLT_EQ);
					else
					{
						mContext.token = GetTokenCode(IDX_CS_LTLT);
						PutBack();
					}
					break;

				case '=':
					mContext.token = GetTokenCode(IDX_CS_LE);
					break;

				default:
					PutBack();
					mContext.token = GetTokenCode(IDX_CS_LANGLE);
			}

			break;

		case '>':
			switch (ReadChar())
			{
				case '>':
					if (ReadChar() == '=')
						mContext.token = GetTokenCode(IDX_CS_GTGT_EQ);
					else
					{
						PutBack();
						mContext.token = GetTokenCode(IDX_CS_GTGT);
					}
					break;

				case '=':
					mContext.token = GetTokenCode(IDX_CS_GE);
					break;

				default:
					PutBack();
					mContext.token = GetTokenCode(IDX_CS_RANGLE);
			}
			break;

		case '(':
			mContext.token = GetTokenCode(IDX_CS_LPAREN);
			break;

		case ')':
			mContext.token = GetTokenCode(IDX_CS_RPAREN);
			break;

		case '.':
			ReadChar();
			if (DecDigit())
			{
				PutBack();
				mContext.ch = '.';
				NumericLiteral();
			}
			else
			{
				PutBack();
				mContext.token = GetTokenCode(IDX_CS_DOT);
			}
			break;

		case ',':
			mContext.token = GetTokenCode(IDX_CS_COMMA);
			break;

		case '[':
			mContext.token = GetTokenCode(IDX_CS_LBRACK);
			break;

		case ']':
			mContext.token = GetTokenCode(IDX_CS_RBRACK);
			break;

		case '{':
			mContext.token = GetTokenCode(IDX_CS_LBRACE);
			break;

		case '}':
			mContext.token = GetTokenCode(IDX_CS_RBRACE);
			break;

		case '=':
			if (ReadChar() == '=')
				mContext.token = GetTokenCode(IDX_CS_EQEQ);
			else
			{
				PutBack();
				mContext.token = GetTokenCode(IDX_CS_EQ);
			}
			break;

		case ':':
			mContext.token = GetTokenCode(IDX_CS_COLON);
			break;

		case ';':
			mContext.token = GetTokenCode(IDX_CS_SEMIC);
			break;

		case '?':
			mContext.token = GetTokenCode(IDX_CS_QUESTION);
			break;

		case '+':
			switch (ReadChar())
			{
				case '+':
					mContext.token = GetTokenCode(IDX_CS_INCR);
					break;

				case '=':
					mContext.token = GetTokenCode(IDX_CS_PLUS_EQ);
					break;

				default:
					PutBack();
					mContext.token = GetTokenCode(IDX_CS_PLUS);	
			}
			break;

		case '-':
			switch (ReadChar())
			{
				case '-':
					mContext.token = GetTokenCode(IDX_CS_DECR);
					break;

				case '=':
					mContext.token = GetTokenCode(IDX_CS_MINUS_EQ);
					break;

				case '>':
					mContext.token = GetTokenCode(IDX_CS_ARROW);
					break;

				default:
					PutBack();
					mContext.token = GetTokenCode(IDX_CS_MINUS);	
			}
			break;

		case '*':
			if (ReadChar() == '=')
				mContext.token = GetTokenCode(IDX_CS_STAR_EQ);
			else
			{
				PutBack();
				mContext.token = GetTokenCode(IDX_CS_STAR);
			}
			break;

		case '/':
			switch (ReadChar())
			{
				case '=':
					mContext.token = GetTokenCode(IDX_CS_SLASH_EQ);
					break;

				case '/':
					SingleLineComment();
					break;

				case '*':
					MultiLineComment();
					break;

				default:
					PutBack();
					mContext.token = GetTokenCode(IDX_CS_SLASH);
			}
			break;

		case '%':
			if (ReadChar() == '=')
				mContext.token = GetTokenCode(IDX_CS_PERCENT_EQ);
			else
			{
				PutBack();
				mContext.token = GetTokenCode(IDX_CS_PERCENT);
			}
			break;

		case '^':
			if (ReadChar() == '=')
				mContext.token = GetTokenCode(IDX_CS_HAT_EQ);
			else
			{
				PutBack();
				mContext.token = GetTokenCode(IDX_CS_HAT);
			}
			break;

		case '&':
			switch (ReadChar())
			{
				case '&':
					mContext.token = GetTokenCode(IDX_CS_ANDAND);
					break;

				case '=':
					mContext.token = GetTokenCode(IDX_CS_AND_EQ);
					break;

				default:
					PutBack();
					mContext.token = GetTokenCode(IDX_CS_AND);
			}
			break;

		case '|':
			switch (ReadChar())
			{
				case '|':
					mContext.token = GetTokenCode(IDX_CS_OROR);
					break;

				case '=':
					mContext.token = GetTokenCode(IDX_CS_OR_EQ);
					break;

				default:
					PutBack();
					mContext.token = GetTokenCode(IDX_CS_OR);
			}
			break;

		case '!':
			if (ReadChar() == '=')
				mContext.token = GetTokenCode(IDX_CS_NE);
			else
			{
				PutBack();
				mContext.token = GetTokenCode(IDX_CS_NOT);
			}
			break;

		case '~':
			mContext.token = GetTokenCode(IDX_CS_TILDE);
			break;

		case '\'':
			QuotedLiteral(false);
			break;

		case '"':
			QuotedLiteral(true);
			break;

        case '@':
            mContext.value_buffer = "";
            ReadChar();

            if (mContext.ch == '"')
            {
            	bool end = false;

				do
                {
                	switch (ReadChar())
                    {
                    	case '"':
                        	if (ReadChar() == '"')
								Eat();
							else
                            {
								PutBack();
                            	mContext.token = GetTokenCode(IDX_CS_STR_LITERAL);
								mContext.token_value = mContext.value_buffer;
                            	end = true;
                            }
	                       	break;

                        case '\0':
							if (IsEOF())
								throw new CBadTokenException(this);
                    }
                }
                while (!end);
            }
            else
            {
	            if (IdStart())
    	        {
    				Identifier();
        			mContext.token = GetTokenCode(IDX_CS_ID);
					mContext.token_value = mContext.value_buffer;
	            }
    	        else
        	        throw new CBadTokenException(this);
            }
			break;

		default:
			if (DecDigit())
				NumericLiteral();
			else
				if (IdStart())
                {
                    if (!Identifier())
					{
						const KEYWORD *k;

						k = MatchKeyword();

						if (k != NULL)
						{
							mContext.token = GetTokenCode(k->index);
						}
						else
						{
							mContext.token = GetTokenCode(IDX_CS_ID);
							mContext.token_value = mContext.value_buffer;
						}
					}
					else
					{
						mContext.token = GetTokenCode(IDX_CS_ID);
						mContext.token_value = mContext.value_buffer;
					}
	              }
                else
				{
					if (IsEOF())
						mContext.token = GetTokenCode(IDX_CS_END_OF_FILE);
					else
						throw new CBadTokenException(this);
				}
	}

	return mContext.token;
}



void CSharpLexer::QuotedLiteral(bool string)
{
	char qChar = string ? '"' : '\'';

	Eat();

	while (ReadChar() != qChar)
	{
		switch (mContext.ch)
		{
			case '\0':
				if (IsEOF())
					throw new CBadTokenException(this);
				break;

			case '\\':
				Eat();
				EscapeSequence(string);
				break;

			default:
				if (NewLine())
					throw new CBadTokenException(this);
		}
	}

	Eat();

	mContext.token = string ? GetTokenCode(IDX_CS_STR_LITERAL) : GetTokenCode(IDX_CS_CHAR_LITERAL);
	mContext.token_value = mContext.value_buffer;
}


void CSharpLexer::NumericLiteral()
{
	if (mContext.ch == '0')
	{
		switch (ReadChar())
		{
			case 'X':
			case 'x':
				ReadChar();
				if (HexDigit())
				{
					do
						ReadChar();
					while (HexDigit());

					mContext.token = GetTokenCode(IDX_CS_INT_LITERAL);
					mContext.token_value = mContext.value_buffer;

					switch (mContext.ch)
					{
						case 'L':
						case 'l':
							switch (ReadChar())
							{
								case 'U':
								case 'u':
									break;
						
								default:
									PutBack();
							}		                        
							break;

						case 'U':
						case 'u':
							switch (ReadChar())
							{
								case 'L':
								case 'l':
									break;
						
								default:
									PutBack();
							}		                        
							break;

						default:
							PutBack();
					}
					return;
				}
				else
					throw new CBadTokenException(this);
				break;

			default:
				PutBack();
		}
	}

	while (DecDigit())
		ReadChar();

	switch (mContext.ch)
	{
		case 'U':
		case 'u':
			switch (ReadChar())
			{
				case 'L':
				case 'l':
					break;

				default:
					PutBack();
			}
			mContext.token = GetTokenCode(IDX_CS_INT_LITERAL);
			mContext.token_value = mContext.value_buffer;
			return;


		case 'L':
		case 'l':
			switch (ReadChar())
			{
				case 'U':
				case 'u':
					break;

				default:
					PutBack();
			}
			mContext.token = GetTokenCode(IDX_CS_INT_LITERAL);
			mContext.token_value = mContext.value_buffer;
			return;

		case 'F':
		case 'f':
        case 'd':
        case 'D':
        case 'm':
        case 'M':
			mContext.token = GetTokenCode(IDX_CS_REAL_LITERAL);
			mContext.token_value = mContext.value_buffer;
			return;

		case '.':
			ReadChar();
            if (!DecDigit())
			{
				PutBack();
				PutBack();
				mContext.token = GetTokenCode(IDX_CS_INT_LITERAL);
				mContext.token_value = mContext.value_buffer;
				return;
			}
            else
            {
				while (DecDigit())
					ReadChar();
                    
				switch (mContext.ch)
				{
					case 'F':
					case 'f':
					case 'D':
					case 'd':
					case 'm':
					case 'M':
						mContext.token = GetTokenCode(IDX_CS_REAL_LITERAL);
						mContext.token_value = mContext.value_buffer;
						return;

					case 'E':
					case 'e':
						break;

					default:
						PutBack();
						mContext.token = GetTokenCode(IDX_CS_REAL_LITERAL);
						mContext.token_value = mContext.value_buffer;
						return;
				}		
            }

		case 'E':
		case 'e':
			switch (ReadChar())
			{
				case '+':
				case '-':
					ReadChar();
			}

			if (!DecDigit())
				throw new CBadTokenException(this);
			else
			{
				do
					ReadChar();
				while (DecDigit());
			}

			switch (mContext.ch)
			{
				case 'F':
				case 'f':
				case 'D':
				case 'd':
				case 'm':
				case 'M':
					break;

				default:
					PutBack();
			}

			mContext.token = GetTokenCode(IDX_CS_REAL_LITERAL);
			mContext.token_value = mContext.value_buffer;
			break;

		default:
			PutBack();
			mContext.token = GetTokenCode(IDX_CS_INT_LITERAL);
			mContext.token_value = mContext.value_buffer;
	}
}



bool CSharpLexer::Identifier()
{
	bool pure_id = false;
	UChar ch;
	do
	{
		ch = ReadChar();
		if (ch == '\\')
		{
			Eat();
			switch (ReadChar(true))
			{
				case 'u':
					UnicodeEscapeSequence(&ch, NULL);
					AppendCharToBuffer(ch);
					pure_id = true;
					break;

				case 'U':
					{
						UChar c;

						UnicodeEscapeSequence(&c, &ch);
						AppendCharToBuffer(c);
						AppendCharToBuffer(ch);
					}
					pure_id = true;
					break;

				default:
					PutBack();
			}
		}
	}
	while (u_isIDPart(ch) && !IsEOF());

	if (IsEOF())
		Eat();
	else
		PutBack();

	UnicodeString normalized;
	UErrorCode err = U_ZERO_ERROR;

	Normalizer::normalize(mContext.value_buffer, UNORM_NFC, 0, normalized, err);

	if (U_FAILURE(err))
		throw new CBadTokenException(this);

	mContext.value_buffer = normalized;

	return pure_id;
}


void CSharpLexer::EscapeSequence(bool string)
{
	long start = mContext.cur - 1;
	SYMBOL_SUBST subst;
	UChar ch;

	switch (ReadChar(true))
	{
		case '\'':
		case '"':
		case '\\':
			ch = mContext.ch;
			break;

		case '\0':
			ch = 0;
			break;

		case 'a':
			ch = '\a';
			break;

		case 'b':
			ch = '\b';
			break;

		case 'f':
			ch = '\f';
			break;

		case 'n':
			ch = '\n';
			break;

		case 'r':
			ch = '\r';
			break;

		case 't':
			ch = '\t';
			break;

		case 'v':			
			ch = '\v';
			break;

		case 'u':
			UnicodeEscapeSequence(&ch, NULL);
			break;

		case 'U':
			{
				UChar c;

				UnicodeEscapeSequence(&c, &ch);

				if (string)
				{
					AppendCharToBuffer(c);
					AppendCharToBuffer(ch);
				}
				else
				{
					if (c != 0)
						throw new CBadTokenException(this);
				}
			}
			break;

		case 'x':
			{
				UnicodeString buf;

				ReadChar(true);
				if (HexDigit())
				{
            		for (int i = 0; i < 4 && HexDigit(); ReadChar(true), i++)
						buf.append(mContext.ch);

					if (!HexDigit())
						PutBack();

					ch = (UChar)wcstol((wchar_t*)buf.getTerminatedBuffer(), NULL, 16);
				}
				else
					throw new CBadTokenException(this);
			}
			break;

		default:
			{
				UnicodeString buf;

				if (OctalDigit())
				{
            		for (int i = 0; i < 3 && OctalDigit(); ReadChar(true), i++)
						buf.append(mContext.ch);

					if (!OctalDigit())
						PutBack();

					unsigned long val = wcstol((wchar_t*)buf.getTerminatedBuffer(), NULL, 8);

					if (val > 255)
						throw new CBadTokenException(this);
					else
						ch = (UChar)val;
				}
				else
					throw new CBadTokenException(this);
			}
	}

	subst.cur = mContext.cur;
	subst.len = subst.cur - start;
	mSubstStack.push(subst);

	AppendCharToBuffer(ch);
}


void CSharpLexer::SingleLineComment()
{
	bool exit = false;

	while (!exit)
	{
		switch (ReadChar(true))
		{
			case '\0':
				if (IsEOF())
					exit = true;
				break;

			default:
				if (NewLine())
					exit = true;
		}
	}
}


void CSharpLexer::MultiLineComment()
{
	bool exit = false;

	while (!exit)
	{
		switch (ReadChar(true))
		{
			case '\0':
				if (IsEOF())
					throw new CBadTokenException(this);
				break;

			case '*':
				if (ReadChar(true) == '/')
					exit = true;
				else
					PutBack();
		}
	}
}


void CSharpLexer::UnicodeEscapeSequence(UChar *first, UChar *second)
{
	UnicodeString buf;
	UChar *cur = first;
	bool first_char = true;

	while (true)
	{
		for (int i = 0; i < 4; i++)
		{
			ReadChar(true);

			if (!HexDigit())
				throw new CBadTokenException(this);

			buf.append(mContext.ch);
		}

		*cur = (UChar)wcstol((wchar_t*)buf.getTerminatedBuffer(), NULL, 16);

		if (first_char && second)
		{
			cur = second;
			buf.remove();
			first_char = false;
		}
		else
			break;
	}
}


bool CSharpLexer::DecDigit()
{
	switch (mContext.ch)
	{
		case '0':
		case '1':
		case '2':
		case '3':
		case '4':
		case '5':
		case '6':
		case '7':
		case '8':
		case '9':
			return true;

		default:
			return false;
	}
}


bool CSharpLexer::NonZeroDecDigit()
{
	switch (mContext.ch)
	{
		case '1':
		case '2':
		case '3':
		case '4':
		case '5':
		case '6':
		case '7':
		case '8':
		case '9':
			return true;

		default:
			return false;
	}
}


bool CSharpLexer::NonDigit()
{

	switch (mContext.ch)
	{
		case '_':
		case 'a':
		case 'b':
		case 'c':
		case 'd':
		case 'e':
		case 'f':
		case 'g':
		case 'h':
		case 'i':
		case 'j':
		case 'k':
		case 'l':
		case 'm':				
		case 'n':
		case 'o':
		case 'p':
		case 'q':
		case 'r':
		case 's':
		case 't':
		case 'u':
		case 'v':
		case 'w':
		case 'x':
		case 'y':
		case 'z':
		case 'A':
		case 'B':
		case 'C':
		case 'D':
		case 'E':
		case 'F':
		case 'G':
		case 'H':
		case 'I':
		case 'J':
		case 'K':
		case 'L':
		case 'M':
		case 'N':
		case 'O':
		case 'P':
		case 'Q':
		case 'R':
		case 'S':
		case 'T':
		case 'U':
		case 'V':
		case 'W':
		case 'X':
		case 'Y':
		case 'Z': 
			return true;

		default:
			return false;
	}
}


bool CSharpLexer::OctalDigit()
{
	switch (mContext.ch)
	{	
		case '0':
		case '1':
		case '2':
		case '3':
		case '4':
		case '5':
		case '6':
		case '7':
			return true;

		default:
			return false;
	}
}


bool CSharpLexer::HexDigit()
{
	switch (mContext.ch)
	{
		case '0':
		case '1':
		case '2':
		case '3':
		case '4':
		case '5':
		case '6':
		case '7':
		case '8':
		case '9':
		case 'a':
		case 'b':
		case 'c':
		case 'd':
		case 'e':
		case 'f':
		case 'A':
		case 'B':
		case 'C':
		case 'D':
		case 'E':
		case 'F':
			return true;

		default:
			return false;
	}
}


bool CSharpLexer::IdStart()
{
	return (u_isIDStart(mContext.ch) || mContext.ch == '_');
}


void CSharpLexer::SkipWhiteSpaces()
{
	bool line_start = true;
	long head;

	while (!IsEOF())
	{
		if (NewLine())
			line_start = true;

		switch (ReadChar(true))
		{
			case '/':
				line_start = false;
				switch (ReadChar(true))
				{
					case '/':
						head = mContext.cur - 2;
						SingleLineComment();
						line_start = true;
						break;

					case '*':
						head = mContext.cur - 2;
						MultiLineComment();
						break;

					default:
						PutBack();
						mContext.ch = '/';
						PutBack();
						return;
				}
				break;

			case '#':
				if (line_start)
				{
					if (!SkipWhiteSpacesPP())
						throw new CInvalidPPExprException(this);

					mContext.value_buffer = "";

					if (Identifier())
						throw new CInvalidPPExprException(this);

					ParsePP();
					mContext.value_buffer = "";
					continue;
				}
				else
				{
					PutBack();
					return;
				}
				break;

			case '\0':
				if (IsEOF())
					return;
				break;

			default:
				if (NewLine())
				{
					line_start = true;
					continue;
				}
				else
				{
					if (!WhiteSpace())
					{
						PutBack();
						return;
					}
				}
		}
	}
}


bool CSharpLexer::NewLine()
{
	switch (mContext.ch)
	{
		case '\n':
		case '\r':
		case 0x2028:
		case 0x2029:
			return true;

		default:
			return false;
	}
}

bool CSharpLexer::WhiteSpace()
{
	switch (mContext.ch)
	{
		case '\t':
		case '\v':
		case '\f':
			return true;

		default:
			if (u_charType(mContext.ch) == U_SPACE_SEPARATOR)
				return true;
			else
				return false;
	}
}


void CSharpLexer::ParsePP()
{
	const KEYWORD *k;
	long head = mContext.cur - 1;

	SkipWhiteSpacesPP();

   	k = MatchPreprocKeyword();

	if (k == NULL)
		throw new CInvalidPPExprException(this);

	switch (k->index)
	{
		case PP_DEFINE:
		case PP_UNDEF:
			if (!m_bFirstToken)
				throw new CInvalidPPExprException(this);
			
			mContext.value_buffer = "";
			ReadChar();	
			
			if (IdStart())
				Identifier();
			else
				throw new CInvalidPPExprException(this);

			if (k->index == PP_DEFINE)
				mPPSymbols.insert(mContext.value_buffer);
			else
				mPPSymbols.erase(mContext.value_buffer);

			break;

		case PP_IF:
			mNestStack.push(PP_STATE(IF_STATE, mContext.cur));
			if (EvalExprPP())
				return;
			else
			{
				for (int t = NextSectionPP(); t != PP_ENDIF; t = NextSectionPP())
				{
					switch (t)
					{
						case PP_ELSE:
							mNestStack.pop();
							mNestStack.push(PP_STATE(ELSE_STATE, mContext.cur));
							return;

						case PP_ELIF:
							if (mNestStack.size() > 0)
							{
								if (mNestStack.top().state == IF_STATE)
								{
									head = mContext.cur;
									if (EvalExprPP())
										return;
								}
								else
									throw new CInvalidPPExprException(this);
							}
							else
								throw new CInvalidPPExprException(this);
							break;

						case PP_EOF:
							throw new CInvalidPPExprException(this);
					}
				}
				mNestStack.pop();
				return;
			}
			break;

		case PP_ELIF:
		case PP_ELSE:
			if (mNestStack.size() > 0)
			{
				if (mNestStack.top().state == IF_STATE)
				{
					for (int t = NextSectionPP(); t != PP_ENDIF; t = NextSectionPP())
					{
						if ((t == PP_ELIF && k->index == PP_ELSE) || t == PP_EOF)
							throw new CInvalidPPExprException(this);
					}
					return;
				}
				else
					throw new CInvalidPPExprException(this);
			}
			else
				throw new CInvalidPPExprException(this);
			break;

		case PP_ENDIF:
			if (mNestStack.size() > 0)
				mNestStack.pop();
			else
				throw new CInvalidPPExprException(this);
			break;

		case PP_LINE:
		case PP_ERROR:
		case PP_WARNING:
		case PP_REGION:
		case PP_ENDREGION:
			while (!NewLine() && !IsEOF())
				ReadChar(true);
	}
}


bool CSharpLexer::SkipWhiteSpacesPP()
{
	while (!IsEOF())
	{
		if (ReadChar(true) == '\0')
		{
			if (IsEOF())
				return false;
		}
		else
		{
			if (NewLine())
				return false;
			else
			{
				if (!WhiteSpace())
				{
					PutBack();
					return true;
				}
			}
		}
	}

	return false;
}


int CSharpLexer::NextSectionPP()
{
	bool line_start = true;
	int nest = 0;
	long head = mContext.cur;

	while (!IsEOF())
	{
		if (NewLine())
			line_start = true;

		switch (ReadChar(true))
		{
			case '#':
				if (line_start)
				{
					if (SkipWhiteSpacesPP())
					{
						const KEYWORD *k;
						long hd = mContext.cur - 1;

						line_start = false;
						mContext.value_buffer = "";
						if (Identifier())
							throw new CInvalidPPExprException(this);

						k = MatchPreprocKeyword();

						if (k != NULL)
						{
							switch (k->index)
							{
								case PP_IF:
									nest += 2;
									break;

								case PP_ELIF:
									if (nest == 0)
										return k->index;
									else
									{
										if ((nest % 2) == 1)
											throw new CInvalidPPExprException(this);
									}
									break;

								case PP_ELSE:
									if (nest == 0)
										return k->index;
									else
										nest--;
									break;

								case PP_ENDIF:
									if (nest == 0)
										return k->index;
									else
										nest -= (2 - (nest % 2));
							}
						}
					}
				}
				break;

			default:
				if (!WhiteSpace())
					line_start = false;
		}
	}

	return PP_EOF;
}


bool CSharpLexer::EvalExprPP(bool paren)
{

	int oper, prev_op = PP_OP_EOE;
	std::stack<int> expr_stack;
	
	expr_stack.push(ParseOperandPP());

	for(;;)
	{
		oper = ParseOperatorPP();

		if ((prev_op - oper) >= 2 || oper == PP_OP_EOE)
		{
			int arg1, arg2, op;

			arg2 = expr_stack.top();
			expr_stack.pop();

			while (!expr_stack.empty())
			{
				op = expr_stack.top();
				expr_stack.pop();

				arg1 = expr_stack.top();
				expr_stack.pop();

				arg2 =  EvalBoolOpPP(arg1, op, arg2);
			}

			expr_stack.push(arg2);

			if (oper == PP_OP_EOE)
				break;
		}

		expr_stack.push(oper);
		expr_stack.push(ParseOperandPP());
		prev_op = oper;
	}

	if (paren)
	{
		if (!SkipWhiteSpacesPP())
			throw new CInvalidPPExprException(this);

		if (ReadChar(true) != ')')
			throw new CInvalidPPExprException(this);
	}

	return expr_stack.top();
}


int CSharpLexer::ParseOperatorPP()
{
	mContext.value_buffer = "";

	if (!SkipWhiteSpacesPP())
		return PP_OP_EOE;

	switch (ReadChar(true))
	{
		case '|':
			if (ReadChar(true) == '|')
				return PP_OP_OR;
			else
				throw new CInvalidPPExprException(this);

		case '&':
			if (ReadChar(true) == '&')
				return PP_OP_AND;
			else
				throw new CInvalidPPExprException(this);

		case '=':
			if (ReadChar(true) == '=')
				return PP_OP_EQ;
			else
				throw new CInvalidPPExprException(this);

		case '!':
			if (ReadChar(true) == '=')
				return PP_OP_NEQ;
			else
				throw new CInvalidPPExprException(this);

		case ')':
			PutBack();
			return PP_OP_EOE;

		case '/':
			if (ReadChar(true) == '/')
			{
				SingleLineComment();
				return PP_OP_EOE;
			}


		default:
			throw new CInvalidPPExprException(this);
	}
}


bool CSharpLexer::ParseOperandPP()
{
	bool invert = false;

	mContext.value_buffer = "";

	if (!SkipWhiteSpacesPP())
		throw new CInvalidPPExprException(this);

	ReadChar(true);

	while (mContext.ch == '!')
	{
		invert = !invert;
		if (!SkipWhiteSpacesPP())
			throw new CInvalidPPExprException(this);
		ReadChar(true);
	}

	if (mContext.ch == '(')
		return (invert ? !EvalExprPP(true) : EvalExprPP(true));
	else
	{
		PutBack();
		bool pure_id = Identifier();
		if (mContext.value_buffer == "false" && !pure_id)
			return (invert ? !false : false);
		else 
		{
			if (mContext.value_buffer == "true" && !pure_id)
				return (invert ? !true : true);
			else 
			{
				if (mPPSymbols.find(mContext.value_buffer) == mPPSymbols.end())
					return (invert ? !false : false);
				else
					return (invert ? !true : true);
			}
		}
	}
}


bool CSharpLexer::EvalBoolOpPP(bool arg1, int op, bool arg2)
{
	switch (op)
	{
		case PP_OP_OR:
			return (arg1 || arg2);

		case PP_OP_AND:
			return (arg1 && arg2);

		case PP_OP_EQ:
			return (arg1 == arg2);

		case PP_OP_NEQ:
			return (arg1 != arg2);

		default:
			return false;	
	}
}


#define TOTAL_KEYWORDS 92
#define MIN_WORD_LENGTH 2
#define MAX_WORD_LENGTH 10
#define MIN_HASH_VALUE 8
#define MAX_HASH_VALUE 254
/* maximum key range = 247, duplicates = 0 */


inline unsigned int CSharpLexer::GetHash (register const UChar *str, register unsigned int len)
{
	static const unsigned char asso_values[] =
	{
		255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
		255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
		255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
		255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
		255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
		255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
		255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
		255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
		255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
		255, 255, 255, 255, 255,  90, 255,  55,  45,  30,
		10,   5,  25,  70,  35,  20,  40,   5, 100,  10,
		50,  45,   5, 255,   0,   5,   0,  30,  30,  85,
		0,  50,   5, 255, 255, 255, 255, 255, 255, 255,
		255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
		255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
		255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
		255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
		255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
		255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
		255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
		255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
		255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
		255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
		255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
		255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
		255, 255, 255, 255, 255, 255
	};

	register int hval = len;

	switch (hval)
	{
		default:
			hval += asso_values[(unsigned char)str[2]];
	/*FALLTHROUGH*/
		case 2:
		case 1:
			hval += asso_values[(unsigned char)str[0]];
			break;
	}

	return hval + asso_values[(unsigned char)str[len - 1]];
}



static const struct KEYWORD wordlist[] =
{
	{(char*)0}, {(char*)0}, {(char*)0}, {(char*)0},
	{(char*)0}, {(char*)0}, {(char*)0}, {(char*)0},
	{"set", 68},
	{(char*)0}, {(char*)0},
	{"struct", 74},
	{(char*)0}, {(char*)0},
	{"type", 80},
	{"event", 26},
	{"params", 57},
	{(char*)0},
	{"explicit", 27},
	{"else", 24},
	{"param", 56},
	{"remove", 64},
	{(char*)0},
	{"int", 41},
	{(char*)0}, {(char*)0},
	{"method", 47},
	{"is", 44},
	{"for", 34},
	{"this", 76},
	{(char*)0},
	{"module", 48},
	{(char*)0},
	{"implicit", 39},
	{"interface", 42},
	{(char*)0},
	{"typeof", 81},
	{"private", 58},
	{(char*)0},
	{"true", 78},
	{"fixed", 32},
	{"sizeof", 70},
	{"default", 20},
	{(char*)0},
	{"case", 12},
	{"field", 30},
	{"unsafe", 85},
	{"if", 38},
	{"out", 54},
	{"enum", 25},
	{(char*)0},
	{"double", 23},
	{"checked", 15},
	{"ref", 63},
	{"byte", 11},
	{"short", 69},
	{"return", 65},
	{"do", 22},
	{"operator", 53},
	{"base", 8},
	{"break", 10},
	{"extern", 28},
	{"as", 6},
	{"override", 55},
	{"void", 89},
	{"sbyte", 66},
	{"switch", 75},
	{"foreach", 35},
	{"abstract", 4},
	{"protected", 60},
	{"catch", 13},
	{"ushort", 86},
	{"in", 40},
	{"get", 36},
	{"namespace", 49},
	{"float", 33},
	{"sealed", 67},
	{(char*)0},
	{"add", 5},
	{"unchecked", 84},
	{(char*)0},
	{"string", 73},
	{(char*)0}, {(char*)0},
	{"uint", 82},
	{"const", 17},
	{"public", 61},
	{(char*)0}, {(char*)0},
	{"char", 14},
	{"throw", 77},
	{"object", 52},
	{(char*)0},
	{"continue", 18},
	{(char*)0},
	{"class", 16},
	{"static", 72},
	{(char*)0}, {(char*)0}, {(char*)0},
	{"stackalloc", 71},
	{(char*)0}, {(char*)0},
	{"try", 79},
	{"__reftype", 2},
	{"__refvalue", 3},
	{(char*)0}, {(char*)0},
	{"property", 59},
	{(char*)0}, {(char*)0}, {(char*)0}, {(char*)0},
	{"readonly", 62},
	{(char*)0},
	{"while", 91},
	{(char*)0}, {(char*)0},
	{"assembly", 7},
	{"goto", 37},
	{(char*)0}, {(char*)0}, {(char*)0},
	{"delegate", 21},
	{(char*)0},
	{"using", 87},
	{(char*)0}, {(char*)0},
	{"internal", 43},
	{(char*)0}, {(char*)0}, {(char*)0},
	{"finally", 31},
	{(char*)0},
	{"__makeref", 1},
	{"false", 29},
	{(char*)0},
	{"virtual", 88},
	{(char*)0},
	{"lock", 45},
	{(char*)0}, {(char*)0}, {(char*)0},
	{"volatile", 90},
	{(char*)0}, {(char*)0}, {(char*)0},
	{"decimal", 19},
	{(char*)0}, {(char*)0},
	{"ulong", 83},
	{(char*)0}, {(char*)0}, {(char*)0},
	{"__arglist", 0},
	{(char*)0}, {(char*)0}, {(char*)0}, {(char*)0},
	{(char*)0}, {(char*)0}, {(char*)0}, {(char*)0},
	{(char*)0}, {(char*)0}, {(char*)0}, {(char*)0},
	{(char*)0}, {(char*)0}, {(char*)0}, {(char*)0},
	{(char*)0}, {(char*)0}, {(char*)0}, {(char*)0},
	{(char*)0}, {(char*)0}, {(char*)0}, {(char*)0},
	{(char*)0}, {(char*)0}, {(char*)0}, {(char*)0},
	{(char*)0}, {(char*)0}, {(char*)0}, {(char*)0},
	{(char*)0}, {(char*)0}, {(char*)0}, {(char*)0},
	{(char*)0}, {(char*)0}, {(char*)0},
	{"bool", 9},
	{(char*)0}, {(char*)0}, {(char*)0}, {(char*)0},
	{(char*)0}, {(char*)0}, {(char*)0}, {(char*)0},
	{(char*)0}, {(char*)0}, {(char*)0}, {(char*)0},
	{(char*)0}, {(char*)0}, {(char*)0}, {(char*)0},
	{(char*)0}, {(char*)0}, {(char*)0}, {(char*)0},
	{(char*)0}, {(char*)0}, {(char*)0}, {(char*)0},
	{(char*)0}, {(char*)0}, {(char*)0}, {(char*)0},
	{"new", 50},
	{"long", 46},
	{(char*)0}, {(char*)0}, {(char*)0}, {(char*)0},
	{(char*)0}, {(char*)0}, {(char*)0}, {(char*)0},
	{(char*)0}, {(char*)0}, {(char*)0}, {(char*)0},
	{(char*)0}, {(char*)0}, {(char*)0}, {(char*)0},
	{(char*)0}, {(char*)0}, {(char*)0}, {(char*)0},
	{(char*)0}, {(char*)0}, {(char*)0}, {(char*)0},
	{(char*)0}, {(char*)0}, {(char*)0}, {(char*)0},
	{(char*)0},
	{"null", 51}
};


const KEYWORD *CSharpLexer::MatchKeyword()
{
	register const UChar *str = mContext.value_buffer.getBuffer();
	unsigned int len = mContext.value_buffer.length();

	if (len <= MAX_WORD_LENGTH && len >= MIN_WORD_LENGTH)
	{
		register int key = GetHash (str, len);

		if (key <= MAX_HASH_VALUE && key >= 0)
		{
			register const char *s = wordlist[key].name;

			if (s && mContext.value_buffer == s)
				return &wordlist[key];
		}
	}
	return 0;
}

#define PP_TOTAL_KEYWORDS 11
#define PP_MIN_WORD_LENGTH 2
#define PP_MAX_WORD_LENGTH 9
#define PP_MIN_HASH_VALUE 2
#define PP_MAX_HASH_VALUE 19
/* maximum key range = 18, duplicates = 0 */

inline unsigned int CSharpLexer::GetPreprocHash(register const UChar *str, register unsigned int len)
{
	static const unsigned char asso_values[] =
	{
		20, 20, 20, 20, 20, 20, 20, 20, 20, 20,
		20, 20, 20, 20, 20, 20, 20, 20, 20, 20,
		20, 20, 20, 20, 20, 20, 20, 20, 20, 20,
		20, 20, 20, 20, 20, 20, 20, 20, 20, 20,
		20, 20, 20, 20, 20, 20, 20, 20, 20, 20,
		20, 20, 20, 20, 20, 20, 20, 20, 20, 20,
		20, 20, 20, 20, 20, 20, 20, 20, 20, 20,
		20, 20, 20, 20, 20, 20, 20, 20, 20, 20,
		20, 20, 20, 20, 20, 20, 20, 20, 20, 20,
		20, 20, 20, 20, 20, 20, 20, 20, 20, 20,
		5,  0,  0,  0, 20,  0, 20, 20, 10, 20,
		5, 20, 20, 20,  0,  5, 20,  5, 20,  0,
		20, 20, 20, 20, 20, 20, 20, 20, 20, 20,
		20, 20, 20, 20, 20, 20, 20, 20, 20, 20,
		20, 20, 20, 20, 20, 20, 20, 20, 20, 20,
		20, 20, 20, 20, 20, 20, 20, 20, 20, 20,
		20, 20, 20, 20, 20, 20, 20, 20, 20, 20,
		20, 20, 20, 20, 20, 20, 20, 20, 20, 20,
		20, 20, 20, 20, 20, 20, 20, 20, 20, 20,
		20, 20, 20, 20, 20, 20, 20, 20, 20, 20,
		20, 20, 20, 20, 20, 20, 20, 20, 20, 20,
		20, 20, 20, 20, 20, 20, 20, 20, 20, 20,
		20, 20, 20, 20, 20, 20, 20, 20, 20, 20,
		20, 20, 20, 20, 20, 20, 20, 20, 20, 20,
		20, 20, 20, 20, 20, 20, 20, 20, 20, 20,
		20, 20, 20, 20, 20, 20
	};
	register int hval = len;

	switch (hval)
	{
		default:
			hval += asso_values[(unsigned char)str[2]];
      /*FALLTHROUGH*/
		case 2:
		case 1:
			hval += asso_values[(unsigned char)str[0]];
			break;
	}
	return hval;
}

static const struct KEYWORD pp_wordlist[] =
{
	{(char*)0}, {(char*)0},
	{"if", 2},
	{(char*)0},
	{"elif", 3 },
	{"error", 7},
	{"region", 9},
	{"warning", 8},
	{(char*)0},
	{"else", 4},
	{"endif", 5},
	{"define", 0},
	{(char*)0}, {(char*)0},
	{"endregion", 10},
	{"undef", 1},
	{(char*)0}, {(char*)0}, {(char*)0},
	{"line", 6}
};


const KEYWORD *CSharpLexer::MatchPreprocKeyword()
{
	register const UChar *str = mContext.value_buffer.getBuffer();
	unsigned int len = mContext.value_buffer.length();

	if (len <= PP_MAX_WORD_LENGTH && len >= PP_MIN_WORD_LENGTH)
	{
		register int key = GetPreprocHash (str, len);

		if (key <= PP_MAX_HASH_VALUE && key >= 0)
		{
			register const char *s = pp_wordlist[key].name;

			if (s && mContext.value_buffer == s)
				return &pp_wordlist[key];
		}
	}
	return 0;
}
