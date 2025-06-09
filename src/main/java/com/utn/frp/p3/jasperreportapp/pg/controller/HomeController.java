package com.utn.frp.p3.jasperreportapp.pg.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/") //si llega una petición a / entonces
    public String showIndex() {
        return "index"; // nombre del archivo html a devolver, sin extensión, que contiene el botón para generar el reporte
    }
}