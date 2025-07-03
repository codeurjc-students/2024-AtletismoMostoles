package com.example.service1.GrpcClients;

import com.example.service1.DTO.PdfDto;
import com.example.service1.DTO.ResultadoDto;
import com.example.service2.grpc.*;
import com.example.service2.grpc.ResultServiceGrpc.ResultServiceBlockingStub;
import com.example.shared.CommonProto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ResultadoGrpcCliente {

    private final ResultServiceBlockingStub resultadoStub;
    private final PdfServiceGrpc.PdfServiceBlockingStub pdfStub;

    public ResultadoGrpcCliente(ResultServiceBlockingStub resultadoStub,
                                PdfServiceGrpc.PdfServiceBlockingStub pdfStub) {
        this.resultadoStub = resultadoStub;
        this.pdfStub = pdfStub;
    }

    public List<ResultadoDto> getResultadosPorAtleta(String atletaId) {
        AthleteIdRequest request = AthleteIdRequest.newBuilder()
                .setAthleteId(atletaId)
                .build();

        ResultListResponse response = resultadoStub.getResultsByAthleteId(request);

        return response.getResultsList().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<ResultadoDto> verResultadosPorEvento(Long eventoId) {
        EventIdRequest request = EventIdRequest.newBuilder()
                .setEventId(eventoId)
                .build();

        ResultListResponse response = resultadoStub.getResultsByEventId(request);

        return response.getResultsList().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public ResultadoDto getResultadoPorId(Long id) {
        ResultIdRequest request = ResultIdRequest.newBuilder()
                .setId(id)
                .build();

        ResultResponse response = resultadoStub.getResultadoById(request);

        return mapToDto(response);
    }


    public ResultadoDto guardarResultado(String atletaId, Long eventoId, Long disciplinaId, String valor) {
        ResultRequest grpcReq = ResultRequest.newBuilder()
                .setAthleteId(atletaId)
                .setEventId(eventoId)
                .setDisciplineId(disciplinaId)
                .setValue(valor)
                .build();

        CommonProto.StatusMessage rpcResp = resultadoStub.saveResult(grpcReq);

        // Si el backend no devuelve el resultado guardado, devuelves el dto original con éxito asumido
        ResultadoDto dto = new ResultadoDto();
        dto.setAtletaId(atletaId);
        dto.setEventoId(eventoId);
        dto.setDisciplinaId(disciplinaId);
        dto.setValor(valor);
        return dto;
    }

    public List<ResultadoDto> guardarResultadosBatch(List<ResultadoDto> dtos) {
        List<ResultRequest> requests = dtos.stream()
                .map(dto -> ResultRequest.newBuilder()
                        .setAthleteId(dto.getAtletaId())
                        .setEventId(dto.getEventoId())
                        .setDisciplineId(dto.getDisciplinaId())
                        .setValue(dto.getValor())
                        .build())
                .collect(Collectors.toList());

        ResultListRequest batch = ResultListRequest.newBuilder()
                .addAllResults(requests)
                .build();

        CommonProto.StatusMessage status = resultadoStub.saveMultipleResults(batch);

        // Devolvemos los DTOs originales (simulado porque el backend no devuelve ResultResponse)
        return dtos;
    }

    public List<ResultadoDto> getAllResultados() {
        Empty request = Empty.newBuilder().build();
        ResultListResponse response = resultadoStub.getAllResults(request);
        return response.getResultsList().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public List<PdfDto> listaPdfHistorico(String atletaId) {
        AthleteIdRequest request = AthleteIdRequest.newBuilder()
                .setAthleteId(atletaId)
                .build();

        PdfListResponse response = pdfStub.getPdfHistory(request);

        return response.getUrlsList().stream().map(url -> {
            PdfDto dto = new PdfDto();
            dto.setAtletaId(atletaId);
            dto.setUrlBlob(url);
            dto.setEstado("GENERADO"); // Asumido
            return dto;
        }).collect(Collectors.toList());
    }

    // ========= Map helpers =========

    private ResultadoDto mapToDto(ResultResponse response) {
        ResultadoDto dto = new ResultadoDto();
        dto.setId(response.getId());
        ResultRequest req = response.getResult();
        dto.setAtletaId(req.getAthleteId());
        dto.setEventoId(req.getEventId());
        dto.setDisciplinaId(req.getDisciplineId());
        dto.setValor(req.getValue());
        return dto;
    }
}
