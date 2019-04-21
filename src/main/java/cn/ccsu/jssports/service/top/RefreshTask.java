/*
 * Created by Long Duping
 * Date 2019-03-24 16:25
 */
package cn.ccsu.jssports.service.top;

import cn.ccsu.jssports.cnt.ErrorConst;
import cn.ccsu.jssports.exception.AppException;
import cn.ccsu.jssports.service.main.MainService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class RefreshTask implements Runnable {
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    private MainService mainService;
    private String openId;
    private RankContainer container;

    public RefreshTask(MainService mainService, String openId) {
        this.mainService = mainService;
        this.openId = openId;
        container = RankContainer.getInstance();
    }

    @Override
    public void run() {
        try {
            Map<String, Object> map = mainService.getRecordList(openId);
            long stepCount = (long) map.get("step_count");
            int maxContinuityCount = (int) map.get("max_continuity_count");
            int totalAttendanceCount = (int) map.get("total_attendance_count");
            container.insert(new RankContainer.Node(openId, maxContinuityCount, stepCount, totalAttendanceCount));
        } catch (AppException e) {
            if (e.getErrcode().equals(ErrorConst.HAS_NO_RESULT.getErrcode())) {
                return;
            }
            e.printStackTrace();
        }
    }

    private String getNow() {
        return sdf.format(new Date(System.currentTimeMillis()));
    }
}
