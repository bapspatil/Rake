# Firebase Authentication
-keepattributes Signature
-keepattributes *Annotation*

# CameraKit
-dontwarn com.google.android.gms.**
-keepclasseswithmembers class com.camerakit.preview.CameraSurfaceView {
    native <methods>;
}

# Firestore
-keep class com.bapspatil.rake.model.mlkit.** { *; }
-dontwarn okio.**
-dontwarn retrofit2.Call
-dontnote retrofit2.Platform$IOS$MainThreadExecutor
-keep class android.support.v7.widget.RecyclerView { *; }
-keep class androidx.recyclerview.widget.RecyclerView { *; }

# Storage
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keepattributes InnerClasses
