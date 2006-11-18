#include <jni.h>

#ifndef _PARSERLIB_H_
#define _PARSERLIB_H_


#ifdef __cplusplus
extern "C" {
#endif


/*
 * Class:     Parser
 * Method:    parse
 * Signature: (ILjava/lang/String;)Ljava/lang/String;
 */

JNIEXPORT jbyteArray JNICALL Java_com_sourcedimensions_server_sys_Parser_parse(JNIEnv *, 
	jclass, jint, jstring, jbyteArray, jint, jbyteArray, jintArray);


#ifdef __cplusplus
}
#endif


#endif // _PARSERLIB_H_
