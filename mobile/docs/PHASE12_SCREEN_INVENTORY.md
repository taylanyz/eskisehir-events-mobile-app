# Phase 12: Mobile App Screen Inventory

## Overview

This document provides a complete inventory of all screens in the Eskişehir Events mobile application, including their purpose, components, navigation flows, and Turkish UI elements.

**Total Screens**: 15 core screens  
**Technology**: Jetpack Compose, Kotlin  
**Language**: Turkish (Primary) with English fallback  

---

## 1. Authentication Screens

### 1.1 Login Screen
**File**: `ui/screens/auth/LoginScreen.kt`

**Purpose**: Allow users to authenticate with their existing account

**Components**:
- Email input field with validation
- Password input field (masked)
- "Forgot Password?" link
- Login button
- "Don't have an account?" register link
- Loading indicator during authentication
- Error message display

**State Management**:
- `AuthViewModel.loginState`: Loading, Success, Error
- Validates email format and password length
- Stores auth token securely in DataStore

**Navigation**:
- On success → Home Screen
- On "Register" link → Register Screen
- On password reset → Password Recovery flow

**Turkish Labels**:
```
"Giriş Yap" (Login)
"E-posta" (Email)
"Şifre" (Password)
"Şifremi Unuttum" (Forgot Password)
"Zaten Hesabın Var Mı?" (Already have account)
"Hesabın Yok Mu?" (Don't have account)
```

---

### 1.2 Register Screen
**File**: `ui/screens/auth/RegisterScreen.kt`

**Purpose**: Allow new users to create an account

**Components**:
- First name input
- Last name input
- Email input with validation
- Password input
- Confirm password input
- Phone number input (optional)
- Terms & conditions checkbox
- Register button
- "Already have account?" login link
- Error message display
- Form validation feedback

**State Management**:
- `AuthViewModel.registerState`: Loading, Success, Error
- Form validation on each field change
- Password confirmation validation
- Email uniqueness check via API

**Navigation**:
- On success → Onboarding Screen
- On "Login" link → Login Screen

**Turkish Labels**:
```
"Kayıt Ol" (Register)
"Ad" (First Name)
"Soyad" (Last Name)
"E-posta" (Email)
"Şifre" (Password)
"Şifre Tekrar" (Confirm Password)
"Telefon Numarası" (Phone Number)
"Kayıt Ol" (Register button)
```

---

## 2. Onboarding Screens

### 2.1 Onboarding Screen
**File**: `ui/screens/onboarding/OnboardingScreen.kt`

**Purpose**: Introduce new users to key features of the app

**Structure**: 4-step carousel

**Step 1 - Personalized Recommendations**
- Title: "Kişiselleştirilmiş Öneriler" (Personalized Recommendations)
- Description: "Sizin ilgi alanlarınıza ve tercihlerinize göre özel rota önerileri alın"
- Illustration: Recommendation icon

**Step 2 - Dynamic Route Planning**
- Title: "Dinamik Rota Planlama" (Dynamic Route Planning)
- Description: "Bütçeniz, zamanınız ve tercihlerinize uygun en iyi rotaları keşfedin"
- Illustration: Route optimization icon

**Step 3 - AI-Powered**
- Title: "Yapay Zeka Desteği" (AI-Powered Suggestions)
- Description: "Sistem sizin tercihlerinizden öğrenerek daha iyi öneriler sunar"
- Illustration: AI/ML icon

**Step 4 - Local Support**
- Title: "Yerel İşletmeleri Destekleyin" (Support Local Businesses)
- Description: "Eskişehir'deki yerel işletmeleri keşfedin ve destekleyin"
- Illustration: Local business icon

**Components**:
- Page indicator dots
- Skip button
- Next/Previous buttons
- Finish button
- Illustrations/animations

**State Management**:
- `OnboardingViewModel`: Current step tracking
- Skip logic to go directly to preferences setup

**Navigation**:
- Skip → Preference Setup Screen
- Finish (after step 4) → Preference Setup Screen

---

### 2.2 Preference Setup Screen
**File**: `ui/screens/onboarding/PreferenceSetupScreen.kt`

**Purpose**: Allow users to set initial preferences

**Sections**:

**1. Interests Section**
- Categories: Museums, Parks, Cafes, Restaurants, Cultural, Historical, Outdoor
- Multi-select checkboxes
- Required: At least one must be selected

