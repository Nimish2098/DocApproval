package com.Project.DocApproval.service;

import com.Project.DocApproval.enums.DecisionType;
import com.Project.DocApproval.model.Decision;
import com.Project.DocApproval.model.Request;
import com.Project.DocApproval.model.User;
import com.Project.DocApproval.repository.DecisionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DecisionService {

    private final DecisionRepository decisionRepository;

    public Decision addDecision(Decision decision){

        return decisionRepository.save(decision);
    }

    public void deleteDecision(Long id){
        decisionRepository.deleteById(id);
    }

    public Decision updateDecision(Long id,Decision decision){
        Decision existingDecision = decisionRepository.getDecisionById(id);

        existingDecision.setDecision(decision.getDecision());
        existingDecision.setReviewer(decision.getReviewer());
        existingDecision.setRequest(decision.getRequest());
        existingDecision.setComment(decision.getComment());
        existingDecision.setDecidedAt(decision.getDecidedAt());

        return decisionRepository.save(existingDecision);
    }

    public Decision patchDecision(Long id, Map<String , Object>map){

        Decision existingDecision = decisionRepository.getDecisionById(id);

        map.forEach((key,value)->{
            switch (key){
                case "request" : existingDecision.setRequest((Request) value); break;
                case "reviewer": existingDecision.setReviewer((User)value); break;
                case "decision": existingDecision.setDecision((DecisionType) value); break;
                case "comment" : existingDecision.setComment((String) value); break;
                case "decidedAt":existingDecision.setDecidedAt((LocalDateTime) value);


            }
        });
    return decisionRepository.save(existingDecision);
    }
}
