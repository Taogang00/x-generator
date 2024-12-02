package com.github.xg.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class XGTabInfo {
    private String title;
    private String content;
    private Integer orderNo;
}
