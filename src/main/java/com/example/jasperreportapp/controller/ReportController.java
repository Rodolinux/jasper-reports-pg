package com.example.jasperreportapp.controller;

import com.example.jasperreportapp.model.Actor;
import com.example.jasperreportapp.repository.ActorRepository;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ReportController {

    private final ActorRepository actorRepository;

    public ReportController(ActorRepository actorRepository) {
        this.actorRepository = actorRepository;
    }

 

    @GetMapping("/generate-report")
    public ResponseEntity<byte[]> generateReport() throws Exception {
        List<Actor> actors = actorRepository.findAll();

        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(actors);

        InputStream jasperStream = new ClassPathResource("reports/actor_report.jrxml").getInputStream();
        JasperReport jasperReport = JasperCompileManager.compileReport(jasperStream);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("CreatedBy", "Spring Boot App");

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        byte[] reportBytes = JasperExportManager.exportReportToPdf(jasperPrint);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("inline", "actor_report.pdf");

        return ResponseEntity.ok().headers(headers).body(reportBytes);
    }
    
 
    
}