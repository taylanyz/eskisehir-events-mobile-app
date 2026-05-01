# Phase 11: Turkish NLP Feedback Loop
## Closing the Learning Loop with Sentiment-Aware Reward Integration

**Version**: 1.0  
**Status**: Design & Implementation  
**Date**: May 2, 2026  

---

## 1. Overview and Goal

Phase 11 integrates user **textual feedback** into the recommendation and route quality learning loop. After a user visits recommended POIs or completes an optimized route, we collect both quantitative ratings (1-5 stars) and qualitative feedback (free-text Turkish). This feedback is analyzed for sentiment and specific themes, then mapped to reward signals that update the Thompson Sampling bandit learner.

**Primary Goal**: Close the learning loop by transforming user sentiment and critique into actionable reward updates, enabling the system to iteratively improve recommendation quality based on lived user experience.

**Thesis Relevance**: 
- Demonstrates practical feedback incorporation in personalized systems
- Shows how unstructured text can be leveraged for model improvement
- Establishes closed-loop learning pipeline for AI-driven recommendations

---

## 2. Feedback Lifecycle and Architecture

### 2.1 Feedback Flow Diagram

```
User completes route
         ↓
[Route completion event]
         ↓
Display feedback screen (Turkish UI)
  - Star rating (1-5)
  - Free text (Turkish, optional, max 500 chars)
  - Category hints (optional): poor_route_logic, crowded, too_expensive, not_enough_time, boring
         ↓
[POST /api/feedback]
         ↓
FeedbackService processes:
  1. Persist UserFeedback entity
  2. TurkishSentimentAnalyzer processes text
  3. Extract sentiment score, entities, themes
  4. FeedbackRewardMapper creates reward signal
  5. BanditLearningService updates Thompson Sampling
  6. Analytics log for evaluation
         ↓
[Feedback integrated into next recommendation]
```

### 2.2 Core Entities and DTOs

#### UserFeedback Entity
```java
@Entity
@Table(name = "user_feedback")
public class UserFeedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "route_id")
    private Route route;
    
    // Quantitative feedback
    private Integer starRating;          // 1-5
    private Long createdAt;
    
    // Qualitative feedback
    private String feedbackText;         // Turkish, max 500 chars
    
    // Sentiment analysis output
    private Double sentimentScore;       // [-1, 1], -1=very negative, 0=neutral, 1=very positive
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "feedback_themes", joinColumns = @JoinColumn(name = "feedback_id"))
    @Column(name = "theme")
    @Enumerated(EnumType.STRING)
    private Set<FeedbackTheme> themes;   // Categorized complaint areas
    
    // Derived signals for learning
    private Double mappedRewardScore;    // Combined star + sentiment → [0, 1]
    private Boolean feedbackProcessed;   // Flag for async processing
}
```

#### FeedbackTheme Enum
```java
public enum FeedbackTheme {
    ROUTE_QUALITY,        // Rota kalitesi kötü
    CROWDING,             // Çok kalabalıktı
    BUDGET_EXCEEDED,      // Bütçe aşıldı
    TIME_MISMATCH,        // Zaman tahminleri hatalı
    BORING,               // İlgi çekici değildi
    ACCESSIBILITY,        // Erişim/ulaşım sorunu
    SAFETY_CONCERN,       // Güvenlik sorunu
    QUALITY_OF_EXPERIENCE // Genel yaşam kalitesi düşük
}
```

#### FeedbackRequest DTO
```java
@Data
@Validated
public class FeedbackRequest {
    @NotNull
    private Long userId;
    
    @NotNull
    private Long routeId;
    
    @NotNull
    @Min(1) @Max(5)
    private Integer starRating;
    
    @Size(max = 500)
    private String feedbackText;        // Optional Turkish text
    
    @ElementCollection
    private Set<FeedbackTheme> suggestedThemes;  // Optional hints from UI
}
```

#### FeedbackResponse DTO
```java
@Data
public class FeedbackResponse {
    private Long feedbackId;
    private Double sentimentScore;
    private Set<FeedbackTheme> themes;
    private Double mappedRewardScore;
    private String message;
}
```

---

## 3. Turkish Sentiment Analysis - MVP Baseline Approach

