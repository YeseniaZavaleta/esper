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
package com.espertech.esper.common.internal.epl.agg.method.sum;

import com.espertech.esper.common.client.type.EPType;
import com.espertech.esper.common.client.type.EPTypeClass;
import com.espertech.esper.common.client.type.EPTypePremade;
import com.espertech.esper.common.internal.bytecodemodel.base.CodegenClassScope;
import com.espertech.esper.common.internal.bytecodemodel.base.CodegenMemberCol;
import com.espertech.esper.common.internal.bytecodemodel.base.CodegenMethod;
import com.espertech.esper.common.internal.bytecodemodel.core.CodegenCtor;
import com.espertech.esper.common.internal.bytecodemodel.model.expression.CodegenExpression;
import com.espertech.esper.common.internal.bytecodemodel.model.expression.CodegenExpressionMember;
import com.espertech.esper.common.internal.bytecodemodel.model.expression.CodegenExpressionRef;
import com.espertech.esper.common.internal.epl.agg.method.core.AggregatorMethodWDistinctWFilterWValueBase;
import com.espertech.esper.common.internal.epl.expression.codegen.ExprForgeCodegenSymbol;
import com.espertech.esper.common.internal.epl.expression.core.ExprForge;
import com.espertech.esper.common.internal.epl.expression.core.ExprNode;
import com.espertech.esper.common.internal.serde.compiletime.resolve.DataInputOutputSerdeForge;
import com.espertech.esper.common.internal.util.JavaClassHelper;

import static com.espertech.esper.common.internal.bytecodemodel.model.expression.CodegenExpressionBuilder.*;
import static com.espertech.esper.common.internal.bytecodemodel.model.expression.CodegenExpressionRelational.CodegenRelational.LE;
import static com.espertech.esper.common.internal.epl.agg.method.core.AggregatorCodegenUtil.readLong;
import static com.espertech.esper.common.internal.epl.agg.method.core.AggregatorCodegenUtil.writeLong;

public abstract class AggregatorSumBase extends AggregatorMethodWDistinctWFilterWValueBase {

    protected CodegenExpressionMember cnt;
    protected CodegenExpressionMember sum;
    protected EPTypeClass sumType;

    protected abstract CodegenExpression initOfSum();

    protected abstract void applyAggEnterSum(CodegenExpressionRef value, EPType valueType, CodegenMethod method);

    protected abstract void applyTableEnterSum(CodegenExpressionRef value, EPType[] evaluationTypes, CodegenMethod method, CodegenClassScope classScope);

    protected abstract void applyAggLeaveSum(CodegenExpressionRef value, EPType valueType, CodegenMethod method);

    protected abstract void applyTableLeaveSum(CodegenExpressionRef value, EPType[] evaluationTypes, CodegenMethod method, CodegenClassScope classScope);

    protected abstract void writeSum(CodegenExpressionRef row, CodegenExpressionRef output, CodegenMethod method, CodegenClassScope classScope);

    protected abstract void readSum(CodegenExpressionRef row, CodegenExpressionRef input, CodegenMethod method, CodegenClassScope classScope);

    public AggregatorSumBase(EPTypeClass optionalDistinctValueType, DataInputOutputSerdeForge optionalDistinctSerde, boolean hasFilter, ExprNode optionalFilter, EPTypeClass sumType) {
        super(optionalDistinctValueType, optionalDistinctSerde, hasFilter, optionalFilter);
        this.sumType = sumType;
    }

    public void initForgeFiltered(int col, CodegenCtor rowCtor, CodegenMemberCol membersColumnized, CodegenClassScope classScope) {
        this.cnt = membersColumnized.addMember(col, EPTypePremade.LONGPRIMITIVE.getEPType(), "cnt");
        this.sum = membersColumnized.addMember(col, JavaClassHelper.getPrimitiveType(sumType), "sum");
        rowCtor.getBlock().assignRef(sum, initOfSum());
    }

    protected final void applyEvalEnterNonNull(CodegenExpressionRef value, EPType valueType, CodegenMethod method, ExprForgeCodegenSymbol symbols, ExprForge[] forges, CodegenClassScope classScope) {
        method.getBlock().increment(cnt);
        applyAggEnterSum(value, valueType, method);
    }

    protected final void applyTableEnterNonNull(CodegenExpressionRef value, EPType[] evaluationTypes, CodegenMethod method, CodegenClassScope classScope) {
        method.getBlock().increment(cnt);
        applyTableEnterSum(value, evaluationTypes, method, classScope);
    }

    protected final void applyEvalLeaveNonNull(CodegenExpressionRef value, EPType valueType, CodegenMethod method, ExprForgeCodegenSymbol symbols, ExprForge[] forges, CodegenClassScope classScope) {
        method.getBlock()
                .ifCondition(relational(cnt, LE, constant(1)))
                .assignRef(cnt, constant(0))
                .assignRef(sum, initOfSum())
                .blockReturnNoValue()
                .decrement(cnt);
        applyAggLeaveSum(value, valueType, method);
    }

    protected final void applyTableLeaveNonNull(CodegenExpressionRef value, EPType[] evaluationTypes, CodegenMethod method, CodegenClassScope classScope) {
        method.getBlock()
                .ifCondition(relational(cnt, LE, constant(1)))
                .assignRef(cnt, constant(0))
                .assignRef(sum, initOfSum())
                .blockReturnNoValue()
                .decrement(cnt);
        applyTableLeaveSum(value, evaluationTypes, method, classScope);
    }

    protected final void clearWODistinct(CodegenMethod method, CodegenClassScope classScope) {
        method.getBlock()
                .assignRef(cnt, constant(0))
                .assignRef(sum, initOfSum());
    }

    public void getValueCodegen(CodegenMethod method, CodegenClassScope classScope) {
        method.getBlock().ifCondition(equalsIdentity(cnt, constant(0)))
                .blockReturn(constantNull())
                .methodReturn(sum);
    }

    protected final void writeWODistinct(CodegenExpressionRef row, int col, CodegenExpressionRef output, CodegenExpressionRef unitKey, CodegenExpressionRef writer, CodegenMethod method, CodegenClassScope classScope) {
        method.getBlock().apply(writeLong(output, row, cnt));
        writeSum(row, output, method, classScope);
    }

    protected final void readWODistinct(CodegenExpressionRef row, int col, CodegenExpressionRef input, CodegenExpressionRef unitKey, CodegenMethod method, CodegenClassScope classScope) {
        method.getBlock().apply(readLong(row, cnt, input));
        readSum(row, input, method, classScope);
    }
}
