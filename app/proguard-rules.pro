# Firebase Authentication
-keepattributes Signature
-keepattributes *Annotation*

# CameraKit
-dontwarn com.google.android.gms.**
-keepclasseswithmembers class com.camerakit.preview.CameraSurfaceView {
    native <methods>;
}