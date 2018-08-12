/**
 * 
 */
package net.consensys.spring.awesome.statemachine.test.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.consensys.spring.awesome.statemachine.annotation.State;

@Data
@Document
@NoArgsConstructor
public class Entity {

    @Id
    private String id;
    
    @State
    private EntityStatus state;
    
    private String value;
    
    public Entity(String id, String value) {
        this.id = id;
        this.value = value;
        this.state = EntityStatus.OPENED;
    }
    
    public Entity(String value) {
        this.value = value;
        this.state = EntityStatus.OPENED;
    }
    
    
    
}
