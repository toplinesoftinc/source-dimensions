#include "../java/javaparser.h"
#include "../java/javaparser5.h"
#include "../csharp/csharpparser.h"
#include "../csharp/csharpparser2.h"
#include "../common/exceptions.h"
#include "../csharp/csharplexer.h"
#include "parserlib.h"
#include <strstream>

JNIEXPORT jbyteArray JNICALL Java_com_sourcedimensions_server_sys_Parser_parse(JNIEnv *env, 
	jclass clazz, jint lang, jstring filename, jbyteArray text, jint len, jbyteArray outbuf, jintArray outlen)
{
	CParser *parser;
	const char *txt, *fn;

	switch(lang)
	{	
		case LANG_JAVA:
			parser = new CJavaParser();
			break;

		case LANG_JAVA5:
			parser = new CJavaParser5();
			break;

		case LANG_CSHARP:
			parser = new CSharpParser();
			break;

		case LANG_CSHARP2:
			parser = new CSharpParser2();
	}

	txt = (const char*)env->GetByteArrayElements(text, NULL);
	fn = env->GetStringUTFChars(filename, NULL);

	if (txt == NULL || fn == NULL)
	         return NULL; /* OutOfMemoryError already thrown */

	istrstream is(txt, len); 
	ostrstream os, es;

	try
	{
		parser->Parse(is, &os, fn);
	}
	catch (CInvalidPPExprException *e)
	{
		int32_t len = e->m_sToken.length();
		char *ascii = new char[len+1];

		e->m_sToken.extract(0, len, ascii);
		es << fn << "-> Invalid preprocessing expression: \"" << ascii << "\". Line:" << e->m_nLine;
	}
	catch (CBadTokenException *e)
	{
		int32_t len = e->m_sToken.length();
		char *ascii = new char[len+1];

		e->m_sToken.extract(0, len, ascii);
		es << fn << "-> Bad token \"" << ascii << "\". Line:" << e->m_nLine;
	}
	catch (CInputStreamReadErrorException*)
	{
		es << fn << "-> Input stream read error.";
	}
	catch (CUnexpectedTokenException *e)
	{
		int32_t len = e->m_sToken.length();
		char *ascii = new char[len+1];

		e->m_sToken.extract(0, len, ascii);
		es << fn << "-> Unexpected token \"" << ascii << "\"(" << e->m_nTermCode << "). State:" << e->mState 
			<< ", Line:" << e->m_nLine;
	}
	catch (CParseStackUnderflowException*)
	{
		es << fn << "-> Reduce stack underflow.";
	}
	catch (CInputConversionErrorException*)
	{
		es << fn << "-> Input conversion error.";
	}
	catch (CUnexpectedEndOfFileException *e)
	{
		es << fn << "-> Unexpected EOF in state " << e->mState;
	}
	catch(...)
	{
		es << "Unknown exception";
	}

	delete parser;

	env->ReleaseByteArrayElements(text, (jbyte*)txt, JNI_ABORT);
	env->ReleaseStringUTFChars(filename, fn);
	  
	if (es.pcount() > 0)
	{
		jclass cls = env->FindClass("com/sourcedimensions/server/exceptions/ParserException");
		if (cls == NULL)
			return NULL;

		es << '\0';
		env->ThrowNew(cls, es.str());
		return NULL;
	}
	else
	{
		jint size = (jint)os.pcount();

		if (outbuf == NULL || (outbuf != NULL && env->GetArrayLength(outbuf) < size))
			outbuf = env->NewByteArray(size);
		
		env->SetByteArrayRegion(outbuf, 0, size, (jbyte*)os.str());

		if (outlen != NULL)
			env->SetIntArrayRegion(outlen, 0, 1, &size);
		
		return outbuf;
	}
}
