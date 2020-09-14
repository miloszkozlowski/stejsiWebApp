package pl.mihome.stejsiWebApp.controller.controllerRest;

import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import pl.mihome.stejsiWebApp.DTO.RodzajPakietuWriteModel;
import pl.mihome.stejsiWebApp.config.AngularConfiguration;
import pl.mihome.stejsiWebApp.logic.LokalizacjaService;
import pl.mihome.stejsiWebApp.logic.RodzajPakietuService;
import pl.mihome.stejsiWebApp.model.Lokalizacja;
import pl.mihome.stejsiWebApp.model.RodzajPakietu;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@CrossOrigin({"http://192.168.0.96:4200", "http://localhost:4200", "http://192.168.1.51:4200"})
@RequestMapping("/ngaccess/locations")
@Secured("ROLE_STEJSI")
public class LocationControllerRest {
    private final LokalizacjaService lokalizacjaService;
    private final AngularConfiguration angularConf;

    public LocationControllerRest(LokalizacjaService lokalizacjaService, AngularConfiguration angularConf) {
        this.lokalizacjaService = lokalizacjaService;
        this.angularConf = angularConf;
    }

    @PostMapping
    public ResponseEntity<Lokalizacja> newPackageType(@RequestBody @Valid Lokalizacja lokalizacja) {
        var addedLocation = lokalizacjaService.addNew(lokalizacja);
        var uri = URI.create(angularConf.getWebServerUrl() + "/locations/" + addedLocation.getId());
        return ResponseEntity.created(uri).body(addedLocation);
    }

    @DeleteMapping("/{locationId}")
    public ResponseEntity<String> removePackageType(@PathVariable Long locationId) {
        lokalizacjaService.remove(locationId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/setdefault/{locationId}")
    public ResponseEntity<String> setDefault(@PathVariable Long locationId) {
        lokalizacjaService.setNewDefault(locationId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Slice<Lokalizacja>> getPackageTypesSlice(@RequestParam(value = "page", required = false) Integer page) {
        return ResponseEntity.ok(lokalizacjaService.getSliceOfActive(page == null ? 0 : page));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Lokalizacja>> getAllLocations() {
        return ResponseEntity.ok(lokalizacjaService.getAll());
    }
}
