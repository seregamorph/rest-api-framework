package com.seregamorph.restapi.test;

import javax.annotation.Nullable;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.junit.runner.Description;

@Data
@RequiredArgsConstructor
public class TestDescription {

    private final Description description;

    private String methodGroup;
    private String targetMethodGroup;
    @Nullable
    private String executionId;

    @Override
    public String toString() {
        String classNameDesc = "className: " + description.getClassName();
        String methodNameDesc = "methodName: " + description.getMethodName();
        String methodGroupDesc = "methodGroup: " + (StringUtils.isBlank(methodGroup) ? "N/A" : methodGroup);
        String targetMethodGroupDesc =
                "targetMethodGroup: " + (StringUtils.isBlank(targetMethodGroup) ? "N/A" : targetMethodGroup);
        String executionIdDesc = "executionId: " + (StringUtils.isBlank(executionId) ? "N/A" : executionId);
        return String
                .join(", ", classNameDesc, methodNameDesc, methodGroupDesc, targetMethodGroupDesc, executionIdDesc);
    }

}
