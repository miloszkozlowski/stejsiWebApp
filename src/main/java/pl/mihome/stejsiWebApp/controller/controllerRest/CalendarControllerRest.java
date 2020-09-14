package pl.mihome.stejsiWebApp.controller.controllerRest;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import pl.mihome.stejsiWebApp.DTO.CalendarItem;
import pl.mihome.stejsiWebApp.logic.TreningService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin({"http://192.168.0.96:4200", "http://localhost:4200", "http://192.168.1.51:4200"})
@RequestMapping("/ngaccess/calendar")
@Secured("ROLE_STEJSI")
public class CalendarControllerRest {

    private final TreningService treningService;

    public CalendarControllerRest(TreningService treningService) {
        this.treningService = treningService;
    }

    @GetMapping
    public ResponseEntity<Map<LocalDateTime, List<CalendarItem>>> getWeeklyCalendar(@RequestParam(required = false, defaultValue = "0") int week) {
        return ResponseEntity.ok(treningService.getMapTrainingsWeekly(week));
    }

    @GetMapping("/canceled")
    public ResponseEntity<List<CalendarItem>> getAllCanceled() {
        return ResponseEntity.ok(treningService.getAllCanceled());
    }
}
