package com.example.TFG_WebApp.Controllers;

import com.example.TFG_WebApp.Models.Coach;
import com.example.TFG_WebApp.Services.CoachService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/coachs/")
public class CoachController {

    @Autowired
    private CoachService coachService;

    @GetMapping("/")
    private String showCoachs(Model model, HttpServletRequest request, Pageable page) {
        Page<Coach> coachs;
        coachs = coachService.getCoaches(page);

        AthleteController.metod_aux(model, coachs.hasPrevious(), coachs.previousPageable(), coachs.hasNext(), coachs.nextPageable(), coachs.getNumber());
        model.addAttribute("coachs", coachs);

        return "clubmembers";
    }

    private String showCoach(Model model, @PathVariable String license){
        Coach coach = coachService.findCoachByLicense(license);

        if (coach != null){
            model.addAttribute("name", coach.getName());
            model.addAttribute("lastname", coach.getLastname());
            model.addAttribute("license", coach.getLicense());
            model.addAttribute("discipline", coach.getDiscipline());

            return "coach_profile";
        } else {
            model.addAttribute("infoTitle", "Error Buscando Al Entrenador");
            model.addAttribute("info", "Puede que halla sido borrado");
            model.addAttribute("showReturn", false);
            model.addAttribute("acceptDirection", "/coachs/");
            return "infoScreen";
        }
    }
}
