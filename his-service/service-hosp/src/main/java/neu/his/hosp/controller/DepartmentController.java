package neu.his.hosp.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import neu.his.common.result.R;
import neu.his.hosp.service.DepartmentService;
import neu.his.model.hosp.Department;
import neu.his.vo.hosp.DepartmentVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Api

@RequestMapping("/admin/hosp/department")
public class DepartmentController {
    @Autowired
    DepartmentService departmentService;

    @ApiOperation("查询当前医院所有的科室信息")
    @GetMapping("getDepartments/{hoscode}")
    public R getDepartments(@PathVariable String hoscode)
    {
        List<DepartmentVo> list = departmentService.findDeptTree(hoscode);
        return R.ok(list);
    }
}
