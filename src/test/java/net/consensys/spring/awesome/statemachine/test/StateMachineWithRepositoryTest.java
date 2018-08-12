/**
 * 
 */
package net.consensys.spring.awesome.statemachine.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import lombok.extern.slf4j.Slf4j;
import net.consensys.spring.awesome.statemachine.configuration.StateMachineConfiguration;
import net.consensys.spring.awesome.statemachine.exception.EntityNotFoundException;
import net.consensys.spring.awesome.statemachine.service.StateMachine;
import net.consensys.spring.awesome.statemachine.test.domain.Entity;
import net.consensys.spring.awesome.statemachine.test.domain.EntityEvent;
import net.consensys.spring.awesome.statemachine.test.domain.EntityStatus;
import net.consensys.spring.awesome.statemachine.test.repository.EntityRepository;

@RunWith(SpringRunner.class)
@SpringBootTest()
@SpringBootApplication
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@EnableMongoRepositories(basePackages = "net.consensys.spring.awesome.statemachine.test.repository")
@TestPropertySource(locations="classpath:application.yml")
@Slf4j
public class StateMachineWithRepositoryTest {

    @Configuration
    static class ConfigurationRepository  extends StateMachineConfiguration<EntityStatus, EntityEvent, Entity, String> {

        @Autowired
        public ConfigurationRepository(EntityRepository repository) {
            super(repository);
            
            add(transition()
                    .event(EntityEvent.START_WORKING)
                    .from(EntityStatus.OPENED)
                    .to(EntityStatus.IN_PROGRESS)
                    .before((e, c) -> log.info("running before with entity {}", e))
                    .after((e, c) -> log.info("running after with entity {}", e))
                    .build());
            
            add(transition()
                    .event(EntityEvent.DECISION)
                    .from(EntityStatus.IN_PROGRESS)
                    .to(EntityStatus.CLOSED, (e, c) -> !e.getValue().equals("cancel"))
                    .to(EntityStatus.CANCELED, (e, c) -> e.getValue().equals("cancel"))
                    .before((e, c) -> log.info("running before with entity {}", e))
                    .after((e, c) -> log.info("running after with entity {}", e))
                    .build());

        }
    }
    
    @Autowired
    StateMachine<EntityStatus, EntityEvent, Entity, String> stateMachine;
    @Autowired
    EntityRepository repository;
    
    @Test
    public void happyPath1() throws Exception {
       final String value = "value";
       
       // Persist object
       Entity entity = repository.save(new Entity(value));
       log.info("entity = {}", entity);
       assertEquals(EntityStatus.OPENED, entity.getState());
       
       // Trigger transition
       stateMachine.onTransition(EntityEvent.START_WORKING, entity.getId());
       
       // Fetch object
       Entity result = repository.findOne(entity.getId());
       log.info("entity = {}", entity);
       assertEquals(EntityStatus.IN_PROGRESS, result.getState());
    }
    
    @Test
    public void happyPath2() {
       final String value = "value";
       
       // Persist object
       Entity entity = repository.save(new Entity(value));
       log.info("entity = {}", entity);
       assertEquals(EntityStatus.OPENED, entity.getState());
       
       // start working
       stateMachine.onTransition(EntityEvent.START_WORKING, entity.getId());
       Entity result = repository.findOne(entity.getId());
       log.info("entity = {}", result);
       assertEquals(EntityStatus.IN_PROGRESS, result.getState());
       
       // Trigger transition
       stateMachine.onTransition(EntityEvent.DECISION, entity.getId());
       Entity result2 = repository.findOne(entity.getId());
       log.info("entity = {}", result2);
       assertEquals(EntityStatus.CLOSED, result2.getState());
       
    }
    
    @Test
    public void happyPath3() {
       final String value = "cancel";
       
       // Persist object
       Entity entity = repository.save(new Entity(value));
       log.info("entity = {}", entity);
       assertEquals(EntityStatus.OPENED, entity.getState());
       
       // start working
       stateMachine.onTransition(EntityEvent.START_WORKING, entity.getId());
       Entity result = repository.findOne(entity.getId());
       log.info("entity = {}", result);
       assertEquals(EntityStatus.IN_PROGRESS, result.getState());
       
       // Trigger transition
       stateMachine.onTransition(EntityEvent.DECISION, entity.getId());
       Entity result2 = repository.findOne(entity.getId());
       log.info("entity = {}", result2);
       assertEquals(EntityStatus.CANCELED, result2.getState());
       
    }
    
    @Test(expected=EntityNotFoundException.class)
    public void entityNotFound() {
       stateMachine.onTransition(EntityEvent.START_WORKING, "unknow");
    }
    
}