### 3.1 Why Baseline (Not Deep Learning)

**Rationale**:
- No Turkish-specific labeled sentiment dataset readily available
- Deep learning requires GPU compute (thesis MVP scope)
- Lexicon-based approach is interpretable and reproducible
- Can demonstrate feedback loop closure without ML complexity
- Future work can upgrade to transformer models

**Trade-off**: Loss in nuance vs. gain in simplicity, speed, and thesis clarity.

### 3.2 Lexicon-Based Sentiment Architecture

#### TurkishSentimentAnalyzer Service

**Core Strategy**: 
1. Turkish text preprocessing (lowercase, punctuation, stopword removal)
2. Dictionary lookup for sentiment words
3. Negation detection and scope
4. Intensifier/diminisher handling (çok, az, hiç, iyice)
5. Emotional indicators (emojis, exclamation marks)
6. Final score: [-1, 1]

#### Sentiment Dictionary Structure

```java
@Component
public class TurkishSentimentLexicon {
    // Positive words
    private static final Map<String, Double> POSITIVE_WORDS = Map.ofEntries(
        Map.entry("güzel", 0.8),
        Map.entry("harika", 0.9),
        Map.entry("mükemmel", 0.95),
        Map.entry("hoş", 0.7),
        Map.entry("iyi", 0.6),
        Map.entry("beğendim", 0.75),
        Map.entry("tavsiye", 0.7),
        Map.entry("kesinlikle", 0.8),
        Map.entry("mutlaka", 0.75),
        Map.entry("çok güzel", 0.9),
        Map.entry("efsane", 0.85)
    );
    
    // Negative words
    private static final Map<String, Double> NEGATIVE_WORDS = Map.ofEntries(
        Map.entry("kötü", -0.8),
        Map.entry("berbat", -0.9),
        Map.entry("korkunç", -0.85),
        Map.entry("fena", -0.7),
        Map.entry("hoşlanmadım", -0.8),
        Map.entry("pişman", -0.75),
        Map.entry("boşa", -0.7),
        Map.entry("çöp", -0.9),
        Map.entry("kocaman sorun", -0.8),
        Map.entry("asla", -0.7),
        Map.entry("hiç", -0.7)
    );
    
    // Negation words (flip sentiment)
    private static final Set<String> NEGATION_WORDS = Set.of(
        "değil", "yok", "hiç", "bir", "asla", "ne"
    );
    
    // Intensifiers (amplify sentiment)
    private static final Map<String, Double> INTENSIFIERS = Map.ofEntries(
        Map.entry("çok", 1.3),
        Map.entry("iyice", 1.25),
        Map.entry("oldukça", 1.2),
        Map.entry("gerçekten", 1.15)
    );
    
    // Diminishers (reduce sentiment)
    private static final Map<String, Double> DIMINISHERS = Map.ofEntries(
        Map.entry("biraz", 0.7),
        Map.entry("az", 0.6),
        Map.entry("hafif", 0.7)
    );
}
```

#### TurkishSentimentAnalyzer Implementation

