#ifndef __JAVALEXER_H__
#define __JAVALEXER_H__


#include "../common/lexer.h"


enum
{
	IDX_J_ABSTRACT,
	IDX_J_ASSERT,
	IDX_J_BOOLEAN,
	IDX_J_BREAK,
	IDX_J_BYTE,
	IDX_J_CASE,
	IDX_J_CATCH,
	IDX_J_CHAR,
	IDX_J_CLASS,
	IDX_J_CONTINUE,
	IDX_J_DEFAULT,
	IDX_J_DO,
	IDX_J_DOUBLE,
	IDX_J_ELSE,
	IDX_J_EXTENDS,
	IDX_J_FALSE,
	IDX_J_FINAL,
	IDX_J_FINALLY,
	IDX_J_FLOAT,
	IDX_J_FOR,
	IDX_J_IF,
	IDX_J_IMPLEMENTS,
	IDX_J_IMPORT,
	IDX_J_INSTANCEOF,
	IDX_J_INT,
	IDX_J_INTERFACE,
	IDX_J_LONG,
	IDX_J_NATIVE,
	IDX_J_NEW,
	IDX_J_NULL,
	IDX_J_PACKAGE,
	IDX_J_PRIVATE,
	IDX_J_PROTECTED,
	IDX_J_PUBLIC,
	IDX_J_RETURN,
	IDX_J_SHORT,
	IDX_J_STATIC,
	IDX_J_STRICTFP,
	IDX_J_SUPER,
	IDX_J_SWITCH,
	IDX_J_SYNCHRONIZED,
	IDX_J_THIS,
	IDX_J_THROW,
	IDX_J_THROWS,
	IDX_J_TRANSIENT,
	IDX_J_TRUE,
	IDX_J_TRY,
	IDX_J_VOID,
	IDX_J_VOLATILE,
	IDX_J_WHILE,
	IDX_J_ID,
	IDX_J_INT_LITERAL,
	IDX_J_FLOAT_LITERAL,
	IDX_J_CHAR_LITERAL,
	IDX_J_STR_LITERAL,
	IDX_J_LPAREN,			
	IDX_J_RPAREN,			
	IDX_J_COMMA,				
	IDX_J_LBRACK,			
	IDX_J_RBRACK,			
	IDX_J_LBRACE,			
	IDX_J_RBRACE,			
	IDX_J_EQ,			 	
	IDX_J_SEMIC,				
	IDX_J_COLON,				
	IDX_J_QUESTION,			
	IDX_J_DOT,				
	IDX_J_PLUS,				
	IDX_J_MINUS,				
	IDX_J_STAR,				
	IDX_J_SLASH,				
	IDX_J_PERCENT,
	IDX_J_HAT,				
	IDX_J_AND,				
	IDX_J_OR,			 	
	IDX_J_TILDE,				
	IDX_J_NOT,				
	IDX_J_PLUS_EQ,			
	IDX_J_MINUS_EQ,			
	IDX_J_STAR_EQ,			
	IDX_J_SLASH_EQ,			
	IDX_J_PERCENT_EQ,
	IDX_J_HAT_EQ,			
	IDX_J_AND_EQ,			
	IDX_J_OR_EQ,				
	IDX_J_LTLT,				
	IDX_J_GTGT,				
	IDX_J_GTGTGT,			
	IDX_J_GTGT_EQ,			
	IDX_J_LTLT_EQ,			
	IDX_J_GTGTGT_EQ,			
	IDX_J_EQEQ,
	IDX_J_NE,			 	
	IDX_J_LE,			 	
	IDX_J_GE,			 	
	IDX_J_LANGLE,			
	IDX_J_RANGLE,			
	IDX_J_ANDAND,			
	IDX_J_OROR,				
	IDX_J_INCR,				
	IDX_J_DECR,
	IDX_J_END_OF_FILE
};



class CJavaLexer : public CLexer
{
public:
	virtual int GetTokenCode(int index);
	virtual int GetTokenIndex(int token);

protected:
	virtual void InitLexer();
	virtual int ReadToken();
	virtual UChar32 ReadChar(bool ignore = false);
	virtual void SkipWhiteSpaces();

	inline bool NewLine();

	inline void QuotedLiteral(bool string);
	inline void NumericLiteral();
	inline void Identifier();
	inline void EscapeSequence();
	inline UChar UnicodeEscape();
	inline void SingleLineComment();
	inline void MultiLineComment();

	inline bool DecDigit();
	inline bool NonDigit();
	inline bool OctalDigit();
	inline bool HexDigit();
	inline bool IdStart();
	inline bool IdPart();
	const KEYWORD *MatchKeyword();


private:
	static inline unsigned int GetHash(const UChar *str, unsigned int len);

protected:
	int m_nSlashCount;
	bool m_bUnicodeEscape;

	static bool m_bIndexMapInit;
};


#endif	// __JAVALEXER_H__
