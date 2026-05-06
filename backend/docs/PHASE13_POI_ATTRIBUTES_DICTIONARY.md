# Phase 13: POI Attributes Data Dictionary

## Overview

Comprehensive data dictionary defining all attributes for Points of Interest (POI) in the Eskişehir dataset. This document serves as the authoritative reference for POI data structure, validation rules, and usage guidelines.

**Version**: 1.0  
**Status**: Complete ✅  
**Last Updated**: May 6, 2026  

---

## 1. Core Identification Attributes

### 1.1 id
- **Type**: UUID (String)
- **Length**: 36 characters (with hyphens)
- **Required**: YES
- **Unique**: YES
- **Example**: `550e8400-e29b-41d4-a716-446655440000`
- **Format**: `xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx`
- **Description**: Unique identifier for POI, generated as UUID v4
- **Usage**: Primary key in database, reference in all transactions
- **Validation**: Must be valid UUID format

### 1.2 name
- **Type**: String
- **Max Length**: 200 characters
- **Required**: YES
- **Language**: Turkish (Türkçe)
- **Example**: `Kurşunlu Camii`
- **Description**: Primary name of POI in Turkish
- **Usage**: Display in UI, search queries, recommendations
- **Rules**:
  - Must not be empty
  - Should use proper Turkish characters (ç, ğ, ı, ö, ş, ü)
  - No URLs or special characters
  - Should be official/recognized name

### 1.3 englishName
- **Type**: String
- **Max Length**: 200 characters
- **Required**: YES
- **Language**: English
- **Example**: `Kurşunlu Mosque`
- **Description**: English translation of POI name
- **Usage**: API responses, international users, documentation
- **Rules**:
  - Direct translation of Turkish name
  - Proper English conventions
  - No diacritical marks
  - Should match official English names where available

### 1.4 category
- **Type**: Enum (String)
- **Required**: YES
- **Allowed Values**:
  - MUSEUM
  - HISTORICAL_SITE
  - MOSQUE
  - CHURCH
  - SYNAGOGUE
  - PARK
  - GARDEN
  - RIVERSIDE_SPOT
  - SPORTS_FACILITY
  - CAFE
  - RESTAURANT
  - BAKERY
  - MARKET
  - BAZAAR
  - HOTEL
  - GUESTHOUSE
  - LIBRARY
  - GALLERY
  - THEATER
  - CINEMA
  - UNIVERSITY
  - SCHOOL
  - SHOPPING_CENTER
  - LOCAL_BUSINESS
  - CRAFT_WORKSHOP
  - TOUR_OPERATOR
  - TRANSPORTATION_HUB
  - OTHER_CULTURAL
  - OTHER_RECREATIONAL
- **Description**: Primary category classification
- **Usage**: Filtering, recommendations, routing
- **Rules**: Must be exactly one category per POI

### 1.5 district
- **Type**: Enum (String)
- **Required**: YES
- **Allowed Values**:
  - ODUNPAZARI
  - SAZOVA
  - TEPEBAŞ
  - ALPARSLAN
  - KURTULUŞ
  - GAZIOSMANPAŞA
  - YUNUSEMRE
  - KEMALPASA
  - MIHALICCIK
  - OTHER
- **Description**: Administrative district of Eskişehir
- **Usage**: Geographic filtering, area-based discovery
- **Rules**: Must match Eskişehir's official districts

### 1.6 description
- **Type**: String
- **Max Length**: 1000 characters
- **Required**: YES
- **Language**: Turkish (Türkçe)
- **Example**: `16. yüzyılda inşa edilmiş, Odunpazarı'nın en önemli dini ve mimari yapılarından biri...`
- **Description**: Detailed description of POI in Turkish
- **Usage**: UI detail screens, search/SEO, user information
- **Rules**:
  - Informative and engaging
  - Should include historical/cultural context
  - No URLs or contact info (separate fields)
  - Proper Turkish formatting

---

## 2. Geographic & Location Attributes

