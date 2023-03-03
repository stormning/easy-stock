package com.slyak.es.controller;

import com.google.common.collect.Maps;
import com.slyak.es.hibernate.assembler.EntityAssemblers;
import com.slyak.es.util.ObjectUtils;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.Map;

public class BaseController {

    @Accessors(chain = true)
    @Data
    public static class Result {
        private boolean success = true;
        private String msg;
        private Object data;

        public Result setData(Object data) {
            if (data != null) {
                EntityAssemblers eas = EntityAssemblers.newInstance();
                eas.assemble(data);
            }
            this.data = data;
            return this;
        }

        public Map<String, Object> toMap() {
            Map<String, Object> ret = Maps.newHashMap();
            ret.put("success", success);
            ret.put("msg", msg);
            ret.put("data", data);
            return ret;
        }
    }

    public static Result ok() {
        return ok(null);
    }

    public static Result ok(Object data) {
        return new Result().setData(data);
    }

    public static Result fail(String msg) {
        return new Result().setMsg(msg).setSuccess(false);
    }

    public static Result fail(Exception ex) {
        return new Result().setMsg(ExceptionUtils.getMessage(ex)).setSuccess(false);
    }
}
