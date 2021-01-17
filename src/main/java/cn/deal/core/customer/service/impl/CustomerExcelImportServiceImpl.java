package cn.deal.core.customer.service.impl;

import cn.deal.component.AsyncTaskComponent;
import cn.deal.component.FileComponent;
import cn.deal.component.domain.AsyncTask;
import cn.deal.component.domain.FileUploaded;
import cn.deal.component.exception.BusinessException;
import cn.deal.component.utils.ExcelFileHelper;
import cn.deal.component.utils.JsonUtil;
import cn.deal.component.utils.MapUtils;
import cn.deal.core.customer.domain.Customer;
import cn.deal.core.customer.domain.CustomerOpt;
import cn.deal.core.customer.domain.vo.CustomerImportVo;
import cn.deal.core.customer.engine.CustomerEngine;
import cn.deal.core.customer.service.CustomerExcelImportService;
import cn.deal.core.meta.domain.CustomerMetaData;
import cn.deal.core.meta.service.CustomerMetaDataService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static cn.deal.component.utils.ExcelFileHelper.ERROR_VALUE;

@Service
public class CustomerExcelImportServiceImpl implements CustomerExcelImportService {

	private Logger logger = LoggerFactory.getLogger(CustomerExcelImportServiceImpl.class);

	private static final String REQUIRED_SUFFIX = "(必填)";

    @Autowired
    private CustomerEngine customerEngine;
    
	@Autowired
	private CustomerMetaDataService customerMetaDataService;
	
	@Autowired
	private FileComponent fileComponent;

	@Autowired
	private AsyncTaskComponent asyncTaskComponent;
	
	/**
	 * 默认导入字段
	 * 
	 */
	private static Map<Integer, String> DefaultMappings = new HashMap<>();

	static {
		DefaultMappings.put(0, "name");
		DefaultMappings.put(1, "company");
		DefaultMappings.put(2, "title");
		DefaultMappings.put(3, "phone");
		DefaultMappings.put(4, "email");
	}


	@Override
	public ImportResult batchAddCustomerFromExcelFile(String appId, Resource excelRes, Map<String, Object> opts) {
		CustomerImportVo vo = buildCustomerImportData(appId, excelRes);
		logger.info("batchAddCustomerFromExcelFile.vo: {}", vo);

		return handleImportCustomer(appId, opts, vo.getTitles(), vo.getContents());
	}

	@Override
	public CustomerImportVo buildCustomerImportData(String appId, Resource excelRes) {
		String[] titles;
		Map<Integer, Map<Integer, Object>> contents;

		// 加载Excel数据
		try {
			ExcelFileHelper excel = new ExcelFileHelper();
			excel.open(excelRes.getFilename(), excelRes.getInputStream());
			titles = excel.readExcelTitle();
			contents = excel.readExcelContent();
			Assert.isTrue(!contents.isEmpty(), "excel无内容");

		} catch (Exception e) {
			throw new BusinessException("read_excel_failed", e.getMessage());
		}

		return CustomerImportVo.builder()
				.appId(appId).titles(titles).contents(contents)
				.build();
	}

	@Override
	public ImportResult handleImportCustomer(String appId, Map<String, Object> opts, String[] titles, Map<Integer, Map<Integer, Object>> contents) {
		try {
			// 加载客户元数据
			List<CustomerMetaData> metaDatas = customerMetaDataService.getCustomerMetas(appId);
			metaDatas.forEach(meta -> {
				if (meta.getRequired()) {
					meta.setTitle(meta.getTitle() + REQUIRED_SUFFIX);
				}
			});

			// 选项
			Map<String, Object> cfg = MapUtils.from(new Object[]{
					CustomerOpt.CREATE_WAY, Customer.CreateWay.BATCH_IMPORT.getValue(),
			});

			ImportResult result = new ImportResult();
			List<Map<Integer, Object>> errors = Lists.newArrayList();

			// 创建客户
			for (int row = 0; row < contents.keySet().size(); row++) {
				// 从第一行开始
				Map<Integer, Object> rows = contents.get(row + 1);

				try {
					if (hasData(rows)) {
						Map<String, String> data = readData(metaDatas, titles, rows);
						data.put(CustomerOpt.CREATE_WAY, String.valueOf(Customer.CreateWay.BATCH_IMPORT.getValue()));
						data.put(CustomerOpt.CUSTOMER_GROUP_ID, String.valueOf(opts.get("customerGroupId")));

						if (MapUtils.equals(opts, "isBelongedMe", "1")) {
							cfg.put(CustomerOpt.KUICK_USER_ID, opts.get("kuickUserId"));
						}

						customerEngine.handleCreate(appId, data, cfg);
					}
				} catch (BusinessException e) {
					rows.put(rows.size(), e.getMessage());
					errors.add(rows);
				}
			}

			// 计算导入结果
			if (errors.size() > 0) {
				result.setErrNumber(errors.size());
				String accessURL = uploadErrorDataToCloud(appId, titles, errors, opts);
				result.setAccessURL(accessURL);
			}
			return result;

		} catch (Exception e1) {
			throw new BusinessException("import_error", e1.getMessage());
		}
	}

