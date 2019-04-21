/*
 * Created by Long Duping
 * Date 2019-03-24 14:22
 */
package cn.ccsu.jssports.pojo;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class RecordDto {

    private Long id;
    private String date;
    private boolean isAttendanced;
    private long step;
    private String msg;

    /**
     * "begin"  or  "end"
     * 废弃了
     */
    private String mark;
}
