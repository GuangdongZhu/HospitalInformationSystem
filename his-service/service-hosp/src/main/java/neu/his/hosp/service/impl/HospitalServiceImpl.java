package neu.his.hosp.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import neu.his.hosp.service.HospitalService;
import neu.his.model.hosp.Hospital;
import neu.his.vo.hosp.HospitalQueryVo;

import java.util.List;
import java.util.Map;

public class HospitalServiceImpl implements HospitalService {
    @Override
    public void save(Map<String, Object> parapMap) {

    }

    @Override
    public Hospital getByHoscode(String hoscode) {
        return null;
    }

    @Override
    public Page<Hospital> selectHospPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo) {
        return null;
    }

    @Override
    public void updateStatus(String id, Integer status) {

    }

    @Override
    public Map<String, Object> getHospById(String id) {
        return null;
    }

    @Override
    public String getHospName(String hoscode) {
        return null;
    }

    @Override
    public List<Hospital> findByHosname(String hosname) {
        return null;
    }

    @Override
    public Map<String, Object> item(String hoscode) {
        return null;
    }
}
