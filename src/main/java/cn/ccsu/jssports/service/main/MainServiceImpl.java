/*
 * Created by Long Duping
 * Date 2019-03-24 12:34
 */
package cn.ccsu.jssports.service.main;

import cn.ccsu.jssports.cnt.Const;
import cn.ccsu.jssports.cnt.ErrorConst;
import cn.ccsu.jssports.conf.AppConfig;
import cn.ccsu.jssports.dao.MainMapper;
import cn.ccsu.jssports.dao.UserMapper;
import cn.ccsu.jssports.exception.AppException;
import cn.ccsu.jssports.pojo.AttendanceInfo;
import cn.ccsu.jssports.pojo.RecordDto;
import cn.ccsu.jssports.pojo.Session;
import cn.ccsu.jssports.pojo.UserInfo;
import cn.ccsu.jssports.service.top.RankContainer;
import cn.ccsu.jssports.util.WXBizDataCrypt;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service("mainService")
public class MainServiceImpl implements MainService {
    private static final Logger logger = LoggerFactory.getLogger(MainService.class);

    public static final long ONE_DAY_IN_SECONDS = 86400000L;


    @Autowired
    private MainMapper mainMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public boolean attendance(AttendanceInfo attendanceInfo) throws AppException {

        Calendar left = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
        Calendar right = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
        left.set(Calendar.HOUR_OF_DAY, 0);
        left.set(Calendar.MINUTE, 0);
        left.set(Calendar.SECOND, 0);

        long leftTime = left.getTimeInMillis();
        long rightTime = leftTime + ONE_DAY_IN_SECONDS;
        right.setTimeInMillis(rightTime);
        // 2019-03-24 00:00:00 之后   2019-03-25 00:00:00 之前
        // 用户的实际打卡时间
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(attendanceInfo.getAttendanceTime());
        //1553407025
        if (attendanceInfo.getStep() < AppConfig.minStep()) {
            throw new AppException(ErrorConst.ATTENDANCE_ERROR_STEP_LESS, "未达到规定的最少步数[" + AppConfig.minStep() + "]");
        }
        if (!now.after(left) || !now.before(right)) {
            throw new AppException(ErrorConst.ATTENDANCE_ERROR_TIME_VALIED, "当前打卡时间不在合法范围内[" + leftTime + "," + rightTime + "]");
        }
        if (mainMapper.isAttendcanced(attendanceInfo.getOpenId(), leftTime, rightTime)) {
            throw new AppException(ErrorConst.ATTENDANCE_ERROR_REPEATED, "您今天已经打过卡了");
        }
        if (1 != mainMapper.insert(attendanceInfo)) {
            throw new AppException(ErrorConst.ATTENDANCE_ERROR_INSERT_TO_DATABASE, "记录打卡信息的时候遇到数据库插入错误");
        }
        return true;
    }

    @Override
    public Map<String, Object> getRecordList(String openId) throws AppException {
        List<AttendanceInfo> attendanceInfoList = mainMapper.selectList(openId, AppConfig.beginDate().getTimeInMillis(), AppConfig.endDate().getTimeInMillis());
        if (attendanceInfoList == null || attendanceInfoList.size() == 0) {
            throw new AppException(ErrorConst.HAS_NO_RESULT);
        }
        List<RecordDto> resList = new ArrayList<>();
        RecordDto rd;
        int index = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
        boolean flag = true;
        long stepCount = 0;
        int continuityCount = 0;
        int maxContinuityCount = 0;
        int totalAttendanceCount = 0;
        Map<Integer, Boolean> used = new HashMap<>();
        Random random = new Random(System.currentTimeMillis());
        int msgLen = Const.MSG.GORGETED_MSGS.length;
        for (int i = 0; i < AppConfig.activityDayCount() && index < attendanceInfoList.size(); i++) {
            AttendanceInfo tmpAttendanceInfo = attendanceInfoList.get(index);
            rd = new RecordDto();
            if (Math.abs(tmpAttendanceInfo.getAttendanceTime() - (AppConfig.beginDate().getTimeInMillis() + i * ONE_DAY_IN_SECONDS)) < ONE_DAY_IN_SECONDS) {
                // 当天打卡正常
                rd.setId((long) i);
                rd.setStep(tmpAttendanceInfo.getStep());
                rd.setDate(sdf.format(tmpAttendanceInfo.getAttendanceTime()));
                rd.setAttendanced(tmpAttendanceInfo.getStep() >= AppConfig.minStep());
                index++;
                continuityCount++;
                totalAttendanceCount++;
            } else {
                // 当天漏签
                rd.setId((long) i);
                rd.setAttendanced(false);
                rd.setDate(sdf.format(AppConfig.beginDate().getTimeInMillis() + i * ONE_DAY_IN_SECONDS));
                flag = false;
                continuityCount = 0;
            }
            Integer randInt = random.nextInt(msgLen);
            while (used.size() <= msgLen && used.containsKey(randInt)) {
                randInt = random.nextInt(msgLen);
            }
            used.put(randInt, true);

            rd.setMsg(Const.MSG.GORGETED_MSGS[randInt]);
            stepCount += rd.getStep();
            resList.add(rd);
            if (continuityCount > maxContinuityCount) {
                maxContinuityCount = continuityCount;
            }
        }
        setMarkMsg(resList, "begin");
        resList.sort((d1, d2) -> (int) (d2.getId() - d1.getId()));
        setMarkMsg(resList, "current");
        Map<String, Object> resMap = new HashMap<>(5);
        resMap.put("is_success", flag && resList.size() == AppConfig.activityDayCount());
        resMap.put("list", resList);
        resMap.put("step_count", stepCount);
        resMap.put("km_distance", (stepCount * 60) / 100 / 1000);
        resMap.put("max_continuity_count", maxContinuityCount);
        resMap.put("total_attendance_count", totalAttendanceCount);
        return resMap;
    }

