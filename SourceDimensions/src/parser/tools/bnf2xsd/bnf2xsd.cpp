#include "bnf2xsd.h"
#include "exception.h"

#include <iomanip>



#define END_OF_FILE (-1)
#define SUBEXPRESSION true


static string str_buf;

static char xsd_header[] = 
	"<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
	"<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">\n\n"
	"<xs:element name=\"SourceFile\" type=\"SourceFile\" />\n\n"
	"<xs:complexType name=\"AstNode\">\n"
	"\t<xs:attribute name=\"l\" type=\"xs:nonNegativeInteger\" use=\"required\" />\n"
	"\t<xs:attribute name=\"r\" type=\"xs:nonNegativeInteger\" use=\"required\" />\n"
	"</xs:complexType>\n\n"
	"<xs:complexType name=\"Terminal\">\n"
	"\t<xs:complexContent>\n"
	"\t\t<xs:extension base=\"AstNode\">\n"
	"\t\t\t<xs:attribute name=\"val\" type=\"xs:string\" use=\"optional\" />\n"
	"\t\t</xs:extension>\n"
	"\t</xs:complexContent>\n"
	"</xs:complexType>\n\n";

static char xsd_footer[] = "\n</xs:schema>";

enum
{
	TOK_IDENTIFIER,
	TOK_ARROW,
	TOK_QUESTION,
	TOK_STAR,
	TOK_PLUS,
	TOK_OR,
	TOK_LPAREN,
	TOK_RPAREN,
	TOK_EOF
};


struct SMacroCtx
{
	SMacroCtx() : pos(0) { }

	string m_Text;
	size_t pos;
};


struct STerm
{
	STerm() : m_nSuffix(SFX_NONE), m_pNestExpr(NULL) { }
	STerm(string &name) : m_nSuffix(SFX_NONE), m_Name(name), m_pNestExpr(NULL) { }
	STerm(SExpression *expr) : m_nSuffix(SFX_NONE), m_pNestExpr(expr) { }

	int m_nSuffix;
	string m_Name;
	SExpression *m_pNestExpr;
};


struct SSequence
{
	list<STerm*> m_TermList;
};


struct SExpression
{
	list<SSequence*> m_SeqList;	
};


struct SDefinition
{
	SDefinition() { }
	SDefinition(string &name) : m_LhsName(name) { }

	string m_LhsName;
	SExpression m_Expr;
};


void CBnf2Xsd::ParseFile(char *filename)
{
	m_InputStream.open(filename, ios_base::in | ios_base::binary);

	if (!m_InputStream.is_open())
		throw new CFileOpenException(filename);

	m_nLine = 1;


	m_PutBackBuf.push_back(list<char>());

	try
	{
		GetNextToken();

		if (m_nCurToken != TOK_IDENTIFIER)
		{
			if (m_nCurToken == TOK_EOF)
				return;
			else
				throw new CUnexpTokenException(m_nLine, m_nCurToken);
		}

		m_DefList.push_back(new SDefinition(m_TokenValue));
		m_DefNameTable.insert(m_TokenValue);
		m_NtNameTable.insert(m_TokenValue);

		if (GetNextToken() != TOK_ARROW)
			throw new CUnexpTokenException(m_nLine, m_nCurToken);

		while (m_nCurToken != TOK_EOF)
			ParseExpr(&m_DefList.back()->m_Expr);
	}
	catch(...)
	{
		m_InputStream.close();
		throw;
	}

	m_InputStream.close();
}


