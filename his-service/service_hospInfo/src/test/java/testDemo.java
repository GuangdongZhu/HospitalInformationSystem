import com.alibaba.excel.EasyExcel;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class testDemo {
    @Test
    public void demo(){
        List<UserData> list = new ArrayList<>();

        for (int i=0;i<10;i++)
        {
            UserData data = new UserData();
            data.setId(i+1);
            data.setName("lucy"+i);
            list.add(data);
        }

        String filename = "D:\\saveexcel\\01.xlsx";
        EasyExcel.write(filename,UserData.class).sheet("用户信息").doWrite(list);
    }

    @Test
    public void demo2(){
        String filename = "D:\\saveexcel\\01.xlsx";
        EasyExcel.read(filename,UserData.class, new ExcelListener()).sheet().doRead();
    }
}
