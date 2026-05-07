# Phase 12: Turkish Text Inventory

## Overview

Complete inventory of all Turkish UI strings used in the Eskişehir Events mobile application. This document serves as the central reference for UI localization and ensures consistency across all screens.

**Total Strings**: 200+  
**Language**: Turkish (Türkçe)  
**Status**: Complete ✅  
**Resource File**: `app/src/main/res/values-tr/strings.xml`  

---

## 1. Authentication & Login (13 strings)

| ID | Turkish | English | Category |
|----|---------|---------|----------|
| auth_login | Giriş Yap | Login | Button/Title |
| auth_register | Kayıt Ol | Register | Button/Title |
| auth_email | E-posta | Email | Label |
| auth_password | Şifre | Password | Label |
| auth_password_confirm | Şifre Tekrar | Confirm Password | Label |
| auth_first_name | Ad | First Name | Label |
| auth_last_name | Soyad | Last Name | Label |
| auth_phone | Telefon Numarası | Phone Number | Label |
| auth_forgot_password | Şifremi Unuttum | Forgot Password? | Link |
| auth_already_have_account | Zaten Hesabın Var Mı? | Already have an account? | Link |
| auth_no_account | Hesabın Yok Mu? | Don't have an account? | Link |
| auth_login_success | Başarıyla giriş yapıldı | Successfully logged in | Message |
| auth_register_success | Kayıt başarılı. Lütfen giriş yapın | Registration successful. Please login | Message |

---

## 2. Onboarding (12 strings)

| ID | Turkish | English | Category |
|----|---------|---------|----------|
| onboarding_welcome | Hoş Geldiniz | Welcome | Title |
| onboarding_title_1 | Kişiselleştirilmiş Öneriler | Personalized Recommendations | Screen Title |
| onboarding_desc_1 | Sizin ilgi alanlarınıza ve tercihlerinize göre özel rota önerileri alın | Get custom route recommendations based on your preferences | Description |
| onboarding_title_2 | Dinamik Rota Planlama | Dynamic Route Planning | Screen Title |
| onboarding_desc_2 | Bütçeniz, zamanınız ve tercihlerinize uygun en iyi rotaları keşfedin | Discover optimal routes that fit your budget and schedule | Description |
| onboarding_title_3 | Yapay Zeka Desteği | AI-Powered Suggestions | Screen Title |
| onboarding_desc_3 | Sistem sizin tercihlerinizden öğrenerek daha iyi öneriler sunar | System learns from your preferences to provide better recommendations | Description |
| onboarding_title_4 | Yerel İşletmeleri Destekleyin | Support Local Businesses | Screen Title |
| onboarding_desc_4 | Eskişehir'deki yerel işletmeleri keşfedin ve destekleyin | Discover and support local businesses in Eskişehir | Description |
| onboarding_skip | Atla | Skip | Button |
| onboarding_next | İleri | Next | Button |
| onboarding_finish | Başla | Start | Button |

---

## 3. Preferences Setup (34 strings)

### 3.1 Main Section Labels
| ID | Turkish | English | Category |
|----|---------|---------|----------|
| preferences_title | Tercihlerinizi Ayarlayın | Set Your Preferences | Title |
| preferences_interests | İlgi Alanları | Interests | Section |
| preferences_interests_desc | Hangi etkinlik ve mekanlardan hoşlanıyorsunuz? | What types of events and places do you like? | Description |
| preferences_budget | Bütçe | Budget | Section |
| preferences_budget_desc | Rota başına harcamak istediğiniz bütçe | Budget per route | Description |
| preferences_transportation | Ulaşım Tercihi | Transportation Mode | Section |
| preferences_crowd | Kalabalık Toleransı | Crowd Tolerance | Section |
| preferences_sustainability | Sürdürülebilirlik | Sustainability | Section |
| preferences_sustainability_desc | Çevre dostu tercihleriniz | Your eco-friendly preferences | Description |

### 3.2 Interest Categories (7 strings)
| ID | Turkish | English | Category |
|----|---------|---------|----------|
| preferences_museums | Müzeler | Museums | Interest |
| preferences_parks | Parklar | Parks | Interest |
| preferences_cafes | Kafeler | Cafes | Interest |
| preferences_restaurants | Restoranlar | Restaurants | Interest |
| preferences_cultural | Kültürel Mekanlar | Cultural Sites | Interest |
| preferences_historical | Tarihi Siteler | Historical Sites | Interest |
| preferences_outdoor | Açık Hava Aktiviteleri | Outdoor Activities | Interest |