**2. Budget Section**
- Radio buttons:
  - "Düşük (0-100 ₺)" (Low)
  - "Orta (100-500 ₺)" (Medium)
  - "Yüksek (500+ ₺)" (High)

**3. Transportation Mode**
- Buttons: Walking, Public Transport, Car, Bike
- Single selection

**4. Crowd Tolerance**
- Radio buttons:
  - "Düşük (sessiz yerler)" (Low - quiet)
  - "Orta" (Medium)
  - "Yüksek (canlı yerler)" (High - lively)

**5. Sustainability**
- Toggle: Eco-Friendly preference

**Components**:
- Checkbox groups
- Radio button groups
- Toggle switches
- Save button with validation
- Progress indicator

**State Management**:
- `PreferenceViewModel`: Preference state management
- Saves to backend via UpdateUserPreferencesUseCase

**Navigation**:
- On save → Home Screen

**Turkish Labels**:
```
"Tercihlerinizi Ayarlayın" (Set Your Preferences)
"İlgi Alanları" (Interests)
"Bütçe" (Budget)
"Ulaşım Tercihi" (Transportation)
"Kalabalık Toleransı" (Crowd Tolerance)
"Sürdürülebilirlik" (Sustainability)
"Kaydet" (Save)
```

---

## 3. Main App Screens

### 3.1 Home Screen
**File**: `ui/screens/home/HomeScreen.kt`

**Purpose**: Main dashboard showing quick actions and featured content

**Sections**:

**1. Welcome Section**
- Greeting: "Merhaba, [First Name]" (Hello, [First Name])
- Last activity or date

**2. Quick Actions**
- Buttons:
  - "Öneriler Al" (Get Recommendations)
  - "Mekanları Keşfet" (Discover Places)
  - "Rotalarım" (My Routes)

**3. Featured Section**
- Title: "Öne Çıkanlar" (Featured)
- Horizontal scrollable card list
- Each card shows: Image, name, rating, category

**4. Nearby Places Section**
- Title: "Yakınımda" (Nearby)
- Uses device location
- List of 5-10 nearest POIs
- Click → Place Detail Screen

**5. Popular Today Section**
- Title: "Bugün Popüler" (Popular Today)
- Trending/high-engagement places
- List view

**Components**:
- AppBar with profile icon
- BottomNavigation for main navigation
- POICard component
- LoadingIndicator for data fetch
- EmptyStateView if no data

**State Management**:
- `HomeViewModel`: Manages all sections' state
- Pull-to-refresh functionality
- Offline mode: Shows cached data with "Offline Mode" banner

**Navigation**:
- Bottom nav: Home, Discovery, Recommendations, Routes, Profile
- Tap POI card → Place Detail Screen
- Tap quick action → Respective feature

**Turkish Labels**:
```
"Anasayfa" (Home)
"Merhaba" (Hello)
"Hızlı İşlemler" (Quick Actions)
"Öneriler Al" (Get Recommendations)
"Mekanları Keşfet" (Discover Places)
"Rotalarım" (My Routes)
"Öne Çıkanlar" (Featured)
"Yakınımda" (Nearby)
"Bugün Popüler" (Popular Today)
```

---

### 3.2 Place Discovery Screen
**File**: `ui/screens/discovery/DiscoveryScreen.kt`

**Purpose**: Search and browse all points of interest

**Components**:

**1. Search Bar**
- Placeholder: "Mekan ara..." (Search places...)
- Search-as-you-type
- Clear button

**2. Filter & Sort**
- Filter button → Opens FilterSheet with:
  - Category checkboxes
  - Distance range slider
  - Budget range slider
  - Rating filter
- Sort button → Options:
  - "Popüler" (Popular)
  - "En Yakın" (Nearest)
  - "En Yüksek Puan" (Highest Rated)

**3. Category Pills**
- Horizontal scrollable
- All, Museum, Park, Cafe, Restaurant, etc.
- Single selection (tags mode)

**4. Results List**
- POIListItem for each result
- Shows: Image, name, rating, distance, category tag
- Lazy loading for performance

**5. Empty/Error States**
- "Sonuç bulunamadı" (No results found)
- "Henüz mekan eklenmemiş" (No places added yet)

**State Management**:
- `DiscoveryViewModel`: Search/filter state
- Debounced search API calls
- Cache search results

**Navigation**:
- Tap POI → Place Detail Screen
- Back → Home Screen

