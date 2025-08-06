package com.example.service1.GrpcClients;

import com.example.service1.DTO.PdfDto;
import com.example.service1.DTO.ResultDto;
import com.example.service2.grpc.*;
import com.example.shared.CommonProto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ResultGrpcClient {

    private final ResultServiceGrpc.ResultServiceBlockingStub resultStub;
    private final PdfServiceGrpc.PdfServiceBlockingStub pdfStub;

    public ResultGrpcClient(ResultServiceGrpc.ResultServiceBlockingStub resultStub,
                            PdfServiceGrpc.PdfServiceBlockingStub pdfStub) {
        this.resultStub = resultStub;
        this.pdfStub = pdfStub;
    }

    public List<ResultDto> getResultsByAthlete(String athleteId) {
        AthleteIdRequest request = AthleteIdRequest.newBuilder()
                .setAthleteId(athleteId)
                .build();

        ResultListResponse response = resultStub.getResultsByAthleteId(request);

        return response.getResultsList().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<ResultDto> getResultsByEvent(Long eventId) {
        EventIdRequest request = EventIdRequest.newBuilder()
                .setEventId(eventId)
                .build();

        ResultListResponse response = resultStub.getResultsByEventId(request);

        return response.getResultsList().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public ResultDto getResultById(Long id) {
        ResultIdRequest request = ResultIdRequest.newBuilder()
                .setId(id)
                .build();

        ResultResponse response = resultStub.getResultById(request);

        return mapToDto(response);
    }


    public ResultDto saveResult(String athleteId, Long eventId, Long disciplineId, String value) {
        ResultRequest grpcReq = ResultRequest.newBuilder()
                .setAthleteId(athleteId)
                .setEventId(eventId)
                .setDisciplineId(disciplineId)
                .setValue(value)
                .build();

        CommonProto.StatusMessage rpcResp = resultStub.saveResult(grpcReq);

        ResultDto dto = new ResultDto();
        if (rpcResp.hasId()) {
            dto.setId(rpcResp.getId());
        }
        dto.setAthleteId(athleteId);
        dto.setEventId(eventId);
        dto.setDisciplineId(disciplineId);
        dto.setValue(value);
        return dto;
    }

    public List<ResultDto> saveResultsBatch(List<ResultDto> DTos) {
        List<ResultRequest> requests = DTos.stream()
                .map(dto -> ResultRequest.newBuilder()
                        .setAthleteId(dto.getAthleteId())
                        .setEventId(dto.getEventId())
                        .setDisciplineId(dto.getDisciplineId())
                        .setValue(dto.getValue())
                        .build())
                .collect(Collectors.toList());
        ResultListRequest batch = ResultListRequest.newBuilder()
                .addAllResults(requests)
                .build();

        resultStub.saveMultipleResults(batch);

        return DTos;
    }

    public List<ResultDto> getAllResults() {
        Empty request = Empty.newBuilder().build();
        ResultListResponse response = resultStub.getAllResults(request);
        return response.getResultsList().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public List<PdfDto> listPdfHistorical(String athleteId) {
        AthleteIdRequest request = AthleteIdRequest.newBuilder()
                .setAthleteId(athleteId)
                .build();

        PdfListResponse response = pdfStub.getPdfHistory(request);

        return response.getUrlsList().stream().map(url -> {
            PdfDto dto = new PdfDto();
            dto.setAtletaId(athleteId);
            dto.setUrlBlob(url);
            dto.setEstado("GENERADO");
            return dto;
        }).collect(Collectors.toList());
    }

    // ========= Map helpers =========

    private ResultDto mapToDto(ResultResponse response) {
        ResultDto dto = new ResultDto();
        dto.setId(response.getId());
        ResultRequest req = response.getResult();
        dto.setAthleteId(req.getAthleteId());
        dto.setEventId(req.getEventId());
        dto.setDisciplineId(req.getDisciplineId());
        dto.setValue(req.getValue());
        return dto;
    }
}
