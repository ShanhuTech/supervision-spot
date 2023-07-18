"use strict";

/**
 * 工具箱
 * 项目积累使用类库。
 */
class Toolkit {
  //############################################################################
  // 字符串
  //############################################################################

  /**
   * 生成uuid
   * @return uuid
   */
  static generateUuid() {
    const hexDigits = "0123456789abcdef";
    let s = [];
    for (let i = 0; i < 32; i++) {
      s[i] = hexDigits.substr(Math.floor(Math.random() * 0x10), 1);
    }
    s[14] = "4";
    s[19] = hexDigits.substr((s[19] & 0x3) | 0x8, 1);
    ////////////////////////////////////////////////////////////////////////////
    // html的id如果不以字母开头有时会报错。
    ////////////////////////////////////////////////////////////////////////////
    s[0] = "u";
    return s.join("");
  }

  /**
   * 忽略大小写比较字符串
   * @param str1 字符串1
   * @param str2 字符串2
   * @return 一致返回true，否则返回false。
   */
  static stringEqualsIgnoreCase(str1, str2) {
    if (typeof(str1) == typeof(str2)) {
      if (str1.toLowerCase() == str2.toLowerCase()) {
        return true;
      }
    }
    return false;
  }

  //############################################################################
  // 文件
  //############################################################################

  /**
   * base64数据转Blob对象
   * @param data base64数据
   * @param type 文件类型
   * @return Blob对象
   */
  static base64ToBlob(data, type) {
    const rawData = data.split(',');
    const array = rawData[0].match(/:(.*?);/);
    const mime = (array && array.length > 1 ? array[1] : type) || type;
    const bytes = window.atob(rawData[1]);
    const buff = new ArrayBuffer(bytes.length);
    const u8Buff = new Uint8Array(buff);
    for (let i = 0; i < bytes.length; i++) {
      u8Buff[i] = bytes.charCodeAt(i);
    }
    return new Blob([buff], {"type": mime});
  }

  /**
   * 下载导出文件
   * @param blob Blob对象
   * @param fileName 下载默认的文件名（包含后缀）
   */
  static downloadExportFile(blob, fileName) {
    const downloadElement = document.createElement('a');
    let href = blob;
    if ("string" == typeof(blob)) {
      downloadElement.target = "_blank";
    } else {
      href = window.URL.createObjectURL(blob);
    }
    downloadElement.href = href;
    downloadElement.download = fileName;
    document.body.appendChild(downloadElement);
    downloadElement.click();
    document.body.removeChild(downloadElement);
    if ("string" != typeof(blob)) {
      window.URL.revokeObjectURL(href);
    }
  }

  //############################################################################
  // JSONObject
  //############################################################################

  /**
   * 判断JSON对象是否存在指定key（如果key值为null，视为不存在）
   * @param jsonObject JSON对象
   * @param key 指定key
   * @return 存在返回true，否则返回false。
   */
  static isJSONObjectExistKey(jsonObject, key) {
    if (Toolkit.stringEqualsIgnoreCase("object", typeof(jsonObject))) {
      if (jsonObject.hasOwnProperty(key)) {
        if (null != jsonObject[key]) {
          return true;
        }
      }
    }
    return false;
  }


  /**
   * 遍历JSON对象的key和value并作为参数传入回调函数
   * @param jsonObject JSON对象
   * @param callback 回调函数：callback(key, value)
   */
  static eachJSONObjectKV(jsonObject, callback) {
    for (const key in jsonObject) {
      callback(key, jsonObject[key]);
    }
  }

  //############################################################################
  // 时间与日期
  //############################################################################

  /**
   * 获取当前年份
   * @param isFull 是否为全格式。如果当前是1999年：全格式1999；非全格式99。
   * @return 当前年份
   */
  static getCurrentYear(isFull) {
    const fullYear = new Date().getFullYear();
    if (isFull) {
      return parseInt(fullYear);
    }
    const fullYearStr = fullYear.toString();
    return fullYearStr.substr(fullYearStr.length - 2);
  }

  //############################################################################
  // 页面样式
  //############################################################################

  /**
   * 获取DOM元素的Rect
   * Rect包括：x、y、width、height、top、right、bottom、left。
   * @param domElement DOM元素
   */
  static getDomElementRect(domElement) {
    return domElement.getBoundingClientRect();
  }

