package cn.deal.component.utils;

import cn.deal.component.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelFileHelper {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private Workbook wb;
    private Sheet sheet;
    private Row row;

    public static final String ERROR_VALUE = "ERROR_VALUE";
    
    /**
     * 打开
     * 
     * @param filepath
     * @param is
     */
    public void open(String filepath, InputStream is) {
        if(filepath==null){
            return;
        }
        String ext = filepath.substring(filepath.lastIndexOf("."));
        try {
            if(".xls".equals(ext)){
                wb = new HSSFWorkbook(is);
            }else if(".xlsx".equals(ext)){
                wb = new XSSFWorkbook(is);
            }else{
                wb=null;
            }
        } catch (FileNotFoundException e) {
            logger.error("FileNotFoundException", e);
        } catch (IOException e) {
            logger.error("IOException", e);
        }
    }

    /**
     * 打开
     * 
     * @param filepath
     * @param is
     */
    public void open(String ext) {
        if("xls".equals(ext)){
            wb = new HSSFWorkbook();
        } else if("xlsx".equals(ext)){
            wb = new XSSFWorkbook();
        } else{
            wb=null;
        }
    }
    
    /**
     * 关闭
     * 
     */
    public void close() {
    	if (wb!=null) {
    		try {
				wb.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }
    
    /**
     * 读取Excel表格表头的内容
     * @return String 表头内容的数组
     */
    public String[] readExcelTitle() throws Exception{
        if(wb==null){
            throw new Exception("Workbook对象为空！");
        }
        sheet = wb.getSheetAt(0);
        row = sheet.getRow(0);
        // 标题总列数
        int colNum = row.getPhysicalNumberOfCells();

        String[] title = new String[colNum];
        for (int i = 0; i < colNum; i++) {
            // title[i] = getStringCellValue(row.getCell((short) i));
            title[i] = row.getCell(i).getStringCellValue();
        }
        return title;
    }

    /**
     * 读取Excel数据内容
     * @return Map 包含单元格数据内容的Map对象
     */
    public Map<Integer, Map<Integer, Object>> readExcelContent() {
        if (wb == null) {
            throw new BusinessException("workbook_is_null", "Workbook对象为空！");
        }
        Map<Integer, Map<Integer, Object>> content = new HashMap<>();

        sheet = getAccuracyContextNum(wb);
        // 得到总行数
        int rowNum = sheet.getLastRowNum();
        row = sheet.getRow(0);
        int colNum = row.getPhysicalNumberOfCells();

        // 正文内容应该从第二行开始,第一行为表头的标题
        for (int i = 1; i <= rowNum; i++) {
            row = sheet.getRow(i);
            if (row != null) {
                int j = 0;
                Map<Integer, Object> cellValue = new HashMap<>();
                while (j < colNum) {
                    Cell cell = row.getCell(j);

                    try {
                        Object obj = getCellFormatValue(cell);
                        cellValue.put(j, obj);
                    } catch (RuntimeException e) {
                        logger.warn("warn in read cell:" + cell, e);
                        cellValue.put(j, ERROR_VALUE);
                    }

                    j++;
                }
                content.put(i, cellValue);
            }
        }

        return content;
    }

    private Sheet getAccuracyContextNum(Workbook workbook) {
        // 取第一个sheet
        Sheet sheet = workbook.getSheetAt(0);
        try {
            // 删除空行
            for (int i = 0; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                // 删除空行
                if (this.isRowEmpty(row)) {
                    int lastRowNum = sheet.getLastRowNum();
                    if (i >= 0 && i < lastRowNum) {
                        // 将行号为i+1一直到行号为lastRowNum的单元格全部上移一行，以便删除i行
                        sheet.shiftRows(i + 1, lastRowNum, -1);
                    }
                    if (i == lastRowNum) {
                        sheet.removeRow(row);
                    }
                    i--;
                }
            }

        } catch (Exception e) {
            return sheet;
        }

        return sheet;
    }

    private boolean isRowEmpty(Row row) {
        for (int i = 0; i < row.getPhysicalNumberOfCells(); i++) {
            Object val = getCellFormatValue(row.getCell(i));
            if (StringUtils.isNotBlank(val.toString())) {
                return false;
            }
        }

        return true;
    }


    /**
     * 根据Cell类型设置数据
     * @param cell 单元格
     * @return Object
     */
    private Object getCellFormatValue(Cell cell) {
        Object cellvalue = StringUtils.EMPTY;

        if (cell != null) {
            // 判断当前Cell的Type
            switch (cell.getCellType()) {
                // 如果当前Cell的Type为NUMERIC
                case Cell.CELL_TYPE_NUMERIC:
                    // 判断当前的cell是否为Date
                    if (DateUtil.isCellDateFormatted(cell)) {
                        cellvalue = DateUtils.format(cell.getDateCellValue());
                    } else {
                        DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();
                        df.applyPattern("#.##");
                        cellvalue = df.format(cell.getNumericCellValue());
                    }
                    break;

                case Cell.CELL_TYPE_FORMULA: {
                    // 判断当前的cell是否为Date
                    if (DateUtil.isCellDateFormatted(cell)) {
                        // 如果是Date类型则，转化为Data格式
                        // data格式是带时分秒的：2013-7-10 0:00:00
                        // cellvalue = cell.getDateCellValue().toLocaleString();
                        // data格式是不带带时分秒的：2013-7-10
                        cellvalue = cell.getDateCellValue();
                    } else {
                        // 如果是纯数字
                        // 取得当前Cell的数值
                    	if(!String.valueOf(cell.getNumericCellValue()).contains("E")){
                    		cellvalue = String.valueOf(cell.getNumericCellValue());
                        } else {
                        	cellvalue = new DecimalFormat("#").format(cell.getNumericCellValue());
                        }
                    }
                    break;
                }

                // 如果当前Cell的Type为STRING
                case Cell.CELL_TYPE_STRING:
                    // 取得当前的Cell字符串
                    cellvalue = cell.getRichStringCellValue().getString();
                    break;

                default:// 默认的Cell值
                    break;
            }
        }

        return cellvalue;
    }

    /**
     * 写入标题
     * 
     * @param titles
     */
	public void writeExcelTitle(String[] titles) {
		this.sheet = wb.createSheet();
		
        //从第一行开始写入
		Row xssfRow = this.sheet.createRow(0); // 从第二行开始
  
        for (int j=0;j<titles.length;j++) {
        	Cell xssfCell = xssfRow.createCell(j); //创建单元格
            xssfCell.setCellValue(titles[j]); //设置单元格内容
        }
	}

	/**
	 * 写入内容
	 * 
	 * @param data
	 */
	public void writeExcelContent(List<Map<Integer, Object>> data) {
		for(int i=0; i<data.size(); i++) {
			Map<Integer, Object> rowData = data.get(i);
			Row xssfRow = this.sheet.createRow(i+1); // 从第二行开始
			
			for(Integer key: rowData.keySet()) {
				Cell xssfCell = xssfRow.createCell(key); //创建单元格
	            xssfCell.setCellValue(String.valueOf(rowData.get(key))); //设置单元格内容
			}
		}
	}

	/**
	 * 保存到文件
	 * 
	 * @param filePath
	 * @throws IOException
	 */
	public void saveTo(OutputStream fos) throws IOException {
		this.wb.write(fos);
	}
	
	/**
	 * 保存到文件
	 * 
	 * @param filePath
	 * @throws IOException
	 */
	public void saveTo(File file) throws IOException {
		FileOutputStream fos = null;
		
		try {
			fos = new FileOutputStream(file);
			this.saveTo(fos);
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}