### 2.1 latitude
- **Type**: Double (Decimal)
- **Precision**: 6 decimal places minimum
- **Range**: 39.70 - 39.85 (Eskişehir bounds)
- **Required**: YES
- **Example**: `39.745412`
- **Description**: Geographic latitude coordinate
- **Usage**: Map display, distance calculations, geospatial queries
- **Validation**:
  - Must be within Eskişehir bounds
  - Must be paired with valid longitude
  - Precision to 6 decimals (~0.1 meter accuracy)
- **Source**: Google Maps, OpenStreetMap, GPS verification

### 2.2 longitude
- **Type**: Double (Decimal)
- **Precision**: 6 decimal places minimum
- **Range**: 30.40 - 30.65 (Eskişehir bounds)
- **Required**: YES
- **Example**: `30.514387`
- **Description**: Geographic longitude coordinate
- **Usage**: Map display, distance calculations, geospatial queries
- **Validation**:
  - Must be within Eskişehir bounds
  - Must be paired with valid latitude
  - Precision to 6 decimals (~0.1 meter accuracy)
- **Source**: Google Maps, OpenStreetMap, GPS verification

### 2.3 address
- **Type**: String
- **Max Length**: 300 characters
- **Required**: YES
- **Language**: Turkish (Türkçe)
- **Example**: `Kurşunlu Camii Sokak, Odunpazarı, Eskişehir`
- **Description**: Full postal address
- **Usage**: Navigation, directions, user communication
- **Format**: [Street/Area], [District], [City]
- **Validation**: Must be complete and verifiable

---

## 3. Operational Hours & Access Attributes

### 3.1 operatingHours
- **Type**: String
- **Format**: `HH:MM-HH:MM` (24-hour format)
- **Required**: YES
- **Example**: `09:00-18:00`
- **Description**: Regular operating hours
- **Usage**: Availability display, accessibility checking
- **Validation**:
  - Valid 24-hour format
  - Start time before end time
  - If 24/7: use `00:00-23:59` or `24H`
- **Special Cases**:
  - `24H` = Always open
  - `CLOSED` = Permanently closed
  - `CALL` = Call for hours

### 3.2 daysClosed
- **Type**: List<String>
- **Allowed Values**: ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"]
- **Required**: NO (empty if open daily)
- **Example**: `["Monday", "Tuesday"]`
- **Description**: Days when POI is typically closed
- **Usage**: Availability calculation, user planning
- **Special Handling**:
  - Religious sites: Friday prayers may affect hours
  - Museums: Often closed on Mondays/public holidays
  - Markets: May be closed certain days

### 3.3 publicHolidaysClosed
- **Type**: Boolean
- **Required**: NO (default: true)
- **Example**: `true`
- **Description**: Whether POI is closed on Turkish public holidays
- **Usage**: Special event planning, availability checking

### 3.4 seasonalClosure
- **Type**: List<String>
- **Format**: `MM-DD to MM-DD`
- **Required**: NO
- **Example**: `["01-01 to 01-10"]`
- **Description**: Seasonal closure periods
- **Usage**: Availability planning, event calendar

---

## 4. Cost & Pricing Attributes

### 4.1 priceLevel
- **Type**: Enum (String)
- **Required**: YES
- **Allowed Values**:
  - FREE (0 ₺)
  - BUDGET (1-50 ₺)
  - MODERATE (51-150 ₺)
  - EXPENSIVE (151-500 ₺)
  - LUXURY (500+ ₺)
- **Example**: `FREE`
- **Description**: Price category for typical visit
- **Usage**: Budget filtering, user preferences matching

### 4.2 estimatedCost
- **Type**: Float
- **Unit**: Turkish Lira (₺)
- **Range**: 0.0 - 10000.0
- **Required**: YES
- **Example**: `0.0` (for free), `25.5` (for paid)
- **Description**: Estimated average cost per person per visit
- **Accuracy**: Within ±20% of actual
- **Update Frequency**: Quarterly
- **Validation**:
  - Must match priceLevel
  - Must be non-negative
  - Should align with category norms