### 3.3 Budget Options (3 strings)
| ID | Turkish | English | Category |
|----|---------|---------|----------|
| preferences_budget_low | Düşük (0-100 ₺) | Low (0-100 ₺) | Option |
| preferences_budget_medium | Orta (100-500 ₺) | Medium (100-500 ₺) | Option |
| preferences_budget_high | Yüksek (500+ ₺) | High (500+ ₺) | Option |

### 3.4 Transportation (4 strings)
| ID | Turkish | English | Category |
|----|---------|---------|----------|
| preferences_walking | Yürüme | Walking | Option |
| preferences_public_transport | Toplu Taşıma | Public Transport | Option |
| preferences_car | Araba | Car | Option |
| preferences_bike | Bisiklet | Bike | Option |

### 3.5 Crowd Tolerance (3 strings)
| ID | Turkish | English | Category |
|----|---------|---------|----------|
| preferences_crowd_low | Düşük (sessiz yerler) | Low (quiet places) | Option |
| preferences_crowd_medium | Orta | Medium | Option |
| preferences_crowd_high | Yüksek (canlı yerler) | High (lively places) | Option |

### 3.6 Actions
| ID | Turkish | English | Category |
|----|---------|---------|----------|
| preferences_eco_friendly | Çevre Dostu | Eco-Friendly | Option |
| preferences_save | Kaydet | Save | Button |
| preferences_saved | Tercihleriniz kaydedildi | Preferences saved | Message |

---

## 4. Home Screen (11 strings)

| ID | Turkish | English | Category |
|----|---------|---------|----------|
| home_title | Anasayfa | Home | Title |
| home_welcome | Merhaba | Hello | Greeting |
| home_quick_actions | Hızlı İşlemler | Quick Actions | Section |
| home_get_recommendations | Öneriler Al | Get Recommendations | Button |
| home_discover_places | Mekanları Keşfet | Discover Places | Button |
| home_my_routes | Rotalarım | My Routes | Button |
| home_featured | Öne Çıkanlar | Featured | Section |
| home_nearby | Yakınımda | Nearby | Section |
| home_popular_today | Bugün Popüler | Popular Today | Section |
| home_no_data | Henüz veri yok | No data yet | Empty State |
| home_loading | Yükleniyor... | Loading... | Loading |

---

## 5. Place Discovery (15 strings)

| ID | Turkish | English | Category |
|----|---------|---------|----------|
| discovery_title | Mekanları Keşfet | Discover Places | Title |
| discovery_search_placeholder | Mekan ara... | Search places... | Placeholder |
| discovery_filter | Filtrele | Filter | Button |
| discovery_sort | Sırala | Sort | Button |
| discovery_sort_popular | Popüler | Popular | Option |
| discovery_sort_nearest | En Yakın | Nearest | Option |
| discovery_sort_rating | En Yüksek Puan | Highest Rated | Option |
| discovery_category_all | Tümü | All | Category |
| discovery_category_museum | Müze | Museum | Category |
| discovery_category_park | Park | Park | Category |
| discovery_category_cafe | Kafe | Cafe | Category |
| discovery_category_restaurant | Restoran | Restaurant | Category |
| discovery_no_results | Sonuç bulunamadı | No results found | Empty State |
| discovery_empty_state | Henüz mekan eklenmemiş | No places added yet | Empty State |
| discovery_place_rating | Puan | Rating | Label |

---

## 6. Place Details (4 strings)

| ID | Turkish | English | Category |
|----|---------|---------|----------|
| discovery_place_distance | Uzaklık | Distance | Label |
| discovery_place_duration | Tahmini Süre | Est. Duration | Label |
| discovery_place_budget | Bütçe | Budget | Label |

---

## 7. Recommendations (12 strings)

