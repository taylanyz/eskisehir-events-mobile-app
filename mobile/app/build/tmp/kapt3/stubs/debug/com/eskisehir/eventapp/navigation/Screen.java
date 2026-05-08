package com.eskisehir.eventapp.navigation;

/**
 * Sealed class defining all navigation routes in the app.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u000f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b6\u0018\u00002\u00020\u0001:\f\u0007\b\t\n\u000b\f\r\u000e\u000f\u0010\u0011\u0012B\u000f\b\u0004\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006\u0082\u0001\f\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001a\u001b\u001c\u001d\u001e\u00a8\u0006\u001f"}, d2 = {"Lcom/eskisehir/eventapp/navigation/Screen;", "", "route", "", "(Ljava/lang/String;)V", "getRoute", "()Ljava/lang/String;", "EventDetail", "Explore", "Favorites", "Home", "Login", "Navigation", "Preferences", "Profile", "Recommendations", "Register", "RouteDetail", "RouteGenerator", "Lcom/eskisehir/eventapp/navigation/Screen$EventDetail;", "Lcom/eskisehir/eventapp/navigation/Screen$Explore;", "Lcom/eskisehir/eventapp/navigation/Screen$Favorites;", "Lcom/eskisehir/eventapp/navigation/Screen$Home;", "Lcom/eskisehir/eventapp/navigation/Screen$Login;", "Lcom/eskisehir/eventapp/navigation/Screen$Navigation;", "Lcom/eskisehir/eventapp/navigation/Screen$Preferences;", "Lcom/eskisehir/eventapp/navigation/Screen$Profile;", "Lcom/eskisehir/eventapp/navigation/Screen$Recommendations;", "Lcom/eskisehir/eventapp/navigation/Screen$Register;", "Lcom/eskisehir/eventapp/navigation/Screen$RouteDetail;", "Lcom/eskisehir/eventapp/navigation/Screen$RouteGenerator;", "app_debug"})
public abstract class Screen {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String route = null;
    
    private Screen(java.lang.String route) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getRoute() {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0000\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006\u00a8\u0006\u0007"}, d2 = {"Lcom/eskisehir/eventapp/navigation/Screen$EventDetail;", "Lcom/eskisehir/eventapp/navigation/Screen;", "()V", "createRoute", "", "eventId", "", "app_debug"})
    public static final class EventDetail extends com.eskisehir.eventapp.navigation.Screen {
        @org.jetbrains.annotations.NotNull()
        public static final com.eskisehir.eventapp.navigation.Screen.EventDetail INSTANCE = null;
        
        private EventDetail() {
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String createRoute(long eventId) {
            return null;
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/eskisehir/eventapp/navigation/Screen$Explore;", "Lcom/eskisehir/eventapp/navigation/Screen;", "()V", "app_debug"})
    public static final class Explore extends com.eskisehir.eventapp.navigation.Screen {
        @org.jetbrains.annotations.NotNull()
        public static final com.eskisehir.eventapp.navigation.Screen.Explore INSTANCE = null;
        
        private Explore() {
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/eskisehir/eventapp/navigation/Screen$Favorites;", "Lcom/eskisehir/eventapp/navigation/Screen;", "()V", "app_debug"})
    public static final class Favorites extends com.eskisehir.eventapp.navigation.Screen {
        @org.jetbrains.annotations.NotNull()
        public static final com.eskisehir.eventapp.navigation.Screen.Favorites INSTANCE = null;
        
        private Favorites() {
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/eskisehir/eventapp/navigation/Screen$Home;", "Lcom/eskisehir/eventapp/navigation/Screen;", "()V", "app_debug"})
    public static final class Home extends com.eskisehir.eventapp.navigation.Screen {
        @org.jetbrains.annotations.NotNull()
        public static final com.eskisehir.eventapp.navigation.Screen.Home INSTANCE = null;
        
        private Home() {
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/eskisehir/eventapp/navigation/Screen$Login;", "Lcom/eskisehir/eventapp/navigation/Screen;", "()V", "app_debug"})
    public static final class Login extends com.eskisehir.eventapp.navigation.Screen {
        @org.jetbrains.annotations.NotNull()
        public static final com.eskisehir.eventapp.navigation.Screen.Login INSTANCE = null;
        
        private Login() {
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\u0010\t\n\u0000\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0014\u0010\u0003\u001a\u00020\u00042\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006\u00a8\u0006\b"}, d2 = {"Lcom/eskisehir/eventapp/navigation/Screen$Navigation;", "Lcom/eskisehir/eventapp/navigation/Screen;", "()V", "createRoute", "", "eventIds", "", "", "app_debug"})
    public static final class Navigation extends com.eskisehir.eventapp.navigation.Screen {
        @org.jetbrains.annotations.NotNull()
        public static final com.eskisehir.eventapp.navigation.Screen.Navigation INSTANCE = null;
        
        private Navigation() {
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String createRoute(@org.jetbrains.annotations.NotNull()
        java.util.List<java.lang.Long> eventIds) {
            return null;
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/eskisehir/eventapp/navigation/Screen$Preferences;", "Lcom/eskisehir/eventapp/navigation/Screen;", "()V", "app_debug"})
    public static final class Preferences extends com.eskisehir.eventapp.navigation.Screen {
        @org.jetbrains.annotations.NotNull()
        public static final com.eskisehir.eventapp.navigation.Screen.Preferences INSTANCE = null;
        
        private Preferences() {
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/eskisehir/eventapp/navigation/Screen$Profile;", "Lcom/eskisehir/eventapp/navigation/Screen;", "()V", "app_debug"})
    public static final class Profile extends com.eskisehir.eventapp.navigation.Screen {
        @org.jetbrains.annotations.NotNull()
        public static final com.eskisehir.eventapp.navigation.Screen.Profile INSTANCE = null;
        
        private Profile() {
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/eskisehir/eventapp/navigation/Screen$Recommendations;", "Lcom/eskisehir/eventapp/navigation/Screen;", "()V", "app_debug"})
    public static final class Recommendations extends com.eskisehir.eventapp.navigation.Screen {
        @org.jetbrains.annotations.NotNull()
        public static final com.eskisehir.eventapp.navigation.Screen.Recommendations INSTANCE = null;
        
        private Recommendations() {
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/eskisehir/eventapp/navigation/Screen$Register;", "Lcom/eskisehir/eventapp/navigation/Screen;", "()V", "app_debug"})
    public static final class Register extends com.eskisehir.eventapp.navigation.Screen {
        @org.jetbrains.annotations.NotNull()
        public static final com.eskisehir.eventapp.navigation.Screen.Register INSTANCE = null;
        
        private Register() {
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/eskisehir/eventapp/navigation/Screen$RouteDetail;", "Lcom/eskisehir/eventapp/navigation/Screen;", "()V", "app_debug"})
    public static final class RouteDetail extends com.eskisehir.eventapp.navigation.Screen {
        @org.jetbrains.annotations.NotNull()
        public static final com.eskisehir.eventapp.navigation.Screen.RouteDetail INSTANCE = null;
        
        private RouteDetail() {
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\u0010\t\n\u0000\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0014\u0010\u0003\u001a\u00020\u00042\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006\u00a8\u0006\b"}, d2 = {"Lcom/eskisehir/eventapp/navigation/Screen$RouteGenerator;", "Lcom/eskisehir/eventapp/navigation/Screen;", "()V", "createRoute", "", "eventIds", "", "", "app_debug"})
    public static final class RouteGenerator extends com.eskisehir.eventapp.navigation.Screen {
        @org.jetbrains.annotations.NotNull()
        public static final com.eskisehir.eventapp.navigation.Screen.RouteGenerator INSTANCE = null;
        
        private RouteGenerator() {
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String createRoute(@org.jetbrains.annotations.NotNull()
        java.util.List<java.lang.Long> eventIds) {
            return null;
        }
    }
}