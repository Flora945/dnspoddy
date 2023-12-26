package fun.doloresflora.dnspoddy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.InetAddresses;
import com.tencentcloudapi.dnspod.v20210323.DnspodClient;
import com.tencentcloudapi.dnspod.v20210323.models.DescribeRecordRequest;
import com.tencentcloudapi.dnspod.v20210323.models.DescribeRecordResponse;
import com.tencentcloudapi.dnspod.v20210323.models.ModifyDynamicDNSRequest;
import com.tencentcloudapi.dnspod.v20210323.models.RecordInfo;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.regex.Pattern;

/**
 * @author qihuaiyuan
 * @since 2023/12/24
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DomainResolveKeeper {

    /**
     * 5分钟
     */
    private static final long TASK_INTERVAL = 5 * 60 * 1000L;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final DnspodClient dnsPodClient;

    private final KeeperProperties properties;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    private String currentIpAddress;

    @PostConstruct
    public void initialize() {
        currentIpAddress = acquireCurrentRecord().getValue();
    }

    /**
     * 每5秒钟执行一次
     */
    @Scheduled(fixedRate = TASK_INTERVAL)
    public void scheduledTask() {
        maintain();
    }

    public void maintain() {
        String ipAddress = acquireIpAddress();
        if (!isInet6Address(ipAddress)) {
            log.warn("""
                Wrong IPv6 address acquired: {}
                Check JVM parameters or Network configurations
                """, ipAddress);
            System.exit(1);
            return;
        }
        log.info("acquired local host ipv6 address on the internet: {}", ipAddress);
        if (ipAddress.equals(currentIpAddress)) {
            log.info("current record is up to date, no need to update");
            return;
        }
        log.info("current record is out of date, need to update");
        updateRecord(ipAddress);
        currentIpAddress = ipAddress;
        log.info("update record successfully");
    }

    @SneakyThrows
    private void updateRecord(String newIpAddress) {
        if (properties.isDry()) {
            log.info("dry run, skip update record");
            return;
        }
        RecordInfo recordInfo = acquireCurrentRecord();
        // 实例化一个请求对象,每个接口都会对应一个request对象
        ModifyDynamicDNSRequest req = new ModifyDynamicDNSRequest();
        req.setDomain(properties.getDomainName());
        req.setRecordId(properties.getRecordId());
        req.setSubDomain(recordInfo.getSubDomain());
        req.setRecordLine(recordInfo.getRecordLine());
        req.setValue(newIpAddress);
        // 返回的resp是一个ModifyDynamicDNSResponse的实例，与请求对象对应
        dnsPodClient.ModifyDynamicDNS(req);
    }

    @SneakyThrows
    private RecordInfo acquireCurrentRecord() {
        log.info("查询当前记录");
        // 实例化一个请求对象,每个接口都会对应一个request对象
        DescribeRecordRequest req = new DescribeRecordRequest();
        req.setDomain(properties.getDomainName());
        req.setRecordId(properties.getRecordId());
        // 返回的resp是一个DescribeRecordResponse的实例，与请求对象对应
        DescribeRecordResponse resp = dnsPodClient.DescribeRecord(req);
        log.info("当前记录: {}", OBJECT_MAPPER.writeValueAsString(resp.getRecordInfo()));
        return resp.getRecordInfo();
    }

    @SneakyThrows
    private String acquireIpAddress() {
        // acquire local host ipv6 address on the internet
        HttpRequest req = HttpRequest.newBuilder().uri(URI.create("http://ip.sb")).GET()
            // must pretend to be "curl" to get ipv6 address
            .header(HttpHeaders.USER_AGENT, "curl/8.4.0").timeout(Duration.ofSeconds(5)).build();

        HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
        return resp.body().trim();
    }

    private static boolean isInet6Address(String address) {
        boolean isInetAddress = InetAddresses.isInetAddress(address);
        log.info("{} is inet address: {} ", address, isInetAddress);
        InetAddress inetAddress = InetAddresses.forString(address);
        log.info("parsed inet address: {} ", inetAddress);
        return isInetAddress &&  inetAddress instanceof Inet6Address;
    }



}
