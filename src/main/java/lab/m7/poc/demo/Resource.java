package lab.m7.poc.demo;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class Resource {

    DataDogRestClient dataDogRestClient;
    
    @GetMapping("/hello")
    public String hello() {
        return "Hello, World!";
    }

    @GetMapping("/datadog/info")
    public String getInfo() {
        return dataDogRestClient.getInfo();
    }

    @GetMapping("/datadog/profile")
    public String getProfile() {
        return dataDogRestClient.getProfile();
    }

}
