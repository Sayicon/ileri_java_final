package com.tbl324.shared.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProblemDetail {

    private String type;
    private String title;
    private int status;
    private String detail;
    private String instance;
    private Map<String, List<String>> errors;
}
