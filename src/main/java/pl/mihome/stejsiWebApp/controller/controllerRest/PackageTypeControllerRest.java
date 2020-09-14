package pl.mihome.stejsiWebApp.controller.controllerRest;

import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import pl.mihome.stejsiWebApp.DTO.RodzajPakietuWriteModel;
import pl.mihome.stejsiWebApp.config.AngularConfiguration;
import pl.mihome.stejsiWebApp.logic.RodzajPakietuService;
import pl.mihome.stejsiWebApp.model.RodzajPakietu;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@CrossOrigin({"http://192.168.0.96:4200", "http://localhost:4200", "http://192.168.1.51:4200"})
@RequestMapping("/ngaccess/packagetypes")
@Secured("ROLE_STEJSI")
public class PackageTypeControllerRest {
    private final RodzajPakietuService packageTypeService;
    private final AngularConfiguration angularConf;

    public PackageTypeControllerRest(RodzajPakietuService packageTypeService, AngularConfiguration angularConf) {
        this.packageTypeService = packageTypeService;
        this.angularConf = angularConf;
    }

    @PostMapping
    public ResponseEntity<RodzajPakietu> newPackageType(@RequestBody @Valid RodzajPakietuWriteModel packageTypeToCreate) {
        var addedType = packageTypeService.saveNewType(packageTypeToCreate);
        var uri = URI.create(angularConf.getWebServerUrl() + "/packagetypes/" + addedType.getId());
        return ResponseEntity.created(uri).body(addedType);
    }

    @DeleteMapping("/{typeId}")
    public ResponseEntity<String> removePackageType(@PathVariable Long typeId) {
        packageTypeService.removeById(typeId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{typeId}")
    public ResponseEntity<RodzajPakietu> getPackageTypeById(@PathVariable Long typeId) {
        return ResponseEntity.ok(packageTypeService.getTypeById(typeId));
    }

    @GetMapping
    public ResponseEntity<Slice<RodzajPakietu>> getPackageTypesSlice(@RequestParam(value = "page", required = false) Integer page) {
        return ResponseEntity.ok(packageTypeService.getSliceOfActive(page == null ? 0 : page));
    }

    @GetMapping("/all")
    public ResponseEntity<List<RodzajPakietu>> getAllPackageTypes() {
        return ResponseEntity.ok(packageTypeService.getAllActive());
    }

}
