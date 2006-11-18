#ifndef __SYMBOL_H__
#define __SYMBOL_H__


#include <list>
#include "lexer.h"

class CParser;


class CSymbol
{
public:
	CSymbol() { }
	CSymbol(CLexer &lexer);
	CSymbol(int nt);

	void Serialize(CParser *parser, ostream &ostr, const char *filename);

	~CSymbol();

	int code;
	int state;
	bool terminal;
	long left, right;
	CSymbol *parent;
	UnicodeString *value;
	list<CSymbol*> *children;

protected:
	void SerializeNode(CParser *parser, ostream &ostr);
};


#endif // __SYMBOL_H__