| ID | Turkish | English | Category |
|----|---------|---------|----------|
| recommendations_title | Öneriler | Recommendations | Title |
| recommendations_generating | Öneriler oluşturuluyor... | Generating recommendations... | Loading |
| recommendations_no_data | Henüz öneri yok | No recommendations yet | Empty State |
| recommendations_why | Neden önerildi? | Why recommended? | Label |
| recommendations_match_score | Uyum: %d | Match: %d%% | Label |
| recommendations_reason_preference | Tercihlerinize uyar | Matches your preferences | Reason |
| recommendations_reason_popular | Popüler | Popular | Reason |
| recommendations_reason_nearby | Yakınız | Near you | Reason |
| recommendations_reason_similar | Benzer mekanlar sevenler sevdi | Similar to places you like | Reason |
| recommendations_save | Kaydet | Save | Button |
| recommendations_add_to_route | Rotaya Ekle | Add to Route | Button |
| recommendations_share | Paylaş | Share | Button |

---

## 8. Route Planning (12 strings)

| ID | Turkish | English | Category |
|----|---------|---------|----------|
| route_title | Rota Detayları | Route Details | Title |
| route_duration | Toplam Süre | Total Duration | Label |
| route_distance | Toplam Mesafe | Total Distance | Label |
| route_budget | Tahmini Bütçe | Est. Budget | Label |
| route_places_count | Mekan Sayısı | Number of Places | Label |
| route_start | Başla | Start | Button |
| route_save | Kaydet | Save | Button |
| route_share | Paylaş | Share | Button |
| route_start_navigation | Navigasyonu Başlat | Start Navigation | Button |
| route_itinerary | Detaylı Tur | Detailed Tour | Section |
| route_step | Adım | Step | Label |
| route_via | Üzerinden | Via | Label |

---

## 9. Navigation (4 strings)

| ID | Turkish | English | Category |
|----|---------|---------|----------|
| route_estimated_arrival | Tahmini Varış | Est. Arrival | Label |
| route_crowd_level | Kalabalık Seviyesi | Crowd Level | Label |
| route_sustainability_score | Sürdürülebilirlik Puanı | Sustainability Score | Label |

---

## 10. Map Screen (7 strings)

| ID | Turkish | English | Category |
|----|---------|---------|----------|
| map_title | Harita | Map | Title |
| map_current_location | Mevcut Konum | Current Location | Label |
| map_zoom_in | Yakınlaş | Zoom In | Button |
| map_zoom_out | Uzaklaş | Zoom Out | Button |
| map_show_route | Rotayı Göster | Show Route | Button |
| map_hide_route | Rotayı Gizle | Hide Route | Button |
| map_my_location | Benim Konumum | My Location | Button |

---

## 11. Saved Routes (7 strings)

| ID | Turkish | English | Category |
|----|---------|---------|----------|
| saved_routes_title | Kayıtlı Rotalar | Saved Routes | Title |
| saved_routes_empty | Henüz kayıtlı rota yok | No saved routes yet | Empty State |
| saved_routes_delete | Sil | Delete | Button |
| saved_routes_edit | Düzenle | Edit | Button |
| saved_routes_delete_confirmation | Bu rotayı silmek istediğinizden emin misiniz? | Are you sure you want to delete this route? | Dialog |
| saved_routes_delete_cancel | İptal | Cancel | Button |
| saved_routes_delete_confirm | Sil | Delete | Button |

---

## 12. Feedback Screen (18 strings)

| ID | Turkish | English | Category |
|----|---------|---------|----------|
| feedback_title | Geri Bildirim | Feedback | Title |
| feedback_subtitle | Bu rota hakkında bize bilgi verin | Tell us about this route | Description |
| feedback_rating | Puanı | Rating | Label |
| feedback_rating_1 | Çok Kötü | Very Bad | Option |
| feedback_rating_2 | Kötü | Bad | Option |
| feedback_rating_3 | Orta | Neutral | Option |
| feedback_rating_4 | İyi | Good | Option |
| feedback_rating_5 | Çok İyi | Very Good | Option |
| feedback_comment | Yorum | Comment | Label |
| feedback_comment_placeholder | Bize deneyiminiz hakkında anlatın... | Tell us about your experience... | Placeholder |
| feedback_visited | Ziyaret Ettiniz Mi? | Did you visit? | Question |
| feedback_yes | Evet | Yes | Option |
| feedback_no | Hayır | No | Option |
| feedback_crowded | Kalabalık Mıydı? | Was it crowded? | Question |
| feedback_not_crowded | Boş | Not Crowded | Option |
| feedback_crowded | Kalabalık | Crowded | Option |
| feedback_would_recommend | Tavsiye Ederim | I would recommend | Label |
| feedback_submit | Gönder | Submit | Button |

