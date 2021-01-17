package cn.deal.component.utils;

import cn.deal.component.exception.BusinessException;
import com.google.common.collect.Maps;
import net.sf.jxls.transformer.XLSTransformer;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.Base64Utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * excel工具类
 */
public class ExcelUtils {

    private static final String PARAM_ERROR_CODE = "param_error";

    /**
     * xls模板路径+文件名
     */
    private static final String CUSTOM_TITLE_TEMPLATE = "/excel/upload_customer_template.xls";

    /**
     * @description 转Workbook
     **/
    private static Workbook toWorkbook(InputStream inputStream, Map<String, Object> beanParams) throws InvalidFormatException {
        // 创建XLSTransformer对象
        XLSTransformer transformer = new XLSTransformer();
        return transformer.transformXLS(inputStream, beanParams);
    }

    public static void expExcel(String fileName, List<String> headers, HttpServletRequest request, HttpServletResponse response) {
        if(headers == null || headers.size() <= 0){
            throw new BusinessException(PARAM_ERROR_CODE, "自定义导出excel，headers为空");
        }
        ClassPathResource resource = new ClassPathResource(CUSTOM_TITLE_TEMPLATE);
        fileName += ".xlsx";
        OutputStream outputStream = null;

        try {
            InputStream inputStream = resource.getInputStream();
            Map<String, Object> map = Maps.newHashMapWithExpectedSize(headers.size());
            map.put("headers", headers);

            String agent = request.getHeader("USER-AGENT");
            if (agent != null && agent.toLowerCase().indexOf("firefox") > 0) {
                fileName = "=?UTF-8?B?" + (Base64Utils.encodeToString(fileName.getBytes(StandardCharsets.UTF_8))) + "?=";
            } else {
                fileName = java.net.URLEncoder.encode(fileName, "UTF-8");
            }

            response.setHeader("Content-Type", "application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);

            Workbook workbook = toWorkbook(inputStream, map);
            outputStream = response.getOutputStream();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            workbook.write(byteArrayOutputStream);

            response.setHeader("Content-Length", byteArrayOutputStream.size() + "");
            workbook.write(response.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.flush();
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
