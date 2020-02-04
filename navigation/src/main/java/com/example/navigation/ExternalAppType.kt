package com.example.navigation

enum class ExternalAppType {
    EMAIL,
    SHARE,
    PHOTO_GALLERY,
    LINK_HANDLER_APP,
    CAMERA
}

data class ExternalAppContent(
    val appType: ExternalAppType,
    var text: String? = null,
    var toEmails: Array<out String>? = null,
    var emailSubject: String? = null,
    var emailTitle: String? = null,
    var requestCode: Int? = null,
    var tempPictureFileUri: String? = null
)