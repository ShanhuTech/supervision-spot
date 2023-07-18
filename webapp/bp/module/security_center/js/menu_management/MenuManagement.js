"use strict";

class MenuManagement {
  /**
   * 构造函数
   *
   * @param ruleArray 规则数组
   */
  constructor(ruleArray) {
    ////////////////////////////////////////////////////////////////////////////
    // 菜单规则。
    ////////////////////////////////////////////////////////////////////////////
    this.menuRule = ruleArray[0];
    ////////////////////////////////////////////////////////////////////////////
    // 菜单数组。
    ////////////////////////////////////////////////////////////////////////////
    this.menuArray = new Array();
    ////////////////////////////////////////////////////////////////////////////
    // 待删除数据的uuid。
    ////////////////////////////////////////////////////////////////////////////
    this.removeDataUuid = null;
    ////////////////////////////////////////////////////////////////////////////
    // 队列。
    ////////////////////////////////////////////////////////////////////////////
    this.queue = new Queue();
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
    // 工具栏。
    ////////////////////////////////////////////////////////////////////////////
    this.toolbar = new JSControl("div");
    ////////////////////////////////////////////////////////////////////////////
    // 添加一级菜单按钮。
    ////////////////////////////////////////////////////////////////////////////
    this.addMenuLevelOneButton = new JSControl("button");
    ////////////////////////////////////////////////////////////////////////////
    // 添加二级菜单按钮。
    ////////////////////////////////////////////////////////////////////////////
    this.addMenuLevelTwoButton = new JSControl("button");
    ////////////////////////////////////////////////////////////////////////////
    // 添加三级菜单按钮。
    ////////////////////////////////////////////////////////////////////////////
    this.addMenuLevelThreeButton = new JSControl("button");
    ////////////////////////////////////////////////////////////////////////////
    // 菜单表格。
    ////////////////////////////////////////////////////////////////////////////
    this.menuTable = new JSControl("table");
    ////////////////////////////////////////////////////////////////////////////
    // 等待遮蔽。
    ////////////////////////////////////////////////////////////////////////////
    this.waitMask = new WaitMask();
    ////////////////////////////////////////////////////////////////////////////
    // 添加菜单AM。
    ////////////////////////////////////////////////////////////////////////////
    this.addMenuAM = new MenuAM(this, "添加菜单", 40);
    ////////////////////////////////////////////////////////////////////////////
    // 修改菜单AM。
    ////////////////////////////////////////////////////////////////////////////
    this.modifyMenuAM = new MenuAM(this, "修改菜单", 40);
    ////////////////////////////////////////////////////////////////////////////
    // 删除确认窗。
    ////////////////////////////////////////////////////////////////////////////
    this.removeConfirmWindow = new ConfirmWindow("删除确认", 30);
    ////////////////////////////////////////////////////////////////////////////
    // 容器。
    ////////////////////////////////////////////////////////////////////////////
    this.container.setAttribute(
      {
        "class": "global_scroll global_scroll_dark container"
      }
    );
    ////////////////////////////////////////////////////////////////////////////
    // 工具栏。
    ////////////////////////////////////////////////////////////////////////////
    this.toolbar.setAttribute(
      {
        "class": "tool_bar"
      }
    );
    ////////////////////////////////////////////////////////////////////////////
    // 添加一级菜单按钮。
    ////////////////////////////////////////////////////////////////////////////
    this.addMenuLevelOneButton.setAttribute(
      {
        "class": "global_button_primary add_menu_level_one_button"
      }
    );
    this.addMenuLevelOneButton.setContent("添加一级菜单");
    ////////////////////////////////////////////////////////////////////////////
    // 添加二级菜单按钮。
    ////////////////////////////////////////////////////////////////////////////
    this.addMenuLevelTwoButton.setAttribute(
      {
        "class": "global_button_primary add_menu_level_two_button"
      }
    );
    this.addMenuLevelTwoButton.setContent("添加二级菜单");
    ////////////////////////////////////////////////////////////////////////////
    // 添加三级菜单按钮。
    ////////////////////////////////////////////////////////////////////////////
    this.addMenuLevelThreeButton.setAttribute(
      {
        "class": "global_button_primary add_menu_level_three_button"
      }
    );
    this.addMenuLevelThreeButton.setContent("添加三级菜单");
    ////////////////////////////////////////////////////////////////////////////
    // 菜单表格。
    ////////////////////////////////////////////////////////////////////////////
    this.menuTable.setAttribute(
      {
        "class": "global_table menu_table"
      }
    );
    const tableHead = `
      <tr>
        <td class = "name">名称</td>
        <td class = "text">显示文本</td>
        <td class = "description">描述</td>
        <td class = "operation">操作</td>
      </tr>
    `;
    this.menuTable.setContent(`
      <thead>${tableHead}</thead>
      <tbody>
        <tr>
          <td class = "rowspan" colspan = "4">尚无数据</td>
        </tr>
      </tbody>
    `);
    ////////////////////////////////////////////////////////////////////////////
    // 等待遮蔽。
    ////////////////////////////////////////////////////////////////////////////
    this.waitMask.setAttribute(
      {
        "class": "global_wait_mask"
      }
    );
    ////////////////////////////////////////////////////////////////////////////
    // 添加菜单AM。
    ////////////////////////////////////////////////////////////////////////////
    this.addMenuAM.setClassSign("add_menu_am");
    ////////////////////////////////////////////////////////////////////////////
    // 修改菜单AM。
    ////////////////////////////////////////////////////////////////////////////
    this.modifyMenuAM.setClassSign("modify_menu_am");
    ////////////////////////////////////////////////////////////////////////////
    // 删除确认窗。
    ////////////////////////////////////////////////////////////////////////////
    this.removeConfirmWindow.setAttribute(
      {
        "class": "remove_confirm_window"
      }
    );
    ////////////////////////////////////////////////////////////////////////////
    // 容器。
    ////////////////////////////////////////////////////////////////////////////
    this.container.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // 工具栏。
    ////////////////////////////////////////////////////////////////////////////
    this.toolbar.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // 添加一级菜单按钮。
    ////////////////////////////////////////////////////////////////////////////
    this.addMenuLevelOneButton.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // 添加二级菜单按钮。
    ////////////////////////////////////////////////////////////////////////////
    this.addMenuLevelTwoButton.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // 添加三级菜单按钮。
    ////////////////////////////////////////////////////////////////////////////
    this.addMenuLevelThreeButton.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // 菜单表格。
    ////////////////////////////////////////////////////////////////////////////
    this.menuTable.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // 等待遮蔽。
    ////////////////////////////////////////////////////////////////////////////
    this.waitMask.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // 添加菜单AM。
    ////////////////////////////////////////////////////////////////////////////
    this.addMenuAM.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // 修改菜单AM。
    ////////////////////////////////////////////////////////////////////////////
    this.modifyMenuAM.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // 删除确认窗。
    ////////////////////////////////////////////////////////////////////////////
    this.removeConfirmWindow.generateCode();
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
    // 容器添加工具栏。
    ////////////////////////////////////////////////////////////////////////////
    this.container.getObject().append(this.toolbar.getCode());
    ////////////////////////////////////////////////////////////////////////////
    // 工具栏添加添加一级菜单按钮。
    ////////////////////////////////////////////////////////////////////////////
    this.toolbar.getObject().append(this.addMenuLevelOneButton.getCode());
    ////////////////////////////////////////////////////////////////////////////
    // 工具栏添加添加二级菜单按钮。
    ////////////////////////////////////////////////////////////////////////////
    this.toolbar.getObject().append(this.addMenuLevelTwoButton.getCode());
    ////////////////////////////////////////////////////////////////////////////
    // 工具栏添加添加三级菜单按钮。
    ////////////////////////////////////////////////////////////////////////////
    this.toolbar.getObject().append(this.addMenuLevelThreeButton.getCode());
    ////////////////////////////////////////////////////////////////////////////
    // 容器添加菜单表格。
    ////////////////////////////////////////////////////////////////////////////
    this.container.getObject().append(this.menuTable.getCode());
    ////////////////////////////////////////////////////////////////////////////
    // 容器添加等待遮蔽。
    ////////////////////////////////////////////////////////////////////////////
    this.container.getObject().append(this.waitMask.getCode());
    ////////////////////////////////////////////////////////////////////////////
    // 容器添加添加一级菜单AM。
    ////////////////////////////////////////////////////////////////////////////
    this.container.getObject().append(this.addMenuAM.getCode());
    ////////////////////////////////////////////////////////////////////////////
    // 容器添加修改菜单AM。
    ////////////////////////////////////////////////////////////////////////////
    this.container.getObject().append(this.modifyMenuAM.getCode());
    ////////////////////////////////////////////////////////////////////////////
    // 容器添加删除确认窗。
    ////////////////////////////////////////////////////////////////////////////
    this.container.getObject().append(this.removeConfirmWindow.getCode());
  }