### 4.3 acceptedPaymentMethods
- **Type**: List<String>
- **Allowed Values**:
  - CASH
  - CREDIT_CARD
  - DEBIT_CARD
  - MOBILE_PAYMENT
  - KONTAKT_CARD
  - BANK_TRANSFER
- **Required**: NO
- **Example**: `["CASH", "CREDIT_CARD"]`
- **Description**: Payment methods accepted
- **Usage**: Accessibility display, transaction planning

---

## 5. Duration & Timing Attributes

### 5.1 estimatedVisitDuration
- **Type**: Integer
- **Unit**: Minutes
- **Range**: 15 - 600
- **Required**: YES
- **Example**: `45`
- **Description**: Typical duration of visit
- **Usage**: Route planning, itinerary creation
- **Guidelines**:
  - Quick visit: 15-30 minutes
  - Standard visit: 30-120 minutes
  - Extended visit: 120-300 minutes
  - Full day: 300+ minutes
- **Calculation Basis**:
  - Size of location
  - Typical activities
  - User surveys/reviews

### 5.2 averageVisitDuration
- **Type**: Integer
- **Unit**: Minutes
- **Required**: NO
- **Example**: `50`
- **Description**: Average actual visit duration from user data
- **Usage**: Machine learning, model training
- **Update Frequency**: Monthly
- **Validation**: Should be close to estimatedVisitDuration

### 5.3 bestTimeToVisit
- **Type**: String
- **Format**: `HH:MM` (24-hour format)
- **Required**: NO
- **Example**: `10:00` or `17:30`
- **Description**: Recommended time for visit
- **Usage**: Recommendations, route optimization
- **Rationale**: Avoid peak crowds, optimal experience

---

## 6. Category & Classification Attributes

### 6.1 tags
- **Type**: List<String>
- **Max Count**: 15 tags per POI
- **Allowed Values**: Predefined taxonomy (see later section)
- **Required**: NO (but recommended: minimum 3)
- **Example**: `["historic", "photography", "tourist-attraction"]`
- **Description**: Descriptive tags/labels for POI
- **Usage**: Search, filtering, recommendations
- **Categories**:
  - Location tags (historic, park, waterfront)
  - Amenity tags (wifi, parking, restroom)
  - Experience tags (photography, romantic, educational)
  - Sustainability tags (eco-friendly, local-business)

### 6.2 indoorOutdoor
- **Type**: Enum (String)
- **Required**: YES
- **Allowed Values**:
  - INDOOR (entirely indoors)
  - OUTDOOR (entirely outdoors)
  - MIXED (both indoor and outdoor)
- **Example**: `MIXED`
- **Description**: Location type classification
- **Usage**: Weather-based recommendations, accessibility
- **Implications**:
  - INDOOR: Weather-proof, lighting important
  - OUTDOOR: Weather-dependent, seasonal appeal
  - MIXED: Flexible, all-weather option

---

## 7. Audience & Suitability Attributes

### 7.1 familyFriendly
- **Type**: Boolean
- **Required**: YES
- **Default**: false
- **Example**: `true`
- **Description**: Suitable for families with children
- **Criteria**:
  - Safe environment
  - Activities for children
  - Facilities (restrooms, water, shade)
  - Parking or easy access
- **Usage**: Family-oriented route filtering

### 7.2 childrenFriendly
- **Type**: Boolean
- **Required**: YES
- **Default**: false
- **Example**: `true`
- **Description**: Specifically good for children
- **Criteria**:
  - Educational or entertaining for kids
  - Appropriate content/environment
  - Child-specific amenities
- **Age Group**: Typically 5-12 years

### 7.3 teenFriendly
- **Type**: Boolean
- **Required**: NO
- **Default**: false
- **Description**: Appeals to teenagers (13-17)

### 7.4 seniorFriendly
- **Type**: Boolean
- **Required**: YES
- **Default**: false
- **Example**: `true`
- **Description**: Suitable for elderly visitors
- **Criteria**:
  - Wheelchair accessible
  - Seating available
  - Elevator access
  - Manageable pace
  - Health facilities nearby
