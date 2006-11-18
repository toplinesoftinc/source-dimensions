#include "javalexer.h"
#include "javasym.h"
#include "../common/parser.h"
#include "../common/exceptions.h"
#include "unicode/uchar.h"
#include "unicode/normlzr.h" 
#include <wchar.h>


bool CJavaLexer::m_bIndexMapInit = false;


static const int token_map[] =
{
	TK_J_ABSTRACT,
	TK_J_ASSERT,
	TK_J_BOOLEAN,
	TK_J_BREAK,
	TK_J_BYTE,
	TK_J_CASE,
	TK_J_CATCH,
	TK_J_CHAR,
	TK_J_CLASS,
	TK_J_CONTINUE,
	TK_J_DEFAULT,
	TK_J_DO,
	TK_J_DOUBLE,
	TK_J_ELSE,
	TK_J_EXTENDS,
	TK_J_FALSE,
	TK_J_FINAL,
	TK_J_FINALLY,
	TK_J_FLOAT,
	TK_J_FOR,
	TK_J_IF,
	TK_J_IMPLEMENTS,
	TK_J_IMPORT,
	TK_J_INSTANCEOF,
	TK_J_INT,
	TK_J_INTERFACE,
	TK_J_LONG,
	TK_J_NATIVE,
	TK_J_NEW,
	TK_J_NULL,
	TK_J_PACKAGE,
	TK_J_PRIVATE,
	TK_J_PROTECTED,
	TK_J_PUBLIC,
	TK_J_RETURN,
	TK_J_SHORT,
	TK_J_STATIC,
	TK_J_STRICTFP,
	TK_J_SUPER,
	TK_J_SWITCH,
	TK_J_SYNCHRONIZED,
	TK_J_THIS,
	TK_J_THROW,
	TK_J_THROWS,
	TK_J_TRANSIENT,
	TK_J_TRUE,
	TK_J_TRY,
	TK_J_VOID,
	TK_J_VOLATILE,
	TK_J_WHILE,
	TK_J_ID,
	TK_J_INT_LITERAL,
	TK_J_FLOAT_LITERAL,
	TK_J_CHAR_LITERAL,
	TK_J_STR_LITERAL,
	TK_J_LPAREN,			
	TK_J_RPAREN,			
	TK_J_COMMA,				
	TK_J_LBRACK,			
	TK_J_RBRACK,			
	TK_J_LBRACE,			
	TK_J_RBRACE,			
	TK_J_EQ,			 	
	TK_J_SEMIC,				
	TK_J_COLON,				
	TK_J_QUESTION,			
	TK_J_DOT,				
	TK_J_PLUS,				
	TK_J_MINUS,				
	TK_J_STAR,				
	TK_J_SLASH,				
	TK_J_PERCENT,
	TK_J_HAT,				
	TK_J_AND,				
	TK_J_OR,			 	
	TK_J_TILDE,				
	TK_J_NOT,				
	TK_J_PLUS_EQ,			
	TK_J_MINUS_EQ,			
	TK_J_STAR_EQ,			
	TK_J_SLASH_EQ,			
	TK_J_PERCENT_EQ,
	TK_J_HAT_EQ,			
	TK_J_AND_EQ,			
	TK_J_OR_EQ,				
	TK_J_LTLT,				
	TK_J_GTGT,				
	TK_J_GTGTGT,			
	TK_J_GTGT_EQ,			
	TK_J_LTLT_EQ,			
	TK_J_GTGTGT_EQ,			
	TK_J_EQEQ,				
	TK_J_NE,			 	
	TK_J_LE,			 	
	TK_J_GE,			 	
	TK_J_LANGLE,			
	TK_J_RANGLE,			
	TK_J_ANDAND,			
	TK_J_OROR,				
	TK_J_INCR,				
	TK_J_DECR,
	TK_J_END_OF_FILE
};


static int index_map[sizeof(token_map)/sizeof(token_map[0])];


void CJavaLexer::InitLexer()
{
	m_nSlashCount = 0;

	if (!m_bIndexMapInit)
	{	
		for (int i = 0; i < sizeof(token_map)/sizeof(token_map[0]); i++)
			index_map[GetTokenCode(i)] = i;				

		m_bIndexMapInit = true;
	}
}


