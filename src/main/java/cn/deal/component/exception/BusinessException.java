package cn.deal.component.exception;

/**
 * 业务异常
 *
 */
public class BusinessException extends RuntimeException {

	private static final long serialVersionUID = 154654568878L;
	
	private String code;
	
	public BusinessException(){}
	
	public BusinessException(String code, String msg){
		super(msg);
		this.code = code;
	}

	public BusinessException(String code, String msg, Throwable cause) {
		super(msg, cause);
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public String toString() {
		return "BusinessException [code=" + code + ", getMessage()=" + getMessage() + "]";
	}
}
