-- ============================================================
-- Flyway Migration V1: Entity-Aligned PostgreSQL Schema
-- Source of truth: JPA entities in com.eskisehir.eventapi.domain.model
-- Naming: SpringPhysicalNamingStrategy (camelCase -> snake_case)
-- ============================================================

-- USERS
create table if not exists users (
    id bigserial primary key,
    email varchar(255) not null unique,
    password_hash varchar(255) not null,
    display_name varchar(255) not null,
    created_at timestamp not null,
    last_login_at timestamp
);

-- USER_PREFERENCES
-- @MapsId on 'user' field means id column is both PK and FK to users(id)
create table if not exists user_preferences (
    id bigint primary key references users(id) on delete cascade,
    budget_sensitivity varchar(255),
    crowd_tolerance varchar(255),
    mobility_preference varchar(255),
    sustainability_preference double precision,
    max_walking_minutes integer
);

-- USER_PREFERRED_CATEGORIES
-- @ElementCollection on UserPreference.preferredCategories
-- @CollectionTable(name="user_preferred_categories", joinColumns=@JoinColumn(name="user_id"))
create table if not exists user_preferred_categories (
    user_id bigint not null references user_preferences(id) on delete cascade,
    category varchar(255) not null
);

-- USER_PREFERRED_TAGS
-- @ElementCollection on UserPreference.preferredTags
-- @CollectionTable(name="user_preferred_tags", joinColumns=@JoinColumn(name="user_id"))
create table if not exists user_preferred_tags (
    user_id bigint not null references user_preferences(id) on delete cascade,
    tag varchar(255) not null
);

-- POIS
-- @ElementCollection tags stored separately; score columns embedded directly in pois
-- Category is an enum (EnumType.STRING) — stored as varchar
create table if not exists pois (
    id bigserial primary key,
    name varchar(255) not null,
    description varchar(2000),
    category varchar(255) not null,
    district varchar(255),
    latitude double precision not null,
    longitude double precision not null,
    venue varchar(255) not null,
    date timestamp,
    price double precision,
    budget_level varchar(255) not null,
    image_url varchar(255),
    estimated_visit_minutes integer,
    indoor_outdoor varchar(255),
    family_friendly boolean,
    sustainability_score double precision default 0.5,
    local_business_score double precision default 0.5,
    crowd_proxy double precision default 0.5,
    popularity_score double precision default 0.5,
    opening_time time,
    closing_time time,
    is_active boolean not null default true
);

-- POI_TAGS
-- @ElementCollection on Poi.tags
-- @CollectionTable(name="poi_tags", joinColumns=@JoinColumn(name="poi_id"))
create table if not exists poi_tags (
    poi_id bigint not null references pois(id) on delete cascade,
    tag varchar(255) not null
);

-- ROUTES
create table if not exists routes (
    id bigserial primary key,
    user_id bigint not null references users(id),
    name varchar(255),
    total_distance_km double precision,
    total_duration_minutes integer,
    estimated_budget double precision,
    carbon_score double precision,
    transport_mode varchar(255),
    status varchar(255) not null,
    created_at timestamp not null,
    is_public boolean not null default false,
    share_code varchar(255) unique,
    average_rating double precision default 0.0,
    total_ratings integer default 0,
    share_count integer default 0
);

-- ROUTE_ITEMS
create table if not exists route_items (
    id bigserial primary key,
    route_id bigint not null references routes(id) on delete cascade,
    poi_id bigint not null references pois(id),
    visit_order integer not null,
    estimated_arrival time,
    estimated_departure time,
    distance_from_previous_km double precision
);

-- ROUTE_RATINGS
create table if not exists route_ratings (
    id bigserial primary key,
    route_id bigint not null references routes(id) on delete cascade,
    user_id bigint not null references users(id),
    rating double precision not null,
    comment varchar(500),
    created_at timestamp not null,
    updated_at timestamp not null,
    unique (route_id, user_id)
);

-- USER_INTERACTIONS
create table if not exists user_interactions (
    id bigserial primary key,
    user_id bigint not null references users(id),
    poi_id bigint not null references pois(id),
    interaction_type varchar(255) not null,
    timestamp timestamp not null,
    context_weather varchar(255),
    context_time_of_day varchar(255),
    context_day_of_week varchar(255)
);

-- RECOMMENDATION_LOGS
-- Note: field 'rank' (not 'rank_position') per RecommendationLog entity
create table if not exists recommendation_logs (
    id bigserial primary key,
    user_id bigint not null references users(id),
    poi_id bigint not null references pois(id),
    score double precision,
    rank integer,
    was_clicked boolean,
    was_visited boolean,
    algorithm_version varchar(255),
    created_at timestamp not null
);

-- USER_FEEDBACK
create table if not exists user_feedback (
    id bigserial primary key,
    user_id bigint not null references users(id),
    route_id bigint references routes(id),
    rating integer not null,
    comment_text varchar(2000),
    sentiment_score double precision,
    created_at timestamp not null
);

-- BANDIT_ARM_STATS
create table if not exists bandit_arm_stats (
    id bigserial primary key,
    user_id bigint not null references users(id),
    poi_id bigint not null references pois(id),
    alpha double precision not null default 1.0,
    beta double precision not null default 1.0,
    plays bigint not null default 0,
    wins bigint not null default 0,
    updated_at timestamp not null,
    unique (user_id, poi_id)
);

-- BANDIT_EVENTS
-- context_vector_json: @Column(length=1000) -> varchar(1000)
create table if not exists bandit_events (
    id bigserial primary key,
    user_id bigint not null references users(id),
    poi_id bigint not null references pois(id),
    context_vector_json varchar(1000),
    reward double precision,
    created_at timestamp not null
);

-- ============================================================
-- Indexes for Query Performance
-- ============================================================

create index if not exists idx_pois_category on pois(category);
create index if not exists idx_pois_is_active on pois(is_active);
create index if not exists idx_pois_date on pois(date);
create index if not exists idx_routes_user_created_at on routes(user_id, created_at desc);
create index if not exists idx_routes_public_rating on routes(is_public, average_rating desc);
create index if not exists idx_user_interactions_user_timestamp on user_interactions(user_id, timestamp desc);
create index if not exists idx_recommendation_logs_user_created on recommendation_logs(user_id, created_at desc);
create index if not exists idx_bandit_events_user_created on bandit_events(user_id, created_at desc);
create index if not exists idx_user_feedback_user_created on user_feedback(user_id, created_at desc);

