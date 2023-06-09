package br.com.docesmarias.pagamentos.service;

import br.com.docesmarias.pagamentos.dto.PagamentoDto;
import br.com.docesmarias.pagamentos.http.PedidoClient;
import br.com.docesmarias.pagamentos.model.Pagamento;
import br.com.docesmarias.pagamentos.model.Status;
import br.com.docesmarias.pagamentos.repository.PagamentoRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

//manupulação do repositorio
@Service
public class PagamentoService {

    @Autowired     //injeção de dependencia
    private PagamentoRepository repository;      // nosso repository

    @Autowired
    private ModelMapper modelMapper;            //permite transferir dados de forma facilitada entre a entidade e o DTO.

    @Autowired
    private PedidoClient pedido;               //injeção de dependencia do Pedido Client do MS



    //Devolver paginado os objetos do tipo pagamentos DTO
    public Page<PagamentoDto> obterTodos(Pageable paginacao) {
        return repository
                .findAll(paginacao)                                        // pega todos os pagamentos
                .map(p -> modelMapper.map(p, PagamentoDto.class));         // mapeia de novo para o pagamentosDTO
    }

    //Devolver um pagamento especifico por ID
    public PagamentoDto obterPorId(Long id) {
        Pagamento pagamento = repository.findById(id)                 //localiza no repositorio pelo id
                .orElseThrow(() -> new EntityNotFoundException());    // se houver uma exception

        PagamentoDto dto = modelMapper.map(pagamento, PagamentoDto.class);             //tranf. Pagamento em pagamentoDto
        dto.setItens(pedido.obterItensDoPedido(pagamento.getPedidoId()).getItens());
        return dto;
    }


    //Cria um pagamento
    public PagamentoDto criarPagamento(PagamentoDto dto) {              // recebe dados DTO
        Pagamento pagamento = modelMapper.map(dto, Pagamento.class);    // tranf de DTO para pagto
        pagamento.setStatus(Status.CRIADO);                             // muda o status como Criado
        repository.save(pagamento);                                     // salva no banco

        return modelMapper.map(pagamento, PagamentoDto.class);          //devolve transf de pagamento para pagtoDTO
    }

    //Atualizando pagamentos
    public PagamentoDto atualizarPagamento(Long id, PagamentoDto dto) {         //recebe dois paramentros
        Pagamento pagamento = modelMapper.map(dto, Pagamento.class);            // tranf de DTO para pagto
        pagamento.setId(id);                                                    //
        pagamento = repository.save(pagamento);                                 // salva alteração no banco
        return modelMapper.map(pagamento, PagamentoDto.class);                  //devolve transf de pagamento para pagtoDTO
    }

    //Excluir pagamento
    public void excluirPagamento(Long id) {
        repository.deleteById(id);            //deleta o Id recebido
    }

    //Confirmar pagamento com status Confirmado
    public void confirmarPagamento(Long id){                         //passar o id
        Optional<Pagamento> pagamento = repository.findById(id);    //recupera o pagamento no banco

        if (!pagamento.isPresent()) {
            throw new EntityNotFoundException();
        }

        pagamento.get().setStatus(Status.CONFIRMADO);                     // set o status com "confirmado"
        repository.save(pagamento.get());                                 //salva no banco
        pedido.atualizaPagamento(pagamento.get().getPedidoId());          //chama o client PEDIDOS para fazer a atualização, passando o Id do pedido
    }


    //Para alterar o status quando entrar no circuit breaker
    public void alteraStatus(Long id) {
        Optional<Pagamento> pagamento = repository.findById(id);    //recupera o pagamento no banco

        if (!pagamento.isPresent()) {
            throw new EntityNotFoundException();
        }

        pagamento.get().setStatus(Status.CONFIRMADO_SEM_INTEGRACAO);      // set o status com "confirmado sem integração"
        repository.save(pagamento.get());                                 //salva no banco

    }



}

















