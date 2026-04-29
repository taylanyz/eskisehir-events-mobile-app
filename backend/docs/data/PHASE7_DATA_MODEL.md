# Phase 7 Data Model and Schema Targets

## Scope

This Phase 7 package completes the data modeling and PostgreSQL transition design work for the current Spring Boot backend.

It does not execute the runtime migration yet. The goal is to make the schema, normalization decisions, PostgreSQL direction, and JPA mapping gaps explicit before the implementation-heavy refactor phases.

## Current Entity Surface

The existing backend already persists the following core concepts:

- `users`
- `user_preferences`
- `user_preferred_categories`
- `user_preferred_tags`
- `pois`
- `poi_tags`
- `routes`
- `route_items`
- `route_ratings`
- `user_interactions`
- `recommendation_logs`
- `user_feedback`
- `bandit_events`
- `bandit_arm_stats`
- `weather_data`

This means the codebase already has a usable MVP persistence model, but the target PostgreSQL schema for thesis-quality evolution should separate stable dimensions from dynamic metrics more cleanly.

## Target PostgreSQL Schema

### Identity and User Profile

#### `users`
- `id` bigint primary key
- `email` varchar(255) not null unique
- `password_hash` varchar(255) not null
- `display_name` varchar(255) not null
- `home_city_id` bigint null
- `created_at` timestamp not null
- `last_login_at` timestamp null

#### `cities`
- `id` bigint primary key
- `code` varchar(64) not null unique
- `name` varchar(128) not null
- `country_code` varchar(8) not null
- `timezone` varchar(64) not null
- `is_active` boolean not null default true

#### `user_preferences`
- `user_id` bigint primary key references `users(id)`
- `budget_sensitivity` varchar(32) null
- `crowd_tolerance` varchar(32) null
- `mobility_preference` varchar(32) null
- `sustainability_preference` numeric(4,3) null
- `max_walking_minutes` integer null

#### `user_preferred_categories`
- `user_id` bigint not null references `user_preferences(user_id)`
- `category_code` varchar(64) not null
- primary key (`user_id`, `category_code`)

#### `user_preferred_tags`
- `user_id` bigint not null references `user_preferences(user_id)`
- `tag` varchar(128) not null
- primary key (`user_id`, `tag`)

### POI and Discovery

#### `poi_categories`
- `code` varchar(64) primary key
- `display_name` varchar(128) not null
- `is_event_category` boolean not null default false

#### `pois`
- `id` bigint primary key
- `city_id` bigint not null references `cities(id)`
- `category_code` varchar(64) not null references `poi_categories(code)`
- `name` varchar(255) not null
- `description` text null
- `district` varchar(128) null
- `venue` varchar(255) not null
- `latitude` numeric(9,6) not null
- `longitude` numeric(9,6) not null
- `location` geometry(Point, 4326) null
- `event_date` timestamp null
- `price_try` numeric(10,2) null
- `budget_level` varchar(32) not null
- `image_url` varchar(1024) null
- `estimated_visit_minutes` integer null
- `indoor_outdoor` varchar(32) null
- `family_friendly` boolean null
- `opening_time` time null
- `closing_time` time null
- `is_active` boolean not null default true
- `created_at` timestamp not null
- `updated_at` timestamp not null

#### `poi_tags`
- `id` bigint primary key
- `tag` varchar(128) not null unique

#### `poi_tag_map`
- `poi_id` bigint not null references `pois(id)`
- `tag_id` bigint not null references `poi_tags(id)`
- primary key (`poi_id`, `tag_id`)

#### `poi_metrics`
- `poi_id` bigint primary key references `pois(id)`
- `sustainability_score` numeric(4,3) not null default 0.500
- `local_business_score` numeric(4,3) not null default 0.500
- `crowd_proxy` numeric(4,3) not null default 0.500
- `popularity_score` numeric(4,3) not null default 0.500
- `updated_at` timestamp not null

`poi_metrics` is intentionally separated from `pois` because these values are recalculated more frequently than descriptive POI fields. This reduces write contention and makes future scoring pipelines cleaner.

### Route Planning and Social Layer

#### `routes`
- `id` bigint primary key
- `user_id` bigint not null references `users(id)`
- `name` varchar(255) null
- `total_distance_km` numeric(10,3) null
- `total_duration_minutes` integer null
- `estimated_budget` numeric(10,2) null
- `carbon_score` numeric(10,3) null
- `transport_mode` varchar(32) null
- `status` varchar(32) not null
- `is_public` boolean not null default false
- `share_code` varchar(64) null unique
- `average_rating` numeric(3,2) not null default 0.00
- `total_ratings` integer not null default 0
- `share_count` integer not null default 0
- `created_at` timestamp not null