int CJavaLexer::ReadToken()
{
	switch (ReadChar())
	{
		case '<':
			switch (ReadChar())
			{
				case '<':
					if (ReadChar() == '=')
						mContext.token = GetTokenCode(IDX_J_LTLT_EQ);
					else
					{
						mContext.token = GetTokenCode(IDX_J_LTLT);
						PutBack();
					}
					break;

				case '=':
					mContext.token = GetTokenCode(IDX_J_LE);
					break;

				default:
					PutBack();
					mContext.token = GetTokenCode(IDX_J_LANGLE);
			}

			break;

		case '>':
			switch (ReadChar())
			{
				case '>':
					switch (ReadChar())
					{
						case '=':
							mContext.token = GetTokenCode(IDX_J_GTGT_EQ);
							break;

						case '>':
							if (ReadChar() == '=')
								mContext.token = GetTokenCode(IDX_J_GTGTGT_EQ);
							else
							{
								PutBack();
								mContext.token = GetTokenCode(IDX_J_GTGTGT);
							}
							break;

						default:
							PutBack();
							mContext.token = GetTokenCode(IDX_J_GTGT);
					}
					break;

				case '=':
					mContext.token = GetTokenCode(IDX_J_GE);
					break;

				default:
					PutBack();
					mContext.token = GetTokenCode(IDX_J_RANGLE);
			}
			break;

		case '(':
			mContext.token = GetTokenCode(IDX_J_LPAREN);
			break;

		case ')':
			mContext.token = GetTokenCode(IDX_J_RPAREN);
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
				mContext.token = GetTokenCode(IDX_J_DOT);
			}
			break;

		case ',':
			mContext.token = GetTokenCode(IDX_J_COMMA);
			break;

		case '[':
			mContext.token = GetTokenCode(IDX_J_LBRACK);
			break;

		case ']':
			mContext.token = GetTokenCode(IDX_J_RBRACK);
			break;

		case '{':
			mContext.token = GetTokenCode(IDX_J_LBRACE);
			break;

		case '}':
			mContext.token = GetTokenCode(IDX_J_RBRACE);
			break;

		case '=':
			if (ReadChar() == '=')
				mContext.token = GetTokenCode(IDX_J_EQEQ);
			else
			{
				PutBack();
				mContext.token = GetTokenCode(IDX_J_EQ);
			}
			break;

		case ':':
			mContext.token = GetTokenCode(IDX_J_COLON);
			break;

		case ';':
			mContext.token = GetTokenCode(IDX_J_SEMIC);
			break;

		case '?':
			mContext.token = GetTokenCode(IDX_J_QUESTION);
			break;

		case '+':
			switch (ReadChar())
			{
				case '+':
					mContext.token = GetTokenCode(IDX_J_INCR);
					break;

				case '=':
					mContext.token = GetTokenCode(IDX_J_PLUS_EQ);
					break;

				default:
					PutBack();
					mContext.token = GetTokenCode(IDX_J_PLUS);	
			}
			break;

		case '-':
			switch (ReadChar())
			{
				case '-':
					mContext.token = GetTokenCode(IDX_J_DECR);
					break;

				case '=':
					mContext.token = GetTokenCode(IDX_J_MINUS_EQ);
					break;

				default:
					PutBack();
					mContext.token = GetTokenCode(IDX_J_MINUS);	
			}
			break;

		case '*':
			if (ReadChar() == '=')
				mContext.token = GetTokenCode(IDX_J_STAR_EQ);
			else
			{
				PutBack();
				mContext.token = GetTokenCode(IDX_J_STAR);
			}
			break;

		case '/':
			switch (ReadChar())
			{
				case '=':
					mContext.token = GetTokenCode(IDX_J_SLASH_EQ);
					break;

				case '/':
					SingleLineComment();
					break;

				case '*':
					MultiLineComment();
					break;

				default:
					PutBack();
					mContext.token = GetTokenCode(IDX_J_SLASH);
			}
			break;

		case '%':
			if (ReadChar() == '=')
				mContext.token = GetTokenCode(IDX_J_PERCENT_EQ);
			else
			{
				PutBack();
				mContext.token = GetTokenCode(IDX_J_PERCENT);
			}
			break;

		case '^':
			if (ReadChar() == '=')
				mContext.token = GetTokenCode(IDX_J_HAT_EQ);
			else
			{
				PutBack();
				mContext.token = GetTokenCode(IDX_J_HAT);
			}
			break;

		case '&':
			switch (ReadChar())
			{
				case '&':
					mContext.token = GetTokenCode(IDX_J_ANDAND);
					break;

				case '=':
					mContext.token = GetTokenCode(IDX_J_AND_EQ);
					break;

				default:
					PutBack();
					mContext.token = GetTokenCode(IDX_J_AND);
			}
			break;

		case '|':
			switch (ReadChar())
			{
				case '|':
					mContext.token = GetTokenCode(IDX_J_OROR);
					break;

				case '=':
					mContext.token = GetTokenCode(IDX_J_OR_EQ);
					break;

				default:
					PutBack();
					mContext.token = GetTokenCode(IDX_J_OR);
			}
			break;

		case '!':
			if (ReadChar() == '=')
				mContext.token = GetTokenCode(IDX_J_NE);
			else
			{
				PutBack();
				mContext.token = GetTokenCode(IDX_J_NOT);
			}
			break;

		case '~':
			mContext.token = GetTokenCode(IDX_J_TILDE);
			break;

		case '\'':
			QuotedLiteral(false);
			break;

		case '"':
			QuotedLiteral(true);
			break;

		default:
			if (DecDigit())
				NumericLiteral();
			else
				if (IdStart())
        		        {
					const KEYWORD *k;

					Identifier();
					k = MatchKeyword();

					if (k != NULL)
					{
						mContext.token = GetTokenCode(k->index);
					}
					else
					{
						mContext.token = GetTokenCode(IDX_J_ID);
						mContext.token_value = mContext.value_buffer;
					}
				}
                		else
				{
					if (IsEOF())
						mContext.token = GetTokenCode(IDX_J_END_OF_FILE);
					else
						throw new CBadTokenException(this);
				}
	}

	return mContext.token;
}


