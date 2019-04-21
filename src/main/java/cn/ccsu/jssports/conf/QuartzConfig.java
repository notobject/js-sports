/*
 * Created by Long Duping
 * Date 2019-03-24 16:04
 */
package cn.ccsu.jssports.conf;

import cn.ccsu.jssports.dao.UserMapper;
import cn.ccsu.jssports.service.main.MainService;
import cn.ccsu.jssports.service.top.TopRefreshService;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class QuartzConfig {
    private ThreadPoolExecutor threadPoolExecutor;
    private BlockingQueue<Runnable> queue;

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MainService mainService;

    @Bean
    public JobDetail topRefreshDetail() {
        queue = new LinkedBlockingQueue<>();
        threadPoolExecutor = new ThreadPoolExecutor(2, 4, 3600, TimeUnit.MINUTES, queue);
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("userMapper", userMapper);
        jobDataMap.put("mainService", mainService);
        jobDataMap.put("threadPoolExecutor", threadPoolExecutor);
        jobDataMap.put("pageCount", 30);
        return JobBuilder.newJob(TopRefreshService.class).setJobData(jobDataMap).withIdentity("top-refresh").storeDurably().build();
    }

    @Bean
    public Trigger topRefreshTrigger() {
        SimpleScheduleBuilder builder = SimpleScheduleBuilder.simpleSchedule()
                // 每5分钟更新一次排行榜
                .withIntervalInMinutes(5)
                .repeatForever();
        return TriggerBuilder.newTrigger()
                .forJob(topRefreshDetail())
                .withIdentity("top-refresh")
                .withSchedule(builder)
                .build();
    }
}
