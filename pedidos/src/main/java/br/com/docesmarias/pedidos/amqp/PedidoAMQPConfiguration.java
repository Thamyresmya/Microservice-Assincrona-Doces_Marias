package br.com.docesmarias.pedidos.amqp;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PedidoAMQPConfiguration {

    //receber e fazer a conversao
    @Bean
    public Jackson2JsonMessageConverter messageConverter(){

        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter messageConverter){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }

    //criando uma Queue(fila)
    @Bean
    public Queue filaDetalhesPedido(){
        return QueueBuilder
                .nonDurable("pagamentos.detalhes-pedido")
                .build();
    }

    //criando uma exchange
    @Bean
    public FanoutExchange fanoutExchange(){
        return ExchangeBuilder
                .fanoutExchange("pagamentos.ex")
                .build();
    }

    //criar o binding
    @Bean
    public Binding bindPagamentoPedido(FanoutExchange fanoutExchange){
        return BindingBuilder
                .bind(filaDetalhesPedido())               //bind com a queue
                .to(fanoutExchange);                      //para exchange
    }

    //Criar Rabbit Admin
    @Bean
    public RabbitAdmin criaRabbitAdmin(ConnectionFactory conn) {
        return new RabbitAdmin(conn);
    }

    //Inicializar o Rabbit e Sinconizar conexão
    @Bean
    public ApplicationListener<ApplicationReadyEvent> inicializaAdmin(RabbitAdmin rabbitAdmin) {
        return event -> rabbitAdmin.initialize();
    }

}
