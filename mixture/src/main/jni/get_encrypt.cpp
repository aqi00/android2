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
Java_com_example_mixture_JniSecretActivity_encryptFromJNI( JNIEnv* env, jobject thiz, jstring raw, jstring key)
{
	const char* str_raw;
	const char* str_key;
	str_raw = env->GetStringUTFChars(raw, 0);
	str_key = env->GetStringUTFChars(key, 0);
	LOGI("str_raw=%s", str_raw);
	LOGI("str_key=%s", str_key);
	char encrypt[1024] = {0};
	AES aes_en((unsigned char*)str_key);
	aes_en.Cipher((char*)str_raw, encrypt);
	LOGI("encrypt=%s", encrypt);
	return env->NewStringUTF(encrypt);
}
