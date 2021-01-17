package cn.deal.component.exception.handler;

import cn.deal.component.exception.BusinessException;
import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;

/**
 * 
 * 全局异常处理，将异常信息封装为网关能处理的形式。
 */
@ControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
	
	/**
	 * 业务异常处理函数
	 * 
	 * @param req
	 * @param e
	 * @return BusinessExceptionInfo：返回信息可根据需求自定义
	 */
	@ResponseBody
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(value= BusinessException.class)
	BusinessExceptionInfo customExceptionHandler(HttpServletRequest req, Exception e){
		logger.warn("Exception in：" + req.getRequestURI(), e);
		
		BusinessException ce = (BusinessException)e;
		long timestamp = System.currentTimeMillis();
		return new BusinessExceptionInfo(timestamp, 500, ce.getCode(), ce.getMessage(), req.getRequestURI());
	}

	/**
	 * 数据库异常处理函数
	 *
	 * @param req
	 * @param e
	 * @return BusinessExceptionInfo：返回信息可根据需求自定义
	 */
	@ResponseBody
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(value= SQLException.class)
	BusinessExceptionInfo customSqlExceptionHandler(HttpServletRequest req, Exception e){
		logger.warn("Exception in：" + req.getRequestURI(), e);
		long timestamp = System.currentTimeMillis();

		if(e instanceof MySQLIntegrityConstraintViolationException) {
			return new BusinessExceptionInfo(timestamp, 500, "data_validate_error", e.getMessage(), req.getRequestURI());
		}

		return new BusinessExceptionInfo(timestamp, 500, "sql_error", e.getMessage(), req.getRequestURI());
	}
	
}
