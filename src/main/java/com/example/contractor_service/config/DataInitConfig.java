package com.example.contractor_service.config;

import com.example.contractor_service.data.DataLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class DataInitConfig {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Bean
    public DataLoader countryTable() {
        return new DataLoader(jdbcTemplate, "country.csv", "country", true);
    }

    @Bean
    public DataLoader industryTable() {
        return new DataLoader(jdbcTemplate, "industry.csv", "industry", false);
    }

    @Bean
    public DataLoader orgformTable() {
        return new DataLoader(jdbcTemplate, "org_form.csv", "org_form", false);
    }

}
