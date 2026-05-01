package com.eskisehir.eventapi.service.nlp;

import com.eskisehir.eventapi.domain.model.FeedbackTheme;
import org.springframework.stereotype.Component;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Extracts categorized complaint themes from Turkish feedback text.
 * Uses keyword matching to identify specific issues (crowding, budget, etc.).
 */
@Component
public class FeedbackThemeExtractor {
    
    private static final Map<FeedbackTheme, Set<String>> THEME_KEYWORDS = Map.ofEntries(
        Map.entry(FeedbackTheme.ROUTE_QUALITY, Set.of(
            "rota", "yol", "seçim", "sıra", "mantıklı", "yanlış", "iyi değil", 
            "korkunç rota", "route", "plan", "sequence", "order"
        )),
        Map.entry(FeedbackTheme.CROWDING, Set.of(
            "kalabalık", "insansız", "tenha", "boş", "dolu", "tıklım tıkış", "izmarit",
            "crowded", "empty", "busy", "packed", "toplanmış"
        )),
        Map.entry(FeedbackTheme.BUDGET_EXCEEDED, Set.of(
            "pahalı", "fiyat", "bütçe", "aştı", "maliyetli", "cüzdan", "zenginler için",
            "expensive", "budget", "cost", "price", "afford"
        )),
        Map.entry(FeedbackTheme.TIME_MISMATCH, Set.of(
            "zaman", "saat", "hızlı", "uzun", "geç kaldı", "yetiş", "acele", "uygun değil",
            "time", "hour", "quick", "long", "rushed", "duration"
        )),
        Map.entry(FeedbackTheme.BORING, Set.of(
            "sıkıcı", "ilgi çekici değil", "eğlenceli değil", "monoton", "rota donmuş", "hep aynı",
            "boring", "dull", "monotonous", "repetitive", "uninteresting"
        )),
        Map.entry(FeedbackTheme.ACCESSIBILITY, Set.of(
            "engelli", "tekerlekli", "ulaşım", "erişim", "merdiven", "gidiş", "nasıl gideceğiz",
            "accessible", "wheelchair", "stairs", "disabled", "mobility"
        )),
        Map.entry(FeedbackTheme.SAFETY_CONCERN, Set.of(
            "güvenlik", "tehlike", "korkuluyor", "korkutucu", "aman", "risk", "tehlikeli yer",
            "safety", "danger", "unsafe", "scary", "risk", "concerned"
        ))
    );
    
    /**
     * Extract complaint themes from feedback text.
     * Returns set of identified themes.
     */
    public Set<FeedbackTheme> extractThemes(String turkishText) {
        Set<FeedbackTheme> themes = new HashSet<>();
        
        if (turkishText == null || turkishText.isBlank()) {
            return themes;
        }
        
        String normalized = turkishText.toLowerCase();
        
        for (Map.Entry<FeedbackTheme, Set<String>> entry : THEME_KEYWORDS.entrySet()) {
            for (String keyword : entry.getValue()) {
                if (normalized.contains(keyword)) {
                    themes.add(entry.getKey());
                    break;  // Move to next theme
                }
            }
        }
        
        return themes;
    }
}
