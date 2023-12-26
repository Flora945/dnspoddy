package fun.doloresflora.dnspoddy;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * @author qihuaiyuan
 * @since 2023/12/24
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Component
@Validated
@ConfigurationProperties("config.keeper")
public class KeeperProperties {

    @NotBlank
    private String domainName;

    @NotNull
    private Long recordId;

    private boolean dry = false;

}
