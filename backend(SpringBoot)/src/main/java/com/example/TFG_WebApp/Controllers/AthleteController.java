package com.example.TFG_WebApp.Controllers;

import com.example.TFG_WebApp.Models.Athlete;
import com.example.TFG_WebApp.Services.AthleteService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/athletes/")
public class AthleteController {

    @Autowired
    private AthleteService athleteService;

    @GetMapping("/")
    private String showAthlete(Model model, Pageable page) {

        Page<Athlete> athletes;
        athletes = athleteService.getPaginatedAndFilteredAthletes(null, null,null,null,null,page);

        metod_aux(model, athletes.hasPrevious(), athletes.previousPageable(), athletes.hasNext(), athletes.nextPageable(), athletes.getNumber());
        model.addAttribute("athletes", athletes);
        return "ranking";
    }

    static void metod_aux(Model model, boolean b, Pageable pageable, boolean b2, Pageable pageable2, int number) {
        model.addAttribute("previousPageNumber", b ? pageable.getPageNumber() : "");
        model.addAttribute("nextPageNumber", b2 ? pageable2.getPageNumber() : "");
        model.addAttribute("current", number + 1);
        model.addAttribute("hasPrevious", b);
        model.addAttribute("hasNext", b2);
    }
}
