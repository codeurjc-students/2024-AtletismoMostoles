package com.example.service1.GrpcClientes;

import com.example.service2.grpc.ResultadoServiceGrpc;
import com.example.service2.grpc.ResultadoServiceGrpc.ResultadoServiceBlockingStub;
import com.example.service2.grpc.ResultadoServiceGrpcProto.GuardarResultadoRequest;
import com.example.service2.grpc.ResultadoServiceGrpcProto.ListaPdfRequest;
import com.example.service2.grpc.ResultadoServiceGrpcProto.ListaPdfResponse;
import com.example.service2.grpc.ResultadoServiceGrpcProto.PdfData;
import com.example.service2.grpc.ResultadoServiceGrpcProto.ResultadoMessage;
import com.example.service2.grpc.ResultadoServiceGrpcProto.VerResultadosRequest;
import com.example.service2.grpc.ResultadoServiceGrpcProto.VerResultadosResponse;

import com.example.service1.DTO.ResultadoDto;
import com.example.service1.DTO.PdfDto;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ResultadoGrpcCliente {

    private final ResultadoServiceBlockingStub resultadoStub;

    public ResultadoGrpcCliente(ResultadoServiceBlockingStub resultadoStub) {
        this.resultadoStub = resultadoStub;
    }

    /**
     * Invoca al RPC verResultados en Servicio2 y convierte la respuesta a List<ResultadoDto>.
     */
    public List<ResultadoDto> verResultados(Long atletaId) {
        VerResultadosRequest request = VerResultadosRequest.newBuilder()
                .setAtletaId(atletaId)
                .build();

        VerResultadosResponse response = resultadoStub.verResultados(request);

        return response.getResultadosList().stream()
                .map(r -> new ResultadoDto(
                        r.getId(),
                        r.getAtletaId(),
                        r.getEventoId(),
                        r.getMarca(),
                        LocalDate.parse(r.getFecha())
                ))
                .collect(Collectors.toList());
    }

    /**
     * Invoca al RPC guardarResultado en Servicio2 y devuelve un ResultadoDto.
     */
    public ResultadoDto guardarResultado(Long atletaId, Long eventoId, double marca, LocalDate fecha) {
        GuardarResultadoRequest grpcReq = GuardarResultadoRequest.newBuilder()
                .setAtletaId(atletaId)
                .setEventoId(eventoId)
                .setMarca(marca)
                .setFecha(fecha.toString())
                .build();

        ResultadoMessage rpcResp = resultadoStub.guardarResultado(grpcReq);

        return new ResultadoDto(
                rpcResp.getId(),
                rpcResp.getAtletaId(),
                rpcResp.getEventoId(),
                rpcResp.getMarca(),
                LocalDate.parse(rpcResp.getFecha())
        );
    }

    /**
     * Invoca al RPC listaPdfHistorico en Servicio2 y devuelve List<PdfDto>.
     */
    public List<PdfDto> listaPdfHistorico(Long atletaId) {
        ListaPdfRequest request = ListaPdfRequest.newBuilder()
                .setAtletaId(atletaId)
                .build();

        ListaPdfResponse response = resultadoStub.listaPdfHistorico(request);

        return response.getPdfsList().stream()
                .map(p -> new PdfDto(
                        p.getRequestId(),
                        LocalDateTime.parse(p.getTimestampGenerado()),
                        p.getUrlBlob(),
                        p.getEstado()
                ))
                .collect(Collectors.toList());
    }
}
