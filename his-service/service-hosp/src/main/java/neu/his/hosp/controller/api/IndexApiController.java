package neu.his.hosp.controller.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import neu.his.common.result.R;
import neu.his.hosp.service.HospitalService;
import neu.his.model.hosp.Hospital;
import neu.his.vo.hosp.HospitalQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.*;

@RestController
@Api
@RequestMapping("/api/hosp/hospital")
public class IndexApiController {

    @Autowired
    HospitalService hospitalService;

    @ApiOperation("查询医院列表")
    @GetMapping("findHospList/{page}/{limit}")
    public R findHospList(@PathVariable Integer page, @PathVariable Integer limit, HospitalQueryVo hospitalQueryVo)
    {
        Page<Hospital> data = hospitalService.selectHospPage(page, limit, hospitalQueryVo);
        return R.ok(data);
    }


    @ApiOperation("根据名称模糊查询")
    @GetMapping("findByHosname/{hosname}")
    public R findByHosname(@PathVariable String hosname){
        List<Hospital> list = hospitalService.findByHosname(hosname);
        return R.ok(list);
    }
}
