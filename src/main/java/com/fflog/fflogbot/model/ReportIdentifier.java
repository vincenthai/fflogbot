package com.fflog.fflogbot.model;

import com.fflog.fflogbot.util.HandlerUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ReportIdentifier extends HandlerUtil.Variables {
    String code;
    int id;
    float startTime;
    float endTime;
}
