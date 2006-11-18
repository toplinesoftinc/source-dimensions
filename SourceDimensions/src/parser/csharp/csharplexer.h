#ifndef __CSHARPLEXER_H__
#define __CSHARPLEXER_H__


#include <map>
#include <set>
#include "../common/lexer.h"


enum
{
	IDX_CS_ARGLIST,
	IDX_CS_MAKEREF,
	IDX_CS_REFTYPE,
	IDX_CS_REFVALUE,
	IDX_CS_ABSTRACT,
	IDX_CS_ADD,
	IDX_CS_AS,
	IDX_CS_ASSEMBLY,
	IDX_CS_BASE,
	IDX_CS_BOOL,
	IDX_CS_BREAK,
	IDX_CS_BYTE,
	IDX_CS_CASE,
	IDX_CS_CATCH,
	IDX_CS_CHAR,
	IDX_CS_CHECKED,
	IDX_CS_CLASS,
	IDX_CS_CONST,
	IDX_CS_CONTINUE,
	IDX_CS_DECIMAL,
	IDX_CS_DEFAULT,
	IDX_CS_DELEGATE,
	IDX_CS_DO,
	IDX_CS_DOUBLE,
	IDX_CS_ELSE,
	IDX_CS_ENUM,
	IDX_CS_EVENT,
	IDX_CS_EXPLICIT,
	IDX_CS_EXTERN,
	IDX_CS_FALSE,
	IDX_CS_FIELD,
	IDX_CS_FINALLY,
	IDX_CS_FIXED,
	IDX_CS_FLOAT,
	IDX_CS_FOR,
	IDX_CS_FOREACH,
	IDX_CS_GET,
	IDX_CS_GOTO,
	IDX_CS_IF,
	IDX_CS_IMPLICIT,
	IDX_CS_IN,
	IDX_CS_INT,
	IDX_CS_INTERFACE,
	IDX_CS_INTERNAL,
	IDX_CS_IS,
	IDX_CS_LOCK,
	IDX_CS_LONG,
	IDX_CS_METHOD,
	IDX_CS_MODULE,
	IDX_CS_NAMESPACE,
	IDX_CS_NEW,
	IDX_CS_NULL,
	IDX_CS_OBJECT,
	IDX_CS_OPERATOR,
	IDX_CS_OUT,
	IDX_CS_OVERRIDE,
	IDX_CS_PARAM,
	IDX_CS_PARAMS,
	IDX_CS_PRIVATE,
	IDX_CS_PROPERTY,
	IDX_CS_PROTECTED,
	IDX_CS_PUBLIC,
	IDX_CS_READONLY,
	IDX_CS_REF,
	IDX_CS_REMOVE,
	IDX_CS_RETURN,
	IDX_CS_SBYTE,
	IDX_CS_SEALED,
	IDX_CS_SET,
	IDX_CS_SHORT,
	IDX_CS_SIZEOF,
	IDX_CS_STACKALLOC,
	IDX_CS_STATIC,
	IDX_CS_STRING,
	IDX_CS_STRUCT,
	IDX_CS_SWITCH,
	IDX_CS_THIS,
	IDX_CS_THROW,
	IDX_CS_TRUE,
	IDX_CS_TRY,
	IDX_CS_TYPE,
	IDX_CS_TYPEOF,
	IDX_CS_UINT,
	IDX_CS_ULONG,
	IDX_CS_UNCHECKED,
	IDX_CS_UNSAFE,
	IDX_CS_USHORT,
	IDX_CS_USING,
	IDX_CS_VIRTUAL,
	IDX_CS_VOID,
	IDX_CS_VOLATILE,
	IDX_CS_WHILE,
	IDX_CS_ID,
	IDX_CS_CHAR_LITERAL,
	IDX_CS_STR_LITERAL,
	IDX_CS_INT_LITERAL,
	IDX_CS_REAL_LITERAL,
	IDX_CS_LANGLE,
	IDX_CS_RANGLE,
	IDX_CS_LPAREN,
	IDX_CS_RPAREN,
	IDX_CS_COMMA,
	IDX_CS_LBRACK,
	IDX_CS_RBRACK,
	IDX_CS_LBRACE,
	IDX_CS_RBRACE,
	IDX_CS_EQ,
	IDX_CS_SEMIC,
	IDX_CS_COLON,
	IDX_CS_QUESTION,
	IDX_CS_DOT,
	IDX_CS_PLUS,
	IDX_CS_MINUS,
	IDX_CS_STAR,
	IDX_CS_SLASH,
	IDX_CS_PERCENT,
	IDX_CS_HAT,
	IDX_CS_AND,
	IDX_CS_OR,
	IDX_CS_TILDE,
	IDX_CS_NOT,
	IDX_CS_PLUS_EQ,
	IDX_CS_MINUS_EQ,
	IDX_CS_STAR_EQ,
	IDX_CS_SLASH_EQ,
	IDX_CS_PERCENT_EQ,
	IDX_CS_HAT_EQ,
	IDX_CS_AND_EQ,
	IDX_CS_OR_EQ,
	IDX_CS_LTLT,
	IDX_CS_GTGT,
	IDX_CS_GTGT_EQ,
	IDX_CS_LTLT_EQ,
	IDX_CS_EQEQ,
	IDX_CS_NE,
	IDX_CS_LE,
	IDX_CS_GE,
	IDX_CS_ANDAND,
	IDX_CS_OROR,
	IDX_CS_INCR,
	IDX_CS_DECR,
	IDX_CS_ARROW,
	IDX_CS_END_OF_FILE
};

