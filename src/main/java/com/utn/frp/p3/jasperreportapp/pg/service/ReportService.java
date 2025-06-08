package com.utn.frp.p3.jasperreportapp.pg.service;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

@Service
public class ReportService {

    @Autowired
    private DataSource dataSource; // Inyecta el DataSource configurado por Spring Boot

    public byte[] generateActorReportPdf() throws JRException {
        try (Connection connection = dataSource.getConnection()) {
            // Cargar el archivo JRXML (el diseño del reporte)
            InputStream employeeReportStream = new ClassPathResource("reports/actor_report.jrxml").getInputStream();
            JasperReport jasperReport = JasperCompileManager.compileReport(employeeReportStream);

            // Parámetros del reporte (en este caso, ninguno adicional)
            Map<String, Object> parameters = new HashMap<>();

            // Llenar el reporte con datos de la base de datos
            // JasperReports ejecuta la consulta SQL definida en el JRXML usando la conexión
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, connection);

            // Exportar el reporte a PDF
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            JRPdfExporter exporter = new JRPdfExporter();
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));

            SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
            exporter.setConfiguration(configuration);
            exporter.exportReport();

            return outputStream.toByteArray();

        } catch (Exception e) {
            throw new JRException("Error al generar el reporte de actores", e);
        }
    }
}