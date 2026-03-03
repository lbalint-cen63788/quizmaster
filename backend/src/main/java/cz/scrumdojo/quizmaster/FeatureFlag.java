package cz.scrumdojo.quizmaster;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FeatureFlag {

    private final boolean enabled;

    public FeatureFlag(@Value("${feature.flag:false}") boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
