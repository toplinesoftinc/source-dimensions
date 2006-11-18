#include "bnf2xsd.h"
#include "exception.h"
#include <iostream>

char usage[] = "Usage: Bnf2Xsd {inputfilename}";


int main(int argc, char *argv[])
{
	CBnf2Xsd parser;

	if (argc < 2)
	{
		cout << usage << endl;
		exit(1);
	}

	try
	{
		parser.ParseFile(argv[1]);
		parser.WriteXsd("output.xsd");
		parser.WriteList("output.lst");
	}
	catch (CFileOpenException *e)
	{
		cout << "Error opening file \"" << e->m_FileName << "\"" << endl;
	}
	catch (CUnexpCharException *e)
	{
		cout << "Line " << e->m_nLine << ": Unexpected character '" << e->m_chErrChar << "'" << endl;
	}
	catch (CUnexpTokenException *e)
	{
		cout << "Line " << e->m_nLine << ": Unexpected token with code " << e->m_nTokenCode << endl;
	}
	catch (CDupDefException *e)
	{
		cout << "Line " << e->m_nLine << ": Duplicated definition for symbol \"" << e->m_Name << "\"" << endl;
	}
	catch (CInvalidMacroDefException *e)
	{
		cout << "Line " << e->m_nLine << ": Invalid macro definition" << endl;
	}
	catch (CInvalidMacroRefException *e)
	{
		cout << "Line " << e->m_nLine << ": Invalid macro reference" << endl;
	}
	catch (CUnexpEofException*)
	{
		cout << "Unexpected end of file" << endl;
	}
}
