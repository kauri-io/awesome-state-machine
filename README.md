# awesome-state-machine

awesome-state-machine is a Spring-Boot library which aims to provide an easy-to-use state machine for Spring-Boot with persistence of the state. 

## Getting started

### Dependency 

Add the following Maven dependency


```
<dependency>
    <groupId>net.consensys.spring</groupId>
    <artifactId>awesome-state-machine</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```


### States 

Declare an Enumeration every states

```
public enum TaskState {
    OPENED, IN_PROGRESS, CLOSED, CANCELED;

}
```

### Events 

Declare an Enumeration to list all events that can change the state

```
public enum TaskEvent {
    START_WORKING, END_TASK;
}

```


### Entity 

On the entity that drives the state, add a field annotated with `@State`

```
@Data @NoArgsConstructor
@Document
public class Task {
    private @Id String id;
    private @State State state;
    private String content;
}
```


### Configuration

Configure your state machine by creating a `@Configuration` class that sxtends `StateMachineConfiguration<S, E, T, I>`

| Type | Description |
| -------- | -------- | 
| S | State enumeration |
| E | Event enumeration |
| T | Entity type |
| I | ID type  |


**Parameters:**

| Name | Description |
| -------- | -------- | 
| repository | CRUDRepository used to retrieve before and persist the entity after the state transition |
| transitions | List of transitions |


**Transitions:**

| Name | Type | Mandatory | Description |
| -------- | -------- | -------- | -------- | 
| event | Event | yes [1] | Event triggered |
| from | State  | yes [1] | Initial state |
| to | State  | yes [1, n] | Target state transitions |
| before | Consumer | no [0, 1] | Execute the consumer before the state transition  |
| after | Consumer<Entiyt, Context> | no [0, 1] | Execute the consumer after the state transition |


```
@Configuration
static class MyStateMachineConfiguration extends StateMachineConfiguration<TaskState, TaskEvent, Task, String> {

    @Autowired
    public ConfigurationNoRepository(EntityRepository repository) {
        super(repository);
        
        add(transition()
                .event(TaskEvent.START_WORKING)
                .from(TaskState.OPENED)
                .to(TaskState.IN_PROGRESS)
                .before((e, c) -> log.info("running before with entity {}", e))
                .after((e, c) -> log.info("running after with entity {}", e))
                .build());
        
        add(transition()
                .event(TaskEvent.END_TASK)
                .from(TaskState.IN_PROGRESS)
                .to(TaskState.CLOSED, (e, c) -> !e.getValue().equals("cancel"))
                .to(TaskState.CANCELED, (e, c) -> e.getValue().equals("cancel"))
                .before((e, c) -> log.info("running before with entity {}", e))
                .after((e, c) -> log.info("running after with entity {}", e))
                .build());

    }
}
 ```


### Runtime
 
After the configuration loaded, a `StateMachine<S, E, T, I>` service is available to trigger transition based on event

| Type | Description |
| -------- | -------- | 
| S | State enumeration |
| E | Event enumeration |
| T | Entity type |
| I | ID type  |

```
@Autowired
StateMachine<TaskState, TaskState, Task, String> stateMachine;
```

Signal a transition

```
stateMachine.onTransition(TaskEvent.START_WORKING, task.getId());
```




    
    
