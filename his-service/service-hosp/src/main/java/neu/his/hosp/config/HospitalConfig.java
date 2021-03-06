package neu.his.hosp.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("neu.his.hosp.mapper")
public class HospitalConfig {
    @Bean
    public PaginationInterceptor hospitalPaginationInterceptor(){
        return new PaginationInterceptor();
    }
}