**Turkish Labels**:
```
"Mekanları Keşfet" (Discover Places)
"Mekan ara..." (Search places...)
"Filtrele" (Filter)
"Sırala" (Sort)
"Tümü" (All)
"Müze" (Museum)
"Park" (Park)
"Kafe" (Cafe)
"Restoran" (Restaurant)
"Sonuç bulunamadı" (No results found)
```

---

### 3.3 Place Detail Screen
**File**: `ui/screens/discovery/PlaceDetailScreen.kt`

**Purpose**: Show comprehensive details of a single place

**Sections**:

**1. Header**
- Large image carousel
- Place name and category badge
- Rating and review count

**2. Overview**
- Description/about text
- Address and opening hours
- Phone number and website

**3. Key Information**
- Tahmini Süre (Estimated Duration)
- Bütçe (Budget/Price Level)
- Kalabalık Seviyesi (Crowd Level) 
- Outdoor/Indoor indicator
- Accessibility info

**4. Gallery**
- Multiple photos in grid

**5. Map Preview**
- Map showing location
- Directions button

**6. User Reviews Section**
- 3-5 recent reviews
- Rating breakdown
- View all reviews link

**7. Action Buttons**
- "Kaydet" (Save)
- "Rotaya Ekle" (Add to Route)
- "Paylaş" (Share)
- Call/Website buttons

**State Management**:
- `DiscoveryViewModel`: POI detail state
- Save to user's favorites via API

**Navigation**:
- Back → Discovery Screen or Home
- Add to Route → Route Form Screen (pre-filled)
- Map → Map Screen with location highlighted

---

### 3.4 Recommendation Screen
**File**: `ui/screens/recommendation/RecommendationScreen.kt`

**Purpose**: Display AI-generated personalized recommendations

**Components**:

**1. Header**
- Title: "Öneriler" (Recommendations)
- Generate button with settings icon
- Refresh button

**2. Filter/Sort Options**
- Sort by: Match score, Popularity, Distance

**3. Recommendation Cards**
- RecommendationCard for each item with:
  - POI image and name
  - Match score (0-100%)
  - Why recommended explanation
  - Save button
  - Add to route button
  - Similar recommendations link

**4. Why Recommended Section**
- Expandable explanation showing:
  - "Tercihlerinize uyar" (Matches preferences)
  - "Popüler" (Popular)
  - "Yakınız" (Near you)
  - "Benzer mekanlar sevenler sevdi" (Others who like similar places liked this)

**5. Loading & Empty States**
- "Öneriler oluşturuluyor..." (Generating recommendations...)
- "Henüz öneri yok" (No recommendations yet)

**State Management**:
- `RecommendationViewModel`: Recommendations state
- Generate recommendations via GetRecommendationsUseCase
- Track which recommendations user interacts with

**Navigation**:
- Tap card → Recommendation Detail Screen
- Similar link → Similar recommendations list
- Add to route → Route Form Screen

**Turkish Labels**:
```
"Öneriler" (Recommendations)
"Neden önerildi?" (Why recommended?)
"Uyum: %d" (Match: %d%)
"Tercihlerinize uyar" (Matches your preferences)
"Popüler" (Popular)
"Yakınız" (Near you)
"Benzer" (Similar)
"Kaydet" (Save)
"Rotaya Ekle" (Add to Route)
```

---

### 3.5 Route Details Screen
**File**: `ui/screens/route/RouteDetailsScreen.kt`

**Purpose**: Display comprehensive route information

**Sections**:

**1. Route Summary**
- Route name/title
- RouteSummary component showing:
  - Total duration
  - Total distance
  - Est. budget
  - Number of places
  - Crowd level indicator
  - Sustainability score

**2. Route Statistics**
- Tahmini Bütçe (Est. Budget)
- Kalabalık Seviyesi (Crowd Level)
- Sürdürülebilirlik Puanı (Sustainability Score)
- Total walking/travel time

**3. Detailed Itinerary (Expandable)**
- RouteStepItem for each step:
  - Step number
  - Place name and image
  - Arrival time
  - Duration at place
  - Distance from previous
  - Action: Get directions

**4. Explainability Section**
- "Neden bu rota?" (Why this route?)
- Explanation factors

**5. Map Preview**
- Google Maps integration
- Route path visualization
- Start/end markers

