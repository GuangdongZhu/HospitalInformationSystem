package neu.his.hospInfo.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import neu.his.hospInfo.listener.dictListener;
import neu.his.hospInfo.mapper.HospitalInfoMapper;
import neu.his.hospInfo.service.HospitalInfoService;
import neu.his.model.cmn.Dict;
import neu.his.vo.cmn.DictEeVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HospitalInfoServiceImpl extends ServiceImpl<HospitalInfoMapper, Dict> implements HospitalInfoService {
    @Override
    @Cacheable(value = "dict", keyGenerator = "keyGenerator")
    public List<Dict> getChildrenData(Long id) {
        List<Dict> list = baseMapper.selectList(new QueryWrapper<Dict>().eq("parent_id", id));
        list.forEach(dict -> {
            dict.setHasChildren(isChildren(dict.getId()));
        });
        return list;
    }

    public boolean isChildren(Long id) {
        Integer count = baseMapper.selectCount(new QueryWrapper<Dict>().eq("parent_id", id));
        return count > 0;
    }

    @Override
    @CacheEvict(value = "dict", allEntries = false)
    public void exportDictData(HttpServletResponse response) {
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("UTF-8");
        List<Dict> dictList = baseMapper.selectList(null);
        List<DictEeVo> list = dictList.stream().map(dict -> {
            DictEeVo vo = new DictEeVo();
            BeanUtils.copyProperties(dict, vo);
            return vo;
        }).collect(Collectors.toList());
        String fileName = "dict";
        try {
            EasyExcel.write(response.getOutputStream(), DictEeVo.class).sheet("dict").doWrite(list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void importDictData(MultipartFile file) {
        try {
            EasyExcel.read(file.getInputStream(), DictEeVo.class, new dictListener(baseMapper)).sheet().doRead();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getDictName(String dictCode, String value) {
        //如果dictCode为空,直接根据value查询
        if (StringUtils.isEmpty(dictCode)) {
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("value", value);
            Dict dict = baseMapper.selectOne(queryWrapper);
            return dict.getName();
        } else {//如果dictCode不为空,根据dictCode和value查询
            //根据dictcode查询dict对象，目的是得到dict的id值，在根据此id值查询此id下的子id
            Dict codeDict = getDictByDictcode(dictCode);
            Long parent_id = codeDict.getId();
            //根据parentId和value值进行查询
            QueryWrapper<Dict> queryWrapper2 = new QueryWrapper();
            queryWrapper2.eq("parent_id", parent_id).eq("value", value);
            Dict finalDict = baseMapper.selectOne(queryWrapper2);
            return finalDict.getName();
        }
    }

    private Dict getDictByDictcode(String dictCode) {
        QueryWrapper<Dict> queryWrapper1 = new QueryWrapper();
        queryWrapper1.eq("dict_code", dictCode);
        return baseMapper.selectOne(queryWrapper1);
    }

    //根据dictcode查询查询子节点
    @Override
    public List<Dict> findByDictCode(String dictCode) {
        //根据dictcode获取对应的id
        QueryWrapper<Dict> queryWrapper1 = new QueryWrapper();
        queryWrapper1.eq("dict_code",dictCode);
        Dict codeDict = baseMapper.selectOne(queryWrapper1);
        //根据id获取子节点
        List<Dict> childData = this.getChildrenData(codeDict.getId());
        return childData;
    }

}
