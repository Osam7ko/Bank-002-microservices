package com.osama.bank002.account.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailDetails {

    private String recipient;
    private String messageBody;
    private String subject;
    private String attachment;
}