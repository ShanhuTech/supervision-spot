"use strict";

class CameraAM {
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
    // 一级标签。
    ////////////////////////////////////////////////////////////////////////////
    this.levelOneLabel = new JSControl("label");
    ////////////////////////////////////////////////////////////////////////////
    // 一级下拉框。
    ////////////////////////////////////////////////////////////////////////////
    this.levelOneSelect = new JSControl("select");
    ////////////////////////////////////////////////////////////////////////////
    // 二级标签。
    ////////////////////////////////////////////////////////////////////////////
    this.levelTwoLabel = new JSControl("label");
    ////////////////////////////////////////////////////////////////////////////
    // 二级下拉框。
    ////////////////////////////////////////////////////////////////////////////
    this.levelTwoSelect = new JSControl("select");
    ////////////////////////////////////////////////////////////////////////////
    // 三级标签。
    ////////////////////////////////////////////////////////////////////////////
    this.levelThreeLabel = new JSControl("label");
    ////////////////////////////////////////////////////////////////////////////
    // 三级下拉框。
    ////////////////////////////////////////////////////////////////////////////
    this.levelThreeSelect = new JSControl("select");
    ////////////////////////////////////////////////////////////////////////////
    // 四级标签。
    ////////////////////////////////////////////////////////////////////////////
    this.levelFourLabel = new JSControl("label");
    ////////////////////////////////////////////////////////////////////////////
    // 四级下拉框。
    ////////////////////////////////////////////////////////////////////////////
    this.levelFourSelect = new JSControl("select");
    ////////////////////////////////////////////////////////////////////////////
    // 五级标签。
    ////////////////////////////////////////////////////////////////////////////
    this.levelFiveLabel = new JSControl("label");
    ////////////////////////////////////////////////////////////////////////////
    // 五级下拉框。
    ////////////////////////////////////////////////////////////////////////////
    this.levelFiveSelect = new JSControl("select");
    ////////////////////////////////////////////////////////////////////////////
    // 名称标签。
    ////////////////////////////////////////////////////////////////////////////
    this.nameLabel = new JSControl("label");
    ////////////////////////////////////////////////////////////////////////////
    // 名称输入框。
    ////////////////////////////////////////////////////////////////////////////
    this.nameTextField = new JSControl("input");
    ////////////////////////////////////////////////////////////////////////////
    // 地址标签。
    ////////////////////////////////////////////////////////////////////////////
    this.urlLabel = new JSControl("label");
    ////////////////////////////////////////////////////////////////////////////
    // 地址输入框。
    ////////////////////////////////////////////////////////////////////////////
    this.urlTextField = new JSControl("input");
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
    // 一级标签。
    ////////////////////////////////////////////////////////////////////////////
    this.levelOneLabel.setAttribute(
      {
        "class": "global_label"
      }
    );
    this.levelOneLabel.setContent("一级地点");
    ////////////////////////////////////////////////////////////////////////////
    // 一级下拉框。
    ////////////////////////////////////////////////////////////////////////////
    this.levelOneSelect.setAttribute(
      {
        "disabled": "disabled",
        "class": "global_select",
        "style": "width: 100%;"
      }
    );
    ////////////////////////////////////////////////////////////////////////////
    // 二级标签。
    ////////////////////////////////////////////////////////////////////////////
    this.levelTwoLabel.setAttribute(
      {
        "class": "global_label"
      }
    );
    this.levelTwoLabel.setContent("二级地点");
    ////////////////////////////////////////////////////////////////////////////
    // 二级下拉框。
    ////////////////////////////////////////////////////////////////////////////
    this.levelTwoSelect.setAttribute(
      {
        "disabled": "disabled",
        "class": "global_select",
        "style": "width: 100%;"
      }
    );
    ////////////////////////////////////////////////////////////////////////////
    // 三级标签。
    ////////////////////////////////////////////////////////////////////////////
    this.levelThreeLabel.setAttribute(
      {
        "class": "global_label"
      }
    );
    this.levelThreeLabel.setContent("三级地点");
    ////////////////////////////////////////////////////////////////////////////
    // 三级下拉框。
    ////////////////////////////////////////////////////////////////////////////
    this.levelThreeSelect.setAttribute(
      {
        "disabled": "disabled",
        "class": "global_select",
        "style": "width: 100%;"
      }
    );
    ////////////////////////////////////////////////////////////////////////////
    // 四级标签。
    ////////////////////////////////////////////////////////////////////////////
    this.levelFourLabel.setAttribute(
      {
        "class": "global_label"
      }
    );
    this.levelFourLabel.setContent("四级地点");
    ////////////////////////////////////////////////////////////////////////////
    // 四级下拉框。
    ////////////////////////////////////////////////////////////////////////////
    this.levelFourSelect.setAttribute(
      {
        "disabled": "disabled",
        "class": "global_select",
        "style": "width: 100%;"
      }
    );
    ////////////////////////////////////////////////////////////////////////////
    // 五级标签。
    ////////////////////////////////////////////////////////////////////////////
    this.levelFiveLabel.setAttribute(
      {
        "class": "global_label"
      }
    );
    this.levelFiveLabel.setContent("五级地点");
    ////////////////////////////////////////////////////////////////////////////
    // 五级下拉框。
    ////////////////////////////////////////////////////////////////////////////
    this.levelFiveSelect.setAttribute(
      {
        "disabled": "disabled",
        "class": "global_select",
        "style": "width: 100%;"
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
        "placeholder": Module.getMethodParameterRuleObj(this.source.cameraRule, "addCamera", "name").format_prompt,
        "style": "width: -webkit-fill-available;"
      }
    );
    ////////////////////////////////////////////////////////////////////////////
    // 地址标签。
    ////////////////////////////////////////////////////////////////////////////
    this.urlLabel.setAttribute(
      {
        "class": "global_label"
      }
    );
    this.urlLabel.setContent("地址");
    ////////////////////////////////////////////////////////////////////////////
    // 地址输入框。
    ////////////////////////////////////////////////////////////////////////////
    this.urlTextField.setAttribute(
      {
        "type": "url",
        "class": "global_input",
        "placeholder": Module.getMethodParameterRuleObj(this.source.cameraRule, "addCamera", "url").format_prompt,
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
    // 一级标签。
    ////////////////////////////////////////////////////////////////////////////
    this.levelOneLabel.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // 一级下拉框。
    ////////////////////////////////////////////////////////////////////////////
    this.levelOneSelect.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // 二级标签。
    ////////////////////////////////////////////////////////////////////////////
    this.levelTwoLabel.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // 二级下拉框。
    ////////////////////////////////////////////////////////////////////////////
    this.levelTwoSelect.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // 三级标签。
    ////////////////////////////////////////////////////////////////////////////
    this.levelThreeLabel.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // 三级下拉框。
    ////////////////////////////////////////////////////////////////////////////
    this.levelThreeSelect.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // 四级标签。
    ////////////////////////////////////////////////////////////////////////////
    this.levelFourLabel.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // 四级下拉框。
    ////////////////////////////////////////////////////////////////////////////
    this.levelFourSelect.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // 五级标签。
    ////////////////////////////////////////////////////////////////////////////
    this.levelFiveLabel.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // 五级下拉框。
    ////////////////////////////////////////////////////////////////////////////
    this.levelFiveSelect.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // 名称标签。
    ////////////////////////////////////////////////////////////////////////////
    this.nameLabel.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // 名称输入框。
    ////////////////////////////////////////////////////////////////////////////
    this.nameTextField.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // 地址标签。
    ////////////////////////////////////////////////////////////////////////////
    this.urlLabel.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // 地址输入框。
    ////////////////////////////////////////////////////////////////////////////
    this.urlTextField.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // 表单列表。
    ////////////////////////////////////////////////////////////////////////////
    const list = new Array();
    list.push({"attr": {"style": "display: none;"}, "required": false, "label_code": `${this.uuidLabel.getCode()}`, "control_id": `${this.uuidTextField.getId()}`, "control_code": `${this.uuidTextField.getCode()}`});
    list.push({"attr": {}, "required": false, "label_code": `${this.levelOneLabel.getCode()}`, "control_id": `${this.levelOneSelect.getId()}`, "control_code": `${this.levelOneSelect.getCode()}`});
    list.push({"attr": {}, "required": false, "label_code": `${this.levelTwoLabel.getCode()}`, "control_id": `${this.levelTwoSelect.getId()}`, "control_code": `${this.levelTwoSelect.getCode()}`});
    list.push({"attr": {}, "required": false, "label_code": `${this.levelThreeLabel.getCode()}`, "control_id": `${this.levelThreeSelect.getId()}`, "control_code": `${this.levelThreeSelect.getCode()}`});
    list.push({"attr": {}, "required": false, "label_code": `${this.levelFourLabel.getCode()}`, "control_id": `${this.levelFourSelect.getId()}`, "control_code": `${this.levelFourSelect.getCode()}`});
    list.push({"attr": {}, "required": false, "label_code": `${this.levelFiveLabel.getCode()}`, "control_id": `${this.levelFiveSelect.getId()}`, "control_code": `${this.levelFiveSelect.getCode()}`});
    list.push({"attr": {}, "required": true, "label_code": `${this.nameLabel.getCode()}`, "control_id": `${this.nameTextField.getId()}`, "control_code": `${this.nameTextField.getCode()}`});
    list.push({"attr": {}, "required": true, "label_code": `${this.urlLabel.getCode()}`, "control_id": `${this.urlTextField.getId()}`, "control_code": `${this.urlTextField.getCode()}`});
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
   *
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
   *
   * @param name 冻结标记名称
   */
  frozenControl(name) {
    ////////////////////////////////////////////////////////////////////////////
    // 存入队列。
    ////////////////////////////////////////////////////////////////////////////
    this.queue.push(name);
    this.levelOneSelect.getObject().attr("disabled", "disabled");
    this.levelTwoSelect.getObject().attr("disabled", "disabled");
    this.levelThreeSelect.getObject().attr("disabled", "disabled");
    this.levelFourSelect.getObject().attr("disabled", "disabled");
    this.levelFiveSelect.getObject().attr("disabled", "disabled");
    this.nameTextField.getObject().attr("disabled", "disabled");
    this.urlTextField.getObject().attr("disabled", "disabled");
    this.formList.confirmButton.getObject().attr("disabled", "disabled");
    this.formList.cancelButton.getObject().attr("disabled", "disabled");
    this.popupWindow.removeEvent();
  }

  /**
   * 恢复控件
   *
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
      this.levelOneSelect.getObject().removeAttr("disabled");
      this.levelTwoSelect.getObject().removeAttr("disabled");
      this.levelThreeSelect.getObject().removeAttr("disabled");
      this.levelFourSelect.getObject().removeAttr("disabled");
      this.levelFiveSelect.getObject().removeAttr("disabled");
      this.nameTextField.getObject().removeAttr("disabled");
      this.urlTextField.getObject().removeAttr("disabled");
      this.formList.confirmButton.getObject().removeAttr("disabled");
      this.formList.cancelButton.getObject().removeAttr("disabled");
      this.popupWindow.recoverEvent();
    }
  }

  /**
   * 重置控件
   */
  resetControl() {
    ////////////////////////////////////////////////////////////////////////////
    // 重置的目的是为了下次输入的方便，对于下拉框来讲不是清空其内容，而是应该重
    // 置默认选项。由于二级下拉框跟随一级下拉框联动，三级下拉框跟随二级下拉框联
    // 动，四级下拉框跟随三级下拉框联动，五级下拉框跟随四级下拉框联动，所以这里
    // 不对除一级以外的下拉框做重置设置。
    ////////////////////////////////////////////////////////////////////////////
    this.uuidTextField.getObject().val("");
    this.levelOneSelect.getObject().find("option:eq(0)").prop("selected", true);
    this.levelOneSelect.getObject().trigger("change");
    this.nameTextField.getObject().val("");
    this.urlTextField.getObject().val("");
  }
}
