#include <jni.h>
#include <string.h>
#include <stdio.h>
#include "aes.h"
#include <android/log.h>

// log标签
#define TAG "MyMsg"
// 定义info信息
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,TAG,__VA_ARGS__)

extern "C"

jstring
Java_com_example_mixture_JniSecretActivity_decryptFromJNI( JNIEnv* env, jobject thiz, jstring des, jstring key)
{
	const char* str_des;
	const char* str_key;
	str_des = env->GetStringUTFChars(des, 0);
	str_key = env->GetStringUTFChars(key, 0);
	LOGI("str_des=%s", str_des);
	LOGI("str_key=%s", str_key);
	char decrypt[1024] = {0};
	AES aes_de((unsigned char*)str_key);
	aes_de.InvCipher((char*)str_des, decrypt);
	LOGI("decrypt=%s", decrypt);
	return env->NewStringUTF(decrypt);
}
