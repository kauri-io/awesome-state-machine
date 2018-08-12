/**
 * 
 */
package net.consensys.spring.awesome.statemachine.test.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Document
@NoArgsConstructor
public class EntityWithoutState {

    @Id
    private String id;
    
    private EntityStatus state;
    
    private String value;
    
    public EntityWithoutState(String id, String value) {
        this.id = id;
        this.value = value;
        this.state = EntityStatus.OPENED;
    }
    
    public EntityWithoutState(String value) {
        this.value = value;
        this.state = EntityStatus.OPENED;
    }
    
    
    
}
