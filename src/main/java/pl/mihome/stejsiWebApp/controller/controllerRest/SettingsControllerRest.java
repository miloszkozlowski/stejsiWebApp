package pl.mihome.stejsiWebApp.controller.controllerRest;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import pl.mihome.stejsiWebApp.config.AngularConfiguration;
import pl.mihome.stejsiWebApp.logic.TrainerDataService;
import pl.mihome.stejsiWebApp.model.TrainerData;

import java.net.URI;

@RestController
@CrossOrigin({"http://192.168.0.96:4200", "http://localhost:4200", "http://192.168.1.51:4200"})
@RequestMapping("/ngaccess/settings")
@Secured("ROLE_STEJSI")
public class SettingsControllerRest {

    private final TrainerDataService trainerDataService;
    private final AngularConfiguration angularConfig;

    public SettingsControllerRest(TrainerDataService trainerDataService, AngularConfiguration angularConfig) {
        this.trainerDataService = trainerDataService;
        this.angularConfig = angularConfig;
    }

    @GetMapping
    public ResponseEntity<TrainerData> getSettings() {
        return ResponseEntity.ok(trainerDataService.getCurrentData());
    }

    @PostMapping
    public ResponseEntity<TrainerData> saveSettings(@RequestBody TrainerData settings) {
        var saved = trainerDataService.newSettings(settings);
        return ResponseEntity.created(URI.create(angularConfig.getWebServerUrl() + "/settings")).body(saved);
    }
}
