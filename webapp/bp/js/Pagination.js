"use strict";

class Pagination extends ObjectFactory {
  /**
   * 构造函数
   * @param source 调用源
   */
  constructor(source) {
    ////////////////////////////////////////////////////////////////////////////
    // 父类构造函数。
    ////////////////////////////////////////////////////////////////////////////
    super();
    ////////////////////////////////////////////////////////////////////////////
    // 调用源。
    ////////////////////////////////////////////////////////////////////////////
    this.source = source;
    ////////////////////////////////////////////////////////////////////////////
    // 分页按钮的文本。
    ////////////////////////////////////////////////////////////////////////////
    this.objectPreviousButtonText = "<";
    this.objectNextButtonText = ">";
    ////////////////////////////////////////////////////////////////////////////
    // 分页查询的参数。
    ////////////////////////////////////////////////////////////////////////////
    this.objectPageCount = -1;
    this.objectOffset = -1;
    this.objectLimit = -1;
    this.objectTotalCount = -1;
    this.dataOffset = -1;
    ////////////////////////////////////////////////////////////////////////////
    // 按钮点击的回调函数。
    ////////////////////////////////////////////////////////////////////////////
    this.buttonClickCallbackFunc = null;
  }

  /**
   * 获取dataOffset
   */
  getDataOffset() {
    return this.dataOffset;
  }

  /**
   * 设置按钮点击回调函数
   */
  setButtonClickCallbackFunc(func) {
    this.buttonClickCallbackFunc = func;
  }

  /**
   * 设置上一页按钮的文本
   */
  setPreviousButtonText(previousButtonText) {
    this.objectPreviousButtonText = previousButtonText;
  }

  /**
   * 设置下一页按钮的文本
   */
  setNextButtonText(nextButtonText) {
    this.objectNextButtonText = nextButtonText;
  }

  /**
   * 设置页签数量（不包括“向左”和“向右”）
   */
  setPageCount(pageCount) {
    this.objectPageCount = pageCount;
  }

  /**
   * 设置当前显示数据的偏移（从0开始）
   */
  setOffset(offset) {
    this.objectOffset = offset;
  }

  /**
   * 设置显示的条目数
   */
  setLimit(limit) {
    this.objectLimit = limit;
  }

  /**
   * 设置总页数
   */
  setTotalCount(totalCount) {
    if (0 >= totalCount) {
      this.objectTotalCount = -1;
    } else {
      this.objectTotalCount = totalCount;
    }
  }

  /**
   * 获取代码
   */
  getCode() {
    return this.objectCode;
  }

  /**
   * 初始化事件
   */
  initEvent() {
    ////////////////////////////////////////////////////////////////////////////
    // [本地资源]当前对象。
    ////////////////////////////////////////////////////////////////////////////
    const thisObj = this;
    this.getObject().find("button").off("click").on("click", function() {
      //////////////////////////////////////////////////////////////////////////
      // 重置分页中global_button_primary按钮为global_button_default。
      //////////////////////////////////////////////////////////////////////////
      const gbpList = thisObj.getObject().find(".global_button_primary");
      gbpList.removeClass("global_button_primary");
      gbpList.addClass("global_button_default");
      //////////////////////////////////////////////////////////////////////////
      // 将global_button_primary添加至点击的button。
      //////////////////////////////////////////////////////////////////////////
      $(this).removeClass("global_button_default");
      $(this).addClass("global_button_primary");
      //////////////////////////////////////////////////////////////////////////
      // 更新dataOffset。
      //////////////////////////////////////////////////////////////////////////
      thisObj.dataOffset = $(this).attr("data-offset");
      //////////////////////////////////////////////////////////////////////////
      // 回调按钮点击事件函数。
      //////////////////////////////////////////////////////////////////////////
      thisObj.buttonClickCallbackFunc && thisObj.buttonClickCallbackFunc(thisObj.source)
    });
  }

  /**
   * 生成代码
   */
  generateCode() {
    ////////////////////////////////////////////////////////////////////////////
    // 属性代码。
    ////////////////////////////////////////////////////////////////////////////
    let attributeCode = "";
    Toolkit.eachJSONObjectKV(this.objectAttribute, function(key, value) {
      attributeCode += `${key} = "${value}"`;
    });
    ////////////////////////////////////////////////////////////////////////////
    // 检查数据的合法性。
    ////////////////////////////////////////////////////////////////////////////
    if ((-1 == this.objectPageCount) || (-1 == this.objectOffset) || (-1 == this.objectLimit) || (-1 == this.objectTotalCount) || (this.objectOffset > this.objectTotalCount)) {
      this.objectCode = `
        <div id = "${this.getId()}" ${attributeCode}>
          <button class = "global_button_primary" data-offset = "0" disabled = "disabled">1</button>
        </div>
      `;
      return;
    }
    let currentPage = 0;
    this.objectCode = `<div id = "${this.getId()}" ${attributeCode}>`;
    if (0 >= this.objectOffset) {
      currentPage = 1;
    } else {
      currentPage = Math.ceil(this.objectOffset / this.objectLimit) + 1;
    }
    const count = Math.ceil(this.objectTotalCount / this.objectLimit);
    const displaySceneCount = Math.ceil(count / this.objectPageCount);
    const currentPageSceneNum = Math.ceil(currentPage / this.objectPageCount);
    if (currentPageSceneNum > 1) {
      const dataOffset = ((currentPageSceneNum - 1) * this.objectPageCount * this.objectLimit) - this.objectLimit;
      this.objectCode += `<button class = "global_button_default" data-offset = "${dataOffset}">${this.objectPreviousButtonText}</button>`;
    }
    for (let i = ((currentPageSceneNum * this.objectPageCount) - this.objectPageCount + 1); i <= (currentPageSceneNum * this.objectPageCount); i++) {
      if (i > count) {
        break;
      }
      const dataOffset = i * this.objectLimit - this.objectLimit;
      if (i == (currentPage)) {
        this.objectCode += `<button class = "global_button_primary" data-offset = "${dataOffset}">${i}</button>`;
      } else {
        this.objectCode += `<button class = "global_button_default" data-offset = "${dataOffset}">${i}</button>`;
      }
    }
    if ((displaySceneCount - currentPageSceneNum) >= 1) {
      const dataOffset = currentPageSceneNum * this.objectPageCount * this.objectLimit;
      this.objectCode += `<button class = "global_button_default" data-offset = "${dataOffset}">${this.objectNextButtonText}</button>`;
    }
    this.objectCode += `</div>`;
  }
}