```java
@Service
public class TurkishSentimentAnalyzer {
    private final TurkishSentimentLexicon lexicon;
    private final FeedbackThemeExtractor themeExtractor;
    
    /**
     * Analyze Turkish text for sentiment and return score in [-1, 1].
     */
    public SentimentAnalysis analyzeSentiment(String turkishText) {
        if (turkishText == null || turkishText.isBlank()) {
            return new SentimentAnalysis(0.0, Set.of());  // Neutral if no text
        }
        
        // 1. Preprocess
        String normalized = preprocess(turkishText);
        String[] tokens = normalized.split("\\s+");
        
        // 2. Score tokens
        double totalScore = 0.0;
        int sentimentTokenCount = 0;
        
        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];
            Double sentimentValue = null;
            
            // Check if positive or negative word
            if (lexicon.POSITIVE_WORDS.containsKey(token)) {
                sentimentValue = lexicon.POSITIVE_WORDS.get(token);
            } else if (lexicon.NEGATIVE_WORDS.containsKey(token)) {
                sentimentValue = lexicon.NEGATIVE_WORDS.get(token);
            }
            
            if (sentimentValue != null) {
                // Check for negation in previous 3 words
                boolean negated = false;
                for (int j = Math.max(0, i - 3); j < i; j++) {
                    if (lexicon.NEGATION_WORDS.contains(tokens[j])) {
                        negated = true;
                        break;
                    }
                }
                
                if (negated) {
                    sentimentValue = -sentimentValue;  // Flip sign
                }
                
                // Check for intensifiers/diminishers
                if (i > 0) {
                    String previous = tokens[i - 1];
                    if (lexicon.INTENSIFIERS.containsKey(previous)) {
                        sentimentValue *= lexicon.INTENSIFIERS.get(previous);
                    } else if (lexicon.DIMINISHERS.containsKey(previous)) {
                        sentimentValue *= lexicon.DIMINISHERS.get(previous);
                    }
                }
                
                totalScore += sentimentValue;
                sentimentTokenCount++;
            }
        }
        
        // 3. Emoji sentiment bonus
        double emojiBonus = 0.0;
        if (turkishText.contains("😊") || turkishText.contains("😄") || turkishText.contains("👍")) {
            emojiBonus = 0.2;
        } else if (turkishText.contains("😠") || turkishText.contains("😞") || turkishText.contains("👎")) {
            emojiBonus = -0.2;
        }
        
        // 4. Exclamation mark intensity
        int exclamationCount = (int) turkishText.chars().filter(ch -> ch == '!').count();
        double exclamationBonus = Math.min(exclamationCount * 0.1, 0.3);  // Cap at 0.3
        if (totalScore < 0) {
            exclamationBonus = -exclamationBonus;  // Negative if text is negative
        }
        
        // 5. Final score calculation
        double finalScore = 0.0;
        if (sentimentTokenCount > 0) {
            finalScore = totalScore / sentimentTokenCount;  // Average sentiment
        }
        finalScore += emojiBonus + exclamationBonus;
        
        // Clamp to [-1, 1]
        finalScore = Math.max(-1.0, Math.min(1.0, finalScore));
        
        // 6. Extract themes
        Set<FeedbackTheme> themes = themeExtractor.extractThemes(turkishText);
        
        return new SentimentAnalysis(finalScore, themes);
    }
    
    private String preprocess(String text) {
        // 1. Lowercase
        String result = text.toLowerCase();
        
        // 2. Remove URLs and email-like patterns
        result = result.replaceAll("https?://\\S+", "");
        result = result.replaceAll("\\S+@\\S+", "");
        
        // 3. Remove extra punctuation (keep for now, analyze later)
        // For Turkish, preserve Turkish-specific chars: ç, ğ, ı, ö, ş, ü
        
        // 4. Multiple spaces → single space
        result = result.replaceAll("\\s+", " ");
        
        return result.trim();
    }
}

@Data
@AllArgsConstructor
public class SentimentAnalysis {
    private Double sentimentScore;     // [-1, 1]
    private Set<FeedbackTheme> themes; // Categorized issues
}
```

#### FeedbackThemeExtractor

```java
@Component
public class FeedbackThemeExtractor {
    
    private static final Map<FeedbackTheme, Set<String>> THEME_KEYWORDS = Map.ofEntries(
        Map.entry(FeedbackTheme.ROUTE_QUALITY, Set.of(
            "rota", "yol", "seçim", "sıra", "mantıklı", "yanlış", "iyi değil", "korkunç rota"
        )),
        Map.entry(FeedbackTheme.CROWDING, Set.of(
            "kalabalık", "insansız", "tenha", "boş", "dolu", "tıklım tıkış", "izmarit"
        )),
        Map.entry(FeedbackTheme.BUDGET_EXCEEDED, Set.of(
            "pahalı", "fiyat", "bütçe", "aştı", "maliyetli", "cüzdan", "zenginler için"
        )),
        Map.entry(FeedbackTheme.TIME_MISMATCH, Set.of(
            "zaman", "saat", "hızlı", "uzun", "geç kaldı", "yetiş", "acele", "uygun değil"
        )),
        Map.entry(FeedbackTheme.BORING, Set.of(
            "sıkıcı", "ilgi çekici değil", "eğlenceli değil", "monoton", "rota donmuş", "hep aynı"
        )),
        Map.entry(FeedbackTheme.ACCESSIBILITY, Set.of(
            "engelli", "tekerlekli", "ulaşım", "erişim", "merdiven", "gidiş", "nasıl gideceğiz"
        )),
        Map.entry(FeedbackTheme.SAFETY_CONCERN, Set.of(
            "güvenlik", "tehlike", "korkuluyor", "korkutucu", "aman", "risk", "tehlikeli yer"
        ))
    );
    
    public Set<FeedbackTheme> extractThemes(String turkishText) {
        Set<FeedbackTheme> themes = new HashSet<>();
        String normalized = turkishText.toLowerCase();
        
        for (Map.Entry<FeedbackTheme, Set<String>> entry : THEME_KEYWORDS.entrySet()) {
            for (String keyword : entry.getValue()) {
                if (normalized.contains(keyword)) {
                    themes.add(entry.getKey());
                    break;
                }
            }
        }
        
        return themes;
    }
}
```

