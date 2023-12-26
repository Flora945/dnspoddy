package fun.doloresflora.dnspoddy;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.dnspod.v20210323.DnspodClient;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

/**
 * @author qihuaiyuan
 * @since 2023/12/24
 */
@Configuration
public class DnsPodConfig {

    @Bean
    @Validated
    @ConfigurationProperties("config.dnspod")
    public DnsPodProperties dnsPodProperties() {
        return new DnsPodProperties();
    }

    @Bean
    public DnspodClient dnspodClient(DnsPodProperties properties) {
        Credential cred = new Credential(properties.getKeyId(), properties.getKeySecret());
        // 实例化一个http选项，可选的，没有特殊需求可以跳过
        HttpProfile httpProfile = new HttpProfile();
        httpProfile.setEndpoint("dnspod.tencentcloudapi.com");
        // 实例化一个client选项，可选的，没有特殊需求可以跳过
        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setHttpProfile(httpProfile);
        // 实例化要请求产品的client对象,clientProfile是可选的
        return new DnspodClient(cred, "", clientProfile);
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DnsPodProperties {

        @NotBlank
        private String keyId;

        @NotBlank
        private String keySecret;

    }

}
