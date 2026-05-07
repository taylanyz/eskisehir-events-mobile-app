-- Create weather_data table for weather caching
CREATE TABLE IF NOT EXISTS weather_data (
    id BIGSERIAL PRIMARY KEY,
    latitude NUMERIC(10, 6) NOT NULL,
    longitude NUMERIC(11, 6) NOT NULL,
    temperature NUMERIC(5, 2),
    feels_like NUMERIC(5, 2),
    temp_min NUMERIC(5, 2),
    temp_max NUMERIC(5, 2),
    pressure INTEGER,
    humidity INTEGER,
    weather_condition VARCHAR(100),
    description VARCHAR(255),
    wind_speed NUMERIC(5, 2),
    wind_direction INTEGER,
    cloudiness INTEGER,
    rain_volume NUMERIC(5, 2),
    snow_volume NUMERIC(5, 2),
    last_updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for weather data queries
CREATE INDEX idx_weather_location ON weather_data(latitude, longitude);
CREATE INDEX idx_weather_last_updated ON weather_data(last_updated DESC);
CREATE INDEX idx_weather_created_at ON weather_data(created_at DESC);
