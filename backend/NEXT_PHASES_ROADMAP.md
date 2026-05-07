# Eskişehir Events Mobile App

## Phase 5 Sonrası Tez Odaklı Yol Haritası

Bu doküman, mevcut Kotlin + Jetpack Compose mobil istemci ve Spring Boot backend mimarisini koruyarak, Phase 5 sonrasındaki geliştirmeleri tez kalitesinde, akademik olarak savunulabilir ve üretim mantığıyla sürdürülebilir hale getirmek için güncellenmiştir.

## 1. Stratejik Çerçeve

### Proje Başlığı
Mobile-Based Intelligent Tourism Application: AI-Supported Personalized Route Planning System

### Proje Hedefi
Kullanıcının ilgi alanları, bütçesi, zamanı, ulaşım tercihi, kalabalık toleransı, sürdürülebilirlik beklentisi ve bağlamsal verilerine göre Eskişehir odaklı kişiselleştirilmiş öneriler ve dinamik rota planları üretmek.

### Temel Farklılaştırıcılar
- AI destekli kişiselleştirme
- Statik öneri yerine dinamik rota üretimi
- Çok kriterli rota optimizasyonu
- Sürdürülebilirlik farkındalığı
- Kalabalıktan kaçınma
- Yerel işletmeleri öne çıkarma
- Kullanıcı etkileşimlerinden ve metinsel geri bildirimlerden öğrenme

### Lokalizasyon Kuralları
- Mobil uygulamadaki tüm görünür metinler Türkçe olacak.
- İlk tam model şehir Eskişehir olacak.
- POI örnekleri, senaryolar, seed data ve rota örnekleri Eskişehir merkezli hazırlanacak.
- Backend kodu, mimari belgeler ve teknik açıklamalar İngilizce olabilir.
- Domain modeli başka şehirlere genişleyebilecek şekilde tasarlanacak.

### Sabit Teknoloji Yığını

#### Mobile
- Kotlin
- Jetpack Compose
- MVVM
- Retrofit / OkHttp
- Coroutines / Flow
- Room veya DataStore gereken yerlerde

#### Backend
- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Security
- PostgreSQL hedef veritabanı
- Redis cache opsiyonel ama önerilir

#### AI / Optimization
- Java tabanlı servis katmanı ile entegrasyon
- Gerekirse Python tabanlı ayrı deneysel servis ileride değerlendirilebilir, ancak ana ürün hattı Spring odaklı kalacak
- OR-Tools Java binding veya Java uyumlu optimizasyon yaklaşımı

### Kritik Mimari İlke
Recommendation generation ve route optimization kesin olarak ayrılacak.

Doğru akış:
1. Kullanıcı profilini oluştur
2. Aday POI havuzunu üret ve skorla
3. Aday havuzunu daralt
4. Bu havuz üzerinden rota optimize et
5. Etkileşim ve geri bildirim topla
6. Öğrenme modelini güncelle

---

## 2. Mevcut Durum Özeti

### Tamamlanan Fazlar
- Phase 1: Data model temeli
- Phase 2: Authentication ve kullanıcı sistemi
- Phase 2.5: Mobile integration temelleri
- Phase 3: Recommendation altyapısının ilk sürümü
- Phase 4: Route planning ve navigation temelleri
- Phase 5: Weather, social, filtering, offline altyapısı, analytics başlangıcı

### Bundan Sonraki Hedef
Phase 5 sonrası çalışmalar, yalnızca yeni özellik eklemek için değil, sistemi tez savunmasına uygun hale getirmek için yeniden yapılandırılacak: mimari belgeler, veri modeli, deney planı, algoritmik gerekçeler ve ölçüm mekanizmaları tamamlanacak.

---

## 3. Phase 6 - High-Level System Design ve Tez Mimari Paketleri

**Priority**: Critical
**Goal**: Sistemin geri kalan tüm geliştirmelerine yön verecek yüksek seviyeli mimari ve tez savunma çerçevesini netleştirmek

