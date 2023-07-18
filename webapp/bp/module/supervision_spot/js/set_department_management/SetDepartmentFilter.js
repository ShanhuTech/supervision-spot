"use strict";

class DepartmentFilter {
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
    // 类型标签。
    ////////////////////////////////////////////////////////////////////////////
    this.typeLabel = new JSControl("label");
    ////////////////////////////////////////////////////////////////////////////
    // 类型下拉框。
    ////////////////////////////////////////////////////////////////////////////
    this.typeSelect = new JSControl("select");
    ////////////////////////////////////////////////////////////////////////////
    // 名称标签。
    ////////////////////////////////////////////////////////////////////////////
    this.nameLabel = new JSControl("label");
    ////////////////////////////////////////////////////////////////////////////
    // 名称输入框。
    ////////////////////////////////////////////////////////////////////////////
    this.nameTextField = new JSControl("input");
    ////////////////////////////////////////////////////////////////////////////
    // 表单列表。
    ////////////////////////////////////////////////////////////////////////////
    this.formList = new FormList();
    ////////////////////////////////////////////////////////////////////////////
    // 弹窗。
    ////////////////////////////////////////////////////////////////////////////
    this.popupWindow = new PopupWindow(this, this.windowTitle, this.windowWidth, this.popupWindowHideCallback);
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
        "placeholder": Module.getMethodParameterRuleObj(this.source.departmentRule, "addDepartment", "name").format_prompt,
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
    // 类型标签。
    ////////////////////////////////////////////////////////////////////////////
    this.typeLabel.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // 类型下拉框。
    ////////////////////////////////////////////////////////////////////////////
    this.typeSelect.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // 名称标签。
    ////////////////////////////////////////////////////////////////////////////
    this.nameLabel.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // 名称输入框。
    ////////////////////////////////////////////////////////////////////////////
    this.nameTextField.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // 表单列表。
    ////////////////////////////////////////////////////////////////////////////
    const list = new Array();
    list.push({"attr": {}, "required": false, "label_code": `${this.typeLabel.getCode()}`, "control_id": `${this.typeSelect.getId()}`, "control_code": `${this.typeSelect.getCode()}`});
    list.push({"attr": {}, "required": false, "label_code": `${this.nameLabel.getCode()}`, "control_id": `${this.nameTextField.getId()}`, "control_code": `${this.nameTextField.getCode()}`});
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
    this.typeSelect.getObject().attr("disabled", "disabled");
    this.nameTextField.getObject().attr("disabled", "disabled");
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
      this.typeSelect.getObject().removeAttr("disabled");
      this.nameTextField.getObject().removeAttr("disabled");
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
    this.typeSelect.getObject().find("option:eq(0)").prop("selected", true);
    this.nameTextField.getObject().val("");
  }
}