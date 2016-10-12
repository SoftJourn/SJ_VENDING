package com.softjourn.vending.service;


import com.softjourn.vending.dao.FieldRepository;
import com.softjourn.vending.dao.MachineRepository;
import com.softjourn.vending.dao.RowRepository;
import com.softjourn.vending.dto.VendingMachineBuilderDTO;
import com.softjourn.vending.entity.Field;
import com.softjourn.vending.entity.VendingMachine;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class VendingServiceTest {

    private VendingMachineBuilderDTO builder1;

    private VendingMachineBuilderDTO builder2;

    @Mock
    private MachineRepository repository;
    @Mock
    private RowRepository rowRepository;
    @Mock
    private FieldRepository fieldRepository;
    @Mock
    private CoinService coinService;
    @Mock
    OAuth2Authentication principal;

    @Mock
    OAuth2AuthenticationDetails authDetails;

    VendingService service;

    @Before
    public void setUp() throws Exception {
        when(principal.getDetails()).thenReturn(authDetails);

        builder1 = new VendingMachineBuilderDTO();
        builder1.setColumnsCount(6);
        builder1.setColumnsNumbering(VendingMachineBuilderDTO.Numbering.NUMERICAL);
        builder1.setRowsCount(10);
        builder1.setRowsNumbering(VendingMachineBuilderDTO.Numbering.ALPHABETICAL);
        builder1.setName("Machine1");

        builder2 = new VendingMachineBuilderDTO();
        builder2.setColumnsCount(5);
        builder2.setColumnsNumbering(VendingMachineBuilderDTO.Numbering.NUMERICAL);
        builder2.setRowsCount(8);
        builder2.setRowsNumbering(VendingMachineBuilderDTO.Numbering.NUMERICAL);
        builder2.setName("Machine2");

        when(repository.save(any(VendingMachine.class))).thenAnswer(invocation -> invocation.getArguments()[0]);

        service = new VendingService(repository, rowRepository, fieldRepository, coinService);

        ReflectionTestUtils.setField(service, "coinsServerHost", "http://localhost");
        ReflectionTestUtils.setField(service, "coinRestTemplate", mock(RestTemplate.class));

    }

    @Test
    public void testCreate() throws Exception {
        VendingMachine machine1 = service.create(builder1, principal);
        VendingMachine machine2 = service.create(builder2, principal);

        assertEquals(60, machine1.getFields().size());
        assertEquals("Machine1", machine1.getName());
        assertEquals(3, machine1.getFields()
                .stream()
                .map(Field::getInternalId)
                .filter(s -> s.equals("A0") || s.equals("C4") || s.equals("J5"))
                .count());

        assertEquals(0, machine1.getFields()
                .stream()
                .map(Field::getInternalId)
                .filter(s -> s.equals("A6") || s.equals("K4"))
                .count());

        assertEquals(40, machine2.getFields().size());
        assertEquals("Machine2", machine2.getName());
        assertEquals(3, machine2.getFields()
                .stream()
                .map(Field::getInternalId)
                .filter(s -> s.equals("00") || s.equals("74") || s.equals("32"))
                .count());

        assertEquals(0, machine2.getFields()
                .stream()
                .map(Field::getInternalId)
                .filter(s -> s.equals("06") || s.equals("84"))
                .count());
    }


}