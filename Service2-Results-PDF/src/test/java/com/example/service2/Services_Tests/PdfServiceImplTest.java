package com.example.service2.Services_Tests;

import com.example.service2.dto.AthleteDto;
import com.example.service2.dto.DisciplineDto;
import com.example.service2.dto.EventDto;
import com.example.service2.entities.PdfHistory;
import com.example.service2.entities.Result;
import com.example.service2.repositories.PdfHistoryRepository;
import com.example.service2.repositories.ResultRepository;
import com.example.service2.services.AzureBlobService;
import com.example.service2.services.PdfServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.client.RestTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PdfServiceImplTest {

    @InjectMocks
    private PdfServiceImpl pdfService;

    @Mock
    private PdfHistoryRepository pdfHistoryRepository;

    @Mock
    private ResultRepository resultRepository;

    @Mock
    private AzureBlobService azureBlobService;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void generarPdfParaAtleta_shouldUploadAndNotify() {
        String athleteId = "123";
        Long eventId = 1L;
        Long disciplineId = 1L;
        String expectedUrl = "https://storage.blob.core.windows.net/resultspdf/file123.pdf";

        // Mock de resultado original
        List<Result> mockResults = List.of(
                new Result(1L, eventId, disciplineId, athleteId, "12.34")
        );
        when(resultRepository.findByAthleteId(athleteId)).thenReturn(mockResults);

        // Mock AthleteDto
        AthleteDto athleteDto = new AthleteDto();
        ReflectionTestUtils.setField(athleteDto, "firstName", "Juan");
        ReflectionTestUtils.setField(athleteDto, "lastName", "Pérez");

        // Mocks de respuestas REST
        when(restTemplate.getForObject(contains("/api/athletes/"), eq(AthleteDto.class)))
                .thenReturn(athleteDto);
        when(restTemplate.getForObject(contains("/api/events/"), eq(EventDto.class)))
                .thenReturn(new EventDto(1L, "Evento 1"));
        when(restTemplate.getForObject(contains("/api/disciplines/"), eq(DisciplineDto.class)))
                .thenReturn(new DisciplineDto(1L, "100m"));

        // Mock de subida a Azure
        when(azureBlobService.uploadPdf(anyString(), any())).thenReturn(expectedUrl);

        // Ejecutar servicio
        pdfService.generarPdfParaAtleta(athleteId);

        // Verificaciones
        verify(resultRepository).findByAthleteId(athleteId);
        verify(azureBlobService).uploadPdf(anyString(), any());
        verify(pdfHistoryRepository).save(argThat(pdf ->
                pdf.getAthleteId().equals(athleteId) &&
                        pdf.getUrl().equals(expectedUrl)
        ));

        ArgumentCaptor<Map<String, String>> mapCaptor = ArgumentCaptor.forClass(Map.class);

        verify(rabbitTemplate).convertAndSend(
                eq("pdf.confirmation.queue"),
                mapCaptor.capture()
        );

        Map<String, String> capturedMap = mapCaptor.getValue();
        assertEquals("123", capturedMap.get("atletaId"));
        assertEquals("https://storage.blob.core.windows.net/resultspdf/file123.pdf", capturedMap.get("url"));

    }

    @Test
    void getUrlsByAthleteId_shouldReturnPdfLinks() {
        String athleteId = "456";
        List<PdfHistory> mockList = List.of(
                new PdfHistory(athleteId, "https://pdf1.com"),
                new PdfHistory(athleteId, "https://pdf2.com")
        );

        when(pdfHistoryRepository.findByAthleteId(athleteId)).thenReturn(mockList);

        List<String> urls = pdfService.getUrlsByAthleteId(athleteId);

        assertEquals(2, urls.size());
        assertTrue(urls.contains("https://pdf1.com"));
        assertTrue(urls.contains("https://pdf2.com"));
    }

    @Test
    void saveUrlForAthlete_shouldSaveCorrectly() {
        String athleteId = "789";
        String url = "https://storage/test.pdf";

        pdfService.saveUrlForAthlete(athleteId, url);

        ArgumentCaptor<PdfHistory> captor = ArgumentCaptor.forClass(PdfHistory.class);
        verify(pdfHistoryRepository).save(captor.capture());

        PdfHistory saved = captor.getValue();
        assertEquals(athleteId, saved.getAthleteId());
        assertEquals(url, saved.getUrl());
    }
}