#### `route_items`
- `id` bigint primary key
- `route_id` bigint not null references `routes(id)` on delete cascade
- `poi_id` bigint not null references `pois(id)`
- `visit_order` integer not null
- `estimated_arrival` time null
- `estimated_departure` time null
- `distance_from_previous_km` numeric(10,3) null
- unique (`route_id`, `visit_order`)

#### `route_ratings`
- `id` bigint primary key
- `route_id` bigint not null references `routes(id)` on delete cascade
- `user_id` bigint not null references `users(id)`
- `rating` numeric(2,1) not null
- `comment` varchar(500) null
- `created_at` timestamp not null
- `updated_at` timestamp not null
- unique (`route_id`, `user_id`)

### Learning, Feedback, and Analytics

#### `user_interactions`
- `id` bigint primary key
- `user_id` bigint not null references `users(id)`
- `poi_id` bigint not null references `pois(id)`
- `interaction_type` varchar(32) not null
- `timestamp` timestamp not null
- `context_weather` varchar(64) null
- `context_time_of_day` varchar(32) null
- `context_day_of_week` varchar(16) null

#### `recommendation_logs`
- `id` bigint primary key
- `user_id` bigint not null references `users(id)`
- `poi_id` bigint not null references `pois(id)`
- `score` numeric(10,6) null
- `rank_position` integer null
- `was_clicked` boolean not null default false
- `was_visited` boolean not null default false
- `algorithm_version` varchar(64) null
- `created_at` timestamp not null

#### `user_feedback`
- `id` bigint primary key
- `user_id` bigint not null references `users(id)`
- `route_id` bigint null references `routes(id)`
- `rating` integer not null
- `comment_text` text null
- `sentiment_score` numeric(4,3) null
- `created_at` timestamp not null

#### `bandit_events`
- `id` bigint primary key
- `user_id` bigint not null references `users(id)`
- `poi_id` bigint not null references `pois(id)`
- `context_vector_json` jsonb null
- `reward` numeric(10,6) null
- `created_at` timestamp not null

#### `bandit_arm_stats`
- `id` bigint primary key
- `user_id` bigint not null references `users(id)`
- `poi_id` bigint not null references `pois(id)`
- `alpha` numeric(10,6) not null default 1.0
- `beta` numeric(10,6) not null default 1.0
- `plays` bigint not null default 0
- `wins` bigint not null default 0
- `updated_at` timestamp not null
- unique (`user_id`, `poi_id`)

### Cached External Context

#### `weather_data`
- `id` bigint primary key
- `city_id` bigint null references `cities(id)`
- `latitude` numeric(9,6) not null
- `longitude` numeric(9,6) not null
- `condition` varchar(64) not null
- `temperature` integer not null
- `humidity` integer not null
- `wind_speed` numeric(8,3) not null
- `is_raining` boolean not null
- `timestamp` timestamp not null
- `last_updated` timestamp not null

## Normalization Decisions

- `poi_categories` is modeled as a dimension table in the target PostgreSQL design, even though the current code uses an enum.
- `poi_tags` and `poi_tag_map` replace the current simple `@ElementCollection` structure for better deduplication and analytics.
- `poi_metrics` is separated from `pois` to isolate frequently updated ranking signals from descriptive POI metadata.
- `recommendation_logs` and `user_interactions` remain separate because they answer different questions: what the system showed versus what the user actually did.
- `cities` is introduced now to avoid hard-coding Eskişehir into the long-term relational model.

## Multi-City Readiness

Eskişehir remains the first production city, but the target schema must support additional cities without table redesign. The minimum requirement is a `cities` table and a `city_id` foreign key on all city-bound resources.

Current immediate priority:

- `users.home_city_id`
- `pois.city_id`
- `weather_data.city_id`

Future optional extensions:

- route start city snapshots
- city-specific operating calendars
- city-specific transport presets

## Phase 7 Outcome

Phase 7 is complete as a design package when the team can answer the following without ambiguity:

- Which logical tables exist now and which ones are target normalized tables
- Which fields stay in JPA enums versus which ones should become dimensions later
- How PostgreSQL and PostGIS will be introduced
- Which tables require indexing for recommendation, route, analytics, and feedback workloads
- How H2 development data will transition to PostgreSQL safely