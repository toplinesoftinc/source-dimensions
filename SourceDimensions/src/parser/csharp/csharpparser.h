#ifndef __CSHARPPARSER_H__
#define __CSHARPPARSER_H__

#include "../common/parser.h"


class CSharpParser : public CParser
{
public:
	virtual char *GetTermName(int code);
	virtual char *GetNtName(int code);

	virtual bool FilterCallback(CSymbol *sym);

	virtual int GetLanguageCode() { return LANG_CSHARP; }

protected:
	virtual int GetAction(int state, int term, CLexer *lexer);
	virtual int GetGoto(int state, int nt);
	virtual int GetClass(int code);
	virtual int GetLHS(int prod);
	virtual int GetRHS(int prod);
	virtual int GetStartState();
	virtual int GetOriginalStateCode(int state);
	virtual bool GetAltAction(CLexer *lexer, int state, int &cur, int &alt);

	virtual CLexer *CreateLexer();
};

#endif // __CSHARPPARSER_H__
