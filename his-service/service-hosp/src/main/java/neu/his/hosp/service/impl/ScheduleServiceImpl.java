package neu.his.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;

import neu.his.hosp.repository.ScheduleRepository;
import neu.his.hosp.service.DepartmentService;
import neu.his.hosp.service.HospitalService;
import neu.his.hosp.service.ScheduleService;
import neu.his.model.hosp.Schedule;
import neu.his.vo.hosp.BookingScheduleRuleVo;
import neu.his.vo.hosp.ScheduleQueryVo;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class ScheduleServiceImpl implements ScheduleService {
    @Autowired
    ScheduleRepository scheduleRepository;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    HospitalService hospitalService;

    @Autowired
    DepartmentService departmentService;


    @Override
    public void save(Map<String, Object> parampMap) {
        String s = JSONObject.toJSONString(parampMap);
        Schedule schedule = JSONObject.parseObject(s, Schedule.class);
        Schedule scheduleExist = scheduleRepository.getScheduleByHoscodeAndHosScheduleId(schedule.getHoscode(),schedule.getHosScheduleId());

        //判断排班是否存在
        if(scheduleExist != null){
            //如果存在，做更改
            scheduleExist.setUpdateTime(new Date());
            scheduleExist.setIsDeleted(0);
            scheduleExist.setStatus(1);
            scheduleRepository.save(scheduleExist);
        }else{
            schedule.setCreateTime(new Date());
            schedule.setUpdateTime(new Date());
            schedule.setIsDeleted(0);
            schedule.setStatus(1);
            scheduleRepository.save(schedule);
        }
    }

    @Override
    public Page<Schedule> finPageSchedule(int page, int limit, ScheduleQueryVo scheduleQueryVo) {
        //MongoRepository开发CRUD
        //创建Pageble对象，里面设置当前页和每页记录数
        Pageable pageable = PageRequest.of(page - 1,limit); // 当前页从0开始，但是我们从1开始传的
        //将departmentQueryVo对象转换为department对象
        Schedule schedule = new Schedule();
        BeanUtils.copyProperties(scheduleQueryVo,schedule);
        schedule.setIsDeleted(0);
        schedule.setStatus(1);
        //创建Example对象
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);
        Example<Schedule> example = Example.of(schedule,matcher);
        Page<Schedule> all = scheduleRepository.findAll(example,pageable);
        return all;
    }

    @Override
    public void remove(String hoscode, String hosScheduleId) {
        //根据医院编号和排班编号查询相关信息
        Schedule schedule = scheduleRepository.getScheduleByHoscodeAndHosScheduleId(hoscode, hosScheduleId);
        if(schedule != null){
            schedule.setIsDeleted(1);
            scheduleRepository.save(schedule);
        }
    }

    @Override
    public Map<String, Object> getScheduleRule(Long page, Long limit, String hoscode, String depcode) {
        // 根据医院编号和科室编号查询
        Criteria criteria = Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode);

        // 根据工作日workdate进行分组
        Aggregation agg  =  Aggregation.newAggregation(
                Aggregation.match(criteria),// 匹配条件
                Aggregation.group("workDate").first("workDate").as("workDate") // 分组条件
                        .count().as("docCount") // 统计数量
                        .sum("reservedNumber").as("reservedNumber") // 统计已被预约的数量
                        .sum("availableNumber").as("availableNumber"), // 统计可用数量

                // 对分组数据进行排序
                Aggregation.sort(Sort.Direction.DESC,"workDate"),
                // 对分组数据进行分页
                Aggregation.skip((page-1)*limit),
                Aggregation.limit(limit)
        );
        // 封装数据
        AggregationResults<BookingScheduleRuleVo> aggResults = mongoTemplate.aggregate(agg, Schedule.class, BookingScheduleRuleVo.class);
        List<BookingScheduleRuleVo> results = aggResults.getMappedResults();

        // 获取分组的结果记录数量
        Aggregation totalAgg = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("workDate")
        );
        AggregationResults<BookingScheduleRuleVo> totalAggResults = mongoTemplate.aggregate(totalAgg, Schedule.class, BookingScheduleRuleVo.class);
        int total = totalAggResults.getMappedResults().size();

        // 将日期转换为星期几
        for(BookingScheduleRuleVo bookingScheduleRuleVo : results){
            Date workDate = bookingScheduleRuleVo.getWorkDate();
            String dayOfWeek = this.getDayOfWeek(new DateTime(workDate));
            bookingScheduleRuleVo.setDayOfWeek(dayOfWeek);
        }

        //设置最终数据进行返回
        Map<String,Object> result = new HashMap<>();
        result.put("bookingScheduleRuleList",results);
        result.put("total",total);

        //获取医院名称
        String hosName = hospitalService.getHospName(hoscode);

        //其他基础数据(医院名称)
        Map<String, String> baseMap = new HashMap<>();
        baseMap.put("hosname",hosName);
        result.put("baseMap",baseMap);

        return result;
    }

    @Override
    public List<Schedule> getScheduleDetail(String hoscode, String depcode, String workDate) {
        //根据参数查询mongodb，得到数据
        List<Schedule> scheduleList = scheduleRepository.findScheduleByHoscodeAndDepcodeAndWorkDate(hoscode,depcode,new DateTime(workDate).toDate());
        //把得到的list遍历，向其中添加医院名称、科室名称、日期对应的星期
        scheduleList.stream().forEach(item -> {
            this.packageSchedule(item);
        });
        return scheduleList;
    }

    private String getDayOfWeek(DateTime dateTime) {
        String dayOfWeek = "";
        switch (dateTime.getDayOfWeek()) {
            case DateTimeConstants.SUNDAY:
                dayOfWeek = "周日";
                break;
            case DateTimeConstants.MONDAY:
                dayOfWeek = "周一";
                break;
            case DateTimeConstants.TUESDAY:
                dayOfWeek = "周二";
                break;
            case DateTimeConstants.WEDNESDAY:
                dayOfWeek = "周三";
                break;
            case DateTimeConstants.THURSDAY:
                dayOfWeek = "周四";
                break;
            case DateTimeConstants.FRIDAY:
                dayOfWeek = "周五";
                break;
            case DateTimeConstants.SATURDAY:
                dayOfWeek = "周六";
            default:
                break;
        }
        return dayOfWeek;
    }

    //把医院名称、科室名称、日期对应的星期封装到排班详情
    private Schedule packageSchedule(Schedule schedule) {
        //设置医院名称
        schedule.getParam().put("hosname",hospitalService.getHospName(schedule.getHoscode()));
        //设置科室名称
        schedule.getParam().put("depname",departmentService.getDepName(schedule.getHoscode(),schedule.getDepcode()));
        //设置日期对应星期
        schedule.getParam().put("dayOfWeek",this.getDayOfWeek(new DateTime(schedule.getWorkDate())));
        return schedule;
    }
}
