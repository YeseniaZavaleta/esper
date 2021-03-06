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
package com.espertech.esper.common.internal.epl.resultset.rowpergrouprollup;

import com.espertech.esper.common.client.EventBean;
import com.espertech.esper.common.client.type.EPTypeClass;

import java.util.Map;

public interface ResultSetProcessorRowPerGroupRollupUnboundHelper {
    EPTypeClass EPTYPE = new EPTypeClass(ResultSetProcessorRowPerGroupRollupUnboundHelper.class);

    Map<Object, EventBean>[] getBuffer();

    void destroy();
}
