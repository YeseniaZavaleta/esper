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
package com.espertech.esper.common.internal.context.module;

import com.espertech.esper.common.client.type.EPTypeClass;
import com.espertech.esper.common.internal.epl.script.core.NameParamNumAndModule;
import com.espertech.esper.common.internal.type.NameAndModule;
import com.espertech.esper.common.internal.util.CollectionUtil;

public class ModuleDependenciesRuntime {
    public final static EPTypeClass EPTYPE = new EPTypeClass(ModuleDependenciesRuntime.class);

    private NameAndModule[] pathEventTypes = NameAndModule.EMPTY_ARRAY;
    private NameAndModule[] pathNamedWindows = NameAndModule.EMPTY_ARRAY;
    private NameAndModule[] pathTables = NameAndModule.EMPTY_ARRAY;
    private NameAndModule[] pathVariables = NameAndModule.EMPTY_ARRAY;
    private NameAndModule[] pathContexts = NameAndModule.EMPTY_ARRAY;
    private NameAndModule[] pathExpressions = NameAndModule.EMPTY_ARRAY;
    private NameAndModule[] pathClasses = NameAndModule.EMPTY_ARRAY;
    private ModuleIndexMeta[] pathIndexes = ModuleIndexMeta.EMPTY_ARRAY;
    private NameParamNumAndModule[] pathScripts = NameParamNumAndModule.EMPTY_ARRAY;
    private String[] publicEventTypes = CollectionUtil.STRINGARRAY_EMPTY;
    private String[] publicVariables = CollectionUtil.STRINGARRAY_EMPTY;

    public NameAndModule[] getPathEventTypes() {
        return pathEventTypes;
    }

    public void setPathEventTypes(NameAndModule[] pathEventTypes) {
        this.pathEventTypes = pathEventTypes;
    }

    public NameAndModule[] getPathNamedWindows() {
        return pathNamedWindows;
    }

    public void setPathNamedWindows(NameAndModule[] pathNamedWindows) {
        this.pathNamedWindows = pathNamedWindows;
    }

    public NameAndModule[] getPathTables() {
        return pathTables;
    }

    public void setPathTables(NameAndModule[] pathTables) {
        this.pathTables = pathTables;
    }

    public NameAndModule[] getPathVariables() {
        return pathVariables;
    }

    public void setPathVariables(NameAndModule[] pathVariables) {
        this.pathVariables = pathVariables;
    }

    public NameAndModule[] getPathContexts() {
        return pathContexts;
    }

    public void setPathContexts(NameAndModule[] pathContexts) {
        this.pathContexts = pathContexts;
    }

    public NameAndModule[] getPathExpressions() {
        return pathExpressions;
    }

    public void setPathExpressions(NameAndModule[] pathExpressions) {
        this.pathExpressions = pathExpressions;
    }

    public String[] getPublicEventTypes() {
        return publicEventTypes;
    }

    public void setPublicEventTypes(String[] publicEventTypes) {
        this.publicEventTypes = publicEventTypes;
    }

    public String[] getPublicVariables() {
        return publicVariables;
    }

    public void setPublicVariables(String[] publicVariables) {
        this.publicVariables = publicVariables;
    }

    public ModuleIndexMeta[] getPathIndexes() {
        return pathIndexes;
    }

    public void setPathIndexes(ModuleIndexMeta[] pathIndexes) {
        this.pathIndexes = pathIndexes;
    }

    public NameParamNumAndModule[] getPathScripts() {
        return pathScripts;
    }

    public void setPathScripts(NameParamNumAndModule[] pathScripts) {
        this.pathScripts = pathScripts;
    }

    public NameAndModule[] getPathClasses() {
        return pathClasses;
    }

    public void setPathClasses(NameAndModule[] pathClasses) {
        this.pathClasses = pathClasses;
    }
}
