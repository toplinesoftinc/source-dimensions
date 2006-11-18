#ifndef __PARSER_H__
#define __PARSER_H__

#include "lexer.h"

enum
{
	CCS_SHIFT,
	CCS_REDUCE,
	CCS_ERROR,
	CCS_ACCEPT
};


enum
{
	LANG_JAVA,
	LANG_JAVA5,
	LANG_CSHARP,
	LANG_CSHARP2
};


class CSymbol;


class CParser
{
public:
	void Parse(istream &istr, ostream *ostr, const char *infilename);

	virtual char *GetTermName(int code) = 0;
	virtual char *GetNtName(int code) = 0;

	virtual bool FilterCallback(CSymbol *sym) { return true; }

	virtual int GetLanguageCode() = 0;

protected:
	virtual int GetAction(int state, int term, CLexer *lexer) = 0;
	virtual int GetGoto(int state, int nt) = 0;
	virtual int GetClass(int code) = 0;
	virtual int GetLHS(int prod) = 0;
	virtual int GetRHS(int prod) = 0;
	virtual int GetStartState() = 0;
	virtual int GetOriginalStateCode(int state) = 0;
	virtual bool GetAltAction(CLexer *lexer, int state, int &cur, int &alt) { return false; }

	virtual CLexer* CreateLexer() = 0;
};


#endif // __PARSER_H__