---

## 4. Feedback → Reward Integration

### 4.1 Mapping Strategy: Star Rating + Sentiment → Reward Score

**Design Decision**: Combine quantitative (star rating) and qualitative (sentiment) signals into a single reward score for Thompson Sampling update.

**Formula**:

```
sentimentScore ∈ [-1, 1]
starRating ∈ [1, 5]

normalizedStars = (starRating - 1) / 4  // Map [1,5] → [0, 1]
normalizedSentiment = (sentimentScore + 1) / 2  // Map [-1, 1] → [0, 1]

weightedScore = 0.6 × normalizedStars + 0.4 × normalizedSentiment

// Confidence: if free text provided, boost confidence; else use only stars
confidenceBoost = (feedbackText != null && !feedbackText.isBlank()) ? 1.2 : 1.0
finalRewardScore = Math.min(weightedScore × confidenceBoost, 1.0)
```

**Rationale**:
- Star rating is explicit user intent (60% weight)
- Sentiment from text adds nuance (40% weight)
- Text presence increases confidence in the feedback
- Both mapped to [0, 1] for Thompson Sampling alpha/beta updates

#### FeedbackRewardMapper Service

```java
@Service
public class FeedbackRewardMapper {
    
    /**
     * Map user feedback (stars + sentiment) to reward score for bandit learning.
     */
    public FeedbackReward mapFeedbackToReward(UserFeedback feedback) {
        // 1. Normalize star rating to [0, 1]
        Double normalizedStars = (feedback.getStarRating() - 1.0) / 4.0;
        
        // 2. Normalize sentiment to [0, 1]
        Double sentimentScore = feedback.getSentimentScore();
        Double normalizedSentiment = (sentimentScore + 1.0) / 2.0;
        
        // 3. Weighted combination
        Double weightedScore = 0.6 * normalizedStars + 0.4 * normalizedSentiment;
        
        // 4. Confidence boost if text provided
        Double confidenceBoost = 1.0;
        if (feedback.getFeedbackText() != null && !feedback.getFeedbackText().isBlank()) {
            confidenceBoost = 1.2;
        }
        
        Double finalReward = Math.min(weightedScore * confidenceBoost, 1.0);
        
        // 5. Theme-based penalty/adjustment
        if (feedback.getThemes() != null && !feedback.getThemes().isEmpty()) {
            // Themes indicate specific issues; adjust if critical
            if (feedback.getThemes().contains(FeedbackTheme.SAFETY_CONCERN)) {
                finalReward = Math.min(finalReward, 0.2);  // Safety concerns → low reward
            }
        }
        
        return new FeedbackReward(
            feedback.getId(),
            feedback.getUserId(),
            feedback.getRouteId(),
            finalReward,
            feedback.getThemes()
        );
    }
}

@Data
@AllArgsConstructor
public class FeedbackReward {
    private Long feedbackId;
    private Long userId;
    private Long routeId;
    private Double rewardScore;          // [0, 1] for bandit
    private Set<FeedbackTheme> themes;
}
```

### 4.2 BanditLearningService Feedback Update

**Integration Point**: After feedback is processed, BanditLearningService records a new bandit event with the feedback-derived reward.