- **Age Group**: 65+ years

### 7.5 petFriendly
- **Type**: Boolean
- **Required**: YES
- **Default**: false
- **Example**: `false`
- **Description**: Pets allowed on premises
- **Specification**: `petDetails` field for exceptions

### 7.6 petDetails
- **Type**: String
- **Max Length**: 200 characters
- **Required**: NO (if petFriendly is false)
- **Example**: `Leashed dogs only in outdoor areas`
- **Description**: Specific pet policies

### 7.7 ageRating
- **Type**: Enum (String)
- **Required**: NO
- **Allowed Values**:
  - ALL_AGES
  - 12_PLUS
  - 16_PLUS
  - 18_PLUS
  - ADULTS_ONLY
- **Example**: `ALL_AGES`
- **Description**: Age appropriateness
- **Usage**: Parental guidance, user filtering

---

## 8. Accessibility & Facilities Attributes

### 8.1 wheelchairAccessible
- **Type**: Boolean
- **Required**: YES
- **Example**: `true`
- **Description**: Full wheelchair accessibility
- **Verification**: On-site check or recent reviews
- **Includes**:
  - Accessible entrance
  - Elevator (if multi-floor)
  - Accessible restrooms
  - Accessible pathways
  - Accessible seating

### 8.2 accessibilityLevel
- **Type**: Integer
- **Range**: 1-5
- **Required**: NO
- **Example**: `4`
- **Levels**:
  - 1: No accessibility features
  - 2: Minimal accessibility
  - 3: Basic accessibility
  - 4: Good accessibility
  - 5: Excellent accessibility

### 8.3 parkingAvailable
- **Type**: Boolean
- **Required**: YES
- **Example**: `true`
- **Description**: Parking facility available

### 8.4 parkingType
- **Type**: List<String>
- **Allowed Values**:
  - FREE (free parking)
  - PAID (paid parking)
  - STREET (street parking)
  - LOT (parking lot)
  - UNDERGROUND (underground garage)
  - VALET (valet parking)
- **Required**: NO (if parkingAvailable is true)
- **Example**: `["FREE", "STREET"]`

### 8.5 publicTransitAccess
- **Type**: Boolean
- **Required**: YES
- **Example**: `true`
- **Description**: Public transportation nearby
- **Definition**: Within 500m walking distance

### 8.6 transitTypes
- **Type**: List<String>
- **Allowed Values**:
  - BUS
  - METRO
  - TRAM
  - TRAIN
- **Required**: NO
- **Example**: `["BUS", "TRAM"]`

### 8.7 restRoomAvailable
- **Type**: Boolean
- **Required**: YES
- **Example**: `true`
- **Description**: Restroom facilities available

### 8.8 wifiAvailable
- **Type**: Boolean
- **Required**: YES
- **Example**: `false`
- **Description**: Free WiFi available

### 8.9 foodServiceAvailable
- **Type**: Boolean
- **Required**: YES
- **Example**: `true`
- **Description**: Food/drink available on site

---

## 9. Contact & Digital Presence Attributes

### 9.1 phoneNumber
- **Type**: String
- **Format**: Turkish phone format
- **Required**: NO (if contact not available)
- **Example**: `+90 222 123 4567` or `0222 123 4567`
- **Description**: Contact phone number
- **Validation**:
  - Must be valid Turkish number
  - Prefer +90 format for international
  - Should be verified current

### 9.2 email
- **Type**: String
- **Format**: Valid email format
- **Required**: NO
- **Example**: `info@example.com`
- **Description**: Contact email address
- **Validation**: Must be valid email format

### 9.3 website
- **Type**: String (URL)
- **Format**: Valid URL format
- **Required**: NO
- **Example**: `https://www.example.com`
- **Description**: Official website URL
- **Validation**:
  - Must start with http:// or https://
  - Should be current and accessible

### 9.4 socialMedia
- **Type**: Object/Dictionary
- **Required**: NO
- **Fields**:
  - instagram: Instagram handle (@username)
  - facebook: Facebook page URL
  - twitter: Twitter handle (@username)
