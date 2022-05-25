package neu.his.hosp.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import lombok.val;
import neu.his.common.result.R;
import neu.his.common.utils.MD5;
import neu.his.hosp.service.HospitalSetService;
import neu.his.model.hosp.HospitalSet;
import neu.his.vo.hosp.HospitalSetQueryVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;

@RestController
@Api
@CrossOrigin
@RequestMapping("/admin/hosp/hospitalSet")
public class HosptialSetController {
    @Autowired
    private HospitalSetService hospitalSetService;

    @ApiOperation("查询所有信息")
    @GetMapping("findAll")
    public R findAll() {
        List<HospitalSet> list = hospitalSetService.list();
        return R.ok(list);
    }


    @ApiOperation("逻辑删除数据")
    @DeleteMapping("delete/{id}")
    public R removeInfoById(@PathVariable Long id) {
        boolean b = hospitalSetService.removeById(id);
        return b ? R.ok() : R.fail();
    }


    // 条件查询带分页
    @ApiOperation("条件查询+分页操作")
    @PostMapping("findPage/{current}/{limit}")
    public R findPageHospSet(@PathVariable Long current,
                             @PathVariable Long limit,
                             @RequestBody(required = false) HospitalSetQueryVo hospitalSetQueryVo) {
        //@RequestBody  接收前端传递给后端的json字符串中的数据，用于post请求
        //创建page对象，传递当前页，每页记录数
        Page<HospitalSet> page = new Page(current, limit);

        //构造查询条件
        String hosname = hospitalSetQueryVo.getHosname(); //医院名称
        String hoscode = hospitalSetQueryVo.getHoscode(); //医院编号
        QueryWrapper<HospitalSet> wrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(hosname)) {
            wrapper.like("hosname", hosname);
        }
        if (!StringUtils.isEmpty(hoscode)) {
            wrapper.eq("hoscode", hoscode);
        }

        //调用方法实现分页查询
        Page<HospitalSet> hospitalSetPage = hospitalSetService.page(page, wrapper);
        //返回结果
        return R.ok(hospitalSetPage);

    }

    // 添加医院设置
    @ApiOperation("添加新的医院设置")
    @PostMapping("saveHospitalSet")
    public R saveHospitalSet(@RequestBody HospitalSet hospitalSet) {
        //设置状态 1：可以使用 0：不可使用
        hospitalSet.setStatus(1);
        //签名秘钥
        Random random = new Random();
        hospitalSet.setSignKey(MD5.encrypt(System.currentTimeMillis() + "" + random.nextInt(1000)));
        boolean save = hospitalSetService.save(hospitalSet);
        return save ? R.ok() : R.fail();
    }

    // 根据id查找信息
    @ApiOperation("根据id查询数据")
    @GetMapping("getInfo/{id}")
    public R findById(@PathVariable Long id) {
        HospitalSet byId = hospitalSetService.getById(id);
        return R.ok(byId);
    }

    // 修改数据
    @ApiOperation("修改数据")
    @PostMapping("/updateInfo")
    public R updateById(@RequestBody HospitalSet set) {
        boolean b = hospitalSetService.updateById(set);
        return b ? R.ok() : R.fail();
    }
    // 删除一条医院设置信息
    @ApiOperation("删除一条医院设置信息")
    @DeleteMapping("/{id}")
    public R bathDelete(@PathVariable Long id)
    {
        boolean b = hospitalSetService.removeById(id);
        return b ? R.ok() : R.fail();
    }

    @ApiOperation("批量删除")
    @DeleteMapping("/batchDelete")
    public R bathDelete(@RequestBody List<Long> ids)
    {
        boolean b = hospitalSetService.removeByIds(ids);
        return b ? R.ok() : R.fail();
    }


    // 医院信息的设置加锁和解锁
    @ApiOperation("锁定和解锁")
    @PutMapping("/lockInfo/{id}/{status}")
    public R lockORUnlock(@PathVariable Long id, @PathVariable Integer status) {
        HospitalSet set = hospitalSetService.getById(id);
        set.setStatus(status);
        boolean b = hospitalSetService.updateById(set);
        return b ? R.ok() : R.fail();
    }

    // 发送签名密钥
    @ApiOperation("发送签名密钥")
    @PostMapping("/sendKey/{id}")
    public R sendKey(@PathVariable Long id) {
        HospitalSet set = hospitalSetService.getById(id);
        String key = set.getSignKey();
        String hoscode = set.getHoscode();

        // todo 发送短信进行验证
        return R.ok();
    }
}