### 6.1 Problem Restatement
- [x] Problemi açık ve akademik dille yeniden yaz
- [x] Kullanıcı girdilerini tanımla: ilgi alanı, bütçe, süre, ulaşım, crowd tolerance, sustainability preference
- [x] Sistem çıktılarını tanımla: öneri listesi, optimize rota, açıklanabilir skor bileşenleri
- [x] Sistem sınırlarını yaz: gerçek zamanlı tam crowd sensörü yok, ilk şehir Eskişehir, MVP kısıtları mevcut

### 6.2 Functional Requirements
- [x] User profile and preference management
- [x] POI discovery and search
- [x] Personalized recommendation generation
- [x] Dynamic route creation
- [x] Route feedback collection
- [x] Interaction logging and learning updates
- [x] Turkish UI support end-to-end

### 6.3 Non-Functional Requirements
- [x] Maintainability
- [x] Modularity
- [x] Explainability
- [x] Scalability for future cities
- [x] Performance targets for recommendation and route generation
- [x] Fault tolerance for external API dependencies

### 6.4 Assumptions and Constraints
- [x] İlk canlı veri şehri Eskişehir
- [x] Crowd değeri gerçek zamanlı değil, proxy model olacak
- [x] Turkish NLP ilk sürümde baseline yaklaşım kullanabilir
- [x] Optimization gerçek zamanlı aşırı büyük problem çözmeyecek, aday havuzu sınırlı olacak

### 6.5 Engineering Risks and Mitigations
- [x] Veri eksikliği riskleri
- [x] Optimization latency riski
- [x] Cold-start kalite riski
- [x] Türkçe yorum analizi doğruluk riski
- [x] Harita / hava durumu gibi dış servis bağımlılığı riski

### 6.6 C4 Model Deliverables
- [x] Context Diagram açıklaması
- [x] Container Diagram açıklaması
- [x] Component Diagram açıklaması
- [x] Gerekli yerlerde code-level design notları
- [x] Structurizr DSL çıktıları hazırlanacak

### 6.7 Target Module Boundaries
- [x] Auth module
- [x] User profile module
- [x] POI module
- [x] Recommendation module
- [x] Bandit learning module
- [x] Route optimization module
- [x] Feedback / NLP module
- [x] Analytics and evaluation module

**Expected Deliverables**
- High-level architecture document
- C4 descriptions
- Structurizr DSL drafts
- Service/module boundary document

---

## 4. Phase 7 - Data Modeling ve PostgreSQL Geçiş Tasarımı

**Priority**: Critical
**Goal**: Tez savunmasına uygun, ölçeklenebilir ve coğrafi sorgulara hazır veri modeli oluşturmak

**Status**: Design and implementation completed. PostgreSQL ready with Flyway migrations.

### 7.1 Database Schema Targets
- [x] `users`
- [x] `user_preferences`
- [x] `poi_categories`
- [x] `pois`
- [x] `poi_tags`
- [x] `poi_tag_map`
- [x] `poi_metrics`
- [x] `routes`
- [x] `route_items`
- [x] `user_interactions`
- [x] `recommendation_logs`
- [x] `user_feedback`
- [x] `bandit_events`

### 7.2 JPA / Relational Design Work
- [x] Mevcut entity'leri yeni şemaya göre hizala
- [x] Çok şehirli genişleme için city dimension ekle
- [x] POI metrics tablosunu ayrı tutma kararını gerekçelendir
- [x] Recommendation log ile interaction log ayrımını netleştir

### 7.3 PostgreSQL and Geospatial Readiness
- [x] PostgreSQL hedef şema planını yaz
- [x] Konum kolonları için uygun tip seçimini belirle
- [x] PostGIS geçiş notlarını hazırla
- [x] İndeks planını oluştur

### 7.4 Migration Plan
- [x] H2 / mevcut geliştirme yapısından PostgreSQL'e geçiş stratejisini yaz
- [x] Flyway veya Liquibase seçimini netleştir
- [x] Seed data yükleme stratejisini yaz

### 7.5 Implementation & Setup
- [x] Flyway dependency'si pom.xml'e eklendi
- [x] V1__Initial_Schema.sql migration dosyası oluşturuldu
- [x] application.properties PostgreSQL dev config'i için güncellendi
- [x] application-h2.properties profili oluşturuldu (quick testing için)
- [x] application-prod.properties Flyway entegrasyonuyla güncellendi

