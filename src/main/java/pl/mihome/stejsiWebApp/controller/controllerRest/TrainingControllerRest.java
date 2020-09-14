package pl.mihome.stejsiWebApp.controller.controllerRest;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import pl.mihome.stejsiWebApp.DTO.TreningReadModel;
import pl.mihome.stejsiWebApp.logic.TreningService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@CrossOrigin({"http://192.168.0.96:4200", "http://localhost:4200", "http://192.168.1.51:4200"})
@RequestMapping("/ngaccess/trainings")
@Secured("ROLE_STEJSI")
public class TrainingControllerRest {

    private final TreningService treningService;

    public TrainingControllerRest(TreningService treningService) {
        this.treningService = treningService;
    }

    @PutMapping("/{tid}")
    public ResponseEntity<TreningReadModel> updateTraining(@PathVariable Long tid, @RequestBody @Valid TrainingUpdateDTO req) {
        var t = treningService.scheduleWithLocalDateTime(tid, req.getDateTime(), Optional.ofNullable(req.getLocationId()));
        return ResponseEntity.ok(new TreningReadModel(t));
    }

    @DeleteMapping("/{tid}")
    public ResponseEntity<?> deleteTraining(@PathVariable Long tid) {
        treningService.removeSchedule(tid);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/done/{tid}")
    public ResponseEntity<TreningReadModel> markTrainingAsDone(@PathVariable Long tid) {
        return ResponseEntity.ok(treningService.confirmPresence(tid));
    }

    @PatchMapping("/uncancel/{tid}")
    public ResponseEntity<TreningReadModel> uncalncelTraining(@PathVariable Long tid) {
        return ResponseEntity.ok(treningService.unCancel(tid));
    }



    static class TrainingUpdateDTO {
        private final LocalDateTime dateTime;
        private final Long locationId;

        public TrainingUpdateDTO(LocalDateTime dateTime, Long locationId) {
            this.dateTime = dateTime;
            this.locationId = locationId;
        }

        public LocalDateTime getDateTime() {
            return dateTime;
        }

        public Long getLocationId() {
            return locationId;
        }
    }
}
