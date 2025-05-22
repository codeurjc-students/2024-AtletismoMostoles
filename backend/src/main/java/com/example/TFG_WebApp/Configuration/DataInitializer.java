package com.example.TFG_WebApp.Configuration;

import com.example.TFG_WebApp.Models.*;
import com.example.TFG_WebApp.Repositories.*;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;

@Component("configDataInitializer")
public class DataInitializer {

    private final AthleteRepository athleteRepository;
    private final CoachRepository coachRepository;
    private final DisciplineRepository disciplineRepository;
    private final EquipmentRepository equipmentRepository;
    private final EventRepository eventRepository;
    private final ResultsRepository resultRepository;

    public DataInitializer(AthleteRepository athleteRepository, CoachRepository coachRepository,
                           DisciplineRepository disciplineRepository, EquipmentRepository equipmentRepository,
                           EventRepository eventRepository, ResultsRepository resultRepository) {
        this.athleteRepository = athleteRepository;
        this.coachRepository = coachRepository;
        this.disciplineRepository = disciplineRepository;
        this.equipmentRepository = equipmentRepository;
        this.eventRepository = eventRepository;
        this.resultRepository = resultRepository;
    }

    @PostConstruct
    public void initData() {
        if (athleteRepository.count() == 0) {
            // Initialize Equipment
            List<Equipment> equipmentList = equipmentRepository.saveAll(Arrays.asList(
                    new Equipment("Cronómetro", "Nuevo Modelo"), new Equipment("Vallas", "Vallas con Altura de 76, 84, 91, 100, 106"), new Equipment("Pértiga" ," 4.00m 145lb 18flex 14tp"),
                    new Equipment("Colchoneta de salto", "Para altura"), new Equipment("Disco" ,"De 1kg"), new Equipment("Jabalina", "De 800g"),
                    new Equipment("Balón medicinal", "5Kg"), new Equipment("Cuerda de resistencia", "De 2m"), new Equipment("Conos" ," De Plastico, pequeños")
            ));

            // Initialize Disciplines
            List<Discipline> disciplines = disciplineRepository.saveAll(Arrays.asList(
                    new Discipline("Salto de Altura", "Lunes 16:00 - 18:00", Collections.singletonList(equipmentList.get(3)), "https://media.istockphoto.com/id/124053579/es/foto/atleta.jpg?s=612x612&w=0&k=20&c=hO1Xxq9kqyK2n6Tlc8rpkbmlHhFLytLSqh6ojUYmy3I="),
                    new Discipline("100m Vallas", "Martes 17:00 - 19:00", Collections.singletonList(equipmentList.get(1)), "https://media.istockphoto.com/id/515791560/es/foto/atleta-masculino-vallas-de-carrera.jpg?s=612x612&w=0&k=20&c=JHLwan5DT4huFnKPVBNgmVvndCa0NA9LuYoFuvSR8aY="),
                    new Discipline("Lanzamiento de Disco", "Miércoles 15:00 - 17:00", Collections.singletonList(equipmentList.get(4)), "https://media.istockphoto.com/id/1648117083/es/foto/atleta-femenina-girando-con-disco.jpg?s=612x612&w=0&k=20&c=uTIBIHflXEWraHeB9GGjAH8no0GwYfGQL-HxvyThspU="),
                    new Discipline("Jabalina", "Jueves 18:00 - 20:00", Collections.singletonList(equipmentList.get(5)), "https://media.istockphoto.com/id/515791354/es/foto/lanzador-de-jabalina.jpg?s=612x612&w=0&k=20&c=uCblB81UhV3Br98tMLxVxyNtOpMblE_MlIPY8ApKubA=" ),
                    new Discipline("Salto con Pértiga", "Viernes 16:00 - 18:00", Collections.singletonList(equipmentList.get(2)), "https://media.istockphoto.com/id/468046416/es/foto/salto-con-p%C3%A9rtiga-retroiluminaci%C3%B3n.jpg?s=612x612&w=0&k=20&c=pn6_zT8346AL7W1_SvYhxLwPz75FfoJ-pZTSvjsJ5dw="),
                    new Discipline("Resistencia", "Sábado 10:00 - 12:00", Collections.singletonList(equipmentList.get(7)), "https://wallpapers.com/images/featured/corredor-qam3p3y1s5qtykgy.jpg"),
                    new Discipline("Velocidad", "Domingo 10:00 - 12:00", Collections.singletonList(equipmentList.get(0)), "https://media.istockphoto.com/id/502290526/es/foto/elite-100-m-runner-sprints-de-cuadras-del-estadio-iluminadas.jpg?s=612x612&w=0&k=20&c=3yt9KLO1T6H-XjTVVbZ1k94et4nsnh8u2D_WSrsX3gc=")
            ));

            // Initialize Coaches
            List<Coach> coaches = coachRepository.saveAll(Arrays.asList(
                    new Coach("C1001", "José", "Martínez", Collections.singletonList(disciplines.get(0))),
                    new Coach("C1002", "Ana", "García", Collections.singletonList(disciplines.get(1))),
                    new Coach("C1003", "Pedro", "Fernández", Collections.singletonList(disciplines.get(2))),
                    new Coach("C1004", "Luis", "González", Collections.singletonList(disciplines.get(3))),
                    new Coach("C1005", "Marta", "López", Collections.singletonList(disciplines.get(4))),
                    new Coach("C1006", "Clara", "Díaz", Collections.singletonList(disciplines.get(5)))
            ));

            // Initialize Athletes
            List<Athlete> athletes = athleteRepository.saveAll(Arrays.asList(
                    new Athlete("A2001", "Carlos", "Pérez", LocalDate.parse("2000-05-10"), coaches.get(0), new HashSet<>(List.of(disciplines.get(0), disciplines.get(1)))),
                    new Athlete("A2002", "Lucía", "Ramírez", LocalDate.parse("1998-08-22"), coaches.get(1), new HashSet<>(List.of(disciplines.get(1), disciplines.get(2)))),
                    new Athlete("A2003", "Sofía", "Hernández", LocalDate.parse("2001-03-14"), coaches.get(2), new HashSet<>(List.of(disciplines.get(2), disciplines.get(3)))),
                    new Athlete("A2004", "David", "Moreno", LocalDate.parse("1999-07-30"), coaches.get(3), new HashSet<>( List.of(disciplines.get(3), disciplines.get(4)))),
                    new Athlete("A2005", "Elena", "Torres", LocalDate.parse("2002-09-12"), coaches.get(4), new HashSet<>(List.of(disciplines.get(4), disciplines.get(5))))
            ));

            // Initialize Events
            List<Event> events = eventRepository.saveAll(Arrays.asList(
                    new Event("Campeonato Nacional", LocalDate.parse("2025-06-10"), "", "https://www.atletismomadrid.com/images/stories/fam.jpg", true, disciplines.subList(0, 3)),
                    new Event("Maratón Ciudadano", LocalDate.parse("2025-05-01"), "", "https://www.atletismomadrid.com/images/stories/fam.jpg", false, disciplines.subList(3, 6)),
                    new Event("Copa Juvenil", LocalDate.parse("2025-07-20"), "", "https://www.atletismomadrid.com/images/stories/fam.jpg", true, disciplines.subList(2, 5))
            ));

            // Initialize Results
            resultRepository.saveAll(Arrays.asList(
                    new Results(athletes.get(0), disciplines.get(0), events.get(0), 2.15),
                    new Results(athletes.get(1), disciplines.get(1), events.get(1), 13.2),
                    new Results(athletes.get(2), disciplines.get(2), events.get(2), 45.6),
                    new Results(athletes.get(3), disciplines.get(3), events.get(1), 5.7),
                    new Results(athletes.get(4), disciplines.get(4), events.get(0), 6.1)
            ));
        }
    }
}