**Expected Deliverables**
- [x] Schema document
- [x] SQL migration plan
- [x] Entity-to-table mapping notes
- [x] Indexing strategy
- [x] Flyway integration
- [x] Development setup guide

**Artifacts**
- `backend/docs/data/PHASE7_DATA_MODEL.md`
- `backend/docs/data/PHASE7_ENTITY_MAPPING.md`
- `backend/docs/data/PHASE7_INDEXING_STRATEGY.md`
- `backend/docs/data/PHASE7_POSTGRESQL_MIGRATION_PLAN.sql`
- `backend/src/main/resources/db/migration/V1__Initial_Schema.sql`
- `backend/docs/POSTGRESQL_SETUP.md`

---

## 5. Phase 8 - Spring Boot Backend Refactor ve Thesis-Ready Service Structure

**Priority**: Critical
**Goal**: Backend'i tez seviyesinde modüler, temiz ve genişletilebilir hale getirmek

**Status**: Implementation starting (PostgreSQL + Java 25 foundation ready)

### 8.1 Package Structure Refinement
- [ ] `config`
- [ ] `controller`
- [ ] `domain.model`
- [ ] `dto`
- [ ] `repository`
- [ ] `service`
- [ ] `algorithm`
- [ ] `optimizer`
- [ ] `nlp`
- [ ] `integration`
- [ ] `metrics`
- [ ] `exception`

### 8.2 REST API Hardening
- [ ] `POST /auth/register`
- [ ] `POST /auth/login`
- [ ] `GET /users/me`
- [ ] `PUT /users/preferences`
- [ ] `GET /pois/search`
- [ ] `GET /recommendations`
- [ ] `POST /routes/generate`
- [ ] `GET /routes/{id}`
- [ ] `POST /interactions`
- [ ] `POST /feedback`

### 8.3 Validation and Error Handling
- [ ] DTO validation standardize edilecek
- [ ] Global exception handler güçlendirilecek
- [ ] Error response contract sabitlenecek
- [ ] Rate limiting ve cache noktaları tanımlanacak

### 8.4 Caching and Async Work
- [ ] Hava durumu cache stratejisi
- [ ] POI cache stratejisi
- [ ] Recommendation cache kuralları
- [ ] Background jobs gerektiren işler: batch scoring, analytics aggregation, feedback processing

**Expected Deliverables**
- Refined backend module plan
- Endpoint contract document
- Error handling strategy
- Caching and background job notes

---

## 6. Phase 9 - AI / ML Design Deepening

**Priority**: High
**Goal**: Recommendation katmanını tez seviyesinde savunulabilir hale getirmek

### 9.1 Content-Based Filtering
- [ ] User feature representation oluştur
- [ ] POI feature representation oluştur
- [ ] Initial recommendation score formülünü tanımla
- [ ] Cold-start için popularity + preference blend yaklaşımı yaz

### 9.2 Candidate Generation and Ranking Pipeline
- [ ] Candidate generation adımını ayrı servis olarak tanımla
- [ ] Candidate ranking adımını ayrı servis olarak tanımla
- [ ] Ranking score bileşenlerini ayrıştır: preference fit, crowd fit, budget fit, sustainability fit, local support fit

### 9.3 Contextual Bandit Design
- [ ] İlk pratik algoritmayı seç: contextual Thompson Sampling veya LinUCB
- [ ] Neden seçildiğini akademik ve mühendislik açısından yaz
- [ ] Context vector yapısını belirle:
  - weather
  - time of day
  - day of week
  - transportation mode
  - budget level
  - crowd preference
  - route length preference
  - POI category

### 9.4 Reward Function
- [ ] Reward mapping'i finalize et:
  - `view = 0.1`
  - `click = 0.2`
  - `save = 0.4`
  - `add_to_route = 0.6`
  - `visited = 0.8`
  - `positive_feedback = 1.0`
  - `dislike = -0.4`
- [ ] Reward event persistence yapısını netleştir

### 9.5 Learning Loop Integration
- [ ] Recommendation log ile bandit event bağlantısını kur
- [ ] Batch vs online update kararını yaz
- [ ] Exploration vs exploitation mekanizmasını parametrize et

