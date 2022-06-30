package neu.his.hosp.controller.api;


import com.alibaba.fastjson.JSONObject;
import neu.his.common.exception.DefinedException;
import neu.his.common.exception.GobalException;
import neu.his.common.helper.HttpRequestHelper;
import neu.his.common.result.R;
import neu.his.common.result.ResultCodeEnum;
import neu.his.common.utils.MD5;
import neu.his.hosp.service.DepartmentService;
import neu.his.hosp.service.HospitalService;
import neu.his.hosp.service.HospitalSetService;
import neu.his.hosp.service.ScheduleService;
import neu.his.model.hosp.Department;
import neu.his.model.hosp.Hospital;
import neu.his.model.hosp.Schedule;
import neu.his.vo.hosp.DepartmentQueryVo;
import neu.his.vo.hosp.ScheduleQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/hosp")
public class ApiController {

    @Autowired
    HospitalService hospitalService;

    @Autowired
    HospitalSetService setService;

    @Autowired
    DepartmentService departmentService;

    @Autowired
    ScheduleService scheduleService;

    // 查询医院接口
    @PostMapping("hospital/show")
    public R getHospital(HttpServletRequest request) {
        Map<String, String[]> param = request.getParameterMap();
        Map<String, Object> map = HttpRequestHelper.switchMap(param);

        // 获取医院系统传递过来的签名，已经使用MD5进行了加密
        String hospSign = (String) map.get("sign");
        // 将签名和数据库中的进行比对
        String hoscode = (String) map.get("hoscode");

        String sign = setService.getSignKey(hoscode);

        // 因为医院系统传过来的是加密的，所以对数据库的sign进行加密然后对比
        String signEncry = MD5.encrypt(sign);

        if (!signEncry.equals(hospSign)) {
            throw new DefinedException(ResultCodeEnum.SIGN_ERROR);
        }

        Hospital byHoscode = hospitalService.getByHoscode(hoscode);
        return R.ok(byHoscode);
    }


    // 上传医院接口
    @PostMapping("saveHospital")
    public R saveHosp(HttpServletRequest request) {
        Map<String, String[]> param = request.getParameterMap();
        Map<String, Object> map = HttpRequestHelper.switchMap(param);

        // 获取医院系统传递过来的签名，已经使用MD5进行了加密
        String hospSign = (String) map.get("sign");
        // 将签名和数据库中的进行比对
        String hoscode = (String) map.get("hoscode");

        String sign = setService.getSignKey(hoscode);

        // 因为医院系统传过来的是加密的，所以对数据库的sign进行加密然后对比
        String signEncry = MD5.encrypt(sign);

        if (!signEncry.equals(hospSign)) {
            throw new DefinedException(ResultCodeEnum.SIGN_ERROR);
        }

        // 将logodata中的"+"转换为" "
        String logoData = (String) map.get("logoData");
        logoData = logoData.replaceAll(" ", "+");
        map.put("logoData", logoData);

        hospitalService.save(map);
        return R.ok();
    }


    // 查询科室接口
    @PostMapping("department/list")
    public R getDepartment(HttpServletRequest request) {
        // 获取科室信息
        Map<String, String[]> param = request.getParameterMap();
        Map<String, Object> map = HttpRequestHelper.switchMap(param);

        //医院编号
        String hoscode = (String) map.get("hoscode");
        //当前页
        int page = Integer.parseInt((String) map.get("page"));
        if (StringUtils.isEmpty(page)) {
            page = 1;
        }
        //每页显示记录数
        int limit = Integer.parseInt((String) map.get("limit"));
        if (StringUtils.isEmpty(limit)) {
            limit = 1;
        }
        //签名校验
        String hospSign = (String) map.get("sign");
        String signKey = setService.getSignKey(hoscode);
        String signKeyMD5 = MD5.encrypt(signKey);
        if (!hospSign.equals(signKeyMD5)) {
            throw new DefinedException(ResultCodeEnum.SIGN_ERROR);
        }
        //调用service查询
        //查询条件的值封装到departmentQueryVo中
        DepartmentQueryVo departmentQueryVo = new DepartmentQueryVo();
        departmentQueryVo.setHoscode(hoscode);
        Page<Department> pageModel = departmentService.finPageDepartment(page, limit, departmentQueryVo);
        return R.ok(pageModel);
    }

