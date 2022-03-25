package io.zoooohs.eb;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
    @GetMapping("/custom/health/path/you/want")
    public String healthCheck() {
        return "건강합니다^^";
    }
}