void CBnf2Xsd::WriteXsd(char *filename)
{
	fstream fs;

	fs.open(filename, ios_base::out | ios_base::trunc);

	if (!fs.is_open())
		throw new CFileOpenException(filename);

	fs << xsd_header;
	
	if (m_DefList.size() > 0)
	{
		fs << "<xs:complexType name=\"SourceFile\">\n";
		fs << "\t<xs:sequence>\n";
		fs << "\t\t<xs:element name=\"" << m_DefList.front()->m_LhsName << 
					"\" type=\"" << m_DefList.front()->m_LhsName << "\" minOccurs=\"0\"/>\n";
		fs << "\t</xs:sequence>\n";
		fs << "\t<xs:attribute name=\"language\" type=\"xs:string\" use=\"optional\" />\n";
		fs << "\t<xs:attribute name=\"file\" type=\"xs:string\" use=\"required\" />\n";
		fs << "\t<xs:attribute name=\"encoding\" type=\"xs:string\" use=\"required\" />\n";
		fs << "\t<xs:attribute name=\"delete\" type=\"xs:boolean\" use=\"optional\" />\n";
		fs << "\t<xs:attribute name=\"binary\" type=\"xs:boolean\" use=\"optional\" />\n";
		fs << "</xs:complexType>\n\n";

		for (list<SDefinition*>::iterator i = m_DefList.begin(); i != m_DefList.end(); i++)
		{
			fs << "<xs:complexType name=\"" << (*i)->m_LhsName << "\" >\n";
			fs << "\t<xs:complexContent>\n";
			fs << "\t\t<xs:extension base=\"AstNode\">\n";

			WriteExpr(fs, &(*i)->m_Expr, 0);
			
			fs << "\t\t</xs:extension>\n";
			fs << "\t</xs:complexContent>\n";
			fs << "</xs:complexType>\n\n";
		}
	}

	fs << xsd_footer;

	fs.close();
}


void CBnf2Xsd::WriteList(char *filename)
{
	fstream fs;

	fs.open(filename, ios_base::out | ios_base::trunc);

	if (!fs.is_open())
		throw new CFileOpenException(filename);

	fs << "/*********************** Non-terminals ***************************/" << endl << endl;

	for (set<string>::iterator i = m_NtNameTable.begin(); i != m_NtNameTable.end(); i++)
		fs << *i << endl;

	fs << endl << endl << endl;

	fs << "/************************** Terminals ****************************/" << endl << endl;

	for (set<string>::iterator i = m_TermNameTable.begin(); i != m_TermNameTable.end(); i++)
		fs << *i << endl;


	fs.close();
}


void CBnf2Xsd::ParseExpr(SExpression *pExpr, bool subexpr)
{
	bool middle = !subexpr;
	int tok;
	string tokval;

	for (;;)
	{
		GetNextToken();

curtok:
		switch (m_nCurToken)
		{
			case TOK_EOF:
				if (middle)
					throw new CUnexpEofException();
				else
					return;

			case TOK_RPAREN:
				if (subexpr && !middle)
					return;
				else
					throw new CUnexpTokenException(m_nLine, m_nCurToken);

			case TOK_OR:
				pExpr->m_SeqList.push_back(new SSequence());
				middle = true;
				continue;

			case TOK_LPAREN:
			case TOK_IDENTIFIER:
				tok = m_nCurToken;
				tokval = m_TokenValue;
				if (m_nCurToken == TOK_IDENTIFIER && GetNextToken() == TOK_ARROW)
				{
					if (middle || subexpr)
						throw new CUnexpTokenException(m_nLine, m_nCurToken);
					else
					{
						middle = true;
						m_DefList.push_back(new SDefinition(tokval));

						if (m_DefNameTable.find(tokval) != m_DefNameTable.end())
							throw new CDupDefException(m_nLine, tokval);
						else
						{
							m_DefNameTable.insert(tokval);
							m_NtNameTable.insert(tokval);
						}

						return;
					}
				}
				else
				{
					if (pExpr->m_SeqList.size() == 0)
						pExpr->m_SeqList.push_back(new SSequence());

					list<STerm*> *term_list = &pExpr->m_SeqList.back()->m_TermList;

					if (tok == TOK_IDENTIFIER)
					{
						if (IsTermName(tokval))
							m_TermNameTable.insert(tokval);
						else
							m_NtNameTable.insert(tokval);

						term_list->push_back(new STerm(tokval));
					}
					else
					{
						STerm *term = new STerm(new SExpression());
						term_list->push_back(term);
						ParseExpr(term->m_pNestExpr, SUBEXPRESSION);
						GetNextToken();
					}
				
					middle = false;

					switch (m_nCurToken)
					{
						case TOK_LPAREN:
						case TOK_RPAREN:
						case TOK_EOF:
						case TOK_IDENTIFIER:
							middle = false;
							goto curtok;

						case TOK_OR:
							middle = true;
							goto curtok;

						case TOK_STAR:
							term_list->back()->m_nSuffix = SFX_STAR;
							break;

						case TOK_PLUS:
							term_list->back()->m_nSuffix = SFX_PLUS;
							break;

						case TOK_QUESTION:
							term_list->back()->m_nSuffix = SFX_QUESTION;
							break;

						default:
							throw new CUnexpTokenException(m_nLine, m_nCurToken);
					}
				}
				break;

			default:
				throw new CUnexpTokenException(m_nLine, m_nCurToken);
		}
	}
}


