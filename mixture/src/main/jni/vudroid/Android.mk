LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := vudroid

LOCAL_SRC_FILES := \
	pdfdroidbridge.c \
	DjvuDroidBridge.cpp

LOCAL_C_INCLUDES := \
	$(LOCAL_PATH)/../mupdf/mupdf/fitz \
	$(LOCAL_PATH)/../mupdf/mupdf/mupdf \
	$(LOCAL_PATH)/../djvudroid

LOCAL_CXX_INCLUDES := \
	$(LOCAL_PATH)/../djvudroid

LOCAL_STATIC_LIBRARIES := mupdf djvudroid

# uses Android log and z library (Android-3 Native API)
LOCAL_LDLIBS := -llog -lz

include $(BUILD_SHARED_LIBRARY)

