package cn.deal.component.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.io.Serializable;
import java.util.Date;


@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AsyncTask implements Serializable {

    @Tolerate
    public AsyncTask() {

    }

    private String id;
    private String type;
    private String params;
    private String progressText;
    private String result;
    private String key;
    private Integer progressVal;
    private Integer progressCount;
    private Integer status;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date createdAt;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date updatedAt;

    public enum Text {

        /**
         * 处理中
         */
        HANDLING("handing"),

        /**
         * 失败
         */
        FAILED("failed"),

        /**
         * 完成
         */
        COMPLETE("complete");

        private String val;
        public String getVal() {
            return val;
        }
        Text(String val) {
            this.val = val;
        }
    }

    public enum Status {
        /**
         * 创建
         */
        CREATED(0),
        /**
         * 执行
         */
        DOING(1),
        /**
         * 结束
         */
        FINISHED(2),
        /**
         * 错误
         */
        ERROR(3);
        private int val;
        public int getVal() {
            return val;
        }
        Status(int val) {
            this.val = val;
        }
    }

    @Override
    public String toString() {
        return "AsyncTask{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", params='" + params + '\'' +
                ", progressText='" + progressText + '\'' +
                ", result='" + result + '\'' +
                ", progressVal=" + progressVal +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
