# CameraKit
-dontwarn com.google.android.gms.**
-keepclasseswithmembers class com.camerakit.preview.CameraSurfaceView {
    native <methods>;
}

# Firebase UI
-dontwarn com.facebook.**
-dontwarn com.twitter.**
-keepnames class com.facebook.login.LoginManager
-keepnames class com.twitter.sdk.android.core.identity.TwitterAuthClient

-dontnote com.google.**
-dontnote com.facebook.**
-dontnote com.twitter.**
-dontnote com.squareup.okhttp.**
-dontnote okhttp3.internal.**

-keepattributes Signature
-keepattributes *Annotation*

-dontnote retrofit2.Platform
-dontwarn retrofit2.** # Also keeps Twitter at bay as long as they keep using Retrofit
-dontwarn okhttp3.**
-dontwarn okio.**
-keepattributes Exceptions

-dontwarn com.google.gson.Gson$6

# Firebase Authentication
-keepattributes Signature
-keepattributes *Annotation*

# Firebase Firestore
-keep class com.bapspatil.rake.model.** { *; }
-dontwarn okio.**
-dontwarn retrofit2.Call
-dontnote retrofit2.Platform$IOS$MainThreadExecutor
-keep class android.support.v7.widget.RecyclerView { *; }
-keep class androidx.recyclerview.widget.RecyclerView { *; }

# Firebase Storage
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keepattributes InnerClasses

# Firebase Cloud Messaging
-keepattributes EnclosingMethod
-keepattributes InnerClasses

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepnames class kotlinx.coroutines.android.AndroidExceptionPreHandler {}
-keepnames class kotlinx.coroutines.android.AndroidDispatcherFactory {}

-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# Retrofit 2.X
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# GSON
-keepattributes Signature

-keepattributes *Annotation*

-dontwarn sun.misc.**

-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

