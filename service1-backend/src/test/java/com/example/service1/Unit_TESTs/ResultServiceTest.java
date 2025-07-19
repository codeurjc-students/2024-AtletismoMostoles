package com.example.service1.Unit_TESTs;

import com.example.service1.DTO.PdfDto;
import com.example.service1.DTO.ResultadoDto;
import com.example.service1.GrpcClients.ResultadoGrpcCliente;
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
    private ResultadoGrpcCliente grpcClient;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private ResultService resultService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllResultados() {
        List<ResultadoDto> mockList = List.of(new ResultadoDto());
        when(grpcClient.getAllResultados()).thenReturn(mockList);

        List<ResultadoDto> result = resultService.getAllResultados();

        assertEquals(1, result.size());
        verify(grpcClient).getAllResultados();
    }

    @Test
    void testGetResultadosDeAtleta() {
        List<ResultadoDto> mockList = List.of(new ResultadoDto());
        when(grpcClient.getResultadosPorAtleta("ABC123")).thenReturn(mockList);

        List<ResultadoDto> result = resultService.getResultadosDeAtleta("ABC123");

        assertEquals(1, result.size());
        verify(grpcClient).getResultadosPorAtleta("ABC123");
    }

    @Test
    void testGetResultadosDeEvento() {
        List<ResultadoDto> mockList = List.of(new ResultadoDto());
        when(grpcClient.verResultadosPorEvento(5L)).thenReturn(mockList);

        List<ResultadoDto> result = resultService.getResultadosDeEvento(5L);

        assertEquals(1, result.size());
        verify(grpcClient).verResultadosPorEvento(5L);
    }

    @Test
    void testGuardarResultado() {
        ResultadoDto mockDto = new ResultadoDto();
        when(grpcClient.guardarResultado("ABC123", 10L, 20L, "9.87")).thenReturn(mockDto);

        ResultadoDto result = resultService.guardarResultado("ABC123", 10L, 20L, "9.87");

        assertEquals(mockDto, result);
        verify(grpcClient).guardarResultado("ABC123", 10L, 20L, "9.87");
    }

    @Test
    void testGuardarResultadoRetornaNull() {
        when(grpcClient.guardarResultado("DEF456", 11L, 22L, "10.23")).thenReturn(null);

        ResultadoDto result = resultService.guardarResultado("DEF456", 11L, 22L, "10.23");

        assertNull(result);
        verify(grpcClient).guardarResultado("DEF456", 11L, 22L, "10.23");
    }

    @Test
    void testGuardarResultadosBatchDesdeDto() {
        List<ResultadoDto> input = List.of(new ResultadoDto());
        when(grpcClient.guardarResultadosBatch(input)).thenReturn(input);

        List<ResultadoDto> result = resultService.guardarResultadosBatchDesdeDto(input);

        assertEquals(1, result.size());
        verify(grpcClient).guardarResultadosBatch(input);
    }

    @Test
    void testGetResultadoPorId() {
        ResultadoDto mockDto = new ResultadoDto();
        when(grpcClient.getResultadoPorId(100L)).thenReturn(mockDto);

        ResultadoDto result = resultService.getResultadoPorId(100L);

        assertEquals(mockDto, result);
        verify(grpcClient).getResultadoPorId(100L);
    }

    @Test
    void testGetHistorialPdf() {
        List<PdfDto> pdfList = List.of(new PdfDto());
        when(grpcClient.listaPdfHistorico("ABC123")).thenReturn(pdfList);

        List<PdfDto> result = resultService.getHistorialPdf("ABC123");

        assertEquals(1, result.size());
        verify(grpcClient).listaPdfHistorico("ABC123");
    }

    @Test
    void testSolicitarGeneracionPdf() {
        resultService.solicitarGeneracionPdf("ABC123");

        verify(rabbitTemplate).convertAndSend(eq("pdf.request.queue"), eq(Map.of("atletaId", "ABC123")));
    }
}
