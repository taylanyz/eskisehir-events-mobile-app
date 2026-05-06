# Phase 13: Eskişehir-Specific MVP Dataset Expansion

## Executive Summary

Phase 13 focuses on creating a comprehensive, thesis-grade dataset of Points of Interest (POIs) in Eskişehir for the MVP. This dataset will support prototyping, testing, and thesis experiments with realistic Eskişehir-specific data including locations, attributes, and computed proxy metrics.

**Status**: In Progress 🚀  
**Target**: Complete dataset with 150-200 POIs  
**Scope**: Eskişehir city center with focus areas: Odunpazarı, Sazova, and surrounding districts  

---

## 1. Strategic Objective

### 1.1 Purpose
- Provide realistic test data for recommendation and route optimization algorithms
- Support thesis experiments with thesis-quality Eskişehir dataset
- Enable development and validation of mobile application features
- Create reusable seed data for future testing and demonstrations

### 1.2 Scope & Scale
- **Geographic Coverage**: Eskişehir city center and key districts
- **POI Count Target**: 150-200 points of interest
- **Data Quality**: Thesis-ready accuracy and completeness
- **Update Frequency**: Static for MVP, can be evolved for later phases

---

## 2. Coverage Areas

### 2.1 Geographic Districts

#### Odunpazarı District (Historic Center)
**Characteristics**: Historic city center, pedestrian-friendly, tourist focus
**POI Types**: Museums, historic sites, cafes, restaurants, shops
**Estimated POIs**: 40-50

**Key Landmarks**:
- Kurşunlu Mosque (Kurşunlu Camii)
- Eski Spa Complex (Eski Kaplıcalar)
- Ethnographic Museum
- Odunpazarı Modern Museum (OMM)
- Odunpazarı Neighborhood

#### Sazova District
**Characteristics**: Suburban, modern, family-friendly
**POI Types**: Parks, recreational centers, modern cafes
**Estimated POIs**: 20-25

**Key Landmarks**:
- Sazova Kültür Parkı (Sazova Culture Park)
- Theme parks
- Recreation centers

#### Central Business District (Tepebağ, Cumhuriyet)
**Characteristics**: Business center, offices, shopping, dining
**POI Types**: Shopping centers, restaurants, cafes, hotels
**Estimated POIs**: 30-35

#### Educational & Cultural Zone
**Characteristics**: Universities, research centers
**POI Types**: Libraries, galleries, educational venues
**Estimated POIs**: 15-20

#### Recreation & Nature Areas
**Characteristics**: Parks, outdoor spaces, riverside
**POI Types**: Parks, sports facilities, riverside venues
**Estimated POIs**: 25-30

#### Hotels & Accommodation
**Distributed across all areas**
**Estimated POIs**: 10-15

#### Other Categories (Bazaars, Markets, Local Businesses)
**Distributed across city**
**Estimated POIs**: 15-20

### 2.2 Coverage Matrix

| District | Museums | Parks | Cafes | Restaurants | Cultural | Historical | Hotels |
|----------|---------|-------|-------|------------|----------|-----------|--------|
| Odunpazarı | 8 | 3 | 15 | 12 | 7 | 10 | 3 |
| Sazova | 2 | 10 | 8 | 6 | 2 | 0 | 2 |
| CBD | 1 | 2 | 20 | 20 | 2 | 2 | 5 |
| Education | 3 | 2 | 10 | 5 | 5 | 2 | 2 |
| Recreation | 0 | 15 | 5 | 8 | 0 | 0 | 1 |
| Other | 2 | 3 | 8 | 8 | 3 | 3 | 2 |
| **TOTAL** | **16** | **35** | **66** | **59** | **19** | **17** | **15** |

---

## 3. POI Attributes Structure

### 3.1 Core Attributes (Required)

| Attribute | Type | Description | Example |
|-----------|------|-------------|---------|
| `id` | UUID | Unique identifier | `uuid-1a2b3c4d` |
| `name` | String | Turkish name of POI | "Kurşunlu Camii" |
| `englishName` | String | English name | "Kurşunlu Mosque" |
| `category` | Enum | POI category | MUSEUM, PARK, CAFE, etc. |
| `district` | Enum | Eskişehir district | ODUNPAZARI, SAZOVA, CBD, etc. |
| `latitude` | Double | Geographic latitude | 39.7454 |
| `longitude` | Double | Geographic longitude | 30.5144 |
| `description` | String | Short description (Turkish) | "16. yüzyılda inşa edilmiş tarihi camii..." |

