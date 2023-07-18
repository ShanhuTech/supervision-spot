"use strict";

/**
 * 队列
 */
class Queue {
  /**
   * 构造函数
   */
  constructor() {
    this.array = new Array();
  }

  /**
   * 存入
   * @param name 名称
   */
  push(name) {
    let isExist = false;
    for (let i = 0; i < this.array.length; i++) {
      if (name == this.array[i].name) {
        this.array[i].enable = true;
        isExist = true;
        break;
      }
    }
    if (!isExist) {
      this.array.push({
        "name": name,
        "enable": true
      });
    }
  }

  /**
   * 取出
   * @param name 名称
   */
  pop(name) {
    for (let i = 0; i < this.array.length; i++) {
      if (name == this.array[i].name) {
        this.array[i].enable = false;
        break;
      }
    }
  }

  /**
   * 判断是否可用
   * @param name 名称
   */
  isEnable(name) {
    for (let i = 0; i < this.array.length; i++) {
      if (name == this.array[i].name) {
        return this.array[i].enable;
      }
    }
    return false;
  }

  /**
   * 队列是否为空
   * @return true: 队列是空的，所有元素全都pop了出去。
   *         false: 队列不是空的，尚有元素未pop出去。
   */
  isEmpty() {
    for (let i = 0; i < this.array.length; i++) {
      if (this.array[i].enable) {
        return false;
      }
    }
    return true;
  }
}
