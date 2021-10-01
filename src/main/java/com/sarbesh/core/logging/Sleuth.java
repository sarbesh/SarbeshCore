package com.sarbesh.core.logging;

import brave.sampler.Sampler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class Sleuth {

    private static final Logger LOGGER = LoggerFactory.getLogger(Sleuth.class);

    @Bean
    public Sampler defaultSampler()
    {
        LOGGER.info("Enabling default Sleuth");
        return Sampler.ALWAYS_SAMPLE;
    }
}
