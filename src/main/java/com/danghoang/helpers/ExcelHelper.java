package com.danghoang.helpers;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Đọc Excel theo TÊN CỘT (dòng đầu là tiêu đề). Không giữ trạng thái nên DataProvider song song gọi được. */
public final class ExcelHelper {

    private ExcelHelper() {}

    public static List<Map<String, String>> readRows(String resourcePath, String sheetName) {
        try (InputStream in = ExcelHelper.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (in == null) {
                throw new IllegalStateException("Không tìm thấy file Excel " + resourcePath);
            }
            try (Workbook workbook = new XSSFWorkbook(in)) {
                Sheet sheet = workbook.getSheet(sheetName);
                if (sheet == null) {
                    throw new IllegalStateException(resourcePath + " không có sheet " + sheetName);
                }
                return toRows(sheet);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Không đọc được " + resourcePath, e);
        }
    }

    private static List<Map<String, String>> toRows(Sheet sheet) {
        DataFormatter formatter = new DataFormatter();
        Row header = sheet.getRow(0);
        List<Map<String, String>> rows = new ArrayList<>();
        for (int r = 1; r <= sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);
            if (row == null) {
                continue;
            }
            Map<String, String> values = new LinkedHashMap<>();
            for (int c = 0; c < header.getLastCellNum(); c++) {
                values.put(formatter.formatCellValue(header.getCell(c)).trim(), formatter.formatCellValue(row.getCell(c)));
            }
            if (values.values().stream().anyMatch(value -> !value.isEmpty())) {
                rows.add(values);
            }
        }
        return rows;
    }
}
