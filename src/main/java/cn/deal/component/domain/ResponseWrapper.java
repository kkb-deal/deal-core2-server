package cn.deal.component.domain;

import java.io.Serializable;


public class ResponseWrapper<T> implements Serializable{

    public enum Status {
        OK(1),
        FAIL(0);
        private int val;
        public int getVal() {
            return val;
        }
        private Status(int val) {
            this.val = val;
        }
    }

    private static final long serialVersionUID = -6114826425096991813L;
    private int status;
    private String code;
    private String msg;
    private T data;

    public ResponseWrapper(Status status, String code, String msg) {
        this.status = status.getVal();
        this.code = code;
        this.msg = msg;
    }

    public ResponseWrapper(Status status, T data) {
        this.status = status.getVal();
        this.data = data;
    }

    public ResponseWrapper() {
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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
        return "ResponseWrapper{" +
                "status=" + status +
                ", code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
