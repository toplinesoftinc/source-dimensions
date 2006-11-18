#ifndef __BNF2XSD_H__
#define __BNF2XSD_H__

#include <list>
#include <string>
#include <fstream>
#include <set>
#include <map>

using namespace std;


enum 
{
	SFX_NONE,
	SFX_QUESTION,
	SFX_STAR,
	SFX_PLUS
};


struct SExpression;
struct SDefinition;
struct SMacroCtx;

class CBnf2Xsd
{
public:
	void ParseFile(char *filename);
	void WriteXsd(char *filename);
	void WriteList(char *filename);

protected:
	void ParseExpr(SExpression *pExpr, bool subexpr = false);
	void WriteExpr(fstream &fs, SExpression *pExpr, int level, int suffix = SFX_NONE);

	int GetNextToken();
	char GetNextChar();
	void SkipWhiteSpaces();
	void PutBack(char ch);
	
	bool IsTermName(string &name);

	fstream m_InputStream;
	int m_nLine;
	char m_chCurChar;
	int m_nCurToken;
	string m_TokenValue;
	set<string> m_DefNameTable, m_NtNameTable, m_TermNameTable;
	list<SDefinition*> m_DefList;
	list< list<char> > m_PutBackBuf;
	list<SMacroCtx*> m_CtxStack;
	list<int> m_LinePosStack;
	map<string, string> m_MacroDefTable;
	map<string, int> m_MacroDefStart;
};


#endif // __BNF2XSD_H__