### 3.2 Operational Attributes

| Attribute | Type | Description | Example |
|-----------|------|-------------|---------|
| `estimatedVisitDuration` | Integer | Minutes at location | 45 |
| `averageVisitDuration` | Integer | Based on user data | 50 |
| `operatingHours` | String | Business hours | "09:00-18:00" |
| `daysClosed` | List<String> | Days when closed | ["Monday"] |
| `priceLevel` | Enum | Cost category | BUDGET, MODERATE, EXPENSIVE, FREE |
| `estimatedCost` | Float | Average cost (₺) | 0.0 (free) |

### 3.3 Categorical Attributes

| Attribute | Type | Description | Example |
|-----------|------|-------------|---------|
| `tags` | List<String> | Keywords/labels | ["historic", "UNESCO", "photography"] |
| `indoorOutdoor` | Enum | Location type | INDOOR, OUTDOOR, MIXED |
| `accessibilityLevel` | Integer | 1-5 scale | 4 (good wheelchair access) |
| `wheelchairAccessible` | Boolean | Wheelchair access | true |
| `parkingAvailable` | Boolean | Parking info | true |
| `publicTransitAccess` | Boolean | Transit nearby | true |

### 3.4 Audience & Suitability

| Attribute | Type | Description | Example |
|-----------|------|-------------|---------|
| `familyFriendly` | Boolean | Suitable for families | true |
| `childrenFriendly` | Boolean | Good for children | true |
| `seniorFriendly` | Boolean | Good for elderly | true |
| `petFriendly` | Boolean | Pets allowed | false |
| `ageRating` | Enum | Target age group | ALL_AGES, 12_PLUS, 16_PLUS |

### 3.5 Contact & Location Information

| Attribute | Type | Description | Example |
|-----------|------|-------------|---------|
| `address` | String | Full address | "Kurşunlu Camii Sokak, Odunpazarı" |
| `phoneNumber` | String | Contact phone | "+90 222 123 4567" |
| `website` | String | Web URL | "https://example.com" |
| `email` | String | Contact email | "info@example.com" |
| `instagramHandle` | String | Social media | "@museumodunpazari" |

### 3.6 Computed Proxy Metrics

| Attribute | Type | Description | Range | Example |
|-----------|------|-------------|-------|---------|
| `popularityScore` | Float | Popularity/visit count | 0-100 | 85 |
| `crowdProxyScore` | Float | Estimated crowd level | 0-100 | 45 |
| `sustainabilityScore` | Float | Eco-friendly rating | 0-100 | 75 |
| `localBusinessScore` | Float | Support local metric | 0-100 | 90 |
| `touristAttraction` | Float | Tourism appeal | 0-100 | 95 |
| `averageRating` | Float | User rating | 1-5 | 4.5 |
| `reviewCount` | Integer | Number of reviews | 0+ | 245 |

### 3.7 Metadata

| Attribute | Type | Description | Example |
|-----------|------|-------------|---------|
| `createdAt` | DateTime | Record creation date | 2026-05-06T10:30:00Z |
| `lastUpdated` | DateTime | Last modification date | 2026-05-06T10:30:00Z |
| `dataSource` | String | Where data came from | "research", "osm", "manual" |
| `verified` | Boolean | Data verification status | true |
| `verified By` | String | Who verified data | "researcher@university.edu" |

---

## 4. Data Categories & Taxonomies

### 4.1 Category Enumeration

```
MUSEUM
HISTORICAL_SITE
MOSQUE
CHURCH
SYNAGOGUE
PARK
GARDEN
RIVERSIDE_SPOT
SPORTS_FACILITY
CAFE
RESTAURANT
BAKERY
MARKET
BAZAAR
HOTEL
GUESTHOUSE
LIBRARY
GALLERY
THEATER
CINEMA
UNIVERSITY
SCHOOL
SHOPPING_CENTER
LOCAL_BUSINESS
CRAFT_WORKSHOP
TOUR_OPERATOR
TRANSPORTATION_HUB
OTHER_CULTURAL
OTHER_RECREATIONAL
```

