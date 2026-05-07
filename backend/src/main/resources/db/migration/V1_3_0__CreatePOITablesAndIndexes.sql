-- PostgreSQL migration for Phase 13: Eskişehir POI Dataset
-- Migration: V1_3_0__CreatePOITablesAndIndexes.sql

-- Create ENUM types for POI attributes
CREATE TYPE poi_category AS ENUM (
    'MUSEUM', 'HISTORICAL_SITE', 'CULTURAL_CENTER', 'ART_GALLERY',
    'MOSQUE', 'CHURCH', 'SYNAGOGUE', 'TEMPLE',
    'PARK', 'GARDEN', 'NATURE_RESERVE', 'ZOO',
    'RESTAURANT', 'CAFE', 'BAKERY', 'TRADITIONAL_MARKET',
    'SHOPPING_CENTER', 'BAZAAR', 'ANTIQUE_SHOP', 'BOOKSTORE',
    'CINEMA', 'THEATER', 'SPORTS_FACILITY', 'SWIMMING_POOL',
    'LIBRARY', 'UNIVERSITY', 'EDUCATIONAL_INSTITUTION',
    'LANDMARK', 'SCENIC_VIEWPOINT', 'OTHER'
);

CREATE TYPE poi_district AS ENUM (
    'ODUNPAZARI', 'SAZOVA', 'YUNUSELI', 'ESKISEHIR_CENTER',
    'TEPEBASΙ', 'ALPASLAN', 'HOŞNUDIYE', 'BAHÇELIEVLER',
    'MIHALICILAR', 'SITELER'
);

CREATE TYPE price_level AS ENUM (
    'FREE', 'BUDGET', 'MODERATE', 'EXPENSIVE', 'LUXURY'
);

CREATE TYPE location_type AS ENUM (
    'INDOOR', 'OUTDOOR', 'MIXED'
);

-- Main POI table
CREATE TABLE poi (
    id VARCHAR(36) PRIMARY KEY,
    
    -- Core Identification
    name VARCHAR(255) NOT NULL,
    english_name VARCHAR(255),
    category poi_category NOT NULL,
    district poi_district NOT NULL,
    
    -- Location
    latitude NUMERIC(10, 6) NOT NULL CHECK (latitude >= -90 AND latitude <= 90),
    longitude NUMERIC(11, 6) NOT NULL CHECK (longitude >= -180 AND longitude <= 180),
    address VARCHAR(500) NOT NULL,
    
    -- Description
    description TEXT,
    english_description TEXT,
    
    -- Operations
    operating_hours JSONB,  -- {"monday":"09:00-18:00", ...}
    
    -- Pricing
    price_level price_level,
    estimated_cost NUMERIC(10, 2),
    estimated_visit_duration INTEGER,  -- in minutes
    
    -- Classification
    tags TEXT,  -- comma-separated or JSON
    location_type location_type,
    
    -- Accessibility
    wheelchair_accessible BOOLEAN DEFAULT FALSE,
    public_transit_access BOOLEAN DEFAULT FALSE,
    parking_available BOOLEAN DEFAULT FALSE,
    restrooms BOOLEAN DEFAULT FALSE,
    child_friendly BOOLEAN DEFAULT FALSE,
    senior_friendly BOOLEAN DEFAULT FALSE,
    pet_friendly BOOLEAN DEFAULT FALSE,
    
    -- Contact
    phone_number VARCHAR(20),
    email VARCHAR(100),
    website VARCHAR(500),
    instagram VARCHAR(100),
    
    -- Proxy Scores (0-100)
    popularity_score NUMERIC(5, 2) CHECK (popularity_score >= 0 AND popularity_score <= 100),
    crowd_proxy_score NUMERIC(5, 2) CHECK (crowd_proxy_score >= 0 AND crowd_proxy_score <= 100),
    sustainability_score NUMERIC(5, 2) CHECK (sustainability_score >= 0 AND sustainability_score <= 100),
    local_business_score NUMERIC(5, 2) CHECK (local_business_score >= 0 AND local_business_score <= 100),
    average_score NUMERIC(5, 2),
    
    -- Metadata
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_source_notes VARCHAR(500),
    
    -- Indexing
    CONSTRAINT check_valid_coordinates CHECK (
        latitude BETWEEN -90 AND 90 AND longitude BETWEEN -180 AND 180
    )
);

