# awesome-state-machine

**awesome-state-machine** is a Spring-Boot library which aims to provide an easy-to-use state machine for Spring-Boot with persistence of the state. 

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

Declare an Enumeration with every states

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

Add a field annotated with `@State` on the entity field that drives the state

```
@Data @NoArgsConstructor
@Document
public class Task {
    private @Id String id;
    private @State TaskState state;
    private String taskContent;
}
```


### Configuration

Configure your state machine by creating a `@Configuration` class that extends `StateMachineConfiguration<S, E, T, I>`

| Type | Description |
| -------- | -------- | 
| S | State enumeration |
| E | Event enumeration |
| T | Entity type |
| I | ID type  |


**Parameters:**

| Name | Type | Mandatory | Description |
| -------- | -------- | -------- | -------- | 
| repository | CRUDRepository | no | CRUDRepository used to retrieve before and persist the entity after the state transition |
| transitions | Transition[] | yes |  List of transitions |


**Configure a transitions:**

Call `add(transition().field1().field2().fieldn().build());`

| Name | Type | Mandatory | Description |
| -------- | -------- | -------- | -------- | 
| event | Event | yes [1] | Event triggered |
| from | State  | yes [1] | Initial state |
| to | State  | yes [1, n] | Target states (if multiple target states, a condition is required to take the right branch) |
| before | Consumer<Entity, Context> | no [0, 1] | Execute the consumer before the state transition  |
| after | Consumer<Entity, Context> | no [0, 1] | Execute the consumer after the state transition |


```
@Configuration
static class MyStateMachineConfiguration extends StateMachineConfiguration<TaskState, TaskEvent, Task, String> {

    @Autowired
    public ConfigurationNoRepository(TaskRepository repository) {
        super(repository);
        
        add(transition()
                .event(TaskEvent.START_WORKING)
                .from(TaskState.OPENED)
                .to(TaskState.IN_PROGRESS)
                .before((e, c) -> log.info("running before with entity {} and context {}", e, c))
                .after((e, c) -> log.info("running after with entity {} and context {}", e, c))
                .build());
        
        add(transition()
                .event(TaskEvent.END_TASK)
                .from(TaskState.IN_PROGRESS)
                .to(TaskState.CLOSED, (e, c) -> !e.getValue().equals("cancel"))
                .to(TaskState.CANCELED, (e, c) -> e.getValue().equals("cancel"))
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

**Service Bean**

```
@Autowired
StateMachine<TaskState, TaskState, Task, String> stateMachine;
```

**Signal a transition**

```
stateMachine.onTransition(TaskEvent.START_WORKING, task.getId());
```

Pass a context object (Object) that can be used in `before` and `after` consumers. (use case: context could be `Principal` to check permission to change the state)
```
stateMachine.onTransition(TaskEvent.START_WORKING, task.getId(), contextObject);
```




    
    