    // 上传科室接口
    @PostMapping("saveDepartment")
    public R saveDepartment(HttpServletRequest request) {
        // 获取科室信息
        Map<String, String[]> param = request.getParameterMap();
        Map<String, Object> map = HttpRequestHelper.switchMap(param);
        // 获取医院系统传递过来的签名，已经使用MD5进行了加密
        String hospSign = (String) map.get("sign");
        // 将签名和数据库中的进行比对
        String hoscode = (String) map.get("hoscode");

        String sign = setService.getSignKey(hoscode);

        // 因为医院系统传过来的是加密的，所以对数据库的sign进行加密然后对比
        String signEncry = MD5.encrypt(sign);

        if (!signEncry.equals(hospSign)) {
            throw new DefinedException(ResultCodeEnum.SIGN_ERROR);
        }

        departmentService.save(map);
        return R.ok();
    }

    // 删除科室接口
    @PostMapping("department/remove")
    public R removeDepartment(HttpServletRequest request){
        //获取传递过来的科室信息
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> parampMap = HttpRequestHelper.switchMap(parameterMap);
        //获取医院编号和科室编号
        String hoscode = (String) parampMap.get("hoscode");
        String depcode = (String) parampMap.get("depcode");
        //签名校验
        String hospSign = (String) parampMap.get("sign");
        String signKey = setService.getSignKey(hoscode);
        String signKeyMD5 = MD5.encrypt(signKey);
        if(!hospSign.equals(signKeyMD5)){
            throw new DefinedException(ResultCodeEnum.SIGN_ERROR);
        }
        departmentService.remove(hoscode,depcode);
        return R.ok();
    }


    //查询排班
    @PostMapping("schedule/list")
    public R findSchedule(HttpServletRequest request) {
        //获取传递过来的科室信息
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> parampMap = HttpRequestHelper.switchMap(parameterMap);
        //医院编号
        String hoscode = (String) parampMap.get("hoscode");
        //科室编号
        String depcode = (String) parampMap.get("depcode");

        //当前页
        int page = Integer.parseInt((String) parampMap.get("page"));
        if (StringUtils.isEmpty(page)) {
            page = 1;
        }
        //每页显示记录数
        int limit = Integer.parseInt((String) parampMap.get("limit"));
        if (StringUtils.isEmpty(limit)) {
            limit = 1;
        }
        //签名校验
        String hospSign = (String) parampMap.get("sign");
        String signKey = setService.getSignKey(hoscode);
        String signKeyMD5 = MD5.encrypt(signKey);
        if (!hospSign.equals(signKeyMD5)) {
            throw new DefinedException(ResultCodeEnum.SIGN_ERROR);
        }
        //调用service查询
        //查询条件的值封装到departmentQueryVo中
        ScheduleQueryVo scheduleQueryVo = new ScheduleQueryVo();
        scheduleQueryVo.setHoscode(hoscode);
        scheduleQueryVo.setDepcode(depcode);
        Page<Schedule> pageModel = scheduleService.finPageSchedule(page, limit, scheduleQueryVo);
        return R.ok(pageModel);
    }


    // 上传排班接口
    @PostMapping("saveSchedule")
    public R saveSchedule(HttpServletRequest request){
        //获取传递过来的科室信息
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> parampMap = HttpRequestHelper.switchMap(parameterMap);

        //签名校验
        String hospSign = (String) parampMap.get("sign");
        String hoscode = (String) parampMap.get("hoscode");
        String signKey = setService.getSignKey(hoscode);
        String signKeyMD5 = MD5.encrypt(signKey);
        if(!hospSign.equals(signKeyMD5)){
            throw new DefinedException(ResultCodeEnum.SIGN_ERROR);
        }

        //调用service方法
        scheduleService.save(parampMap);
        return R.ok();
    }

    //删除排班
    @PostMapping("schedule/remove")
    public R remove(HttpServletRequest request){
        //获取传递过来的科室信息
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> parampMap = HttpRequestHelper.switchMap(parameterMap);
        //获取医院编号和排班编号
        String hoscode = (String) parampMap.get("hoscode");
        String hosScheduleId = (String) parampMap.get("hosScheduleId");
        //签名校验
        String hospSign = (String) parampMap.get("sign");
        String signKey = setService.getSignKey(hoscode);
        String signKeyMD5 = MD5.encrypt(signKey);
        if(!hospSign.equals(signKeyMD5)){
            throw new DefinedException(ResultCodeEnum.SIGN_ERROR);
        }
        scheduleService.remove(hoscode,hosScheduleId);
        return R.ok();
    }
}
