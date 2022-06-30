import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class UserData {
    @ExcelProperty(value = "用户id",index = 0)
    private Integer id;

    @ExcelProperty(value = "用户姓名", index = 1)
    private String name;
}
