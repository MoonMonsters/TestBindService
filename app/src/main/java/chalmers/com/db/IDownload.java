package chalmers.com.db;

import chalmers.com.bean.ThreadInfo;

/**
 * Created by Chalmers on 2016-06-15 21:19.
 * email:qxinhai@yeah.net
 */
public interface IDownload {
    /** 插入 */
    void insert(ThreadInfo threadInfo);
    /** 更新 */
    void update(String url, long finished);
    /** 查询 */
    ThreadInfo query(String url);
}
