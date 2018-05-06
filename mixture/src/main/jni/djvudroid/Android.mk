LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := djvudroid
LOCAL_CFLAGS    := -DHAVE_CONFIG_H -DTHREADMODEL=NOTHREADS -DDEBUGLVL=0
LOCAL_LDLIBS 	:= -Wl,-llog -Wl,-Lbuild/platforms/android-1.5/arch-arm/usr/lib
LOCAL_SRC_FILES := Arrays.cpp BSByteStream.cpp BSEncodeByteStream.cpp ByteStream.cpp DataPool.cpp DjVmDir.cpp DjVmDir0.cpp DjVmDoc.cpp DjVmNav.cpp DjVuAnno.cpp DjVuDocEditor.cpp DjVuDocument.cpp DjVuDumpHelper.cpp DjVuErrorList.cpp DjVuFile.cpp DjVuFileCache.cpp DjVuGlobal.cpp DjVuGlobalMemory.cpp DjVuImage.cpp DjVuInfo.cpp DjVuMessage.cpp DjVuMessageLite.cpp DjVuNavDir.cpp DjVuPalette.cpp DjVuPort.cpp DjVuText.cpp GBitmap.cpp GContainer.cpp GException.cpp GIFFManager.cpp GMapAreas.cpp GOS.cpp GPixmap.cpp GRect.cpp GScaler.cpp GSmartPointer.cpp GString.cpp GThreads.cpp GURL.cpp GUnicode.cpp IFFByteStream.cpp IW44EncodeCodec.cpp IW44Image.cpp JB2EncodeCodec.cpp JB2Image.cpp JPEGDecoder.cpp MMRDecoder.cpp MMX.cpp UnicodeByteStream.cpp XMLParser.cpp XMLTags.cpp ZPCodec.cpp atomic.cpp debug.cpp DjVuToPS.cpp ddjvuapi.cpp miniexp.cpp
include $(BUILD_STATIC_LIBRARY)
