package com.nc.model;

import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class AjaxResponseBody {
    private String msg;
    private Set<Hardware> result;
    private List<Double> currency;
}
