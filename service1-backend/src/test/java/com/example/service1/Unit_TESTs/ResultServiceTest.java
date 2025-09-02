package com.example.service1.Unit_TESTs;

import com.example.service1.DTO.PdfDto;
import com.example.service1.DTO.ResultDto;
import com.example.service1.GrpcClients.ResultGrpcClient;
import com.example.service1.Services.ResultService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ResultServiceTest {

    @Mock
    private ResultGrpcClient grpcClient;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private ResultService resultService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllResults() {
        List<ResultDto> mockList = List.of(new ResultDto());
        when(grpcClient.getAllResults()).thenReturn(mockList);

        List<ResultDto> result = resultService.getAllResults();

        assertEquals(1, result.size());
        verify(grpcClient).getAllResults();
    }

    @Test
    void testGetResultsByAthlete() {
        List<ResultDto> mockList = List.of(new ResultDto());
        when(grpcClient.getResultsByAthlete("ABC123")).thenReturn(mockList);

        List<ResultDto> result = resultService.getResultsByAthlete("ABC123");

        assertEquals(1, result.size());
        verify(grpcClient).getResultsByAthlete("ABC123");
    }

    @Test
    void testGetResultsByEvent() {
        List<ResultDto> mockList = List.of(new ResultDto());
        when(grpcClient.getResultsByEvent(5L)).thenReturn(mockList);

        List<ResultDto> result = resultService.getResultsByEvent(5L);

        assertEquals(1, result.size());
        verify(grpcClient).getResultsByEvent(5L);
    }

    @Test
    void testSaveResult() {
        ResultDto mockDto = new ResultDto();
        when(grpcClient.saveResult("ABC123", 10L, 20L, "9.87")).thenReturn(mockDto);

        ResultDto result = resultService.saveResult("ABC123", 10L, 20L, "9.87");

        assertEquals(mockDto, result);
        verify(grpcClient).saveResult("ABC123", 10L, 20L, "9.87");
    }

    @Test
    void testSaveResultReturnNull() {
        when(grpcClient.saveResult("DEF456", 11L, 22L, "10.23")).thenReturn(null);

        ResultDto result = resultService.saveResult("DEF456", 11L, 22L, "10.23");

        assertNull(result);
        verify(grpcClient).saveResult("DEF456", 11L, 22L, "10.23");
    }

    @Test
    void testSaveResultsBatchFromDto() {
        List<ResultDto> input = List.of(new ResultDto());
        when(grpcClient.saveResultsBatch(input)).thenReturn(input);

        List<ResultDto> result = resultService.saveResultsBatchFromDto(input);

        assertEquals(1, result.size());
        verify(grpcClient).saveResultsBatch(input);
    }

    @Test
    void testGetResultById() {
        ResultDto mockDto = new ResultDto();
        when(grpcClient.getResultById(100L)).thenReturn(mockDto);

        ResultDto result = resultService.getResultById(100L);

        assertEquals(mockDto, result);
        verify(grpcClient).getResultById(100L);
    }

    @Test
    void testGetHistorialPdf() {
        List<PdfDto> pdfList = List.of(new PdfDto());
        when(grpcClient.listPdfHistorical("ABC123")).thenReturn(pdfList);

        List<PdfDto> result = resultService.getHistorialPdf("ABC123");

        assertEquals(1, result.size());
        verify(grpcClient).listPdfHistorical("ABC123");
    }

    @Test
    void testRequestGenerationPdf() {
        resultService.requestGenerationPdf("ABC123");

        verify(rabbitTemplate).convertAndSend(eq("pdf.request.queue"), eq(Map.of("athleteId", "ABC123")));
    }
}
