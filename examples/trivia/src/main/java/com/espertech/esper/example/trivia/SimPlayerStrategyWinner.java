/*
 ***************************************************************************************
 *  Copyright (C) 2006 EsperTech, Inc. All rights reserved.                            *
 *  http://www.espertech.com/esper                                                     *
 *  http://www.espertech.com                                                           *
 *  ---------------------------------------------------------------------------------- *
 *  The software in this package is published under the terms of the GPL license       *
 *  a copy of which has been included with this distribution in the license.txt file.  *
 ***************************************************************************************
 */
package com.espertech.esper.example.trivia;

import com.espertech.esper.runtime.client.EPEventService;

import java.util.Map;

public class SimPlayerStrategyWinner implements SimPlayerStrategy {

    private final String playerId;
    private String currentQuestionId;

    public SimPlayerStrategyWinner(String playerId) {
        this.playerId = playerId;
    }

    public void newQuestion(Map<String, Object> currentQuestion) {
        currentQuestionId = (String) currentQuestion.get(EventFactory.QID);
    }

    public void update(long currentTime, Map<String, Object> currentQuestion, int sec, EPEventService runtime) {
        if (sec != 1) {
            return;
        }

        Map<String, Object> answer = EventFactory.makePlayerAnswer(playerId, currentQuestionId, "A", currentTime);
        runtime.sendEventMap(answer, "PlayerAnswer");
    }
}