**Expected Deliverables**
- AI methodology document
- Feature engineering table
- Reward model notes
- Recommendation pipeline pseudocode

---

## 7. Phase 10 - Multi-Criteria Route Optimization Redesign

**Priority**: High
**Goal**: Route planning modülünü formal ve tezde savunulabilir çok kriterli optimizasyon sistemine dönüştürmek

### 10.1 Problem Formulation
- [ ] Problemi Orienteering Problem / multi-objective route planning bağlamında açıkla
- [ ] Objective function tanımla
- [ ] Hard constraints ve soft objectives ayrımını yaz

### 10.2 Optimization Factors
- [ ] distance
- [ ] duration
- [ ] budget
- [ ] crowd exposure
- [ ] sustainability / carbon impact
- [ ] preference match
- [ ] local business support

### 10.3 Normalization Strategy
- [ ] Tüm faktörler için normalization yöntemi yaz
- [ ] Farklı ölçekteki sinyallerin tek skor altında birleşme stratejisini açıkla

### 10.4 Initial Weights and Adaptation
- [ ] Başlangıç ağırlıklarını öner
- [ ] Kullanıcı tercihlerine göre weight shifting stratejisi tasarla
- [ ] Must-see locations ve avoid-overcrowded tercihlerinin constraint dönüşümünü açıkla

### 10.5 Solver Design
- [ ] OR-Tools Java kullanımı değerlendir
- [ ] Alternatif heuristic fallback tasarla
- [ ] Infeasible route senaryoları için fallback planı oluştur

### 10.6 Output Explainability
- [ ] Üretilen rotanın neden seçildiğini açıklayan summary alanları tasarla
- [ ] Route detail ekranı için açıklanabilir skor bileşenleri planla

**Expected Deliverables**
- Formal optimization document
- Objective function definition
- Solver design notes
- Route optimization pseudocode

---

## 8. Phase 11 - Turkish NLP Feedback Loop

**Priority**: Medium
**Goal**: Kullanıcı metinsel geri bildirimini recommendation quality update akışına bağlamak

### 11.1 Feedback Ingestion
- [ ] Route sonrası feedback akışını standartlaştır
- [ ] Star rating + serbest metin yapısını sabitle
- [ ] Feedback lifecycle'ı tanımla

### 11.2 MVP Turkish Sentiment Approach
- [ ] Basit Türkçe sentiment baseline tasarla
- [ ] Sözlük tabanlı veya hafif sınıflandırıcı yaklaşımı seç
- [ ] Text cleaning ve normalization stratejisini belirle

### 11.3 Learning Integration
- [ ] Sentiment skorunu reward modeline bağla
- [ ] Negative feedback nedenlerini kategorize et
- [ ] Recommendation quality tuning için feedback aggregation tasarla

### 11.4 Future Extensions
- [ ] Advanced Turkish NLP future plan
- [ ] Aspect-based feedback analysis future plan
- [ ] Embedding-based similarity future plan

**Expected Deliverables**
- Feedback pipeline document
- Sentiment baseline design
- Reward update integration notes

---

## 9. Phase 12 - Mobile App Re-Architecture for Thesis Quality

**Priority**: Critical
**Goal**: Kotlin / Jetpack Compose istemcisini tez seviyesinde modüler ve Türkçe UX odaklı hale getirmek

**Status**: ✅ COMPLETE

### 12.1 Mobile Package Structure
- [x] `data` - Repository pattern, remote API, local cache
- [x] `domain` - Use cases and domain models
- [x] `ui` - Screens, components, ViewModels
- [x] `navigation` - Navigation routes and deeplinks
- [x] `di` - Dependency injection (Hilt modules)
- [x] `components` - Reusable Jetpack Compose components
- [x] `viewmodel` - MVVM ViewModels with state management
- [x] `local` - Room database and DataStore
- [x] `remote` - API clients and DTOs

### 12.2 Core Screens
- [x] Splash Screen
- [x] Onboarding (4-step carousel)
- [x] Login / Register Screens
- [x] Preference Setup Screen
- [x] Home Screen (Dashboard)
- [x] Place Discovery Screen
- [x] Recommendation Results Screen
- [x] Route Details Screen
- [x] Map Screen (Google Maps integration)
- [x] Saved Routes Screen
- [x] Feedback Screen
- [x] Profile Screen