---

## 13. Profile Screen (22 strings)

### 13.1 Profile Information
| ID | Turkish | English | Category |
|----|---------|---------|----------|
| profile_title | Profil | Profile | Title |
| profile_account | Hesap | Account | Section |
| profile_name | Ad Soyad | Full Name | Label |
| profile_email | E-posta | Email | Label |
| profile_phone | Telefon | Phone | Label |
| profile_preferences | Tercihler | Preferences | Section |
| profile_edit_preferences | Tercihleri Düzenle | Edit Preferences | Button |

### 13.2 Statistics
| ID | Turkish | English | Category |
|----|---------|---------|----------|
| profile_statistics | İstatistikler | Statistics | Section |
| profile_routes_created | Oluşturulan Rotalar | Routes Created | Stat |
| profile_routes_completed | Tamamlanan Rotalar | Routes Completed | Stat |
| profile_places_visited | Ziyaret Edilen Mekanlar | Places Visited | Stat |
| profile_feedback_given | Verilen Geri Bildirim | Feedback Given | Stat |

### 13.3 Settings
| ID | Turkish | English | Category |
|----|---------|---------|----------|
| profile_settings | Ayarlar | Settings | Section |
| profile_notification_settings | Bildirim Ayarları | Notification Settings | Option |
| profile_language | Dil | Language | Option |
| profile_about | Hakkında | About | Option |

### 13.4 Actions
| ID | Turkish | English | Category |
|----|---------|---------|----------|
| profile_logout | Çıkış Yap | Logout | Button |
| profile_delete_account | Hesabı Sil | Delete Account | Button |
| profile_delete_account_warning | Hesabınız ve tüm verileri kalıcı olarak silinecektir | Your account and all data will be permanently deleted | Warning |

---

## 14. Common UI Elements (21 strings)

| ID | Turkish | English | Category |
|----|---------|---------|----------|
| common_ok | Tamam | OK | Button |
| common_cancel | İptal | Cancel | Button |
| common_save | Kaydet | Save | Button |
| common_delete | Sil | Delete | Button |
| common_edit | Düzenle | Edit | Button |
| common_close | Kapat | Close | Button |
| common_loading | Yükleniyor... | Loading... | Loading |
| common_error | Hata Oluştu | Error | Title |
| common_try_again | Tekrar Dene | Try Again | Button |
| common_no_internet | İnternet Bağlantısı Yok | No Internet Connection | Message |
| common_offline | Çevrimdışı Mod | Offline Mode | Message |
| common_back | Geri | Back | Button |
| common_next | İleri | Next | Button |
| common_finish | Bitir | Finish | Button |
| common_continue | Devam Et | Continue | Button |
| common_success | Başarılı | Success | Message |
| common_filter | Filtrele | Filter | Button |
| common_apply | Uygula | Apply | Button |
| common_reset | Sıfırla | Reset | Button |
| common_search | Ara | Search | Label |
| common_empty_state | Veri Yok | No Data | Title |

---

## 15. Error Messages (8 strings)

| ID | Turkish | English | Category |
|----|---------|---------|----------|
| common_empty_state_desc | Bu alan şu anda boş | This section is currently empty | Description |
| error_network | Ağ hatası. Lütfen tekrar deneyin | Network error. Please try again | Error |
| error_server | Sunucu hatası. Lütfen daha sonra deneyiniz | Server error. Please try later | Error |
| error_timeout | İstek zaman aşımına uğradı | Request timeout | Error |
| error_unknown | Bilinmeyen hata oluştu | Unknown error occurred | Error |
| error_invalid_input | Girdi geçersiz | Invalid input | Error |
| error_permission_denied | İzin reddedildi | Permission denied | Error |
| error_location_required | Konum izni gereklidir | Location permission required | Error |

---

## 16. Validation Messages (4 strings)

| ID | Turkish | English | Category |
|----|---------|---------|----------|
| error_no_data | Veri bulunamadı | No data found | Error |
| validation_required | Bu alan zorunludur | This field is required | Validation |
| validation_email_invalid | Geçersiz e-posta adresi | Invalid email address | Validation |
| validation_password_short | Şifre en az 6 karakter olmalı | Password must be at least 6 characters | Validation |

---

## 17. Additional Validation (1 string)

