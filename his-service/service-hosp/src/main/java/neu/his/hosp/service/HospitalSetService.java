package neu.his.hosp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import neu.his.model.hosp.HospitalSet;

public interface HospitalSetService extends IService<HospitalSet> {
    String getSignKey(String hoscode);
}
