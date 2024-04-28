package com.heima.wemedia.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * to control fallback bean
 */
@Configuration
@ComponentScan("com.heima.apis.article.fallback")
public class InitConfig {
}
