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
package com.espertech.esper.common.internal.epl.agg.method.nth;

import com.espertech.esper.common.client.serde.DataInputOutputSerde;
import com.espertech.esper.common.client.serde.EventBeanCollatedWriter;
import com.espertech.esper.common.client.type.EPType;
import com.espertech.esper.common.client.type.EPTypeClass;
import com.espertech.esper.common.client.type.EPTypePremade;
import com.espertech.esper.common.internal.bytecodemodel.base.CodegenBlock;
import com.espertech.esper.common.internal.bytecodemodel.base.CodegenClassScope;
import com.espertech.esper.common.internal.bytecodemodel.base.CodegenMemberCol;
import com.espertech.esper.common.internal.bytecodemodel.base.CodegenMethod;
import com.espertech.esper.common.internal.bytecodemodel.core.CodegenCtor;
import com.espertech.esper.common.internal.bytecodemodel.model.expression.CodegenExpression;
import com.espertech.esper.common.internal.bytecodemodel.model.expression.CodegenExpressionField;
import com.espertech.esper.common.internal.bytecodemodel.model.expression.CodegenExpressionMember;
import com.espertech.esper.common.internal.bytecodemodel.model.expression.CodegenExpressionRef;
import com.espertech.esper.common.internal.epl.agg.method.core.AggregatorMethodWDistinctWFilterWValueBase;
import com.espertech.esper.common.internal.epl.expression.codegen.ExprForgeCodegenSymbol;
import com.espertech.esper.common.internal.epl.expression.core.ExprForge;
import com.espertech.esper.common.internal.epl.expression.core.ExprNode;
import com.espertech.esper.common.internal.serde.compiletime.resolve.DataInputOutputSerdeForge;
import com.espertech.esper.common.internal.serde.compiletime.sharable.CodegenSharableSerdeClassTyped;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.function.Consumer;

import static com.espertech.esper.common.internal.bytecodemodel.model.expression.CodegenExpressionBuilder.*;
import static com.espertech.esper.common.internal.bytecodemodel.model.expression.CodegenExpressionRelational.CodegenRelational.GT;
import static com.espertech.esper.common.internal.epl.agg.method.core.AggregatorCodegenUtil.rowDotMember;
import static com.espertech.esper.common.internal.serde.compiletime.sharable.CodegenSharableSerdeClassTyped.CodegenSharableSerdeName.VALUE_NULLABLE;

public class AggregatorNth extends AggregatorMethodWDistinctWFilterWValueBase {

    private final AggregationForgeFactoryNth factory;
    private CodegenExpressionMember circularBuffer;
    private CodegenExpressionMember currentBufferElementPointer;
    private CodegenExpressionMember numDataPoints;
    private CodegenExpressionField serdeValue;

    public AggregatorNth(AggregationForgeFactoryNth factory, EPTypeClass optionalDistinctValueType, DataInputOutputSerdeForge optionalDistinctSerde, boolean hasFilter, ExprNode optionalFilter) {
        super(optionalDistinctValueType, optionalDistinctSerde, hasFilter, optionalFilter);
        this.factory = factory;
    }

    public void initForgeFiltered(int col, CodegenCtor rowCtor, CodegenMemberCol membersColumnized, CodegenClassScope classScope) {
        this.circularBuffer = membersColumnized.addMember(col, EPTypePremade.OBJECTARRAY.getEPType(), "buf");
        this.currentBufferElementPointer = membersColumnized.addMember(col, EPTypePremade.INTEGERPRIMITIVE.getEPType(), "cbep");
        this.numDataPoints = membersColumnized.addMember(col, EPTypePremade.LONGPRIMITIVE.getEPType(), "cnt");
        this.serdeValue = classScope.addOrGetFieldSharable(new CodegenSharableSerdeClassTyped(VALUE_NULLABLE, factory.childType, factory.serde, classScope));
    }

    protected void applyEvalEnterNonNull(CodegenExpressionRef value, EPType valueType, CodegenMethod method, ExprForgeCodegenSymbol symbols, ExprForge[] forges, CodegenClassScope classScope) {
        applyEvalEnterNonNull(value, method);
    }

    protected void applyEvalLeaveNonNull(CodegenExpressionRef value, EPType valueType, CodegenMethod method, ExprForgeCodegenSymbol symbols, ExprForge[] forges, CodegenClassScope classScope) {
        applyEvalLeaveNonNull(method);
    }

    protected void applyTableEnterNonNull(CodegenExpressionRef value, EPType[] evaluationTypes, CodegenMethod method, CodegenClassScope classScope) {
        applyEvalEnterNonNull(value, method);
    }

    protected void applyTableLeaveNonNull(CodegenExpressionRef value, EPType[] evaluationTypes, CodegenMethod method, CodegenClassScope classScope) {
        applyEvalLeaveNonNull(method);
    }

    protected void clearWODistinct(CodegenMethod method, CodegenClassScope classScope) {
        method.getBlock().apply(clearCode());
    }

    protected void writeWODistinct(CodegenExpressionRef row, int col, CodegenExpressionRef output, CodegenExpressionRef unitKey, CodegenExpressionRef writer, CodegenMethod method, CodegenClassScope classScope) {
        method.getBlock().staticMethod(this.getClass(), "write", output, unitKey, writer, serdeValue, rowDotMember(row, circularBuffer), rowDotMember(row, numDataPoints), rowDotMember(row, currentBufferElementPointer), constant(factory.getSizeOfBuf()));
    }