UChar32 CJavaLexer::ReadChar(bool ignore)
{
	UChar32 ch = CLexer::ReadChar(ignore);

	m_bUnicodeEscape = false;

	if (ch == '\\')
	{
		long start = mContext.cur - 1;

		m_nSlashCount++;
 
		if (m_nSlashCount % 2 == 1)
		{
			if (CLexer::ReadChar(ignore) == 'u')
			{
				SYMBOL_SUBST subst;

				if (!ignore)
				{
					Eat();
					Eat();	
				}

				ch = UnicodeEscape();
				subst.cur = mContext.cur;
				subst.len = subst.cur - start;
				mSubstStack.push(subst);

				if (!ignore)
					AppendCharToBuffer(ch);

				m_bCharIgnored = ignore;
				mContext.ch = ch;
				m_nSlashCount = 0;

				m_bUnicodeEscape = true;
			}
			else
			{
				PutBack();
				mContext.ch = '\\';
			}
		}
	}
	else
		m_nSlashCount = 0;

	return ch;
}


void CJavaLexer::QuotedLiteral(bool string)
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
				EscapeSequence();
				break;

			default:
				if (NewLine() && !m_bUnicodeEscape)
					throw new CBadTokenException(this);
		}
	}

	Eat();

	mContext.token = string ? GetTokenCode(IDX_J_STR_LITERAL) : GetTokenCode(IDX_J_CHAR_LITERAL);
	mContext.token_value = mContext.value_buffer;
}


void CJavaLexer::NumericLiteral()
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

					if (mContext.ch != 'L' && mContext.ch != 'l')
						PutBack();

					mContext.token = GetTokenCode(IDX_J_INT_LITERAL);
					mContext.token_value = mContext.value_buffer;
					return;
				}
				else
					throw new CBadTokenException(this);

				break;

			case 'L':
			case 'l':
			case 'F':
			case 'f':
			case 'd':
			case 'D':
			case 'E':
			case 'e':
			case '.':
				break;

			default:
				while (OctalDigit())
					ReadChar();		

				if (mContext.ch != 'L' && mContext.ch != 'l')
					PutBack();

				mContext.token = GetTokenCode(IDX_J_INT_LITERAL);
				mContext.token_value = mContext.value_buffer;
				return;
		}
	}

	while (DecDigit())
		ReadChar();

	switch (mContext.ch)
	{
		case 'L':
		case 'l':
			mContext.token = GetTokenCode(IDX_J_INT_LITERAL);
			mContext.token_value = mContext.value_buffer;
			return;

		case 'F':
		case 'f':
        case 'd':
        case 'D':
			mContext.token = GetTokenCode(IDX_J_FLOAT_LITERAL);
			mContext.token_value = mContext.value_buffer;
			return;

		case '.':
			ReadChar();
            if (DecDigit())
            {
				do
					ReadChar();
				while (DecDigit());
            }
            else
            {
				if (mContext.value_buffer[0] == '.')
            		throw new CBadTokenException(this);
			}
                    
			switch (mContext.ch)
			{
				case 'F':
				case 'f':
				case 'D':
				case 'd':
					mContext.token = GetTokenCode(IDX_J_FLOAT_LITERAL);
					mContext.token_value = mContext.value_buffer;
					return;

				case 'E':
				case 'e':
					break;

				default:
					PutBack();
					mContext.token = GetTokenCode(IDX_J_FLOAT_LITERAL);
					mContext.token_value = mContext.value_buffer;
					return;
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
					break;

				default:
					PutBack();
			}

			mContext.token = GetTokenCode(IDX_J_FLOAT_LITERAL);
			mContext.token_value = mContext.value_buffer;
			break;

		default:
			PutBack();
			mContext.token = GetTokenCode(IDX_J_INT_LITERAL);
			mContext.token_value = mContext.value_buffer;
	}

}


