package com.example.TFG_WebApp.Controllers;

import com.example.TFG_WebApp.Services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/events/")
public class EventController {

    @Autowired
    private EventService eventService;

    @GetMapping("/")
    private String getEvents(Model model, @RequestParam String order){

        if(order.equals("Organizador")){
            model.addAttribute("events", eventService.getEventeOrganized(true));
        }else if(order.equals("Participante")){
            model.addAttribute("events", eventService.getEventeOrganized(false));
        }else{
            model.addAttribute("events", eventService.getNextEvents());
        }
        model.addAttribute("defaultOrganizador", order.equals("Organizador"));
        model.addAttribute("defaultParticipantes",  order.equals("Participante"));
        model.addAttribute("defaultProximos",  order.equals("Proximos"));
        return "events";
    }
}
