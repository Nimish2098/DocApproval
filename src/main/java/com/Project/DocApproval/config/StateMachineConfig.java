package com.Project.DocApproval.config;

import com.Project.DocApproval.enums.RequestEvent;
import com.Project.DocApproval.enums.RequestStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigBuilder;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

@Configuration
@EnableStateMachine
public class StateMachineConfig extends EnumStateMachineConfigurerAdapter<RequestStatus, RequestEvent> {


    @Override
    public void configure(StateMachineConfigurationConfigurer<RequestStatus, RequestEvent> config)
            throws Exception {
        config
                .withConfiguration()
                .autoStartup(true)
                .listener(listener());
    }



    @Override
    public void configure(StateMachineStateConfigurer<RequestStatus, RequestEvent> states) throws Exception {
        states.withStates()
                .initial(RequestStatus.DRAFT)
                .state(RequestStatus.PENDING)
                .end(RequestStatus.ACCEPTED)
                .end(RequestStatus.REJECTED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<RequestStatus, RequestEvent> transitions) throws Exception {

        transitions
                .withExternal().source(RequestStatus.DRAFT).target(RequestStatus.PENDING).event(RequestEvent.SUBMIT)
                .and()
                .withExternal().source(RequestStatus.PENDING).target(RequestStatus.ACCEPTED).event(RequestEvent.APPROVE)
                .and()
                .withExternal().source(RequestStatus.PENDING).target(RequestStatus.REJECTED).event(RequestEvent.REJECT);

    }

    @Bean
    public StateMachineListener<RequestStatus, RequestEvent> listener() {
        return new StateMachineListenerAdapter<RequestStatus, RequestEvent>() {
            @Override
            public void stateChanged(State<RequestStatus, RequestEvent> from, State<RequestStatus, RequestEvent> to) {
                System.out.println("State change to " + to.getId());
            }
        };
    }
}
