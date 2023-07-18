"use strict";

class PlaceAttributeManagement {
  /**
   * 构造函数
   *
   * @param ruleArray 规则数组
   */
  constructor(ruleArray) {
    ////////////////////////////////////////////////////////////////////////////
    // 地点属性规则。
    ////////////////////////////////////////////////////////////////////////////
    this.placeAttributeRule = ruleArray[0];
    ////////////////////////////////////////////////////////////////////////////
    // 地点属性数组。
    ////////////////////////////////////////////////////////////////////////////
    this.placeAttributeArray = new Array();
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
    // 添加地点属性按钮。
    ////////////////////////////////////////////////////////////////////////////
    this.addPlaceAttributeButton = new JSControl("button");
    ////////////////////////////////////////////////////////////////////////////
    // 地点属性表格。
    ////////////////////////////////////////////////////////////////////////////
    this.placeAttributeTable = new JSControl("table");
    ////////////////////////////////////////////////////////////////////////////
    // 等待遮蔽。
    ////////////////////////////////////////////////////////////////////////////
    this.waitMask = new WaitMask();
    ////////////////////////////////////////////////////////////////////////////
    // 添加地点属性AM。
    ////////////////////////////////////////////////////////////////////////////
    this.addPlaceAttributeAM = new PlaceAttributeAM(this, "添加地点属性", 40);
    ////////////////////////////////////////////////////////////////////////////
    // 修改地点属性AM。
    ////////////////////////////////////////////////////////////////////////////
    this.modifyPlaceAttributeAM = new PlaceAttributeAM(this, "修改地点属性", 40);
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
    // 添加地点属性按钮。
    ////////////////////////////////////////////////////////////////////////////
    this.addPlaceAttributeButton.setAttribute(
      {
        "class": "global_button_primary add_place_attribute_button"
      }
    );
    this.addPlaceAttributeButton.setContent("添加地点属性");
    ////////////////////////////////////////////////////////////////////////////
    // 地点属性表格。
    ////////////////////////////////////////////////////////////////////////////
    this.placeAttributeTable.setAttribute(
      {
        "class": "global_table place_attribute_table"
      }
    );
    const tableHead = `
      <tr>
        <td class = "name">名称</td>
        <td class = "operation">操作</td>
      </tr>
    `;
    this.placeAttributeTable.setContent(`
      <thead>${tableHead}</thead>
      <tbody>
        <tr>
          <td class = "rowspan" colspan = "2">尚无数据</td>
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
    // 添加地点属性AM。
    ////////////////////////////////////////////////////////////////////////////
    this.addPlaceAttributeAM.setClassSign("add_place_attribute_am");
    ////////////////////////////////////////////////////////////////////////////
    // 修改地点属性AM。
    ////////////////////////////////////////////////////////////////////////////
    this.modifyPlaceAttributeAM.setClassSign("modify_place_attribute_am");
    ////////////////////////////////////////////////////////////////////////////
    // 删除确认窗。
    ////////////////////////////////////////////////////////////////////////////
    this.removeConfirmWindow.setAttribute(
      {
        "class": "confirm_window"
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
    // 添加地点属性按钮。
    ////////////////////////////////////////////////////////////////////////////
    this.addPlaceAttributeButton.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // 地点属性表格。
    ////////////////////////////////////////////////////////////////////////////
    this.placeAttributeTable.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // 等待遮蔽。
    ////////////////////////////////////////////////////////////////////////////
    this.waitMask.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // 添加地点属性AM。
    ////////////////////////////////////////////////////////////////////////////
    this.addPlaceAttributeAM.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // 修改地点属性AM。
    ////////////////////////////////////////////////////////////////////////////
    this.modifyPlaceAttributeAM.generateCode();
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
    // 工具栏添加添加地点属性按钮。
    ////////////////////////////////////////////////////////////////////////////
    this.toolbar.getObject().append(this.addPlaceAttributeButton.getCode());
    ////////////////////////////////////////////////////////////////////////////
    // 容器添加地点属性表格。
    ////////////////////////////////////////////////////////////////////////////
    this.container.getObject().append(this.placeAttributeTable.getCode());
    ////////////////////////////////////////////////////////////////////////////
    // 容器添加等待遮蔽。
    ////////////////////////////////////////////////////////////////////////////
    this.container.getObject().append(this.waitMask.getCode());
    ////////////////////////////////////////////////////////////////////////////
    // 容器添加添加地点属性AM。
    ////////////////////////////////////////////////////////////////////////////
    this.container.getObject().append(this.addPlaceAttributeAM.getCode());
    ////////////////////////////////////////////////////////////////////////////
    // 容器添加修改地点属性AM。
    ////////////////////////////////////////////////////////////////////////////
    this.container.getObject().append(this.modifyPlaceAttributeAM.getCode());
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
    // 注册添加地点属性按钮的click事件。
    ////////////////////////////////////////////////////////////////////////////
    this.addPlaceAttributeButton.getObject().off("click").on("click", null, this, this.addPlaceAttributeButtonClickEvent);
    ////////////////////////////////////////////////////////////////////////////
    // 初始化添加地点属性AM事件。
    ////////////////////////////////////////////////////////////////////////////
    this.addPlaceAttributeAM.initEvent();
    ////////////////////////////////////////////////////////////////////////////
    // 初始化修改地点属性AM事件。
    ////////////////////////////////////////////////////////////////////////////
    this.modifyPlaceAttributeAM.initEvent();
    ////////////////////////////////////////////////////////////////////////////
    // 初始化删除确认窗事件。
    ////////////////////////////////////////////////////////////////////////////
    this.removeConfirmWindow.initEvent();
    ////////////////////////////////////////////////////////////////////////////
    // 注册添加地点属性AM确认按钮的click事件。
    ////////////////////////////////////////////////////////////////////////////
    this.addPlaceAttributeAM.formList.confirmButton.getObject().off("click").on("click", null, this, this.addPlaceAttributeAMConfirmButtonClickEvent);
    ////////////////////////////////////////////////////////////////////////////
    // 注册添加地点属性AM取消按钮的click事件。
    ////////////////////////////////////////////////////////////////////////////
    this.addPlaceAttributeAM.formList.cancelButton.getObject().off("click").on("click", null, this, this.addPlaceAttributeAMCancelButtonClickEvent);
    ////////////////////////////////////////////////////////////////////////////
    // 注册修改地点属性AM确认按钮的click事件。
    ////////////////////////////////////////////////////////////////////////////
    this.modifyPlaceAttributeAM.formList.confirmButton.getObject().off("click").on("click", null, this, this.modifyPlaceAttributeAMConfirmButtonClickEvent);
    ////////////////////////////////////////////////////////////////////////////
    // 注册修改地点属性AM取消按钮的click事件。
    ////////////////////////////////////////////////////////////////////////////
    this.modifyPlaceAttributeAM.formList.cancelButton.getObject().off("click").on("click", null, this, this.modifyPlaceAttributeAMCancelButtonClickEvent);
    ////////////////////////////////////////////////////////////////////////////
    // 注册删除确认窗确认按钮的click事件。
    ////////////////////////////////////////////////////////////////////////////
    this.removeConfirmWindow.confirmButton.getObject().off("click").on("click", null, this, this.removeConfirmWindowConfirmButtonClickEvent);
  }

  /**
   * 添加地点属性按钮click事件
   * @param event 事件对象
   */
  addPlaceAttributeButtonClickEvent(event) {
    ////////////////////////////////////////////////////////////////////////////
    // 获取调用源。
    ////////////////////////////////////////////////////////////////////////////
    const source = event.data;
    source.addPlaceAttributeAM.show();
  }

  /**
   * 添加地点属性AM确认按钮click事件
   * @param event 事件对象
   */
  addPlaceAttributeAMConfirmButtonClickEvent(event) {
    ////////////////////////////////////////////////////////////////////////////
    // 获取调用源。
    ////////////////////////////////////////////////////////////////////////////
    const source = event.data;
    ////////////////////////////////////////////////////////////////////////////
    // 校验数据之前，先要隐藏之前的提示。
    ////////////////////////////////////////////////////////////////////////////
    source.addPlaceAttributeAM.formList.hideAllPrompt();
    ////////////////////////////////////////////////////////////////////////////
    // 参数检查数组。
    ////////////////////////////////////////////////////////////////////////////
    const parameterCheckArray = new Array();
    parameterCheckArray.push({"name": "name", "value": source.addPlaceAttributeAM.nameTextField.getObject().val(), "id": source.addPlaceAttributeAM.nameTextField.getId(), "allow_null": false, "custom_error_message": null});
    parameterCheckArray.push({"name": "order", "value": source.addPlaceAttributeAM.orderTextField.getObject().val(), "id": source.addPlaceAttributeAM.orderTextField.getId(), "allow_null": false, "custom_error_message": null});
    ////////////////////////////////////////////////////////////////////////////
    // 检查参数。
    ////////////////////////////////////////////////////////////////////////////
    for (let i = 0; i < parameterCheckArray.length; i++) {
      const parameterObj = parameterCheckArray[i];
      if (!Module.checkParameter(source.placeAttributeRule, "addPlaceAttribute", parameterObj, source, function error(source, errorMessage) {
        source.addPlaceAttributeAM.formList.showPrompt(parameterObj.id, errorMessage);
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
    // 添加地点属性。
    ////////////////////////////////////////////////////////////////////////////
    Network.request(Network.RequestType.POST, Network.ResponseType.JSON, [{"Content-Type": "application/x-www-form-urlencoded"}],
      Configure.getServerUrl() + "/module/supervision.spot.PlaceAttribute/addPlaceAttribute", parameterArray, source,
      function loadStart(xhr, xhrEvent, source) {
        source.addPlaceAttributeAM.formList.hideResultInfo(); // 隐藏结果信息。
        source.addPlaceAttributeAM.frozenControl("addPlaceAttribute"); // 冻结控件。
      },
      function error(xhr, xhrEvent, source) {
        source.addPlaceAttributeAM.formList.showResultInfo("error", "网络请求失败");
      },
      function timeout(xhr, xhrEvent, source) {
        source.addPlaceAttributeAM.formList.showResultInfo("error", "网络请求超时");
      },
      function readyStateChange(xhr, xhrEvent, source) {
        if ((XMLHttpRequest.DONE == xhr.readyState) && (200 == xhr.status)) {
          source.addPlaceAttributeAM.recoverControl("addPlaceAttribute"); // 恢复控件。
          //////////////////////////////////////////////////////////////////////
          // 响应结果。
          //////////////////////////////////////////////////////////////////////
          const responseResult = xhr.response;
          if (Toolkit.stringEqualsIgnoreCase("SUCCESS", responseResult.status)) {
            ////////////////////////////////////////////////////////////////////
            // 显示成功信息。
            ////////////////////////////////////////////////////////////////////
            source.addPlaceAttributeAM.formList.showResultInfo("success", "添加成功");
            ////////////////////////////////////////////////////////////////////
            // 重置控件。
            ////////////////////////////////////////////////////////////////////
            source.addPlaceAttributeAM.resetControl();
            ////////////////////////////////////////////////////////////////////
            // 获取地点属性。
            ////////////////////////////////////////////////////////////////////
            source.getPlaceAttribute();
          } else if (Toolkit.stringEqualsIgnoreCase("ERROR", responseResult.status)) {
            source.addPlaceAttributeAM.formList.showResultInfo("error", responseResult.attach);
          } else {
            source.addPlaceAttributeAM.formList.showResultInfo("error", "操作异常");
          }
        }
      }
    );
  }

  /**
   * 添加地点属性AM取消按钮click事件
   * @param event 事件对象
   */
  addPlaceAttributeAMCancelButtonClickEvent(event) {
    ////////////////////////////////////////////////////////////////////////////
    // 获取调用源。
    ////////////////////////////////////////////////////////////////////////////
    const source = event.data;
    source.addPlaceAttributeAM.hide();
  }

  /**
   * 修改地点属性AM确认按钮click事件
   * @param event 事件对象
   */
  modifyPlaceAttributeAMConfirmButtonClickEvent(event) {
    ////////////////////////////////////////////////////////////////////////////
    // 获取调用源。
    ////////////////////////////////////////////////////////////////////////////
    const source = event.data;
    ////////////////////////////////////////////////////////////////////////////
    // 校验数据之前，先要隐藏之前的提示。
    ////////////////////////////////////////////////////////////////////////////
    source.modifyPlaceAttributeAM.formList.hideAllPrompt();
    ////////////////////////////////////////////////////////////////////////////
    // 参数检查数组。
    ////////////////////////////////////////////////////////////////////////////
    const parameterCheckArray = new Array();
    parameterCheckArray.push({"name": "uuid", "value": source.modifyPlaceAttributeAM.uuidTextField.getObject().val(), "id": source.modifyPlaceAttributeAM.uuidTextField.getId(), "allow_null": false, "custom_error_message": null});
    parameterCheckArray.push({"name": "name", "value": source.modifyPlaceAttributeAM.nameTextField.getObject().val(), "id": source.modifyPlaceAttributeAM.nameTextField.getId(), "allow_null": false, "custom_error_message": null});
    parameterCheckArray.push({"name": "order", "value": source.modifyPlaceAttributeAM.orderTextField.getObject().val(), "id": source.modifyPlaceAttributeAM.orderTextField.getId(), "allow_null": false, "custom_error_message": null});
    ////////////////////////////////////////////////////////////////////////////
    // 检查参数。
    ////////////////////////////////////////////////////////////////////////////
    for (let i = 0; i < parameterCheckArray.length; i++) {
      const parameterObj = parameterCheckArray[i];
      if (!Module.checkParameter(source.placeAttributeRule, "modifyPlaceAttribute", parameterObj, source, function error(source, errorMessage) {
        source.modifyPlaceAttributeAM.formList.showPrompt(parameterObj.id, errorMessage);
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
    // 修改地点属性。
    ////////////////////////////////////////////////////////////////////////////
    Network.request(Network.RequestType.POST, Network.ResponseType.JSON, [{"Content-Type": "application/x-www-form-urlencoded"}],
      Configure.getServerUrl() + "/module/supervision.spot.PlaceAttribute/modifyPlaceAttribute", parameterArray, source,
      function loadStart(xhr, xhrEvent, source) {
        source.modifyPlaceAttributeAM.formList.hideResultInfo(); // 隐藏结果信息。
        source.modifyPlaceAttributeAM.frozenControl("modifyPlaceAttribute"); // 冻结控件。
      },
      function error(xhr, xhrEvent, source) {
        source.modifyPlaceAttributeAM.formList.showResultInfo("error", "网络请求失败");
      },
      function timeout(xhr, xhrEvent, source) {
        source.modifyPlaceAttributeAM.formList.showResultInfo("error", "网络请求超时");
      },
      function readyStateChange(xhr, xhrEvent, source) {
        if ((XMLHttpRequest.DONE == xhr.readyState) && (200 == xhr.status)) {
          source.modifyPlaceAttributeAM.recoverControl("modifyPlaceAttribute"); // 恢复控件。
          //////////////////////////////////////////////////////////////////////
          // 响应结果。
          //////////////////////////////////////////////////////////////////////
          const responseResult = xhr.response;
          if (Toolkit.stringEqualsIgnoreCase("SUCCESS", responseResult.status)) {
            ////////////////////////////////////////////////////////////////////
            // 显示成功信息。
            ////////////////////////////////////////////////////////////////////
            source.modifyPlaceAttributeAM.formList.showResultInfo("success", "修改成功");
            ////////////////////////////////////////////////////////////////////
            // 重置控件（修改功能在成功修改之后，不需要重置控件内容，这里不用但
            // 做保留）。
            ////////////////////////////////////////////////////////////////////
            // source.modifyPlaceAttributeAM.resetControl();
            ////////////////////////////////////////////////////////////////////
            // 获取地点属性。
            ////////////////////////////////////////////////////////////////////
            source.getPlaceAttribute();
          } else if (Toolkit.stringEqualsIgnoreCase("ERROR", responseResult.status)) {
            source.modifyPlaceAttributeAM.formList.showResultInfo("error", responseResult.attach);
          } else {
            source.modifyPlaceAttributeAM.formList.showResultInfo("error", "操作异常");
          }
        }
      }
    );
  }

  /**
   * 修改地点属性AM取消按钮click事件
   * @param event 事件对象
   */
  modifyPlaceAttributeAMCancelButtonClickEvent(event) {
    ////////////////////////////////////////////////////////////////////////////
    // 获取调用源。
    ////////////////////////////////////////////////////////////////////////////
    const source = event.data;
    source.modifyPlaceAttributeAM.hide();
  }

  /**
   * 地点属性表格修改按钮click事件
   * @param event 事件对象
   */
  placeAttributeTableModifyButtonClickEvent(event) {
    ////////////////////////////////////////////////////////////////////////////
    // 获取调用源。
    ////////////////////////////////////////////////////////////////////////////
    const source = event.data;
    ////////////////////////////////////////////////////////////////////////////
    // 获取待修改数据的uuid。
    ////////////////////////////////////////////////////////////////////////////
    const uuid = $(this).parent().attr("data-uuid");
    ////////////////////////////////////////////////////////////////////////////
    // 从地点属性数组中获取该uuid对应的数据。
    ////////////////////////////////////////////////////////////////////////////
    let obj = null;
    for (let i = 0; i < source.placeAttributeArray.length; i++) {
      if (uuid == source.placeAttributeArray[i].uuid) {
        obj = source.placeAttributeArray[i];
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
      source.modifyPlaceAttributeAM.uuidTextField.getObject().val(obj.uuid);
      //////////////////////////////////////////////////////////////////////////
      // 加载名称。
      //////////////////////////////////////////////////////////////////////////
      source.modifyPlaceAttributeAM.nameTextField.getObject().val(obj.name);
      //////////////////////////////////////////////////////////////////////////
      // 加载排序。
      //////////////////////////////////////////////////////////////////////////
      source.modifyPlaceAttributeAM.orderTextField.getObject().val(obj.order);
      //////////////////////////////////////////////////////////////////////////
      // 显示修改地点属性。
      //////////////////////////////////////////////////////////////////////////
      source.modifyPlaceAttributeAM.show();
    }
  }

  /**
   * 地点属性表格删除按钮click事件
   * @param event 事件对象
   */
  placeAttributeTableRemoveButtonClickEvent(event) {
    ////////////////////////////////////////////////////////////////////////////
    // 获取调用源。
    ////////////////////////////////////////////////////////////////////////////
    const source = event.data;
    source.removeDataUuid = $(this).parent().attr("data-uuid");
    source.removeConfirmWindow.show("确认要删除地点属性吗？");
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
    parameterCheckArray.push({"name": "uuid", "value": source.removeDataUuid, "id": source.removeConfirmWindow.contentLabel.getId(), "allow_null": false, "custom_error_message": null});
    ////////////////////////////////////////////////////////////////////////////
    // 检查参数。
    ////////////////////////////////////////////////////////////////////////////
    for (let i = 0; i < parameterCheckArray.length; i++) {
      const parameterObj = parameterCheckArray[i];
      if (!Module.checkParameter(source.placeAttributeRule, "removePlaceAttribute", parameterObj, source, function error(source, errorMessage) {
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
    // 删除地点属性。
    ////////////////////////////////////////////////////////////////////////////
    Network.request(Network.RequestType.POST, Network.ResponseType.JSON, [{"Content-Type": "application/x-www-form-urlencoded"}],
      Configure.getServerUrl() + "/module/supervision.spot.PlaceAttribute/removePlaceAttribute", parameterArray, source,
      function loadStart(xhr, xhrEvent, source) {
        source.removeConfirmWindow.hideResultInfo(); // 隐藏结果信息。
        source.removeConfirmWindow.frozenControl("removePlaceAttribute"); // 冻结控件。
      },
      function error(xhr, xhrEvent, source) {
        source.removeConfirmWindow.showResultInfo("error", "网络请求失败");
      },
      function timeout(xhr, xhrEvent, source) {
        source.removeConfirmWindow.showResultInfo("error", "网络请求超时");
      },
      function readyStateChange(xhr, xhrEvent, source) {
        if ((XMLHttpRequest.DONE == xhr.readyState) && (200 == xhr.status)) {
          source.removeConfirmWindow.recoverControl("removePlaceAttribute"); // 恢复控件。
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
            // 获取地点属性。
            ////////////////////////////////////////////////////////////////////
            source.getPlaceAttribute();
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
   * 获取地点属性
   */
  getPlaceAttribute() {
    ////////////////////////////////////////////////////////////////////////////
    // 获取地点属性。
    ////////////////////////////////////////////////////////////////////////////
    Network.request(Network.RequestType.POST, Network.ResponseType.JSON, [{"Content-Type": "application/x-www-form-urlencoded"}],
      Configure.getServerUrl() + "/module/supervision.spot.PlaceAttribute/getPlaceAttribute", [{"Account-Token": AccountSecurity.getItem("account_token")}], this,
      function loadStart(xhr, xhrEvent, source) {
        source.frozenControl("getPlaceAttribute"); // 冻结控件。
        source.waitMask.show(); // 显示等待遮蔽。
      },
      function error(xhr, xhrEvent, source) {
        Error.redirect("../home/error.html", "获取地点属性", "网络请求失败", window.location.href);
      },
      function timeout(xhr, xhrEvent, source) {
        Error.redirect("../home/error.html", "获取地点属性", "网络请求超时", window.location.href);
      },
      function readyStateChange(xhr, xhrEvent, source) {
        if ((XMLHttpRequest.DONE == xhr.readyState) && (200 == xhr.status)) {
          source.recoverControl("getPlaceAttribute"); // 恢复控件。
          source.waitMask.hide(); // 隐藏等待遮蔽。
          //////////////////////////////////////////////////////////////////////
          // 响应结果。
          //////////////////////////////////////////////////////////////////////
          const responseResult = xhr.response;
          if (Toolkit.stringEqualsIgnoreCase("SUCCESS", responseResult.status)) {
            ////////////////////////////////////////////////////////////////////
            // 清空数组。
            ////////////////////////////////////////////////////////////////////
            source.placeAttributeArray.splice(0, source.placeAttributeArray.length);
            ////////////////////////////////////////////////////////////////////
            // 更新数组。
            ////////////////////////////////////////////////////////////////////
            for (let i = 0; i < responseResult.content.array.length; i++) {
              source.placeAttributeArray.push(responseResult.content.array[i]);
            }
            ////////////////////////////////////////////////////////////////////
            // 恢复控件。
            ////////////////////////////////////////////////////////////////////
            source.recoverControl("getPlaceAttribute");
            ////////////////////////////////////////////////////////////////////
            // 如果地点属性数组不为空，则添加表格数据。
            ////////////////////////////////////////////////////////////////////
            if (0 < source.placeAttributeArray.length) {
              let code = "";
              for (let i = 0; i < source.placeAttributeArray.length; i++) {
                const placeAttribute = source.placeAttributeArray[i];
                code += `
                  <tr data-order = "${placeAttribute.order}">
                    <td class = "name">${placeAttribute.name}</td>
                    <td class = "operation" data-uuid = "${placeAttribute.uuid}"><span class = "modify">修改</span><span class = "remove">删除</span></td>
                  </tr>
                `;
              }
              source.placeAttributeTable.getObject().find("tbody").html(code);
            } else {
              source.placeAttributeTable.getObject().find("tbody").html(`
                <tr>
                  <td class = "rowspan" colspan = "2">尚无数据</td>
                </tr>
              `);
            }
            ////////////////////////////////////////////////////////////////////
            // 加载完成后注册事件。
            ////////////////////////////////////////////////////////////////////
            source.placeAttributeTable.getObject().find("tbody").find("tr").find(".operation").find(".modify").off("click").on("click", null, source, source.placeAttributeTableModifyButtonClickEvent);
            source.placeAttributeTable.getObject().find("tbody").find("tr").find(".operation").find(".remove").off("click").on("click", null, source, source.placeAttributeTableRemoveButtonClickEvent);
          } else {
            Error.redirect("../home/error.html", "获取地点属性", responseResult.attach, window.location.href);
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
    this.addPlaceAttributeButton.getObject().attr("disabled", "disabled");
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
      this.addPlaceAttributeButton.getObject().removeAttr("disabled");
    }
  }
}
