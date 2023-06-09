package br.com.docesmarias.pedidos.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemDoPedidoDto {

    private Long id;
    private Integer quantidade;
    private String descricao;
}
