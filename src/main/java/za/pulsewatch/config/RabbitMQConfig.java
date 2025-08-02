package za.pulsewatch.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String PPG_EXCHANGE = "ppg.exchange";
    public static final String PPG_ANALYSIS_QUEUE = "ppg.analysis.queue";
    public static final String PPG_REALTIME_QUEUE = "ppg.realtime.queue";
    public static final String PPG_ROUTING_KEY = "ppg.analysis";
    public static final String PPG_REALTIME_ROUTING_KEY = "ppg.realtime.*";

    @Bean
    public TopicExchange ppgExchange() {
        return new TopicExchange(PPG_EXCHANGE);
    }

    @Bean
    public Queue ppgAnalysisQueue() {
        return new Queue(PPG_ANALYSIS_QUEUE, true);
    }

    @Bean
    public Queue ppgRealtimeQueue() {
        return new Queue(PPG_REALTIME_QUEUE, true);
    }

    @Bean
    public Binding ppgAnalysisBinding() {
        return BindingBuilder
                .bind(ppgAnalysisQueue())
                .to(ppgExchange())
                .with(PPG_ROUTING_KEY);
    }

    @Bean
    public Binding ppgRealtimeBinding() {
        return BindingBuilder
                .bind(ppgRealtimeQueue())
                .to(ppgExchange())
                .with(PPG_REALTIME_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}