### 12.3 Turkish UI Requirements
- [x] Tüm label, CTA ve boş durum metinleri Türkçe (strings-tr.xml)
- [x] Recommendation açıklama metinleri Türkçe
- [x] Feedback akışı tamamen Türkçe
- [x] Onboarding tez hedefini destekleyecek biçimde net
- [x] 200+ Turkish UI strings defined and cataloged
- [x] English fallback strings provided
- [x] Turkish grammar and consistency verified

### 12.4 UX and State Management
- [x] State management standardı (MVVM + Unidirectional Flow)
- [x] Screen state, effect ve event ayrımı tanımlanmış
- [x] ScreenState sealed class (Loading, Success, Error, Empty)
- [x] Navigation events via Channel
- [x] Side effects via SharedFlow
- [x] API entegrasyonu error handling standardize
- [x] Offline mode implemented with cache fallback
- [x] OfflineBanner component
- [x] Cached data display with timestamp
- [x] Network-first with fallback strategy
- [x] Error dialogs and retry mechanisms
- [x] Loading indicators on all async operations

**Expected Deliverables** ✅
- [x] Mobile architecture document: `mobile/docs/PHASE12_MOBILE_ARCHITECTURE.md`
- [x] Screen inventory: `mobile/docs/PHASE12_SCREEN_INVENTORY.md`
- [x] Turkish text inventory: `mobile/docs/PHASE12_TURKISH_TEXT_INVENTORY.md`
- [x] State management guideline: `mobile/docs/PHASE12_STATE_MANAGEMENT_GUIDELINES.md`
- [x] Turkish strings resource: `app/src/main/res/values-tr/strings.xml`
- [x] English strings resource: `app/src/main/res/values/strings.xml`

**Artifacts Created**
1. `mobile/docs/PHASE12_MOBILE_ARCHITECTURE.md` (16 sections)
2. `mobile/docs/PHASE12_SCREEN_INVENTORY.md` (15 screens documented)
3. `mobile/docs/PHASE12_STATE_MANAGEMENT_GUIDELINES.md` (16 sections)
4. `mobile/docs/PHASE12_TURKISH_TEXT_INVENTORY.md` (21 categories, 200+ strings)
5. `app/src/main/res/values-tr/strings.xml` (Turkish localization)
6. `app/src/main/res/values/strings.xml` (English fallback)

---

## 10. Phase 13 - Eskişehir-Specific MVP Dataset Expansion (COMPLETE ✅)

**Priority**: High
**Goal**: Prototip, test ve tez deneyi için yeterli kalitede Eskişehir veri seti oluşturmak

### 13.1 Coverage Areas (COMPLETE ✅)
- [x] Odunpazarı (10-12 POIs)
- [x] Sazova (10-12 POIs)
- [x] Museums (distributed across areas)
- [x] Local cafes (distributed across areas)
- [x] Parks (distributed across areas)
- [x] Cultural locations (distributed across areas)
- [x] Historical sites (distributed across areas)
- [x] Riverside spots (distributed across areas)

### 13.2 POI Attributes (COMPLETE ✅)
- [x] name (Turkish)
- [x] englishName (English translation)
- [x] category (29 category enum values)
- [x] district (10 district enum values)
- [x] latitude (6 decimal precision, Eskişehir bounds)
- [x] longitude (6 decimal precision, Eskişehir bounds)
- [x] estimated visit duration (minutes)
- [x] estimated cost (Turkish Lira)
- [x] price level (Free/Budget/Moderate/Expensive/Luxury)
- [x] tags (List<String> with taxonomy)
- [x] indoor/outdoor (Enum: Indoor/Outdoor/Mixed)
- [x] family-friendly (Boolean)
- [x] sustainability/local score (0-100 float)
- [x] crowd proxy (0-100 float)
- [x] popularity (0-100 float)
- [x] opening hours (HH:MM-HH:MM format)
- [x] Plus 11 additional attributes (accessibility, parking, transit, etc.)

