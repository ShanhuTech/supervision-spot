"use strict";

class AccountInformation{
  /**
   * 构造函数
   *
   * @param ruleArray 规则数组
   */
  constructor(ruleArray) {
    ////////////////////////////////////////////////////////////////////////////
    // 管理员规则。
    ////////////////////////////////////////////////////////////////////////////
    this.adminRule = ruleArray[0];
  }

  /**
   * 生成代码
   */
  generateCode() {
    ////////////////////////////////////////////////////////////////////////////
    // 容器。
    ////////////////////////////////////////////////////////////////////////////
    this.container = new JSControl("div");
    ////////////////////////////////////////////////////////////////////////////
    // 配置表格。
    ////////////////////////////////////////////////////////////////////////////
    this.configTable = new JSControl("table");
    ////////////////////////////////////////////////////////////////////////////
    // 修改密码。
    ////////////////////////////////////////////////////////////////////////////
    this.modifyPassword = new ModifyPassword(this, "修改密码", 40);
    ////////////////////////////////////////////////////////////////////////////
    // 容器。
    ////////////////////////////////////////////////////////////////////////////
    this.container.setAttribute(
      {
        "class": "global_scroll global_scroll_dark container"
      }
    );
    ////////////////////////////////////////////////////////////////////////////
    // 配置表格。
    ////////////////////////////////////////////////////////////////////////////
    this.configTable.setAttribute(
      {
        "class": "global_table config_table"
      }
    );
    this.configTable.setContent(`
      <tbody>
        <tr>
          <td class = "title" colspan = "2">安全设置</td>
        </tr>
        <tr>
          <td>后台管理登陆密码</td>
          <td class = "operation"><span class = "modify_password">修改</span></td>
        </tr>
      </tbody>
    `);
    ////////////////////////////////////////////////////////////////////////////
    // 修改密码。
    ////////////////////////////////////////////////////////////////////////////
    this.modifyPassword.setClassSign("modify_password");
    ////////////////////////////////////////////////////////////////////////////
    // 容器。
    ////////////////////////////////////////////////////////////////////////////
    this.container.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // 配置表格。
    ////////////////////////////////////////////////////////////////////////////
    this.configTable.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // 修改密码。
    ////////////////////////////////////////////////////////////////////////////
    this.modifyPassword.generateCode();
  }

  /**
   * 初始化视图
   */
  initView() {
    ////////////////////////////////////////////////////////////////////////////
    // 页面添加容器。
    ////////////////////////////////////////////////////////////////////////////
    $("body").html(this.container.getCode());
    ////////////////////////////////////////////////////////////////////////////
    // 容器添加配置表格。
    ////////////////////////////////////////////////////////////////////////////
    this.container.getObject().append(this.configTable.getCode());
    ////////////////////////////////////////////////////////////////////////////
    // 容器添加修改密码。
    ////////////////////////////////////////////////////////////////////////////
    this.container.getObject().append(this.modifyPassword.getCode());
  }

  /**
   * 初始化事件
   */
  initEvent() {
    ////////////////////////////////////////////////////////////////////////////
    // 注册配置表格中修改密码的click事件。
    ////////////////////////////////////////////////////////////////////////////
    this.configTable.getObject().find("tbody").find("tr").find(".operation").find(".modify_password").off("click").on("click", null, this, this.configTableModifyPasswordClickEvent);
    ////////////////////////////////////////////////////////////////////////////
    // 初始化修改密码事件。
    ////////////////////////////////////////////////////////////////////////////
    this.modifyPassword.initEvent();
    ////////////////////////////////////////////////////////////////////////////
    // 注册修改密码确认按钮的click事件。
    ////////////////////////////////////////////////////////////////////////////
    this.modifyPassword.formList.confirmButton.getObject().off("click").on("click", null, this, this.modifyPasswordConfirmButtonClickEvent);
    ////////////////////////////////////////////////////////////////////////////
    // 注册修改密码取消按钮的click事件。
    ////////////////////////////////////////////////////////////////////////////
    this.modifyPassword.formList.cancelButton.getObject().off("click").on("click", null, this, this.modifyPasswordCancelButtonClickEvent);
  }

  /**
   * 配置表格中修改密码click事件
   * @param event 事件对象
   */
  configTableModifyPasswordClickEvent(event) {
    ////////////////////////////////////////////////////////////////////////////
    // 获取调用源。
    ////////////////////////////////////////////////////////////////////////////
    const source = event.data;
    source.modifyPassword.show();
  }

