#ifndef __EXCEPTION_H__
#define __EXCEPTION_H__


#include <string.h>


class CException
{
};


class CFileOpenException : public CException
{
public:
	CFileOpenException(char *filename) : m_FileName(filename) { }

	string m_FileName;
};


class CUnexpCharException : public CException
{
public:
	CUnexpCharException(int line, char ch) : m_nLine(line), m_chErrChar(ch) { }

	int m_nLine;
	char m_chErrChar;
};


class CUnexpTokenException : public CException
{
public:
	CUnexpTokenException(int line, int code) : m_nLine(line), m_nTokenCode(code) { }

	int m_nLine;
	int m_nTokenCode;
};


class CDupDefException : public CException
{
public:
	CDupDefException(int line, string &name) : m_nLine(line), m_Name(name) { }

	int m_nLine;
	string m_Name;
};


class CInvalidMacroDefException : public CException
{
public:
	CInvalidMacroDefException(int line) : m_nLine(line) { }

	int m_nLine;
};


class CInvalidMacroRefException : public CException
{
public:
	CInvalidMacroRefException(int line) : m_nLine(line) { }

	int m_nLine;
};


class CUnexpEofException : public CException
{
};

#endif // __EXCEPTION_H__
