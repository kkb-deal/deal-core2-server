package cn.deal.component.kuick.domain;

/**
 * 响应报文
 */
public class ResponseBody {
	private Integer status;
	private Object data;
	private String msg;
	private String code;
	
	public ResponseBody() {
		super();
	}

	public ResponseBody(Object data) {
		super();
		this.status = 1;
		this.data = data;
	}
	
	public ResponseBody(String code, String msg) {
		super();
		this.status = 0;
		this.msg = msg;
		this.code = code;
	}


	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}


	/**
	 * 成功消息
	 * 
	 * @param data
	 * @return
	 */
	public static ResponseBody success(Object data) {
		return new ResponseBody(data);
	}
	
	/**
	 * 失败消息
	 * 
	 * @param code
	 * @param msg
	 * @return
	 */
	public static ResponseBody fail(String code, String msg) {
		return new ResponseBody(code, msg);
	}
}