```java
// In BanditLearningService
public void recordFeedbackReward(FeedbackReward feedbackReward) {
    // Find associated user and route context
    User user = userRepository.findById(feedbackReward.getUserId())
        .orElseThrow(() -> new UserNotFoundException(...));
    
    Route route = routeRepository.findById(feedbackReward.getRouteId())
        .orElseThrow(() -> new RouteNotFoundException(...));
    
    // Build context from route
    ContextVector context = buildContextFromRoute(route);
    
    // Record as bandit event
    Double reward = feedbackReward.getRewardScore();
    Boolean success = reward >= 0.75;  // Same threshold as other rewards
    
    BanditEvent event = new BanditEvent();
    event.setUserId(user.getId());
    event.setRecommendationLogId(route.getRecommendationLogId());  // Link to original recommendation
    event.setEventType("FEEDBACK_REWARD");
    event.setRewardValue(reward);
    event.setSuccess(success);
    event.setContext(context.toJson());
    event.setCreatedAt(System.currentTimeMillis());
    
    banditEventRepository.save(event);
    
    // Update Thompson Sampling stats for POIs in route
    for (RoutePoi routePoi : route.getRoutePois()) {
        updateThompsonStats(routePoi.getPoiId(), success);
    }
}
```

---

## 5. Feedback Service Implementation

### 5.1 FeedbackService - Main Orchestrator

```java
@Service
@Transactional
@Slf4j
public class FeedbackService {
    private final UserRepository userRepository;
    private final RouteRepository routeRepository;
    private final FeedbackRepository feedbackRepository;
    private final TurkishSentimentAnalyzer sentimentAnalyzer;
    private final FeedbackRewardMapper rewardMapper;
    private final BanditLearningService banditLearningService;
    private final AnalyticsService analyticsService;
    
    public FeedbackResponse submitFeedback(FeedbackRequest request) {
        // 1. Validate user and route exist
        User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new UserNotFoundException(...));
        
        Route route = routeRepository.findById(request.getRouteId())
            .orElseThrow(() -> new RouteNotFoundException(...));
        
        // 2. Create UserFeedback entity
        UserFeedback feedback = new UserFeedback();
        feedback.setUser(user);
        feedback.setRoute(route);
        feedback.setStarRating(request.getStarRating());
        feedback.setFeedbackText(request.getFeedbackText());
        feedback.setCreatedAt(System.currentTimeMillis());
        
        // 3. Analyze sentiment (if text provided)
        if (request.getFeedbackText() != null && !request.getFeedbackText().isBlank()) {
            SentimentAnalysis analysis = sentimentAnalyzer.analyzeSentiment(request.getFeedbackText());
            feedback.setSentimentScore(analysis.getSentimentScore());
            feedback.setThemes(analysis.getThemes());
        } else {
            // No text: derive sentiment purely from stars
            double sentimentFromStars = (request.getStarRating() - 1) / 4.0 * 2.0 - 1.0;  // Map [1,5] → [-1, 1]
            feedback.setSentimentScore(sentimentFromStars);
            feedback.setThemes(request.getSuggestedThemes() != null ? request.getSuggestedThemes() : Set.of());
        }
        
        // 4. Map to reward score
        FeedbackReward reward = rewardMapper.mapFeedbackToReward(feedback);
        feedback.setMappedRewardScore(reward.getRewardScore());
        
        // 5. Persist
        UserFeedback saved = feedbackRepository.save(feedback);
        
        // 6. Update bandit learning asynchronously
        banditLearningService.recordFeedbackReward(reward);
        
        // 7. Log for analytics
        analyticsService.logFeedbackEvent(saved);
        
        log.info("Feedback submitted for route {}: stars={}, sentiment={:.2f}, reward={:.2f}",
            route.getId(), request.getStarRating(), feedback.getSentimentScore(), reward.getRewardScore());
        
        return new FeedbackResponse(
            saved.getId(),
            saved.getSentimentScore(),
            saved.getThemes(),
            saved.getMappedRewardScore(),
            "Geri bildiriminiz kaydedildi. Teşekkürler!"  // "Your feedback was recorded. Thank you!"
        );
    }
}
```

---

