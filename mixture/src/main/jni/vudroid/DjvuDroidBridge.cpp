/*
 * DjvuDroidBridge.cpp
 *
 *  Created on: 17.01.2010
 *      Author: Cool
 */

#include <jni.h>
#include <stdlib.h>
#include <DjvuDroidTrace.h>
#include <ddjvuapi.h>

#define HANDLE_TO_DOC(handle) (ddjvu_document_t*)handle
#define HANDLE(ptr) (jlong)ptr

extern "C" jlong
Java_org_vudroid_djvudroid_codec_DjvuContext_create(JNIEnv *env,
                                    jclass cls)
{
	ddjvu_context_t* context = ddjvu_context_create(DJVU_DROID);
	DEBUG_PRINT("Creating context: %x", context);
	return (jlong) context;
}

extern "C" void
Java_org_vudroid_djvudroid_codec_DjvuContext_free(JNIEnv *env,
                                    jclass cls,
                                    jlong contextHandle)
{
	ddjvu_context_release((ddjvu_context_t *)contextHandle);
}

extern "C" jlong
Java_org_vudroid_djvudroid_codec_DjvuDocument_open(JNIEnv *env,
                                    jclass cls,
                                    jlong contextHandle,
                                    jstring fileName)
{
    const char* fileNameString = env->GetStringUTFChars(fileName, NULL);
	DEBUG_PRINT("Opening document: %s", fileNameString);
    jlong docHandle = (jlong)(ddjvu_document_create_by_filename((ddjvu_context_t*)(contextHandle), fileNameString, FALSE));
	env->ReleaseStringUTFChars(fileName, fileNameString);
    return docHandle;
}

void CallDocInfoCallback(JNIEnv* env, jobject thiz, const ddjvu_message_t* msg)
{
	DEBUG_WRITE("Calling handleDocInfo callback");
	jclass cls = env->GetObjectClass(thiz);
	if (!cls)
		return;
    jmethodID handleDocInfoId = env->GetMethodID(cls, "handleDocInfo", "()V");
    if (!handleDocInfoId)
    	return;
    env->CallVoidMethod(thiz, handleDocInfoId);
}

void ThrowDjvuError(JNIEnv* env, const ddjvu_message_t* msg)
{
    jclass exceptionClass = env->FindClass("java/lang/RuntimeException");
    if (!exceptionClass)
    	return;
    if (!msg || !msg->m_error.message)
    {
    	env->ThrowNew(exceptionClass, "Djvu decoding error!");
    	return;
    }
    env->ThrowNew(exceptionClass, msg->m_error.message);
}

extern "C" void
Java_org_vudroid_djvudroid_codec_DjvuContext_handleMessage(JNIEnv *env,
                                    jobject thiz,
                                    jlong contextHandle)
{
	const ddjvu_message_t *msg;
	ddjvu_context_t* ctx = (ddjvu_context_t*)(contextHandle);
	DEBUG_PRINT("handleMessage for ctx: %x",ctx);
	if(msg = ddjvu_message_peek(ctx))
    {
        switch (msg->m_any.tag)
        {
            case DDJVU_ERROR:
            	ThrowDjvuError(env, msg);
                break;
            case DDJVU_INFO:
                break;
            case DDJVU_DOCINFO:
            	CallDocInfoCallback(env, thiz, msg);
            	break;
            default:
                break;
        }
        ddjvu_message_pop(ctx);
	}
}

extern "C" jlong
Java_org_vudroid_djvudroid_codec_DjvuDocument_getPage(JNIEnv *env,
                                    jclass cls,
                                    jlong docHandle,
                                    jint pageNumber)
{
	DEBUG_PRINT("getPage num: %d", pageNumber);
	return (jlong)ddjvu_page_create_by_pageno((ddjvu_document_t*)docHandle, pageNumber);
}

extern "C" void
Java_org_vudroid_djvudroid_codec_DjvuDocument_free(JNIEnv *env,
                                    jclass cls,
                                    jlong docHandle)
{
	ddjvu_document_release((ddjvu_document_t*)docHandle);
}

extern "C" jint
Java_org_vudroid_djvudroid_codec_DjvuDocument_getPageCount(JNIEnv *env,
                                    jclass cls,
                                    jlong docHandle)
{
	return ddjvu_document_get_pagenum(HANDLE_TO_DOC(docHandle));
}

extern "C" jboolean
Java_org_vudroid_djvudroid_codec_DjvuPage_isDecodingDone(JNIEnv *env,
                                    jclass cls,
                                    jlong pageHandle)
{
	return ddjvu_page_decoding_done((ddjvu_page_t*)pageHandle);
}

extern "C" jint
Java_org_vudroid_djvudroid_codec_DjvuPage_getWidth(JNIEnv *env,
                                    jclass cls,
                                    jlong pageHangle)
{
	return ddjvu_page_get_width((ddjvu_page_t*)pageHangle);
}

extern "C" jint
Java_org_vudroid_djvudroid_codec_DjvuPage_getHeight(JNIEnv *env,
                                    jclass cls,
                                    jlong pageHangle)
{
	return ddjvu_page_get_height((ddjvu_page_t*)pageHangle);
}

extern "C" jboolean
Java_org_vudroid_djvudroid_codec_DjvuPage_renderPage(JNIEnv *env,
                                    jclass cls,
                                    jlong pageHangle,
                                    jint targetWidth,
                                    jint targetHeight,
                                    jfloat pageSliceX,
                                    jfloat pageSliceY,
                                    jfloat pageSliceWidth,
                                    jfloat pageSliceHeight,
                                    jintArray buffer)
{
	DEBUG_WRITE("Rendering page");
	ddjvu_page_t* page = (ddjvu_page_t*)((pageHangle));
    ddjvu_rect_t pageRect;
    pageRect.x = 0;
    pageRect.y = 0;
    pageRect.w = targetWidth / pageSliceWidth;
    pageRect.h = targetHeight / pageSliceHeight;
    ddjvu_rect_t targetRect;
    targetRect.x = pageSliceX * targetWidth / pageSliceWidth;
    targetRect.y = pageSliceY * targetHeight / pageSliceHeight;
    targetRect.w = targetWidth;
    targetRect.h = targetHeight;
    unsigned int masks[] = {0xFF0000, 0x00FF00, 0x0000FF};
    ddjvu_format_t* pixelFormat = ddjvu_format_create(DDJVU_FORMAT_RGBMASK32, 3, masks);
    ddjvu_format_set_row_order(pixelFormat, TRUE);
    ddjvu_format_set_y_direction(pixelFormat, TRUE);

    char *pBuffer = (char *)env->GetPrimitiveArrayCritical(buffer, 0);
    jboolean result = ddjvu_page_render(page, DDJVU_RENDER_COLOR, &pageRect, &targetRect, pixelFormat, targetWidth * 4, pBuffer);
    env->ReleasePrimitiveArrayCritical(buffer, pBuffer, 0);

    ddjvu_format_release(pixelFormat);
    return result;
}

extern "C" void
Java_org_vudroid_djvudroid_codec_DjvuPage_free(JNIEnv *env,
                                    jclass cls,
                                    jlong pageHangle)
{
	ddjvu_page_release((ddjvu_page_t*)pageHangle);
}
