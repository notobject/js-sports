/*
 * Created by Long Duping
 * Date 2019-03-24 15:57
 */
package cn.ccsu.jssports.service.top;

import cn.ccsu.jssports.dao.UserMapper;
import cn.ccsu.jssports.service.main.MainService;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 排行榜更新任务队列
 */
@Service
public class TopRefreshService implements Job {
    private static final Logger logger = LoggerFactory.getLogger(TopRefreshService.class);
    private UserMapper userMapper;
    private MainService mainService;

    private int pageCount = 30;
    private ThreadPoolExecutor threadPoolExecutor;

    public TopRefreshService() {
    }

    public TopRefreshService(UserMapper userMapper, MainService mainService) {
        this.userMapper = userMapper;
        this.mainService = mainService;
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.info("Top refresh begin, rank list size: " + RankContainer.getInstance().size());
        JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        userMapper = (UserMapper) jobDataMap.get("userMapper");
        mainService = (MainService) jobDataMap.get("mainService");
        threadPoolExecutor = (ThreadPoolExecutor) jobDataMap.get("threadPoolExecutor");
        pageCount = (int) jobDataMap.get("pageCount");

        List<String> list = userMapper.selectOpenIdList(0, pageCount);
        List<Future> futureList = new ArrayList<>();
        int count = 0;
        if (list != null && list.size() > 0) {
            logger.info("user list size: " + list.size());
            while (list.size() > 0) {
                list = userMapper.selectOpenIdList(count, pageCount);
                count += list.size();
                for (String openId : list) {
                    Future<?> future = threadPoolExecutor.submit(new RefreshTask(mainService, openId));
                    futureList.add(future);
                }
            }
        }
        try {
            // 等待任务队列全部执行完成
            for (Future f : futureList) {
                f.get();
            }
            // 交换
            RankContainer.getInstance().exchange();
            logger.info("Top refresh complete. rank list size: " + RankContainer.getInstance().size());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