## 6. Controller Endpoint

### 6.1 FeedbackController

```java
@RestController
@RequestMapping("/api/feedback")
@Slf4j
public class FeedbackController {
    
    private final FeedbackService feedbackService;
    
    @PostMapping
    public ResponseEntity<FeedbackResponse> submitFeedback(
        @Valid @RequestBody FeedbackRequest request,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            // Ensure user ID matches authenticated principal
            if (!request.getUserId().equals(extractUserId(userDetails))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            FeedbackResponse response = feedbackService.submitFeedback(request);
            return ResponseEntity.ok(response);
        } catch (UserNotFoundException | RouteNotFoundException e) {
            log.warn("Feedback submission failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error("Unexpected error processing feedback", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{feedbackId}")
    public ResponseEntity<FeedbackResponse> getFeedback(@PathVariable Long feedbackId) {
        // Return feedback details (optional - for debugging)
        return ResponseEntity.ok(new FeedbackResponse(...));
    }
    
    private Long extractUserId(UserDetails userDetails) {
        // Extract from JWT or session
        return (Long) ((SecurityContext) SecurityContextHolder.getContext())
            .getAuthentication()
            .getPrincipal();  // Implementation depends on auth structure
    }
}
```

---

## 7. Database Schema Extension

### 7.1 Migration: Add Feedback Tables

```sql
-- V4__Add_Feedback_Schema.sql

CREATE TABLE user_feedback (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    route_id BIGINT REFERENCES routes(id) ON DELETE SET NULL,
    star_rating INTEGER NOT NULL CHECK (star_rating >= 1 AND star_rating <= 5),
    feedback_text TEXT,  -- Turkish free text, max 500 chars in app
    sentiment_score DECIMAL(3, 2),  -- [-1, 1]
    mapped_reward_score DECIMAL(3, 2),  -- [0, 1]
    feedback_processed BOOLEAN DEFAULT FALSE,
    created_at BIGINT NOT NULL,
    CONSTRAINT rating_not_null CHECK (star_rating IS NOT NULL)
);

CREATE TABLE feedback_themes (
    feedback_id BIGINT NOT NULL REFERENCES user_feedback(id) ON DELETE CASCADE,
    theme VARCHAR(50) NOT NULL,  -- Enum: ROUTE_QUALITY, CROWDING, etc.
    PRIMARY KEY (feedback_id, theme)
);

-- Index for quick queries
CREATE INDEX idx_user_feedback_user_id ON user_feedback(user_id);
CREATE INDEX idx_user_feedback_route_id ON user_feedback(route_id);
CREATE INDEX idx_user_feedback_created_at ON user_feedback(created_at DESC);
```

---

## 8. Learning Loop Closure Diagram

```
User visits recommended route
         ↓
[Route completion event]
         ↓
Collect star rating + Turkish feedback text
         ↓
TurkishSentimentAnalyzer:
  - Preprocess Turkish text
  - Lexicon lookup + negation handling
  - Extract sentiment score ∈ [-1, 1]
  - Identify complaint themes
         ↓
FeedbackRewardMapper:
  - Combine stars (60%) + sentiment (40%)
  - Apply confidence boost if text present
  - Map to reward ∈ [0, 1]
         ↓
BanditLearningService:
  - Record bandit event with feedback reward
  - Update Thompson Sampling α/β for POIs
  - success = reward >= 0.75
         ↓
Next recommendation pulls updated Thompson posterior
  - POIs with positive feedback → higher sampled μ
  - POIs with negative feedback → lower sampled μ
         ↓
✓ Loop closed: user experience → model improvement
```

---

## 9. Sentiment Score Interpretation

| Score Range | Interpretation | Reward Mapping | Thompson Update |
|---|---|---|---|
| 0.8 to 1.0 | Very Positive | 0.85-1.0 | α += 1 (strong success) |
| 0.5 to 0.8 | Positive | 0.65-0.85 | α += 1 (success) |
| 0.0 to 0.5 | Neutral/Mixed | 0.45-0.65 | No update or light adjustment |
| -0.5 to 0.0 | Negative/Mixed | 0.25-0.45 | β += 1 (failure) |
| -0.8 to -0.5 | Negative | 0.1-0.25 | β += 1 (strong failure) |
| -1.0 to -0.8 | Very Negative | 0.0-0.1 | β += 2 (critical failure) |