### 4.2 District Enumeration

```
ODUNPAZARI
SAZOVA
TEPEBAŞ
ALPARSLAN
KURTULUŞ
GAZIOSMANPAŞA
YUNUSEMRE
KEMALPASA
MIHALICCIK
```

### 4.3 Price Level Enumeration

```
FREE (0 ₺)
BUDGET (0-50 ₺)
MODERATE (50-150 ₺)
EXPENSIVE (150-500 ₺)
LUXURY (500+ ₺)
```

### 4.4 Crowd Proxy Levels

```
VERY_QUIET (0-20%)
QUIET (20-40%)
MODERATE (40-60%)
BUSY (60-80%)
VERY_BUSY (80-100%)
```

---

## 5. POI Tagging System

### 5.1 Location Tags

- `historic` - Historical significance
- `museum` - Museum or exhibition space
- `park` - Park or green space
- `waterfront` - Near water/riverside
- `shopping` - Shopping/commercial
- `dining` - Food/dining
- `cultural` - Cultural venue
- `religious` - Religious site
- `scenic` - Scenic views
- `photography` - Good for photography

### 5.2 Amenity Tags

- `wifi` - Free WiFi available
- `parking` - Parking available
- `restroom` - Restroom available
- `wheelchair-accessible` - Wheelchair accessible
- `pet-friendly` - Pets allowed
- `family-friendly` - Good for families
- `gift-shop` - Gift shop available
- `cafe` - Café or food inside
- `guided-tours` - Guided tours available
- `audio-guide` - Audio guide available

### 5.3 Experience Tags

- `photography` - Good for photography
- `instagram-worthy` - Social media appeal
- `quiet` - Peaceful, quiet place
- `lively` - Energetic, lively atmosphere
- `romantic` - Romantic setting
- `educational` - Educational value
- `outdoor-activity` - Outdoor activities
- `indoor` - Indoor activities
- `rainy-day` - Good for rainy weather
- `sunset-views` - Good sunset views

### 5.4 Sustainability Tags

- `eco-friendly` - Environmentally friendly
- `local-business` - Local-owned business
- `traditional-craft` - Traditional crafts
- `recycled-products` - Sells/uses recycled products
- `zero-waste` - Zero-waste initiative
- `organic-food` - Organic products
- `car-free` - No car required to visit
- `public-transit` - Easy public transit access

---

## 6. Proxy Scoring Algorithms

### 6.1 Popularity Score Calculation

**Formula**: Based on multiple factors

```
popularityScore = (
    (category_base_score * 0.3) +
    (location_accessibility * 0.2) +
    (historical_significance * 0.2) +
    (tourist_appeal * 0.15) +
    (local_reputation * 0.15)
) * 100

Range: 0-100
Interpretation:
- 0-25: Lesser-known spots
- 25-50: Known locally
- 50-75: Popular destinations
- 75-100: Major attractions
```

**Category Base Scores**:
- MUSEUM: 85
- HISTORICAL_SITE: 90
- PARK: 65
- RESTAURANT: 55
- CAFE: 45
- LOCAL_BUSINESS: 40
- HOTEL: 60
- OTHER: 30

### 6.2 Crowd Proxy Score Calculation

**Methodology**: Time-based and location-based estimation

```
crowdProxyScore = (
    (time_of_day_factor * 0.3) +
    (day_of_week_factor * 0.25) +
    (capacity_utilization * 0.25) +
    (seasonal_factor * 0.2)
) * 100

Range: 0-100
Interpretation:
- 0-20: Very quiet (ideal for solitude)
- 20-40: Quiet (peaceful)
- 40-60: Moderate (mixed)
- 60-80: Busy (energetic)
- 80-100: Very busy (crowded)
```

**Time of Day Factors**:
- Morning (6-10): 0.3 (quiet)
- Late Morning (10-12): 0.5 (moderate)
- Lunch (12-14): 0.8 (busy)
- Afternoon (14-17): 0.6 (moderate-busy)
- Evening (17-20): 0.7 (busy)
- Night (20-23): 0.5 (moderate)
- Late Night (23-6): 0.1 (quiet/closed)

