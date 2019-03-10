package com.bapspatil.rake.util

/*
** Created by Bapusaheb Patil {@link https://bapspatil.com}
*/

class Constants {
    companion object {
        const val KEY_TEXT_RESULT = "KEY_TEXT_RESULT"
        const val KEY_FUNCTION = "KEY_FUNCTION"
        const val VALUE_RECOGNIZE_TEXT = "VALUE_RECOGNIZE_TEXT"
        const val VALUE_SCAN_BARCODE = "VALUE_SCAN_BARCODE"
        const val VALUE_LABEL_IMAGE = "VALUE_LABEL_IMAGE"

        // App keys
        const val KEY_PREFERENCE_FIRST_LAUNCH = "KEY_PREFERENCE_FIRST_LAUNCH"
        const val KEY_CURRENT_USER_UID = "KEY_CURRENT_USER_UID"

        // Firebase keys
        const val KEY_FIRESTORE_USERS = "users"
        const val KEY_FIRESTORE_USER_FULL_NAME = "fullName"
        const val KEY_FIRESTORE_USER_LABELLED_IMAGES = "labelledImages"
        const val KEY_FIRESTORE_USER_SCANNED_BARCODES = "scannedBarcodes"
        const val KEY_FIRESTORE_USER_RECOGNIZED_TEXT = "recognizedText"

        // Firestore Labelled Image keys
        const val KEY_FIRESTORE_LI_IMAGE_FILE = "imageFile"
        const val KEY_FIRESTORE_LI_IMAGE_HEIGHT = "imageHeight"
        const val KEY_FIRESTORE_LI_IMAGE_WIDTH = "imageWidth"
        const val KEY_FIRESTORE_LI_LABELS = "labels"
    }
}