  /**
   * 设置Html的font-size
   *
   * @param baseValue 基础值（比如：设计中一个按钮的宽度是100px，那么可以假设它
   *                  的宽度是10rem，那么基础值就是10，它是一个相对换算的定义）
   * @param designWidth 设计稿宽度（比如：在Axure中设计App或小程序，一般都以750p
   *                    x的宽度设计页面，那么设计稿宽度就是750）
   */
  static setHtmlFontSize(baseValue, designWidth) {
    const htmlWidth = Toolkit.getDomElementRect(document.getElementsByTagName("html")[0]).width;
    const fontSize = baseValue * htmlWidth / designWidth;
    ////////////////////////////////////////////////////////////////////////////
    // [注释说明]这里不能用jquery中css()设置html的font-size，会出现设置与获取值
    // 不符的问题。虽然设置没有问题，但为了一致统一采用DOM的方法设置font-size。
    ////////////////////////////////////////////////////////////////////////////
    document.getElementsByTagName("html")[0].style.fontSize = fontSize + "px";
  }

  /**
   * 获取Html的font-size
   */
  static getHtmlFontSize() {
    ////////////////////////////////////////////////////////////////////////////
    // [注释说明]这里不能用jquery中css()获取html的font-size，会出现设置与获取值
    // 不符的问题。虽然设置没有问题，但为了一致统一采用DOM的方法设置font-size。
    ////////////////////////////////////////////////////////////////////////////
    return parseInt(document.getElementsByTagName("html")[0].style.fontSize);
  }

  /**
   * 动态加载JavaScript
   * @param url javascript地址
   * @param callback 加载完成后的回调函数
   */
  static dynamicLoadJavaScript(url, callback) {
    const script = document.createElement("script");
    script.type = "text/javascript";
    if (script.readyState) {
      script.onload = script.onreadystatechange = function () {
        if (Toolkit.stringEqualsIgnoreCase("loaded", script.readyState) || Toolkit.stringEqualsIgnoreCase("complete", script.readyState)) {
          script.onreadystatechange = null;
          callback();
        }
      };
    } else {
      script.onload = function () {
        callback();
      };
    }
    script.src = url;
    document.getElementsByTagName("head")[0].appendChild(script);
  }

  /**
   * DOM元素加载完成后的回调
   * [备注]以content: url(...)形式显示图片的img无法判断。
   * @param domElement DOM元素
   * @param param 传入call的参数
   * @param callback 加载完成后的回调函数
   */
  static domElementLoadedCallback(domElement, param, callback) {
    if (domElement.readyState) {
      domElement.onload = domElement.onreadystatechange = function () {
        if (Toolkit.stringEqualsIgnoreCase("loaded", script.readyState) || Toolkit.stringEqualsIgnoreCase("complete", script.readyState)) {
          domElement.onreadystatechange = null;
          callback(param);
        }
      };
    } else {
      domElement.onload = function () {
        callback(param);
      };
    }
  }

  //############################################################################
  // 动态参数
  //############################################################################

  /**
   * 根据参数名从url获取参数值
   * @param name 参数名
   * @return 返回参数名对应的值值，没有找到返回null。
   */
  static getUrlParameter(name) {
    if (null == name) {
      return null;
    }
    const reg = new RegExp(`(^|&)${name}=([^&]*)(&|$)`);
    const result = window.location.search.substr(1).match(reg);
    if (null != result) {
      const value = result[2];
      if (0 >= value.length) {
        return null;
      }
      return value;
    } else {
      return null;
    }
  }

  //############################################################################
  // 编码
  //############################################################################

  /**
   * Url参数编码
   * @param value 值
   * @return 返回对应值的Url编码（如果值为null，则返回null）。
   */
  static urlParameterEncode(value) {
    if (null == value) {
      return null;
    }
    return encodeURIComponent(value);
  }

  /**
   * Url参数解码
   * @param value 值
   * @return 返回对应值的Url解码（如果值为null，则返回null）。
   */
  static urlParameterDecode(value) {
    if (null == value) {
      return null;
    }
    return decodeURIComponent(value);
  }
}
