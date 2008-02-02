#ifndef __LEXER_H__
#define __LEXER_H__


#include <string>
#include <stack>
#include <iostream>

#include "unicode/unistr.h"
#include "unicode/utypes.h"

using namespace std;


#include <fstream>

#define CONV_BUF_SIZE	1024 * 512


struct CONTEXT_BASE
{
	long pos;

	long left, right, cur, line, col, tab_num;
	int token;
	UnicodeString value_buffer, token_value;
};


struct LEX_CONTEXT : public CONTEXT_BASE
{
	UChar32 ch;
	int target_cur;
	bool buf_overflow;
	char *prev_bound;
	char *source_bound;
	int source_len;
	int target_frame_id;
	bool eof;
};
 

struct SYMBOL_SUBST
{
	long cur;
	int len;
};


struct KEYWORD
{
	char *name;
	int index;
};


class CLexer
{
public:
	CLexer();

	void SetInputStream(istream &istr);
	virtual istream *GetInputStream();

	int GetCurToken() const;
	virtual int GetNextToken();
	const UnicodeString &GetTokenValue() { return mContext.token_value; }
	const UnicodeString &GetTokenBuffer() { return mContext.value_buffer; }

	virtual int LookAhead(unsigned int pos = 1);

	long GetLeftPos() const;
	long GetRightPos() const;
	long GetLineNum() const;
	const char *GetEncoding() const;
	virtual bool IsEOF();

	virtual void Mark();
	virtual void Unmark();
	virtual void Rollback();

	virtual int GetTokenCode(int index) = 0;
	virtual int GetTokenIndex(int token) { return -1; }

	virtual ~CLexer();

protected:
	virtual void InitLexer() = 0;
	virtual int ReadToken() = 0;
	virtual void SkipWhiteSpaces() = 0;

	virtual bool PreProcess();
	virtual void PostProcess();

	virtual UChar32 ReadChar(bool ignore = false);
	virtual bool Match(const char* match);
	virtual void PutBack();
	virtual void Eat();

	void AppendCharToBuffer(UChar32 ch);

	UChar32 GetNextChar();
	virtual void ReadBlock();

	const char* m_chEncoding;
	istream *m_pStream;
	bool m_bCharIgnored;
	bool m_bLastBlock;
	int m_nSignatureLength;
	short m_nCurBufIndex;
	bool m_bReuseBuffer;
	LEX_CONTEXT mContext;
	UConverter *m_pConverter;
	char m_chSourceBuf[CONV_BUF_SIZE];
	UChar m_chTargetBuf[2][CONV_BUF_SIZE];
	UChar *m_pTargetBound[2];
	UChar *m_pTargetBuf;
	UChar **m_ppTargetBound;
	std::stack<LEX_CONTEXT> mCtxStack;
	std::stack<SYMBOL_SUBST> mSubstStack;
};

#endif  // __LEXER_H__
