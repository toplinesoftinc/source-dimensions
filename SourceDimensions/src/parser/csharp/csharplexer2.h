#ifndef __CSHARPLEXER2_H__
#define __CSHARPLEXER2_H__


#include "csharplexer.h"

enum
{
	IDX_CS_WHITESPACE = IDX_CS_END_OF_FILE + 1,
	IDX_CS_PARTIAL,
	IDX_CS_ALIAS,
	IDX_CS_WHERE,
	IDX_CS_YIELD,
	IDX_CS_SCOPE,
	IDX_CS_QQ
};


class CSharpLexer2 : public CSharpLexer
{
public:
	CSharpLexer2() : rangle(false) { }

	virtual int GetTokenCode(int index);
	virtual int GetTokenIndex(int token);

protected:
	virtual void InitLexer();
	virtual void ParsePP();

	virtual bool PreProcess();
	virtual void PostProcess();

	bool rangle;
};


#endif	// __CSHARPLEXER2_H__
