#ifndef __JAVALEXER5_H__
#define __JAVALEXER5_H__

#include "javalexer.h"


enum
{
	IDX_J_WHITESPACE = IDX_J_END_OF_FILE + 1,
	IDX_J_ELLIPSIS,
	IDX_J_AT,
	IDX_J_ENUM
};


class CJavaLexer5 : public CJavaLexer
{
public:
	CJavaLexer5() : rangle(false) { }

	virtual int GetTokenCode(int index);
	virtual int GetTokenIndex(int token);

protected:
	virtual void InitLexer();

	virtual bool PreProcess();
	virtual void PostProcess();

	inline void HexLiteral();

	bool rangle;
};

#endif // __JAVALEXER5_H__
