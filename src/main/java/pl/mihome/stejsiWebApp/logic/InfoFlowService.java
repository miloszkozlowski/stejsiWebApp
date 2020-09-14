package pl.mihome.stejsiWebApp.logic;

import org.springframework.stereotype.Service;
import pl.mihome.stejsiWebApp.DTO.InfoFlow;

@Service
public class InfoFlowService {

    private final LokalizacjaService locationService;

    public InfoFlowService(LokalizacjaService locationService) {
        this.locationService = locationService;
    }

    public InfoFlow getCurrentFlow() {
        var flow = new InfoFlow();
        flow.setHasDefaultLocation(locationService.getDefaultId() != null);

        return flow;
    }
}