**Day of Week Factors**:
- Monday-Thursday: 0.5 (quiet)
- Friday: 0.7 (moderate-busy)
- Saturday: 0.9 (very busy)
- Sunday: 0.8 (busy)

**Capacity Utilization**:
- Museums, galleries: Small capacity (0.7)
- Parks, riverside: Large capacity (0.4)
- Restaurants, cafes: Medium capacity (0.6)

**Seasonal Factors**:
- Summer: 0.8 (tourism high)
- Spring/Fall: 0.6 (moderate)
- Winter: 0.4 (low tourism)

### 6.3 Sustainability Score Calculation

```
sustainabilityScore = (
    (environmental_impact * 0.25) +
    (local_economy_support * 0.3) +
    (cultural_preservation * 0.2) +
    (accessibility * 0.15) +
    (green_space_contribution * 0.1)
) * 100

Range: 0-100
```

**Environmental Impact**:
- Parks, green spaces: 1.0
- Public venues with eco-initiatives: 0.8
- Traditional/historic buildings: 0.7
- Restaurants with sustainable practices: 0.6
- Regular businesses: 0.3
- High-impact businesses: 0.1

**Local Economy Support**:
- Family-owned business: 1.0
- Local cooperative: 0.9
- Small local business: 0.8
- Part of local chain: 0.5
- National chain: 0.2
- International chain: 0.1

**Cultural Preservation**:
- UNESCO World Heritage: 1.0
- Historic site with active preservation: 0.9
- Cultural institution: 0.8
- Historical building (functional): 0.6
- Modern building: 0.2

**Accessibility**:
- Excellent (wheelchair, transit, walking): 1.0
- Good (two of three): 0.7
- Moderate (one of three): 0.4
- Poor (none): 0.1

**Green Space Contribution**:
- Large park or garden: 1.0
- Medium park: 0.7
- Small garden: 0.4
- No green space: 0.1

### 6.4 Local Business Support Score

```
localBusinessScore = (
    (ownership_local * 0.4) +
    (employee_local * 0.3) +
    (supply_chain_local * 0.2) +
    (community_engagement * 0.1)
) * 100

Range: 0-100
```

**Ownership Local**:
- Family-owned, generations: 1.0
- Recent family-owned: 0.9
- Local cooperative: 0.85
- Small local business: 0.7
- Part-local chain: 0.4
- Corporate/chain: 0.1

**Employee Local**:
- 100% local employees: 1.0
- 75%+ local: 0.8
- 50%+ local: 0.6
- <50% local: 0.3
- No local employees: 0.1

**Supply Chain Local**:
- 100% local sourcing: 1.0
- 75%+ local: 0.8
- 50%+ local: 0.6
- <50% local: 0.3
- No local sourcing: 0.1

**Community Engagement**:
- Active cultural/social initiatives: 1.0
- Some community involvement: 0.6
- Minimal involvement: 0.2
- No involvement: 0.0

---

## 7. Data Quality Assurance

### 7.1 Validation Rules

- **Coordinates**: Must be within Eskişehir bounds
  - Latitude: 39.7-39.8
  - Longitude: 30.4-30.6
- **Names**: Must be non-empty, Turkish (primary) + English (secondary)
- **Category**: Must be valid enum value
- **Opening Hours**: Must follow HH:MM-HH:MM format
- **Scores**: Must be 0-100 range
- **Phone**: Optional but must follow Turkish phone format if provided
- **Website**: Must be valid URL format if provided

### 7.2 Completeness Checklist

- [ ] All required fields populated
- [ ] Coordinates verified within Eskişehir bounds
- [ ] Turkish names verified for accuracy
- [ ] English translations quality-checked
- [ ] Opening hours realistic
- [ ] Prices reasonable for category
- [ ] Tags appropriate for category
- [ ] Descriptions informative and accurate
- [ ] Contact information current
- [ ] Proxy scores calculated correctly

### 7.3 Accuracy Verification

- Cross-reference with public sources (Google Maps, Wikipedia, official websites)
- Verify addresses in Eskişehir public records
- Check opening hours with official sources or calls
- Validate price levels from recent visitor reviews
- Confirm coordinates with mapping tools

---