---

## 10. MVP Turkish NLP - Limitations and Future Work

### Current MVP Limitations
- **Lexicon-based only**: No contextual understanding (e.g., "not bad" → positive, but negation handling may miss nuance)
- **Single-word matching**: Phrases not explicitly in dictionary may be missed
- **No dependency parsing**: Turkish grammar (agglutination, case markers) partially ignored
- **Emoji heuristic**: Crude but effective for MVP
- **Domain-specific terms**: Travel/tourism domain terms manually curated

### Future Extensions (Phase 11+)
1. **Turkish Transformer Models**: 
   - Fine-tune mBERT or Turkish BERT on travel sentiment data
   - Handles complex Turkish morphology better

2. **Aspect-Based Sentiment Analysis**:
   - Separate scores per aspect (route_quality, crowd, budget, etc.)
   - More granular reward updates

3. **Topic Modeling**:
   - LDA or Turkish-specific topic models on feedback corpus
   - Identify emergent complaint clusters

4. **Embedding-Based Similarity**:
   - Use Turkish word embeddings to find semantically similar feedback
   - Generalize from sparse feedback to similar routes

5. **Active Learning**:
   - Request more feedback from users on ambiguous routes
   - Reduce cold-start problem

---

## 11. Testing and Validation

### Unit Tests

#### TurkishSentimentAnalyzerTest
```java
@Test
public void testPositiveSentiment() {
    String text = "Bu rota çok güzel, harika deneyim!";
    SentimentAnalysis result = analyzer.analyzeSentiment(text);
    assertThat(result.getSentimentScore()).isGreaterThan(0.5);
}

@Test
public void testNegativeSentiment() {
    String text = "Korkunç bir rota, kalabalıktı ve çok pahalı.";
    SentimentAnalysis result = analyzer.analyzeSentiment(text);
    assertThat(result.getSentimentScore()).isLessThan(-0.3);
}

@Test
public void testNegationHandling() {
    String text = "Rota değil kötü, aksine çok iyi.";  // "Not bad, actually very good"
    SentimentAnalysis result = analyzer.analyzeSentiment(text);
    assertThat(result.getSentimentScore()).isGreaterThan(0.3);
}

@Test
public void testThemeExtraction() {
    String text = "Rota kalitesi çok kötü ve kalabalıktı.";
    SentimentAnalysis result = analyzer.analyzeSentiment(text);
    assertThat(result.getThemes()).contains(FeedbackTheme.ROUTE_QUALITY, FeedbackTheme.CROWDING);
}
```

#### FeedbackRewardMapperTest
```java
@Test
public void test5StarsPositiveSentiment() {
    UserFeedback feedback = createFeedback(5, 0.8, "Harika!");
    FeedbackReward reward = mapper.mapFeedbackToReward(feedback);
    assertThat(reward.getRewardScore()).isGreaterThan(0.8);
}

@Test
public void test1StarNegativeSentiment() {
    UserFeedback feedback = createFeedback(1, -0.9, "Korkunç");
    FeedbackReward reward = mapper.mapFeedbackToReward(feedback);
    assertThat(reward.getRewardScore()).isLessThan(0.2);
}

@Test
public void testSafetyThemePenalty() {
    UserFeedback feedback = createFeedback(3, 0.5, "Good");
    feedback.setThemes(Set.of(FeedbackTheme.SAFETY_CONCERN));
    FeedbackReward reward = mapper.mapFeedbackToReward(feedback);
    assertThat(reward.getRewardScore()).isLessThanOrEqualTo(0.2);
}
```

### Integration Tests

