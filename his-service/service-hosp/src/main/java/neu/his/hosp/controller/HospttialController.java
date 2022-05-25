package neu.his.hosp.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import neu.his.common.result.R;
import neu.his.hosp.service.HospitalService;
import neu.his.hosp.service.HospitalSetService;

import neu.his.model.hosp.Hospital;
import neu.his.vo.hosp.HospitalQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api
@RestController
@CrossOrigin
@RequestMapping("/admin/hospital")
public class HospttialController {
//    @Autowired
//    HospitalService hospitalService;
//
//    //医院列表(条件查询分页)
//    @GetMapping("list/{page}/{limit}")
//    public R listHosp(@PathVariable Integer page,
//                           @PathVariable Integer limit,
//                           HospitalQueryVo hospitalQueryVo){  //将查询的条件参数封装到了HospitalSetQueryVo中
//        //此时医院的数据存在于mongodb中
//        Page<Hospital> pageModel = hospitalService.selectHospPage(page,limit,hospitalQueryVo);
//        List<Hospital> content = pageModel.getContent();
//        long totalElements = pageModel.getTotalElements();
//        return R.ok(pageModel);
//    }
}