-- Create indexes for performance
CREATE INDEX idx_poi_category ON poi(category);
CREATE INDEX idx_poi_district ON poi(district);
CREATE INDEX idx_poi_location ON poi(latitude, longitude);
CREATE INDEX idx_poi_popularity_score ON poi(popularity_score DESC);
CREATE INDEX idx_poi_sustainability_score ON poi(sustainability_score DESC);
CREATE INDEX idx_poi_local_business_score ON poi(local_business_score DESC);
CREATE INDEX idx_poi_wheelchair_accessible ON poi(wheelchair_accessible) WHERE wheelchair_accessible = true;
CREATE INDEX idx_poi_child_friendly ON poi(child_friendly) WHERE child_friendly = true;
CREATE INDEX idx_poi_price_level ON poi(price_level);
CREATE INDEX idx_poi_created_at ON poi(created_at DESC);
CREATE INDEX idx_poi_name_search ON poi USING GIN(to_tsvector('turkish', name));

-- Create GiST index for geographic queries
CREATE INDEX idx_poi_location_gist ON poi USING GIST (
    box(point(longitude - 0.05, latitude - 0.05), 
        point(longitude + 0.05, latitude + 0.05))
);

-- Create a materialized view for statistics
CREATE VIEW poi_statistics AS
SELECT 
    COUNT(*) as total_pois,
    COUNT(DISTINCT category) as total_categories,
    COUNT(DISTINCT district) as total_districts,
    AVG(popularity_score) as avg_popularity_score,
    AVG(crowd_proxy_score) as avg_crowd_score,
    AVG(sustainability_score) as avg_sustainability_score,
    AVG(local_business_score) as avg_local_business_score,
    COUNT(CASE WHEN wheelchair_accessible = true THEN 1 END) as wheelchair_accessible_count,
    COUNT(CASE WHEN child_friendly = true THEN 1 END) as child_friendly_count,
    COUNT(CASE WHEN price_level = 'FREE' THEN 1 END) as free_pois_count
FROM poi;

-- Create view for district statistics
CREATE VIEW poi_district_statistics AS
SELECT 
    district,
    COUNT(*) as poi_count,
    AVG(popularity_score) as avg_popularity,
    AVG(sustainability_score) as avg_sustainability,
    COUNT(CASE WHEN wheelchair_accessible = true THEN 1 END) as accessible_count
FROM poi
GROUP BY district
ORDER BY poi_count DESC;

-- Create view for category statistics
CREATE VIEW poi_category_statistics AS
SELECT 
    category,
    COUNT(*) as poi_count,
    AVG(popularity_score) as avg_popularity,
    AVG(estimated_cost) as avg_cost,
    COUNT(CASE WHEN child_friendly = true THEN 1 END) as child_friendly_count
FROM poi
GROUP BY category
ORDER BY poi_count DESC;

-- Trigger to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_poi_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER poi_updated_at_trigger
BEFORE UPDATE ON poi
FOR EACH ROW
EXECUTE FUNCTION update_poi_updated_at();

-- Trigger to calculate average score
CREATE OR REPLACE FUNCTION calculate_poi_average_score()
RETURNS TRIGGER AS $$
BEGIN
    NEW.average_score := (
        COALESCE(NEW.popularity_score, 0) +
        COALESCE(NEW.crowd_proxy_score, 0) +
        COALESCE(NEW.sustainability_score, 0) +
        COALESCE(NEW.local_business_score, 0)
    ) / 4.0;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER poi_average_score_trigger
BEFORE INSERT OR UPDATE ON poi
FOR EACH ROW
EXECUTE FUNCTION calculate_poi_average_score();

-- Grant permissions
GRANT SELECT ON poi TO public;
GRANT SELECT ON poi_statistics TO public;
GRANT SELECT ON poi_district_statistics TO public;
GRANT SELECT ON poi_category_statistics TO public;
