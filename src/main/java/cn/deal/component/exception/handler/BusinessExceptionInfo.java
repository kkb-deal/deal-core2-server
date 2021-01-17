package cn.deal.component.exception.handler;

/**
 * 异常信息
 */
public class BusinessExceptionInfo {
	
	private long timestamp;
	private int status;
	private String error;
	private String code;
	private String message;
	private String path;
	
	public BusinessExceptionInfo(){};
	
	public BusinessExceptionInfo(long timestamp, int status, String code, String message, String path){
		this.timestamp = timestamp;
		this.status = status;
		this.error = code;
		this.code = code;
		this.message = message;
		this.path = path;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	
}

