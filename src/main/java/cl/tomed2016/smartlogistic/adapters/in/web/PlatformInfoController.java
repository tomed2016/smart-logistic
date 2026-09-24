package cl.tomed2016.smartlogistic.adapters.in.web;

import cl.tomed2016.smartlogistic.domain.calendar.BusinessCalendar;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/platform")
public class PlatformInfoController {

    @GetMapping("/info")
    PlatformInfoResponse info() {
        return new PlatformInfoResponse(
                "smart-logistic",
                BusinessCalendar.CHILE_ZONE_ID.getId(),
                "CLP",
                "modular-monolith"
        );
    }

    record PlatformInfoResponse(String system, String timezone, String currency, String recommendedArchitecture) {
    }
}
