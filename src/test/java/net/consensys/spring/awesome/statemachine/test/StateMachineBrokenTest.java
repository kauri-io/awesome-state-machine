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
import net.consensys.spring.awesome.statemachine.exception.EntityHasNoStateAnnotationException;
import net.consensys.spring.awesome.statemachine.service.StateMachine;
import net.consensys.spring.awesome.statemachine.test.domain.EntityEvent;
import net.consensys.spring.awesome.statemachine.test.domain.EntityStatus;
import net.consensys.spring.awesome.statemachine.test.domain.EntityWithoutState;

@RunWith(SpringRunner.class)
@SpringBootTest()
@SpringBootApplication
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@EnableMongoRepositories(basePackages = "net.consensys.spring.awesome.statemachine.test.repository")
@TestPropertySource(locations="classpath:application.yml")
@Slf4j
public class StateMachineBrokenTest {
    
    static final String ID = "id";
    static final String VALUE = "value";
    
    @Configuration
    static class ConfigurationNoRepository  extends StateMachineConfiguration<EntityStatus, EntityEvent, EntityWithoutState, String> {

        public ConfigurationNoRepository() {
            super();
            
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
                    .to(EntityStatus.CLOSED, (e, c) -> e.getValue().equals("close"))
                    .to(EntityStatus.CANCELED, (e, c) -> e.getValue().equals("cancel"))
                    .before((e, c) -> log.info("running before with entity {}", e))
                    .after((e, c) -> log.info("running after with entity {}", e))
                    .build());

        }
    }
    
    @Autowired
    StateMachine<EntityStatus, EntityEvent, EntityWithoutState, String> stateMachine;
    
    @Test(expected=EntityHasNoStateAnnotationException.class)
    public void entityWithoutState() {
        EntityWithoutState e = new EntityWithoutState(ID, VALUE);
        assertEquals(EntityStatus.OPENED, e.getState());
        
        stateMachine.onTransition(EntityEvent.START_WORKING, ID, e, null);
    }
    
    
}
