package com.softjourn.vending.service;


import com.softjourn.vending.dto.AmountDTO;
import com.softjourn.vending.dto.TransactionDTO;
import com.softjourn.vending.exceptions.NotEnoughAmountException;
import com.softjourn.vending.exceptions.PaymentProcessingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.security.Principal;
import java.util.Collections;

@Service
public class CoinService {

    @Value("${coins.server.host}")
    private String coinsServerHost;

    @Value("${coins.spent.path}")
    private String coinsSpentPath;

    private RestTemplate coinRestTemplate;

    public CoinService() {
        coinRestTemplate = new RestTemplate(new SimpleClientHttpRequestFactory() {
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
    }

    @PreAuthorize("isAuthenticated()")
    public boolean spent(Principal principal, BigDecimal amount) {
        try {
            return coinRestTemplate.exchange(coinsServerHost + coinsSpentPath,
                    HttpMethod.POST,
                    prepareRequest(principal, amount),
                    TransactionDTO.class).getBody().getStatus().equals("SUCCESS");
        } catch (HttpClientErrorException hcee) {
            if (hcee.getStatusCode().equals(HttpStatus.CONFLICT)) {
                throw new NotEnoughAmountException();
            } else {
                throw new PaymentProcessingException(hcee);
            }
        } catch (RestClientException rce) {
            throw new PaymentProcessingException(rce);
        }
    }

    private HttpEntity<AmountDTO> prepareRequest(Principal principal, BigDecimal amount) {
        return new HttpEntity<>(new AmountDTO(amount), new HttpHeaders(){{
            put("Authorization", Collections.singletonList(getTokenHeader(principal)));
        }});
    }

    private String getTokenHeader(Principal principal) {
        if(principal instanceof OAuth2Authentication) {
            OAuth2AuthenticationDetails authenticationDetails = (OAuth2AuthenticationDetails) ((OAuth2Authentication)principal).getDetails();
            return authenticationDetails.getTokenType() + " " + authenticationDetails.getTokenValue();
        } else {
            throw new IllegalStateException("Unsupported autentication type.");
        }

    }
}
