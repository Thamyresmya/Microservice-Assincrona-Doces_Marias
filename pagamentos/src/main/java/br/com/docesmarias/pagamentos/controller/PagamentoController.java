package br.com.docesmarias.pagamentos.controller;

import br.com.docesmarias.pagamentos.dto.PagamentoDto;
import br.com.docesmarias.pagamentos.service.PagamentoService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;

@RestController
@RequestMapping("/pagamentos")
public class PagamentoController {

    @Autowired
    private PagamentoService service;

    @Autowired
    private RabbitTemplate rabbitTemplate;              //


    //Para requisição do tipo Get -> Listar todos os pagamentos ja com paginação
    @GetMapping
    public Page<PagamentoDto> listar(@PageableDefault(size = 10) Pageable paginacao) {
        return service.obterTodos(paginacao);                        // chamada do metodo
    }


    //Para requisição do tipo Get por Id -> detalhar todos os pagamentos
    @GetMapping("/{id}")
    public ResponseEntity<PagamentoDto> detalhar(@PathVariable @NotNull Long id) {         // @PathVariable -> vai enviar o Id no caminho do EndPoint
        PagamentoDto dto = service.obterPorId(id);

        return ResponseEntity.ok(dto);
    }

    //Para cadastar um novo pagamento
    @PostMapping
    public ResponseEntity<PagamentoDto> cadastrar(@RequestBody @Valid PagamentoDto dto, UriComponentsBuilder uriBuilder) {
        PagamentoDto pagamento = service.criarPagamento(dto);
        URI endereco = uriBuilder.path("/pagamentos/{id}").buildAndExpand(pagamento.getId()).toUri();

        rabbitTemplate.convertAndSend("pagamentos.ex","", pagamento);
        return ResponseEntity.created(endereco).body(pagamento);
    }


    //Para alterar
    @PutMapping("/{id}")
    public ResponseEntity<PagamentoDto> atualizar(@PathVariable @NotNull Long id, @RequestBody @Valid PagamentoDto dto) { //Path vai no caminho da rota e o Resquest vai no corpo da requisição
        PagamentoDto atualizado = service.atualizarPagamento(id, dto);
        return ResponseEntity.ok(atualizado);
    }


    //Excluir pagamento por id
    @DeleteMapping("/{id}")
    public ResponseEntity<PagamentoDto> remover(@PathVariable @NotNull Long id) {
        service.excluirPagamento(id);
        return ResponseEntity.noContent().build();
    }

    //alterar confirmação do pedido
    @PatchMapping("/{id}/confirmar")
    @CircuitBreaker(name = "atualizaPedido", fallbackMethod = "pagamentoAutorizadoComIntegracaoPendente")    //anotação do circuit breaker
    public void confirmarPagamento(@PathVariable @NotNull Long id){             // com parametro do Id
        service.confirmarPagamento(id);                                         // chama o metodo confPagto com o Id
    }


    //Para o fallback -> 
    public void pagamentoAutorizadoComIntegracaoPendente(Long id, Exception e){
        service.alteraStatus(id);
    }

}