**6. Action Buttons**
- "Başla" (Start) → Navigation Screen
- "Kaydet" (Save) → SaveRouteDialog
- "Paylaş" (Share) → Share intent

**State Management**:
- `RouteViewModel`: Route detail state
- Save route to backend

**Navigation**:
- Start → Navigation Screen
- Back → Previous screen
- Place in itinerary → Place Detail

**Turkish Labels**:
```
"Rota Detayları" (Route Details)
"Toplam Süre" (Total Duration)
"Toplam Mesafe" (Total Distance)
"Tahmini Bütçe" (Est. Budget)
"Mekan Sayısı" (Number of Places)
"Başla" (Start)
"Kaydet" (Save)
"Paylaş" (Share)
"Detaylı Tur" (Detailed Tour)
```

---

### 3.6 Navigation Screen
**File**: `ui/screens/route/NavigationScreen.kt`

**Purpose**: Provide turn-by-turn navigation for active route

**Components**:

**1. Map View**
- Full-screen Google Maps
- Route polyline visualization
- Current location marker
- Destination marker
- Place markers along route

**2. Navigation Card (Bottom Sheet)**
- Current step information
- Instruction text
- Distance to next place
- Estimated arrival time
- Action buttons: Skip step, Call POI, Get details

**3. Top Bar**
- Route title/name
- Timer: Elapsed time
- Remaining time
- Close/Exit navigation button

**4. Turn-by-Turn Updates**
- Automatic update as user moves
- Step completion detection
- Audio cues (optional)

**State Management**:
- `NavigationViewModel`: Real-time location tracking
- Google Maps API integration
- Route progress tracking
- Step detection logic

**Navigation**:
- Close → Route Details Screen
- Complete route → Feedback Screen

**Turkish Labels**:
```
"Adım" (Step)
"Üzerinden" (Via)
"Tahmini Varış" (Est. Arrival)
"Sonraki Adım" (Next Step)
"Rotayı Gizle" (Hide Route)
```

---

### 3.7 Saved Routes Screen
**File**: `ui/screens/route/SavedRoutesScreen.kt`

**Purpose**: Display user's saved/favorite routes

**Components**:

**1. Header**
- Title: "Kayıtlı Rotalar" (Saved Routes)
- Sort options: Date, Duration, Popularity

**2. Routes List**
- RouteCard for each saved route:
  - Route image (first POI or custom)
  - Route name
  - Save date: "Ziyaret Tarihi" (Visit Date)
  - Duration and place count
  - Rating/completion status

**3. Route Actions** (Long-press or menu)
- "Başla" (Start) → Navigation Screen
- "Düzenle" (Edit) → Route Form with pre-fill
- "Sil" (Delete) → Delete confirmation
- "Paylaş" (Share)

**4. Empty State**
- "Henüz kayıtlı rota yok" (No saved routes yet)
- Suggestion to create routes

**State Management**:
- `RouteViewModel.savedRoutes`: List of saved routes
- Delete route via API
- Sort/filter local state

**Navigation**:
- Tap route → Route Details Screen
- Start → Navigation Screen

**Turkish Labels**:
```
"Kayıtlı Rotalar" (Saved Routes)
"Henüz kayıtlı rota yok" (No saved routes yet)
"Ziyaret Tarihi" (Visit Date)
"Sil" (Delete)
"Düzenle" (Edit)
"Başla" (Start)
```

---

### 3.8 Map Screen
**File**: `ui/screens/map/MapScreen.kt`

**Purpose**: Standalone map view for exploration and location services

**Components**:

**1. Full Google Maps**
- Current location marker
- POI markers with custom icons
- Route polyline if viewing route
- Cluster markers for density areas

**2. Map Controls**
- Zoom in/out buttons
- My location button (GPS)
- Route layer toggle

**3. Place Popup**
- Tap marker → Shows POI card
- Actions: Details, Add to route, Save

**4. Legend**
- Map legend explaining marker types

**State Management**:
- `MapViewModel`: Location and marker state
- Real-time location updates
- Marker clustering

**Navigation**:
- Tap place → Place Detail Screen

**Turkish Labels**:
```
"Harita" (Map)
"Mevcut Konum" (Current Location)
"Yakınlaş" (Zoom In)
"Uzaklaş" (Zoom Out)
"Rotayı Göster" (Show Route)
"Rotayı Gizle" (Hide Route)
"Benim Konumum" (My Location)
```

---