  /**
   * 初始化事件
   */
  initEvent() {
    ////////////////////////////////////////////////////////////////////////////
    // 注册添加一级菜单按钮的click事件。
    ////////////////////////////////////////////////////////////////////////////
    this.addMenuLevelOneButton.getObject().off("click").on("click", null, this, this.addMenuLevelOneButtonClickEvent);
    ////////////////////////////////////////////////////////////////////////////
    // 注册添加二级菜单按钮的click事件。
    ////////////////////////////////////////////////////////////////////////////
    this.addMenuLevelTwoButton.getObject().off("click").on("click", null, this, this.addMenuLevelTwoButtonClickEvent);
    ////////////////////////////////////////////////////////////////////////////
    // 注册添加三级菜单按钮的click事件。
    ////////////////////////////////////////////////////////////////////////////
    this.addMenuLevelThreeButton.getObject().off("click").on("click", null, this, this.addMenuLevelThreeButtonClickEvent);
    ////////////////////////////////////////////////////////////////////////////
    // 初始化添加菜单AM事件。
    ////////////////////////////////////////////////////////////////////////////
    this.addMenuAM.initEvent();
    ////////////////////////////////////////////////////////////////////////////
    // 初始化修改菜单AM事件。
    ////////////////////////////////////////////////////////////////////////////
    this.modifyMenuAM.initEvent();
    ////////////////////////////////////////////////////////////////////////////
    // 初始化删除确认窗事件。
    ////////////////////////////////////////////////////////////////////////////
    this.removeConfirmWindow.initEvent();
    ////////////////////////////////////////////////////////////////////////////
    // 注册添加菜单AM确认按钮的click事件。
    ////////////////////////////////////////////////////////////////////////////
    this.addMenuAM.formList.confirmButton.getObject().off("click").on("click", null, this, this.addMenuAMConfirmButtonClickEvent);
    ////////////////////////////////////////////////////////////////////////////
    // 注册添加菜单AM取消按钮的click事件。
    ////////////////////////////////////////////////////////////////////////////
    this.addMenuAM.formList.cancelButton.getObject().off("click").on("click", null, this, this.addMenuAMCancelButtonClickEvent);
    ////////////////////////////////////////////////////////////////////////////
    // 注册添加菜单AM一级菜单下拉框的change事件。
    ////////////////////////////////////////////////////////////////////////////
    this.addMenuAM.levelOneSelect.getObject().off("change").on("change", null, this, this.addMenuAMLevelOneSelectChangeEvent);
    ////////////////////////////////////////////////////////////////////////////
    // 注册修改菜单AM确认按钮的click事件。
    ////////////////////////////////////////////////////////////////////////////
    this.modifyMenuAM.formList.confirmButton.getObject().off("click").on("click", null, this, this.modifyMenuAMConfirmButtonClickEvent);
    ////////////////////////////////////////////////////////////////////////////
    // 注册修改菜单AM取消按钮的click事件。
    ////////////////////////////////////////////////////////////////////////////
    this.modifyMenuAM.formList.cancelButton.getObject().off("click").on("click", null, this, this.modifyMenuAMCancelButtonClickEvent);
    ////////////////////////////////////////////////////////////////////////////
    // 注册修改菜单AM一级菜单下拉框的change事件。
    ////////////////////////////////////////////////////////////////////////////
    this.modifyMenuAM.levelOneSelect.getObject().off("change").on("change", null, this, this.modifyMenuLevelOneSelectChangeEvent);
    ////////////////////////////////////////////////////////////////////////////
    // 注册删除确认窗确认按钮的click事件。
    ////////////////////////////////////////////////////////////////////////////
    this.removeConfirmWindow.confirmButton.getObject().off("click").on("click", null, this, this.removeConfirmWindowConfirmButtonClickEvent);
  }

