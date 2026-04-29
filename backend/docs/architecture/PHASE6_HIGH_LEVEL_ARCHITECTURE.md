# Phase 6 High-Level Architecture

## 1. Problem Restatement

Bu sistemin amacı, Eskişehir'deki ziyaretçilere veya şehir sakinlerine, kişisel tercihleri ve anlık bağlamı dikkate alarak dinamik ve kişiselleştirilmiş gezi rotaları üretmektir. Problem yalnızca "hangi noktalar önerilsin" değildir; asıl hedef, kullanıcının ilgi alanları, bütçesi, zamanı, ulaşım tercihi, kalabalık toleransı, hava durumu ve sürdürülebilirlik eğilimleri gibi birden fazla sinyalden yararlanarak hem iyi POI adayları hem de uygulanabilir bir rota oluşturmaktır.

Bu nedenle sistem iki ayrı ama bağlantılı karar katmanına bölünmelidir:

1. Recommendation layer: hangi POI'ler kullanıcının ilgisine uygundur?
2. Route optimization layer: bu POI'ler içinden hangileri hangi sırayla rota haline getirilmelidir?

Bu ayrım, hem yazılım mimarisi hem de tez savunması açısından kritik tasarım kararıdır.

## 2. Functional Requirements

### 2.1 User Profile Module
- Kullanıcı kayıt ve giriş akışları
- Tercih yönetimi: kategori, bütçe, mobilite, kalabalık toleransı, sürdürülebilirlik
- Kullanıcı davranış geçmişinin tutulması

### 2.2 POI Module
- POI listeleme, arama ve filtreleme
- POI metadata: koordinat, kategori, açık saatler, tahmini ziyaret süresi, maliyet seviyesi
- Crowd proxy, popularity, sustainability ve local support sinyalleri

### 2.3 Recommendation Module
- Content-based initial scoring
- Cold-start fallback
- Candidate generation
- Candidate ranking
- Recommendation log üretimi

### 2.4 Contextual Learning Module
- User interaction logging
- Reward event üretimi
- Bandit statistics update
- Context-aware ranking improvement

### 2.5 Route Optimization Module
- Recommended candidate havuzu üzerinden rota üretimi
- Zaman, bütçe, crowd, mesafe, sürdürülebilirlik ve local support kısıtlarının yönetimi
- Rota detayları ve turn-by-turn veri üretimi

### 2.6 Feedback Module
- Star rating ve metinsel yorum alma
- Yorumlardan sentiment veya davranış sinyali çıkarma
- Recommendation quality update için geri besleme sağlama

### 2.7 Mobile UX Module
- Türkçe onboarding, keşif, öneri, rota ve geri bildirim akışları
- Kimlik doğrulama, profil, favoriler ve rota ekranları
- Açıklanabilir öneri ve rota özetleri

## 3. Non-Functional Requirements

- Maintainability: Recommendation, optimization, feedback ve analytics kodları birbirinden ayrık olmalı.
- Explainability: Öneri ve rota kararları tez düzeyinde açıklanabilir olmalı.
- Scalability: Veri modeli birden fazla şehir eklenmesine izin vermeli.
- Testability: Controller, service ve algorithm seviyelerinde ayrı test yüzeyleri olmalı.
- Performance: Recommendation cevapları etkileşimli kullanım için hızlı, route generation ise makul gecikmede olmalı.
- Fault tolerance: Weather, map veya geospatial servisler başarısız olduğunda fallback davranışı tanımlı olmalı.

## 4. Assumptions and Constraints

- İlk tam şehir modeli Eskişehir'dir.
- Tüm mobil görünür metinler Türkçe olacaktır.
- Crowd değeri gerçek zamanlı sensör verisi yerine proxy model ile sağlanacaktır.
- NLP ilk sürümde hafif sentiment baseline kullanabilir.
- Route optimization tüm şehir grafiği üzerinde değil, recommendation katmanından gelen sınırlı aday havuzu üzerinde çalışacaktır.
- Mevcut ürün hattı Kotlin + Jetpack Compose ve Spring Boot ekseninde korunacaktır.

## 5. Engineering Risks and Mitigation Strategies

### 5.1 Data Quality Risk
Risk: POI skorları, crowd proxy ve local support değerleri gerçek veriden ziyade tahmini olabilir.

Mitigation:
- Seed data için açık proxy kuralları belgelenecek.
- `poi_metrics` veya eşdeğer yapı üzerinden sinyaller ayrı tutulacak.
- Tezde gerçek zamanlı veri yokluğu açıkça varsayım olarak belirtilecek.

### 5.2 Recommendation Bias Risk
Risk: İlk etkileşimlerden sonra sistem erken bir profile aşırı saplanabilir.

Mitigation:
- Cold-start ve exploration mantığı recommendation katmanında korunacak.
- Reward mapping sabit ve açık belgelenecek.
- Recommendation logs ve bandit events ayrı tutulacak.

### 5.3 Optimization Latency Risk
Risk: Çok sayıda POI ve çok kriterli objective function çözüm süresini artırabilir.

Mitigation:
- Optimization yalnızca candidate pool üzerinde çalışacak.
- Exact çözüm yerine heuristic veya bounded solver yaklaşımı benimsenebilecek.
- Infeasible route fallback senaryoları tasarlanacak.

### 5.4 Turkish NLP Risk
Risk: Türkçe sentiment analizi düşük veri ve hafif model nedeniyle sınırlı olabilir.

Mitigation:
- MVP'de baseline sentiment yaklaşımı kullanılacak.
- Gelişmiş NLP future extension olarak belgelenip ayrılacak.

### 5.5 Architecture Drift Risk
Risk: Hızlı özellik geliştirmeleri modüler mimariyi bozabilir.