### 13.3 Dataset Engineering (COMPLETE ✅)
- [x] Seed data format: JSON (primary), CSV (spreadsheet), Kotlin (type-safe)
- [x] POI scoring proxies: 4 complete algorithms defined
  - [x] Popularity Score (0-100 with category baseline, ratings, review count, seasonality)
  - [x] Crowd Proxy Score (0-100 with time-of-day, day-of-week, capacity, seasonality)
  - [x] Sustainability Score (0-100 with environmental, community, cultural, accessibility)
  - [x] Local Business Score (0-100 with ownership, employment, supply, engagement)
- [x] Local business score production logic: Complete Kotlin implementation with tag-based classification
- [x] Crowd proxy production logic: Time-based and day-of-week pattern algorithms

### 13.4 Implementation Roadmap (COMPLETE ✅)
- [x] Step-by-step execution guide (12 steps from data models to mobile integration)
- [x] QA checklist (data quality, distribution, database, API, mobile)
- [x] Risk mitigation plan
- [x] Resource allocation and timeline
- [x] Phase 14 preparation plan
- [x] File checklist with all artifacts

**Deliverables** (6 documents, all COMPLETE ✅):
1. ✅ PHASE13_ESKISEHIR_DATA_STRATEGY.md - Executive summary + POI framework
2. ✅ PHASE13_POI_ATTRIBUTES_DICTIONARY.md - All 14+ attributes with validation
3. ✅ PHASE13_PROXY_SCORING_RULES.md - 4 scoring algorithms with Kotlin code
4. ✅ PHASE13_SEED_DATA_GENERATOR.md - Complete generator implementation
5. ✅ PHASE13_IMPLEMENTATION_ROADMAP.md - Execution plan + checklist
6. ✅ Database schema and migrations (included in deliverable 4)

**Phase 13 Design Phase**: 100% COMPLETE ✅

**Next Phase 13 Steps (Implementation)**:
1. Create Kotlin data models (POI.kt with all 14+ attributes)
2. Implement TurkishNameGenerator with Turkish place names
3. Implement LocationGenerator with Eskişehir district bounds
4. Implement POIScoreCalculator with 4 scoring formulas
5. Run seed data generator: `./gradlew generateSeedData`
6. Validate dataset: 80-100 POIs with score distributions
7. Create database migrations and load seed data
8. Test API endpoints (GET /pois, /pois/{id}, filters)
9. Integrate POI data into mobile app (list, details, map)
10. Complete QA and performance testing

**Quality Gates for Implementation**:
- 100 POIs generated with complete coverage
- All 14+ attributes populated (0 nulls for required fields)
- Score distributions: Popularity 45-65, Crowd 40-60, Sustainability 50-70, Local Business 45-70
- Zero validation errors, <5 warnings
- >80% test coverage for generator code
- API response time <200ms for all endpoints
- Mobile app displays POIs smoothly

**Timeline**: 2-3 weeks implementation (1-2 developers)
**Blockers**: None (all design complete, ready for coding)
**Next Phase**: Phase 14 - Recommendation Engine (ready to start)

---

## 11. Phase 14 - Testing, Evaluation and Thesis Experiment Design

**Priority**: Critical
**Goal**: Sistemi yazılım, AI ve kullanıcı memnuniyeti açısından ölçülebilir hale getirmek
**Status**: ✅ DESIGN COMPLETE - Ready for implementation

### 14.1 Technical Testing
- [ ] Unit tests (50+ backend tests, 20+ mobile tests)
- [ ] Integration tests (20+ database/API tests)
- [ ] API tests (15 endpoints)
- [ ] route generation tests (optimality, constraints, edge cases)
- [ ] mobile UI tests (Jetpack Compose component tests)

### 14.2 AI Evaluation Metrics
- [ ] Precision@K (target ≥0.50 at K=10)
- [ ] Recall@K (target ≥0.60 at K=10)
- [ ] NDCG@K (target ≥0.70 at K=5)
- [ ] CTR (target ≥0.20, baseline 0.10)
- [ ] route acceptance rate (target ≥0.85, baseline 0.70)

### 14.3 Route Quality Metrics
- [ ] total distance (target 15-25 km average)
- [ ] total duration (target 3-5 hours including visits)
- [ ] budget compliance (target 100%, avg 85-95% utilization)
- [ ] crowd exposure (target 25-35 for eco routes vs 60-75 for baseline)
- [ ] carbon impact (target 20-30% reduction for eco-optimized routes)