void CBnf2Xsd::WriteExpr(fstream &fs, SExpression *pExpr, int level, int suffix)
{
	fs << str_buf.assign(level + 3, '\t') << "<xs:choice ";

	switch (suffix)
	{
		case SFX_QUESTION:
			fs << "minOccurs=\"0\" maxOccurs=\"1\" ";
			break;

		case SFX_STAR:
			fs << "minOccurs=\"0\" maxOccurs=\"unbounded\" ";
			break;

		case SFX_PLUS:
			fs << "minOccurs=\"1\" maxOccurs=\"unbounded\" ";
	}

	fs << ">\n";

	for (list<SSequence*>::iterator i = pExpr->m_SeqList.begin(); i != pExpr->m_SeqList.end(); i++)
	{
		fs << str_buf.assign(level + 4, '\t') << "<xs:sequence>\n";

		for (list<STerm*>::iterator j = (*i)->m_TermList.begin(); j != (*i)->m_TermList.end(); j++)
		{
			if (!(*j)->m_pNestExpr)
			{
				fs << str_buf.assign(level + 5, '\t') << "<xs:element ";
				fs << "name=\"" << (*j)->m_Name << "\" ";

				switch ((*j)->m_nSuffix)
				{
					case SFX_NONE:
						fs << "minOccurs=\"1\" maxOccurs=\"1\" ";
						break;

					case SFX_QUESTION:
						fs << "minOccurs=\"0\" maxOccurs=\"1\" ";
						break;

					case SFX_STAR:
						fs << "minOccurs=\"0\" maxOccurs=\"unbounded\" ";
						break;

					case SFX_PLUS:
						fs << "minOccurs=\"1\" maxOccurs=\"unbounded\" ";
				}

				set<string>::iterator i = m_DefNameTable.find((*j)->m_Name);

				if (i == m_DefNameTable.end())
				{
					if (IsTermName((*j)->m_Name))
						fs << "type=\"Terminal\" />\n";
					else
						fs << "type=\"AstNode\" />\n";
				}
				else
					fs << "type=\"" << (*j)->m_Name << "\" />\n";
			}
			else
				WriteExpr(fs, (*j)->m_pNestExpr, level + 2, (*j)->m_nSuffix);
		}

		fs << str_buf.assign(level + 4, '\t') << "</xs:sequence>\n";
	}

	fs << str_buf.assign(level + 3, '\t') << "</xs:choice>\n";
}