  /**
   * 修改密码确认按钮click事件
   * @param event 事件对象
   */
  modifyPasswordConfirmButtonClickEvent(event) {
    ////////////////////////////////////////////////////////////////////////////
    // 获取调用源。
    ////////////////////////////////////////////////////////////////////////////
    const source = event.data;
    ////////////////////////////////////////////////////////////////////////////
    // 校验数据之前，先要隐藏之前的提示。
    ////////////////////////////////////////////////////////////////////////////
    source.modifyPassword.formList.hideAllPrompt();
    ////////////////////////////////////////////////////////////////////////////
    // 参数检查数组。
    ////////////////////////////////////////////////////////////////////////////
    const parameterCheckArray = new Array();
    parameterCheckArray.push({"name": "old_password", "value": source.modifyPassword.oldPasswordTextField.getObject().val(), "id": source.modifyPassword.oldPasswordTextField.getId(), "allow_null": false, "custom_error_message": null});
    parameterCheckArray.push({"name": "new_password", "value": source.modifyPassword.newPasswordTextField.getObject().val(), "id": source.modifyPassword.newPasswordTextField.getId(), "allow_null": false, "custom_error_message": null});
    ////////////////////////////////////////////////////////////////////////////
    // 检查参数。
    ////////////////////////////////////////////////////////////////////////////
    for (let i = 0; i < parameterCheckArray.length; i++) {
      const parameterObj = parameterCheckArray[i];
      if (!Module.checkParameter(source.adminRule, "modifyAdminPasswordOfSelf", parameterObj, source, function error(source, errorMessage) {
        source.modifyPassword.formList.showPrompt(parameterObj.id, errorMessage);
      })) {
        return;
      }
    }
    ////////////////////////////////////////////////////////////////////////////
    // 参数数组。
    ////////////////////////////////////////////////////////////////////////////
    const parameterArray = new Array();
    parameterArray.push({"Account-Token": AccountSecurity.getItem("account_token")});
    for (let i = 0; i < parameterCheckArray.length; i++) {
      const parameter = parameterCheckArray[i];
      const param = {};
      param[parameter.name] = parameter.value;
      parameterArray.push(param);
    }
    ////////////////////////////////////////////////////////////////////////////
    // 修改管理员自身密码。
    ////////////////////////////////////////////////////////////////////////////
    Network.request(Network.RequestType.POST, Network.ResponseType.JSON, [{"Content-Type": "application/x-www-form-urlencoded"}],
      Configure.getServerUrl() + "/module/security.Admin/modifyAdminPasswordOfSelf", parameterArray, source,
      function loadStart(xhr, xhrEvent, source) {
        source.modifyPassword.formList.hideResultInfo(); // 隐藏结果信息。
        source.modifyPassword.frozenControl("modifyAdminPasswordOfSelf"); // 冻结控件。
      },
      function error(xhr, xhrEvent, source) {
        source.modifyPassword.formList.showResultInfo("error", "网络请求失败");
      },
      function timeout(xhr, xhrEvent, source) {
        source.modifyPassword.formList.showResultInfo("error", "网络请求超时");
      },
      function readyStateChange(xhr, xhrEvent, source) {
        if ((XMLHttpRequest.DONE == xhr.readyState) && (200 == xhr.status)) {
          source.modifyPassword.recoverControl("modifyAdminPasswordOfSelf"); // 恢复控件。
          //////////////////////////////////////////////////////////////////////
          // 响应结果。
          //////////////////////////////////////////////////////////////////////
          const responseResult = xhr.response;
          if (Toolkit.stringEqualsIgnoreCase("SUCCESS", responseResult.status)) {
            ////////////////////////////////////////////////////////////////////
            // 显示成功信息。
            ////////////////////////////////////////////////////////////////////
            source.modifyPassword.formList.showResultInfo("success", "修改成功");
            ////////////////////////////////////////////////////////////////////
            // 重置控件
            ////////////////////////////////////////////////////////////////////
            source.modifyPassword.resetControl();
          } else if (Toolkit.stringEqualsIgnoreCase("ERROR", responseResult.status)) {
            source.modifyPassword.formList.showResultInfo("error", responseResult.attach);
          } else {
            source.modifyPassword.formList.showResultInfo("error", "操作异常");
          }
        }
      }
    );
  }

  /**
   * 修改密码取消按钮click事件
   * @param event 事件对象
   */
  modifyPasswordCancelButtonClickEvent(event) {
    ////////////////////////////////////////////////////////////////////////////
    // 获取调用源。
    ////////////////////////////////////////////////////////////////////////////
    const source = event.data;
    source.modifyPassword.hide();
  }
}
