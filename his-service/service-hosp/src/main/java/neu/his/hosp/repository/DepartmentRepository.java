package neu.his.hosp.repository;

import neu.his.model.hosp.Department;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DepartmentRepository extends MongoRepository<Department,String> {
    Department getDepartmentByHoscodeAndDepcode(String hoscode, String depcode);

    Department getDepartmentByHoscode(String hoscode);

}
