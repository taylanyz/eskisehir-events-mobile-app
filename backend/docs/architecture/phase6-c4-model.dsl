workspace "Eskisehir Events Mobile App" "Phase 6 C4 architecture for the thesis-oriented intelligent tourism system" {

    model {
        user = person "Tourist / End User" "Uses the Turkish mobile application to discover places and generate personalized routes in Eskisehir."

        system = softwareSystem "Eskisehir Intelligent Tourism System" "Provides personalized recommendations, route planning, feedback collection and contextual tourism support." {
            mobile = container "Mobile App" "Kotlin, Jetpack Compose" "Turkish mobile client for authentication, discovery, recommendations, route viewing and feedback."
            backend = container "Backend API" "Java 21, Spring Boot" "REST API for auth, POI discovery, recommendations, routes, interactions, analytics and contextual enrichment."
            db = container "Primary Database" "PostgreSQL" "Stores users, preferences, POIs, routes, recommendation logs, feedback and learning events."
            cache = container "Context Cache" "Redis" "Caches weather and short-lived contextual or query results."

            authComponent = component "Auth and Security" "Spring Security, JWT" "Handles registration, login, token validation and access control."
            profileComponent = component "User Profile Services" "Spring Services" "Manages users and preferences."
            poiComponent = component "POI Discovery Services" "Spring Services" "Handles POI search, listing and filtering."
            recommendationComponent = component "Recommendation Engine" "Service + Algorithm" "Builds candidate POI pools and ranks them." 
            learningComponent = component "Interaction and Bandit Learning" "Service + Repositories" "Logs interactions and updates contextual learning data."
            routeComponent = component "Route Planning and Navigation" "Service + Optimizer" "Builds routes from candidate POIs and route constraints."
            contextComponent = component "Weather and Context Enrichment" "Service + Cache" "Adds weather and contextual signals to responses."
            analyticsComponent = component "Analytics and Evaluation" "Service" "Aggregates operational metrics and evaluation signals."
        }

        weatherApi = softwareSystem "Weather Provider" "External weather data provider."
        mapApi = softwareSystem "Map and Routing Provider" "External map, routing or geospatial service provider."

        user -> mobile "Uses"
        mobile -> backend "Calls REST APIs"
        backend -> db "Reads from and writes to"
        backend -> cache "Reads from and writes to"
        backend -> weatherApi "Fetches weather context from"
        backend -> mapApi "Fetches routing and distance data from"

        mobile -> authComponent "Uses through API"
        mobile -> poiComponent "Uses through API"
        mobile -> recommendationComponent "Requests ranked POIs from"
        mobile -> routeComponent "Requests route generation from"
        mobile -> learningComponent "Sends interaction and feedback events to"

        recommendationComponent -> profileComponent "Reads user preferences from"
        recommendationComponent -> poiComponent "Reads candidate POIs from"
        recommendationComponent -> learningComponent "Uses learning statistics from"
        routeComponent -> recommendationComponent "Consumes candidate POIs from"
        routeComponent -> contextComponent "Uses context signals from"
        learningComponent -> analyticsComponent "Exposes evaluation and reward signals to"
        contextComponent -> weatherApi "Queries"
        routeComponent -> mapApi "Queries"
    }

    views {
        systemContext system "SystemContext" {
            include *
            autolayout lr
        }

        container system "Containers" {
            include user
            include mobile
            include backend
            include db
            include cache
            include weatherApi
            include mapApi
            autolayout lr
        }

        component backend "BackendComponents" {
            include authComponent
            include profileComponent
            include poiComponent
            include recommendationComponent
            include learningComponent
            include routeComponent
            include contextComponent
            include analyticsComponent
            autolayout lr
        }

        styles {
            element "Person" {
                background "#0b7285"
                color "#ffffff"
                shape person
            }
            element "Software System" {
                background "#1971c2"
                color "#ffffff"
            }
            element "Container" {
                background "#2f9e44"
                color "#ffffff"
            }
            element "Component" {
                background "#f08c00"
                color "#ffffff"
            }
        }
    }
}