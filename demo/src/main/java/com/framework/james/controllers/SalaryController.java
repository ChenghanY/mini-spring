package com.framework.james.controllers;

import com.framework.james.beans.Autowired;
import com.framework.james.service.SalaryService;
import com.framework.james.web.mvc.Controller;
import com.framework.james.web.mvc.RequestMapping;
import com.framework.james.web.mvc.RequestParam;

/**
 *
 */
@Controller
public class SalaryController {
    @Autowired
    private SalaryService salaryService;

    @RequestMapping("/salary")
    public String getSalary(@RequestParam("name") String name, @RequestParam("age") String age) {
        if(name.isEmpty() || age.isEmpty()){
            return "params error. please format like this : /salary?name=james&age=1";
        }
        return salaryService.calSalary(Integer.parseInt(age));
    }
}
