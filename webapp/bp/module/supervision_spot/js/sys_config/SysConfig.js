"use strict";

class SystemConfig {
  /**
   * 构造函数
   *
   * @param ruleArray 规则数组
   */
  constructor(ruleArray) {
    ////////////////////////////////////////////////////////////////////////////
    // 系统配置规则。
    ////////////////////////////////////////////////////////////////////////////
    this.sysConfigRule = ruleArray[0];
    ////////////////////////////////////////////////////////////////////////////
    // 队列。
    ////////////////////////////////////////////////////////////////////////////
    this.queue = new Queue();
    ////////////////////////////////////////////////////////////////////////////
    // 等待遮蔽。
    ////////////////////////////////////////////////////////////////////////////
    this.waitMask = new WaitMask();
    ////////////////////////////////////////////////////////////////////////////
    // 个人问题触发报告次数。
    ////////////////////////////////////////////////////////////////////////////
    this.personProblemTriggerReportCount = -1;
    ////////////////////////////////////////////////////////////////////////////
    // 个人初始分值。
    ////////////////////////////////////////////////////////////////////////////
    this.personInitScoreNum = -1;
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
    // 修改个人问题触发报告次数。
    ////////////////////////////////////////////////////////////////////////////
    this.personProblemTrigger = new PersonProblemTrigger(this, "设置", 40);
    ////////////////////////////////////////////////////////////////////////////
    // 修改个人初始分值。
    ////////////////////////////////////////////////////////////////////////////
    this.personInitScore = new PersonInitScore(this, "设置", 40);
    ////////////////////////////////////////////////////////////////////////////
    // 等待遮蔽。
    ////////////////////////////////////////////////////////////////////////////
    this.waitMask.setAttribute(
      {
        "class": "global_wait_mask"
      }
    );
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
          <td class = "title" colspan = "2">问题触发</td>
        </tr>
        <tr>
          <td>个人问题触发报告次数</td>
          <td class = "operation"><span class = "person_problem_trigger">修改</span></td>
        </tr>
        <tr>
          <td>个人初始分值</td>
          <td class = "operation"><span class = "person_init_score">修改</span></td>
        </tr>
      </tbody>
    `);
    ////////////////////////////////////////////////////////////////////////////
    // 修改个人问题触发报告次数。
    ////////////////////////////////////////////////////////////////////////////
    this.personProblemTrigger.setClassSign("person_problem_trigger");
    ////////////////////////////////////////////////////////////////////////////
    // 修改个人初始分值。
    ////////////////////////////////////////////////////////////////////////////
    this.personInitScore.setClassSign("person_init_score");
    ////////////////////////////////////////////////////////////////////////////
    // 容器。
    ////////////////////////////////////////////////////////////////////////////
    this.container.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // 配置表格。
    ////////////////////////////////////////////////////////////////////////////
    this.configTable.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // 修改个人问题触发报告次数。
    ////////////////////////////////////////////////////////////////////////////
    this.personProblemTrigger.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // 修改个人初始分值。
    ////////////////////////////////////////////////////////////////////////////
    this.personInitScore.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // 等待遮蔽。
    ////////////////////////////////////////////////////////////////////////////
    this.waitMask.generateCode();
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
    // 容器添加修改个人问题触发报告次数。
    ////////////////////////////////////////////////////////////////////////////
    this.container.getObject().append(this.personProblemTrigger.getCode());
    ////////////////////////////////////////////////////////////////////////////
    // 容器添加修改个人初始分值。
    ////////////////////////////////////////////////////////////////////////////
    this.container.getObject().append(this.personInitScore.getCode());
    ////////////////////////////////////////////////////////////////////////////
    // 容器添加等待遮蔽。
    ////////////////////////////////////////////////////////////////////////////
    this.container.getObject().append(this.waitMask.getCode());
  }

  /**
   * 初始化事件
   */
  initEvent() {
    ////////////////////////////////////////////////////////////////////////////
    // 注册配置表格中修改个人问题触发报告次数的click事件。
    ////////////////////////////////////////////////////////////////////////////
    this.configTable.getObject().find("tbody").find("tr").find(".operation").find(".person_problem_trigger").off("click").on("click", null, this, this.configTablePersonProblemTriggerClickEvent);
    ////////////////////////////////////////////////////////////////////////////
    // 注册配置表格中修改个人初始分值的click事件。
    ////////////////////////////////////////////////////////////////////////////
    this.configTable.getObject().find("tbody").find("tr").find(".operation").find(".person_init_score").off("click").on("click", null, this, this.configTablePersonInitScoreClickEvent);
    ////////////////////////////////////////////////////////////////////////////
    // 初始化修改个人问题触发报告次数事件。
    ////////////////////////////////////////////////////////////////////////////
    this.personProblemTrigger.initEvent();
    ////////////////////////////////////////////////////////////////////////////
    // 初始化修改个人初始分值。
    ////////////////////////////////////////////////////////////////////////////
    this.personInitScore.initEvent();
    ////////////////////////////////////////////////////////////////////////////
    // 注册修改个人问题触发报告次数确认按钮的click事件。
    ////////////////////////////////////////////////////////////////////////////
    this.personProblemTrigger.formList.confirmButton.getObject().off("click").on("click", null, this, this.personProblemTriggerConfirmButtonClickEvent);
    ////////////////////////////////////////////////////////////////////////////
    // 注册修改个人问题触发报告次数取消按钮的click事件。
    ////////////////////////////////////////////////////////////////////////////
    this.personProblemTrigger.formList.cancelButton.getObject().off("click").on("click", null, this, this.personProblemTriggerCancelButtonClickEvent);
    ////////////////////////////////////////////////////////////////////////////
    // 注册修改个人初始分值确认按钮的click事件。
    ////////////////////////////////////////////////////////////////////////////
    this.personInitScore.formList.confirmButton.getObject().off("click").on("click", null, this, this.personInitScoreConfirmButtonClickEvent);
    ////////////////////////////////////////////////////////////////////////////
    // 注册修改个人初始分值取消按钮的click事件。
    ////////////////////////////////////////////////////////////////////////////
    this.personInitScore.formList.cancelButton.getObject().off("click").on("click", null, this, this.personInitScoreCancelButtonClickEvent);
  }

  /**
   * 配置表格中修改个人问题触发报告次数click事件
   * @param event 事件对象
   */
  configTablePersonProblemTriggerClickEvent(event) {
    ////////////////////////////////////////////////////////////////////////////
    // 获取调用源。
    ////////////////////////////////////////////////////////////////////////////
    const source = event.data;
    ////////////////////////////////////////////////////////////////////////////
    // 加载个人问题触发报告次数。
    ////////////////////////////////////////////////////////////////////////////
    source.personProblemTrigger.personProblemTriggerReportCountTextField.getObject().val(source.personProblemTriggerReportCount);
    ////////////////////////////////////////////////////////////////////////////
    // 显示修改个人问题触发报告次数。
    ////////////////////////////////////////////////////////////////////////////
    source.personProblemTrigger.show();
  }

  /**
   * 配置表格中修改个人初始分值click事件
   * @param event 事件对象
   */
  configTablePersonInitScoreClickEvent(event) {
    ////////////////////////////////////////////////////////////////////////////
    // 获取调用源。
    ////////////////////////////////////////////////////////////////////////////
    const source = event.data;
    ////////////////////////////////////////////////////////////////////////////
    // 加载个人初始分值。
    ////////////////////////////////////////////////////////////////////////////
    source.personInitScore.personInitScoreTextField.getObject().val(source.personInitScoreNum);
    ////////////////////////////////////////////////////////////////////////////
    // 显示修改个人初始分值。
    ////////////////////////////////////////////////////////////////////////////
    source.personInitScore.show();
  }

  /**
   * 修改个人问题触发报告次数确认按钮click事件
   * @param event 事件对象
   */
  personProblemTriggerConfirmButtonClickEvent(event) {
    ////////////////////////////////////////////////////////////////////////////
    // 获取调用源。
    ////////////////////////////////////////////////////////////////////////////
    const source = event.data;
    ////////////////////////////////////////////////////////////////////////////
    // 校验数据之前，先要隐藏之前的提示。
    ////////////////////////////////////////////////////////////////////////////
    source.personProblemTrigger.formList.hideAllPrompt();
    ////////////////////////////////////////////////////////////////////////////
    // 参数检查数组。
    ////////////////////////////////////////////////////////////////////////////
    const parameterCheckArray = new Array();
    parameterCheckArray.push({"name": "person_problem_trigger_report_count", "value": source.personProblemTrigger.personProblemTriggerReportCountTextField.getObject().val(), "id": source.personProblemTrigger.personProblemTriggerReportCountTextField.getId(), "allow_null": false, "custom_error_message": null});
    ////////////////////////////////////////////////////////////////////////////
    // 检查参数。
    ////////////////////////////////////////////////////////////////////////////
    for (let i = 0; i < parameterCheckArray.length; i++) {
      const parameterObj = parameterCheckArray[i];
      if (!Module.checkParameter(source.sysConfigRule, "modifySystemConfig", parameterObj, source, function error(source, errorMessage) {
        source.personProblemTrigger.formList.showPrompt(parameterObj.id, errorMessage);
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
    // 修改个人问题触发报告次数。
    ////////////////////////////////////////////////////////////////////////////
    Network.request(Network.RequestType.POST, Network.ResponseType.JSON, [{"Content-Type": "application/x-www-form-urlencoded"}],
      Configure.getServerUrl() + "/module/supervision.spot.SystemConfig/modifySystemConfig", parameterArray, source,
      function loadStart(xhr, xhrEvent, source) {
        source.personProblemTrigger.formList.hideResultInfo(); // 隐藏结果信息。
        source.personProblemTrigger.frozenControl("modifySystemConfig"); // 冻结控件。
      },
      function error(xhr, xhrEvent, source) {
        source.personProblemTrigger.formList.showResultInfo("error", "网络请求失败");
      },
      function timeout(xhr, xhrEvent, source) {
        source.personProblemTrigger.formList.showResultInfo("error", "网络请求超时");
      },
      function readyStateChange(xhr, xhrEvent, source) {
        if ((XMLHttpRequest.DONE == xhr.readyState) && (200 == xhr.status)) {
          source.personProblemTrigger.recoverControl("modifySystemConfig"); // 恢复控件。
          //////////////////////////////////////////////////////////////////////
          // 响应结果。
          //////////////////////////////////////////////////////////////////////
          const responseResult = xhr.response;
          if (Toolkit.stringEqualsIgnoreCase("SUCCESS", responseResult.status)) {
            ////////////////////////////////////////////////////////////////////
            // 显示成功信息。
            ////////////////////////////////////////////////////////////////////
            source.personProblemTrigger.formList.showResultInfo("success", "修改成功");
            ////////////////////////////////////////////////////////////////////
            // 重置控件（修改功能在成功修改之后，不需要重置控件内容，这里不用但
            // 做保留）。
            ////////////////////////////////////////////////////////////////////
            // source.personProblemTrigger.resetControl();
            ////////////////////////////////////////////////////////////////////
            // 获取系统配置。
            ////////////////////////////////////////////////////////////////////
            source.getSystemConfig();
          } else if (Toolkit.stringEqualsIgnoreCase("ERROR", responseResult.status)) {
            source.personProblemTrigger.formList.showResultInfo("error", responseResult.attach);
          } else {
            source.personProblemTrigger.formList.showResultInfo("error", "操作异常");
          }
        }
      }
    );
  }

  /**
   * 修改个人问题触发报告次数取消按钮click事件
   * @param event 事件对象
   */
  personProblemTriggerCancelButtonClickEvent(event) {
    ////////////////////////////////////////////////////////////////////////////
    // 获取调用源。
    ////////////////////////////////////////////////////////////////////////////
    const source = event.data;
    source.personProblemTrigger.hide();
  }

  /**
   * 修改个人初始分值确认按钮click事件
   * @param event 事件对象
   */
  personInitScoreConfirmButtonClickEvent(event) {
    ////////////////////////////////////////////////////////////////////////////
    // 获取调用源。
    ////////////////////////////////////////////////////////////////////////////
    const source = event.data;
    ////////////////////////////////////////////////////////////////////////////
    // 校验数据之前，先要隐藏之前的提示。
    ////////////////////////////////////////////////////////////////////////////
    source.personInitScore.formList.hideAllPrompt();
    ////////////////////////////////////////////////////////////////////////////
    // 参数检查数组。
    ////////////////////////////////////////////////////////////////////////////
    const parameterCheckArray = new Array();
    parameterCheckArray.push({"name": "person_init_score", "value": source.personInitScore.personInitScoreTextField.getObject().val(), "id": source.personInitScore.personInitScoreTextField.getId(), "allow_null": false, "custom_error_message": null});
    ////////////////////////////////////////////////////////////////////////////
    // 检查参数。
    ////////////////////////////////////////////////////////////////////////////
    for (let i = 0; i < parameterCheckArray.length; i++) {
      const parameterObj = parameterCheckArray[i];
      if (!Module.checkParameter(source.sysConfigRule, "modifySystemConfig", parameterObj, source, function error(source, errorMessage) {
        source.personInitScore.formList.showPrompt(parameterObj.id, errorMessage);
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
    // 修改个人初始分值。
    ////////////////////////////////////////////////////////////////////////////
    Network.request(Network.RequestType.POST, Network.ResponseType.JSON, [{"Content-Type": "application/x-www-form-urlencoded"}],
      Configure.getServerUrl() + "/module/supervision.spot.SystemConfig/modifySystemConfig", parameterArray, source,
      function loadStart(xhr, xhrEvent, source) {
        source.personInitScore.formList.hideResultInfo(); // 隐藏结果信息。
        source.personInitScore.frozenControl("modifySystemConfig"); // 冻结控件。
      },
      function error(xhr, xhrEvent, source) {
        source.personInitScore.formList.showResultInfo("error", "网络请求失败");
      },
      function timeout(xhr, xhrEvent, source) {
        source.personInitScore.formList.showResultInfo("error", "网络请求超时");
      },
      function readyStateChange(xhr, xhrEvent, source) {
        if ((XMLHttpRequest.DONE == xhr.readyState) && (200 == xhr.status)) {
          source.personInitScore.recoverControl("modifySystemConfig"); // 恢复控件。
          //////////////////////////////////////////////////////////////////////
          // 响应结果。
          //////////////////////////////////////////////////////////////////////
          const responseResult = xhr.response;
          if (Toolkit.stringEqualsIgnoreCase("SUCCESS", responseResult.status)) {
            ////////////////////////////////////////////////////////////////////
            // 显示成功信息。
            ////////////////////////////////////////////////////////////////////
            source.personInitScore.formList.showResultInfo("success", "修改成功");
            ////////////////////////////////////////////////////////////////////
            // 重置控件（修改功能在成功修改之后，不需要重置控件内容，这里不用但
            // 做保留）。
            ////////////////////////////////////////////////////////////////////
            // source.personInitScore.resetControl();
            ////////////////////////////////////////////////////////////////////
            // 获取系统配置。
            ////////////////////////////////////////////////////////////////////
            source.getSystemConfig();
          } else if (Toolkit.stringEqualsIgnoreCase("ERROR", responseResult.status)) {
            source.personInitScore.formList.showResultInfo("error", responseResult.attach);
          } else {
            source.personInitScore.formList.showResultInfo("error", "操作异常");
          }
        }
      }
    );
  }

  /**
   * 修改个人初始分值取消按钮click事件
   * @param event 事件对象
   */
  personInitScoreCancelButtonClickEvent(event) {
    ////////////////////////////////////////////////////////////////////////////
    // 获取调用源。
    ////////////////////////////////////////////////////////////////////////////
    const source = event.data;
    source.personInitScore.hide();
  }

  /**
   * 获取系统配置
   */
  getSystemConfig() {
    ////////////////////////////////////////////////////////////////////////////
    // 获取系统配置。
    ////////////////////////////////////////////////////////////////////////////
    Network.request(Network.RequestType.POST, Network.ResponseType.JSON, [{"Content-Type": "application/x-www-form-urlencoded"}],
      Configure.getServerUrl() + "/module/supervision.spot.SystemConfig/getSystemConfig", [{"Account-Token": AccountSecurity.getItem("account_token")}], this,
      function loadStart(xhr, xhrEvent, source) {
        source.frozenControl("getSystemConfig"); // 冻结控件。
        source.waitMask.show(); // 显示等待遮蔽。
      },
      function error(xhr, xhrEvent, source) {
        Error.redirect("../home/error.html", "获取系统配置", "网络请求失败", window.location.href);
      },
      function timeout(xhr, xhrEvent, source) {
        Error.redirect("../home/error.html", "获取系统配置", "网络请求超时", window.location.href);
      },
      function readyStateChange(xhr, xhrEvent, source) {
        if ((XMLHttpRequest.DONE == xhr.readyState) && (200 == xhr.status)) {
          source.recoverControl("getSystemConfig"); // 恢复控件。
          source.waitMask.hide(); // 隐藏等待遮蔽。 
          //////////////////////////////////////////////////////////////////////
          // 响应结果。
          //////////////////////////////////////////////////////////////////////
          const responseResult = xhr.response;
          if (Toolkit.stringEqualsIgnoreCase("SUCCESS", responseResult.status)) {
            ////////////////////////////////////////////////////////////////////
            // 更新对象。
            ////////////////////////////////////////////////////////////////////
            source.personProblemTriggerReportCount = responseResult.content.array[0].person_problem_trigger_report_count;
            source.personInitScoreNum = responseResult.content.array[0].person_init_score;
          } else {
            Error.redirect("../home/error.html", "获取系统配置", responseResult.attach, window.location.href);
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
    }
  }
}