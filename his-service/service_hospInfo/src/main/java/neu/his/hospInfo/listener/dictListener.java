package neu.his.hospInfo.listener;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import neu.his.hospInfo.mapper.HospitalInfoMapper;
import neu.his.model.cmn.Dict;
import neu.his.vo.cmn.DictEeVo;
import org.springframework.beans.BeanUtils;

public class dictListener extends AnalysisEventListener<DictEeVo> {

    HospitalInfoMapper mapper;

    public dictListener(HospitalInfoMapper infoMapper) {
        this.mapper = infoMapper;
    }

    @Override
    public void invoke(DictEeVo dictEeVo, AnalysisContext analysisContext) {
        Dict dict = new Dict();
        BeanUtils.copyProperties(dictEeVo, dict);
        mapper.insert(dict);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