void CJavaLexer::Identifier()
{
	do
	{	
		ReadChar();
	}
	while (IdPart() && !IsEOF());

	if (IsEOF())
	{
		Eat();
		Eat();
	}
	else
		PutBack();

	UnicodeString normalized;
	UErrorCode err = U_ZERO_ERROR;

	Normalizer::normalize(mContext.value_buffer, UNORM_NFC, 0, normalized, err);

	if (U_FAILURE(err))
		throw new CBadTokenException(this);

	mContext.value_buffer = normalized;
}


void CJavaLexer::EscapeSequence()
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


UChar CJavaLexer::UnicodeEscape()
{
	UnicodeString buf;

	while (CLexer::ReadChar(true) == 'u');
	PutBack();
	
	for (int i = 0; i < 4; i++)
	{
		CLexer::ReadChar(true);

		if (!HexDigit())
			throw new CBadTokenException(this);

		buf.append(mContext.ch);
	}

	return (UChar)wcstol((wchar_t*)buf.getTerminatedBuffer(), NULL, 16);
}


void CJavaLexer::SingleLineComment()
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


void CJavaLexer::MultiLineComment()
{
	bool exit = false;

	while (!exit)
	{
		switch (ReadChar(true)) 
		{
			case '\0':
				if (IsEOF())
					throw new CBadTokenException(this);

			case '*':
				if (ReadChar(true) == '/')
					exit = true;
				else
					PutBack();
		}
	}
}


bool CJavaLexer::DecDigit()
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


