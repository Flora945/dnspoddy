package fun.doloresflora.dnspoddy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author qihuaiyuan
 */
@EnableScheduling
@SpringBootApplication
public class DnspoddyApplication {

    public static void main(String[] args) {
        SpringApplication.run(DnspoddyApplication.class, args);
    }

}
