"use strict";

class PlaceWhdayAM {
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
    // 地点标签。
    ////////////////////////////////////////////////////////////////////////////
    this.placeLabel = new JSControl("label");
    ////////////////////////////////////////////////////////////////////////////
    // 地点输入框。
    ////////////////////////////////////////////////////////////////////////////
    this.placeTextField = new JSControl("input");
    ////////////////////////////////////////////////////////////////////////////
    // 类型标签。
    ////////////////////////////////////////////////////////////////////////////
    this.typeLabel = new JSControl("label");
    ////////////////////////////////////////////////////////////////////////////
    // 类型下拉框。
    ////////////////////////////////////////////////////////////////////////////
    this.typeSelect = new JSControl("select");
    ////////////////////////////////////////////////////////////////////////////
    // 工作休息日时间标签。
    ////////////////////////////////////////////////////////////////////////////
    this.whdayDatetimeLabel = new JSControl("label");
    ////////////////////////////////////////////////////////////////////////////
    // 工作休息日时间输入框。
    ////////////////////////////////////////////////////////////////////////////
    this.whdayDatetimeTextField = new JSControl("input");
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
    // 地点标签。
    ////////////////////////////////////////////////////////////////////////////
    this.placeLabel.setAttribute(
      {
        "class": "global_label"
      }
    );
    this.placeLabel.setContent("地点");
    ////////////////////////////////////////////////////////////////////////////
    // 地点输入框。
    ////////////////////////////////////////////////////////////////////////////
    this.placeTextField.setAttribute(
      {
        "disabled": "disabled",
        "type": "text",
        "class": "global_input",
        "style": "width: -webkit-fill-available;",
        "value": this.source.placeFullName
      }
    );
    ////////////////////////////////////////////////////////////////////////////
    // 类型标签。
    ////////////////////////////////////////////////////////////////////////////
    this.typeLabel.setAttribute(
      {
        "class": "global_label"
      }
    );
    this.typeLabel.setContent("类型");
    ////////////////////////////////////////////////////////////////////////////
    // 类型下拉框。
    ////////////////////////////////////////////////////////////////////////////
    this.typeSelect.setAttribute(
      {
        "class": "global_select",
        "style": "width: 100%;"
      }
    );
    this.typeSelect.setContent(`
      <option value = "ON">工作</option>
      <option value = "OFF">休息</option>
    `);
    ////////////////////////////////////////////////////////////////////////////
    // 工作休息日时间标签。
    ////////////////////////////////////////////////////////////////////////////
    this.whdayDatetimeLabel.setAttribute(
      {
        "class": "global_label"
      }
    );
    this.whdayDatetimeLabel.setContent("时间");
    ////////////////////////////////////////////////////////////////////////////
    // 工作休息日时间输入框。
    ////////////////////////////////////////////////////////////////////////////
    this.whdayDatetimeTextField.setAttribute(
      {
        "type": "text",
        "class": "global_input",
        "placeholder": Module.getMethodParameterRuleObj(this.source.placeWhdayRule, "addPlaceWhday", "whday_datetime").format_prompt,
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
    // 地点标签。
    ////////////////////////////////////////////////////////////////////////////
    this.placeLabel.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // 地点输入框。
    ////////////////////////////////////////////////////////////////////////////
    this.placeTextField.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // 类型标签。
    ////////////////////////////////////////////////////////////////////////////
    this.typeLabel.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // 类型下拉框。
    ////////////////////////////////////////////////////////////////////////////
    this.typeSelect.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // 工作休息日时间标签。
    ////////////////////////////////////////////////////////////////////////////
    this.whdayDatetimeLabel.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // 工作休息日时间输入框。
    ////////////////////////////////////////////////////////////////////////////
    this.whdayDatetimeTextField.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // 表单列表。
    ////////////////////////////////////////////////////////////////////////////
    const list = new Array();
    list.push({"attr": {"style": "display: none;"}, "required": false, "label_code": `${this.uuidLabel.getCode()}`, "control_id": `${this.uuidTextField.getId()}`, "control_code": `${this.uuidTextField.getCode()}`});
    list.push({"attr": {}, "required": true, "label_code": `${this.placeLabel.getCode()}`, "control_id": `${this.placeTextField.getId()}`, "control_code": `${this.placeTextField.getCode()}`});
    list.push({"attr": {}, "required": true, "label_code": `${this.typeLabel.getCode()}`, "control_id": `${this.typeSelect.getId()}`, "control_code": `${this.typeSelect.getCode()}`});
    list.push({"attr": {}, "required": true, "label_code": `${this.whdayDatetimeLabel.getCode()}`, "control_id": `${this.whdayDatetimeTextField.getId()}`, "control_code": `${this.whdayDatetimeTextField.getCode()}`});
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
    ////////////////////////////////////////////////////////////////////////////
    // 初始化时间控件
    // 全格式时间为：Y-m-d H:i:s，比如：2001-03-10 17:16:18
    ////////////////////////////////////////////////////////////////////////////
    $("#" + this.whdayDatetimeTextField.getId()).datetimepicker({
      "timepicker": false, // 隐藏时间选择器
      "format": "Y-m-d" // 时间格式，比如：2001-03-10。
    });
    $.datetimepicker.setLocale("zh"); // 设置语言为简体中文
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
    this.typeSelect.getObject().attr("disabled", "disabled");
    this.whdayDatetimeTextField.getObject().attr("disabled", "disabled");
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
      this.typeSelect.getObject().removeAttr("disabled");
      this.whdayDatetimeTextField.getObject().removeAttr("disabled");
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
    this.placeTextField.getObject().val("");
    this.typeSelect.getObject().find("option:eq(0)").prop("selected", true);
    this.whdayDatetimeTextField.getObject().val("");
  }
}