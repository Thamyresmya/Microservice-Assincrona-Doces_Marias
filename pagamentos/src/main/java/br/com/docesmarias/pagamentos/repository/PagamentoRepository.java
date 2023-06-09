package br.com.docesmarias.pagamentos.repository;

import br.com.docesmarias.pagamentos.model.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;

// class Pagamento e Id do tipo Long
public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {

}
