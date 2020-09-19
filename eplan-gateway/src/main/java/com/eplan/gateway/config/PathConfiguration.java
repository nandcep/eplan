package com.eplan.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
* @author  Adinandra Dharmasurya
* @version 1.0
* @since   2020-09-19
*/
@Component
@ConfigurationProperties(prefix = "eplan.path")
public class PathConfiguration {

    private String[] ignores;

    public String[] getIgnores() {
		return ignores;
	}

	public void setIgnores(String[] ignores) {
		this.ignores = ignores;
	}
    
}
