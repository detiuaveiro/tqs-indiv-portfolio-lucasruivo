package com.example.zeromonos.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class MunicipioService {

    private final RestTemplate restTemplate;

    @Value("${geoapi.base-url:https://geoapi.pt}")
    private String baseUrl;

    private List<String> municipiosCache;

    public MunicipioService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<String> getMunicipios() {
        if (municipiosCache == null) {
            String url = baseUrl + "/municipios?json=1";
            String[] municipiosArray = restTemplate.getForObject(url, String[].class);
            municipiosCache = Arrays.asList(municipiosArray);
        }
        return municipiosCache;
    }

    public boolean isValidMunicipality(String municipality) {
        List<String> municipios = getMunicipios();
        return municipios.contains(municipality);
    }
}