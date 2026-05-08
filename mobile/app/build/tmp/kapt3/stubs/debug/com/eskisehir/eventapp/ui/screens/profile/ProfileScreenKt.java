package com.eskisehir.eventapp.ui.screens.profile;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000>\n\u0000\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0006\u001a.\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u00022\u0006\u0010\b\u001a\u00020\t2\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u000b0\u00012\u0006\u0010\f\u001a\u00020\u0002H\u0003\u001a\u0010\u0010\r\u001a\u00020\u00062\u0006\u0010\u000e\u001a\u00020\u000bH\u0003\u001a*\u0010\u000f\u001a\u00020\u00062\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00110\u00012\u0012\u0010\u0012\u001a\u000e\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020\u00060\u0013H\u0003\u001a>\u0010\u0015\u001a\u00020\u00062\f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00020\u00012\f\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00060\u00182\u0018\u0010\u0019\u001a\u0014\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00020\u0001\u0012\u0004\u0012\u00020\u00060\u0013H\u0001\u001aF\u0010\u001a\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u00022\u0006\u0010\b\u001a\u00020\t2\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u000b0\u00012\u0006\u0010\f\u001a\u00020\u00022\u0016\b\u0002\u0010\u0012\u001a\u0010\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020\u0006\u0018\u00010\u0013H\u0003\u001a(\u0010\u001b\u001a\u00020\u00062\u000e\b\u0002\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u00060\u00182\u000e\b\u0002\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u00060\u0018H\u0007\"\u0017\u0010\u0000\u001a\b\u0012\u0004\u0012\u00020\u00020\u0001\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0003\u0010\u0004\u00a8\u0006\u001e"}, d2 = {"ALL_INTERESTS", "", "", "getALL_INTERESTS", "()Ljava/util/List;", "EventCategorySection", "", "title", "icon", "Landroidx/compose/ui/graphics/vector/ImageVector;", "events", "Lcom/eskisehir/eventapp/data/model/Event;", "emptyMessage", "EventListItem", "event", "FavoritePlacesSection", "places", "Lcom/eskisehir/eventapp/data/local/entity/FavoritePlaceEntity;", "onRemove", "Lkotlin/Function1;", "", "InterestsDialog", "currentSelected", "onDismiss", "Lkotlin/Function0;", "onSave", "ProfileEventSection", "ProfileScreen", "onLogoutSuccess", "onEditProfileClick", "app_debug"})
public final class ProfileScreenKt {
    @org.jetbrains.annotations.NotNull()
    private static final java.util.List<java.lang.String> ALL_INTERESTS = null;
    
    @org.jetbrains.annotations.NotNull()
    public static final java.util.List<java.lang.String> getALL_INTERESTS() {
        return null;
    }
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class, androidx.compose.foundation.layout.ExperimentalLayoutApi.class})
    @androidx.compose.runtime.Composable()
    public static final void ProfileScreen(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onLogoutSuccess, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onEditProfileClick) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void ProfileEventSection(java.lang.String title, androidx.compose.ui.graphics.vector.ImageVector icon, java.util.List<com.eskisehir.eventapp.data.model.Event> events, java.lang.String emptyMessage, kotlin.jvm.functions.Function1<? super java.lang.Long, kotlin.Unit> onRemove) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void FavoritePlacesSection(java.util.List<com.eskisehir.eventapp.data.local.entity.FavoritePlaceEntity> places, kotlin.jvm.functions.Function1<? super java.lang.Long, kotlin.Unit> onRemove) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void EventCategorySection(java.lang.String title, androidx.compose.ui.graphics.vector.ImageVector icon, java.util.List<com.eskisehir.eventapp.data.model.Event> events, java.lang.String emptyMessage) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void EventListItem(com.eskisehir.eventapp.data.model.Event event) {
    }
    
    @kotlin.OptIn(markerClass = {androidx.compose.foundation.layout.ExperimentalLayoutApi.class})
    @androidx.compose.runtime.Composable()
    public static final void InterestsDialog(@org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> currentSelected, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDismiss, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.util.List<java.lang.String>, kotlin.Unit> onSave) {
    }
}