package gr.hua.dit.notification_service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Διαβάζει ρυθμίσεις από application.yml με prefix "routee".
 * Π.χ. routee.appId, routee.appSecret, routee.sender
 */

@Configuration
@ConfigurationProperties(prefix = "routee")
public class RouteeProperties {

    private String appId;
    private String appSecret;
    private String sender;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
