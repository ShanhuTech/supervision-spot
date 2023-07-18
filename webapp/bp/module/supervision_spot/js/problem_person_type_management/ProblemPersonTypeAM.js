"use strict";

class ProblemPersonTypeAM {
  /**
   * 构造函数
   *
   * @param source 调用源
   * @param windowTitle 窗口标题
   * @param windowWidth 窗口宽度
   */
  constructor(source, windowTitle, windowWidth) {
    ////////////////////////////////////////////////////////////////////////////
    // 调用源。
    ////////////////////////////////////////////////////////////////////////////
    this.source = source;
    ////////////////////////////////////////////////////////////////////////////
    // 赋值窗口标题。
    ////////////////////////////////////////////////////////////////////////////
    this.windowTitle = windowTitle;
    ////////////////////////////////////////////////////////////////////////////
    // 赋值窗口宽度。
    ////////////////////////////////////////////////////////////////////////////
    this.windowWidth = windowWidth;
    ////////////////////////////////////////////////////////////////////////////
    // 窗口class标记。
    ////////////////////////////////////////////////////////////////////////////
    this.windowClassSign = "";
    ////////////////////////////////////////////////////////////////////////////
    // 队列。
    ////////////////////////////////////////////////////////////////////////////
    this.queue = new Queue();
  }

  /**
   * 设置窗口class标记
   */
  setClassSign(classSign) {
    this.windowClassSign = classSign;
  }