Mitigation:
- Backend modül sınırları açıkça tanımlanacak.
- Recommendation ve route optimization servisleri ayrık tutulacak.
- Phase 6 sonrası tüm geliştirmeler bu sınırlar üzerinden ilerleyecek.

## 6. Recommended Project Folder Structure

## 6.1 Backend Structure

```text
backend/
  src/main/java/com/eskisehir/eventapi/
    config/
    controller/
    security/
    exception/
    dto/
    domain/
      model/
    repository/
    service/
    algorithm/
    optimizer/
    nlp/
    integration/
    metrics/
    util/
  src/main/resources/
    application.properties
    application-prod.properties
    data/
  src/test/java/com/eskisehir/eventapi/
    controller/
    service/
    algorithm/
    integration/
  docs/architecture/
```

## 6.2 Mobile Structure

```text
mobile/app/src/main/java/com/eskisehir/eventapp/
  data/
    local/
    model/
    remote/
  domain/
    usecase/
  di/
  navigation/
  ui/
    components/
    screens/
      auth/
      home/
      explore/
      recommendations/
      route/
      social/
      settings/
      detail/
      profile/
    viewmodel/
```

## 7. Service / Module Boundaries

### 7.1 Auth Module
- Sorumluluk: register, login, token refresh, access control
- Ana bileşenler: `AuthController`, `AuthService`, JWT security bileşenleri

### 7.2 User Profile Module
- Sorumluluk: kullanıcı bilgileri ve tercihleri
- Ana bileşenler: `UserController`, `UserService`, `UserPreferenceRepository`

### 7.3 POI Module
- Sorumluluk: POI listeleme, arama, filtreleme, temel metadata
- Ana bileşenler: `PoiController`, `PoiService`, `AdvancedFilterService`, `PoiRepository`

### 7.4 Recommendation Module
- Sorumluluk: candidate generation, ranking, cold-start, recommendation logs
- Ana bileşenler: `RecommendationController`, `RecommendationEngine`, `RecommendationService`, `RecommendationStrategy`, `ColdStartStrategy`

### 7.5 Interaction & Bandit Module
- Sorumluluk: user interaction loglama, reward üretimi, bandit statistics güncelleme
- Ana bileşenler: `InteractionController`, `InteractionService`, `BanditStatsRepository`, `BanditEventRepository`, `ThompsonSamplingStrategy`

### 7.6 Route Optimization Module
- Sorumluluk: rota üretimi, route metrics, navigation steps, social route actions
- Ana bileşenler: `RouteController`, `RoutePlanner`, `RouteService`, `GeoService`

### 7.7 Context Module
- Sorumluluk: weather enrichment, cache, contextual signals
- Ana bileşenler: `WeatherService`, `CacheService`, future Redis integration

### 7.8 Feedback & NLP Module
- Sorumluluk: textual feedback ingestion, sentiment extraction, recommendation tuning
- Ana bileşenler: `UserFeedback`, future `nlp` package services

### 7.9 Analytics Module
- Sorumluluk: request metrics, usage analytics, model evaluation signals
- Ana bileşenler: `AnalyticsController`, `AnalyticsService`

## 8. C4 Model Description

### 8.1 Context View
- Primary actor: Tourist / user using the mobile application in Turkish
- Main system: Eskişehir Intelligent Tourism Application
- External systems:
  - Weather API
  - Map / routing provider
  - PostgreSQL database
  - Redis cache

### 8.2 Container View
- Mobile App Container: Kotlin Android app, Jetpack Compose UI, ViewModels, Retrofit clients
- Backend API Container: Spring Boot REST API handling auth, recommendations, routes, feedback
- Relational Database Container: PostgreSQL storing users, POIs, routes, logs, feedback, bandit data
- Cache Container: Redis for weather and short-lived context cache
- External Data Containers: weather and mapping providers

### 8.3 Component View for Backend
- Auth API + Security layer
- User profile services
- POI search/filter services
- Recommendation engine
- Interaction logging and bandit updater
- Route planner / optimizer
- Weather enrichment and contextual cache
- Analytics and evaluation services

### 8.4 Code-Level Guidance
- Controllers orchestrate requests and DTO mapping.
- Services own domain decisions.
- Repositories only handle persistence concerns.
- Algorithm and optimizer packages must not contain transport-layer concerns.
- Mobile ViewModels consume use cases and avoid direct Retrofit orchestration inside composables.

## 9. First Implementation Milestone

Phase 6 sonrasında başlanacak ilk milestone, Architecture and Foundation Pack olacaktır.

### Milestone Scope
- Mevcut Kotlin/Spring sistemine göre C4 mimari çıktılarının tamamlanması
- PostgreSQL hedef şema taslağının çıkarılması
- Backend package refinement planının hazırlanması
- Mobile package temizliği ve hedef ekran envanterinin sabitlenmesi
- Eskişehir seed dataset alanlarının netleştirilmesi

### Exit Criteria
- Mimari doküman repo içinde yer almalı
- Structurizr DSL dosyası oluşturulmalı
- Phase 7 veri modelleme işine başlanabilecek kadar net entity/table eşlemesi hazırlanmalı
- Recommendation ve optimization ayrımı yazılı olarak sabitlenmiş olmalı

### Out of Scope
- Tam PostgreSQL migration implementasyonu
- NLP modülünün gerçek kodu
- OR-Tools entegrasyonunun tamamlanması
- Tüm mobil ekranların bitirilmesi

## 10. Decision Summary

- Existing Kotlin + Spring direction is retained.
- Recommendation and route optimization are separate bounded decisions.
- Eskişehir remains the first fully modeled city.
- Turkish-first UX is a mandatory product constraint.
- Phase 6 is considered complete when architecture, module boundaries, C4 description and initial milestone definition are committed as repository artifacts.