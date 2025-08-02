package za.pulsewatch.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import za.pulsewatch.dto.PPGAnalysisResponse;

@Service
public class MessagePublisher {

    private final RabbitTemplate rabbitTemplate;
    
    @Value("${ppg.exchange.name}")
    private String exchangeName;
    
    @Value("${ppg.routing.key}")
    private String routingKey;

    @Autowired
    public MessagePublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishAnalysisResult(PPGAnalysisResponse response) {
        rabbitTemplate.convertAndSend(exchangeName, routingKey, response);
    }

    public void publishRealtimeUpdate(String userId, PPGAnalysisResponse response) {
        String realtimeRoutingKey = "realtime." + userId;
        rabbitTemplate.convertAndSend(exchangeName, realtimeRoutingKey, response);
    }
}
