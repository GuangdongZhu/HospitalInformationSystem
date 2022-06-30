package neu.his.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiOperation;
import neu.his.common.exception.DefinedException;
import neu.his.common.result.ResultCodeEnum;
import neu.his.hosp.feign.HospitalInfoFeign;
import neu.his.hosp.repository.HospitalRepository;
import neu.his.hosp.service.HospitalService;
import neu.his.model.hosp.Hospital;
import neu.his.vo.hosp.HospitalQueryVo;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;


import javax.naming.ldap.PagedResultsControl;
import java.util.*;

@Service
class HospitalSevriceImpl implements HospitalService {
    @Autowired
    HospitalRepository hospitalRepository;

    @Autowired
    HospitalInfoFeign hospitalInfoFeign;

    @Override
    public void save(Map<String, Object> parapMap) {
        //把参数的map集合转换成对象Hospital，方便操作
        String mapString = JSONObject.toJSONString(parapMap); //先把map转换成字符串
        Hospital hospital = JSONObject.parseObject(mapString, Hospital.class); // 把字符串转换成Hospital对象
        //判断是否存在相同数据
        String hoscode = hospital.getHoscode();
        Hospital hospitalExist = hospitalRepository.getHospitalByHoscode(hoscode);
        //如果存在，进行修改
        if (hospitalExist != null) {
            hospital.setStatus(hospitalExist.getStatus());
            hospital.setCreateTime(hospitalExist.getCreateTime());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        } else {
            //如果不存在，进行添加
            hospital.setStatus(0);
            hospital.setCreateTime(new Date());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        }
    }

    @Override
    public Hospital getByHoscode(String hoscode) {
        Hospital hospitalByHoscode = hospitalRepository.getHospitalByHoscode(hoscode);
        return hospitalByHoscode;
    }

    @Override
    public Page<Hospital> selectHospPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        ExampleMatcher matcher = ExampleMatcher.matching().withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING).withIgnoreCase(true);
        //hospitalSetQueryVo转换成hospital对象
        Hospital hospital = new Hospital();
        BeanUtils.copyProperties(hospitalQueryVo, hospital);
        //创建Example对象
        Example<Hospital> example = Example.of(hospital, matcher);
        //调用方法实现查询
        Page<Hospital> pages = hospitalRepository.findAll(example, pageable);

        //查询到所有医院集合并遍历，然后获取到医院等级信息
        //采用流的方式
        pages.getContent().stream().forEach(item -> {
            this.setHospitalHosType(item);
        });
        return pages;
    }

    //获取查询到的医院集合，遍历进行医院等级封装
    private Hospital setHospitalHosType(Hospital hospital) {
        //根据dictCode和value获取医院等级名称
        String hostypeString = hospitalInfoFeign.getName("Hostype", hospital.getHostype());
        //查询省，市，地区
        String provinceString = hospitalInfoFeign.getName(hospital.getProvinceCode());
        String cityString = hospitalInfoFeign.getName(hospital.getCityCode());
        String districtString = hospitalInfoFeign.getName(hospital.getDistrictCode());
        hospital.getParam().put("hostypeString", hostypeString);
        hospital.getParam().put("fullAddress", provinceString + cityString + districtString);
        return hospital;
    }

    @Override
    public void updateStatus(String id, Integer status) {
        // 根据id查询医院的信息
        Hospital hospital = hospitalRepository.findById(id).get();

        // 设置修改的值
        hospital.setStatus(status);
        hospital.setUpdateTime(new Date());
        hospitalRepository.save(hospital);

    }

    @Override
    public Map<String, Object> getHospById(String id) {
        Map<String,Object> map = new HashMap<>();

        Hospital hospital = this.setHospitalHosType(hospitalRepository.findById(id).get());
        map.put("hospital",hospital);
        map.put("bookingRule",hospital.getBookingRule());
        //
        hospital.setBookingRule(null);

        return map;
    }

    // 根据医院id获得医院名称
    @Override
    public String getHospName(String hoscode) {
        Hospital hospital = hospitalRepository.getHospitalByHoscode(hoscode);
        if (hospital != null)
            return hospital.getHosname();
        return null;
    }

    @Override
    public List<Hospital> findByHosname(String hosname) {
        List<Hospital> list = hospitalRepository.findHospitalByHosnameLike(hosname);
        return list;
    }

    @Override
    public Map<String, Object> item(String hoscode) {
        return null;
    }

}
