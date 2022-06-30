package neu.his.hospInfo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import neu.his.model.cmn.Dict;
import neu.his.model.hosp.HospitalSet;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface HospitalInfoService extends IService<Dict> {
    List<Dict> getChildrenData(Long id);

    void exportDictData(HttpServletResponse response);

    void importDictData(MultipartFile file);

    String getDictName(String dictCode, String value);

    List<Dict> findByDictCode(String dictCode);
}
