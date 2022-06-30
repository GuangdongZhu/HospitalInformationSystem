package neu.his.hospInfo.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("neu.his.hospInfo.mapper")
public class HospitalInfoConfig {
    @Bean
    public PaginationInterceptor hospitalInfoPaginationInterceptor(){
        return new PaginationInterceptor();
    }
}
