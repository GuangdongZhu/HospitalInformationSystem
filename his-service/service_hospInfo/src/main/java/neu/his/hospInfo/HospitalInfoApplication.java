package neu.his.hospInfo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan("neu.his")
public class HospitalInfoApplication {
    public static void main(String[] args) {
        SpringApplication.run(HospitalInfoApplication.class,args);
    }
}