  /**
   * 生成代码
   */
  generateCode() {
    ////////////////////////////////////////////////////////////////////////////
    // uuid标签。
    ////////////////////////////////////////////////////////////////////////////
    this.uuidLabel = new JSControl("label");
    ////////////////////////////////////////////////////////////////////////////
    // uuid输入框。
    ////////////////////////////////////////////////////////////////////////////
    this.uuidTextField = new JSControl("input");
    ////////////////////////////////////////////////////////////////////////////
    // 名称标签。
    ////////////////////////////////////////////////////////////////////////////
    this.nameLabel = new JSControl("label");
    ////////////////////////////////////////////////////////////////////////////
    // 名称输入框。
    ////////////////////////////////////////////////////////////////////////////
    this.nameTextField = new JSControl("input");
    ////////////////////////////////////////////////////////////////////////////
    // 分值标签。
    ////////////////////////////////////////////////////////////////////////////
    this.scoreLabel = new JSControl("label");
    ////////////////////////////////////////////////////////////////////////////
    // 分值输入框。
    ////////////////////////////////////////////////////////////////////////////
    this.scoreTextField = new JSControl("input");
    ////////////////////////////////////////////////////////////////////////////
    // 排序标签。
    ////////////////////////////////////////////////////////////////////////////
    this.orderLabel = new JSControl("label");
    ////////////////////////////////////////////////////////////////////////////
    // 排序输入框。
    ////////////////////////////////////////////////////////////////////////////
    this.orderTextField = new JSControl("input");
    ////////////////////////////////////////////////////////////////////////////
    // 表单列表。
    ////////////////////////////////////////////////////////////////////////////
    this.formList = new FormList();
    ////////////////////////////////////////////////////////////////////////////
    // 弹窗。
    ////////////////////////////////////////////////////////////////////////////
    this.popupWindow = new PopupWindow(this, this.windowTitle, this.windowWidth, this.popupWindowHideCallback);
    ////////////////////////////////////////////////////////////////////////////
    // uuid标签。
    ////////////////////////////////////////////////////////////////////////////
    this.uuidLabel.setAttribute(
      {
        "class": "global_label"
      }
    );
    this.uuidLabel.setContent("uuid");
    ////////////////////////////////////////////////////////////////////////////
    // uuid输入框。
    ////////////////////////////////////////////////////////////////////////////
    this.uuidTextField.setAttribute(
      {
        "type": "text",
        "class": "global_input",
        "style": "width: -webkit-fill-available;",
        "disabled": "disabled"
      }
    );
    ////////////////////////////////////////////////////////////////////////////
    // 名称标签。
    ////////////////////////////////////////////////////////////////////////////
    this.nameLabel.setAttribute(
      {
        "class": "global_label"
      }
    );
    this.nameLabel.setContent("名称");
    ////////////////////////////////////////////////////////////////////////////
    // 名称输入框。
    ////////////////////////////////////////////////////////////////////////////
    this.nameTextField.setAttribute(
      {
        "type": "text",
        "class": "global_input",
        "placeholder": Module.getMethodParameterRuleObj(this.source.problemPersonTypeRule, "addProblemPersonType", "name").format_prompt,
        "style": "width: -webkit-fill-available;"
      }
    );
    ////////////////////////////////////////////////////////////////////////////
    // 分值标签。
    ////////////////////////////////////////////////////////////////////////////
    this.scoreLabel.setAttribute(
      {
        "class": "global_label"
      }
    );
    this.scoreLabel.setContent("分值");
    ////////////////////////////////////////////////////////////////////////////
    // 分值输入框。
    ////////////////////////////////////////////////////////////////////////////
    this.scoreTextField.setAttribute(
      {
        "type": "text",
        "class": "global_input",
        "placeholder": Module.getMethodParameterRuleObj(this.source.problemPersonTypeRule, "addProblemPersonType", "score").format_prompt,
        "style": "width: -webkit-fill-available;"
      }
    );
    ////////////////////////////////////////////////////////////////////////////
    // 排序标签。
    ////////////////////////////////////////////////////////////////////////////
    this.orderLabel.setAttribute(
      {
        "class": "global_label"
      }
    );
    this.orderLabel.setContent("排序编号");
    ////////////////////////////////////////////////////////////////////////////
    // 排序输入框。
    ////////////////////////////////////////////////////////////////////////////
    this.orderTextField.setAttribute(
      {
        "type": "text",
        "class": "global_input",
        "placeholder": Module.getMethodParameterRuleObj(this.source.problemPersonTypeRule, "addProblemPersonType", "order").format_prompt,
        "style": "width: -webkit-fill-available;"
      }
    );
    ////////////////////////////////////////////////////////////////////////////
    // 表单列表。
    ////////////////////////////////////////////////////////////////////////////
    this.formList.setAttribute(
      {
        "class": "global_form_list"
      }
    );
    ////////////////////////////////////////////////////////////////////////////
    // 弹窗。
    ////////////////////////////////////////////////////////////////////////////
    this.popupWindow.setAttribute(
      {
        "class": `global_popup_window ${this.windowClassSign}`,
        "style": "display: none;"
      }
    );
    ////////////////////////////////////////////////////////////////////////////
    // uuid标签。
    ////////////////////////////////////////////////////////////////////////////
    this.uuidLabel.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // uuid输入框。
    ////////////////////////////////////////////////////////////////////////////
    this.uuidTextField.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // 名称标签。
    ////////////////////////////////////////////////////////////////////////////
    this.nameLabel.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // 名称输入框。
    ////////////////////////////////////////////////////////////////////////////
    this.nameTextField.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // 分值标签。
    ////////////////////////////////////////////////////////////////////////////
    this.scoreLabel.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // 分值输入框。
    ////////////////////////////////////////////////////////////////////////////
    this.scoreTextField.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // 排序标签。
    ////////////////////////////////////////////////////////////////////////////
    this.orderLabel.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // 排序输入框。
    ////////////////////////////////////////////////////////////////////////////
    this.orderTextField.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // 表单列表。
    ////////////////////////////////////////////////////////////////////////////
    const list = new Array();
    list.push({"attr": {"style": "display: none;"}, "required": false, "label_code": `${this.uuidLabel.getCode()}`, "control_id": `${this.uuidTextField.getId()}`, "control_code": `${this.uuidTextField.getCode()}`});
    list.push({"attr": {}, "required": true, "label_code": `${this.nameLabel.getCode()}`, "control_id": `${this.nameTextField.getId()}`, "control_code": `${this.nameTextField.getCode()}`});
    list.push({"attr": {}, "required": true, "label_code": `${this.scoreLabel.getCode()}`, "control_id": `${this.scoreTextField.getId()}`, "control_code": `${this.scoreTextField.getCode()}`});
    list.push({"attr": {}, "required": true, "label_code": `${this.orderLabel.getCode()}`, "control_id": `${this.orderTextField.getId()}`, "control_code": `${this.orderTextField.getCode()}`});
    this.formList.setContent(list);
    this.formList.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // 弹窗。
    ////////////////////////////////////////////////////////////////////////////
    this.popupWindow.setContent(this.formList.getCode());
    this.popupWindow.generateCode();
  }