## 8. Data Export Formats

### 8.1 JSON Format (Primary)

```json
{
  "id": "uuid-1a2b3c4d",
  "name": "Kurşunlu Camii",
  "englishName": "Kurşunlu Mosque",
  "category": "MOSQUE",
  "district": "ODUNPAZARI",
  "latitude": 39.7454,
  "longitude": 30.5144,
  "description": "16. yüzyılda inşa edilmiş tarihi camii...",
  "estimatedVisitDuration": 45,
  "operatingHours": "06:00-22:00",
  "daysClosed": [],
  "priceLevel": "FREE",
  "estimatedCost": 0.0,
  "tags": ["historic", "religious", "photography"],
  "indoorOutdoor": "MIXED",
  "familyFriendly": true,
  "childrenFriendly": true,
  "wheelchairAccessible": false,
  "address": "Kurşunlu Camii Sokak, Odunpazarı",
  "phoneNumber": "+90 222 123 4567",
  "website": "https://example.com",
  "instagram": "@kurşunlucamii",
  "popularityScore": 85.5,
  "crowdProxyScore": 65.0,
  "sustainabilityScore": 78.5,
  "localBusinessScore": 95.0,
  "averageRating": 4.5,
  "reviewCount": 245,
  "createdAt": "2026-05-06T10:30:00Z",
  "verified": true
}
```

### 8.2 CSV Format (Tabular)

Columns: id, name, englishName, category, district, latitude, longitude, priceLevel, estimatedDuration, familyFriendly, popularityScore, crowdProxyScore, sustainabilityScore, localBusinessScore

### 8.3 SQL Format (Database)

INSERT statements for direct database population

---

## 9. Implementation Timeline

### Phase 13.1: Strategy & Structure (Week 1)
- Define dataset scope and coverage areas ✅
- Create POI attribute schema ✅
- Design proxy scoring algorithms ✅
- Create data dictionary documentation

### Phase 13.2: Data Collection (Weeks 2-3)
- Research and collect POI data from public sources
- Verify locations and opening hours
- Create initial seed dataset (150-200 POIs)

### Phase 13.3: Proxy Calculation (Week 4)
- Implement proxy scoring algorithms
- Calculate all proxy metrics for POIs
- Validate score distributions

### Phase 13.4: Data Export & Testing (Week 5)
- Export data in JSON/CSV formats
- Validate data quality
- Integration testing with backend

---

## 10. Data Sources & References

### 10.1 Primary Sources

1. **Google Maps API**
   - POI locations, opening hours, ratings
   - User reviews for sentiment analysis

2. **OpenStreetMap (OSM)**
   - Geographic coordinates
   - POI classifications
   - Amenity information

3. **Turkish Tourism Board (Kultur ve Turizm Bakanlığı)**
   - Official tourism information
   - Cultural heritage sites

4. **Municipality of Eskişehir (Eskişehir Belediyesi)**
   - Public records
   - City infrastructure
   - Parks and facilities

5. **Wikipedia & Travel Guides**
   - Historical information
   - Cultural descriptions

### 10.2 Manual Research Sources

- Travel blogs and tourism websites
- Social media (Instagram, TripAdvisor)
- Local directory and business listings
- Academic papers on Eskişehir tourism
- Historical records and cultural databases

---

## 11. Next Steps

1. ✅ Define dataset strategy and structure
2. 🔄 Create detailed POI data dictionary
3. 🔄 Develop proxy scoring algorithms
4. Collect and compile POI data
5. Implement automated scoring calculations
6. Generate seed dataset JSON/CSV files
7. Validate data quality
8. Integrate with backend database
9. Create data management utilities
10. Document data pipeline

---

## 12. Deliverables Checklist

- [ ] Eskişehir data strategy document (this file)
- [ ] POI attributes dictionary
- [ ] Proxy scoring rules documentation
- [ ] Seed data generator code (Java)
- [ ] Sample seed dataset (JSON, 150-200 POIs)
- [ ] Data validation utility
- [ ] Data export tools (JSON, CSV, SQL)
- [ ] Quality assurance report

---

**Status**: In Progress 🚀  
**Last Updated**: May 6, 2026  
**Phase**: 13 (Dataset Expansion)  
**Priority**: High