struct PP_STATE
{
	PP_STATE(int s, long p) : state(s), pos(p) { }
	int state;
	long pos;
};

typedef std::map<UnicodeString, int> string_map;
typedef std::set<UnicodeString> string_set;
typedef std::stack<PP_STATE> int_stack;


enum PP_tokens
{
	PP_EOF = -1,
	PP_DEFINE = 0,
	PP_UNDEF,
	PP_IF,
	PP_ELIF,
	PP_ELSE,
	PP_ENDIF,
	PP_LINE,
	PP_ERROR,
	PP_WARNING,
	PP_REGION,
	PP_ENDREGION
};


enum PP_operators
{
	PP_OP_EOE = -1,
	PP_OP_OR = 0,
	PP_OP_AND = 2,
	PP_OP_EQ = 4,
	PP_OP_NEQ = 5
};


class CSharpLexer : public CLexer
{
public:
	virtual int GetTokenCode(int index);
	virtual int GetTokenIndex(int token);

	void DefinePPSymbol(const UChar* SymbolName);
	void UndefPPSymbol(const UChar* SymbolName);

	virtual void Rollback();

protected:
	virtual void InitLexer();
	virtual int ReadToken();
	virtual void SkipWhiteSpaces();

	bool NewLine();
	inline bool WhiteSpace();

	inline void QuotedLiteral(bool string);
	inline void NumericLiteral();
	inline bool Identifier();
	inline void EscapeSequence(bool string);
	inline void SingleLineComment();
	inline void MultiLineComment();

	inline void UnicodeEscapeSequence(UChar *first, UChar *second);
	inline bool DecDigit();
	inline bool NonZeroDecDigit();
	inline bool NonDigit();
	inline bool OctalDigit();
	inline bool HexDigit();
	inline bool IdStart();

	virtual void ParsePP();
	inline bool SkipWhiteSpacesPP();
	inline int NextSectionPP();
	inline bool EvalExprPP(bool paren = false);
	inline int ParseOperatorPP();
	inline bool ParseOperandPP();
	inline bool EvalBoolOpPP(bool arg1, int op, bool arg2);

	const KEYWORD *MatchPreprocKeyword();
	const KEYWORD *MatchKeyword();

private:
	static inline unsigned int GetPreprocHash(const UChar *str, unsigned int len);
	static inline unsigned int GetHash(const UChar *str, unsigned int len);


protected:
	bool m_bFirstToken;
	int_stack mNestStack;
	string_set mPPSymbols;

	static bool m_bIndexMapInit;
};


#endif	// __CSHARPLEXER_H__
