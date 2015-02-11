LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_JAVA_LIBRARIES := bouncycastle telephony-common
LOCAL_STATIC_JAVA_LIBRARIES := json jsonmapper commons junit spring-rest-template spring-core netty

LOCAL_MODULE_TAGS := optional

# 1. Copy .so to out/target/product/***/system/lib
$(shell cp $(wildcard $(LOCAL_PATH)/libs/armeabi/*.so $(TARGET_OUT_SHARED_LIBRARIES)))
$(shell cp $(wildcard $(LOCAL_PATH)/libs/armeabi/*.so $(TARGET_OUT_SHARED_LIBRARIES_UNSTRIPPED)))
$(shell cp $(wildcard $(LOCAL_PATH)/libs/*.jar $(TARGET_OUT_SHARED_LIBRARIES)))
$(shell cp $(wildcard $(LOCAL_PATH)/libs/*.jar $(TARGET_OUT_SHARED_LIBRARIES_UNSTRIPPED)))


# 2. Copy config.xml to out/target/product/***/system/etc/   {TARGET_OUT_ETC}
# $(shell cp $(wildcard $(LOCAL_PATH)/configs/* $(TARGET_OUT_ETC)))


LOCAL_SRC_FILES := $(call all-java-files-under, src)

LOCAL_PACKAGE_NAME := Silveroak-WifiPlayer
LOCAL_CERTIFICATE := platform

LOCAL_OVERRIDES_PACKAGES := Home


LOCAL_PROGUARD_FLAG_FILES := proguard.flags

include $(BUILD_PACKAGE)

# Use the folloing include to make our test apk.
# include $(call all-makefiles-under,$(LOCAL_PATH))

include $(CLEAR_VARS)
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := json:libs/jackson-core-asl-1.9.13.jar
include $(BUILD_MULTI_PREBUILT)

include $(CLEAR_VARS)
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := jsonmapper:libs/jackson-mapper-asl-1.9.13.jar
include $(BUILD_MULTI_PREBUILT)

include $(CLEAR_VARS)
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := commons:libs/commons-lang-2.6.jar
include $(BUILD_MULTI_PREBUILT)

include $(CLEAR_VARS)
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := junit:libs/junit-4.10.jar
include $(BUILD_MULTI_PREBUILT)

include $(CLEAR_VARS)
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := spring-rest-template:libs/spring-android-rest-template-2.0.0.M1.jar
include $(BUILD_MULTI_PREBUILT)

include $(CLEAR_VARS)
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := spring-core:libs/spring-android-core-2.0.0.M1.jar
include $(BUILD_MULTI_PREBUILT)

include $(CLEAR_VARS)
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := netty:libs/netty-all-4.0.23.Final.jar
include $(BUILD_MULTI_PREBUILT)

include $(BUILD_MULTI_PREBUILT)
include $(call all-makefiles-under,$(LOCAL_PATH))