package com.eskisehir.eventapi.dto;

/**
 * Request DTO for sharing a route.
 */
public class RouteShareRequest {
    private Boolean isPublic; // Make route public/private
    private String shareMessage; // Optional message when sharing

    public RouteShareRequest() {}

    public RouteShareRequest(Boolean isPublic, String shareMessage) {
        this.isPublic = isPublic;
        this.shareMessage = shareMessage;
    }

    public Boolean getIsPublic() { return isPublic; }
    public void setIsPublic(Boolean isPublic) { this.isPublic = isPublic; }

    public String getShareMessage() { return shareMessage; }
    public void setShareMessage(String shareMessage) { this.shareMessage = shareMessage; }
}
