package cn.deal.core.customer.service;

import cn.deal.component.domain.AsyncTask;
import cn.deal.core.customer.domain.Customer;
import cn.deal.core.customer.domain.vo.CustomerImportVo;
import org.springframework.core.io.Resource;

import java.util.List;
import java.util.Map;

/**
 * 导入客户服务
 */
public interface CustomerExcelImportService {

	CustomerImportVo buildCustomerImportData(String appId, Resource excelRes);

	ImportResult handleImportCustomer(String appId, Map<String, Object> opts, String[] titles, Map<Integer, Map<Integer, Object>> contents);

	ImportResult handleImportCustomer(String appId, Map<String, Object> opts, String[] titles, Map<Integer, Map<Integer, Object>> contents, AsyncTask task);

	/**
	 * 导入结果
	 */
	public static class ImportResult {
		private List<Customer> customerList;
		private String accessURL;
		private Integer errNumber;
		
		
		public List<Customer> getCustomerList() {
			return customerList;
		}
		public void setCustomerList(List<Customer> customerList) {
			this.customerList = customerList;
		}
		public String getAccessURL() {
			return accessURL;
		}
		public void setAccessURL(String accessURL) {
			this.accessURL = accessURL;
		}
		public Integer getErrNumber() {
			return errNumber;
		}
		public void setErrNumber(Integer errNumber) {
			this.errNumber = errNumber;
		}
	}
	
	
	/**
	 * 批量导入客户
	 * 
	 * @param appId
	 * @param excelRes
	 * @param opts
	 */
	ImportResult batchAddCustomerFromExcelFile(String appId, Resource excelRes, Map<String, Object> opts);
}
