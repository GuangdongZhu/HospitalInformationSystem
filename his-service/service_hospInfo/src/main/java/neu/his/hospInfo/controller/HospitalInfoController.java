package neu.his.hospInfo.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import neu.his.common.result.R;
import neu.his.hospInfo.service.HospitalInfoService;
import neu.his.model.cmn.Dict;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@Api(value = "数据字典相关")
@RequestMapping("/admin/hospInfo/dict")
public class HospitalInfoController {
    @Autowired
    HospitalInfoService infoService;

    @ApiOperation("查询子数据")
    @GetMapping("/findChildrenData/{id}")
    public R findChildren(@PathVariable Long id)
    {
        List<Dict> list =infoService.getChildrenData(id);
        return R.ok(list);
    }

    @ApiOperation("导出数据")
    @GetMapping("/exportData")
    public R exportData(HttpServletResponse response)
    {
        infoService.exportDictData(response);
        return R.ok();
    }

    @ApiOperation("导入数据")
    @PostMapping("/importData")
    public R importData(MultipartFile file)
    {
        infoService.importDictData(file);
        return R.ok();
    }

    //根据dictcode和value查询名称
    @GetMapping("getName/{dictCode}/{value}")
    public String getName(@PathVariable String dictCode,
                          @PathVariable String value){
        String dictName = infoService.getDictName(dictCode,value);
        return dictName;
    }

    //根据value查询查询名称
    @GetMapping("getName/{value}")
    public String getName(@PathVariable String value){
        String dictName = infoService.getDictName("",value);
        return dictName;
    }

    //根据dictcode查询查询子节点
    @ApiOperation(value = "根据dictcode查询查询子节点")
    @GetMapping("/findByDictCode/{dictCode}")
    public R findByDictCode(@PathVariable("dictCode") String dictCode){
        List<Dict> list = infoService.findByDictCode(dictCode);
        return R.ok(list);
    }

}
