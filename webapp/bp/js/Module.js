"use strict";

/**
 * 模块
 */
class Module {
  /**
   * 获取模块规则
   * @param source 调用源
   * @param moduleName 模块名称
   * @param loadStartCallback 回调函数
   * @param errorCallback 回调函数
   * @param timeoutCallback 回调函数
   * @param readyStateChangeCallback 回调函数
   * @return 没有异常返回true，否则返回false。
   */
  static getModuleRule(source, moduleName, loadStartCallback, errorCallback, timeoutCallback, readyStateChangeCallback) {
    try {
      Network.request(Network.RequestType.GET, Network.ResponseType.JSON, [{"Content-Type": "application/x-www-form-urlencoded"}],
        Configure.getServerUrl() + `/rule/${moduleName}`, null, source,
        function loadStart(xhr, xhrEvent, source) {
          loadStartCallback(xhr, xhrEvent, source);
        },
        function error(xhr, xhrEvent, source) {
          errorCallback(xhr, xhrEvent, source);
        },
        function timeout(xhr, xhrEvent, source) {
          timeoutCallback(xhr, xhrEvent, source);
        },
        function readyStateChange(xhr, xhrEvent, source) {
          readyStateChangeCallback(xhr, xhrEvent, source);
        }
      );
      return true;
    } catch (e) {
      return false;
    }
  }

  /**
   * 获取模块规则的Promise
   * @param source 调用源
   * @param moduleName 模块名称
   * @param ruleArray 接口数组
   * @return 模块规则的Promise
   */
  static getModuleRulePromise(source, moduleName, ruleArray) {
    return new Promise(function(resolve, reject) {
      const ruleResult = Module.getModuleRule(source, moduleName,
        function loadStart(xhr, xhrEvent, source) {},
        function error(xhr, xhrEvent, source) {
          Error.redirect("../home/error.html", "获取规则", "网络请求失败", window.location.href);
        },
        function timeout(xhr, xhrEvent, source) {
          Error.redirect("../home/error.html", "获取规则", "网络请求超时", window.location.href);
        },
        function readyStateChange(xhr, xhrEvent, source) {
          if ((XMLHttpRequest.DONE == xhr.readyState) && (200 == xhr.status)) {
            const responseResult = xhr.response;
            if (Toolkit.stringEqualsIgnoreCase("SUCCESS", responseResult.status)) {
              const rule = responseResult.content;
              ruleArray.push(rule);
              resolve();
            } else {
              Error.redirect("../home/error.html", "获取规则", responseResult.attach, window.location.href);
            }
          }
        }
      );
      if (!ruleResult) {
        Error.redirect("../home/error.html", "获取规则", "模块接口规则异常", window.location.href);
      }
    });
  }

  /**
   * 获取方法参数的规则对象
   * @param ruleObj 规则对象
   * @param methodName 方法名称
   * @param parameterName 参数名称
   * @return 成功返回方法参数的规则对象，否则返回null。
   */
  static getMethodParameterRuleObj(ruleObj, methodName, parameterName) {
    for (let i = 0; i < ruleObj.methods.length; i++) {
      const method = ruleObj.methods[i];
      if (Toolkit.stringEqualsIgnoreCase(methodName, method.name)) {
        for (let j = 0; j < method.parameters.length; j++) {
          let parameter = method.parameters[j];
          if (Toolkit.stringEqualsIgnoreCase(parameterName, parameter.name)) {
            return parameter;
          }
        }
        break;
      }
    }
    return null;
  }

  /**
   * 检查参数
   * @param ruleObj 规则对象
   * @param methodName 方法名称
   * @param parameterObj 参数对象
   * @param source 调用源
   * @param errorCallback 错误回调函数
   * @return 没有错误返回true，否则返回false并且触发错误回调函数。
   */
  static checkParameter(ruleObj, methodName, parameterObj, source, errorCallback) {
    if (!Toolkit.isJSONObjectExistKey(parameterObj, "name")) {
      errorCallback(source, "参数名称不存在");
      return false;
    }
    if (!parameterObj.allow_null) {
      if ((!Toolkit.isJSONObjectExistKey(parameterObj, "value")) || (null == parameterObj.value)) {
        if (null != parameterObj.custom_error_message) {
          errorCallback(source, parameterObj.custom_error_message);
        } else {
          errorCallback(source, `${parameterObj.name}参数值不存在`);
        }
        return false;
      }
    }
    const parameterRule = Module.getMethodParameterRuleObj(ruleObj, methodName, parameterObj.name);
    if (null == parameterRule) {
      errorCallback(source, `接口中没有找到检查参数: ${parameterObj.name}`);
      return false;
    }
    if ((!parameterObj.allow_null) && (0 >= parameterObj.value.length)) {
      errorCallback(source, `${parameterRule.text}不能为空`);
      return false;
    }
    if ((!parameterRule.allow_null) && (0 >= parameterObj.value.length)) {
      errorCallback(source, `${parameterRule.text}不能为空`);
      return false;
    }
    if ((null != parameterObj.value) && (0 < parameterObj.value.length)) {
      if (null == parameterObj.value.match(parameterRule.format)) {
        if (null != parameterObj.custom_error_message) {
          errorCallback(source, parameterObj.custom_error_message);
        } else {
          errorCallback(source, `${parameterRule.text}必须是${parameterRule.format_prompt}`);
        }
        return false;
      }
    }
    return true;
  }
}
