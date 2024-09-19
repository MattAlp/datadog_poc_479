package lab.m7.poc.demo;

import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Configuration
@Slf4j
public class OkHttpClientConfig {

    @Bean("okHttpClient")
    public OkHttpClient okHttpClientDefault() {
        return new OkHttpClient().newBuilder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(20, 2, TimeUnit.MINUTES))
                .build();
    }

    @Bean("unsafeOkHttpClient")
    public OkHttpClient unsafeOkHttpClient(){
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        return configureToIgnoreCertificate(builder).build();
    }

    //Desabilitar certificado SSL
    private OkHttpClient.Builder configureToIgnoreCertificate(OkHttpClient.Builder builder) {
        try {

            // Criar um gerenciador de confiança que não valide cadeias de certificados
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                            // desativa a verificacao de ssl
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                            // desativa a verificacao de ssl
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create a ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager)trustAllCerts[0]);
            builder.hostnameVerifier((hostname, session) -> true);
        } catch (Exception e) {
            log.warn(String.format("Exception while configuring IgnoreSslCertificate with message: {%s} and stacktrace: [%s]", e.getMessage(), Arrays.toString(e.getStackTrace())));
        }
        return builder;
    }

}
