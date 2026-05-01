package com.eskisehir.eventapi.service.nlp;

import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.Set;

/**
 * Turkish sentiment lexicon for MVP baseline sentiment analysis.
 * Contains dictionaries of positive/negative words and modifiers.
 */
@Component
public class TurkishSentimentLexicon {
    
    // Positive sentiment words
    public static final Map<String, Double> POSITIVE_WORDS = Map.ofEntries(
        Map.entry("güzel", 0.8),
        Map.entry("harika", 0.9),
        Map.entry("mükemmel", 0.95),
        Map.entry("hoş", 0.7),
        Map.entry("iyi", 0.6),
        Map.entry("beğendim", 0.75),
        Map.entry("tavsiye", 0.7),
        Map.entry("kesinlikle", 0.8),
        Map.entry("mutlaka", 0.75),
        Map.entry("efsane", 0.85),
        Map.entry("süper", 0.85),
        Map.entry("enfes", 0.85),
        Map.entry("seviyor", 0.8),
        Map.entry("başarılı", 0.7),
        Map.entry("muvaffak", 0.7),
        Map.entry("duygulandım", 0.75),
        Map.entry("etkilendi", 0.7)
    );
    
    // Negative sentiment words
    public static final Map<String, Double> NEGATIVE_WORDS = Map.ofEntries(
        Map.entry("kötü", -0.8),
        Map.entry("berbat", -0.9),
        Map.entry("korkunç", -0.85),
        Map.entry("fena", -0.7),
        Map.entry("hoşlanmadım", -0.8),
        Map.entry("pişman", -0.75),
        Map.entry("boşa", -0.7),
        Map.entry("çöp", -0.9),
        Map.entry("asla", -0.7),
        Map.entry("hiç", -0.7),
        Map.entry("başarısız", -0.75),
        Map.entry("rezalet", -0.9),
        Map.entry("utanç", -0.85),
        Map.entry("hayal kırıklığı", -0.8),
        Map.entry("sinir bozucu", -0.75),
        Map.entry("kızgın", -0.7),
        Map.entry("bıktım", -0.8)
    );
    
    // Negation words that flip sentiment
    public static final Set<String> NEGATION_WORDS = Set.of(
        "değil", "yok", "hiç", "bir", "asla", "ne", "dem", "tanımazsam", "tanımıyorum"
    );
    
    // Intensifiers that amplify sentiment
    public static final Map<String, Double> INTENSIFIERS = Map.ofEntries(
        Map.entry("çok", 1.3),
        Map.entry("iyice", 1.25),
        Map.entry("oldukça", 1.2),
        Map.entry("gerçekten", 1.15),
        Map.entry("çok çok", 1.4),
        Map.entry("çok ama çok", 1.4),
        Map.entry("extremely", 1.3)
    );
    
    // Diminishers that reduce sentiment
    public static final Map<String, Double> DIMINISHERS = Map.ofEntries(
        Map.entry("biraz", 0.7),
        Map.entry("az", 0.6),
        Map.entry("hafif", 0.7),
        Map.entry("kısmen", 0.75),
        Map.entry("sanki", 0.8)
    );
}
