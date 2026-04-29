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

### 7.1 Database Schema Targets
- [ ] `users`
- [ ] `user_preferences`
- [ ] `poi_categories`
- [ ] `pois`
- [ ] `poi_tags`
- [ ] `poi_tag_map`
- [ ] `poi_metrics`
- [ ] `routes`
- [ ] `route_items`
- [ ] `user_interactions`
- [ ] `recommendation_logs`
- [ ] `user_feedback`
- [ ] `bandit_events`

### 7.2 JPA / Relational Design Work
- [ ] Mevcut entity'leri yeni şemaya göre hizala
- [ ] Çok şehirli genişleme için city dimension ekle
- [ ] POI metrics tablosunu ayrı tutma kararını gerekçelendir
- [ ] Recommendation log ile interaction log ayrımını netleştir

### 7.3 PostgreSQL and Geospatial Readiness
- [ ] PostgreSQL hedef şema planını yaz
- [ ] Konum kolonları için uygun tip seçimini belirle
- [ ] PostGIS geçiş notlarını hazırla
- [ ] İndeks planını oluştur

### 7.4 Migration Plan
- [ ] H2 / mevcut geliştirme yapısından PostgreSQL'e geçiş stratejisini yaz
- [ ] Flyway veya Liquibase seçimini netleştir
- [ ] Seed data yükleme stratejisini yaz

**Expected Deliverables**
- Schema document
- SQL migration plan
- Entity-to-table mapping notes
- Indexing strategy

---

## 5. Phase 8 - Spring Boot Backend Refactor ve Thesis-Ready Service Structure

**Priority**: Critical
**Goal**: Backend'i tez seviyesinde modüler, temiz ve genişletilebilir hale getirmek

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

### 12.1 Mobile Package Structure
- [ ] `data`
- [ ] `domain`
- [ ] `ui`
- [ ] `navigation`
- [ ] `di`
- [ ] `components`
- [ ] `viewmodel`
- [ ] `local`
- [ ] `remote`

### 12.2 Core Screens
- [ ] Splash
- [ ] Onboarding
- [ ] Login / Register
- [ ] Preference Setup
- [ ] Home
- [ ] Place Discovery
- [ ] Recommendation Results
- [ ] Route Details
- [ ] Map Screen
- [ ] Saved Routes
- [ ] Feedback Screen
- [ ] Profile

### 12.3 Turkish UI Requirements
- [ ] Tüm label, CTA ve boş durum metinleri Türkçe olacak
- [ ] Recommendation açıklama metinleri Türkçe olacak
- [ ] Feedback akışı Türkçe hazırlanacak
- [ ] Onboarding tez hedefini destekleyecek biçimde net olacak

### 12.4 UX and State Management
- [ ] State management standardı netleştirilecek
- [ ] Screen state, effect ve event ayrımı kurulacak
- [ ] API entegrasyonu ve error state gösterimi standardize edilecek
- [ ] Offline ve cache davranışı kullanıcıya anlaşılır biçimde gösterilecek

**Expected Deliverables**
- Mobile architecture document
- Screen inventory
- Turkish text inventory
- State management guideline

---

## 10. Phase 13 - Eskişehir-Specific MVP Dataset Expansion

**Priority**: High
**Goal**: Prototip, test ve tez deneyi için yeterli kalitede Eskişehir veri seti oluşturmak

### 13.1 Coverage Areas
- [ ] Odunpazarı
- [ ] Sazova
- [ ] museums
- [ ] local cafes
- [ ] parks
- [ ] cultural locations
- [ ] historical sites
- [ ] riverside spots

### 13.2 POI Attributes
- [ ] name
- [ ] category
- [ ] district
- [ ] latitude
- [ ] longitude
- [ ] estimated visit duration
- [ ] average price level
- [ ] tags
- [ ] indoor/outdoor
- [ ] family-friendly
- [ ] sustainability/local score
- [ ] crowd proxy
- [ ] popularity
- [ ] opening hours

### 13.3 Dataset Engineering
- [ ] Seed data format seç
- [ ] POI scoring proxies tanımla
- [ ] Local business score üretim mantığını yaz
- [ ] Crowd proxy üretim mantığını yaz

**Expected Deliverables**
- Eskişehir seed dataset
- Data dictionary
- Proxy score generation rules

---

## 11. Phase 14 - Testing, Evaluation and Thesis Experiment Design

**Priority**: Critical
**Goal**: Sistemi yazılım, AI ve kullanıcı memnuniyeti açısından ölçülebilir hale getirmek

### 14.1 Technical Testing
- [ ] Unit tests
- [ ] Integration tests
- [ ] API tests
- [ ] route generation tests
- [ ] mobile UI tests

### 14.2 AI Evaluation Metrics
- [ ] Precision@K
- [ ] Recall@K
- [ ] NDCG@K
- [ ] CTR
- [ ] route acceptance rate

### 14.3 Route Quality Metrics
- [ ] total distance
- [ ] total duration
- [ ] budget compliance
- [ ] crowd exposure
- [ ] carbon impact

### 14.4 User Satisfaction and Thesis Evaluation
- [ ] Kullanıcı memnuniyeti anket tasarımı
- [ ] Baseline sistem ile karşılaştırma planı
- [ ] Deney düzeneği ve hipotezlerin yazılması

**Expected Deliverables**
- Test plan
- Evaluation metric guide
- Thesis experiment outline

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