- **Example**:
  ```json
  {
    "instagram": "@museumodunpazari",
    "facebook": "https://facebook.com/OMM",
    "twitter": "@OMMuseum"
  }
  ```

---

## 10. Computed Proxy Metrics Attributes

### 10.1 popularityScore
- **Type**: Float
- **Range**: 0.0 - 100.0
- **Required**: YES
- **Precision**: 1 decimal place
- **Example**: `85.5`
- **Description**: Popularity/visit frequency metric
- **Calculation**: See Proxy Scoring Rules document
- **Update Frequency**: Weekly
- **Interpretation**:
  - 0-25: Lesser-known spots
  - 25-50: Known locally
  - 50-75: Popular destinations
  - 75-100: Major attractions

### 10.2 crowdProxyScore
- **Type**: Float
- **Range**: 0.0 - 100.0
- **Required**: YES
- **Precision**: 1 decimal place
- **Example**: `65.0`
- **Description**: Estimated crowd level (time-based)
- **Calculation**: See Proxy Scoring Rules document
- **Update Frequency**: Real-time (model-based)
- **Interpretation**:
  - 0-20: Very quiet
  - 20-40: Quiet
  - 40-60: Moderate
  - 60-80: Busy
  - 80-100: Very busy

### 10.3 sustainabilityScore
- **Type**: Float
- **Range**: 0.0 - 100.0
- **Required**: YES
- **Precision**: 1 decimal place
- **Example**: `78.5`
- **Description**: Environmental & social sustainability metric
- **Calculation**: See Proxy Scoring Rules document
- **Update Frequency**: Quarterly
- **Factors**:
  - Environmental impact
  - Local economy support
  - Cultural preservation
  - Accessibility
  - Green space contribution

### 10.4 localBusinessScore
- **Type**: Float
- **Range**: 0.0 - 100.0
- **Required**: YES
- **Precision**: 1 decimal place
- **Example**: `90.0`
- **Description**: Local business support metric
- **Calculation**: See Proxy Scoring Rules document
- **Update Frequency**: Quarterly
- **Factors**:
  - Ownership locality
  - Local employees
  - Supply chain locality
  - Community engagement

### 10.5 averageRating
- **Type**: Float
- **Range**: 1.0 - 5.0
- **Required**: NO
- **Precision**: 1 decimal place
- **Example**: `4.5`
- **Description**: User rating average
- **Source**: Google Maps, TripAdvisor, internal reviews
- **Calculation**: (sum of all ratings) / (number of ratings)

### 10.6 reviewCount
- **Type**: Integer
- **Range**: 0 - unlimited
- **Required**: NO
- **Example**: `245`
- **Description**: Total number of reviews/ratings
- **Source**: External platforms, internal system

### 10.7 touristAttractionScore
- **Type**: Float
- **Range**: 0.0 - 100.0
- **Required**: NO
- **Precision**: 1 decimal place
- **Example**: `95.0`
- **Description**: Tourist appeal rating
- **Calculation**: Combination of factors
  - Historical/cultural significance
  - Popularity among tourists
  - Rating scores
  - Iconic status

---

## 11. Metadata Attributes

### 11.1 createdAt
- **Type**: DateTime (ISO 8601)
- **Format**: `YYYY-MM-DDTHH:MM:SSZ`
- **Required**: YES
- **Example**: `2026-05-06T10:30:00Z`
- **Description**: Record creation timestamp
- **Usage**: Audit trail, data freshness
- **Auto-generated**: Yes

### 11.2 lastUpdated
- **Type**: DateTime (ISO 8601)
- **Format**: `YYYY-MM-DDTHH:MM:SSZ`
- **Required**: YES
- **Example**: `2026-05-06T10:30:00Z`
- **Description**: Last modification timestamp
- **Usage**: Data freshness, version control
- **Auto-updated**: On any field change

