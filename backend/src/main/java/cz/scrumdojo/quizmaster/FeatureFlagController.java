package cz.scrumdojo.quizmaster;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class FeatureFlagController {

    private final FeatureFlag featureFlag;

    @Autowired
    public FeatureFlagController(FeatureFlag featureFlag) {
        this.featureFlag = featureFlag;
    }

    @GetMapping("/feature-flag")
    public boolean isFeatureEnabled() {
        return featureFlag.isEnabled();
    }
}
