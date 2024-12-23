package com.example.TFG_WebApp.Controllers;

import com.example.TFG_WebApp.Models.Discipline;
import com.example.TFG_WebApp.Models.Event;
import com.example.TFG_WebApp.Services.DisciplineService;
import com.example.TFG_WebApp.Services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/events/")
public class EventController {

    @Autowired
    private EventService eventService;
    @Autowired
    private DisciplineService disciplineService;

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

    @GetMapping("/calendario/")
    private String getCalendarEventes(Model model){

        return "events_calendar";
    }

    @GetMapping("/details/")
    private String getEventDetails(Model model, @RequestParam String id){

        return "event_details";
    }

    @GetMapping("/newEvent/")
    private String newEventForm(Model model, Pageable page){
        Page<Discipline> disciplines = disciplineService.getPaginatedAndFilteredDisciplines(null, null, null, page);
        model.addAttribute("disciplines", disciplines);
        model.addAttribute("direction", "/events/");
        return "newEventForm";
    }

    @PostMapping("/")
    public String addEvent(Model model, @RequestParam String name, @RequestParam LocalDate date, @RequestParam String link_map, @RequestParam String link_image, @RequestParam(required = false) String organizer){
        boolean organizerB;
        if (organizer != null){
            organizerB = Boolean.parseBoolean(organizer);
        } else {
            organizerB = false;
        }
        Event newEvent = eventService.createEvent(new Event(name, date,organizerB, Optional.of(link_image), link_map));
        model.addAttribute("isError", false);
        model.addAttribute("infoTitle", "Nuevo evento");
        model.addAttribute("returnDirection", "/events/?order=Proximo");
        model.addAttribute("acceptDirection", "/events/?order=Proximo");
        model.addAttribute("info","Se ha añadido con éxito el nuevo evento \"" + newEvent.getName() + "\".");
        return "infoScreen";
    }

    @GetMapping("/eliminar/{idEvent}/")
    public String eliminarEvent(@PathVariable Long idEvent, Model model){
        Optional<Event> deletEvent = eventService.deleteEvent(idEvent);
        model.addAttribute("infoTitle", "Eliminación de Evento");
        model.addAttribute("returnDirection", "/events/?order=Proximo");
        model.addAttribute("acceptDirection", "/events/?order=Proximo");
        if(deletEvent != null){
            model.addAttribute("isError", false);
            model.addAttribute("info","Se ha eliminado con éxito el país \"" + deletEvent.get().getName() + "\".");
        } else {
            model.addAttribute("isError", true);
            model.addAttribute("info","No se ha podido eliminar el país \"" + deletEvent.get().getName() + "\".");
        }
        return "infoScreen";

    }

}
