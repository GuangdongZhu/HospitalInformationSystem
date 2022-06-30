package neu.his.hosp.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import neu.his.common.result.R;
import neu.his.hosp.service.HospitalService;
import neu.his.hosp.service.HospitalSetService;

import neu.his.model.hosp.Hospital;
import neu.his.vo.hosp.HospitalQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api
@RestController

@RequestMapping("/admin/hosp/hospital")
public class HospttialController {
    @Autowired
    HospitalService hospitalService;

    //医院列表(条件查询分页)
    @GetMapping("list/{page}/{limit}")
    public R listHosp(@PathVariable Integer page,
                           @PathVariable Integer limit,
                           HospitalQueryVo hospitalQueryVo){  //将查询的条件参数封装到了HospitalSetQueryVo中
        //此时医院的数据存在于mongodb中
        Page<Hospital> pageModel = hospitalService.selectHospPage(page,limit,hospitalQueryVo);
        List<Hospital> content = pageModel.getContent();
        long totalElements = pageModel.getTotalElements();
        return R.ok(pageModel);
    }

    // 更新医院的上线状态
    @ApiOperation("更新医院上线状态")
    @GetMapping("updateHospStatus/{id}/{status}")
    public R updateHospStatus(@PathVariable String id, @PathVariable Integer status){
        hospitalService.updateStatus(id,status);
        return R.ok();
    }

    // 显示医院的详情信息
    @ApiOperation("显示医院的详情信息")
    @GetMapping("showHospDetail/{id}")
    public R showHospDetail(@PathVariable String id){
        System.out.println(id);
        Map<String, Object> map = hospitalService.getHospById(id);
        System.out.println(map.get("hosname"));
        return R.ok(map);
    }
}
