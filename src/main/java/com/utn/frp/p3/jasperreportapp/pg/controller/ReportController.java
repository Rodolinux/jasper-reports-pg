package com.utn.frp.p3.jasperreportapp.pg.controller;

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

import com.utn.frp.p3.jasperreportapp.pg.model.Actor;
import com.utn.frp.p3.jasperreportapp.pg.repository.ActorRepository;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller //podría emplear RestController, pero Controller da más control aunque exige más código para generar el pdf
public class ReportController {

    private final ActorRepository actorRepository; //repositorio JPA, viene de ActorRepository.java

    public ReportController(ActorRepository actorRepository) {
        this.actorRepository = actorRepository; //inyectamos el repositorio en el constructor
    }

 

    @GetMapping("/generate-report") //llegamos aquí desde el botón que está dentro de un form, que apunta a esta url
    //la respuesta de este metodo es una lista de bytes, que es el pdf
    public ResponseEntity<byte[]> generateReport() throws Exception {
        List<Actor> actors = actorRepository.findAll(); //actor es una lista de objetos de la clase Actor; actorRepository accede a la db otorgando métodos JPA
        
        //se declara el datasource de actors
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(actors); 
        
        //cargar el template del reporte
        InputStream jasperStream = new ClassPathResource("reports/Simple_Blue.jrxml").getInputStream();
        
        //compilar el reporte jrxml en un objeto JasperReport. En proyectos grandes, es conveniente precompilar el reporte como .jasper
        JasperReport jasperReport = JasperCompileManager.compileReport(jasperStream);
        
        //Configurar los parámetros para el informe
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("CreatedBy", "Spring Boot App");
    
        //llenar el informe, combinando el informe compilado, los parámetros y el datasource
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        
        //exportar el informe a PDF
        byte[] reportBytes = JasperExportManager.exportReportToPdf(jasperPrint);
        
        //configuración de la respuesta http
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("inline", "actor_report.pdf");
        
        //devolver la respuesta
        return ResponseEntity.ok().headers(headers).body(reportBytes);
    }
    
 
    
}