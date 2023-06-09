package br.com.docesmarias.avaliacao.amqp;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AvaliacaoAMQPConfiguration {
    @Bean
    public Jackson2JsonMessageConverter messageConverter(){
        return  new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter messageConverter){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return  rabbitTemplate;
    }

    //criando a queue
    @Bean
    public Queue filaDetalhesAvaliacao() {
        return QueueBuilder
                .nonDurable("pagamentos.detalhes-avaliacao")
                .deadLetterExchange("pagamentos.dlx")                     //vincula a dlx a fila principal
                .build();
    }

    //criando a queue DLQ
    @Bean
    public Queue filaDlqDetalhesAvaliacao() {
        return QueueBuilder
                .nonDurable("pagamentos.detalhes-avaliacao-dlq")
                .build();
    }

    //criando a exchange
    @Bean
    public FanoutExchange fanoutExchange() {
        return ExchangeBuilder
                .fanoutExchange("pagamentos.ex")
                .build();
    }

    //criando a exchange DLX
    @Bean
    public FanoutExchange deadLetterExchange() {
        return ExchangeBuilder
                .fanoutExchange("pagamentos.dlx")
                .build();
    }

    //criando binding exchange e queue
    @Bean
    public Binding bindDlxPagamentoPedido() {
        return BindingBuilder
                .bind(filaDlqDetalhesAvaliacao())          // fila DLQ
                .to(deadLetterExchange());                 // exchage DLX
    }

    //criando binding exchange e queue DLX com DLQ
    @Bean
    public Binding bindPagamentoPedido() {
        return BindingBuilder
                .bind(filaDetalhesAvaliacao())
                .to(fanoutExchange());
    }

    @Bean
    public RabbitAdmin criaRabbitAdmin(ConnectionFactory conn) {
        return new RabbitAdmin(conn);
    }

    @Bean
    public ApplicationListener<ApplicationReadyEvent> inicializaAdmin(RabbitAdmin rabbitAdmin) {
        return event -> rabbitAdmin.initialize();
    }

}
