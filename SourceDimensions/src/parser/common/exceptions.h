#ifndef __EXCEPTIONS_H__
#define __EXCEPTIONS_H__


class CLexer;


class CException
{
public:
	CException() { }
	CException(CLexer *pLexer) : 
		m_nLine(pLexer->GetLineNum()),
		m_sToken(pLexer->GetTokenBuffer()),
		m_nTermCode(pLexer->GetCurToken()),
		m_nLeft(pLexer->GetLeftPos()), 
		m_nRight(pLexer->GetRightPos()) { }

	long m_nLine, m_nLeft, m_nRight;
	UnicodeString m_sToken;
	int m_nTermCode;
};


class CInvalidPPExprException : public CException
{
public:
	CInvalidPPExprException(CLexer *pLexer) : CException(pLexer) { }
};


class CBadTokenException : public CException
{
public:
	CBadTokenException(CLexer *pLexer) : CException(pLexer) { }
};


class CFileOpenErrorException : public CException
{
public:
	CFileOpenErrorException(const char *filename) : m_sFileName(filename) { }
	const char *m_sFileName;
};


class CInputStreamReadErrorException : public CException
{
};


class CParseStackUnderflowException : public CException
{
};


class CTooDeepPutback : public CException
{
};


class CInputConversionErrorException : public CException
{
};


class CUnexpectedTokenException : public CException
{
public:
	CUnexpectedTokenException(CLexer *pLexer, long state) : 
									CException(pLexer), mState(state) { }

	long mState;
};


class CUnexpectedEndOfFileException : public CException
{
public: 
	CUnexpectedEndOfFileException(long state) : mState(state) { }
	
	long mState;
};


#endif // __EXCEPTIONS_H__

