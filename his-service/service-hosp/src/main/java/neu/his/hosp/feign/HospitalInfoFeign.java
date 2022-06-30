package neu.his.hosp.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("service-hospInfo")
public interface HospitalInfoFeign {
    @GetMapping("/admin/hospInfo/dict/getName/{dictCode}/{value}")
    public String getName(@PathVariable("dictCode") String dictCode, @PathVariable("value") String value);

    @GetMapping("/admin/hospInfo/dict/getName/{value}")
    public String getName(@PathVariable("value") String value);
}
