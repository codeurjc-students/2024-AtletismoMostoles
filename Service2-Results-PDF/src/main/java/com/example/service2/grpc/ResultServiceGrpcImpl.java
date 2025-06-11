package com.example.service2.grpc;

import com.example.service2.entities.PdfRequest;
import com.example.service2.entities.Result;
import com.example.service2.services.PdfService;
import com.example.service2.services.ResultService;
import com.example.service2.grpc.ResultadoServiceGrpcProto.*;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

@GrpcService
public class ResultServiceGrpcImpl extends ResultadoServiceGrpc.ResultadoServiceImplBase {

    private final ResultService resultService;
    private final PdfService pdfService;

    public ResultServiceGrpcImpl(ResultService resultService, PdfService pdfService) {
        this.resultService = resultService;
        this.pdfService = pdfService;
    }

    @Override
    public void verResultados(VerResultadosRequest request, StreamObserver<VerResultadosResponse> responseObserver) {
        List<Result> resultados = resultService.findByAtletaId(request.getAtletaId());
        VerResultadosResponse.Builder response = VerResultadosResponse.newBuilder();
        for (Result r : resultados) {
            response.addResultados(mapResultToProto(r));
        }
        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    @Override
    public void verResultadosPorEvento(VerResultadosPorEventoRequest request, StreamObserver<VerResultadosResponse> responseObserver) {
        List<Result> resultados = resultService.findByEventoId(request.getEventoId());
        VerResultadosResponse.Builder response = VerResultadosResponse.newBuilder();
        for (Result r : resultados) {
            response.addResultados(mapResultToProto(r));
        }
        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    @Override
    public void guardarResultado(GuardarResultadoRequest request, StreamObserver<ResultadoMessage> responseObserver) {
        Result r = new Result();
        r.setAtletaId(request.getAtletaId());
        r.setEventoId(request.getEventoId());
        r.setMarca(request.getMarca());
        r.setFecha(LocalDate.ofEpochDay(request.getFecha().getSeconds())); // convertir a Instant o LocalDate en la entidad

        Result saved = resultService.save(r);

        ResultadoMessage response = ResultadoMessage.newBuilder()
                .setId(saved.getId())
                .setAtletaId(saved.getAtletaId())
                .setEventoId(saved.getEventoId())
                .setMarca(saved.getMarca())
                .setFecha(request.getFecha()) // ya viene en formato proto
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void listaPdfHistorico(ListaPdfRequest request, StreamObserver<ListaPdfResponse> responseObserver) {
        List<PdfRequest> lista = pdfService.findHistorialPorAtletaId(request.getAtletaId());

        ListaPdfResponse.Builder response = ListaPdfResponse.newBuilder();
        for (PdfRequest pdf : lista) {
            response.addPdfs(PdfData.newBuilder()
                    .setRequestId(pdf.getRequestId())
                    .setTimestampGenerado(com.google.protobuf.Timestamp.newBuilder()
                            .setSeconds(pdf.getTimestampGenerado().getEpochSecond())
                            .build())
                    .setUrlBlob(pdf.getUrlBlob() != null ? pdf.getUrlBlob() : "")
                    .setEstado(pdf.getEstado())
                    .build());
        }

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    private ResultadoData mapResultToProto(Result r) {
        return ResultadoData.newBuilder()
                .setId(r.getId())
                .setAtletaId(r.getAtletaId())
                .setEventoId(r.getEventoId())
                .setMarca(r.getMarca())
                .setFecha(com.google.protobuf.Timestamp.newBuilder()
                        .setSeconds(r.getFecha().atStartOfDay(ZoneOffset.UTC).toInstant().getEpochSecond())
                        .build())
                .build();
    }
}
