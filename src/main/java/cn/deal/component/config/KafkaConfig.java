package cn.deal.component.config;

import cn.deal.component.messaging.channel.*;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.internals.DefaultPartitioner;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;


@Configuration
@EnableBinding({
    CustomerDomainEventChannel.class,
    SwarmCustomerParamChannel.class,
    KuickUserDomainEventChannel.class,
    AvatarGenSuccessChannel.class
})
public class KafkaConfig {
	@Value("${spring.cloud.stream.kafka.binder.brokers}")
    private String bootstrapServersConfig;
    
	@Bean
	public ProducerFactory<String, String> producerFactory() {
	    return new DefaultKafkaProducerFactory<>(producerConfigs());
	}

	@Bean
	public Map<String, Object> producerConfigs() {
	    Map<String, Object> props = new HashMap<>();
	    
	    // See https://kafka.apache.org/documentation/#producerconfigs for more properties
	    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServersConfig);
	    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
	    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
	    props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, DefaultPartitioner.class);
	    props.put(ProducerConfig.RETRIES_CONFIG, 3); // 三次重试
	    
	    return props;
	}

	@Bean(name="rawKafkaTemplate")
	public KafkaTemplate<String, String> kafkaTemplate() {
	    return new KafkaTemplate<String, String>(producerFactory());
	}
}
