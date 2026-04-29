# Phase 7 PostgreSQL and Indexing Strategy

## PostgreSQL Baseline Decisions

- PostgreSQL is the target system of record.
- H2 remains acceptable only for fast local development and basic tests.
- Flyway is the preferred migration tool for this project because the schema evolution path is SQL-heavy, reviewable, and easier to defend in a thesis appendix.
- PostGIS is not required on day one, but the schema is prepared for it by reserving a `location geometry(Point, 4326)` column on `pois`.

## Type Decisions

### Location data

- Current implementation: `latitude` and `longitude` as `Double`
- PostgreSQL target: keep `latitude` and `longitude` columns for compatibility, add `location geometry(Point, 4326)` when PostGIS is enabled

This allows an incremental path:

1. Preserve existing application logic.
2. Add geospatial capability without rewriting all repository code immediately.
3. Backfill `location` from the existing latitude/longitude pair.

### Numeric scores

- Use `numeric(4,3)` for bounded scores such as sustainability, crowd, popularity, sentiment.
- Use `numeric(10,6)` for bandit reward or recommendation score values where precision matters more.
- Use `numeric(10,2)` for money.

### JSON context

- `bandit_events.context_vector_json` should become `jsonb` in PostgreSQL.
- This preserves flexibility while enabling future indexed key access if context analysis grows.

## Required Indexes

### Users and profile

- `users(email)` unique index
- `users(home_city_id)` btree index after city dimension is introduced

### POI discovery

- `pois(city_id, category_code)` btree index
- `pois(city_id, is_active)` btree index
- `pois(district)` btree index
- `pois(event_date)` btree index
- `pois(budget_level)` btree index
- `pois(location)` gist index after PostGIS activation
- `poi_tag_map(tag_id, poi_id)` btree index
- `poi_metrics(popularity_score)` btree index
- `poi_metrics(crowd_proxy)` btree index

### Routes

- `routes(user_id, created_at desc)` btree index
- `routes(share_code)` unique index
- `routes(is_public, average_rating desc)` btree index
- `route_items(route_id, visit_order)` unique index
- `route_ratings(route_id)` btree index

### Recommendation and learning

- `recommendation_logs(user_id, created_at desc)` btree index
- `recommendation_logs(user_id, rank_position)` btree index
- `recommendation_logs(poi_id, created_at desc)` btree index
- `user_interactions(user_id, timestamp desc)` btree index
- `user_interactions(poi_id, interaction_type)` btree index
- `bandit_events(user_id, created_at desc)` btree index
- `bandit_events(poi_id, created_at desc)` btree index
- `bandit_arm_stats(user_id, poi_id)` unique index

### Feedback and analytics

- `user_feedback(user_id, created_at desc)` btree index
- `user_feedback(route_id, created_at desc)` btree index
- `weather_data(last_updated desc)` btree index
- `weather_data(city_id, timestamp desc)` btree index after city dimension is introduced

## Query Patterns Behind the Indexes

- recommendation candidate filtering by city, category, budget, active status
- recent user interaction lookup for learning and personalization
- route history and social route listing
- recommendation evaluation by impression and click/visit outcomes
- weather cache refresh and nearest relevant location lookup

## PostGIS Adoption Notes

Phase 7 recommendation:

- keep Haversine-compatible latitude/longitude logic working initially
- add PostGIS in the first PostgreSQL implementation wave
- introduce `ST_DWithin`, `ST_Distance`, and nearest-neighbor queries only after the baseline migration is stable

This avoids coupling the first PostgreSQL cutover to geospatial repository refactors.