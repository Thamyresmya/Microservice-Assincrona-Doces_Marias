package br.com.docesmarias.pedidos.amqp;

import br.com.docesmarias.pedidos.dto.PagamentoDto;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PagamentoListener {

    //metodo para receber as mensagens do MS-Pagamentos
    @RabbitListener(queues = "pagamentos.detalhes-pedido")                       //receber da fila
    public void recebeMensagem(PagamentoDto pagamento){                          //vai receber um pagamentoDTO
        String mensagem = """
                   Dados do pagamento: %s
                   Nome: %s
                   Número do Pedido: %s
                   Valor R$: %s
                   Status: %s  
                """.formatted(pagamento.getId(),
                pagamento.getNome(),
                pagamento.getPedidoId(),
                pagamento.getValor(),
                pagamento.getStatus());

        System.out.println("Recebi a mensagem " + mensagem);
    }
}
