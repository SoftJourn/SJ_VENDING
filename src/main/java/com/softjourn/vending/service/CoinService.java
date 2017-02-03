package com.softjourn.vending.service;


import com.softjourn.common.auth.OAuthHelper;
import com.softjourn.vending.dto.AmountDTO;
import com.softjourn.vending.dto.TransactionDTO;
import com.softjourn.vending.exceptions.NotEnoughAmountException;
import com.softjourn.vending.exceptions.PaymentProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
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

    public static final String SUCCESS_TRANSACTION_STATUS = "SUCCESS";

    @Value("${coins.server.host}")
    private String coinsServerHost;

    @Value("${coins.spent.path}")
    private String coinsSpentPath;

    private RestTemplate coinRestTemplate;

    @Autowired
    private OAuthHelper oAuthHelper;

    public CoinService() {
        coinRestTemplate = new RestTemplate(new SimpleClientHttpRequestFactory() {
            @Override
            protected HttpURLConnection openConnection(URL url, Proxy proxy) throws IOException {
                URLConnection urlConnection = (proxy != null ? url.openConnection(proxy) : url.openConnection());
                Assert.isInstanceOf(HttpURLConnection.class, urlConnection);
                if (urlConnection instanceof HttpsURLConnection) {
                    ((HttpsURLConnection) urlConnection).setHostnameVerifier((s, sslSession) -> true);
                }
                return (HttpURLConnection) urlConnection;
            }
        });
    }

    @PreAuthorize("isAuthenticated()")
    public TransactionDTO spent(Principal principal, BigDecimal amount, String machineAddress) {
        return wrapExceptionHandling(() -> {
            ResponseEntity<TransactionDTO> response = coinRestTemplate.exchange(coinsServerHost + "/buy/" + machineAddress,
                    HttpMethod.POST,
                    prepareRequest(principal, amount),
                    TransactionDTO.class);
            if (response.getBody().getStatus().equals(SUCCESS_TRANSACTION_STATUS)) {
                return response.getBody();
            } else {
                throw new PaymentProcessingException("Unsuccessful call to coins server. " + response.getBody().getError());
            }
        });
    }

    public void refill(Principal adminPrincipal, BigDecimal amount, String machineName) {
        distributeMoney(adminPrincipal, amount, machineName);
    }

    public void returnMoney(TransactionDTO tx) {
        String url = coinsServerHost + "/rollback/" + tx.getId();
        ResponseEntity<TransactionDTO> response = oAuthHelper.requestWithToken(url, HttpMethod.POST, HttpEntity.EMPTY, TransactionDTO.class);
        if (! response.getStatusCode().is2xxSuccessful()) {
            throw new PaymentProcessingException("Unable to rollback transaction " + tx.getId() + ". Coins server response " + response.toString());
        }
        if (!response.getBody().getStatus().equals(SUCCESS_TRANSACTION_STATUS)) {
            throw new PaymentProcessingException("Unable to rollback transaction " + tx.getId() + ". Coins server error " + response.getBody().getError());
        }
    }

    private void distributeMoney(Principal adminPrincipal, BigDecimal amount, String machineName) {
        wrapExceptionHandling(() -> {
            ResponseEntity<TransactionDTO> response = coinRestTemplate.exchange(coinsServerHost + "/distribute/" + machineName,
                    HttpMethod.POST,
                    prepareRequest(adminPrincipal, amount),
                    TransactionDTO.class);
            if (!(response.getStatusCode().is2xxSuccessful())) {
                throw new PaymentProcessingException("Unsuccessful call to coins server. " + response.getBody().getError());
            }
            return null;
        });
    }

    private TransactionDTO wrapExceptionHandling(Supplier<TransactionDTO> function) {
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
        return new HttpEntity<>(new AmountDTO(amount), new HttpHeaders() {{
            put("Authorization", Collections.singletonList(getTokenHeader(principal)));
        }});
    }

    private String getTokenHeader(Principal principal) {
        if (principal instanceof OAuth2Authentication) {
            OAuth2AuthenticationDetails authenticationDetails = (OAuth2AuthenticationDetails) ((OAuth2Authentication) principal).getDetails();
            return authenticationDetails.getTokenType() + " " + authenticationDetails.getTokenValue();
        } else {
            throw new IllegalStateException("Unsupported autentication type.");
        }

    }
}
