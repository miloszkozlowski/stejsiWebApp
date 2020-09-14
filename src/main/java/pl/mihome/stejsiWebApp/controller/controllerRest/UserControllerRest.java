package pl.mihome.stejsiWebApp.controller.controllerRest;

import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import pl.mihome.stejsiWebApp.DTO.PodopiecznyReadModel;
import pl.mihome.stejsiWebApp.DTO.PodopiecznyWriteModel;
import pl.mihome.stejsiWebApp.config.AngularConfiguration;
import pl.mihome.stejsiWebApp.logic.PodopiecznyService;

import javax.validation.Valid;
import javax.websocket.server.PathParam;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin({"http://192.168.0.96:4200", "http://localhost:4200", "http://192.168.1.51:4200"})
@RequestMapping("/ngaccess/users")
@Secured("ROLE_STEJSI")
public class UserControllerRest {

    private final PodopiecznyService podopiecznyService;
    private final AngularConfiguration angularConf;

    public UserControllerRest(PodopiecznyService podopiecznyService, AngularConfiguration angularConf) {
        this.podopiecznyService = podopiecznyService;
        this.angularConf = angularConf;
    }

    @PostMapping
    ResponseEntity<PodopiecznyReadModel> createNewUser(@RequestBody @Valid PodopiecznyWriteModel source) {
        var savedUser = podopiecznyService.saveNewUser(source);
        var uri = URI.create(angularConf.getWebServerUrl() + "/users/" + savedUser.getId());
        return ResponseEntity.created(uri).body(savedUser);
    }

    @GetMapping
    ResponseEntity<Slice<PodopiecznyReadModel>> readAll(@RequestParam(value = "page", required = false, defaultValue = "0") Integer pageNo) {
        return ResponseEntity.ok(podopiecznyService.getAllSlice(pageNo));
    }

    @GetMapping("/{id}")
    ResponseEntity<PodopiecznyReadModel> readUserById(@PathVariable Long id) {
        var loadedUser = podopiecznyService.getUserById(id);
        return ResponseEntity.ok(loadedUser);
    }

    @GetMapping("/email/available")
    ResponseEntity<Map<String, Object>> checkEmailAvailability(@PathParam("email") String email) {
        if (email == null) {
            throw new IllegalArgumentException("PARAM_ERROR");
        }
        var response = new HashMap<String, Object>();
        response.put("email", email);
        response.put("available", podopiecznyService.isEmailAvailableForUse(email));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/phone/available")
    ResponseEntity<Map<String, Object>> checkPhoneNumberAvailability(@PathParam("phoneNumber") String phoneNumber) {
        if (phoneNumber == null) {
            throw new IllegalArgumentException("PARAM_ERROR");
        }
        var response = new HashMap<String, Object>();
        response.put("phoneNumber", phoneNumber);
        response.put("available", podopiecznyService.isPhoneNumberAvailableForUser(phoneNumber));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    ResponseEntity<List<PodopiecznyReadModel>> searchForUser(@RequestParam(value = "keyword") String searchKey) {
        return ResponseEntity.ok(podopiecznyService.returnElasticsearchResult(searchKey));
    }

}
