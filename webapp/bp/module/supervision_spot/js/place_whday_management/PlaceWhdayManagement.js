"use strict";

class PlaceWhdayManagement {
  /**
   * 构造函数
   *
   * @param ruleArray 规则数组
   */
  constructor(ruleArray) {
    ////////////////////////////////////////////////////////////////////////////
    // 地点工作休息日规则。
    ////////////////////////////////////////////////////////////////////////////
    this.placeWhdayRule = ruleArray[0];
    ////////////////////////////////////////////////////////////////////////////
    // 地点工作休息日数组。
    ////////////////////////////////////////////////////////////////////////////
    this.placeWhdayArray = new Array();
    ////////////////////////////////////////////////////////////////////////////
    // 待删除数据的uuid。
    ////////////////////////////////////////////////////////////////////////////
    this.removeDataUuid = null;
    ////////////////////////////////////////////////////////////////////////////
    // 队列。
    ////////////////////////////////////////////////////////////////////////////
    this.queue = new Queue();
    ////////////////////////////////////////////////////////////////////////////
    // 传递参数。
    ////////////////////////////////////////////////////////////////////////////
    this.placeUuid = Toolkit.getUrlParameter("place_uuid");
    this.placeFullName = Toolkit.urlParameterDecode(Toolkit.getUrlParameter("place_full_name"));
    this.returnPlaceListParameter = Toolkit.getUrlParameter("return_place_list_parameter"); // 不解码直接返回编码格式
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
    // 返回列表按钮。
    ////////////////////////////////////////////////////////////////////////////
    this.returnListButton = new JSControl("button");
    ////////////////////////////////////////////////////////////////////////////
    // 添加地点工作休息日按钮。
    ////////////////////////////////////////////////////////////////////////////
    this.addPlaceWhdayButton = new JSControl("button");
    ////////////////////////////////////////////////////////////////////////////
    // 地点工作休息日表格。
    ////////////////////////////////////////////////////////////////////////////
    this.placeWhdayTable = new JSControl("table");
    ////////////////////////////////////////////////////////////////////////////
    // 等待遮蔽。
    ////////////////////////////////////////////////////////////////////////////
    this.waitMask = new WaitMask();
    ////////////////////////////////////////////////////////////////////////////
    // 添加地点工作休息日AM。
    ////////////////////////////////////////////////////////////////////////////
    this.addPlaceWhdayAM = new PlaceWhdayAM(this, "添加地点工作休息日", 40);
    ////////////////////////////////////////////////////////////////////////////
    // 修改地点工作休息日AM。
    ////////////////////////////////////////////////////////////////////////////
    this.modifyPlaceWhdayAM = new PlaceWhdayAM(this, "修改地点工作休息日", 40);
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
    // 返回列表按钮。
    ////////////////////////////////////////////////////////////////////////////
    this.returnListButton.setAttribute(
      {
        "class": "global_button_primary return_list_button"
      }
    );
    this.returnListButton.setContent("返回地点列表");
    ////////////////////////////////////////////////////////////////////////////
    // 添加地点工作休息日按钮。
    ////////////////////////////////////////////////////////////////////////////
    this.addPlaceWhdayButton.setAttribute(
      {
        "class": "global_button_primary add_place_whday_button"
      }
    );
    this.addPlaceWhdayButton.setContent("添加时间");
    ////////////////////////////////////////////////////////////////////////////
    // 地点工作休息日表格。
    ////////////////////////////////////////////////////////////////////////////
    this.placeWhdayTable.setAttribute(
      {
        "class": "global_table place_whday_table"
      }
    );
    const tableHead = `
      <tr>
        <td class = "place">地点</td>
        <td class = "datetime">时间</td>
        <td class = "type">类型</td>
        <td class = "operation">操作</td>
      </tr>
    `;
    this.placeWhdayTable.setContent(`
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
    // 添加地点工作休息日AM。
    ////////////////////////////////////////////////////////////////////////////
    this.addPlaceWhdayAM.setClassSign("add_place_whday_am");
    ////////////////////////////////////////////////////////////////////////////
    // 修改地点工作休息日AM。
    ////////////////////////////////////////////////////////////////////////////
    this.modifyPlaceWhdayAM.setClassSign("modify_place_whday_am");
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
    // 返回列表按钮。
    ////////////////////////////////////////////////////////////////////////////
    this.returnListButton.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // 添加地点工作休息日按钮。
    ////////////////////////////////////////////////////////////////////////////
    this.addPlaceWhdayButton.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // 地点工作休息日表格。
    ////////////////////////////////////////////////////////////////////////////
    this.placeWhdayTable.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // 等待遮蔽。
    ////////////////////////////////////////////////////////////////////////////
    this.waitMask.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // 添加地点工作休息日AM。
    ////////////////////////////////////////////////////////////////////////////
    this.addPlaceWhdayAM.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // 修改地点工作休息日AM。
    ////////////////////////////////////////////////////////////////////////////
    this.modifyPlaceWhdayAM.generateCode();
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
    // 工具栏添加返回列表按钮。
    ////////////////////////////////////////////////////////////////////////////
    this.toolbar.getObject().append(this.returnListButton.getCode());
    ////////////////////////////////////////////////////////////////////////////
    // 工具栏添加添加地点工作休息日按钮。
    ////////////////////////////////////////////////////////////////////////////
    this.toolbar.getObject().append(this.addPlaceWhdayButton.getCode());
    ////////////////////////////////////////////////////////////////////////////
    // 容器添加地点工作休息日表格。
    ////////////////////////////////////////////////////////////////////////////
    this.container.getObject().append(this.placeWhdayTable.getCode());
    ////////////////////////////////////////////////////////////////////////////
    // 容器添加等待遮蔽。
    ////////////////////////////////////////////////////////////////////////////
    this.container.getObject().append(this.waitMask.getCode());
    ////////////////////////////////////////////////////////////////////////////
    // 容器添加添加地点工作休息日AM。
    ////////////////////////////////////////////////////////////////////////////
    this.container.getObject().append(this.addPlaceWhdayAM.getCode());
    ////////////////////////////////////////////////////////////////////////////
    // 容器添加修改地点工作休息日AM。
    ////////////////////////////////////////////////////////////////////////////
    this.container.getObject().append(this.modifyPlaceWhdayAM.getCode());
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
    // 注册返回列表按钮的click事件。
    ////////////////////////////////////////////////////////////////////////////
    this.returnListButton.getObject().off("click").on("click", null, this, this.returnListButtonClickEvent);
    ////////////////////////////////////////////////////////////////////////////
    // 注册添加地点工作休息日按钮的click事件。
    ////////////////////////////////////////////////////////////////////////////
    this.addPlaceWhdayButton.getObject().off("click").on("click", null, this, this.addPlaceWhdayButtonClickEvent);
    ////////////////////////////////////////////////////////////////////////////
    // 初始化添加地点工作休息日AM事件。
    ////////////////////////////////////////////////////////////////////////////
    this.addPlaceWhdayAM.initEvent();
    ////////////////////////////////////////////////////////////////////////////
    // 初始化修改地点工作休息日AM事件。
    ////////////////////////////////////////////////////////////////////////////
    this.modifyPlaceWhdayAM.initEvent();
    ////////////////////////////////////////////////////////////////////////////
    // 初始化删除确认窗事件。
    ////////////////////////////////////////////////////////////////////////////
    this.removeConfirmWindow.initEvent();
    ////////////////////////////////////////////////////////////////////////////
    // 注册添加地点工作休息日AM确认按钮的click事件。
    ////////////////////////////////////////////////////////////////////////////
    this.addPlaceWhdayAM.formList.confirmButton.getObject().off("click").on("click", null, this, this.addPlaceWhdayAMConfirmButtonClickEvent);
    ////////////////////////////////////////////////////////////////////////////
    // 注册添加地点工作休息日AM取消按钮的click事件。
    ////////////////////////////////////////////////////////////////////////////
    this.addPlaceWhdayAM.formList.cancelButton.getObject().off("click").on("click", null, this, this.addPlaceWhdayAMCancelButtonClickEvent);
    ////////////////////////////////////////////////////////////////////////////
    // 注册修改地点工作休息日AM确认按钮的click事件。
    ////////////////////////////////////////////////////////////////////////////
    this.modifyPlaceWhdayAM.formList.confirmButton.getObject().off("click").on("click", null, this, this.modifyPlaceWhdayAMConfirmButtonClickEvent);
    ////////////////////////////////////////////////////////////////////////////
    // 注册修改地点工作休息日AM取消按钮的click事件。
    ////////////////////////////////////////////////////////////////////////////
    this.modifyPlaceWhdayAM.formList.cancelButton.getObject().off("click").on("click", null, this, this.modifyPlaceWhdayAMCancelButtonClickEvent);
    ////////////////////////////////////////////////////////////////////////////
    // 注册删除确认窗确认按钮的click事件。
    ////////////////////////////////////////////////////////////////////////////
    this.removeConfirmWindow.confirmButton.getObject().off("click").on("click", null, this, this.removeConfirmWindowConfirmButtonClickEvent);
  }

  /**
   * 返回列表按钮click事件
   * @param event 事件对象
   */
  returnListButtonClickEvent(event) {
    ////////////////////////////////////////////////////////////////////////////
    // 获取调用源。
    ////////////////////////////////////////////////////////////////////////////
    const source = event.data;
    window.location.href = `./place_management.html?return_place_list_parameter=${source.returnPlaceListParameter}`;
  }

  /**
   * 添加地点工作休息日按钮click事件
   * @param event 事件对象
   */
  addPlaceWhdayButtonClickEvent(event) {
    ////////////////////////////////////////////////////////////////////////////
    // 获取调用源。
    ////////////////////////////////////////////////////////////////////////////
    const source = event.data;
    source.addPlaceWhdayAM.show();
  }

  /**
   * 添加地点工作休息日AM确认按钮click事件
   * @param event 事件对象
   */
  addPlaceWhdayAMConfirmButtonClickEvent(event) {
    ////////////////////////////////////////////////////////////////////////////
    // 获取调用源。
    ////////////////////////////////////////////////////////////////////////////
    const source = event.data;
    ////////////////////////////////////////////////////////////////////////////
    // 校验数据之前，先要隐藏之前的提示。
    ////////////////////////////////////////////////////////////////////////////
    source.addPlaceWhdayAM.formList.hideAllPrompt();
    ////////////////////////////////////////////////////////////////////////////
    // 参数检查数组。
    ////////////////////////////////////////////////////////////////////////////
    const parameterCheckArray = new Array();
    parameterCheckArray.push({"name": "place_uuid", "value": source.placeUuid, "id": "0", "allow_null": false, "custom_error_message": null});
    parameterCheckArray.push({"name": "type", "value": source.addPlaceWhdayAM.typeSelect.getObject().val(), "id": source.addPlaceWhdayAM.typeSelect.getId(), "allow_null": false, "custom_error_message": null});
    parameterCheckArray.push({"name": "whday_datetime", "value": source.addPlaceWhdayAM.whdayDatetimeTextField.getObject().val(), "id": source.addPlaceWhdayAM.whdayDatetimeTextField.getId(), "allow_null": false, "custom_error_message": null});
    ////////////////////////////////////////////////////////////////////////////
    // 检查参数。
    ////////////////////////////////////////////////////////////////////////////
    for (let i = 0; i < parameterCheckArray.length; i++) {
      const parameterObj = parameterCheckArray[i];
      if (!Module.checkParameter(source.placeWhdayRule, "addPlaceWhday", parameterObj, source, function error(source, errorMessage) {
        source.addPlaceWhdayAM.formList.showPrompt(parameterObj.id, errorMessage);
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
    // 添加地点工作休息日。
    ////////////////////////////////////////////////////////////////////////////
    Network.request(Network.RequestType.POST, Network.ResponseType.JSON, [{"Content-Type": "application/x-www-form-urlencoded"}],
      Configure.getServerUrl() + "/module/supervision.spot.PlaceWhday/addPlaceWhday", parameterArray, source,
      function loadStart(xhr, xhrEvent, source) {
        source.addPlaceWhdayAM.formList.hideResultInfo(); // 隐藏结果信息。
        source.addPlaceWhdayAM.frozenControl("addPlaceWhday"); // 冻结控件。
      },
      function error(xhr, xhrEvent, source) {
        source.addPlaceWhdayAM.formList.showResultInfo("error", "网络请求失败");
      },
      function timeout(xhr, xhrEvent, source) {
        source.addPlaceWhdayAM.formList.showResultInfo("error", "网络请求超时");
      },
      function readyStateChange(xhr, xhrEvent, source) {
        if ((XMLHttpRequest.DONE == xhr.readyState) && (200 == xhr.status)) {
          source.addPlaceWhdayAM.recoverControl("addPlaceWhday"); // 恢复控件。
          //////////////////////////////////////////////////////////////////////
          // 响应结果。
          //////////////////////////////////////////////////////////////////////
          const responseResult = xhr.response;
          if (Toolkit.stringEqualsIgnoreCase("SUCCESS", responseResult.status)) {
            ////////////////////////////////////////////////////////////////////
            // 显示成功信息。
            ////////////////////////////////////////////////////////////////////
            source.addPlaceWhdayAM.formList.showResultInfo("success", "添加成功");
            ////////////////////////////////////////////////////////////////////
            // 重置控件。
            ////////////////////////////////////////////////////////////////////
            source.addPlaceWhdayAM.resetControl();
            ////////////////////////////////////////////////////////////////////
            // 获取地点工作休息日。
            ////////////////////////////////////////////////////////////////////
            source.getPlaceWhday();
          } else if (Toolkit.stringEqualsIgnoreCase("ERROR", responseResult.status)) {
            source.addPlaceWhdayAM.formList.showResultInfo("error", responseResult.attach);
          } else {
            source.addPlaceWhdayAM.formList.showResultInfo("error", "操作异常");
          }
        }
      }
    );
  }

  /**
   * 添加地点工作休息日AM取消按钮click事件
   * @param event 事件对象
   */
  addPlaceWhdayAMCancelButtonClickEvent(event) {
    ////////////////////////////////////////////////////////////////////////////
    // 获取调用源。
    ////////////////////////////////////////////////////////////////////////////
    const source = event.data;
    source.addPlaceWhdayAM.hide();
  }

  /**
   * 修改地点工作休息日AM确认按钮click事件
   * @param event 事件对象
   */
  modifyPlaceWhdayAMConfirmButtonClickEvent(event) {
    ////////////////////////////////////////////////////////////////////////////
    // 获取调用源。
    ////////////////////////////////////////////////////////////////////////////
    const source = event.data;
    ////////////////////////////////////////////////////////////////////////////
    // 校验数据之前，先要隐藏之前的提示。
    ////////////////////////////////////////////////////////////////////////////
    source.modifyPlaceWhdayAM.formList.hideAllPrompt();
    ////////////////////////////////////////////////////////////////////////////
    // 参数检查数组。
    ////////////////////////////////////////////////////////////////////////////
    const parameterCheckArray = new Array();
    parameterCheckArray.push({"name": "uuid", "value": source.modifyPlaceWhdayAM.uuidTextField.getObject().val(), "id": source.modifyPlaceWhdayAM.uuidTextField.getId(), "allow_null": false, "custom_error_message": null});
    parameterCheckArray.push({"name": "type", "value": source.modifyPlaceWhdayAM.typeSelect.getObject().val(), "id": source.modifyPlaceWhdayAM.typeSelect.getId(), "allow_null": false, "custom_error_message": null});
    parameterCheckArray.push({"name": "whday_datetime", "value": source.modifyPlaceWhdayAM.whdayDatetimeTextField.getObject().val(), "id": source.modifyPlaceWhdayAM.whdayDatetimeTextField.getId(), "allow_null": false, "custom_error_message": null});
    ////////////////////////////////////////////////////////////////////////////
    // 检查参数。
    ////////////////////////////////////////////////////////////////////////////
    for (let i = 0; i < parameterCheckArray.length; i++) {
      const parameterObj = parameterCheckArray[i];
      if (!Module.checkParameter(source.placeWhdayRule, "modifyPlaceWhday", parameterObj, source, function error(source, errorMessage) {
        source.modifyPlaceWhdayAM.formList.showPrompt(parameterObj.id, errorMessage);
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
    // 修改地点工作休息日。
    ////////////////////////////////////////////////////////////////////////////
    Network.request(Network.RequestType.POST, Network.ResponseType.JSON, [{"Content-Type": "application/x-www-form-urlencoded"}],
      Configure.getServerUrl() + "/module/supervision.spot.PlaceWhday/modifyPlaceWhday", parameterArray, source,
      function loadStart(xhr, xhrEvent, source) {
        source.modifyPlaceWhdayAM.formList.hideResultInfo(); // 隐藏结果信息。
        source.modifyPlaceWhdayAM.frozenControl("modifyPlaceWhday"); // 冻结控件。
      },
      function error(xhr, xhrEvent, source) {
        source.modifyPlaceWhdayAM.formList.showResultInfo("error", "网络请求失败");
      },
      function timeout(xhr, xhrEvent, source) {
        source.modifyPlaceWhdayAM.formList.showResultInfo("error", "网络请求超时");
      },
      function readyStateChange(xhr, xhrEvent, source) {
        if ((XMLHttpRequest.DONE == xhr.readyState) && (200 == xhr.status)) {
          source.modifyPlaceWhdayAM.recoverControl("modifyPlaceWhday"); // 恢复控件。
          //////////////////////////////////////////////////////////////////////
          // 响应结果。
          //////////////////////////////////////////////////////////////////////
          const responseResult = xhr.response;
          if (Toolkit.stringEqualsIgnoreCase("SUCCESS", responseResult.status)) {
            ////////////////////////////////////////////////////////////////////
            // 显示成功信息。
            ////////////////////////////////////////////////////////////////////
            source.modifyPlaceWhdayAM.formList.showResultInfo("success", "修改成功");
            ////////////////////////////////////////////////////////////////////
            // 重置控件（修改功能在成功修改之后，不需要重置控件内容，这里不用但
            // 做保留）。
            ////////////////////////////////////////////////////////////////////
            // source.modifyPlaceWhdayAM.resetControl();
            ////////////////////////////////////////////////////////////////////
            // 获取地点工作休息日。
            ////////////////////////////////////////////////////////////////////
            source.getPlaceWhday();
          } else if (Toolkit.stringEqualsIgnoreCase("ERROR", responseResult.status)) {
            source.modifyPlaceWhdayAM.formList.showResultInfo("error", responseResult.attach);
          } else {
            source.modifyPlaceWhdayAM.formList.showResultInfo("error", "操作异常");
          }
        }
      }
    );
  }

  /**
   * 修改地点工作休息日AM取消按钮click事件
   * @param event 事件对象
   */
  modifyPlaceWhdayAMCancelButtonClickEvent(event) {
    ////////////////////////////////////////////////////////////////////////////
    // 获取调用源。
    ////////////////////////////////////////////////////////////////////////////
    const source = event.data;
    source.modifyPlaceWhdayAM.hide();
  }

  /**
   * 地点工作休息日表格修改按钮click事件
   * @param event 事件对象
   */
  placeWhdayTableModifyButtonClickEvent(event) {
    ////////////////////////////////////////////////////////////////////////////
    // 获取调用源。
    ////////////////////////////////////////////////////////////////////////////
    const source = event.data;
    ////////////////////////////////////////////////////////////////////////////
    // 获取待修改数据的uuid。
    ////////////////////////////////////////////////////////////////////////////
    const uuid = $(this).parent().attr("data-uuid");
    ////////////////////////////////////////////////////////////////////////////
    // 从地点工作休息日数组中获取该uuid对应的数据。
    ////////////////////////////////////////////////////////////////////////////
    let obj = null;
    for (let i = 0; i < source.placeWhdayArray.length; i++) {
      if (uuid == source.placeWhdayArray[i].uuid) {
        obj = source.placeWhdayArray[i];
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
      source.modifyPlaceWhdayAM.uuidTextField.getObject().val(obj.uuid);
      //////////////////////////////////////////////////////////////////////////
      // 加载类型。
      //////////////////////////////////////////////////////////////////////////
      source.modifyPlaceWhdayAM.typeSelect.getObject().children("option").each(function() {
        if (Toolkit.stringEqualsIgnoreCase($(this).val(), obj.type)) {
          $(this).prop("selected", "selected");
        } else {
          $(this).removeProp("selected");
        }
      });
      //////////////////////////////////////////////////////////////////////////
      // 加载工作休息日时间。
      //////////////////////////////////////////////////////////////////////////
      source.modifyPlaceWhdayAM.whdayDatetimeTextField.getObject().val(obj.whday_datetime);
      //////////////////////////////////////////////////////////////////////////
      // 显示修改地点工作休息日。
      //////////////////////////////////////////////////////////////////////////
      source.modifyPlaceWhdayAM.show();
    }
  }

  /**
   * 地点工作休息日表格删除按钮click事件
   * @param event 事件对象
   */
  placeWhdayTableRemoveButtonClickEvent(event) {
    ////////////////////////////////////////////////////////////////////////////
    // 获取调用源。
    ////////////////////////////////////////////////////////////////////////////
    const source = event.data;
    source.removeDataUuid = $(this).parent().attr("data-uuid");
    source.removeConfirmWindow.show("确认要删除时间吗？");
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
      if (!Module.checkParameter(source.placeWhdayRule, "removePlaceWhday", parameterObj, source, function error(source, errorMessage) {
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
    // 删除地点工作休息日。
    ////////////////////////////////////////////////////////////////////////////
    Network.request(Network.RequestType.POST, Network.ResponseType.JSON, [{"Content-Type": "application/x-www-form-urlencoded"}],
      Configure.getServerUrl() + "/module/supervision.spot.PlaceWhday/removePlaceWhday", parameterArray, source,
      function loadStart(xhr, xhrEvent, source) {
        source.removeConfirmWindow.hideResultInfo(); // 隐藏结果信息。
        source.removeConfirmWindow.frozenControl("removePlaceWhday"); // 冻结控件。
      },
      function error(xhr, xhrEvent, source) {
        source.removeConfirmWindow.showResultInfo("error", "网络请求失败");
      },
      function timeout(xhr, xhrEvent, source) {
        source.removeConfirmWindow.showResultInfo("error", "网络请求超时");
      },
      function readyStateChange(xhr, xhrEvent, source) {
        if ((XMLHttpRequest.DONE == xhr.readyState) && (200 == xhr.status)) {
          source.removeConfirmWindow.recoverControl("removePlaceWhday"); // 恢复控件。
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
            // 获取地点工作休息日。
            ////////////////////////////////////////////////////////////////////
            source.getPlaceWhday();
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
   * 获取地点工作休息日
   */
  getPlaceWhday() {
    ////////////////////////////////////////////////////////////////////////////
    // 参数检查数组。
    ////////////////////////////////////////////////////////////////////////////
    const parameterCheckArray = new Array();
    parameterCheckArray.push({"name": "place_uuid", "value": this.placeUuid, "id": "0", "allow_null": false, "custom_error_message": null});
    ////////////////////////////////////////////////////////////////////////////
    // 检查参数。
    ////////////////////////////////////////////////////////////////////////////
    for (let i = 0; i < parameterCheckArray.length; i++) {
      const parameterObj = parameterCheckArray[i];
      if (!Module.checkParameter(this.placeWhdayRule, "getPlaceWhday", parameterObj, this, function error(source, errorMessage) {
        Error.redirect("../home/error.html", "获取地点工作休息日", errorMessage, window.location.href);
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
    // 获取地点工作休息日。
    ////////////////////////////////////////////////////////////////////////////
    Network.request(Network.RequestType.POST, Network.ResponseType.JSON, [{"Content-Type": "application/x-www-form-urlencoded"}],
      Configure.getServerUrl() + "/module/supervision.spot.PlaceWhday/getPlaceWhday", parameterArray, this,
      function loadStart(xhr, xhrEvent, source) {
        source.frozenControl("getPlaceWhday"); // 冻结控件。
        source.waitMask.show(); // 显示等待遮蔽。
      },
      function error(xhr, xhrEvent, source) {
        Error.redirect("../home/error.html", "获取地点工作休息日", "网络请求失败", window.location.href);
      },
      function timeout(xhr, xhrEvent, source) {
        Error.redirect("../home/error.html", "获取地点工作休息日", "网络请求超时", window.location.href);
      },
      function readyStateChange(xhr, xhrEvent, source) {
        if ((XMLHttpRequest.DONE == xhr.readyState) && (200 == xhr.status)) {
          source.recoverControl("getPlaceWhday"); // 恢复控件。
          source.waitMask.hide(); // 隐藏等待遮蔽。
          //////////////////////////////////////////////////////////////////////
          // 响应结果。
          //////////////////////////////////////////////////////////////////////
          const responseResult = xhr.response;
          if (Toolkit.stringEqualsIgnoreCase("SUCCESS", responseResult.status)) {
            ////////////////////////////////////////////////////////////////////
            // 清空数组。
            ////////////////////////////////////////////////////////////////////
            source.placeWhdayArray.splice(0, source.placeWhdayArray.length);
            ////////////////////////////////////////////////////////////////////
            // 更新数组。
            ////////////////////////////////////////////////////////////////////
            for (let i = 0; i < responseResult.content.array.length; i++) {
              source.placeWhdayArray.push(responseResult.content.array[i]);
            }
            ////////////////////////////////////////////////////////////////////
            // 恢复控件。
            ////////////////////////////////////////////////////////////////////
            source.recoverControl("getPlaceWhday");
            ////////////////////////////////////////////////////////////////////
            // 如果地点工作休息日数组不为空，则添加表格数据。
            ////////////////////////////////////////////////////////////////////
            if (0 < source.placeWhdayArray.length) {
              let code = "";
              for (let i = 0; i < source.placeWhdayArray.length; i++) {
                const placeWhday = source.placeWhdayArray[i];
                let type = "";
                ////////////////////////////////////////////////////////////////
                // 根据显示要求优化数据。
                ////////////////////////////////////////////////////////////////
                if (Toolkit.stringEqualsIgnoreCase(placeWhday.type, "ON")) {
                  type = "工作";
                } else {
                  type = "休息";
                }
                code += `
                  <tr>
                    <td class = "place">${placeWhday.full_name}</td>
                    <td class = "datetime">${placeWhday.whday_datetime}</td>
                    <td class = "type">${type}</td>
                    <td class = "operation" data-uuid = "${placeWhday.uuid}"><span class = "modify">修改</span><span class = "remove">删除</span></td>
                  </tr>
                `;
              }
              source.placeWhdayTable.getObject().find("tbody").html(code);
            } else {
              source.placeWhdayTable.getObject().find("tbody").html(`
                <tr>
                  <td class = "rowspan" colspan = "4">尚无数据</td>
                </tr>
              `);
            }
            ////////////////////////////////////////////////////////////////////
            // 加载完成后注册事件。
            ////////////////////////////////////////////////////////////////////
            source.placeWhdayTable.getObject().find("tbody").find("tr").find(".operation").find(".modify").off("click").on("click", null, source, source.placeWhdayTableModifyButtonClickEvent);
            source.placeWhdayTable.getObject().find("tbody").find("tr").find(".operation").find(".remove").off("click").on("click", null, source, source.placeWhdayTableRemoveButtonClickEvent);
          } else {
            Error.redirect("../home/error.html", "获取地点工作休息日", responseResult.attach, window.location.href);
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
    this.returnListButton.getObject().attr("disabled", "disabled");
    this.addPlaceWhdayButton.getObject().attr("disabled", "disabled");
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
      this.returnListButton.getObject().removeAttr("disabled");
      this.addPlaceWhdayButton.getObject().removeAttr("disabled");
    }
  }
}
