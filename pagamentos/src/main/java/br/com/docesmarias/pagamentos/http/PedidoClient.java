package br.com.docesmarias.pagamentos.http;

import br.com.docesmarias.pagamentos.model.Pedido;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient("pedidos-ms")                       //comunicacao sincrona com pedidos-ms
public interface PedidoClient {
    @RequestMapping(method = RequestMethod.PUT, value = "/pedidos/{id}/pago")  //nome do serviço / tipo da requisição / e o que tem que fazer
    void atualizaPagamento(@PathVariable Long id);                             //informar q Id vai direto na rota // no endpoint


    //metodo para retornar Pedido e itens
    @RequestMapping(method = RequestMethod.GET, value = "/pedidos/{id}")
    Pedido obterItensDoPedido(@PathVariable Long id);

}


