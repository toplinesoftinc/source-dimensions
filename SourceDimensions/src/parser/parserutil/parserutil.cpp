#include "../java/javaparser.h"
#include "../java/javaparser5.h"
#include "../csharp/csharpparser.h"
#include "../csharp/csharpparser2.h"
#include "../common/exceptions.h"
#include "../csharp/csharplexer.h"

static char *usage = "Usage: parserutil (-j | -j5 | -cs | -cs2) InputFileName [XmlOutputFileName]";

int main(int argc, char *argv[])
{
	CParser *parser = NULL;
	char *infilename;

	if (argc < 3)
	{
		cout << usage << endl;
		exit(1);
	}

	if (strcmp(argv[1], "-j") == 0)
		parser = new CJavaParser();
	else if (strcmp(argv[1], "-j5") == 0)
		parser = new CJavaParser5();
	else if (strcmp(argv[1], "-cs") == 0)
		parser = new CSharpParser();
	else if (strcmp(argv[1], "-cs2") == 0)
		parser = new CSharpParser2();

	if (!parser)
	{
		cout << usage << endl;
		exit(1);
	}

	infilename = argv[2];

	try
	{
		fstream ifs, *ofs = NULL;
		char *outfilename = NULL;

		ifs.open(infilename, ios_base::in | ios_base::binary);

		if (!ifs.is_open())
			throw new CFileOpenErrorException(infilename);

		if (argc >= 4)
		{
			outfilename = argv[3];
			ofs = new fstream();
			ofs->open(outfilename, ios_base::out | ios_base::trunc | ios_base::binary);

			if (!ofs->is_open())
				throw new CFileOpenErrorException(outfilename);
		}
			
	
		parser->Parse(ifs, ofs, outfilename);

		if (ofs)
			delete ofs;
	}
	catch(CInvalidPPExprException *e)
	{
		int32_t len = e->m_sToken.length();
		char *ascii = new char[len+1];

		e->m_sToken.extract(0, len, ascii);
		cout << infilename << ": Invalid preprocessing expression: \"" << ascii << "\". Line:" << e->m_nLine << endl;
		exit(1);
	}
	catch(CBadTokenException *e)
	{
		int32_t len = e->m_sToken.length();
		char *ascii = new char[len+1];

		e->m_sToken.extract(0, len, ascii);
		cout << infilename << ": Bad token \"" << ascii << "\". Line:" << e->m_nLine << endl;
		exit(1);
	}
	catch (CFileOpenErrorException *e)
	{
		cout << "Cannot open file \"" << e->m_sFileName << "\"" << endl;
		exit(1);
	}
	catch (CInputStreamReadErrorException*)
	{
		cout << infilename << ": Input stream read error." << endl;
		exit(1);
	}
	catch (CUnexpectedTokenException *e)
	{
		int32_t len = e->m_sToken.length();
		char *ascii = new char[len+1];

		e->m_sToken.extract(0, len, ascii);
		cout << infilename << ": Unexpected token \"" << ascii << "\"(" << e->m_nTermCode << "). State:" << e->mState 
			<< ", Line:" << e->m_nLine << endl;
		exit(1);
	}
	catch (CParseStackUnderflowException*)
	{
		cout << infilename << ": Reduce stack underflow." << endl;
		exit(1);
	}
	catch (CInputConversionErrorException*)
	{
		cout << infilename << ": Input conversion error." << endl;
		exit(1);
	}
	catch (CUnexpectedEndOfFileException *e)
	{
		cout << infilename << ": Unexpected EOF in state " << e->mState << endl;
	}
}
