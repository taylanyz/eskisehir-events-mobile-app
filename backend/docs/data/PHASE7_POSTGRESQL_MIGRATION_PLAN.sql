-- Phase 7 design artifact
-- This file is a reviewable PostgreSQL baseline draft, not an active Flyway migration yet.

create table if not exists cities (
    id bigserial primary key,
    code varchar(64) not null unique,
    name varchar(128) not null,
    country_code varchar(8) not null,
    timezone varchar(64) not null,
    is_active boolean not null default true
);

create table if not exists users (
    id bigserial primary key,
    email varchar(255) not null unique,
    password_hash varchar(255) not null,
    display_name varchar(255) not null,
    home_city_id bigint references cities(id),
    created_at timestamp not null,
    last_login_at timestamp
);

create table if not exists user_preferences (
    user_id bigint primary key references users(id) on delete cascade,
    budget_sensitivity varchar(32),
    crowd_tolerance varchar(32),
    mobility_preference varchar(32),
    sustainability_preference numeric(4,3),
    max_walking_minutes integer
);

create table if not exists user_preferred_categories (
    user_id bigint not null references user_preferences(user_id) on delete cascade,
    category_code varchar(64) not null,
    primary key (user_id, category_code)
);

create table if not exists user_preferred_tags (
    user_id bigint not null references user_preferences(user_id) on delete cascade,
    tag varchar(128) not null,
    primary key (user_id, tag)
);

create table if not exists poi_categories (
    code varchar(64) primary key,
    display_name varchar(128) not null,
    is_event_category boolean not null default false
);

create table if not exists pois (
    id bigserial primary key,
    city_id bigint not null references cities(id),
    category_code varchar(64) not null references poi_categories(code),
    name varchar(255) not null,
    description text,
    district varchar(128),
    latitude numeric(9,6) not null,
    longitude numeric(9,6) not null,
    venue varchar(255) not null,
    event_date timestamp,
    price_try numeric(10,2),
    budget_level varchar(32) not null,
    image_url varchar(1024),
    estimated_visit_minutes integer,
    indoor_outdoor varchar(32),
    family_friendly boolean,
    opening_time time,
    closing_time time,
    is_active boolean not null default true,
    created_at timestamp not null,
    updated_at timestamp not null
);

create table if not exists poi_tags (
    id bigserial primary key,
    tag varchar(128) not null unique
);

create table if not exists poi_tag_map (
    poi_id bigint not null references pois(id) on delete cascade,
    tag_id bigint not null references poi_tags(id) on delete cascade,
    primary key (poi_id, tag_id)
);

create table if not exists poi_metrics (
    poi_id bigint primary key references pois(id) on delete cascade,
    sustainability_score numeric(4,3) not null default 0.500,
    local_business_score numeric(4,3) not null default 0.500,
    crowd_proxy numeric(4,3) not null default 0.500,
    popularity_score numeric(4,3) not null default 0.500,
    updated_at timestamp not null
);

create table if not exists routes (
    id bigserial primary key,
    user_id bigint not null references users(id),
    name varchar(255),
    total_distance_km numeric(10,3),
    total_duration_minutes integer,
    estimated_budget numeric(10,2),
    carbon_score numeric(10,3),
    transport_mode varchar(32),
    status varchar(32) not null,
    is_public boolean not null default false,
    share_code varchar(64) unique,
    average_rating numeric(3,2) not null default 0.00,
    total_ratings integer not null default 0,
    share_count integer not null default 0,
    created_at timestamp not null
);

create table if not exists route_items (
    id bigserial primary key,
    route_id bigint not null references routes(id) on delete cascade,
    poi_id bigint not null references pois(id),
    visit_order integer not null,
    estimated_arrival time,
    estimated_departure time,
    distance_from_previous_km numeric(10,3),
    unique (route_id, visit_order)
);

create table if not exists route_ratings (
    id bigserial primary key,
    route_id bigint not null references routes(id) on delete cascade,
    user_id bigint not null references users(id),
    rating numeric(2,1) not null,
    comment varchar(500),
    created_at timestamp not null,
    updated_at timestamp not null,
    unique (route_id, user_id)
);

create table if not exists user_interactions (
    id bigserial primary key,
    user_id bigint not null references users(id),
    poi_id bigint not null references pois(id),
    interaction_type varchar(32) not null,
    timestamp timestamp not null,
    context_weather varchar(64),
    context_time_of_day varchar(32),
    context_day_of_week varchar(16)
);

create table if not exists recommendation_logs (
    id bigserial primary key,
    user_id bigint not null references users(id),
    poi_id bigint not null references pois(id),
    score numeric(10,6),
    rank_position integer,
    was_clicked boolean not null default false,
    was_visited boolean not null default false,
    algorithm_version varchar(64),
    created_at timestamp not null
);

create table if not exists user_feedback (
    id bigserial primary key,
    user_id bigint not null references users(id),
    route_id bigint references routes(id),
    rating integer not null,
    comment_text text,
    sentiment_score numeric(4,3),
    created_at timestamp not null
);

create table if not exists bandit_events (
    id bigserial primary key,
    user_id bigint not null references users(id),
    poi_id bigint not null references pois(id),
    context_vector_json jsonb,
    reward numeric(10,6),
    created_at timestamp not null
);

create table if not exists bandit_arm_stats (
    id bigserial primary key,
    user_id bigint not null references users(id),
    poi_id bigint not null references pois(id),
    alpha numeric(10,6) not null default 1.0,
    beta numeric(10,6) not null default 1.0,
    plays bigint not null default 0,
    wins bigint not null default 0,
    updated_at timestamp not null,
    unique (user_id, poi_id)
);

create table if not exists weather_data (
    id bigserial primary key,
    city_id bigint references cities(id),
    latitude numeric(9,6) not null,
    longitude numeric(9,6) not null,
    condition varchar(64) not null,
    temperature integer not null,
    humidity integer not null,
    wind_speed numeric(8,3) not null,
    is_raining boolean not null,
    timestamp timestamp not null,
    last_updated timestamp not null
);

create index if not exists idx_users_home_city_id on users(home_city_id);
create index if not exists idx_pois_city_category on pois(city_id, category_code);
create index if not exists idx_pois_city_active on pois(city_id, is_active);
create index if not exists idx_pois_event_date on pois(event_date);
create index if not exists idx_routes_user_created_at on routes(user_id, created_at desc);
create index if not exists idx_routes_public_rating on routes(is_public, average_rating desc);
create index if not exists idx_recommendation_logs_user_created_at on recommendation_logs(user_id, created_at desc);
create index if not exists idx_user_interactions_user_timestamp on user_interactions(user_id, timestamp desc);
create index if not exists idx_bandit_events_user_created_at on bandit_events(user_id, created_at desc);
create index if not exists idx_user_feedback_user_created_at on user_feedback(user_id, created_at desc);
create index if not exists idx_weather_data_last_updated on weather_data(last_updated desc);

insert into cities (code, name, country_code, timezone)
values ('eskisehir', 'Eskisehir', 'TR', 'Europe/Istanbul')
on conflict (code) do nothing;

insert into poi_categories (code, display_name, is_event_category)
values
    ('MUSEUM', 'Museum', false),
    ('CAFE', 'Cafe', false),
    ('PARK', 'Park', false),
    ('HISTORICAL_SITE', 'Historical Site', false),
    ('CONCERT', 'Concert', true),
    ('FESTIVAL', 'Festival', true)
on conflict (code) do nothing;