package com.eskisehir.eventapi.service.route.optimization;

import com.eskisehir.eventapi.dto.RouteScoreBreakdown;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

/**
 * Generates human-readable explanations for optimized routes based on score breakdown.
 */
@Service
public class RouteExplainer {
    
    /**
     * Generates narrative explanation for a route based on scores and metrics.
     */
    public String generateExplanation(
        RouteScoreBreakdown scores,
        int poiCount,
        double distanceKm,
        double durationMinutes,
        double costTl,
        double co2EmissionsKg,
        int localPoiCount,
        double budgetBufferTl
    ) {
        StringBuilder explanation = new StringBuilder();
        
        // Main score commentary
        if (scores.getFinalCompositeScore() >= 0.85) {
            explanation.append("Harika bir rota! (Excellent route!)");
        } else if (scores.getFinalCompositeScore() >= 0.70) {
            explanation.append("İyi bir rota. (Good route.)");
        } else if (scores.getFinalCompositeScore() >= 0.55) {
            explanation.append("Makul bir rota. (Reasonable route.)");
        } else {
            explanation.append("Sınırlı seçeneklerle rota oluşturuldu. (Route created with limited options.)");
        }
        explanation.append("\n\n");
        
        // POI and distance commentary
        explanation.append(String.format("• %d POI seçildi. Toplam mesafe: %.1f km.\n", poiCount, distanceKm));
        explanation.append(String.format("• Tahmini süre: %d dakika.\n", (int) durationMinutes));
        
        // Preference fit commentary
        if (scores.getPreferenceFit() >= 0.80) {
            explanation.append("• Tercihlerinize %% 100 uyuyor. (Aligns perfectly with your preferences.)\n");
        } else if (scores.getPreferenceFit() >= 0.60) {
            explanation.append("• Tercihlerinize %% 75'ten fazla uyuyor. (Mostly aligns with your preferences.)\n");
        } else {
            explanation.append("• Tercihlerinize orta düzeyde uyuyor. (Moderately aligns with your preferences.)\n");
        }
        
        // Crowd exposure commentary
        if (scores.getCrowdAvoidance() >= 0.80) {
            explanation.append("• Kalabalık maruziyeti düşük. (Low crowd exposure.)\n");
        } else if (scores.getCrowdAvoidance() >= 0.60) {
            explanation.append("• Kalabalık maruziyeti orta. (Moderate crowd exposure; avoid peak hours.)\n");
        } else {
            explanation.append("• Rota yoğun alanlarda seyahat ediyor. (Route passes through crowded areas.)\n");
        }
        
        // Budget commentary
        if (budgetBufferTl > 0) {
            explanation.append(String.format("• Bütçe tamponunuz: ₺%.0f. (Budget buffer: ₺%.0f.)\n", 
                                            budgetBufferTl, budgetBufferTl));
        } else {
            explanation.append(String.format("• Bütçe maksimumuna ulaştığınız: ₺%.0f.\n", costTl));
        }
        
        // Sustainability commentary
        if (scores.getSustainability() >= 0.80) {
            explanation.append(String.format("• Sürdürülebilir seçim: %.1f kg CO₂. (Sustainable: %.1f kg CO₂.)\n", 
                                            co2EmissionsKg, co2EmissionsKg));
        } else if (scores.getSustainability() >= 0.60) {
            explanation.append(String.format("• Orta karbon ayakizi: %.1f kg CO₂.\n", co2EmissionsKg));
        } else {
            explanation.append(String.format("• Daha düşük karbon seçeneğini düşünün: %.1f kg CO₂.\n", co2EmissionsKg));
        }
        
        // Local support commentary
        if (localPoiCount > poiCount / 2) {
            explanation.append(String.format("• %d yerel işletmeyi destekliyor. (Supporting %d local businesses.)\n", 
                                            localPoiCount, localPoiCount));
        }
        
        // Diversity commentary
        if (scores.getDiversity() >= 0.75) {
            explanation.append("• Çeşitli POI kategorileri. (Diverse POI categories.)\n");
        } else if (scores.getDiversity() >= 0.50) {
            explanation.append("• Makul çeşitlilik. (Reasonable variety.)\n");
        }
        
        // Final recommendation
        explanation.append("\n💡 Tavsiye: ");
        if (scores.getCrowdAvoidance() < 0.50 && scores.getFinalCompositeScore() >= 0.60) {
            explanation.append("Sabah erken saatlerde ziyaret edin. (Visit early morning to avoid crowds.)");
        } else if (scores.getSustainability() < 0.60 && scores.getFinalCompositeScore() >= 0.60) {
            explanation.append("Toplu taşıma kullanmayı düşünün. (Consider public transport.)");
        } else if (scores.getPreferenceFit() < 0.70) {
            explanation.append("Alternatif rotaya bakın. (Consider alternative route.)");
        } else {
            explanation.append("Harika! Eğlenin. (Enjoy your tour!)");
        }
        
        return explanation.toString();
    }
    
    /**
     * Generates key insight about which factor is most important in this route.
     */
    public String generateKeyInsight(RouteScoreBreakdown scores) {
        Map<String, Double> factors = new HashMap<>();
        factors.put("Tercih Uyumu", scores.getPreferenceFit());
        factors.put("Kalabalık Kaçınma", scores.getCrowdAvoidance());
        factors.put("Bütçe Verimliliği", scores.getBudgetEfficiency());
        factors.put("Sürdürülebilirlik", scores.getSustainability());
        factors.put("Yerel Destek", scores.getLocalSupport());
        factors.put("Çeşitlilik", scores.getDiversity());
        
        // Find highest and lowest scoring factors
        String bestFactor = factors.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("Tercih Uyumu");
        
        String weakestFactor = factors.entrySet().stream()
            .min(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("Çeşitlilik");
        
        Double bestScore = factors.get(bestFactor);
        Double worstScore = factors.get(weakestFactor);
        
        StringBuilder insight = new StringBuilder();
        insight.append(String.format("En güçlü taraf: %s (%.0f%%). ", bestFactor, bestScore * 100));
        insight.append(String.format("Geliştirilecek: %s (%.0f%%).", weakestFactor, worstScore * 100));
        
        return insight.toString();
    }
}
