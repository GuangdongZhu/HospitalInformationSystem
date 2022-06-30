package neu.his.hosp.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import neu.his.common.result.R;
import neu.his.hosp.service.ScheduleService;
import neu.his.model.hosp.Schedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.*;

@RestController
@Api

@RequestMapping("/admin/hosp/schedule")
public class ScheduleController {
    @Autowired
    ScheduleService scheduleService;

    // 根据医院编号和科室编号，查询排班规则数据
    @ApiOperation("查询排班规则数据")
    @GetMapping("getScheduleRule/{page}/{limit}/{hoscode}/{depcode}")
    public R getScheduleRule(@PathVariable Long page, @PathVariable Long limit, @PathVariable String hoscode, @PathVariable String depcode) {
        Map<String,Object> data = scheduleService.getScheduleRule(page,limit,hoscode,depcode);
        return R.ok(data);
    }


    // 根据医院编号和科室编号，工作日期，查询详细排班信息
    @ApiOperation("查询详细排班信息")
    @GetMapping("getScheduleDetail/{hoscode}/{depcode}/{workDate}")
    public R getScheduleDetail(@PathVariable String hoscode, @PathVariable String depcode, @PathVariable String workDate) {
        List<Schedule> list = scheduleService.getScheduleDetail(hoscode,depcode,workDate);
        return R.ok(list);
    }
}
