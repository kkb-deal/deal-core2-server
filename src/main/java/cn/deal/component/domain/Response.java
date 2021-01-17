package cn.deal.component.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@JsonIgnoreProperties(ignoreUnknown = true)
public class Response<T> {

    public Response() {

    }

    public enum Status{
        /**
         * 错误
         */
        ERROR(0),
        OK(1);

        private int val;
        Status(int val){
            this.val = val;
        }
        public int getVal(){
            return val;
        }
    }

    private int status;
    private T data;
    private Object msg;
    private Object code;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Object getMsg() {
        return msg;
    }

    public void setMsg(Object msg) {
        this.msg = msg;
    }

    public Object getCode() {
        return code;
    }

    public void setCode(Object code) {
        this.code = code;
    }

    public Response(Status s, T data) {
        this.status = s.getVal();
        this.data = data;
    }

}
