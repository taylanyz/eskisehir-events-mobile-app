package com.eskisehir.eventapp.data.model

/**
 * Enum representing user's status with an event
 */
enum class UserEventStatus {
    ATTENDED,      // Kullanıcı etkinliğe gitti
    GOING,         // Kullanıcı etkinliğe gidecek
    WANT_TO_GO,    // Kullanıcı etkinliğe gitmek istiyor
    NONE           // Durum seçilmemiş
}