  /**
   * 初始化事件
   */
  initEvent() {
    ////////////////////////////////////////////////////////////////////////////
    // 初始化弹窗事件。
    ////////////////////////////////////////////////////////////////////////////
    this.popupWindow.initEvent();
  }

  /**
   * 获取代码
   */
  getCode() {
    ////////////////////////////////////////////////////////////////////////////
    // 由于当前对象基于PopupWindow，所有的内容都放在了popupWindow里面
    // 所以应该返回popupWindow的代码。
    ////////////////////////////////////////////////////////////////////////////
    return this.popupWindow.getCode();
  }

  /**
   * 显示弹窗
   */
  show() {
    ////////////////////////////////////////////////////////////////////////////
    // 显示弹窗。
    ////////////////////////////////////////////////////////////////////////////
    this.popupWindow.show();
    ////////////////////////////////////////////////////////////////////////////
    // 设置焦点在名称输入框。
    ////////////////////////////////////////////////////////////////////////////
    this.nameTextField.getObject().focus();
  }

  /**
   * 隐藏弹窗
   */
  hide() {
    ////////////////////////////////////////////////////////////////////////////
    // 隐藏弹窗。
    ////////////////////////////////////////////////////////////////////////////
    this.popupWindow.hide();
  }

  /**
   * 弹窗隐藏回调方法
   * @param source 调用源
   */
  popupWindowHideCallback(source) {
    ////////////////////////////////////////////////////////////////////////////
    // 隐藏结果信息。
    ////////////////////////////////////////////////////////////////////////////
    source.formList.hideResultInfo();
    ////////////////////////////////////////////////////////////////////////////
    // 隐藏所有提示。
    ////////////////////////////////////////////////////////////////////////////
    source.formList.hideAllPrompt();
    ////////////////////////////////////////////////////////////////////////////
    // 重置控件。
    ////////////////////////////////////////////////////////////////////////////
    source.resetControl();
  }

  /**
   * 冻结控件
   * @param name 冻结标记名称
   */
  frozenControl(name) {
    ////////////////////////////////////////////////////////////////////////////
    // 存入队列。
    ////////////////////////////////////////////////////////////////////////////
    this.queue.push(name);
    this.nameTextField.getObject().attr("disabled", "disabled");
    this.scoreTextField.getObject().attr("disabled", "disabled");
    this.orderTextField.getObject().attr("disabled", "disabled");
    this.formList.confirmButton.getObject().attr("disabled", "disabled");
    this.formList.cancelButton.getObject().attr("disabled", "disabled");
    this.popupWindow.removeEvent();
  }

  /**
   * 恢复控件
   * @param name 恢复标记名称
   */
  recoverControl(name) {
    ////////////////////////////////////////////////////////////////////////////
    // 队列取出。
    ////////////////////////////////////////////////////////////////////////////
    this.queue.pop(name);
    if (this.queue.isEmpty()) {
      //////////////////////////////////////////////////////////////////////////
      // 如果取出了队列中所有的元素才能恢复。
      //////////////////////////////////////////////////////////////////////////
      this.nameTextField.getObject().removeAttr("disabled");
      this.scoreTextField.getObject().removeAttr("disabled");
      this.orderTextField.getObject().removeAttr("disabled");
      this.formList.confirmButton.getObject().removeAttr("disabled");
      this.formList.cancelButton.getObject().removeAttr("disabled");
      this.popupWindow.recoverEvent();
    }
  }

  /**
   * 重置控件
   */
  resetControl() {
    this.uuidTextField.getObject().val("");
    this.nameTextField.getObject().val("");
    this.scoreTextField.getObject().val("");
    this.orderTextField.getObject().val("");
  }
}
