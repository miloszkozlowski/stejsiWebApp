package pl.mihome.stejsiWebApp.controller.controllerRest;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.mihome.stejsiWebApp.DTO.InfoFlow;
import pl.mihome.stejsiWebApp.logic.InfoFlowService;

@RestController
@RequestMapping("/ngaccess/info")
@Secured("ROLE_STEJSI")
public class InfoFlowController {

    private final InfoFlowService infoFlowService;

    public InfoFlowController(InfoFlowService infoFlowService) {
        this.infoFlowService = infoFlowService;
    }

    @GetMapping
    public ResponseEntity<InfoFlow> getCurrentInfoFlow() {
        return ResponseEntity.ok(infoFlowService.getCurrentFlow());
    }

}
