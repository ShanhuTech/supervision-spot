"use strict";

class CameraFilter {
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
    // 状态标签。
    ////////////////////////////////////////////////////////////////////////////
    this.statusLabel = new JSControl("label");
    ////////////////////////////////////////////////////////////////////////////
    // 状态下拉框。
    ////////////////////////////////////////////////////////////////////////////
    this.statusSelect = new JSControl("select");
    ////////////////////////////////////////////////////////////////////////////
    // 表单列表。
    ////////////////////////////////////////////////////////////////////////////
    this.formList = new FormList();
    ////////////////////////////////////////////////////////////////////////////
    // 弹窗。
    ////////////////////////////////////////////////////////////////////////////
    this.popupWindow = new PopupWindow(this, this.windowTitle, this.windowWidth, this.popupWindowHideCallback);
    ////////////////////////////////////////////////////////////////////////////
    // 状态标签。
    ////////////////////////////////////////////////////////////////////////////
    this.statusLabel.setAttribute(
      {
        "class": "global_label"
      }
    );
    this.statusLabel.setContent("状态");
    ////////////////////////////////////////////////////////////////////////////
    // 状态下拉框。
    ////////////////////////////////////////////////////////////////////////////
    this.statusSelect.setAttribute(
      {
        "class": "global_select",
        "style": "width: 100%;"
      }
    );
    this.statusSelect.setContent(`
      <option value = "">全部</option>
      <option value = "SUCCESS">成功</option>
      <option value = "INVALID_RTSP_URL">非法rtsp地址</option>
      <option value = "CONNECT_TIMEOUT">连接超时</option>
      <option value = "HOST_PORT_CLOSED">主机端口关闭</option>
      <option value = "GET_STATUS_FAIL">获取状态失败</option>
      <option value = "GET_AUTHORIZATION_FAIL">获取认证失败</option>
      <option value = "INVALID_STATUS_CODE">非法状态码</option>
      <option value = "PARSE_AUTH_FAIL">解析身份认证失败</option>
      <option value = "AUTH_FAIL">身份认证失败</option>
      <option value = "OTHER_ERROR">其他错误</option>
    `);
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
    // 状态标签。
    ////////////////////////////////////////////////////////////////////////////
    this.statusLabel.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // 状态下拉框。
    ////////////////////////////////////////////////////////////////////////////
    this.statusSelect.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // 表单列表。
    ////////////////////////////////////////////////////////////////////////////
    const list = new Array();
    list.push({"attr": {}, "required": false, "label_code": `${this.statusLabel.getCode()}`, "control_id": `${this.statusSelect.getId()}`, "control_code": `${this.statusSelect.getCode()}`});
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
    this.statusSelect.getObject().attr("disabled", "disabled");
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
      this.statusSelect.getObject().removeAttr("disabled");
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
    // 置默认选项。
    ////////////////////////////////////////////////////////////////////////////
    this.statusSelect.getObject().find("option:eq(0)").prop("selected", true);
  }
}