### 3.9 Feedback Screen
**File**: `ui/screens/feedback/FeedbackScreen.kt`

**Purpose**: Collect user feedback after route completion

**Components**:

**1. Header**
- Title: "Geri Bildirim" (Feedback)
- Subtitle: "Bu rota hakkında bize bilgi verin" (Tell us about this route)
- Route title/info

**2. Rating Section**
- 5-star rating component
- Star labels:
  - 1: "Çok Kötü" (Very Bad)
  - 2: "Kötü" (Bad)
  - 3: "Orta" (Neutral)
  - 4: "İyi" (Good)
  - 5: "Çok İyi" (Very Good)

**3. Comment Section**
- Text input field
- Placeholder: "Bize deneyiminiz hakkında anlatın..." (Tell us about your experience...)
- Character limit indicator

**4. Additional Questions**
- "Ziyaret Ettiniz Mi?" (Did you visit?)
  - Yes/No toggle
- "Kalabalık Mıydı?" (Was it crowded?)
  - Boş (Empty) / Kalabalık (Crowded) toggle
- "Tavsiye Ederim" (I would recommend)
  - Yes/No toggle

**5. Actions**
- "Gönder" (Submit) button
- "İptal" (Cancel) button

**6. Success State**
- "Geri bildiriminiz kaydedildi, teşekkürler!" (Your feedback has been saved, thank you!)

**State Management**:
- `FeedbackViewModel`: Form state management
- Form validation
- Submit via API

**Navigation**:
- Cancel → Back to previous screen
- Submit → Success message → Home Screen

**Turkish Labels**:
```
"Geri Bildirim" (Feedback)
"Bu rota hakkında bize bilgi verin" (Tell us about this route)
"Puanı" (Rating)
"Yorum" (Comment)
"Ziyaret Ettiniz Mi?" (Did you visit?)
"Kalabalık Mıydı?" (Was it crowded?)
"Tavsiye Ederim" (I would recommend)
"Gönder" (Submit)
```

---

### 3.10 Profile Screen
**File**: `ui/screens/profile/ProfileScreen.kt`

**Purpose**: Display user profile, statistics, and settings

**Sections**:

**1. Profile Header**
- User avatar/profile picture
- Full name
- Edit profile button

**2. Account Section**
- Email display
- Phone number display
- Edit Account button

**3. Preferences Section**
- Current preferences summary
- "Tercihleri Düzenle" (Edit Preferences) button

**4. Statistics Section**
- "İstatistikler" (Statistics):
  - Oluşturulan Rotalar (Routes Created)
  - Tamamlanan Rotalar (Routes Completed)
  - Ziyaret Edilen Mekanlar (Places Visited)
  - Verilen Geri Bildirim (Feedback Given)

**5. Settings Section**
- Notification preferences toggle
- Language selection (Turkish/English)
- About section with version info

**6. Actions**
- "Çıkış Yap" (Logout) button
- "Hesabı Sil" (Delete Account) button
  - Warning: "Hesabınız ve tüm verileri kalıcı olarak silinecektir"

**State Management**:
- `ProfileViewModel`: User profile state
- Statistics fetching
- Logout/account deletion

**Navigation**:
- Edit preferences → Preference Edit Screen
- Logout → Login Screen
- Back → Home Screen

**Turkish Labels**:
```
"Profil" (Profile)
"Hesap" (Account)
"Ad Soyad" (Full Name)
"E-posta" (Email)
"Telefon" (Phone)
"Tercihler" (Preferences)
"Tercihleri Düzenle" (Edit Preferences)
"İstatistikler" (Statistics)
"Oluşturulan Rotalar" (Routes Created)
"Tamamlanan Rotalar" (Routes Completed)
"Ziyaret Edilen Mekanlar" (Places Visited)
"Verilen Geri Bildirim" (Feedback Given)
"Ayarlar" (Settings)
"Bildirim Ayarları" (Notification Settings)
"Dil" (Language)
"Hakkında" (About)
"Çıkış Yap" (Logout)
"Hesabı Sil" (Delete Account)
```

---

## 4. Modal/Dialog Screens

### 4.1 Route Generation Dialog
**Purpose**: Configure and generate a new route

**Fields**:
- Start location (auto-filled with current location)
- Route name
- Number of places
- Budget range
- Duration preference
- Interests/categories filter

**Buttons**: Generate, Cancel

---