  /**
   * 添加一级菜单按钮click事件
   * @param event 事件对象
   */
  addMenuLevelOneButtonClickEvent(event) {
    ////////////////////////////////////////////////////////////////////////////
    // 获取调用源。
    ////////////////////////////////////////////////////////////////////////////
    const source = event.data;
    source.addMenuAM.show(1);
  }

  /**
   * 添加二级菜单按钮click事件
   * @param event 事件对象
   */
  addMenuLevelTwoButtonClickEvent(event) {
    ////////////////////////////////////////////////////////////////////////////
    // 获取调用源。
    ////////////////////////////////////////////////////////////////////////////
    const source = event.data;
    source.addMenuAM.show(2);
  }

  /**
   * 添加三级菜单按钮click事件
   * @param event 事件对象
   */
  addMenuLevelThreeButtonClickEvent(event) {
    ////////////////////////////////////////////////////////////////////////////
    // 获取调用源。
    ////////////////////////////////////////////////////////////////////////////
    const source = event.data;
    source.addMenuAM.show(3);
  }

  /**
   * 添加菜单AM确认按钮click事件
   * @param event 事件对象
   */
  addMenuAMConfirmButtonClickEvent(event) {
    ////////////////////////////////////////////////////////////////////////////
    // 获取调用源。
    ////////////////////////////////////////////////////////////////////////////
    const source = event.data;
    ////////////////////////////////////////////////////////////////////////////
    // 校验数据之前，先要隐藏之前的提示。
    ////////////////////////////////////////////////////////////////////////////
    source.addMenuAM.formList.hideAllPrompt();
    ////////////////////////////////////////////////////////////////////////////
    // 参数检查数组。
    ////////////////////////////////////////////////////////////////////////////
    const parameterCheckArray = new Array();
    if (1 == source.addMenuAM.menuLevel) {
      //////////////////////////////////////////////////////////////////////////
      // 添加一级菜单。
      //////////////////////////////////////////////////////////////////////////
      parameterCheckArray.push({"name": "parent_uuid", "value": "0", "id": source.addMenuAM.levelOneSelect.getId(), "allow_null": false, "custom_error_message": null});
      parameterCheckArray.push({"name": "name", "value": source.addMenuAM.nameTextField.getObject().val(), "id": source.addMenuAM.nameTextField.getId(), "allow_null": false, "custom_error_message": null});
      parameterCheckArray.push({"name": "text", "value": source.addMenuAM.textTextField.getObject().val(), "id": source.addMenuAM.textTextField.getId(), "allow_null": false, "custom_error_message": null});
      parameterCheckArray.push({"name": "order", "value": source.addMenuAM.orderTextField.getObject().val(), "id": source.addMenuAM.orderTextField.getId(), "allow_null": false, "custom_error_message": null});
      if (0 < source.addMenuAM.descriptionTextField.getObject().val().length) {
        parameterCheckArray.push({"name": "description", "value": source.addMenuAM.descriptionTextField.getObject().val(), "id": source.addMenuAM.descriptionTextField.getId(), "allow_null": true, "custom_error_message": null});
      }
    } else if (2 == source.addMenuAM.menuLevel) {
      //////////////////////////////////////////////////////////////////////////
      // 添加二级菜单。
      //////////////////////////////////////////////////////////////////////////
      parameterCheckArray.push({"name": "parent_uuid", "value": source.addMenuAM.levelOneSelect.getObject().val(), "id": source.addMenuAM.levelOneSelect.getId(), "allow_null": false, "custom_error_message": null});
      parameterCheckArray.push({"name": "name", "value": source.addMenuAM.nameTextField.getObject().val(), "id": source.addMenuAM.nameTextField.getId(), "allow_null": false, "custom_error_message": null});
      parameterCheckArray.push({"name": "text", "value": source.addMenuAM.textTextField.getObject().val(), "id": source.addMenuAM.textTextField.getId(), "allow_null": false, "custom_error_message": null});
      parameterCheckArray.push({"name": "order", "value": source.addMenuAM.orderTextField.getObject().val(), "id": source.addMenuAM.orderTextField.getId(), "allow_null": false, "custom_error_message": null});
      if (0 < source.addMenuAM.descriptionTextField.getObject().val().length) {
        parameterCheckArray.push({"name": "description", "value": source.addMenuAM.descriptionTextField.getObject().val(), "id": source.addMenuAM.descriptionTextField.getId(), "allow_null": true, "custom_error_message": null});
      }
    } else if (3 == source.addMenuAM.menuLevel) {
      //////////////////////////////////////////////////////////////////////////
      // 添加三级菜单。
      //////////////////////////////////////////////////////////////////////////
      parameterCheckArray.push({"name": "parent_uuid", "value": source.addMenuAM.levelTwoSelect.getObject().val(), "id": source.addMenuAM.levelTwoSelect.getId(), "allow_null": false, "custom_error_message": null});
      parameterCheckArray.push({"name": "name", "value": source.addMenuAM.nameTextField.getObject().val(), "id": source.addMenuAM.nameTextField.getId(), "allow_null": false, "custom_error_message": null});
      parameterCheckArray.push({"name": "text", "value": source.addMenuAM.textTextField.getObject().val(), "id": source.addMenuAM.textTextField.getId(), "allow_null": false, "custom_error_message": null});
      parameterCheckArray.push({"name": "link", "value": source.addMenuAM.linkTextField.getObject().val(), "id": source.addMenuAM.linkTextField.getId(), "allow_null": false, "custom_error_message": null});
      parameterCheckArray.push({"name": "order", "value": source.addMenuAM.orderTextField.getObject().val(), "id": source.addMenuAM.orderTextField.getId(), "allow_null": false, "custom_error_message": null});
      if (0 < source.addMenuAM.descriptionTextField.getObject().val().length) {
        parameterCheckArray.push({"name": "description", "value": source.addMenuAM.descriptionTextField.getObject().val(), "id": source.addMenuAM.descriptionTextField.getId(), "allow_null": true, "custom_error_message": null});
      }
    }
    ////////////////////////////////////////////////////////////////////////////
    // 检查参数。
    ////////////////////////////////////////////////////////////////////////////
    for (let i = 0; i < parameterCheckArray.length; i++) {
      const parameterObj = parameterCheckArray[i];
      if (!Module.checkParameter(source.menuRule, "addMenu", parameterObj, source, function error(source, errorMessage) {
        source.addMenuAM.formList.showPrompt(parameterObj.id, errorMessage);
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
    // 添加菜单。
    ////////////////////////////////////////////////////////////////////////////
    Network.request(Network.RequestType.POST, Network.ResponseType.JSON, [{"Content-Type": "application/x-www-form-urlencoded"}],
      Configure.getServerUrl() + "/module/security.Menu/addMenu", parameterArray, source,
      function loadStart(xhr, xhrEvent, source) {
        source.addMenuAM.formList.hideResultInfo(); // 隐藏结果信息。
        source.addMenuAM.frozenControl("addMenu"); // 冻结控件。
      },
      function error(xhr, xhrEvent, source) {
        source.addMenuAM.formList.showResultInfo("error", "网络请求失败");
      },
      function timeout(xhr, xhrEvent, source) {
        source.addMenuAM.formList.showResultInfo("error", "网络请求超时");
      },
      function readyStateChange(xhr, xhrEvent, source) {
        if ((XMLHttpRequest.DONE == xhr.readyState) && (200 == xhr.status)) {
          source.addMenuAM.recoverControl("addMenu"); // 恢复控件。
          //////////////////////////////////////////////////////////////////////
          // 响应结果。
          //////////////////////////////////////////////////////////////////////
          const responseResult = xhr.response;
          if (Toolkit.stringEqualsIgnoreCase("SUCCESS", responseResult.status)) {
            ////////////////////////////////////////////////////////////////////
            // 显示成功信息。
            ////////////////////////////////////////////////////////////////////
            source.addMenuAM.formList.showResultInfo("success", "添加成功");
            ////////////////////////////////////////////////////////////////////
            // 重置控件。
            ////////////////////////////////////////////////////////////////////
            source.addMenuAM.resetControl();
            ////////////////////////////////////////////////////////////////////
            // 获取菜单。
            ////////////////////////////////////////////////////////////////////
            source.getMenu();
          } else if (Toolkit.stringEqualsIgnoreCase("ERROR", responseResult.status)) {
            source.addMenuAM.formList.showResultInfo("error", responseResult.attach);
          } else {
            source.addMenuAM.formList.showResultInfo("error", "操作异常");
          }
        }
      }
    );
  }

  /**
   * 添加菜单AM取消按钮click事件
   * @param event 事件对象
   */
  addMenuAMCancelButtonClickEvent(event) {
    ////////////////////////////////////////////////////////////////////////////
    // 获取调用源。
    ////////////////////////////////////////////////////////////////////////////
    const source = event.data;
    source.addMenuAM.hide();
  }

  /**
   * 添加菜单AM一级下拉框确认change事件
   * @param event 事件对象
   */
  addMenuAMLevelOneSelectChangeEvent(event) {
    ////////////////////////////////////////////////////////////////////////////
    // 获取调用源。
    ////////////////////////////////////////////////////////////////////////////
    const source = event.data;
    ////////////////////////////////////////////////////////////////////////////
    // 清空二级菜单下拉框的数据并设置状态。
    ////////////////////////////////////////////////////////////////////////////
    source.addMenuAM.levelTwoSelect.getObject().empty();
    source.addMenuAM.levelTwoSelect.getObject().attr("disabled", "disabled");
    ////////////////////////////////////////////////////////////////////////////
    // 填充二级菜单数据。
    ////////////////////////////////////////////////////////////////////////////
    {
      const selectVal = source.addMenuAM.levelOneSelect.getObject().val();
      for (let i = 0; i < source.menuArray.length; i++) {
        const m = source.menuArray[i];
        if (selectVal == m.parent_uuid) {
          source.addMenuAM.levelTwoSelect.getObject().append(`<option value = "${m.uuid}">${m.text}</option>`);
        }
      }
    }
    ////////////////////////////////////////////////////////////////////////////
    // 根据addMenuAM二级菜单下拉框的数据有无设置状态。
    ////////////////////////////////////////////////////////////////////////////
    if (0 < source.addMenuAM.levelTwoSelect.getObject().children("option").length) {
      source.addMenuAM.levelTwoSelect.getObject().removeAttr("disabled");
      source.addMenuAM.levelTwoSelect.getObject().trigger("change");
    }
  }

  /**
   * 修改菜单AM确认按钮click事件
   * @param event 事件对象
   */
  modifyMenuAMConfirmButtonClickEvent(event) {
    ////////////////////////////////////////////////////////////////////////////
    // 获取调用源。
    ////////////////////////////////////////////////////////////////////////////
    const source = event.data;
    ////////////////////////////////////////////////////////////////////////////
    // 校验数据之前，先要隐藏之前的提示。
    ////////////////////////////////////////////////////////////////////////////
    source.modifyMenuAM.formList.hideAllPrompt();
    ////////////////////////////////////////////////////////////////////////////
    // 参数检查数组。
    ////////////////////////////////////////////////////////////////////////////
    const parameterCheckArray = new Array();
    if (1 == source.modifyMenuAM.menuLevel) {
      //////////////////////////////////////////////////////////////////////////
      // 修改一级菜单。
      //////////////////////////////////////////////////////////////////////////
      parameterCheckArray.push({"name": "uuid", "value": source.modifyMenuAM.uuidTextField.getObject().val(), "id": source.modifyMenuAM.uuidTextField.getId(), "allow_null": false});
      parameterCheckArray.push({"name": "parent_uuid", "value": "0", "id": source.modifyMenuAM.levelOneSelect.getId(), "allow_null": false});
      parameterCheckArray.push({"name": "name", "value": source.modifyMenuAM.nameTextField.getObject().val(), "id": source.modifyMenuAM.nameTextField.getId(), "allow_null": false});
      parameterCheckArray.push({"name": "text", "value": source.modifyMenuAM.textTextField.getObject().val(), "id": source.modifyMenuAM.textTextField.getId(), "allow_null": false});
      parameterCheckArray.push({"name": "order", "value": source.modifyMenuAM.orderTextField.getObject().val(), "id": source.modifyMenuAM.orderTextField.getId(), "allow_null": false});
      if (0 < source.modifyMenuAM.descriptionTextField.getObject().val().length) {
        parameterCheckArray.push({"name": "description", "value": source.modifyMenuAM.descriptionTextField.getObject().val(), "id": source.modifyMenuAM.descriptionTextField.getId(), "allow_null": true});
      } else {
        parameterCheckArray.push({"name": "description_null", "value": "null", "id": source.modifyMenuAM.descriptionTextField.getId(), "allow_null": true});
      }
    } else if (2 == source.modifyMenuAM.menuLevel) {
      //////////////////////////////////////////////////////////////////////////
      // 修改二级菜单。
      //////////////////////////////////////////////////////////////////////////
      parameterCheckArray.push({"name": "uuid", "value": source.modifyMenuAM.uuidTextField.getObject().val(), "id": source.modifyMenuAM.uuidTextField.getId(), "allow_null": false});
      parameterCheckArray.push({"name": "parent_uuid", "value": source.modifyMenuAM.levelOneSelect.getObject().val(), "id": source.modifyMenuAM.levelOneSelect.getId(), "allow_null": false});
      parameterCheckArray.push({"name": "name", "value": source.modifyMenuAM.nameTextField.getObject().val(), "id": source.modifyMenuAM.nameTextField.getId(), "allow_null": false});
      parameterCheckArray.push({"name": "text", "value": source.modifyMenuAM.textTextField.getObject().val(), "id": source.modifyMenuAM.textTextField.getId(), "allow_null": false});
      parameterCheckArray.push({"name": "order", "value": source.modifyMenuAM.orderTextField.getObject().val(), "id": source.modifyMenuAM.orderTextField.getId(), "allow_null": false});
      if (0 < source.modifyMenuAM.descriptionTextField.getObject().val().length) {
        parameterCheckArray.push({"name": "description", "value": source.modifyMenuAM.descriptionTextField.getObject().val(), "id": source.modifyMenuAM.descriptionTextField.getId(), "allow_null": true});
      } else {
        parameterCheckArray.push({"name": "description_null", "value": "null", "id": source.modifyMenuAM.descriptionTextField.getId(), "allow_null": true});
      }
    } else if (3 == source.modifyMenuAM.menuLevel) {
      //////////////////////////////////////////////////////////////////////////
      // 修改三级菜单。
      //////////////////////////////////////////////////////////////////////////
      parameterCheckArray.push({"name": "uuid", "value": source.modifyMenuAM.uuidTextField.getObject().val(), "id": source.modifyMenuAM.uuidTextField.getId(), "allow_null": false});
      parameterCheckArray.push({"name": "parent_uuid", "value": source.modifyMenuAM.levelTwoSelect.getObject().val(), "id": source.modifyMenuAM.levelTwoSelect.getId(), "allow_null": false});
      parameterCheckArray.push({"name": "name", "value": source.modifyMenuAM.nameTextField.getObject().val(), "id": source.modifyMenuAM.nameTextField.getId(), "allow_null": false});
      parameterCheckArray.push({"name": "text", "value": source.modifyMenuAM.textTextField.getObject().val(), "id": source.modifyMenuAM.textTextField.getId(), "allow_null": false});
      parameterCheckArray.push({"name": "link", "value": source.modifyMenuAM.linkTextField.getObject().val(), "id": source.modifyMenuAM.linkTextField.getId(), "allow_null": false});
      parameterCheckArray.push({"name": "order", "value": source.modifyMenuAM.orderTextField.getObject().val(), "id": source.modifyMenuAM.orderTextField.getId(), "allow_null": false});
      if (0 < source.modifyMenuAM.descriptionTextField.getObject().val().length) {
        parameterCheckArray.push({"name": "description", "value": source.modifyMenuAM.descriptionTextField.getObject().val(), "id": source.modifyMenuAM.descriptionTextField.getId(), "allow_null": true});
      } else {
        parameterCheckArray.push({"name": "description_null", "value": "null", "id": source.modifyMenuAM.descriptionTextField.getId(), "allow_null": true});
      }
    }
    ////////////////////////////////////////////////////////////////////////////
    // 检查参数。
    ////////////////////////////////////////////////////////////////////////////
    for (let i = 0; i < parameterCheckArray.length; i++) {
      const parameterObj = parameterCheckArray[i];
      if (!Module.checkParameter(source.menuRule, "modifyMenu", parameterObj, source, function error(source, errorMessage) {
        source.modifyMenuAM.formList.showPrompt(parameterObj.id, errorMessage);
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
    // 修改菜单。
    ////////////////////////////////////////////////////////////////////////////
    Network.request(Network.RequestType.POST, Network.ResponseType.JSON, [{"Content-Type": "application/x-www-form-urlencoded"}],
      Configure.getServerUrl() + "/module/security.Menu/modifyMenu", parameterArray, source,
      function loadStart(xhr, xhrEvent, source) {
        source.modifyMenuAM.formList.hideResultInfo(); // 隐藏结果信息。
        source.modifyMenuAM.frozenControl("modifyMenu"); // 冻结控件。
      },
      function error(xhr, xhrEvent, source) {
        source.modifyMenuAM.formList.showResultInfo("error", "网络请求失败");
      },
      function timeout(xhr, xhrEvent, source) {
        source.modifyMenuAM.formList.showResultInfo("error", "网络请求超时");
      },
      function readyStateChange(xhr, xhrEvent, source) {
        if ((XMLHttpRequest.DONE == xhr.readyState) && (200 == xhr.status)) {
          source.modifyMenuAM.recoverControl("modifyMenu"); // 恢复控件。
          //////////////////////////////////////////////////////////////////////
          // 响应结果。
          //////////////////////////////////////////////////////////////////////
          const responseResult = xhr.response;
          if (Toolkit.stringEqualsIgnoreCase("SUCCESS", responseResult.status)) {
            ////////////////////////////////////////////////////////////////////
            // 显示成功信息。
            ////////////////////////////////////////////////////////////////////
            source.modifyMenuAM.formList.showResultInfo("success", "修改成功");
            ////////////////////////////////////////////////////////////////////
            // 重置控件（修改功能在成功修改之后，不需要重置控件内容，这里不用但
            // 做保留）。
            ////////////////////////////////////////////////////////////////////
            // source.modifyMenuAM.resetControl();
            ////////////////////////////////////////////////////////////////////
            // 获取菜单。
            ////////////////////////////////////////////////////////////////////
            source.getMenu();
          } else if (Toolkit.stringEqualsIgnoreCase("ERROR", responseResult.status)) {
            source.modifyMenuAM.formList.showResultInfo("error", responseResult.attach);
          } else {
            source.modifyMenuAM.formList.showResultInfo("error", "操作异常");
          }
        }
      }
    );
  }

  /**
   * 修改菜单AM取消按钮click事件
   * @param event 事件对象
   */
  modifyMenuAMCancelButtonClickEvent(event) {
    ////////////////////////////////////////////////////////////////////////////
    // 获取调用源。
    ////////////////////////////////////////////////////////////////////////////
    const source = event.data;
    source.modifyMenuAM.hide();
  }

  /**
   * 修改菜单AM一级菜单下拉框change事件
   * @param event 事件对象
   */
  modifyMenuLevelOneSelectChangeEvent(event) {
    ////////////////////////////////////////////////////////////////////////////
    // 获取调用源。
    ////////////////////////////////////////////////////////////////////////////
    const source = event.data;
    ////////////////////////////////////////////////////////////////////////////
    // 清空二级菜单下拉框的数据并设置状态。
    ////////////////////////////////////////////////////////////////////////////
    source.modifyMenuAM.levelTwoSelect.getObject().empty();
    source.modifyMenuAM.levelTwoSelect.getObject().attr("disabled", "disabled");
    ////////////////////////////////////////////////////////////////////////////
    // 遍历子菜单数组添加至二级菜单下拉框。
    ////////////////////////////////////////////////////////////////////////////
    {
      const selectVal = source.modifyMenuAM.levelOneSelect.getObject().val();
      for (let i = 0; i < source.menuArray.length; i++) {
        const m = source.menuArray[i];
        if (selectVal == m.parent_uuid) {
          source.modifyMenuAM.levelTwoSelect.getObject().append(`<option value = "${m.uuid}">${m.name}</option>`);
        }
      }
      //////////////////////////////////////////////////////////////////////////
      // 根据modifyMenuAM二级菜单下拉框的数据有无设置状态。
      //////////////////////////////////////////////////////////////////////////
      if (0 < source.modifyMenuAM.levelTwoSelect.getObject().children("option").length) {
        source.modifyMenuAM.levelTwoSelect.getObject().removeAttr("disabled");
      }
    }
  }

  /**
   * 菜单表格修改按钮click事件
   * @param event 事件对象
   */
  menuTableModifyButtonClickEvent(event) {
    ////////////////////////////////////////////////////////////////////////////
    // 获取调用源。
    ////////////////////////////////////////////////////////////////////////////
    const source = event.data;
    ////////////////////////////////////////////////////////////////////////////
    // 获取待修改数据的uuid。
    ////////////////////////////////////////////////////////////////////////////
    const uuid = $(this).parent().attr("data-uuid");
    ////////////////////////////////////////////////////////////////////////////
    // 从菜单数组中获取该uuid对应的数据。
    ////////////////////////////////////////////////////////////////////////////
    let obj = null;
    for (let i = 0; i < source.menuArray.length; i++) {
      if (uuid == source.menuArray[i].uuid) {
        obj = source.menuArray[i];
        break;
      }
    }
    ////////////////////////////////////////////////////////////////////////////
    // 设置当前数据至修改界面。
    ////////////////////////////////////////////////////////////////////////////
    if (null != obj) {
      //////////////////////////////////////////////////////////////////////////
      // 加载uuid。
      //////////////////////////////////////////////////////////////////////////
      source.modifyMenuAM.uuidTextField.getObject().val(obj.uuid);
      //////////////////////////////////////////////////////////////////////////
      // 加载名称。
      //////////////////////////////////////////////////////////////////////////
      source.modifyMenuAM.nameTextField.getObject().val(obj.name);
      //////////////////////////////////////////////////////////////////////////
      // 加载文本。
      //////////////////////////////////////////////////////////////////////////
      source.modifyMenuAM.textTextField.getObject().val(obj.text);
      //////////////////////////////////////////////////////////////////////////
      // 加载排序。
      //////////////////////////////////////////////////////////////////////////
      source.modifyMenuAM.orderTextField.getObject().val(obj.order);
      //////////////////////////////////////////////////////////////////////////
      // 加载描述。
      //////////////////////////////////////////////////////////////////////////
      if (Toolkit.isJSONObjectExistKey(obj, "description")) {
        source.modifyMenuAM.descriptionTextField.getObject().val(obj.description);
      }
      if (1 == obj.level) {
        ////////////////////////////////////////////////////////////////////////
        // 显示修改一级菜单。
        ////////////////////////////////////////////////////////////////////////
        source.modifyMenuAM.show(1);
      } else if (2 == obj.level) {
        ////////////////////////////////////////////////////////////////////////
        // 加载一级菜单下拉框并设置状态。
        ////////////////////////////////////////////////////////////////////////
        source.modifyMenuAM.levelOneSelect.getObject().empty();
        source.modifyMenuAM.levelOneSelect.getObject().attr("disabled", "disabled");
        ////////////////////////////////////////////////////////////////////////
        // 加载一级菜单下拉框并设置选中项。
        ////////////////////////////////////////////////////////////////////////
        for (let i = 0; i < source.menuArray.length; i++) {
          const m = source.menuArray[i];
          if (1 == m.level) {
            let selectedCode = "";
            if (obj.parent_uuid == m.uuid) {
              selectedCode = ` selected = "selected"`;
            }
            source.modifyMenuAM.levelOneSelect.getObject().append(`<option value = "${m.uuid}"${selectedCode}>${m.name}</option>`);
          }
        }
        //////////////////////////////////////////////////////////////////////
        // 根据modifyMenuAM一级菜单下拉框的数据有无设置状态。
        //////////////////////////////////////////////////////////////////////
        if (0 < source.modifyMenuAM.levelOneSelect.getObject().children("option").length) {
          source.modifyMenuAM.levelOneSelect.getObject().removeAttr("disabled");
        }
        ////////////////////////////////////////////////////////////////////////
        // 显示修改二级菜单。
        ////////////////////////////////////////////////////////////////////////
        source.modifyMenuAM.show(2);
      } else if (3 == obj.level) {
        ////////////////////////////////////////////////////////////////////////
        // 加载链接。
        ////////////////////////////////////////////////////////////////////////
        source.modifyMenuAM.linkTextField.getObject().val(obj.link);
        ////////////////////////////////////////////////////////////////////////
        // 根据三级菜单的parent_uuid找到对应uuid的二级菜单。
        ////////////////////////////////////////////////////////////////////////
        let levelTwoMenuUuid = null;
        let levelTwoMenuParentUuid = null;
        for (let i = 0; i < source.menuArray.length; i++) {
          if (obj.parent_uuid == source.menuArray[i].uuid) {
            levelTwoMenuUuid = source.menuArray[i].uuid;
            levelTwoMenuParentUuid = source.menuArray[i].parent_uuid;
            break;
          }
        }
        if ((null == levelTwoMenuUuid) || (null == levelTwoMenuParentUuid)) {
          return;
        }
        ////////////////////////////////////////////////////////////////////////
        // 根据二级菜单的parent_uuid找到对应uuid的一级菜单。
        ////////////////////////////////////////////////////////////////////////
        let levelOneMenuUuid = null;
        for (let i = 0; i < source.menuArray.length; i++) {
          if (levelTwoMenuParentUuid == source.menuArray[i].uuid) {
            levelOneMenuUuid = source.menuArray[i].uuid;
            break;
          }
        }
        if (null == levelOneMenuUuid) {
          return;
        }
        ////////////////////////////////////////////////////////////////////////
        // 清空一级菜单下拉框的数据并设置状态。
        ////////////////////////////////////////////////////////////////////////
        source.modifyMenuAM.levelOneSelect.getObject().empty();
        source.modifyMenuAM.levelOneSelect.getObject().attr("disabled", "disabled");
        ////////////////////////////////////////////////////////////////////////
        // 加载一级菜单下拉框并设置选中项。
        ////////////////////////////////////////////////////////////////////////
        for (let i = 0; i < source.menuArray.length; i++) {
          const m = source.menuArray[i];
          if (1 == m.level) {
            let selectedCode = "";
            if (levelOneMenuUuid == m.uuid) {
              selectedCode = ` selected = "selected"`;
            }
            source.modifyMenuAM.levelOneSelect.getObject().append(`<option value = "${m.uuid}"${selectedCode}>${m.name}</option>`);
          }
        }
        ////////////////////////////////////////////////////////////////////////
        // 根据modifyMenuAM一级菜单下拉框的数据有无设置状态。
        ////////////////////////////////////////////////////////////////////////
        if (0 < source.modifyMenuAM.levelOneSelect.getObject().children("option").length) {
          source.modifyMenuAM.levelOneSelect.getObject().removeAttr("disabled");
        }
        ////////////////////////////////////////////////////////////////////////
        // 清空二级菜单下拉框的数据并设置状态。
        ////////////////////////////////////////////////////////////////////////
        source.modifyMenuAM.levelTwoSelect.getObject().empty();
        source.modifyMenuAM.levelTwoSelect.getObject().attr("disabled", "disabled");
        ////////////////////////////////////////////////////////////////////////
        // 加载二级菜单下拉框并设置选中项。
        ////////////////////////////////////////////////////////////////////////
        for (let i = 0; i < source.menuArray.length; i++) {
          const m = source.menuArray[i];
          if (levelTwoMenuParentUuid == m.parent_uuid) {
            let selectedCode = "";
            if (levelTwoMenuUuid == m.uuid) {
              selectedCode = ` selected = "selected"`;
            }
            source.modifyMenuAM.levelTwoSelect.getObject().append(`<option value = "${m.uuid}"${selectedCode}>${m.name}</option>`);
          }
        }
        ////////////////////////////////////////////////////////////////////////
        // 根据modifyMenuAM二级菜单下拉框的数据有无设置状态。
        ////////////////////////////////////////////////////////////////////////
        if (0 < source.modifyMenuAM.levelTwoSelect.getObject().children("option").length) {
          source.modifyMenuAM.levelTwoSelect.getObject().removeAttr("disabled");
        }
        ////////////////////////////////////////////////////////////////////////
        // 显示修改三级菜单。
        ////////////////////////////////////////////////////////////////////////
        source.modifyMenuAM.show(3);
      }
    }
  }

  /**
   * 菜单表格删除按钮click事件
   * @param event 事件对象
   */
  menuTableRemoveButtonClickEvent(event) {
    ////////////////////////////////////////////////////////////////////////////
    // 获取调用源。
    ////////////////////////////////////////////////////////////////////////////
    const source = event.data;
    source.removeDataUuid = $(this).parent().attr("data-uuid");
    source.removeConfirmWindow.show("确认要删除菜单吗？");
  }

  /**
   * 删除确认窗确认按钮click事件
   * @param event 事件对象
   */
  removeConfirmWindowConfirmButtonClickEvent(event) {
    ////////////////////////////////////////////////////////////////////////////
    // 获取调用源。
    ////////////////////////////////////////////////////////////////////////////
    const source = event.data;
    ////////////////////////////////////////////////////////////////////////////
    // 参数检查数组。
    ////////////////////////////////////////////////////////////////////////////
    const parameterCheckArray = new Array();
    parameterCheckArray.push({"name": "uuid", "value": source.removeDataUuid, "id": source.removeConfirmWindow.contentLabel.getId(), "allow_null": false});
    ////////////////////////////////////////////////////////////////////////////
    // 检查参数。
    ////////////////////////////////////////////////////////////////////////////
    for (let i = 0; i < parameterCheckArray.length; i++) {
      const parameterObj = parameterCheckArray[i];
      if (!Module.checkParameter(source.menuRule, "removeMenu", parameterObj, source, function error(source, errorMessage) {
        source.removeConfirmWindow.showResultInfo("error", errorMessage);
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
    // 删除菜单。
    ////////////////////////////////////////////////////////////////////////////
    Network.request(Network.RequestType.POST, Network.ResponseType.JSON, [{"Content-Type": "application/x-www-form-urlencoded"}],
      Configure.getServerUrl() + "/module/security.Menu/removeMenu", parameterArray, source,
      function loadStart(xhr, xhrEvent, source) {
        source.removeConfirmWindow.hideResultInfo(); // 隐藏结果信息。
        source.removeConfirmWindow.frozenControl(); // 冻结控件。
      },
      function error(xhr, xhrEvent, source) {
        source.removeConfirmWindow.showResultInfo("error", "网络请求失败");
      },
      function timeout(xhr, xhrEvent, source) {
        source.removeConfirmWindow.showResultInfo("error", "网络请求超时");
      },
      function readyStateChange(xhr, xhrEvent, source) {
        if ((XMLHttpRequest.DONE == xhr.readyState) && (200 == xhr.status)) {
          source.removeConfirmWindow.recoverControl(); // 恢复控件。
          //////////////////////////////////////////////////////////////////////
          // 响应结果。
          //////////////////////////////////////////////////////////////////////
          const responseResult = xhr.response;
          if (Toolkit.stringEqualsIgnoreCase("SUCCESS", responseResult.status)) {
            ////////////////////////////////////////////////////////////////////
            // 显示成功信息。
            ////////////////////////////////////////////////////////////////////
            source.removeConfirmWindow.showResultInfo("success", "删除成功");
            ////////////////////////////////////////////////////////////////////
            // 删除完成。
            ////////////////////////////////////////////////////////////////////
            source.removeConfirmWindow.complete();
            ////////////////////////////////////////////////////////////////////
            // 获取菜单。
            ////////////////////////////////////////////////////////////////////
            source.getMenu();
          } else if (Toolkit.stringEqualsIgnoreCase("ERROR", responseResult.status)) {
            source.removeConfirmWindow.showResultInfo("error", responseResult.attach);
          } else {
            source.removeConfirmWindow.showResultInfo("error", "操作异常");
          }
        }
      }
    );
  }

  /**
   * 获取菜单
   */
  getMenu() {
    ////////////////////////////////////////////////////////////////////////////
    // 获取菜单。
    ////////////////////////////////////////////////////////////////////////////
    Network.request(Network.RequestType.POST, Network.ResponseType.JSON, [{"Content-Type": "application/x-www-form-urlencoded"}],
      Configure.getServerUrl() + "/module/security.Menu/getMenu", [{"Account-Token": AccountSecurity.getItem("account_token")}], this,
      function loadStart(xhr, xhrEvent, source) {
        source.frozenControl("getMenu"); // 冻结控件。
        source.waitMask.show(); // 显示等待遮蔽。
      },
      function error(xhr, xhrEvent, source) {
        Error.redirect("../home/error.html", "获取菜单", "网络请求失败", window.location.href);
      },
      function timeout(xhr, xhrEvent, source) {
        Error.redirect("../home/error.html", "获取菜单", "网络请求超时", window.location.href);
      },
      function readyStateChange(xhr, xhrEvent, source) {
        if ((XMLHttpRequest.DONE == xhr.readyState) && (200 == xhr.status)) {
          source.recoverControl("getMenu"); // 恢复控件。
          source.waitMask.hide(); // 隐藏等待遮蔽。
          //////////////////////////////////////////////////////////////////////
          // 响应结果。
          //////////////////////////////////////////////////////////////////////
          const responseResult = xhr.response;
          if (Toolkit.stringEqualsIgnoreCase("SUCCESS", responseResult.status)) {
            ////////////////////////////////////////////////////////////////////
            // 清空数组。
            ////////////////////////////////////////////////////////////////////
            source.menuArray.splice(0, source.menuArray.length);
            ////////////////////////////////////////////////////////////////////
            // 存入菜单数组。
            ////////////////////////////////////////////////////////////////////
            for (let i = 0; i < responseResult.content.array.length; i++) {
              source.menuArray.push(responseResult.content.array[i]);
            }
            ////////////////////////////////////////////////////////////////////
            // 刷新下拉框，这里只能刷新添加菜单的下拉框，修改菜单里下拉框的数据
            // 在点击修改按钮时加载。
            ////////////////////////////////////////////////////////////////////
            source.addMenuAM.refreshSelect(source.menuArray);
            ////////////////////////////////////////////////////////////////////
            // 恢复控件。
            ////////////////////////////////////////////////////////////////////
            source.recoverControl("getMenu");
            ////////////////////////////////////////////////////////////////////
            // 如果菜单数组不为空，则添加表格数据。
            ////////////////////////////////////////////////////////////////////
            if (0 < source.menuArray.length) {
              let code = "";
              for (let i = 0; i < source.menuArray.length; i++) {
                const menu = source.menuArray[i];
                let menuLevelClassName = "";
                if (1 == menu.level) {
                  // 一级菜单
                  menuLevelClassName = "menu_level_one";
                } else if (2 == menu.level) {
                  // 二级菜单
                  menuLevelClassName = "menu_level_two";
                } else {
                  // 三级菜单
                  menuLevelClassName = "menu_level_three";
                }
                let description = "";
                ////////////////////////////////////////////////////////////////
                // 根据显示要求优化数据。
                ////////////////////////////////////////////////////////////////
                if (null == menu.description) {
                  description = "无";
                } else {
                  description = menu.description;
                }
                code += `
                  <tr data-parent_uuid = "${menu.parent_uuid}" data-order = "${menu.order}">
                    <td class = "name ${menuLevelClassName}">${menu.name}</td>
                    <td class = "text">${menu.text}</td>
                    <td class = "description">${description}</td>
                    <td class = "operation" data-uuid = "${menu.uuid}"><span class = "modify">修改</span><span class = "remove">删除</span></td>
                  </tr>
                `;
              }
              source.menuTable.getObject().find("tbody").html(code);
            } else {
              source.menuTable.getObject().find("tbody").html(`
                <tr>
                  <td class = "rowspan" colspan = "4">尚无数据</td>
                </tr>
              `);
            }
            ////////////////////////////////////////////////////////////////////
            // 加载完成后注册事件。
            ////////////////////////////////////////////////////////////////////
            source.menuTable.getObject().find("tbody").find("tr").find(".operation").find(".modify").off("click").on("click", null, source, source.menuTableModifyButtonClickEvent);
            source.menuTable.getObject().find("tbody").find("tr").find(".operation").find(".remove").off("click").on("click", null, source, source.menuTableRemoveButtonClickEvent);
          } else {
            Error.redirect("../home/error.html", "获取菜单", responseResult.attach, window.location.href);
          }
        }
      }
    );
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
    this.addMenuLevelOneButton.getObject().attr("disabled", "disabled");
    this.addMenuLevelTwoButton.getObject().attr("disabled", "disabled");
    this.addMenuLevelThreeButton.getObject().attr("disabled", "disabled");
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
      this.addMenuLevelOneButton.getObject().removeAttr("disabled");
      this.addMenuLevelTwoButton.getObject().removeAttr("disabled");
      this.addMenuLevelThreeButton.getObject().removeAttr("disabled");
    }
  }
}
