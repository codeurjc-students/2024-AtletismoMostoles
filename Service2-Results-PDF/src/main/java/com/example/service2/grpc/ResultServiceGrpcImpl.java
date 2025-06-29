package com.example.service2.grpc;

import com.example.service2.entities.PdfRequest;
import com.example.service2.entities.Result;
import com.example.service2.services.PdfService;
import com.example.service2.services.ResultService;
import com.example.service2.grpc.ResultadoServiceGrpcProto.*;
import com.example.service3.grpc.EventoServiceGrpcProto;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.time.LocalDate;
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
        r.setDisciplinaId(request.getDisciplinaId()); // 🆕
        r.setMarca(request.getMarca());
        r.setFecha(LocalDate.parse(request.getFecha())); // viene como ISO string

        Result saved = resultService.save(r);

        ResultadoMessage response = ResultadoMessage.newBuilder()
                .setId(saved.getId())
                .setAtletaId(saved.getAtletaId())
                .setEventoId(saved.getEventoId())
                .setDisciplinaId(saved.getDisciplinaId()) // 🆕
                .setMarca(saved.getMarca())
                .setFecha(saved.getFecha().toString()) // ISO string
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
                    .setAtletaId(pdf.getAtletaId() != null ? pdf.getAtletaId() : 0)
                    .setEventoId(pdf.getEventoId() != null ? pdf.getEventoId() : 0)
                    .setTimestampGenerado(pdf.getTimestampGenerado().toString()) // ISO
                    .setUrlBlob(pdf.getUrlBlob() != null ? pdf.getUrlBlob() : "")
                    .setEstado(pdf.getEstado())
                    .build());
        }

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }
    @Override
    public void verResultadoPorId(GetResultadoRequest request, StreamObserver<ResultadoMessage> responseObserver) {
        Result result = resultService.findById(request.getId());
        ResultadoMessage message = ResultadoMessage.newBuilder()
                .setId(result.getId())
                .setAtletaId(result.getAtletaId())
                .setEventoId(result.getEventoId())
                .setDisciplinaId(result.getDisciplinaId())
                .setMarca(result.getMarca())
                .setFecha(result.getFecha().toString())
                .build();
        responseObserver.onNext(message);
        responseObserver.onCompleted();
    }

    @Override
    public void buscarResultadosFiltrados(ResultadoFiltroRequest request, StreamObserver<VerResultadosResponse> responseObserver) {
        List<Result> resultados = resultService.findByEventoIdAndDisciplinaId(
                request.getEventId(), request.getDisciplineId()
        );
        VerResultadosResponse.Builder response = VerResultadosResponse.newBuilder();
        for (Result r : resultados) {
            response.addResultados(mapResultToProto(r));
        }
        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    @Override
    public void actualizarResultado(UpdateResultadoRequest request, StreamObserver<ResultadoMessage> responseObserver) {
        Result updated = new Result();
        updated.setId(request.getId());
        updated.setAtletaId(request.getAtletaId());
        updated.setEventoId(request.getEventoId());
        updated.setDisciplinaId(request.getDisciplinaId());
        updated.setMarca(request.getMarca());
        updated.setFecha(LocalDate.parse(request.getFecha()));

        Result saved = resultService.update(request.getId(), updated);

        ResultadoMessage message = ResultadoMessage.newBuilder()
                .setId(saved.getId())
                .setAtletaId(saved.getAtletaId())
                .setEventoId(saved.getEventoId())
                .setDisciplinaId(saved.getDisciplinaId())
                .setMarca(saved.getMarca())
                .setFecha(saved.getFecha().toString())
                .build();
        responseObserver.onNext(message);
        responseObserver.onCompleted();
    }

    @Override
    public void borrarResultado(DeleteResultadoRequest request, StreamObserver<com.example.service3.grpc.EventoServiceGrpcProto.StatusMessage> responseObserver) {
        try {
            resultService.delete(request.getId());
            responseObserver.onNext(EventoServiceGrpcProto.StatusMessage.newBuilder()
                    .setSuccess(true)
                    .setMensaje("Resultado eliminado.")
                    .build());
        } catch (Exception e) {
            responseObserver.onNext(EventoServiceGrpcProto.StatusMessage.newBuilder()
                    .setSuccess(false)
                    .setMensaje("Error: " + e.getMessage())
                    .build());
        }
        responseObserver.onCompleted();
    }

    @Override
    public void guardarResultadosBatch(BatchGuardarRequest request, StreamObserver<BatchGuardarResponse> responseObserver) {
        List<ResultadoMessage> guardados = request.getResultadosList().stream().map(r -> {
            Result nuevo = new Result();
            nuevo.setAtletaId(r.getAtletaId());
            nuevo.setEventoId(r.getEventoId());
            nuevo.setDisciplinaId(r.getDisciplinaId());
            nuevo.setMarca(r.getMarca());
            nuevo.setFecha(LocalDate.parse(r.getFecha()));

            Result saved = resultService.save(nuevo);

            return ResultadoMessage.newBuilder()
                    .setId(saved.getId())
                    .setAtletaId(saved.getAtletaId())
                    .setEventoId(saved.getEventoId())
                    .setDisciplinaId(saved.getDisciplinaId())
                    .setMarca(saved.getMarca())
                    .setFecha(saved.getFecha().toString())
                    .build();
        }).toList();

        BatchGuardarResponse response = BatchGuardarResponse.newBuilder()
                .addAllResultados(guardados)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }


    private ResultadoData mapResultToProto(Result r) {
        return ResultadoData.newBuilder()
                .setId(r.getId())
                .setAtletaId(r.getAtletaId())
                .setEventoId(r.getEventoId())
                .setDisciplinaId(r.getDisciplinaId()) // 🆕
                .setMarca(r.getMarca())
                .setFecha(r.getFecha().toString()) // ISO
                .build();
    }
}
