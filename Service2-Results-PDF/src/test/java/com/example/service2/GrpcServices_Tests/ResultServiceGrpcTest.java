package com.example.service2.GrpcServices_Tests;

import com.example.service2.entities.Result;
import com.example.service2.grpc.*;
import com.example.service2.services.ResultServiceImpl;
import com.example.shared.CommonProto.StatusMessage;
import io.grpc.ManagedChannel;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

public class ResultServiceGrpcTest {

    private ResultServiceImpl resultService;
    private ResultServiceGrpc.ResultServiceBlockingStub blockingStub;

    @BeforeEach
    void setUp() throws Exception {
        resultService = mock(ResultServiceImpl.class);

        String serverName = InProcessServerBuilder.generateName();
        InProcessServerBuilder serverBuilder = InProcessServerBuilder.forName(serverName).directExecutor();

        serverBuilder.addService(new ResultServiceGrpcImpl() {{
            this.resultService = ResultServiceGrpcTest.this.resultService;
        }}).build().start();

        ManagedChannel channel = InProcessChannelBuilder.forName(serverName).directExecutor().build();
        blockingStub = ResultServiceGrpc.newBlockingStub(channel);
    }

    @Test
    void saveResult_returnsSuccess() {
        Result saved = new Result(1L, 100L, 200L, "A1", "9.99");
        when(resultService.saveResult(any())).thenReturn(saved);

        ResultRequest request = ResultRequest.newBuilder()
                .setEventId(100L)
                .setDisciplineId(200L)
                .setAthleteId("A1")
                .setValue("9.99")
                .build();

        StatusMessage response = blockingStub.saveResult(request);

        assertTrue(response.getSuccess());
        assertEquals("Resultado guardado correctamente", response.getMensaje());
        assertEquals(1L, response.getId());

        verify(resultService).saveResult(argThat(r ->
                r.getEventId().equals(100L) &&
                        r.getDisciplineId().equals(200L) &&
                        r.getAthleteId().equals("A1") &&
                        r.getValue().equals("9.99")
        ));
    }

    @Test
    void saveMultipleResults_savesAllAndReturnsSuccess() {
        ResultRequest r1 = ResultRequest.newBuilder().setEventId(1L).setDisciplineId(2L).setAthleteId("A1").setValue("11.11").build();
        ResultRequest r2 = ResultRequest.newBuilder().setEventId(3L).setDisciplineId(4L).setAthleteId("A2").setValue("22.22").build();

        ResultListRequest request = ResultListRequest.newBuilder()
                .addResults(r1)
                .addResults(r2)
                .build();

        StatusMessage response = blockingStub.saveMultipleResults(request);

        assertTrue(response.getSuccess());
        assertEquals("Resultados guardados correctamente", response.getMensaje());

        verify(resultService).saveAll(argThat(list ->
                list.size() == 2 &&
                        list.get(0).getAthleteId().equals("A1") &&
                        list.get(1).getValue().equals("22.22")
        ));
    }

    @Test
    void getResultsByAthleteId_returnsResults() {
        List<Result> mockResults = List.of(
                new Result(1L, 100L, 200L, "A1", "12.3")
        );
        when(resultService.getResultsByAthleteId("A1")).thenReturn(mockResults);

        AthleteIdRequest request = AthleteIdRequest.newBuilder().setAthleteId("A1").build();
        ResultListResponse response = blockingStub.getResultsByAthleteId(request);

        assertEquals(1, response.getResultsCount());
        ResultResponse result = response.getResults(0);
        assertEquals("12.3", result.getResult().getValue());
        assertEquals("A1", result.getResult().getAthleteId());
    }

    @Test
    void getResultsByEventId_returnsCorrectData() {
        List<Result> mockResults = List.of(
                new Result(2L, 50L, 70L, "A9", "8.88")
        );
        when(resultService.getResultsByEventId(50L)).thenReturn(mockResults);

        EventIdRequest request = EventIdRequest.newBuilder().setEventId(50L).build();
        ResultListResponse response = blockingStub.getResultsByEventId(request);

        assertEquals(1, response.getResultsCount());
        ResultResponse result = response.getResults(0);
        assertEquals(50L, result.getResult().getEventId());
        assertEquals("8.88", result.getResult().getValue());
    }

    @Test
    void getAllResults_returnsAllStoredResults() {
        List<Result> all = List.of(
                new Result(1L, 10L, 20L, "A1", "10.1"),
                new Result(2L, 11L, 21L, "A2", "10.2")
        );
        when(resultService.getAllResults()).thenReturn(all);

        ResultListResponse response = blockingStub.getAllResults(Empty.newBuilder().build());

        assertEquals(2, response.getResultsCount());
        assertEquals("10.2", response.getResults(1).getResult().getValue());
    }
}
