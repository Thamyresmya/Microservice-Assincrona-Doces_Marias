package br.com.docesmarias.pagamentos.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Entity                      //entidade
@Table(name = "pagamentos")  // alterando o nome da tabela no banco de dados
@Getter                      // criando os getters
@Setter                      // criando os setters
@AllArgsConstructor   // gerar os construtores com 1 parâmetro
@NoArgsConstructor    // gerar os construtores sem parâmetro
public class Pagamento {

    @Id           // chave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY)  //estrategia de controle do Id // identidade na propria tabela
    private Long id;

    @NotNull         //O campo nome não pode ser nulo
    @Positive
    private BigDecimal valor;

    @Size(max = 100)
    private String nome;

    @Size(max = 19)
    private String numero;

    @Size(max = 7)
    private String expiracao;

    @Size(min = 3, max = 3)
    private String codigo;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Status status;

    @NotNull
    private Long pedidoId;

    @NotNull
    private Long formaDePagamentoId;


}