    private void setMarkMsg(List<RecordDto> resList, String mark) {
        if (resList.size() > 0) {
            RecordDto recordDto = resList.get(0);
            if (recordDto.isAttendanced()) {
                if ("begin".equals(mark)) {
                    recordDto.setMsg("活动开始第一天。圆满达成目标！真棒！");
                } else if ("current".equals(mark)) {
                    recordDto.setMsg("达成目标了，又是元气满满的一天！");
                }
            } else {
                if ("begin".equals(mark)) {
                    recordDto.setMsg("活动开始第一天。很遗憾，您没有达成目标！");
                } else if ("current".equals(mark)) {
                    recordDto.setMsg("昨天过去了就算了，今天要继续加油哦！");
                }
            }
        }
    }

    @Override
    public Map<String, Object> getTop(String openId, int count) throws AppException {
        logger.info("get top rank ,count: " + count);
        Map<String, Object> retMap = new HashMap<>(2);
        UserInfo user;
        List<RankContainer.Node> list = RankContainer.getInstance().getTop(count);

        Map<String, Object> mySelfMap = RankContainer.getInstance().getPosition(openId);
        Map<String, Object> currentUser = new HashMap<>();
        if (mySelfMap != null) {
            user = userMapper.selectByOpenId(openId);
            currentUser.put("rank", mySelfMap.get("position"));
            currentUser.put("openId", openId);
            currentUser.put("nickName", user.getNickName());
            currentUser.put("avatarUrl", user.getAvatarUrl());
            currentUser.put("realName", user.getRealName());
            currentUser.put("jwcAccount", user.getJwcAccount());
            currentUser.put("continuity_count", mySelfMap.get("maxContinuityCount"));
            currentUser.put("step_count", mySelfMap.get("stepCount"));
            currentUser.put("total_attendance_count", mySelfMap.get("totalAttendanceCount"));
            currentUser.put("timestamp", System.currentTimeMillis());
        }
        List<Map<String, Object>> resList = new ArrayList<>(100);
        Map<String, Object> map;
        if (list == null || list.size() == 0) {
            logger.info("top rank list is empty");
            return null;
        }
        for (int i = 0; i < list.size(); i++) {
            RankContainer.Node node = list.get(i);
            user = userMapper.selectByOpenId(node.getOpenId());
            map = new HashMap<>();
            map.put("rank", i + 1);
            map.put("openId", node.getOpenId());
            map.put("nickName", user.getNickName());
            map.put("avatarUrl", user.getAvatarUrl());
            map.put("realName", user.getRealName());
            map.put("continuity_count", node.getMaxContinuityCount());
            map.put("step_count", node.getStepCount());
            map.put("total_attendance_count", node.getTotalAttendanceCount());
            map.put("timestamp", System.currentTimeMillis());
            resList.add(map);
        }
        retMap.put("rankList", resList);
        retMap.put("currentUser", currentUser);
        return retMap;
    }

    @Override
    public Map<String, Object> getStep(String openId, String encryptedData, String iv) throws AppException {
        Map<String, Object> map;
        Session session = userMapper.selectSession(openId);
        logger.info("session: " + session);
        if (session == null) {
            throw new AppException(ErrorConst.SESSION_EXPIRED, "Session为空，请重新登录.");
        }
        Date updateTime = session.getUpdateTime();
        Calendar update = Calendar.getInstance();
        update.setTime(updateTime);
        Calendar nowTime = Calendar.getInstance();
        long expiredIn = (nowTime.getTimeInMillis() - update.getTimeInMillis()) / 1000;
        logger.info("session key already exist in " + expiredIn + " seconds");
        if (expiredIn > 7200) {
            throw new AppException(ErrorConst.SESSION_EXPIRED, "Session过期，请重新登录。");
        }
        JSONObject json = WXBizDataCrypt.decodeCryptedData(session.getSessionKey(), encryptedData, iv);
        logger.info("step data: " + json);
        if (json == null) {
            throw new AppException(ErrorConst.PARSE_ERROR, "用户的步数数据解密出现了错误，请确认你的请求顺序：login -> step");
        }
        JSONArray stepInfoList = json.getJSONArray("stepInfoList");
        long now = System.currentTimeMillis() / 1000;
        for (int i = 0; i < stepInfoList.size(); i++) {
            JSONObject item = stepInfoList.getJSONObject(i);
            long timesatmp = item.getLong("timestamp");
            if (now - timesatmp < 24 * 60 * 60) {
                map = new HashMap<>();
                int step = item.getInteger("step");
                map.put("step", step);
                map.put("timestamp", item.getLongValue("timestamp"));
                logger.info("openId: " + session.getOpenId() + " sessionKey: " + session.getSessionKey() + "today step: " + step);
                return map;
            }
        }
        return null;
    }
}
