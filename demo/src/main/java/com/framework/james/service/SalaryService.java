package com.framework.james.service;

import com.framework.james.beans.Bean;

@Bean
public class SalaryService {
    public String calSalary(Integer age) {
        return String.valueOf(age * 5000);
    }
}