bool CJavaLexer::NonDigit()
{

	switch (mContext.ch)
	{
		case '$':
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


bool CJavaLexer::OctalDigit()
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


bool CJavaLexer::HexDigit()
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


bool CJavaLexer::IdStart()
{
	if (u_isIDStart(mContext.ch))
		return true;
	else
	{
		switch (u_charType(mContext.ch))
		{
			case U_CONNECTOR_PUNCTUATION:
			case U_CURRENCY_SYMBOL :
				return true;

			default:
				return false;
		}
	}
}


bool CJavaLexer::IdPart()
{
	if (u_isIDPart(mContext.ch))
		return true;
	else
	{
		if (u_charType(mContext.ch) == U_CURRENCY_SYMBOL)
			return true;
		else
			return false;
	}
}


void CJavaLexer::SkipWhiteSpaces()
{
	long head;

	while (!IsEOF())
	{
		switch (ReadChar(true))
		{
			case '/':
				switch (ReadChar(true))
				{
					case '/':
						head = mContext.cur - 2;
						SingleLineComment();
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

			case '\0':
				if (IsEOF())
					return;
				break;

			default:
				if (!u_isWhitespace(mContext.ch))
				{
					PutBack();
					return;
				}
		}
	}
}


bool CJavaLexer::NewLine()
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


int CJavaLexer::GetTokenCode(int index)
{
	return token_map[index];
}


int CJavaLexer::GetTokenIndex(int token)
{
	return index_map[token];
}


#define TOTAL_KEYWORDS 50
#define MIN_WORD_LENGTH 2
#define MAX_WORD_LENGTH 12
#define MIN_HASH_VALUE 2
#define MAX_HASH_VALUE 82
/* maximum key range = 81, duplicates = 0 */



inline unsigned int CJavaLexer::GetHash (register const UChar *str, register unsigned int len)
{
	static const unsigned char asso_values[] =
	{
		83, 83, 83, 83, 83, 83, 83, 83, 83, 83,
		83, 83, 83, 83, 83, 83, 83, 83, 83, 83,
		83, 83, 83, 83, 83, 83, 83, 83, 83, 83,
		83, 83, 83, 83, 83, 83, 83, 83, 83, 83,
		83, 83, 83, 83, 83, 83, 83, 83, 83, 83,
		83, 83, 83, 83, 83, 83, 83, 83, 83, 83,
		83, 83, 83, 83, 83, 83, 83, 83, 83, 83,
		83, 83, 83, 83, 83, 83, 83, 83, 83, 83,
		83, 83, 83, 83, 83, 83, 83, 83, 83, 83,
		83, 83, 83, 83, 83, 83, 83, 10, 15, 25,
		0, 55, 10, 15, 83,  5,  5, 83, 40, 10,
		0, 20,  5, 50,  5,  0,  0, 20, 45, 30,
		20, 83,  0, 83, 83, 83, 83, 83, 83, 83,
		83, 83, 83, 83, 83, 83, 83, 83, 83, 83,
		83, 83, 83, 83, 83, 83, 83, 83, 83, 83,
		83, 83, 83, 83, 83, 83, 83, 83, 83, 83,
		83, 83, 83, 83, 83, 83, 83, 83, 83, 83,
		83, 83, 83, 83, 83, 83, 83, 83, 83, 83,
		83, 83, 83, 83, 83, 83, 83, 83, 83, 83,
		83, 83, 83, 83, 83, 83, 83, 83, 83, 83,
		83, 83, 83, 83, 83, 83, 83, 83, 83, 83,
		83, 83, 83, 83, 83, 83, 83, 83, 83, 83,
		83, 83, 83, 83, 83, 83, 83, 83, 83, 83,
		83, 83, 83, 83, 83, 83, 83, 83, 83, 83,
		83, 83, 83, 83, 83, 83, 83, 83, 83, 83,
		83, 83, 83, 83, 83, 83, 83
	};
	register int hval = len;

	switch (hval)
	{
		default:
			 hval += asso_values[(unsigned char)str[2]+1];
		/*FALLTHROUGH*/
		case 2:
		case 1:
			hval += asso_values[(unsigned char)str[0]];
			break;
	}
	return hval;
}

static const struct KEYWORD wordlist[] =
{
	{(char*)0}, {(char*)0},
	{"do", 11},
	{"try", 46},
	{(char*)0},
	{"throw", 42},
	{"throws", 43},
	{"if", 20},
	{"strictfp", 37},
	{"this", 41},
	{"short", 35},
	{"switch", 39},
	{"package", 30},
	{"for", 19},
	{"null", 29},
	{"instanceof", 23},
	{"assert", 1},
	{"private", 31},
	{"abstract", 0},
	{"protected", 32},
	{"float", 18},
	{"static", 36},
	{"default", 10},
	{"new", 28},
	{"transient", 44},
	{"false", 15},
	{"native", 27},
	{"boolean", 2},
	{"int", 24},
	{"case", 5},
	{"break", 3},
	{"return", 34},
	{"synchronized", 40},
	{(char*)0},
	{"interface", 25},
	{"final", 16},
	{"public", 33},
	{"finally", 17},
	{(char*)0},
	{"byte", 4},
	{"while", 49},
	{(char*)0}, {(char*)0}, {(char*)0},
	{"char", 7},
	{"class", 8},
	{(char*)0}, {(char*)0}, {(char*)0},
	{"true", 45},
	{"catch", 6},
	{"double", 12},
	{(char*)0},
	{"continue", 9},
	{"void", 47},
	{"super", 38},
	{(char*)0}, {(char*)0}, {(char*)0},
	{"else", 13},
	{(char*)0},
	{"import", 22},
	{(char*)0},
	{"volatile", 48},
	{"long", 26},
	{"implements", 21},
	{(char*)0}, {(char*)0}, {(char*)0}, {(char*)0},
	{(char*)0}, {(char*)0}, {(char*)0}, {(char*)0},
	{(char*)0}, {(char*)0}, {(char*)0}, {(char*)0},
	{(char*)0}, {(char*)0}, {(char*)0}, {(char*)0},
	{"extends", 14}
};


const KEYWORD *CJavaLexer::MatchKeyword()
{
	register const UChar *str = mContext.value_buffer.getBuffer();
	unsigned int len = mContext.value_buffer.length();

	if (len <= MAX_WORD_LENGTH && len >= MIN_WORD_LENGTH)
	{
		register int key = GetHash(str, len);

		if (key <= MAX_HASH_VALUE && key >= 0)
		{
			register const char *s = wordlist[key].name;

			if (s && mContext.value_buffer == s)
				return &wordlist[key];
		}
	}
	return 0;
}
