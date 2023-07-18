"use strict";

class ModifyPassword {
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
    // 旧密码标签。
    ////////////////////////////////////////////////////////////////////////////
    this.oldPasswordLabel = new JSControl("label");
    ////////////////////////////////////////////////////////////////////////////
    // 旧密码输入框。
    ////////////////////////////////////////////////////////////////////////////
    this.oldPasswordTextField = new JSControl("input");
    ////////////////////////////////////////////////////////////////////////////
    // 新密码标签。
    ////////////////////////////////////////////////////////////////////////////
    this.newPasswordLabel = new JSControl("label");
    ////////////////////////////////////////////////////////////////////////////
    // 新密码输入框。
    ////////////////////////////////////////////////////////////////////////////
    this.newPasswordTextField = new JSControl("input");
    ////////////////////////////////////////////////////////////////////////////
    // 表单列表。
    ////////////////////////////////////////////////////////////////////////////
    this.formList = new FormList();
    ////////////////////////////////////////////////////////////////////////////
    // 弹窗。
    ////////////////////////////////////////////////////////////////////////////
    this.popupWindow = new PopupWindow(this, this.windowTitle, this.windowWidth, this.popupWindowHideCallback);
    ////////////////////////////////////////////////////////////////////////////
    // 旧密码标签。
    ////////////////////////////////////////////////////////////////////////////
    this.oldPasswordLabel.setAttribute(
      {
        "class": "global_label"
      }
    );
    this.oldPasswordLabel.setContent("旧密码");
    ////////////////////////////////////////////////////////////////////////////
    // 旧密码输入框。
    ////////////////////////////////////////////////////////////////////////////
    this.oldPasswordTextField.setAttribute(
      {
        "type": "password",
        "class": "global_input",
        "placeholder": Module.getMethodParameterRuleObj(this.source.adminRule, "modifyAdminPasswordOfSelf", "old_password").format_prompt,
        "style": "width: -webkit-fill-available;"
      }
    );
    ////////////////////////////////////////////////////////////////////////////
    // 新密码标签。
    ////////////////////////////////////////////////////////////////////////////
    this.newPasswordLabel.setAttribute(
      {
        "class": "global_label"
      }
    );
    this.newPasswordLabel.setContent("新密码");
    ////////////////////////////////////////////////////////////////////////////
    // 新密码输入框。
    ////////////////////////////////////////////////////////////////////////////
    this.newPasswordTextField.setAttribute(
      {
        "type": "password",
        "class": "global_input",
        "placeholder": Module.getMethodParameterRuleObj(this.source.adminRule, "modifyAdminPasswordOfSelf", "new_password").format_prompt,
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
    // 旧密码标签。
    ////////////////////////////////////////////////////////////////////////////
    this.oldPasswordLabel.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // 旧密码输入框。
    ////////////////////////////////////////////////////////////////////////////
    this.oldPasswordTextField.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // 新密码标签。
    ////////////////////////////////////////////////////////////////////////////
    this.newPasswordLabel.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // 新密码输入框。
    ////////////////////////////////////////////////////////////////////////////
    this.newPasswordTextField.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // 表单列表。
    ////////////////////////////////////////////////////////////////////////////
    const list = new Array();
    list.push({"attr": {}, "required": true, "label_code": `${this.oldPasswordLabel.getCode()}`, "control_id": `${this.oldPasswordTextField.getId()}`, "control_code": `${this.oldPasswordTextField.getCode()}`});
    list.push({"attr": {}, "required": true, "label_code": `${this.newPasswordLabel.getCode()}`, "control_id": `${this.newPasswordTextField.getId()}`, "control_code": `${this.newPasswordTextField.getCode()}`});
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
    // 设置焦点在旧密码输入框。
    ////////////////////////////////////////////////////////////////////////////
    this.oldPasswordTextField.getObject().focus();
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
   *
   * @param name 冻结标记名称
   */
  frozenControl(name) {
    ////////////////////////////////////////////////////////////////////////////
    // 存入队列。
    ////////////////////////////////////////////////////////////////////////////
    this.queue.push(name);
    this.oldPasswordTextField.getObject().attr("disabled", "disabled");
    this.newPasswordTextField.getObject().attr("disabled", "disabled");
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
      this.oldPasswordTextField.getObject().removeAttr("disabled");
      this.newPasswordTextField.getObject().removeAttr("disabled");
      this.formList.confirmButton.getObject().removeAttr("disabled");
      this.formList.cancelButton.getObject().removeAttr("disabled");
      this.popupWindow.recoverEvent();
    }
  }

  /**
   * 重置控件
   */
  resetControl() {
    this.oldPasswordTextField.getObject().val("");
    this.newPasswordTextField.getObject().val("");
  }
}