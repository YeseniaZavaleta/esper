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
package com.espertech.esper.common.internal.filterspec;

import com.espertech.esper.common.internal.bytecodemodel.base.CodegenClassScope;
import com.espertech.esper.common.internal.bytecodemodel.base.CodegenMethod;
import com.espertech.esper.common.internal.bytecodemodel.base.CodegenMethodScope;
import com.espertech.esper.common.internal.bytecodemodel.model.expression.CodegenExpression;
import com.espertech.esper.common.internal.bytecodemodel.model.expression.CodegenExpressionNewAnonymousClass;
import com.espertech.esper.common.internal.bytecodemodel.model.expression.CodegenExpressionUtil;
import com.espertech.esper.common.internal.context.aifactory.core.SAIFFInitializeSymbolWEventType;
import com.espertech.esper.common.internal.epl.expression.core.ExprFilterSpecLookupable;
import com.espertech.esper.common.internal.epl.expression.core.ExprFilterSpecLookupableForge;
import com.espertech.esper.common.internal.util.ClassHelperPrint;

import java.util.Arrays;
import java.util.Collections;

import static com.espertech.esper.common.internal.bytecodemodel.model.expression.CodegenExpressionBuilder.*;

/**
 * This class represents a single, constant value filter parameter in an {@link FilterSpecActivatable} filter specification.
 */
public final class FilterSpecParamConstantForge extends FilterSpecParamForge {
    private final Object filterConstant;

    public FilterSpecParamConstantForge(ExprFilterSpecLookupableForge lookupable, FilterOperator filterOperator, Object filterConstant)
        throws IllegalArgumentException {
        super(lookupable, filterOperator);
        this.filterConstant = filterConstant;

        if (filterOperator.isRangeOperator()) {
            throw new IllegalArgumentException("Illegal filter operator " + filterOperator + " supplied to " +
                "constant filter parameter");
        }
    }

    public CodegenExpression makeCodegen(CodegenClassScope classScope, CodegenMethodScope parent, SAIFFInitializeSymbolWEventType symbols) {
        CodegenMethod method = parent.makeChild(FilterSpecParam.EPTYPE, FilterSpecParamConstantForge.class, classScope);
        method.getBlock()
            .declareVar(ExprFilterSpecLookupable.EPTYPE, "lookupable", localMethod(lookupable.makeCodegen(method, symbols, classScope)))
            .declareVar(ExprFilterSpecLookupable.EPTYPE_FILTEROPERATOR, "op", enumValue(FilterOperator.class, filterOperator.name()));

        CodegenExpressionNewAnonymousClass inner = newAnonymousClass(method.getBlock(), FilterSpecParam.EPTYPE, Arrays.asList(ref("lookupable"), ref("op")));
        CodegenMethod getFilterValue = CodegenMethod.makeParentNode(FilterValueSetParam.EPTYPE, this.getClass(), classScope).addParam(FilterSpecParam.GET_FILTER_VALUE_FP);
        inner.addMethod("getFilterValue", getFilterValue);
        getFilterValue.getBlock().methodReturn(FilterValueSetParamImpl.codegenNew(constant(filterConstant)));

        method.getBlock().methodReturn(inner);
        return localMethod(method);
    }

    /**
     * Returns the constant value.
     *
     * @return constant value
     */
    public Object getFilterConstant() {
        return filterConstant;
    }

    public final String toString() {
        return super.toString() + " filterConstant=" + filterConstant;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        FilterSpecParamConstantForge that = (FilterSpecParamConstantForge) o;

        if (filterConstant != null ? !filterConstant.equals(that.filterConstant) : that.filterConstant != null)
            return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (filterConstant != null ? filterConstant.hashCode() : 0);
        return result;
    }

    public void valueExprToString(StringBuilder out, int i) {
        valueExprToString(out, filterConstant);
    }

    public static void valueExprToString(StringBuilder out, Object constant) {
        out.append("constant ");
        CodegenExpressionUtil.renderConstant(out, constant, Collections.emptyMap(), false);
        out.append(" type ").append(ClassHelperPrint.getClassNameFullyQualPretty(constant == null ? null : constant.getClass()));
    }

    public static String valueExprToString(Object constant) {
        StringBuilder builder = new StringBuilder();
        valueExprToString(builder, constant);
        return builder.toString();
    }
}
