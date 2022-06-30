package neu.his.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import neu.his.hosp.repository.DepartmentRepository;
import neu.his.hosp.service.DepartmentService;
import neu.his.model.hosp.Department;
import neu.his.vo.hosp.DepartmentQueryVo;
import neu.his.vo.hosp.DepartmentVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DepartmentServiceImpl implements DepartmentService {
    @Autowired
    DepartmentRepository repository;


    @Override
    public void save(Map<String, Object> map) {
        // 将map转换为Department对象
        String s = JSONObject.toJSONString(map);
        Department department = JSONObject.parseObject(s,Department.class);


        Department departmentExist = repository.getDepartmentByHoscodeAndDepcode(department.getHoscode(),department.getDepcode());

        //如果存在，进行修改
        if(departmentExist != null){
            department.setCreateTime(departmentExist.getCreateTime());
            department.setUpdateTime(new Date());
            department.setIsDeleted(0);
            repository.save(department);
        }else{
            //如果不存在，进行添加
            department.setCreateTime(new Date());
            department.setUpdateTime(new Date());
            department.setIsDeleted(0);
            repository.save(department);
        }
    }

    @Override
    public Page<Department> finPageDepartment(int page, int limit, DepartmentQueryVo departmentQueryVo) {
        //创建Pageble对象，里面设置当前页和每页记录数
        Pageable pageable = PageRequest.of(page - 1,limit); // 当前页从0开始，但是我们从1开始传的
        //将departmentQueryVo对象转换为department对象
        Department department = new Department();
        BeanUtils.copyProperties(departmentQueryVo,department);
        department.setIsDeleted(0);
        //创建Example对象
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);
        Example<Department> example = Example.of(department,matcher);
        Page<Department> all = repository.findAll(example,pageable);
        return all;
    }

    @Override
    public void remove(String hoscode, String depcode) {
        Department department = repository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
        if (department!=null){
            department.setIsDeleted(1);
            repository.save(department);
        }
    }

    @Override
    public List<DepartmentVo> findDeptTree(String hoscode) {
        List<DepartmentVo> resList = new ArrayList<>();

        Department department = new Department();
        department.setHoscode(hoscode);
        Example example = Example.of(department);
        List<Department> all = repository.findAll(example);


        Map<String, List<Department>> departmentMap = all.stream().collect(Collectors.groupingBy(Department::getBigcode));

        for (Map.Entry<String, List<Department>> entry : departmentMap.entrySet())
        {
            // 大科室编号
            String bigcode = entry.getKey();

            // 大科室编号对应的全局数据
            List<Department> bigDeptList = entry.getValue();

            // 封装大科室信息
            DepartmentVo bigDeptVo = new DepartmentVo();
            bigDeptVo.setDepcode(bigcode);
            bigDeptVo.setDepname(bigDeptList.get(0).getDepname());

            // 封装小科室信息
            List<DepartmentVo> children = new ArrayList<>();
            for (Department childDepartment : bigDeptList) {
                DepartmentVo childrenDeptVo = new DepartmentVo();
                childrenDeptVo.setDepcode(childDepartment.getDepcode());
                childrenDeptVo.setDepname(childDepartment.getDepname());

                // 将信息添加到列表中
                children.add(childrenDeptVo);
            }
            bigDeptVo.setChildren(children);
            resList.add(bigDeptVo);
        }

        return resList;
    }

    @Override
    public Object getDepName(String hoscode, String depcode) {
        Department department = repository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
        if(department != null)
            return department.getDepname();
        return null;
    }


}
