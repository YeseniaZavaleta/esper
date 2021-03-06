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
package com.espertech.esper.common.internal.serde.serdeset.builtin;

import com.espertech.esper.common.client.serde.DataInputOutputSerde;
import com.espertech.esper.common.client.serde.EventBeanCollatedWriter;
import com.espertech.esper.common.client.type.EPTypeClass;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Date;

public class DIODateSerde implements DataInputOutputSerde<Date> {
    public final static EPTypeClass EPTYPE = new EPTypeClass(DIODateSerde.class);

    public final static DIODateSerde INSTANCE = new DIODateSerde();

    private DIODateSerde() {
    }

    public void write(Date object, DataOutput output) throws IOException {
        writeInternal(object, output);
    }

    public Date read(DataInput input) throws IOException {
        return readInternal(input);
    }

    public void write(Date object, DataOutput output, byte[] unitKey, EventBeanCollatedWriter writer) throws IOException {
        writeInternal(object, output);
    }

    public Date read(DataInput input, byte[] unitKey) throws IOException {
        return readInternal(input);
    }

    protected static void writeInternal(Date object, DataOutput output) throws IOException {
        if (object == null) {
            output.writeLong(-1);
            return;
        }
        output.writeLong(object.getTime());
    }

    protected static Date readInternal(DataInput input) throws IOException {
        long value = input.readLong();
        if (value == -1) {
            return null;
        }
        Date date = new Date();
        date.setTime(value);
        return date;
    }
}
