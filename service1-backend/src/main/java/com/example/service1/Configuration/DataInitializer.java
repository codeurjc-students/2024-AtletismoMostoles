package com.example.service1.Configuration;

import com.example.service1.Entities.Athlete;
import com.example.service1.Entities.Coach;
import com.example.service1.Entities.Discipline;
import com.example.service1.Entities.Equipment;
import com.example.service1.Repositories.AthleteRepository;
import com.example.service1.Repositories.CoachRepository;
import com.example.service1.Repositories.DisciplineRepository;
import com.example.service1.Repositories.EquipmentRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;

@Component
public class DataInitializer {

    private final AthleteRepository athleteRepository;
    private final CoachRepository coachRepository;
    private final DisciplineRepository disciplineRepository;
    private final EquipmentRepository equipmentRepository;

    public DataInitializer(AthleteRepository athleteRepository,
                           CoachRepository coachRepository,
                           DisciplineRepository disciplineRepository,
                           EquipmentRepository equipmentRepository) {
        this.athleteRepository = athleteRepository;
        this.coachRepository = coachRepository;
        this.disciplineRepository = disciplineRepository;
        this.equipmentRepository = equipmentRepository;
    }

    @PostConstruct
    public void initData() {
        if (athleteRepository.count() == 0 && coachRepository.count() == 0) {
            // Equipamiento
            Equipment cronometro = new Equipment("Cronómetro", "Modelo Pro");
            Equipment disco = new Equipment("Disco 1kg", "Disco oficial competición");
            Equipment vallas = new Equipment("Vallas", "Vallas 100m");
            Equipment jabalina = new Equipment("Jabalina", "800g profesional");
            Equipment pertiga = new Equipment("Pértiga", "4.20m fibra");

            equipmentRepository.saveAll(List.of(cronometro, disco, vallas, jabalina, pertiga));

            // Disciplinas
            Discipline altura = new Discipline("Salto de Altura", "Lunes 16-18", List.of(cronometro),"https://media.istockphoto.com/id/124053579/es/foto/atleta.jpg?s=612x612&w=0&k=20&c=hO1Xxq9kqyK2n6Tlc8rpkbmlHhFLytLSqh6ojUYmy3I=");
            Discipline vallas100m = new Discipline("100m Vallas", "Martes 17-19", List.of(vallas),"https://media.istockphoto.com/id/515791560/es/foto/atleta-masculino-vallas-de-carrera.jpg?s=612x612&w=0&k=20&c=JHLwan5DT4huFnKPVBNgmVvndCa0NA9LuYoFuvSR8aY=");
            Discipline discoLan = new Discipline("Lanzamiento de Disco", "Miércoles 15-17", List.of(disco),"https://media.istockphoto.com/id/1648117083/es/foto/atleta-femenina-girando-con-disco.jpg?s=612x612&w=0&k=20&c=uTIBIHflXEWraHeB9GGjAH8no0GwYfGQL-HxvyThspU=");
            Discipline jabalinaLan = new Discipline("Jabalina", "Jueves 18-20", List.of(jabalina),"https://media.istockphoto.com/id/515791354/es/foto/lanzador-de-jabalina.jpg?s=612x612&w=0&k=20&c=uCblB81UhV3Br98tMLxVxyNtOpMblE_MlIPY8ApKubA=");
            Discipline pertigaSalto = new Discipline("Salto con Pértiga", "Viernes 10-12", List.of(pertiga),"https://media.istockphoto.com/id/468046416/es/foto/salto-con-p%C3%A9rtiga-retroiluminaci%C3%B3n.jpg?s=612x612&w=0&k=20&c=pn6_zT8346AL7W1_SvYhxLwPz75FfoJ-pZTSvjsJ5dw=");

            disciplineRepository.saveAll(List.of(altura, vallas100m, discoLan, jabalinaLan, pertigaSalto));

            // Entrenadores
            Coach coach1 = new Coach("1001", "José", "Martínez", List.of(altura));
            Coach coach2 = new Coach("1002", "Ana", "García", List.of(vallas100m));
            Coach coach3 = new Coach("1003", "Pedro", "López", List.of(discoLan));
            Coach coach4 = new Coach("1004", "Marta", "Fernández", List.of(jabalinaLan));
            Coach coach5 = new Coach("1005", "Luis", "Díaz", List.of(pertigaSalto));
            coachRepository.saveAll(List.of(coach1, coach2, coach3, coach4, coach5));

            // Atletas
            Athlete atleta1 = new Athlete("A001", "Carlos", "Pérez", LocalDate.of(2000, 5, 10), coach1, new HashSet<>(List.of(altura, vallas100m)));
            Athlete atleta2 = new Athlete("A002", "Lucía", "Torres", LocalDate.of(1999, 8, 22), coach2, new HashSet<>(List.of(vallas100m, discoLan)));
            Athlete atleta3 = new Athlete("A003", "David", "Ruiz", LocalDate.of(2001, 3, 14), coach3, new HashSet<>(List.of(discoLan, jabalinaLan)));
            Athlete atleta4 = new Athlete("A004", "Sofía", "Moreno", LocalDate.of(2002, 2, 3), coach4, new HashSet<>(List.of(jabalinaLan, pertigaSalto)));
            Athlete atleta5 = new Athlete("A005", "Andrés", "Vega", LocalDate.of(2000, 12, 21), coach5, new HashSet<>(List.of(altura, pertigaSalto)));
            athleteRepository.saveAll(List.of(atleta1, atleta2, atleta3, atleta4, atleta5));
        }
    }
}