### 4.2 Filter Sheet
**Purpose**: Filter results on Discovery screen

**Options**:
- Categories (multi-select)
- Distance range (slider)
- Budget range (slider)
- Rating minimum (slider)
- Open now toggle

**Buttons**: Apply, Reset, Cancel

---

### 4.3 Error Dialog
**Purpose**: Display error messages to user

**Components**:
- Error icon
- Error title
- Error description
- Retry button
- Cancel button

**Turkish**: Error messages from ErrorMessages constants

---

### 4.4 Offline Banner
**Purpose**: Alert user to offline mode

**Display**: Top banner showing:
- "Çevrimdışı Mod" (Offline Mode)
- "İnternet Bağlantısı Yok" (No Internet Connection)
- Dismiss button

---

## 5. Navigation Flow Diagram

```
┌─────────────┐
│    Splash   │
└──────┬──────┘
       │
   ┌───▼─────────┐
   │ Auth Check  │
   └───┬───────┬─┘
       │       │
   No  │       │ Yes
       │       │
   ┌───▼──┐  ┌─▼──────────────────┐
   │Login │  │Home (Main App)     │
   │Reg   │  │  ├─ Home Screen    │
   └──┬───┘  │  ├─ Discovery      │
       │      │  ├─ Recommend     │
       │      │  ├─ Routes        │
   ┌───▼──────┴──┤ Map            │
   │Onboarding   │  └─ Profile    │
   │+Preferences │
   └─────────────┘
```

---

## 6. Turkish Text Categories

### Complete Turkish Strings by Category

| Category | Count | Examples |
|----------|-------|----------|
| Authentication | 15 | Giriş Yap, Kayıt Ol, E-posta, Şifre |
| Onboarding | 12 | Hoş Geldiniz, Kişiselleştirilmiş, Dinamik Rota |
| Screens | 50+ | Anasayfa, Mekanları Keşfet, Öneriler, Rotalarım |
| Components | 40+ | Filtrele, Sırala, Kaydet, Sil, İptal |
| Errors | 15+ | Hata Oluştu, İnternet Bağlantısı Yok, Zaman Aşımı |
| Common | 20+ | Tamam, Kapat, Geri, İleri, Devam Et |

**Total Strings**: 200+ Turkish UI strings defined in strings.xml

---

## 7. Screen Design Specifications

### Typography
- **Headers**: 28sp, Bold
- **Titles**: 20sp, Semi-bold
- **Body**: 14sp, Regular
- **Caption**: 12sp, Regular

### Color Palette (Material 3)
- Primary: Eskişehir theme color
- Secondary: Accent color
- Tertiary: Tertiary color
- Error: #B3261E

### Spacing
- Small: 8dp
- Medium: 16dp
- Large: 24dp
- Extra Large: 32dp

### Minimum Touch Target: 48dp

---

## 8. Accessibility Features

All screens implement:
- Content descriptions for images and icons
- Semantic Compose components for screen readers
- Color contrast ≥ 4.5:1 for text
- Keyboard navigation support
- Focus indicators
- Error message announcements

---

## 9. Loading & Empty States

### Loading State
- Circular progress indicator in center
- "Yükleniyor..." (Loading...) text
- Used on first data fetch

### Empty State
- Empty state illustration
- Title: "Henüz veri yok" (No data yet) or similar
- Subtitle with suggestion
- Call-to-action button if applicable

### Error State
- Error icon
- Error message from backend or ErrorMessages constants
- Retry button
- Back button

---

## 10. Offline Mode Strategy

### Cached Data Display
- Previously fetched data shown with last update time
- "Son güncelleme:" (Last updated:) timestamp
- Refresh button to attempt reconnection

### Queue for Upload
- Changes made offline stored locally
- "Bağlantı sağlandığında değişiklikler senkronize edilecektir"
- Automatic sync when reconnected

---

## 11. Performance Considerations

- Lazy loading for lists (LazyColumn/LazyRow)
- Image loading with Coil caching
- ViewModel state retention
- Coroutine cancellation on screen exit
- Database queries with pagination

---

## 12. Testing Screens

- Unit tests for ViewModels
- Compose UI tests for screens and components
- Navigation tests for screen transitions
- Integration tests for complete user flows

---

**Screen Inventory Status**: Complete ✅  
**Total Screens**: 15 core + 4 modals  
**Turkish Localization**: 100%  
**Last Updated**: May 2026  

