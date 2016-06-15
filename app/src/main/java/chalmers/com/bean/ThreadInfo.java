package chalmers.com.bean;

/**
 * Created by Chalmers on 2016-06-15 18:35.
 * email:qxinhai@yeah.net
 */
public class ThreadInfo {
    /** 下载链接 */
    private String url;
    /** 文件名称 */
    private String filename;
    /** 开始位置 */
    private long start;
    /** 结束位置 */
    private long end;
    /** 已经下载量 */
    private long finished;

    public ThreadInfo(String url, String filename, long start, long end, long finished) {
        this.url = url;
        this.filename = filename;
        this.start = start;
        this.end = end;
        this.finished = finished;
    }

    public ThreadInfo(){}

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public long getFinished() {
        return finished;
    }

    public void setFinished(long finished) {
        this.finished = finished;
    }
}
