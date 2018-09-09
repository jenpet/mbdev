package pet.jen.mbdev.api.auth.client;

import com.google.common.net.HttpHeaders;
import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;
import pet.jen.mbdev.api.auth.domain.OAuthConfig;

import javax.net.ssl.*;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class BaseClientBuilder {

    public static final MediaType MEDIA_TYPE_FORM =  MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");

    public static OkHttpClient.Builder get(OAuthConfig config) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        addDefaultLoggingInterceptor(builder);

        if(config.isTrustAllSslHosts()) {
            trustAllSslHosts(builder);
        }
        return builder;
    }

    public static Interceptor createFormHeaderInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request().newBuilder()
                        .addHeader(HttpHeaders.CONTENT_TYPE, MEDIA_TYPE_FORM.toString())
                        .build();
                return chain.proceed(request);
            }
        };
    }

    private static final TrustManager[] trustAllCerts = new TrustManager[] {
            new X509TrustManager() {
                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[]{};
                }
            }
    };

    private static final SSLContext trustAllSslContext;

    static {
        try {
            trustAllSslContext = SSLContext.getInstance("SSL");
            trustAllSslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException(e);
        }
    }

    private static final SSLSocketFactory trustAllSslSocketFactory = trustAllSslContext.getSocketFactory();

    private static OkHttpClient.Builder trustAllSslHosts(OkHttpClient.Builder builder) {
        builder.sslSocketFactory(trustAllSslSocketFactory, (X509TrustManager)trustAllCerts[0]);
        builder.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
        return builder;
    }

    private static OkHttpClient.Builder addDefaultLoggingInterceptor(OkHttpClient.Builder builder) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(loggingInterceptor);
        return builder;
    }
}