    protected void readWODistinct(CodegenExpressionRef row, int col, CodegenExpressionRef input, CodegenExpressionRef unitKey, CodegenMethod method, CodegenClassScope classScope) {
        CodegenExpressionMember state = memberCol("state", col);
        method.getBlock()
                .declareVar(AggregationNthState.EPTYPE, state.getRef(), staticMethod(this.getClass(), "read", input, unitKey, serdeValue, constant(factory.getSizeOfBuf())))
                .assignRef(rowDotMember(row, circularBuffer), exprDotMethod(state, "getCircularBuffer"))
                .assignRef(rowDotMember(row, currentBufferElementPointer), exprDotMethod(state, "getCurrentBufferElementPointer"))
                .assignRef(rowDotMember(row, numDataPoints), exprDotMethod(state, "getNumDataPoints"));
    }

    public void getValueCodegen(CodegenMethod method, CodegenClassScope classScope) {
        CodegenExpression sizeBuf = constant(factory.getSizeOfBuf());
        method.getBlock().ifNullReturnNull(circularBuffer)
                .declareVar(EPTypePremade.INTEGERPRIMITIVE.getEPType(), "index", op(op(currentBufferElementPointer, "+", sizeBuf), "%", sizeBuf))
                .methodReturn(arrayAtIndex(circularBuffer, ref("index")));
    }

    /**
     * NOTE: Code-generation-invoked method, method name and parameter order matters
     *
     * @param input         input
     * @param unitKey       unit key
     * @param serdeNullable binding
     * @param sizeBuf       size
     * @return state
     * @throws IOException ioerror
     */
    public static AggregationNthState read(DataInput input, byte[] unitKey, DataInputOutputSerde serdeNullable, int sizeBuf) throws IOException {
        boolean filled = input.readBoolean();
        AggregationNthState state = new AggregationNthState();
        if (!filled) {
            return state;
        }
        Object[] circularBuffer = new Object[sizeBuf];
        state.circularBuffer = circularBuffer;
        state.numDataPoints = input.readLong();
        state.currentBufferElementPointer = input.readInt();
        for (int i = 0; i < sizeBuf; i++) {
            circularBuffer[i] = serdeNullable.read(input, unitKey);
        }
        return state;
    }

    /**
     * NOTE: Code-generation-invoked method, method name and parameter order matters
     *
     * @param output                      output
     * @param unitKey                     unit key
     * @param writer                      writer
     * @param serdeNullable               binding
     * @param circularBuffer              buffer
     * @param numDataPoints               points
     * @param currentBufferElementPointer pointer
     * @param sizeBuf                     size
     * @throws IOException io error
     */
    public static void write(DataOutput output, byte[] unitKey, EventBeanCollatedWriter writer, DataInputOutputSerde serdeNullable, Object[] circularBuffer, long numDataPoints, int currentBufferElementPointer, int sizeBuf) throws IOException {
        output.writeBoolean(circularBuffer != null);
        if (circularBuffer != null) {
            output.writeLong(numDataPoints);
            output.writeInt(currentBufferElementPointer);
            for (int i = 0; i < sizeBuf; i++) {
                serdeNullable.write(circularBuffer[i], output, unitKey, writer);
            }
        }
    }

    private Consumer<CodegenBlock> clearCode() {
        return block -> {
            block.assignRef(circularBuffer, newArrayByLength(EPTypePremade.OBJECT.getEPType(), constant(factory.getSizeOfBuf())))
                    .assignRef(numDataPoints, constant(0))
                    .assignRef(currentBufferElementPointer, constant(0));
        };
    }

    protected void applyEvalEnterNonNull(CodegenExpressionRef valueExpr, CodegenMethod method) {
        method.getBlock().increment(numDataPoints)
                .ifCondition(equalsNull(circularBuffer))
                .apply(clearCode())
                .blockEnd()
                .assignArrayElement(circularBuffer, currentBufferElementPointer, valueExpr)
                .assignRef(currentBufferElementPointer, op(op(currentBufferElementPointer, "+", constant(1)), "%", constant(factory.getSizeOfBuf())));
    }

    protected void applyEvalLeaveNonNull(CodegenMethod method) {
        method.getBlock().ifCondition(relational(constant(factory.getSizeOfBuf()), GT, numDataPoints))
                .declareVar(EPTypePremade.INTEGERPRIMITIVE.getEPType(), "diff", op(constant(factory.getSizeOfBuf()), "-", cast(EPTypePremade.INTEGERPRIMITIVE.getEPType(), numDataPoints)))
                .declareVar(EPTypePremade.INTEGERPRIMITIVE.getEPType(), "index", op(op(op(currentBufferElementPointer, "+", ref("diff")), "-", constant(1)), "%", constant(factory.getSizeOfBuf())))
                .assignArrayElement(circularBuffer, ref("index"), constantNull())
                .blockEnd()
                .decrement(numDataPoints);
    }

    public static class AggregationNthState {
        public final static EPTypeClass EPTYPE = new EPTypeClass(AggregationNthState.class);

        private Object[] circularBuffer;
        private int currentBufferElementPointer;
        private long numDataPoints;

        public Object[] getCircularBuffer() {
            return circularBuffer;
        }

        public int getCurrentBufferElementPointer() {
            return currentBufferElementPointer;
        }

        public long getNumDataPoints() {
            return numDataPoints;
        }
    }
}