### 14.4 User Satisfaction and Thesis Evaluation
- [ ] Kullanıcı memnuniyeti anket tasarımı (SUS + 5-point likert scales)
- [ ] Baseline sistem ile karşılaştırma planı (70 participants, RCT design)
- [ ] Deney düzeneği ve hipotezlerin yazılması (see detailed outline)

**Expected Deliverables** (3 documents - COMPLETE ✅)
- [x] PHASE14_TEST_PLAN.md (comprehensive test strategy)
- [x] PHASE14_EVALUATION_METRICS_GUIDE.md (detailed metric definitions)
- [x] PHASE14_THESIS_EXPERIMENT_OUTLINE.md (experimental design & hypotheses)

**Next Implementation Steps**:
1. Create unit test skeletons (Spring Boot + Kotlin)
2. Set up test infrastructure (JUnit, Mockito, Testcontainers)
3. Implement database integration tests
4. Establish performance baseline
5. Recruit 70 study participants (35 test, 35 control)
6. Run 4-week user study (May 18 - Jun 14)
7. Analyze results and write thesis chapter

**Study Timeline**:
- Recruitment: May 1-15 ✅ (before May 18 start)
- Baseline period: May 18-31 (2 weeks)
- Intervention: Jun 1-14 (2 weeks)
- Analysis: Jun 15 - Jul 15
- Thesis write-up: Jul 16-31

**Timeline**: 6-8 weeks (3-4 development + 4 user study)
**Blockers**: None - all design complete, ready to code
**Next Phase**: Phase 15 - Implementation Roadmap and Delivery Milestones

---

## 12. Phase 15 - Implementation Roadmap and Delivery Milestones

**Priority**: Critical
**Goal**: Kalan tüm çalışmaları uygulanabilir milestone'lara ayırmak

### 15.1 Milestone Order
- [ ] Milestone 1: High-level architecture + C4 + Structurizr
- [ ] Milestone 2: PostgreSQL schema + migration strategy
- [ ] Milestone 3: Backend modular refactor
- [ ] Milestone 4: Content-based recommender hardening
- [ ] Milestone 5: Route optimizer redesign
- [ ] Milestone 6: Contextual bandit integration
- [ ] Milestone 7: Turkish feedback analysis
- [ ] Milestone 8: Mobile architecture cleanup + Turkish UX alignment
- [ ] Milestone 9: Eskişehir dataset expansion
- [ ] Milestone 10: Testing + thesis evaluation

### 15.2 Required Artifact Per Milestone
- [ ] Code changes
- [ ] Documentation updates
- [ ] Test outputs
- [ ] Academic justification notes

---

## 13. Bir Sonraki Somut Adım

Phase 6 mimari paketi tamamlandı. Sıradaki doğrudan uygulama fazı, Phase 7 veri modelleme ve PostgreSQL geçiş tasarımıdır.

### Immediate Focus
- [ ] PostgreSQL hedef şemasını yaz
- [ ] JPA entity-to-table hizalamasını çıkar
- [ ] recommendation_logs ve user_interactions ayrımını netleştir
- [ ] PostGIS readiness ve index planını dokümante et
- [ ] Migration strategy belgesini hazırla

---

## 14. Working Principles

- Kotlin ve Spring ekseni korunacak.
- AI ve optimization kararları akademik gerekçeleriyle yazılacak.
- MVP-first yaklaşım sürdürülecek.
- UI dili Türkçe olacak.
- Kod kadar dokümantasyon da teslim çıktısı kabul edilecek.
- Her ana karar için trade-off notu tutulacak.

---

## 15. Beklenen Ana Artefaktlar

- Markdown architecture docs
- Structurizr DSL files
- PostgreSQL migration scripts
- Spring Boot backend refactor planı ve modül belgeleri
- Recommendation and optimization design docs
- Turkish UI text catalog
- Eskişehir seed dataset
- Test and thesis evaluation plan

Bu roadmap bundan sonra yalnızca yapılacaklar listesi değil, aynı zamanda tez savunma çerçevesi, mimari referans ve teknik karar kılavuzu olarak kullanılacaktır.
