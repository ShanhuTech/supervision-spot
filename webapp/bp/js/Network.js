"use strict";

/**
 * 网络
 */
class Network {
  /*
   * 请求类型枚举
   */
  static RequestType = {
    "GET": "GET",
    "POST": "POST"
  };

  /*
   * 响应类型枚举
   */
  static ResponseType = {
    "TEXT": "text",
    "ARRAYBUFFER": "arraybuffer",
    "BLOB": "blob",
    "DOCUMENT": "document",
    "JSON": "json"
  };

  /**
   * 请求
   *
   * 注意：
   * [1]添加监听事件。除此之外事件还包括：abort、load、loadend、progress，这里暂时不用。
   * [2]只要readyState发生变化，就会触发此事件。需要判断xhr两个属性：xhr.readyState和xhr.status。
   * xhr.readyState在XMLHttpRequest中有5个枚举值（如XMLHttpRequest.DONE）分别是：
   * [值] [状态]             [描述]
   * 0    UNSENT             代理被创建，但尚未调用open()方法。
   * 1    OPENED	           open()方法已经被调用。
   * 2    HEADERS_RECEIVED   send()方法已经被调用并且头部和状态已经可获得。
   * 3    LOADING            下载中；responseText属性已经包含部分数据。
   * 4    DONE               下载操作已完成。
   * xhr.status对应4个无符号短整型，请求完成前status的值为0。如果XMLHttpRequest出错，浏览器返回的status也为0。
   * status码是标准的HTTP status codes。举个例子，200代表一个成功的请求。所以这里以200为执行成功的判断标准即可。即：
   * if ((XMLHttpRequest.DONE == xhr.readyState) && (200 == xhr.status)) { }
   * [3]GET请求时XMLHttpRequest会忽略参数数组parameterArray，只能通过Url传递参数，只有POST请求可以设置参数数组。
   *
   * @param requestType Network.RequestType枚举的请求类型
   * @param responseType Network.ResponseType枚举的响应类型
   * @param requestHeaderArray 请求Header数组。格式：[{"${name}": "${value}"}]。
   * @param url 请求地址
   * @param parameterArray 参数数组。格式：[{"${name}": "${value}"}]。
   * @param source 调用源对象
   * @param ... 不定长参数（根据传入参数数量不同，通过js的arguments判断具体属于哪种调用，多态的变相实现），包括：
   *            [1]长度1：triggerCallbackFuncPrefix。
   *            [2]长度4：loadStartCallback、errorCallback、timeoutCallback、readyStateChangeCallback。
   */
  static request(requestType, responseType, requestHeaderArray, url, parameterArray, source) {
    ////////////////////////////////////////////////////////////////////////////
    // 检查请求类型。
    ////////////////////////////////////////////////////////////////////////////
    if (!((Network.RequestType.GET == requestType) || (Network.RequestType.POST == requestType))) {
      throw new Error("Invalid Request Type");
    }
    ////////////////////////////////////////////////////////////////////////////
    // 检查响应类型。
    ////////////////////////////////////////////////////////////////////////////
    if (!((Network.ResponseType.TEXT == responseType) || (Network.ResponseType.ARRAYBUFFER == responseType)
        || (Network.ResponseType.BLOB == responseType) || (Network.ResponseType.DOCUMENT == responseType)
        || (Network.ResponseType.JSON == responseType))) {
      throw new Error("Invalid Response Type");
    }
    ////////////////////////////////////////////////////////////////////////////
    // 创建XMLHttpRequest对象。
    ////////////////////////////////////////////////////////////////////////////
    const xhr = new XMLHttpRequest();
    ////////////////////////////////////////////////////////////////////////////
    // 设置请求超时时间。
    ////////////////////////////////////////////////////////////////////////////
    xhr.timeout = 1000 * 10;
    ////////////////////////////////////////////////////////////////////////////
    // 设置响应类型。
    ////////////////////////////////////////////////////////////////////////////
    xhr.responseType = responseType;
    ////////////////////////////////////////////////////////////////////////////
    // 请求地址。
    ////////////////////////////////////////////////////////////////////////////
    xhr.open(requestType, url, true/* 异步 */);
    ////////////////////////////////////////////////////////////////////////////
    // 设置请求头（必须在open方法之后send方法之前调用）。
    ////////////////////////////////////////////////////////////////////////////
    if ((null != requestHeaderArray) && (Array.isArray(requestHeaderArray))) {
      for (let i = 0; i < requestHeaderArray.length; i++) {
        const requestHeader = requestHeaderArray[i];
        Toolkit.eachJSONObjectKV(requestHeader, function(key, value) {
          xhr.setRequestHeader(key, value);
        });
      }
    }
    ////////////////////////////////////////////////////////////////////////////
    // 当前已存在参数数量（即：requestType、responseType、requestHeaderArray、ur
    // l、parameterArray、source。
    ////////////////////////////////////////////////////////////////////////////
    const currentExistParameterCount = 6;
    if ((currentExistParameterCount + 1) == arguments.length) { // 长度1
      const triggerCallbackFuncPrefix = arguments[6];
      xhr.addEventListener("loadstart", function loadstart(xhrEvent) {
        $(document).trigger(`${triggerCallbackFuncPrefix}LoadStartEvent`, [xhr, xhrEvent, source]);
      }, false);
      xhr.addEventListener("error", function error(xhrEvent) {
        $(document).trigger(`${triggerCallbackFuncPrefix}ErrorEvent`, [xhr, xhrEvent, source]);
      }, false);
      xhr.addEventListener("timeout", function timeout(xhrEvent) {
        $(document).trigger(`${triggerCallbackFuncPrefix}TimeoutEvent`, [xhr, xhrEvent, source]);
      }, false);
      xhr.addEventListener("readystatechange", function readystatechange(xhrEvent) {
        $(document).trigger(`${triggerCallbackFuncPrefix}ReadyStateChangeEvent`, [xhr, xhrEvent, source]);
      }, false);
    } else if ((currentExistParameterCount + 4) == arguments.length) { // 长度4
      const loadStartCallback = arguments[6];
      const errorCallback = arguments[7];
      const timeoutCallback = arguments[8];
      const readyStateChangeCallback = arguments[9];
      xhr.addEventListener("loadstart", function loadStart(xhrEvent) {
        if (null != loadStartCallback) {
          loadStartCallback(xhr, xhrEvent, source);
        }
      }, false);
      xhr.addEventListener("error", function error(xhrEvent) {
        if (null != errorCallback) {
          errorCallback(xhr, xhrEvent, source);
        }
      }, false);
      xhr.addEventListener("timeout", function timeout(xhrEvent) {
        if (null != timeoutCallback) {
          timeoutCallback(xhr, xhrEvent, source);
        }
      }, false);
      xhr.addEventListener("readystatechange", function readyStateChange(xhrEvent) {
        if (null != readyStateChangeCallback) {
          readyStateChangeCallback(xhr, xhrEvent, source);
        }
      }, false);
    } else {
      throw new Error("Invalid Parameter Count");
    }
    ////////////////////////////////////////////////////////////////////////////
    // 发送。
    ////////////////////////////////////////////////////////////////////////////
    if ((null != parameterArray) && (0 < parameterArray.length)) {
      let parameterStr = "";
      for (let i = 0; i < parameterArray.length; i++) {
        const parameter = parameterArray[i];
        Toolkit.eachJSONObjectKV(parameter, function(key, value) {
          if (null != value) { // 这里不能判断长度，当类型为数字时，长度无效。
            parameterStr += `${key}=${value}&`;
          }
        });
      }
      parameterStr = parameterStr.substring(0, parameterStr.length - 1);
      xhr.send(parameterStr);
    } else {
      xhr.send();
    }
  }
}
