/*
 * Created by Long Duping
 * Date 2019-03-24 18:41
 */
package cn.ccsu.jssports.service.top;

import cn.ccsu.jssports.cnt.ErrorConst;
import cn.ccsu.jssports.exception.AppException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.logging.log4j.util.Strings;

import java.util.*;

/**
 * 排行榜内存存储容器
 */
public class RankContainer {

    private static final int MAX_SIZE = 100;
    private static RankContainer instance;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class Node {
        /**
         * 排行榜排序节点
         */
        private String openId;
        private int maxContinuityCount;
        private long stepCount;
        private int totalAttendanceCount;
    }

    private List<Node> arr = new ArrayList<>();
    private List<Node> arrBack = new ArrayList<>();
    private Map<String, Boolean> inserted = new HashMap<>();
    private int p = 0;

    private RankContainer() {
    }

    /**
     * 单例
     *
     * @return
     */
    public static RankContainer getInstance() {
        synchronized (RankContainer.class) {
            if (instance == null) {
                synchronized (RankContainer.class) {
                    instance = new RankContainer();
                }
            }
            return instance;
        }
    }

    /**
     * 获取当前排行榜人数
     * ，简单的插入排序（可以考虑换成堆实现）
     *
     * @return
     */
    public int size() {
        return arrBack.size();
    }

    /**
     * 排行榜实时更新
     *
     * @param node
     */
    public synchronized void insert(Node node) {
        if (inserted.containsKey(node.getOpenId())) {
            return;
        }
        int i;
        for (i = 0; i < arr.size(); i++) {
            if (node.totalAttendanceCount >= arr.get(i).totalAttendanceCount) {
                break;
            }
        }
        if (i == arr.size()) {
            add(-1, node);
            return;
        }
        while (i < arr.size() && node.totalAttendanceCount == arr.get(i).totalAttendanceCount && node.stepCount < arr.get(i).stepCount) {
            i++;
        }
        add(i, node);
    }

    /**
     * 获取前size个节点
     *
     * @param size
     * @return
     */
    public List<Node> getTop(int size) {
        if (size <= 0 || arrBack.size() <= 0) {
            return null;
        }
        if (size >= arrBack.size()) {
            size = arrBack.size();
        }
        if (size > MAX_SIZE) {
            size = MAX_SIZE;
        }
        return arrBack.subList(0, size);
    }

    /**
     * 获取自己的排名，找不到则返回 -1 .表示未上榜
     *
     * @param openId
     * @return
     * @throws AppException
     */
    public Map<String, Object> getPosition(String openId) throws AppException {
        if (Strings.isEmpty(openId)) {
            throw new AppException(ErrorConst.PARAM_ERROR, "open id can not be empty!");
        }
        for (int i = 0; i < arrBack.size(); i++) {
            Node node = arrBack.get(i);
            if (openId.equals(node.getOpenId())) {
                Map<String, Object> map = new HashMap<>();
                map.put("position", i + 1);
                map.put("stepCount", node.getStepCount());
                map.put("maxContinuityCount", node.getMaxContinuityCount());
                map.put("totalAttendanceCount", node.getTotalAttendanceCount());
                return map;
            }
        }
        return null;
    }

    /**
     * 交换更新数组 和 缓存数组。
     */
    public synchronized void exchange() {
        arrBack.clear();
        arrBack.addAll(arr);
        arr.clear();
        inserted.clear();
    }

    private synchronized void add(int index, Node node) {
        if (index == -1) {
            arr.add(node);
        } else {
            arr.add(index, node);
        }
        inserted.put(node.getOpenId(), true);
    }
}
