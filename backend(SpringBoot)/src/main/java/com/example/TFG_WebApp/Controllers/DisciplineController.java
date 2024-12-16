package com.example.TFG_WebApp.Controllers;

import com.example.TFG_WebApp.Services.DisciplineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/disciplines/")
public class DisciplineController {

    @Autowired DisciplineService disciplineService;

    
}