	@Async
	@Override
	public ImportResult handleImportCustomer(String appId, Map<String, Object> opts, String[] titles, Map<Integer, Map<Integer, Object>> contents, AsyncTask task) {
		try {
			// 加载客户元数据
			List<CustomerMetaData> metaDatas = customerMetaDataService.getCustomerMetas(appId);
			metaDatas.forEach(meta -> {
				if (meta.getRequired()) {
					meta.setTitle(meta.getTitle() + REQUIRED_SUFFIX);
				}
			});

			// 选项
			Map<String, Object> cfg = MapUtils.from(new Object[]{
					CustomerOpt.CREATE_WAY, Customer.CreateWay.BATCH_IMPORT.getValue(),
			});

			ImportResult result = new ImportResult();
			List<Map<Integer, Object>> errors = Lists.newArrayList();

			// 创建客户
			for (int row = 0; row < contents.keySet().size(); row++) {
				// 从第一行开始
				Map<Integer, Object> rows = contents.get(row + 1);

				try {
					if (hasData(rows)) {
						Map<String, String> data = readData(metaDatas, titles, rows);
						data.put(CustomerOpt.CREATE_WAY, String.valueOf(Customer.CreateWay.BATCH_IMPORT.getValue()));
						data.put(CustomerOpt.CUSTOMER_GROUP_ID, String.valueOf(opts.get("customerGroupId")));

						if (MapUtils.equals(opts, "isBelongedMe", "1")) {
							cfg.put(CustomerOpt.KUICK_USER_ID, opts.get("kuickUserId"));
						}

						customerEngine.handleCreate(appId, data, cfg);
					}
				} catch (BusinessException e) {
					rows.put(rows.size(), e.getMessage());
					errors.add(rows);
				}

				if (row % 100 == 0) {
					asyncTaskComponent.editTaskById(task.getId(), AsyncTask.Status.DOING.getVal(), AsyncTask.Text.HANDLING.getVal(), row, "处理中");
				}
			}

			// 计算导入结果
			if (errors.size() > 0) {
				result.setErrNumber(errors.size());
				String accessURL = uploadErrorDataToCloud(appId, titles, errors, opts);
				result.setAccessURL(accessURL);
				asyncTaskComponent.editTaskById(task.getId(), AsyncTask.Status.ERROR.getVal(), AsyncTask.Text.FAILED.getVal(), task.getProgressCount(), JsonUtil.toJson(result));
			} else {
				asyncTaskComponent.editTaskById(task.getId(), AsyncTask.Status.FINISHED.getVal(), AsyncTask.Text.COMPLETE.getVal(), task.getProgressCount(), "处理完成");
			}

			return result;

		} catch (Exception e1) {
			asyncTaskComponent.editTaskById(task.getId(), AsyncTask.Status.ERROR.getVal(), AsyncTask.Text.FAILED.getVal(), task.getProgressCount(), "处理失败");
			throw new BusinessException("import_error", e1.getMessage());
		}
	}

	/**
	 * 上传错误数据到云端
	 * 
	 * @param errorList
	 * @return
	 */
	public String uploadErrorDataToCloud(String appId, String[] titles, List<Map<Integer, Object>> errorList, Map<String, Object> opts) {
		String tempPath = System.getProperty("java.io.tmpdir") + File.separator + UUID.randomUUID() + "." + "xlsx";
		File file = new File(tempPath);
 
		// 导出为Excel
		try {
			ExcelFileHelper excel = new ExcelFileHelper();
			excel.open("xlsx");
			excel.writeExcelTitle(titles);
			excel.writeExcelContent(errorList);
			excel.saveTo(file);
		}  catch (IOException e) {
			logger.error("error in save excel:", e);
			throw new BusinessException("excel_write_error", "生成导入错误Excel时错误！");
		}  
		
		// 将Excel上传到云端
		try {
			String kuickUserId = (String)opts.get("kuickUserId");
			FileUploaded uploaded = fileComponent.uploadFileByKuickUser(appId, kuickUserId, file);
			return uploaded.getUrl();
		} catch (Exception e) {
			logger.error("error in upload excel File:", e);
			throw new BusinessException("upload_file_error", "上传导入错误Excel时错误！");
		}
	}

	/**
	 * 读取数据
	 * 
	 * @param metaDatas
	 * @param titles
	 * @param rowDatas
	 * @return
	 */
	private Map<String, String> readData(List<CustomerMetaData> metaDatas, String[] titles, Map<Integer, Object> rowDatas) {
		Map<String, String> data = Maps.newHashMapWithExpectedSize(rowDatas.size());

		// 根据客户元数据的标题，映射数据
		if (metaDatas != null && metaDatas.size() > 0) {
			for (CustomerMetaData meta : metaDatas) {
				String title = meta.getTitle();
				int col = ArrayUtils.indexOf(titles, title);
				if (col >= 0) {
					String value = rowDatas.get(col) + "";
					if (StringUtils.isNotBlank(value)) {
						if (StringUtils.equals(value, ERROR_VALUE)) {
							throw new BusinessException("error_value", "excel格式异常");
						}

						if (StringUtils.equals(meta.getName(), "province")) {
							String[] pcc = StringUtils.split(value, "-");
							if (pcc.length == 3) {
								data.put("province", pcc[0]);
								data.put("city", pcc[1]);
								data.put("county", pcc[2]);
							} else {
								data.put(meta.getName(), value);
							}

						} else {
						 	data.put(meta.getName(), value);
						}
					}
				}
			}
		}

		return data;
	}

	/**
	 * 是否有数据
	 * 
	 * @param rowDatas
	 * @return
	 */
	private boolean hasData(Map<Integer, Object> rowDatas) {
		if (rowDatas != null && !rowDatas.isEmpty()) {
			for (int i : DefaultMappings.keySet()) {
				String value = String.valueOf(rowDatas.get(i));

				if (StringUtils.isNotBlank(value)) {
					return true;
				} else if (StringUtils.equals(value, ERROR_VALUE)) {
					throw new BusinessException("error_value", "");
				}
			}
		}

		return false;
	}
}
