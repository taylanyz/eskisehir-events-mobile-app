package com.eskisehir.eventapp.ui.screens.detail;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u00000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\u001a&\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00010\u0007H\u0003\u001a\u001e\u0010\b\u001a\u00020\u00012\u0006\u0010\t\u001a\u00020\n2\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00010\u0007H\u0007\u001a\u0018\u0010\f\u001a\u00020\u00012\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u0010H\u0003\u00a8\u0006\u0011"}, d2 = {"CommentItem", "", "comment", "Lcom/eskisehir/eventapp/data/local/entity/CommentEntity;", "isOwner", "", "onDelete", "Lkotlin/Function0;", "EventDetailScreen", "eventId", "", "onBackClick", "InfoRow", "icon", "Landroidx/compose/ui/graphics/vector/ImageVector;", "text", "", "app_debug"})
public final class EventDetailScreenKt {
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void EventDetailScreen(long eventId, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onBackClick) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void InfoRow(androidx.compose.ui.graphics.vector.ImageVector icon, java.lang.String text) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void CommentItem(com.eskisehir.eventapp.data.local.entity.CommentEntity comment, boolean isOwner, kotlin.jvm.functions.Function0<kotlin.Unit> onDelete) {
    }
}