#### FeedbackServiceIntegrationTest
```java
@Test
public void testFullFeedbackFlow() {
    // 1. Create user and route
    User user = createTestUser();
    Route route = createTestRoute(user);
    
    // 2. Submit feedback
    FeedbackRequest request = new FeedbackRequest();
    request.setUserId(user.getId());
    request.setRouteId(route.getId());
    request.setStarRating(5);
    request.setFeedbackText("Harika rota, çok iyi planlanmış!");
    
    FeedbackResponse response = feedbackService.submitFeedback(request);
    
    // 3. Verify feedback persisted
    UserFeedback saved = feedbackRepository.findById(response.getFeedbackId()).get();
    assertThat(saved.getSentimentScore()).isGreaterThan(0.5);
    assertThat(saved.getMappedRewardScore()).isGreaterThan(0.75);
    
    // 4. Verify bandit event created
    BanditEvent event = banditEventRepository.findLatestByRouteId(route.getId());
    assertThat(event.getEventType()).isEqualTo("FEEDBACK_REWARD");
    assertThat(event.getSuccess()).isTrue();
}
```

---

## 12. Performance Considerations

| Operation | Latency Target | Approach |
|---|---|---|
| Sentiment analysis | < 200ms | Lexicon lookup (O(n) where n = token count) |
| Theme extraction | < 100ms | Keyword set intersection |
| Reward mapping | < 50ms | Simple arithmetic |
| Feedback persistence | < 500ms | Indexed DB write |
| Bandit update | < 300ms | Batch or async |
| **Total feedback submission** | **< 1 second** | Async bandit update for non-blocking response |

---

## 13. Localization and Turkish UI Integration

### Turkish UI Strings for Feedback Screen

```kotlin
// Mobile app (Compose)
@Composable
fun FeedbackScreen(route: Route) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Bu rotayı nasıl buldunuz?", style = MaterialTheme.typography.headlineSmall)
        
        // Star rating
        Text("Puan: (1 = Çok Kötü, 5 = Mükemmel)")
        RatingBar(currentRating = selectedRating) { selectedRating = it }
        
        // Free text
        TextField(
            value = feedbackText,
            onValueChange = { feedbackText = it },
            label = { Text("Ek yorum (isteğe bağlı)") },
            placeholder = { Text("Deneyiminizi bizimle paylaşın...") },
            maxLines = 5
        )
        
        // Theme suggestions
        Text("Sorunlar (varsa):")
        listOf(
            FeedbackTheme.ROUTE_QUALITY to "Rota planlaması iyi değildi",
            FeedbackTheme.CROWDING to "Çok kalabalıktı",
            FeedbackTheme.BUDGET_EXCEEDED to "Bütçeyi aştı",
            FeedbackTheme.TIME_MISMATCH to "Zaman yanlış tahmin edildi"
        ).forEach { (theme, label) ->
            Checkbox(
                checked = selectedThemes.contains(theme),
                onCheckedChange = { 
                    if (it) selectedThemes.add(theme) else selectedThemes.remove(theme)
                },
                label = { Text(label) }
            )
        }
        
        Button(
            onClick = { submitFeedback() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Geri Bildirim Gönder")
        }
    }
}
```

---

## 14. Success Criteria

**Phase 11 is complete when:**

1. ✅ Turkish sentiment analyzer processes feedback text with accuracy > 80% on manual test set
2. ✅ Feedback → Reward mapping produces sensible scores (5-star + positive text → high reward)
3. ✅ Bandit learning service integrates feedback rewards into Thompson Sampling updates
4. ✅ Full feedback submission flow (UI → API → sentiment → reward → bandit) works end-to-end
5. ✅ Unit and integration tests pass
6. ✅ Feedback is properly persisted and linked to original recommendations
7. ✅ Backend compiles without errors
8. ✅ Documentation updated with Phase 11 design and implementation notes

---

## 15. Summary

**Phase 11 closes the learning loop** by integrating user textual feedback into the recommendation system's bandit learning process. Through Turkish sentiment analysis (MVP lexicon-based approach), we extract sentiment and complaint themes from user feedback, map them to reward signals, and update the Thompson Sampling model. This enables the system to iteratively improve recommendations based on real user experience—a key demonstration of AI-driven personalization for the thesis.

**Key Achievements**:
- Sentiment-aware reward generation from mixed qualitative/quantitative feedback
- Turkish NLP baseline that is interpretable and reproducible
- Closed-loop learning pipeline from user feedback to model update
- Foundation for future advanced Turkish NLP extensions

**Ready for Phase 12**: Mobile app refactoring and Turkish UX alignment.
