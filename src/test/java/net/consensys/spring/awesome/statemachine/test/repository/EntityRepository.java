/**
 * 
 */
package net.consensys.spring.awesome.statemachine.test.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import net.consensys.spring.awesome.statemachine.test.domain.Entity;

/**
 * @author Gregoire Jeanmart <gregoire.jeanmart@consensys.net>
 *
 */
@Repository
public interface EntityRepository extends MongoRepository<Entity, String> {

}