| ID | Turkish | English | Category |
|----|---------|---------|----------|
| validation_phone_invalid | Geçersiz telefon numarası | Invalid phone number | Validation |

---

## 18. Offline & Cache Messages (5 strings)

| ID | Turkish | English | Category |
|----|---------|---------|----------|
| offline_mode_title | Çevrimdışı Mod | Offline Mode | Title |
| offline_mode_desc | İnternet bağlantısı olmadan çalışıyorsunuz | You are working without internet connection | Description |
| offline_cached_data | Önceki verileri görüntülüyorsunuz | Viewing previously cached data | Message |
| offline_changes_will_sync | Bağlantı sağlandığında değişiklikler senkronize edilecektir | Changes will be synced when connection is restored | Message |
| cache_last_updated | Son güncelleme: | Last updated: | Label |

---

## 19. Additional UI (1 string)

| ID | Turkish | English | Category |
|----|---------|---------|----------|
| cache_refresh | Yenile | Refresh | Button |

---

## 20. Feedback Cancel Button (1 string)

| ID | Turkish | English | Category |
|----|---------|---------|----------|
| feedback_cancel | İptal | Cancel | Button |

---

## 21. Success Message for Feedback (1 string)

| ID | Turkish | English | Category |
|----|---------|---------|----------|
| feedback_submitted | Geri bildiriminiz kaydedildi, teşekkürler! | Your feedback has been saved, thank you! | Message |

---

## Turkish Text by Screen

### Login Screen
- Giriş Yap, E-posta, Şifre, Şifremi Unuttum, Hesabın Yok Mu?, Başarıyla giriş yapıldı

### Register Screen
- Kayıt Ol, Ad, Soyad, E-posta, Şifre, Şifre Tekrar, Telefon Numarası, Kayıt başarılı...

### Onboarding
- Hoş Geldiniz, Kişiselleştirilmiş Öneriler, Dinamik Rota Planlama, Yapay Zeka Desteği, Yerel İşletmeleri Destekleyin, Atla, İleri, Başla

### Preference Setup
- Tercihlerinizi Ayarlayın, İlgi Alanları, Bütçe, Ulaşım Tercihi, Kalabalık Toleransı, Müzeler, Parklar, Kafeler, Restoranlar, Kültürel Mekanlar, Tarihi Siteler, Açık Hava Aktiviteleri, Düşük, Orta, Yüksek, Yürüme, Toplu Taşıma, Araba, Bisiklet, Çevre Dostu, Kaydet, Tercihleriniz kaydedildi

### Home Screen
- Anasayfa, Merhaba, Hızlı İşlemler, Öneriler Al, Mekanları Keşfet, Rotalarım, Öne Çıkanlar, Yakınımda, Bugün Popüler, Henüz veri yok, Yükleniyor...

### Discovery Screen
- Mekanları Keşfet, Mekan ara..., Filtrele, Sırala, Popüler, En Yakın, En Yüksek Puan, Tümü, Müze, Park, Kafe, Restoran, Sonuç bulunamadı, Henüz mekan eklenmemiş, Puan, Uzaklık, Tahmini Süre, Bütçe

### Recommendations
- Öneriler, Öneriler oluşturuluyor..., Henüz öneri yok, Neden önerildi?, Uyum: %d, Tercihlerinize uyar, Popüler, Yakınız, Benzer mekanlar sevenler sevdi, Kaydet, Rotaya Ekle, Paylaş

### Route Details
- Rota Detayları, Toplam Süre, Toplam Mesafe, Tahmini Bütçe, Mekan Sayısı, Başla, Kaydet, Paylaş, Navigasyonu Başlat, Detaylı Tur, Adım, Üzerinden, Tahmini Varış, Kalabalık Seviyesi, Sürdürülebilirlik Puanı

### Navigation
- Harita, Mevcut Konum, Yakınlaş, Uzaklaş, Rotayı Göster, Rotayı Gizle, Benim Konumum

### Saved Routes
- Kayıtlı Rotalar, Henüz kayıtlı rota yok, Sil, Düzenle, Bu rotayı silmek istediğinizden emin misiniz?, İptal, Ziyaret Tarihi

### Feedback
- Geri Bildirim, Bu rota hakkında bize bilgi verin, Puanı, Çok Kötü, Kötü, Orta, İyi, Çok İyi, Yorum, Bize deneyiminiz hakkında anlatın..., Ziyaret Ettiniz Mi?, Evet, Hayır, Kalabalık Mıydı?, Boş, Kalabalık, Tavsiye Ederim, Gönder, Geri bildiriminiz kaydedildi, teşekkürler!

