LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := MBUI
LOCAL_SRC_FILES := MBUI.cpp

include $(BUILD_SHARED_LIBRARY)

