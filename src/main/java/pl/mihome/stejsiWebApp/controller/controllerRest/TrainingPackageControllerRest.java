package pl.mihome.stejsiWebApp.controller.controllerRest;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import pl.mihome.stejsiWebApp.DTO.CalendarItem;
import pl.mihome.stejsiWebApp.DTO.PakietReadModel;
import pl.mihome.stejsiWebApp.DTO.PakietWriteModel;
import pl.mihome.stejsiWebApp.config.AngularConfiguration;
import pl.mihome.stejsiWebApp.logic.PakietTreningowService;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@CrossOrigin({"http://192.168.0.96:4200", "http://localhost:4200", "http://192.168.1.51:4200"})
@RequestMapping("/ngaccess/packages")
@Secured("ROLE_STEJSI")
public class TrainingPackageControllerRest {

    private final AngularConfiguration angularConf;
    private final PakietTreningowService service;

    public TrainingPackageControllerRest(AngularConfiguration angularConf, PakietTreningowService service) {
        this.angularConf = angularConf;
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<PakietReadModel> openNewPackage(@RequestBody @Valid PakietWriteModel req) {
        var created = service.createNew(req);
        return ResponseEntity.created(URI.create(angularConf.getWebServerUrl() + "/packages/" + created.getId())).body(created);
    }

    @PatchMapping("/close/{pid}")
    public ResponseEntity<?> closePackageById(@PathVariable Long pid) {
        service.closePackage(pid);
        return ResponseEntity.noContent().build();
    }


    @PatchMapping("/paid/{pid}")
    public ResponseEntity<?> confirmPayment(@PathVariable Long pid) {
        service.togglePayment(pid);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/unclosed")
    public ResponseEntity<List<PakietReadModel>> getUnclosedPackages() {
        return ResponseEntity.ok(service.getUnclosedPackages());
    }

}