### Profile
- Profil, Hesap, Ad Soyad, E-posta, Telefon, Tercihler, Tercihleri Düzenle, İstatistikler, Oluşturulan Rotalar, Tamamlanan Rotalar, Ziyaret Edilen Mekanlar, Verilen Geri Bildirim, Ayarlar, Bildirim Ayarları, Dil, Hakkında, Çıkış Yap, Hesabı Sil, Hesabınız ve tüm verileri kalıcı olarak silinecektir

---

## Common Strings Across All Screens

| Turkish | English | Usage |
|---------|---------|-------|
| Tamam | OK | Dialogs, Alerts |
| İptal | Cancel | Dialogs, Forms |
| Kaydet | Save | Forms, Routes, Preferences |
| Sil | Delete | Lists, Details |
| Düzenle | Edit | Profile, Preferences |
| Kapat | Close | Modals, Sheets |
| Yükleniyor... | Loading... | Data Loading |
| Hata Oluştu | Error | Error Dialogs |
| Tekrar Dene | Try Again | Error States |
| İnternet Bağlantısı Yok | No Internet Connection | Offline Mode |
| Çevrimdışı Mod | Offline Mode | Offline Banner |
| Geri | Back | Navigation |
| İleri | Next | Navigation, Onboarding |
| Bitir | Finish | Onboarding |
| Devam Et | Continue | Forms, Navigation |
| Başarılı | Success | Success Messages |
| Filtrele | Filter | Lists |
| Sırala | Sort | Lists |
| Uygula | Apply | Filters, Dialogs |
| Sıfırla | Reset | Forms, Filters |
| Ara | Search | Search Bars |

---

## String Categories Summary

| Category | Count | Examples |
|----------|-------|----------|
| Authentication | 13 | Login, Register, Email, Password |
| Onboarding | 12 | Welcome, Personalized, Dynamic Routes |
| Preferences | 34 | Interests, Budget, Transportation, Crowd |
| Home | 11 | Home, Hello, Quick Actions |
| Discovery | 15 | Discover, Search, Filter, Sort |
| Recommendations | 12 | Recommendations, Why, Match Score |
| Routes | 12 | Route Details, Duration, Distance |
| Navigation | 4 | Map, Zoom, Route Visibility |
| Saved Routes | 7 | Saved Routes, Delete, Edit |
| Feedback | 18 | Feedback, Rating, Comment, Visited |
| Profile | 22 | Profile, Account, Statistics, Settings |
| Common | 21 | OK, Cancel, Save, Delete, Loading |
| Errors | 8 | Network, Server, Timeout, Unknown |
| Validation | 5 | Required, Email, Password, Phone |
| Offline | 5 | Offline Mode, Cached Data, Refresh |
| **TOTAL** | **200+** | - |

---

## String Naming Conventions

**Format**: `[category]_[subcategory]_[specific]`

**Examples**:
- `auth_login` - Authentication > Login button
- `preferences_budget_low` - Preferences > Budget > Low option
- `recommendations_reason_preference` - Recommendations > Reason > Preference match
- `common_loading` - Common UI > Loading indicator
- `error_network` - Errors > Network error

**Guidelines**:
- Use lowercase with underscores
- Be specific and descriptive
- Group related strings with same prefix
- Avoid generic names like "text" or "message"

---

## Testing Checklist

- [ ] All strings present in strings.xml (Turkish)
- [ ] All strings present in strings.xml (English fallback)
- [ ] String IDs follow naming conventions
- [ ] No hardcoded strings in code
- [ ] Turkish grammar and spelling verified
- [ ] Consistency in terminology across screens
- [ ] String length appropriate for UI layouts
- [ ] Special characters properly escaped
- [ ] Pluralization rules applied
- [ ] Gender-neutral language used where appropriate

---

## Resources for Future Updates

- Main Turkish strings file: `app/src/main/res/values-tr/strings.xml`
- English fallback: `app/src/main/res/values/strings.xml`
- This inventory document for reference

---

**Status**: Complete ✅  
**Total Strings**: 200+ Turkish UI strings  
**Last Updated**: May 2026  
**Phase**: 12 (Turkish Text Inventory)  

