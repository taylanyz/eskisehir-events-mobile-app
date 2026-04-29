# Phase 7 Entity-to-Table Mapping Notes

## Purpose

This document maps the current JPA entities to the target PostgreSQL schema and calls out deliberate gaps between the current implementation and the target relational design.

## Direct Mappings

| Current entity | Current table | Target PostgreSQL table | Mapping status | Notes |
| --- | --- | --- | --- | --- |
| `User` | `users` | `users` | Direct | Add `home_city_id` in implementation phase. |
| `UserPreference` | `user_preferences` | `user_preferences` | Direct | Shared PK with `users` remains valid. |
| `Poi` | `pois` | `pois` | Partial | Move dynamic score fields into `poi_metrics`; add `city_id`; prepare PostGIS `location`. |
| `Route` | `routes` | `routes` | Direct | Existing social fields fit target shape. |
| `RouteItem` | `route_items` | `route_items` | Direct | Add unique constraint on `(route_id, visit_order)`. |
| `RouteRating` | `route_ratings` | `route_ratings` | Direct | Existing uniqueness matches target. |
| `UserInteraction` | `user_interactions` | `user_interactions` | Direct | Keep separate from recommendation logs. |
| `RecommendationLog` | `recommendation_logs` | `recommendation_logs` | Direct | Rename `rank` to `rank_position` at DB level if desired. |
| `UserFeedback` | `user_feedback` | `user_feedback` | Direct | Already supports route-linked feedback and future sentiment output. |
| `BanditEvent` | `bandit_events` | `bandit_events` | Partial | Store `contextVectorJson` as `jsonb` in PostgreSQL. |
| `BanditArmStat` | `bandit_arm_stats` | `bandit_arm_stats` | Direct | Unique `(user_id, poi_id)` is already correct. |
| `WeatherData` | `weather_data` | `weather_data` | Partial | Add optional `city_id` and expiration/index strategy. |

## Collection and Join Table Mappings

| Current JPA structure | Current physical shape | Target PostgreSQL shape | Decision |
| --- | --- | --- | --- |
| `UserPreference.preferredCategories` | `user_preferred_categories` with string enum values | `user_preferred_categories` | Keep as join table; defer category dimension FK until enum stabilization. |
| `UserPreference.preferredTags` | `user_preferred_tags` | `user_preferred_tags` | Keep simple table because user-owned tag preference values are lightweight. |
| `Poi.tags` | `poi_tags` element collection table | `poi_tags` + `poi_tag_map` | Normalize in PostgreSQL target for deduplication and better analytics. |

## Deliberate Divergences

### 1. `poi_categories`

The current code represents categories as a Java enum. That is acceptable for MVP stability, but the target schema defines `poi_categories` as a reference table because:

- academic documentation benefits from explicit categorical dimensions
- admin tooling becomes easier later
- cross-city onboarding becomes less brittle

Phase 7 decision: keep enum-based code for now, document the future relational target.

### 2. `poi_metrics`

The current `Poi` entity stores these fields inline:

- `sustainabilityScore`
- `localBusinessScore`
- `crowdProxy`
- `popularityScore`

Phase 7 decision: treat them as a separate target table in PostgreSQL design. This keeps mutable ranking signals separate from descriptive location metadata and better supports periodic recomputation.

### 3. City dimension

The current schema is effectively Eskişehir-bound by convention. Phase 7 introduces an explicit `cities` dimension in the target schema, but does not force runtime entity changes yet.

### 4. Recommendation versus interaction logging

These two tables must remain separate:

- `recommendation_logs` records exposure by the system
- `user_interactions` records observed user behavior

Collapsing them would make offline evaluation less defensible because impression data and outcome data would be mixed.

## Implementation Priority After Phase 7

1. Add Flyway and create the baseline PostgreSQL migration.
2. Introduce `cities` and seed Eskişehir as the first record.
3. Move POI metric fields to `poi_metrics` without changing API contracts.
4. Normalize POI tags into `poi_tags` and `poi_tag_map`.
5. Convert `bandit_events.context_vector_json` from string semantics to PostgreSQL `jsonb` semantics.