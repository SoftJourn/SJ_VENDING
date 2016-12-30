package com.softjourn.vending.service;


import com.softjourn.vending.entity.VendingMachine;
import com.softjourn.vending.exceptions.MachineBusyException;
import com.softjourn.vending.exceptions.VendingProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.security.*;
import java.time.Instant;
import java.util.Collections;
import java.util.Optional;

@Service
@Profile("prod")
public class MachineServiceProd implements MachineService {

    private static final String AUTH_HEADER_NAME = "Authorization";

    @Value("${machine.request.signer.keystore.file}")
    String machineSignerKeystoreFile;
    @Value("${machine.request.signer.keystore.password}")
    String keystorePassword;
    @Value("${machine.request.signer.keystore.alias}")
    String keystoreAlias;

    private VendingService vendingService;
    private RestTemplate template;
    private Signature signature;

    public MachineServiceProd(VendingService vendingService, RestTemplate template) {
        this.vendingService = vendingService;
        this.template = template;
    }

    @Autowired
    public MachineServiceProd(VendingService vendingService) {
        this.vendingService = vendingService;
        this.template = new RestTemplate();
        ((SimpleClientHttpRequestFactory) this.template.getRequestFactory()).setConnectTimeout(5000);
        ((SimpleClientHttpRequestFactory) this.template.getRequestFactory()).setReadTimeout(20000);
    }

    public void buy(Integer machineId, String fieldInternalId) {
        try {
            Optional.ofNullable(vendingService.get(machineId))
                    .map(VendingMachine::getUrl)
                    .map(url -> post(url, fieldInternalId))
                    .ifPresent((result) -> {
                        if (result == 509) {
                            throw new MachineBusyException(machineId);
                        } else if (result != 200) {
                            throw new RuntimeException("Error response from server \"" + result + "\"" + ".");
                        }
                    });
        } catch (Exception e) {
            throw new VendingProcessingException("Error occurred while processing vending request. " + e.getMessage(), e);
        }

    }

    private int post(String url, String fieldInternalId) {
            HttpHeaders authHeader = new HttpHeaders();
            authHeader.put(AUTH_HEADER_NAME, Collections.singletonList(createSignedData(fieldInternalId)));

            ResponseEntity<String> response = template.exchange(url,
                    HttpMethod.POST,
                    new HttpEntity<>(fieldInternalId, authHeader),
                    String.class);
            System.out.println(response.getBody());
            return response.getStatusCode().value();
    }

    private String createSignedData(String cell) {
        String raw = Instant.now().toEpochMilli() + "" + cell;
        try {
            signature.update(raw.getBytes());
            String signed = new BigInteger(signature.sign()).toString(16);
            return raw + "." + signed;
        } catch (SignatureException e) {
            throw new RuntimeException("Can't sign data " + raw, e);
        }
    }

    @PostConstruct
    private void prepareSignerKey() {
        try {
            KeyPair keyPair = new KeyStoreKeyFactory(
                    new ClassPathResource(machineSignerKeystoreFile), keystorePassword.toCharArray())
                    .getKeyPair(keystoreAlias, keystorePassword.toCharArray());

            signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(keyPair.getPrivate());
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Can't prepare machine request signer. ", e);
        }
    }


}
