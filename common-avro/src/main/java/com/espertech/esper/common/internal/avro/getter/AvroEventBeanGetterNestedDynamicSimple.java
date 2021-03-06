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
package com.espertech.esper.common.internal.avro.getter;

import com.espertech.esper.common.client.EventBean;
import com.espertech.esper.common.client.PropertyAccessException;
import com.espertech.esper.common.client.type.EPTypePremade;
import com.espertech.esper.common.internal.avro.core.AvroConstant;
import com.espertech.esper.common.internal.bytecodemodel.base.CodegenClassScope;
import com.espertech.esper.common.internal.bytecodemodel.base.CodegenMethod;
import com.espertech.esper.common.internal.bytecodemodel.base.CodegenMethodScope;
import com.espertech.esper.common.internal.bytecodemodel.model.expression.CodegenExpression;
import com.espertech.esper.common.internal.event.core.EventPropertyGetterSPI;
import org.apache.avro.generic.GenericData;

import static com.espertech.esper.common.internal.bytecodemodel.model.expression.CodegenExpressionBuilder.*;

public class AvroEventBeanGetterNestedDynamicSimple implements EventPropertyGetterSPI {

    private final int posTop;
    private final String propertyName;

    public AvroEventBeanGetterNestedDynamicSimple(int posTop, String propertyName) {
        this.posTop = posTop;
        this.propertyName = propertyName;
    }

    public Object get(EventBean eventBean) throws PropertyAccessException {
        return get((GenericData.Record) eventBean.getUnderlying());
    }

    public boolean isExistsProperty(EventBean eventBean) {
        return isExistsProperty((GenericData.Record) eventBean.getUnderlying());
    }

    private Object get(GenericData.Record record) throws PropertyAccessException {
        GenericData.Record inner = (GenericData.Record) record.get(posTop);
        if (inner == null) {
            return null;
        }
        return inner.get(propertyName);
    }

    private CodegenMethod getCodegen(CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return codegenMethodScope.makeChild(EPTypePremade.OBJECT.getEPType(), this.getClass(), codegenClassScope).addParam(AvroConstant.EPTYPE_RECORD, "record").getBlock()
                .declareVar(AvroConstant.EPTYPE_RECORD, "inner", cast(AvroConstant.EPTYPE_RECORD, exprDotMethod(ref("record"), "get", constant(posTop))))
                .ifRefNullReturnNull("inner")
                .methodReturn(exprDotMethod(ref("inner"), "get", constant(propertyName)));
    }

    private boolean isExistsProperty(GenericData.Record record) {
        GenericData.Record inner = (GenericData.Record) record.get(posTop);
        if (inner == null) {
            return false;
        }
        return inner.getSchema().getField(propertyName) != null;
    }

    private CodegenMethod isExistsPropertyCodegen(CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return codegenMethodScope.makeChild(EPTypePremade.BOOLEANPRIMITIVE.getEPType(), this.getClass(), codegenClassScope).addParam(AvroConstant.EPTYPE_RECORD, "record").getBlock()
                .declareVar(AvroConstant.EPTYPE_RECORD, "inner", cast(AvroConstant.EPTYPE_RECORD, exprDotMethod(ref("record"), "get", constant(posTop))))
                .ifRefNullReturnFalse("inner")
                .methodReturn(notEqualsNull(exprDotMethodChain(ref("inner")).add("getSchema").add("getField", constant(propertyName))));
    }

    public Object getFragment(EventBean eventBean) throws PropertyAccessException {
        return null;
    }

    public CodegenExpression eventBeanGetCodegen(CodegenExpression beanExpression, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return underlyingGetCodegen(castUnderlying(AvroConstant.EPTYPE_RECORD, beanExpression), codegenMethodScope, codegenClassScope);
    }

    public CodegenExpression eventBeanExistsCodegen(CodegenExpression beanExpression, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return underlyingExistsCodegen(castUnderlying(AvroConstant.EPTYPE_RECORD, beanExpression), codegenMethodScope, codegenClassScope);
    }

    public CodegenExpression eventBeanFragmentCodegen(CodegenExpression beanExpression, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return constantNull();
    }

    public CodegenExpression underlyingGetCodegen(CodegenExpression underlyingExpression, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return localMethod(getCodegen(codegenMethodScope, codegenClassScope), underlyingExpression);
    }

    public CodegenExpression underlyingExistsCodegen(CodegenExpression underlyingExpression, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return localMethod(isExistsPropertyCodegen(codegenMethodScope, codegenClassScope), underlyingExpression);
    }

    public CodegenExpression underlyingFragmentCodegen(CodegenExpression underlyingExpression, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return constantNull();
    }
}
