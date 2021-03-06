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
package com.espertech.esper.common.internal.view.firstlength;

import com.espertech.esper.common.client.EventType;
import com.espertech.esper.common.client.type.EPTypeClass;
import com.espertech.esper.common.internal.context.module.EPStatementInitServices;
import com.espertech.esper.common.internal.epl.expression.core.ExprEvaluator;
import com.espertech.esper.common.internal.view.core.*;

/**
 * Factory for {@link FirstLengthWindowView}.
 */
public class FirstLengthWindowViewFactory implements DataWindowViewFactory {
    public final static EPTypeClass EPTYPE = new EPTypeClass(FirstLengthWindowViewFactory.class);

    protected ExprEvaluator size;
    protected EventType eventType;

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public void init(ViewFactoryContext viewFactoryContext, EPStatementInitServices services) {
    }

    public View makeView(AgentInstanceViewFactoryChainContext agentInstanceViewFactoryContext) {
        int size = ViewFactoryUtil.evaluateSizeParam(getViewName(), this.size, agentInstanceViewFactoryContext.getAgentInstanceContext());
        return new FirstLengthWindowView(agentInstanceViewFactoryContext, this, size);
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setSize(ExprEvaluator size) {
        this.size = size;
    }

    public String getViewName() {
        return ViewEnum.FIRST_LENGTH_WINDOW.getName();
    }
}