### 11.3 dataSource
- **Type**: String
- **Allowed Values**:
  - RESEARCH (Manual research)
  - OSM (OpenStreetMap)
  - GOOGLE_MAPS (Google Maps API)
  - GOVERNMENT (Official government records)
  - SURVEY (User survey)
  - MANUAL (Manual data entry)
  - IMPORT (External import)
- **Required**: YES
- **Example**: `RESEARCH`
- **Description**: Origin of data

### 11.4 verified
- **Type**: Boolean
- **Required**: YES
- **Default**: false
- **Example**: `true`
- **Description**: Data verification status
- **Definition**: Cross-referenced with multiple sources

### 11.5 verifiedBy
- **Type**: String
- **Required**: NO (if verified is false)
- **Example**: `researcher@university.edu`
- **Description**: Email/ID of person who verified
- **Usage**: Accountability, contact for questions

### 11.6 verificationDate
- **Type**: DateTime (ISO 8601)
- **Required**: NO (if verified is false)
- **Example**: `2026-05-01T14:30:00Z`
- **Description**: When data was verified

### 11.7 notes
- **Type**: String
- **Max Length**: 500 characters
- **Required**: NO
- **Example**: `Opening hours vary seasonally. Best to call ahead for confirmation.`
- **Description**: Internal notes about POI
- **Usage**: Data maintenance, reminders for updates

---

## 12. Example POI Record

```json
{
  "id": "uuid-museum-001",
  "name": "Kurşunlu Camii",
  "englishName": "Kurşunlu Mosque",
  "category": "MOSQUE",
  "district": "ODUNPAZARI",
  "description": "16. yüzyılda inşa edilmiş, Odunpazarı'nın en önemli dini ve mimari yapılarından biri olan Kurşunlu Camii...",
  "latitude": 39.745412,
  "longitude": 30.514387,
  "address": "Kurşunlu Camii Sokak, Odunpazarı, Eskişehir",
  "operatingHours": "06:00-22:00",
  "daysClosed": [],
  "priceLevel": "FREE",
  "estimatedCost": 0.0,
  "estimatedVisitDuration": 45,
  "tags": ["historic", "religious", "photography", "tourist-attraction"],
  "indoorOutdoor": "MIXED",
  "familyFriendly": true,
  "childrenFriendly": true,
  "seniorFriendly": true,
  "petFriendly": false,
  "wheelchairAccessible": false,
  "accessibilityLevel": 2,
  "parkingAvailable": true,
  "parkingType": ["STREET"],
  "publicTransitAccess": true,
  "transitTypes": ["BUS", "TRAM"],
  "restRoomAvailable": true,
  "wifiAvailable": false,
  "foodServiceAvailable": false,
  "phoneNumber": "+90 222 123 4567",
  "email": "info@kurşunlucamii.org",
  "website": "https://www.kurşunlucamii.org",
  "socialMedia": {
    "instagram": "@kurşunlucamii",
    "facebook": "https://facebook.com/kurşunlucamii"
  },
  "popularityScore": 85.5,
  "crowdProxyScore": 65.0,
  "sustainabilityScore": 78.5,
  "localBusinessScore": 0.0,
  "averageRating": 4.5,
  "reviewCount": 245,
  "touristAttractionScore": 95.0,
  "createdAt": "2026-05-06T10:30:00Z",
  "lastUpdated": "2026-05-06T10:30:00Z",
  "dataSource": "RESEARCH",
  "verified": true,
  "verifiedBy": "researcher@university.edu",
  "verificationDate": "2026-05-05T15:00:00Z",
  "notes": "Popular tourist destination. Friday prayers 12:00-13:00."
}
```

---

## 13. Data Type Reference

### 13.1 Primitive Types
- **String**: Text up to specified max length
- **Integer**: Whole numbers
- **Float/Double**: Decimal numbers
- **Boolean**: true/false
- **DateTime**: ISO 8601 format (YYYY-MM-DDTHH:MM:SSZ)
- **UUID**: 36-character unique identifier

### 13.2 Collection Types
- **List<String>**: Array of strings
- **List<Enum>**: Array of enum values
- **Object/Dictionary**: Nested key-value structure

