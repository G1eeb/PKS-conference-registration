package ru.mirea.conference.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ru.mirea.conference.model.Registration;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExcelExporter {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static void exportRegistrations(List<Registration> registrations, String filePath) {
        try (Workbook wb = new XSSFWorkbook();
             FileOutputStream out = new FileOutputStream(filePath)) {

            Sheet sheet = wb.createSheet("Registrations");
            String[] headers = {"ID", "Участник", "Конференция", "Статус", "Дата регистрации"};

            CellStyle headerStyle = wb.createCellStyle();
            Font bold = wb.createFont();
            bold.setBold(true);
            headerStyle.setFont(bold);

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = 1;
            for (Registration r : registrations) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(r.getId() != null ? r.getId() : 0);
                row.createCell(1).setCellValue(
                        r.getParticipantName() != null ? r.getParticipantName() : String.valueOf(r.getParticipantId()));
                row.createCell(2).setCellValue(
                        r.getConferenceTitle() != null ? r.getConferenceTitle() : String.valueOf(r.getConferenceId()));
                row.createCell(3).setCellValue(r.getStatus().name());
                row.createCell(4).setCellValue(
                        r.getRegisteredAt() != null ? r.getRegisteredAt().format(FMT) : "");
            }

            for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);
            wb.write(out);
            System.out.println("✔ Excel успешно сохранён: " + filePath);

        } catch (IOException e) {
            throw new RuntimeException("Ошибка экспорта в Excel: " + e.getMessage(), e);
        }
    }
}