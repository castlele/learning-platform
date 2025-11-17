package com.castlelecs.platform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ModuleCommand {

    private String title;
    private String description;
    private Integer orderIndex;
}
