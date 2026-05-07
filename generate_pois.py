#!/usr/bin/env python3
import json
import random
from datetime import datetime, timedelta

# Existing categories and Turkish names
categories = {
    "WORKSHOP": ["Seramik Atölyesi", "Fotoğraf Atölyesi", "Yazılım Kursu", "Yemek Pişirme Kursu", "Dans Dersi", "Ressam Atölyesi", "Kaligrafi Kursu"],
    "WALKING": ["Porsuk Çayı Yürüyüşü", "Orman Doğa Yürüyüşü", "Şehir Yürüyüş Turu", "Tarih Yürüyüşü", "Park Yürüyüşü"],
    "PARK": ["Yeşil Alan Parkı", "Piknik Alanı", "Çocuk Oyun Parkı", "Spor Parkı", "Rekreasyon Alanı"],
    "CULTURAL": ["Kültür Merkezi", "Sanat Galerisi", "Sosyal Mekan", "Topluluk Merkezi", "Yaratıcı Alan"],
    "SPORTS": ["Voleybol Turnuvası", "Yoga Aktivitesi", "Yüzme Etkinliği", "Bisiklet Yarışması", "Koşu Maratonu"],
    "MARKET": ["Pazarın Pazarı", "Antika Pazarı", "Çiftçi Pazarı", "El Sanatları Pazarı", "Kitap Pazarı"],
    "NATURE": ["Orman Bölgesi", "Dağ Yürüyüş Parkuru", "Tepe Gezisi", "Vadiye Iniş", "Kamp Alanı"],
    "RESTAURANT": ["Lezzet Bahçesi", "Geleneksel Mutfak", "Ocakbaşı Restoran", "Mantı Evi", "Fırın Ürünleri"],
}

desc_templates = {
    "WORKSHOP": "Profesyonel eğitmenleriyle {name} atölyesi. Yeni beceriler öğren ve yaratıcı ol.",
    "WALKING": "{name} boyunca rehberli bir tur. Doğa ve kültürü keşfet.",
    "PARK": "{name} şehrin ortasında rahatlamak ve doğayı yakından tanımak için ideal.",
    "CULTURAL": "{name} yerli sanatçıları ve kültürel etkinlikleri sergiliyor.",
    "SPORTS": "{name} heyecan dolu bir spor etkinliği. Katıl veya izle.",
    "MARKET": "{name} pazarında yerel ve taze ürünler bulabilirsin.",
    "NATURE": "{name} doğanın güzelliğinin ortasında. Macera ve rahatlama.",
    "RESTAURANT": "{name} kendine özgü lezzet sunar. Yerel tatları tadıyor.",
}

base_coords = [
    (39.7667, 30.5256),  # Center
    (39.7700, 30.5300),
    (39.7650, 30.5200),
    (39.7750, 30.5150),
    (39.7600, 30.5350),
    (39.7800, 30.5000),
    (39.7550, 30.5100),
]

tags_map = {
    "WORKSHOP": ["handcraft", "learning", "fun", "interactive"],
    "WALKING": ["outdoor", "nature", "guided", "scenic", "exercise"],
    "PARK": ["free", "outdoor", "family-friendly", "relax", "nature"],
    "CULTURAL": ["local", "art", "community", "social"],
    "SPORTS": ["active", "competitive", "exercise", "team"],
    "MARKET": ["shopping", "local", "authentic", "handmade"],
    "NATURE": ["outdoor", "adventure", "hiking", "scenic"],
    "RESTAURANT": ["food", "local", "traditional", "must-try"],
}

def generate_coordinate():
    """Generate random coordinate near Eskişehir"""
    base = random.choice(base_coords)
    lat = base[0] + random.uniform(-0.02, 0.02)
    lon = base[1] + random.uniform(-0.02, 0.02)
    return round(lat, 4), round(lon, 4)

def generate_poi(category_type, name, index):
    """Generate a single POI"""
    lat, lon = generate_coordinate()
    
    desc = desc_templates.get(category_type, f"{name} hakkında merak edilecek bir mekan.")
    desc = desc.replace("{name}", name)
    
    # Pricing
    if category_type == "PARK" or category_type == "WALKING" or category_type == "NATURE":
        price = 0.0
        budget_level = "FREE"
    elif category_type == "MARKET":
        price = 0.0
        budget_level = "FREE"
    elif category_type in ["WORKSHOP", "SPORTS"]:
        price = random.choice([50, 75, 100, 120, 150])
        budget_level = "LOW" if price < 100 else "MEDIUM"
    else:
        price = random.choice([0, 20, 40, 60, 80, 100])
        budget_level = "FREE" if price == 0 else ("LOW" if price < 50 else "MEDIUM")
    
    # Scores
    sustainability_score = random.uniform(0.5, 1.0)
    local_business_score = random.uniform(0.5, 1.0)
    crowd_proxy = random.uniform(0.3, 0.9)
    popularity_score = random.uniform(0.5, 0.9)
    
    poi = {
        "name": name,
        "description": desc,
        "category": "WORKSHOP" if category_type == "WORKSHOP" else category_type,
        "district": random.choice(["Tepebaşı", "Odunpazarı", "Mihalıççık"]),
        "latitude": lat,
        "longitude": lon,
        "venue": name,
        "price": price,
        "budgetLevel": budget_level,
        "imageUrl": f"https://picsum.photos/seed/poi{index}/400/300",
        "tags": random.sample(tags_map.get(category_type, ["local", "popular"]), k=min(3, len(tags_map.get(category_type, [])))),
        "estimatedVisitMinutes": random.choice([30, 45, 60, 90, 120]) if category_type != "WALKING" else random.choice([60, 90, 120, 150]),
        "indoorOutdoor": "INDOOR" if category_type in ["WORKSHOP", "RESTAURANT", "MARKET"] else ("OUTDOOR" if category_type in ["PARK", "WALKING", "NATURE", "MARKET"] else "BOTH"),
        "familyFriendly": category_type not in ["SPORTS"],
        "sustainabilityScore": sustainability_score,
        "localBusinessScore": local_business_score,
        "crowdProxy": crowd_proxy,
        "popularityScore": popularity_score,
        "isActive": True
    }
    
    if "openingTime" in ["RESTAURANT", "WORKSHOP", "MARKET", "CULTURAL"]:
        poi["openingTime"] = "09:00"
        poi["closingTime"] = "22:00"
    
    return poi

# Load existing POIs
with open("backend/src/main/resources/data/pois.json", "r", encoding="utf-8") as f:
    pois = json.load(f)

print(f"Loaded {len(pois)} existing POIs")

# Keep only the original 38 POIs by loading from scratch
original_pois = pois[:38] if len(pois) > 38 else pois
pois = original_pois

# Generate new POIs
new_index = 100
categories_list = list(categories.items())

# Generate ~150 new POIs (3-4 per category name variation)
for category_type, names in categories_list:
    for name in names:
        for i in range(3):  # 3 variations per name = ~7*3 = 21 per category
            poi_name = f"{name}" if i == 0 else f"{name} - Şube {i}"
            poi = generate_poi(category_type, poi_name, new_index)
            pois.append(poi)
            new_index += 1

print(f"Generated {len(pois)} total POIs")

# Save updated POIs
with open("backend/src/main/resources/data/pois.json", "w", encoding="utf-8") as f:
    json.dump(pois, f, ensure_ascii=False, indent=2)

print(f"✅ Successfully saved {len(pois)} POIs to pois.json")
