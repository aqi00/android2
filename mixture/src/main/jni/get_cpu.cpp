#include <jni.h>
#include <string.h>
#include <stdio.h>

extern "C"

jstring
Java_com_example_mixture_JniCpuActivity_cpuFromJNI( JNIEnv* env, jobject thiz, jint i1, jfloat f1, jdouble d1, jboolean b1 )
{
#if defined(__arm__)
  #if defined(__ARM_ARCH_7A__)
    #if defined(__ARM_NEON__)
      #if defined(__ARM_PCS_VFP)
        #define ABI "armeabi-v7a/NEON (hard-float)"
      #else
        #define ABI "armeabi-v7a/NEON"
      #endif
    #else
      #if defined(__ARM_PCS_VFP)
        #define ABI "armeabi-v7a (hard-float)"
      #else
        #define ABI "armeabi-v7a"
      #endif
    #endif
  #else
   #define ABI "armeabi"
  #endif
#elif defined(__i386__)
   #define ABI "x86"
#elif defined(__x86_64__)
   #define ABI "x86_64"
#elif defined(__mips64)  /* mips64el-* toolchain defines __mips__ too */
   #define ABI "mips64"
#elif defined(__mips__)
   #define ABI "mips"
#elif defined(__aarch64__)
   #define ABI "arm64-v8a"
#else
   #define ABI "unknown"
#endif

	char desc[200] = {0};
	sprintf(desc, "%d %f %lf %u \nHello from JNI !  Compiled with %s.", i1, f1, d1, b1, ABI);
	return env->NewStringUTF(desc);
}
