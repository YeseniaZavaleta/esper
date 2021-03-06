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
package com.espertech.esper.common.internal.view.access;

import com.espertech.esper.common.client.type.EPTypeClass;
import com.espertech.esper.common.internal.epl.expression.core.ExprEvaluatorContext;
import com.espertech.esper.common.internal.view.previous.PreviousGetterStrategy;

/**
 * Getter that provides an index at runtime.
 */
public class RandomAccessByIndexGetter implements RandomAccessByIndexObserver, PreviousGetterStrategy {
    public final static EPTypeClass EPTYPE = new EPTypeClass(RandomAccessByIndexGetter.class);

    private RandomAccessByIndex randomAccessByIndex;

    public RandomAccessByIndexGetter() {
    }

    /**
     * Returns the index for access.
     *
     * @return index
     */
    public RandomAccessByIndex getAccessor() {
        return randomAccessByIndex;
    }

    public void updated(RandomAccessByIndex randomAccessByIndex) {
        this.randomAccessByIndex = randomAccessByIndex;
    }

    public PreviousGetterStrategy getStrategy(ExprEvaluatorContext ctx) {
        return this;
    }
}