---

## 14. Validation & Quality Rules

### 14.1 Mandatory Field Validation
- All "Required: YES" fields must be populated
- Empty strings not acceptable for required fields
- Null values only acceptable for optional fields

### 14.2 Range Validation
- Float/Integer values must be within specified ranges
- Coordinates must be within Eskişehir bounds
- Scores must be 0-100

### 14.3 Format Validation
- Phone numbers must match Turkish format
- URLs must be valid and accessible
- Dates must be valid ISO 8601 format

### 14.4 Logical Validation
- `averageVisitDuration` should be close to `estimatedVisitDuration`
- If `wheelchairAccessible` is true, `accessibilityLevel` should be ≥4
- Proxy scores should be within reasonable ranges for category

---

## 15. Data Migration & Import

### 15.1 CSV Import Format

```
id,name,englishName,category,district,latitude,longitude,priceLevel,estimatedCost,estimatedVisitDuration,familyFriendly,childrenFriendly,seniorFriendly,petFriendly,wheelchairAccessible,parkingAvailable,publicTransitAccess,phoneNumber,website,popularityScore,crowdProxyScore,sustainabilityScore,localBusinessScore
uuid-1,Kurşunlu Camii,Kurşunlu Mosque,MOSQUE,ODUNPAZARI,39.745412,30.514387,FREE,0.0,45,true,true,true,false,false,true,true,+90 222 123 4567,https://example.com,85.5,65.0,78.5,0.0
```

### 15.2 SQL Insert Template

```sql
INSERT INTO pois (id, name, englishName, category, district, latitude, longitude, description, address, operatingHours, daysClosed, priceLevel, estimatedCost, estimatedVisitDuration, tags, indoorOutdoor, familyFriendly, childrenFriendly, seniorFriendly, petFriendly, wheelchairAccessible, parkingAvailable, publicTransitAccess, phoneNumber, email, website, popularityScore, crowdProxyScore, sustainabilityScore, localBusinessScore, averageRating, reviewCount, createdAt, lastUpdated, dataSource, verified)
VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
```

---

## 16. API Response Examples

### 16.1 GET /api/pois/{id}

```json
{
  "status": "success",
  "data": {
    "id": "uuid-museum-001",
    "name": "Kurşunlu Camii",
    "englishName": "Kurşunlu Mosque",
    "category": "MOSQUE",
    "district": "ODUNPAZARI",
    "description": "16. yüzyılda inşa edilmiş, Odunpazarı'nın en önemli dini ve mimari yapılarından biri olan Kurşunlu Camii...",
    "latitude": 39.745412,
    "longitude": 30.514387,
    "address": "Kurşunlu Camii Sokak, Odunpazarı, Eskişehir",
    "operatingHours": "06:00-22:00",
    "priceLevel": "FREE",
    "estimatedCost": 0.0,
    "estimatedVisitDuration": 45,
    "tags": ["historic", "religious", "photography"],
    "familyFriendly": true,
    "wheelchairAccessible": false,
    "parkingAvailable": true,
    "publicTransitAccess": true,
    "averageRating": 4.5,
    "reviewCount": 245,
    "popularityScore": 85.5,
    "crowdProxyScore": 65.0,
    "sustainabilityScore": 78.5,
    "phoneNumber": "+90 222 123 4567",
    "website": "https://www.kurşunlucamii.org"
  }
}
```

---

## 17. Completeness Checklist

- [ ] All required fields populated
- [ ] All optional fields considered
- [ ] Coordinates verified within Eskişehir
- [ ] Names verified in Turkish & English
- [ ] Opening hours realistic
- [ ] Prices align with category
- [ ] Tags appropriate and specific
- [ ] Accessibility data accurate
- [ ] Contact info current
- [ ] Proxy scores calculated
- [ ] Data cross-referenced with sources
- [ ] Final verification completed

---

**Status**: Complete ✅  
**Version**: 1.0  
**Last Updated**: May 6, 2026  
**Phase**: 13 (POI Dictionary)  

