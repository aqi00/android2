LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

# 指定so库文件的名称
LOCAL_MODULE    := jni_mix
# 指定需要编译的源文件列表
LOCAL_SRC_FILES := find_name.cpp get_cpu.cpp get_encrypt.cpp get_decrypt.cpp aes.cpp
# 指定C++的编译标志
LOCAL_CPPFLAGS += -fexceptions
# 指定要加载的静态库
LOCAL_WHOLE_STATIC_LIBRARIES += android_support
# 指定需要链接的库
LOCAL_LDLIBS    := -llog

include $(BUILD_SHARED_LIBRARY)
$(call import-module, android/support)
