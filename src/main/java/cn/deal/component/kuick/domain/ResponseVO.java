package cn.deal.component.kuick.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseVO<T> {

    public enum Status{
        /**
         * 错误
         */
        ERROR(0),
        OK(1);

        private int val;
        private Status(int val){
            this.val = val;
        }
        public int getVal(){
            return val;
        }
    }


    private Integer status;
    private String msg;

    private T data;

    public ResponseVO(Integer status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public ResponseVO(Integer status, String msg, T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ResponseVO{" +
                "status=" + status +
                "mgs=" + msg +
                ", data=" + data +
                '}';
    }

}
