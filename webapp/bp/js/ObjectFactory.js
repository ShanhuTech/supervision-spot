"use strict";

/**
 * 对象工厂
 */
class ObjectFactory {
  /**
   * 构造函数
   */
  constructor() {
    ////////////////////////////////////////////////////////////////////////////
    // 对象的id。
    ////////////////////////////////////////////////////////////////////////////
    this.objectId = Toolkit.generateUuid();
    ////////////////////////////////////////////////////////////////////////////
    // 对象的attribute（必须用""，因为使用ObjectFactory对象时，会直接对其进行字
    // 符串操作。
    ////////////////////////////////////////////////////////////////////////////
    this.objectAttribute = "";
    ////////////////////////////////////////////////////////////////////////////
    // 对象的content（必须用""，因为使用ObjectFactory对象时，会直接对其进行字符
    // 串操作。
    ////////////////////////////////////////////////////////////////////////////
    this.objectContent = "";
    ////////////////////////////////////////////////////////////////////////////
    // 对象的code（必须用""，因为使用ObjectFactory对象时，会直接对其进行字符串操
    // 作。
    ////////////////////////////////////////////////////////////////////////////
    this.objectCode = "";
  }

  /**
   * 获取对象的id
   */
  getId() {
    return this.objectId;
  }

  /**
   * 获取对象的object
   */
  getObject() {
    return $("#" + this.getId());
  }

  /**
   * 设置对象的attribute
   *
   * attribute的格式为json对象，其中封装了标签的属性和标签值。如：
   * {
   *   "class": "default-theme",
   *   "style": "width: 10rem; height: 20rem;"
   * }
   */
  setAttribute(attribute) {
    this.objectAttribute = attribute;
  }

  /**
   * 设置对象的content
   * @param content 内容
   */
  setContent(content) {
    this.objectContent = content;
  }

  /**
   * 获取对象的代码
   */
  getCode() {
    return this.objectCode;
  }
}
