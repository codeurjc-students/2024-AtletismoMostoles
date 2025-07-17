package com.example.service2.grpc;

import com.example.shared.CommonProto.StatusMessage;
import com.example.service2.entities.Result;
import com.example.service2.services.ResultServiceImpl;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@GrpcService
public class ResultServiceGrpcImpl extends ResultServiceGrpc.ResultServiceImplBase {

    @Autowired
    protected ResultServiceImpl resultService;

    @Override
    public void saveResult(ResultRequest request, StreamObserver<StatusMessage> responseObserver) {
        Result result = new Result(null, request.getEventId(), request.getDisciplineId(), request.getAthleteId(), request.getValue());
        Long id = resultService.saveResult(result).getId();
        StatusMessage response = StatusMessage.newBuilder()
                .setSuccess(true)
                .setMensaje("Resultado guardado correctamente")
                .setId(id)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void saveMultipleResults(ResultListRequest request, StreamObserver<StatusMessage> responseObserver) {
        List<Result> results = request.getResultsList().stream().map(r ->
                new Result(null, r.getEventId(), r.getDisciplineId(), r.getAthleteId(), r.getValue())
        ).collect(Collectors.toList());
        resultService.saveAll(results);
        StatusMessage response = StatusMessage.newBuilder()
                .setSuccess(true)
                .setMensaje("Resultados guardados correctamente")
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getResultsByAthleteId(AthleteIdRequest request, StreamObserver<ResultListResponse> responseObserver) {
        List<Result> list = resultService.getResultsByAthleteId(request.getAthleteId());
        ResultListResponse response = ResultListResponse.newBuilder()
                .addAllResults(list.stream().map(r -> ResultResponse.newBuilder()
                                .setId(r.getId())
                                .setResult(ResultRequest.newBuilder()
                                        .setEventId(r.getEventId())
                                        .setDisciplineId(r.getDisciplineId())
                                        .setAthleteId(r.getAthleteId())
                                        .setValue(r.getValue())
                                        .build())
                                .build())
                        .collect(Collectors.toList()))
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getResultsByEventId(EventIdRequest request, StreamObserver<ResultListResponse> responseObserver) {
        List<Result> list = resultService.getResultsByEventId(request.getEventId());
        ResultListResponse response = ResultListResponse.newBuilder()
                .addAllResults(list.stream().map(r -> ResultResponse.newBuilder()
                                .setId(r.getId())
                                .setResult(ResultRequest.newBuilder()
                                        .setEventId(r.getEventId())
                                        .setDisciplineId(r.getDisciplineId())
                                        .setAthleteId(r.getAthleteId())
                                        .setValue(r.getValue())
                                        .build())
                                .build())
                        .collect(Collectors.toList()))
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getAllResults(Empty request, StreamObserver<ResultListResponse> responseObserver) {
        List<Result> list = resultService.getAllResults();
        ResultListResponse response = ResultListResponse.newBuilder()
                .addAllResults(list.stream().map(r -> ResultResponse.newBuilder()
                                .setId(r.getId())
                                .setResult(ResultRequest.newBuilder()
                                        .setEventId(r.getEventId())
                                        .setDisciplineId(r.getDisciplineId())
                                        .setAthleteId(r.getAthleteId())
                                        .setValue(r.getValue())
                                        .build())
                                .build())
                        .collect(Collectors.toList()))
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
