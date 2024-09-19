package lab.m7.poc.demo;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
@Slf4j
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class DataDogRestClient {

    @NonFinal
    String endPoint;

    @NonFinal
    String profileEndPoint;

    OkHttpClient okHttpClient;

    @NonFinal
    @Value("${datadog.address:localhost}")
    private String address;

    @NonFinal
    @Value("${datadog.port:8126}")
    private String port;

    @PostConstruct
    private void setEndpoints() {
        String alertEngineURL = "http://" + address + ":" + port;
        endPoint = alertEngineURL + "/info";
        profileEndPoint = alertEngineURL + "/profiling/v1/input";
    }

    public String getInfo() {
        return executeGet(endPoint);
    }

    public String getProfile() {
        return executeGet(profileEndPoint);
    }

    private String executeGet(String uri) {
        Request request = new Request.Builder()
                .url(uri)
                .get()
                .build();

        try (Response response = okHttpClient.newCall(request).execute()){
            if (response.isSuccessful()) {
                return response.body().string();
            } else {
                log.warn("Unsuccessful response to get info in this endpoint: {}", uri);
                return "";
            }
        } catch (Exception e) {
            log.error("Internal server error response to get info in this endpoint: {}", uri, e);
            throw new IllegalStateException("Erro não esperado ao bucar info do datadog");
        }
    }

}
