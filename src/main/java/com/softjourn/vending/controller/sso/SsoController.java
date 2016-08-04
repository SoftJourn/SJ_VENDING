package com.softjourn.vending.controller.sso;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class SsoController {

    /**
     * Handle redirected requests from auth server,
     * get tokens for this {@param code} and redirect with token to admin page
     * @param code
     * @param redirectAttributes
     * @return
     */
    @RequestMapping("/sso")
    public String sso(@RequestParam String code, RedirectAttributes redirectAttributes) {
        redirectAttributes.addAttribute("access_token", getTokenByCode(code));
        return "redirect:admin";
    }

    private String getTokenByCode(String code) {
        RestTemplate restTemplate = new RestTemplate(new SimpleClientHttpRequestFactory() {
            @Override
            protected HttpURLConnection openConnection(URL url, Proxy proxy) throws IOException {
                URLConnection urlConnection = (proxy != null ? url.openConnection(proxy) : url.openConnection());
                Assert.isInstanceOf(HttpURLConnection.class, urlConnection);
                if(urlConnection instanceof HttpsURLConnection) {
                    ((HttpsURLConnection)urlConnection).setHostnameVerifier((s, sslSession) -> true);
                }
                return (HttpURLConnection) urlConnection;
            }
        });


        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic dmVuZGluZ19hZG1pbjpzdXBlcnNlY3JldA==");
        //headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        Map<String, String> params = new HashMap<>();
        params.put("code", code);
        params.put("grant_type","authorization_code");
        params.put("redirect_uri","https://localhost:8222/sso");

        HttpEntity<String> entity = new HttpEntity<>("", headers);

        Map map = restTemplate.postForEntity(formatUrl("https://localhost:8111/oauth/token?", params),
                entity,
                HashMap.class).getBody();

        System.out.println(map);
        return (String) map.get("access_token");
    }

    private String formatUrl(String host, Map<String, String> params) {
        return params
                .entrySet()
                .stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("&", host, ""));
    }
}
