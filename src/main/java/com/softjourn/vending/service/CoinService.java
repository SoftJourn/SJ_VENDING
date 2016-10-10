package com.softjourn.vending.service;


import com.softjourn.vending.dto.AmountDTO;
import com.softjourn.vending.dto.TransactionDTO;
import com.softjourn.vending.exceptions.NotEnoughAmountException;
import com.softjourn.vending.exceptions.PaymentProcessingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
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
import java.util.function.Supplier;

@Service
public class CoinService {

    @Value("${coins.server.host}")
    private String coinsServerHost;

    @Value("${coins.spent.path}")
    private String coinsSpentPath;

    @Value("${coins.treasury.account}")
    private String treasuryErisAccount;

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
    public BigDecimal spent(Principal principal, BigDecimal amount, String machineAddress) {
        return wrapExceptionHandling(() -> {
            ResponseEntity<TransactionDTO> response =  coinRestTemplate.exchange(coinsServerHost + "/buy/" + machineAddress,
                    HttpMethod.POST,
                    prepareRequest(principal, amount),
                    TransactionDTO.class);
            if (response.getBody().getStatus().equals("SUCCESS")) {
                return response.getBody().getRemain();
            } else {
                throw new PaymentProcessingException("Unsuccessful call to coins server. " + response.getBody().getError());
            }
        });
    }

    private BigDecimal distributeMoney(Principal adminPrincipal, BigDecimal amount) {
        return wrapExceptionHandling(() ->  {
            ResponseEntity<TransactionDTO> response =  coinRestTemplate.exchange(coinsServerHost + "/distribute",
                    HttpMethod.POST,
                    prepareRequest(adminPrincipal, amount),
                    TransactionDTO.class);
            if (response.getBody().getStatus().equals("SUCCESS")) {
                return response.getBody().getRemain();
            } else {
                throw new PaymentProcessingException("Unsuccessful call to coins server. " + response.getBody().getError());
            }
        });
    }

    private BigDecimal wrapExceptionHandling(Supplier<BigDecimal> function) {
        try {
            return function.get();
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