int CBnf2Xsd::GetNextToken()
{
	for (;;)
	{
		m_TokenValue.erase();
		SkipWhiteSpaces();

		switch (GetNextChar())
		{
			case '-':
				if (GetNextChar() == '>')
					m_nCurToken = TOK_ARROW;
				else
					throw new CUnexpCharException(m_nLine, m_chCurChar);
				break;

			case '*': 
				m_nCurToken = TOK_STAR;
				break;

			case '?':
				m_nCurToken = TOK_QUESTION;
				break;

			case '+':
				m_nCurToken = TOK_PLUS;
				break;

			case '|':
				m_nCurToken = TOK_OR;
				break;

			case '(':
				m_nCurToken = TOK_LPAREN;
				break;

			case ')':
				m_nCurToken = TOK_RPAREN;
				break;

			case '@':
				if (GetNextChar() == '@')
				{
					string textbuf;
					int pos;

					if (GetNextToken() != TOK_IDENTIFIER)
						throw new CInvalidMacroDefException(m_nLine);
					
					SkipWhiteSpaces();

					if (GetNextChar() != '{')
						throw new CInvalidMacroDefException(m_nLine);

					pos = m_nLine;

					while (GetNextChar() != '}')
					{
						if (m_chCurChar == END_OF_FILE)
							throw new CUnexpEofException();
						textbuf += m_chCurChar;
					}

					m_MacroDefTable[m_TokenValue] = textbuf;
					m_MacroDefStart[m_TokenValue] = pos;
				}
				else
				{
					PutBack(m_chCurChar);

					if (GetNextToken() != TOK_IDENTIFIER)
						throw new CInvalidMacroRefException(m_nLine);

					map<string, string>::iterator p;
					
					p = m_MacroDefTable.find(m_TokenValue);

					if (p == m_MacroDefTable.end())
						throw new CInvalidMacroRefException(m_nLine);

					SMacroCtx *ctx = new SMacroCtx();
					ctx->m_Text = p->second;
					m_CtxStack.push_back(ctx);
					m_LinePosStack.push_back(m_nLine);
					m_nLine = m_MacroDefStart[m_TokenValue];
					m_PutBackBuf.push_back(list<char>());
				}
				continue;

			case END_OF_FILE:
				m_nCurToken = TOK_EOF;
				break;

			default:
				while (isdigit(m_chCurChar) || isalpha(m_chCurChar) || m_chCurChar == '_')
				{
					m_TokenValue += m_chCurChar;
					GetNextChar();
				}

				if (m_TokenValue.length() == 0)
					throw new CUnexpCharException(m_nLine, m_chCurChar);
				
				PutBack(m_chCurChar);

				m_nCurToken = TOK_IDENTIFIER;
		}

		return m_nCurToken;
	}
}


char CBnf2Xsd::GetNextChar()
{
	if (m_PutBackBuf.back().size() > 0)
	{
		m_chCurChar = m_PutBackBuf.back().back();
		m_PutBackBuf.back().pop_back();
	}
	else
	{
		bool read = true;

		while (m_CtxStack.size() > 0)
		{
			if (m_CtxStack.back()->pos >= m_CtxStack.back()->m_Text.length())
			{
				delete m_CtxStack.back();
				m_CtxStack.pop_back();
				m_PutBackBuf.pop_back();
				m_nLine = m_LinePosStack.back();		
				m_LinePosStack.pop_back();

				return GetNextChar();
			}
			else
			{
				m_chCurChar = m_CtxStack.back()->m_Text[m_CtxStack.back()->pos++];
				read = false;
				break;
			}
		}

		if (read)
			m_InputStream.read(&m_chCurChar, 1);
	}

	if (m_chCurChar == '\n')
		m_nLine++;

	if (m_InputStream.eof() && m_CtxStack.size() == 0)
		m_chCurChar = END_OF_FILE;

	return m_chCurChar;
}


void CBnf2Xsd::SkipWhiteSpaces()
{
	for (;;)
	{
		switch (GetNextChar())
		{
			case '\n':
			case '\r':
			case '\t':
			case ' ':
				continue;

			case '/':
				switch (GetNextChar())
				{
					case '*':
						for (;;)
						{	
							GetNextChar();
							if (m_chCurChar == '*')
							{
								if (GetNextChar() == '/')
									break;
							}
							else if (m_chCurChar == END_OF_FILE)
								throw new CUnexpEofException();
						}
						break;

					case '/':
						for (;;)
						{
							GetNextChar();
							if (m_chCurChar == '\n' || m_chCurChar == END_OF_FILE)
								break;
						}
				}
				break;

			default:
				PutBack(m_chCurChar);
				return;
		}
	}
}


void CBnf2Xsd::PutBack(char ch)
{
	m_PutBackBuf.back().push_back(ch);
}


bool CBnf2Xsd::IsTermName(string &name)
{
	for (size_t i = 0; i < name.length(); i++)
	{
		if (islower(name[i]))
			return false;
	}

	return true;
}
