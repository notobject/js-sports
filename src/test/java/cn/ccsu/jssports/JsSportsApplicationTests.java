package cn.ccsu.jssports;

import cn.ccsu.jssports.conf.AppConfig;
import cn.ccsu.jssports.dao.MainMapper;
import cn.ccsu.jssports.dao.UserMapper;
import cn.ccsu.jssports.exception.AppException;
import cn.ccsu.jssports.pojo.AttendanceInfo;
import cn.ccsu.jssports.pojo.RecordDto;
import cn.ccsu.jssports.pojo.UserInfo;
import cn.ccsu.jssports.service.main.MainService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Random;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JsSportsApplicationTests {

    @Autowired
    private MainMapper mainMapper;
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MainService mainService;

    @Test
    public void contextLoads() {
    }

    @Test
    public void testInsertAttendanceInfo() {

        Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < 1000; i++) {
            UserInfo userInfo = new UserInfo();
            userInfo.setOpenId("abc-" + i);
            userMapper.insert(userInfo);

            for (int j = 0; j < 11; j++) {
                Calendar instance = Calendar.getInstance();
                instance.add(Calendar.DAY_OF_MONTH, j);
                AttendanceInfo ai = new AttendanceInfo();
                ai.setOpenId("abc-" + i);
                ai.setStep(10000 + (long) random.nextInt(20000));
                ai.setAttendanceTime(instance.getTimeInMillis());
                mainMapper.insert(ai);
            }
        }
    }

    @Test
    public void testInsertUser() {
        for (int i = 0; i < 1000; i++) {
            UserInfo userInfo = new UserInfo();
            userInfo.setOpenId("abc-" + i);
            userMapper.insert(userInfo);
        }
    }

    @Test
    public void testRecordList() {
        for (int i = 0; i < 10; i++) {
            System.out.println("============================================================");
            Map<String, Object> map = null;
            try {
                map = mainService.getRecordList("abc-" + i);
                List<RecordDto> resList = (List<RecordDto>) map.get("list");
                System.out.println(map.get("is_success"));
                System.out.println(map.get("step_count"));
                System.out.println(map.get("km_distance"));
                System.out.println(map.get("max_continuity_count"));
                for (RecordDto rd : resList) {
                    System.out.println(rd);
                }
            } catch (AppException e) {

            }

        }
    }

}
