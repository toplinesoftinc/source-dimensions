#include "symbol.h"
#include "parser.h"
#include "unicode/ustring.h"


static const char header[] = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";


static const char *language[] =
{
	"java",
	"java5",
	"csharp",
	"csharp2"
};


CSymbol::CSymbol(CLexer &lexer) : 
	terminal(true),
	code(lexer.GetCurToken()), 
	left(lexer.GetLeftPos()), 
	right(lexer.GetRightPos()),
	parent(NULL),
	children(NULL)
{
	if (lexer.GetTokenValue().length() > 0)
		value = new UnicodeString(lexer.GetTokenValue());
	else
		value = NULL;
}


 CSymbol::CSymbol(int nt) : 
	terminal(false),
	code(nt), 
	value(NULL),
	parent(NULL),
	children(new list<CSymbol*>())
{
}


void CSymbol::Serialize(CParser *parser, ostream &ostr, const char *filename, const char *encoding)
{
	ostr << header << "<SourceFile language=\"" << language[parser->GetLanguageCode()]
			<< "\" file=\"" << filename << "\" " << "encoding=\"" << encoding << "\">";
	SerializeNode(parser, ostr);
	ostr << "</SourceFile>";
}


CSymbol::~CSymbol()
{
	if (children)
	{
		for (list<CSymbol*>::iterator i = children->begin(); i != children->end(); i++)
			delete *i;

		delete children;
	}

	if (value)
		delete value;
}


void CSymbol::SerializeNode(CParser *parser, ostream &ostr)
{
	bool skip = !parser->FilterCallback(this);

	if (!skip)
	{
		ostr << "<" << (terminal ? parser->GetTermName(code) : parser->GetNtName(code))
			<< " l=\"" << dec << left << "\" r=\"" << dec << right << "\"";

		if (value)
		{
			UErrorCode err = U_ZERO_ERROR;
			UChar32 uc;
			int len = value->length() * 4 + 1;
			char *val = new char[len];

			u_strToUTF8(val, len, NULL, value->getBuffer(), value->length(), &err);
			len = strlen(val);

			ostr << " val=\"";
			for (int32_t i = 0; i < len;)
			{
				U8_NEXT(val, i, len, uc);
				switch (uc)
				{
					case '\"':
						ostr << "&quot;";
						break;

					case '\'':
						ostr << "&apos;";
						break;

					case '&':
						ostr << "&amp;";
						break;

					case '<':
						ostr << "&lt;";
						break;

					case '>':
						ostr << "&gt;";
						break;

					default:
						if (uc == 0x9 || uc == 0xA || uc == 0xD ||
							(uc >= 0x0020 && uc <= 0xD7FF) ||
							(uc >= 0xE000 && uc <= 0xFFFD) ||
							(uc >= 0x10000 && uc <= 0x10FFFF)
							)
						{
							int u8l = U8_LENGTH(uc);

							for (int l = 0; l < u8l; l++)
								ostr << val[i-u8l+l];	
						}
						else
						{
							ostr << "\\u" << hex << uppercase << uc;
						}
				}	
			}

			ostr << "\"";
			delete val;
		}
	}

	if (children)
	{
		int bmk, cur;
		
		bmk = cur = ostr.tellp();

		if (!skip)
		{
			ostr << ">";
			cur = ostr.tellp();
		}

		for (list<CSymbol*>::iterator i = children->begin(); i != children->end(); i++)
			(*i)->SerializeNode(parser, ostr);

		if (!skip)
		{
			if (cur < ostr.tellp())
			{
				ostr << "</" << (terminal ? parser->GetTermName(code) : parser->GetNtName(code)) << ">";
			}
			else
			{
				ostr.seekp(bmk, ios::beg);
				ostr << "/>";
			}
		}
	}
	else
	{
		if (!skip) 
			ostr << "/>";
	}
}
