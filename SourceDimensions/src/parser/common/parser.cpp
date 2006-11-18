#include "parser.h"
#include "exceptions.h"
#include "symbol.h"
#include <vector>


#define INIT_STACK_SIZE	256


struct AmbigRec
{
	int action;
	CSymbol* symbol;
};


enum
{
	ERR_UNEXPECTED_TOKEN,
	ERR_UNEXPECTED_EOF
};


void CParser::Parse(istream &istr, ostream *ostr, const char* infilename)
{
	std::vector<CSymbol*> parse_stack;
	std::vector<AmbigRec> ambig_stack;
	int action = GetStartState(); 
	int alt, left, right;
	CException *last_err = NULL;
	int last_err_type;
	CLexer *lexer = CreateLexer();
	CSymbol *sym;
	bool reduce = false;

	parse_stack.reserve(INIT_STACK_SIZE);
	parse_stack.push_back(new CSymbol(0));
	parse_stack.back()->state = GetStartState();

	lexer->SetInputStream(istr);

	for (;;)
	{
		if (!reduce)
			lexer->GetNextToken();

		action = GetAction(parse_stack.back()->state, lexer->GetCurToken(), lexer);

		if (GetAltAction(lexer, GetOriginalStateCode(parse_stack.back()->state), action, alt))
		{
			AmbigRec rec;

			rec.action = alt;
			rec.symbol = parse_stack.back();

			lexer->Mark();
			ambig_stack.push_back(rec);
		}

sw:
		reduce = (GetClass(action) == CCS_REDUCE);

		switch (GetClass(action))
		{
			case CCS_SHIFT:
				parse_stack.push_back(new CSymbol(*lexer));
				parse_stack.back()->state = action;
				break;

			case CCS_REDUCE:
				sym = new CSymbol(GetLHS(action));
				right = left = parse_stack.back()->right;
				for (int i = GetRHS(action); i > 0; i--)
				{
					if (i == 1)
						left = parse_stack.back()->left;

					parse_stack.back()->parent = sym;
					sym->children->push_front(parse_stack.back());	
					parse_stack.pop_back();

					if (parse_stack.size() == 0 && i < 1)
						throw new CParseStackUnderflowException();
				}
				
				sym->left = left;
				sym->right = right;
				sym->state = GetGoto(parse_stack.back()->state, GetLHS(action));
				parse_stack.push_back(sym);
				break;

			case CCS_ERROR:
				if (ambig_stack.size() > 0)
				{
					CSymbol *cur;

					if (lexer->IsEOF())
					{
						if (last_err && last_err->m_nLeft < lexer->GetLeftPos())
						{
							*last_err = CUnexpectedEndOfFileException(GetOriginalStateCode(parse_stack.back()->state));
							last_err_type = ERR_UNEXPECTED_EOF;
						}
						else if (!last_err)
						{
							last_err = new CUnexpectedEndOfFileException(GetOriginalStateCode(parse_stack.back()->state));
							last_err_type = ERR_UNEXPECTED_EOF;
						}
					}
					else
					{
						if (last_err && last_err->m_nLeft < lexer->GetLeftPos())
						{
							*last_err = CUnexpectedTokenException(lexer, GetOriginalStateCode(parse_stack.back()->state));
							last_err_type = ERR_UNEXPECTED_TOKEN;
						}
						else if (!last_err)
						{
							last_err = new CUnexpectedTokenException(lexer, GetOriginalStateCode(parse_stack.back()->state));
							last_err_type = ERR_UNEXPECTED_TOKEN;
						}
					}

					lexer->Rollback();

					action = ambig_stack.back().action;

					while (parse_stack.back() != ambig_stack.back().symbol)
					{
						cur = parse_stack.back();
						parse_stack.pop_back();

						if (!cur->terminal)
						{
							while (cur->children->size() > 0 && parse_stack.back() != ambig_stack.back().symbol)
							{
								parse_stack.push_back(cur->children->front());
								cur->children->pop_front();
							}
						}
					}

					ambig_stack.pop_back();
					goto sw;
				}
				else
				{
					if (last_err && last_err->m_nLeft > lexer->GetLeftPos())
					{
						if (last_err_type == ERR_UNEXPECTED_TOKEN)
							throw static_cast<CUnexpectedTokenException*>(last_err);
						else
							throw static_cast<CUnexpectedEndOfFileException*>(last_err);
					}
					else
					{
						if (lexer->IsEOF())
							throw new CUnexpectedEndOfFileException(GetOriginalStateCode(parse_stack.back()->state));
						else
							throw new CUnexpectedTokenException(lexer, GetOriginalStateCode(parse_stack.back()->state));
					}
				}

			case CCS_ACCEPT:
				if (last_err)
					delete last_err;

				if (ostr)
					parse_stack.back()->Serialize(this, *ostr, infilename);

				delete lexer;
				delete parse_stack.back();

				return;
		}		
	}
}
