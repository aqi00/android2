# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\adt-bundle-windows-x86_64-20140702\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
#指定代码的压缩级别
-optimizationpasses 5
#是否使用大小写混合
-dontusemixedcaseclassnames
#优化/不优化输入的类文件
-dontoptimize
#是否混淆第三方jar包
-dontskipnonpubliclibraryclasses
#混淆时是否做预校验
-dontpreverify
#混淆时是否记录日志
-verbose
#混淆时所采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
#保护注解
-keepattributes *Annotation*

#保持JNI用到的native方法不被混淆
-keepclasseswithmembers class * {
    native <methods>;
}

#保持自定义控件的构造函数不被混淆，因为自定义控件很可能直接写在布局文件中
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

#保持自定义控件的构造函数不被混淆
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

#保持布局中onClick属性指定的方法不被混淆
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

#保持枚举enum类不被混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

#保持序列化的Parcelable不被混淆
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

#指定哪些第三方jar包需要混淆
#-libraryjars libs/bcprov-jdk16-1.46.jar

#保持哪些系统组件类不被混淆
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.support.v4.**
-keep public class com.android.vending.licensing.ILicensingService

#保持哪些第三方jar包不被混淆。比如上一节RSA算法用到了bcprov-jdk16-1.46.jar，该jar包里的工具类就不可混淆
-keep class org.bouncycastle.**
-dontwarn org.bouncycastle.**
