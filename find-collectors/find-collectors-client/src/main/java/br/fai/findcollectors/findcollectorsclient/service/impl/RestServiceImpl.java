package br.fai.findcollectors.findcollectorsclient.service.impl;


import br.fai.findcollectors.findcollectorsclient.service.RestService;
import com.google.gson.Gson;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class RestServiceImpl<T> implements RestService<T> {

    private static final String BASE_ENDPOINT = "http://localhost:8081/api/";

    private String buildEndPoint(String resource) {
        return BASE_ENDPOINT + resource;
    }

    @Override
    public HttpHeaders getAuthenticatedHeaders(String username, String password) {
        return null;
    }

    @Override
    public HttpHeaders getRequestHeaders() {
        return null;
    }

    @Override
    public List<T> get(String resource) {

        List<T> response = null;

        final RestTemplate restTemplate = new RestTemplate();


        try {

            final HttpEntity<String> requestEntity = new HttpEntity<>("");

            ResponseEntity<List<T>> requestResponse = restTemplate.exchange(
                    buildEndPoint(resource),
                    HttpMethod.GET,
                    requestEntity,
                    new ParameterizedTypeReference<List<T>>() {
                    }
            );

            response = requestResponse.getBody();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    @Override
    public T getById(String resource, Class<T> clazz) {

        T response = null;

        final RestTemplate restTemplate = new RestTemplate();


        try {

            final HttpEntity<String> requestEntity = new HttpEntity<>("");

            ResponseEntity<String> requestResponse = restTemplate.exchange(
                    buildEndPoint(resource),
                    HttpMethod.GET,
                    requestEntity,
                    String.class
            );

            final Gson gson = new Gson();

            response = gson.fromJson(requestResponse.getBody(), clazz);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    @Override
    public int post(String resource, T entity) {

        final RestTemplate restTemplate = new RestTemplate();

        try {

            final HttpEntity<T> httpEntity = new HttpEntity<>(entity);

            final ResponseEntity<String> responseEntity = restTemplate.exchange(
                    buildEndPoint(resource),
                    HttpMethod.POST,
                    httpEntity,
                    String.class
            );

            final String response = responseEntity.getBody();

            return Integer.parseInt(response);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    @Override
    public boolean put(String resource, T entity) {

        boolean response = false;

        final RestTemplate restTemplate = new RestTemplate();

        try {

            final HttpEntity<T> httpEntity = new HttpEntity<>(entity);

            final ResponseEntity<Boolean> responseEntity = restTemplate.exchange(
                    buildEndPoint(resource),
                    HttpMethod.PUT,
                    httpEntity,
                    Boolean.class
            );

            response = responseEntity.getBody();

        } catch (Exception e) {
            e.printStackTrace();
        }


        return response;
    }

    @Override
    public boolean deleteById(String resource) {

        boolean response = false;

        final RestTemplate restTemplate = new RestTemplate();

        try {

            final HttpEntity<String> httpEntity = new HttpEntity<>("");

            final ResponseEntity<Boolean> responseEntity = restTemplate.exchange(
                    buildEndPoint(resource),
                    HttpMethod.DELETE,
                    httpEntity,
                    Boolean.class
            );

            response = responseEntity.getBody();

        } catch (Exception e) {
            e.printStackTrace();
        }


        return response;
    }
}
