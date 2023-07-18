"use strict";

class Api {
  /**
   * 构造函数
   */
  constructor() {
    ////////////////////////////////////////////////////////////////////////////
    // 模块类数组
    ////////////////////////////////////////////////////////////////////////////
    this.moduleClassArray = new Array();
    ////////////////////////////////////////////////////////////////////////////
    // 当前模块类数组的序号。
    ////////////////////////////////////////////////////////////////////////////
    this.currentModuleClassArrayIndex = 0;
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
    // 模块方法表格。
    ////////////////////////////////////////////////////////////////////////////
    this.moduleMethodTable = new JSControl("table");
    ////////////////////////////////////////////////////////////////////////////
    // 返回顶部按钮。
    ////////////////////////////////////////////////////////////////////////////
    this.returnTopButton = new JSControl("div");
    ////////////////////////////////////////////////////////////////////////////
    // 等待遮蔽。
    ////////////////////////////////////////////////////////////////////////////
    this.waitMask = new WaitMask();
    ////////////////////////////////////////////////////////////////////////////
    // 容器。
    ////////////////////////////////////////////////////////////////////////////
    this.container.setAttribute(
      {
        "class": "global_scroll global_scroll_dark container"
      }
    );
    ////////////////////////////////////////////////////////////////////////////
    // 模块方法表格。
    ////////////////////////////////////////////////////////////////////////////
    this.moduleMethodTable.setAttribute(
      {
        "class": "global_table module_method_table"
      }
    );
    const newWindowHref = window.location.href;
    const tableHead = `
      <tr>
        <td class = "name">名称</td>
        <td class = "description">描述</td>
        <td class = "new_window_open"><a href = "${newWindowHref}" target = "_blank">新窗口打开</a></td>
      </tr>
    `;
    this.moduleMethodTable.setContent(`
      <thead>${tableHead}</thead>
      <tbody>
        <tr>
          <td class = "rowspan" colspan = "3">尚无数据</td>
        </tr>
      </tbody>
    `);
    ////////////////////////////////////////////////////////////////////////////
    // 返回顶部按钮。
    ////////////////////////////////////////////////////////////////////////////
    this.returnTopButton.setAttribute(
      {
        "class": "return_top_button"
      }
    );
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
    this.container.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // 模块方法表格。
    ////////////////////////////////////////////////////////////////////////////
    this.moduleMethodTable.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // 返回顶部按钮。
    ////////////////////////////////////////////////////////////////////////////
    this.returnTopButton.generateCode();
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
    // 容器添加模块方法表格。
    ////////////////////////////////////////////////////////////////////////////
    this.container.getObject().append(this.moduleMethodTable.getCode());
    ////////////////////////////////////////////////////////////////////////////
    // 容器添加返回顶部按钮。
    ////////////////////////////////////////////////////////////////////////////
    this.container.getObject().append(this.returnTopButton.getCode());
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
    // 注册返回顶部按钮的click事件。
    ////////////////////////////////////////////////////////////////////////////
    this.returnTopButton.getObject().off("click").on("click", null, this, this.returnTopButtonClickEvent);
  }

  /**
   * 返回顶部按钮的click事件
   * @param event 事件对象
   */
  returnTopButtonClickEvent(event) {
    const source = event.data;
    source.container.getObject().scrollTop(0);
  }

  /**
   * 获取模块方法
   */
  getModuleMethod() {
    ////////////////////////////////////////////////////////////////////////////
    // 参数数组。
    ////////////////////////////////////////////////////////////////////////////
    const parameterArray = new Array();
    parameterArray.push({"Account-Token": AccountSecurity.getItem("account_token")});
    ////////////////////////////////////////////////////////////////////////////
    // 获取模块接口。
    ////////////////////////////////////////////////////////////////////////////
    Network.request(Network.RequestType.POST, Network.ResponseType.JSON, [{"Content-Type": "application/x-www-form-urlencoded"}],
      Configure.getServerUrl() + "/module/security.ModuleMethod/getModuleMethod", parameterArray, this,
      function loadStart(xhr, xhrEvent, source) {
        source.waitMask.show(); // 显示等待遮蔽。
      },
      function error(xhr, xhrEvent, source) {
        Error.redirect("../home/error.html", "获取模块方法", "网络请求失败", window.location.href);
      },
      function timeout(xhr, xhrEvent, source) {
        Error.redirect("../home/error.html", "获取模块方法", "网络请求超时", window.location.href);
      },
      function readyStateChange(xhr, xhrEvent, source) {
        if ((XMLHttpRequest.DONE == xhr.readyState) && (200 == xhr.status)) {
          source.waitMask.hide(); // 隐藏等待遮蔽。
          //////////////////////////////////////////////////////////////////////
          // 响应结果。
          //////////////////////////////////////////////////////////////////////
          const responseResult = xhr.response;
          if (Toolkit.stringEqualsIgnoreCase("SUCCESS", responseResult.status)) {
            ////////////////////////////////////////////////////////////////////
            // 保存模块类数组。
            ////////////////////////////////////////////////////////////////////
            const moduleMethodArray = responseResult.content.array;
            for (let i = 0; i < moduleMethodArray.length; i++) {
              const moduleMethod = moduleMethodArray[i];
              for (let j = 0; j < moduleMethod.methods.length; j++) {
                const method = moduleMethod.methods[j];
                if (0 >= source.moduleClassArray.length) {
                  source.moduleClassArray.push(method.class_name);
                } else {
                  let isExist = false;
                  for (let m = 0; m < source.moduleClassArray.length; m++) {
                    const moduleClass = source.moduleClassArray[m];
                    if (method.class_name == moduleClass) {
                      isExist = true;
                      break;
                    }
                  }
                  if (!isExist) {
                    source.moduleClassArray.push(method.class_name);
                  }
                }
              }
            }
            ////////////////////////////////////////////////////////////////////
            // 排序模块类数组。
            ////////////////////////////////////////////////////////////////////
            source.moduleClassArray.sort();
            ////////////////////////////////////////////////////////////////////
            // 获取模块注解。
            ////////////////////////////////////////////////////////////////////
            source.getModuleAnnotation();
          } else {
            Error.redirect("../home/error.html", responseResult.content, responseResult.attach, window.location.href);
          }
        }
      }
    );
  }

  /**
   * 获取模块注解
   */
  getModuleAnnotation() {
    ////////////////////////////////////////////////////////////////////////////
    // 获取接口文档。
    ////////////////////////////////////////////////////////////////////////////
    Network.request(Network.RequestType.GET, Network.ResponseType.JSON, [{"Content-Type": "application/x-www-form-urlencoded"}],
      Configure.getServerUrl() + "/annotation/" + this.moduleClassArray[this.currentModuleClassArrayIndex], null, this,
      function loadStart(xhr, xhrEvent, source) {
        source.waitMask.show(); // 显示等待遮蔽。
      },
      function error(xhr, xhrEvent, source) {
        Error.redirect("../home/error.html", "获取模块文档", "网络请求失败", window.location.href);
      },
      function timeout(xhr, xhrEvent, source) {
        Error.redirect("../home/error.html", "获取模块文档", "网络请求超时", window.location.href);
      },
      function readyStateChange(xhr, xhrEvent, source) {
        if ((XMLHttpRequest.DONE == xhr.readyState) && (200 == xhr.status)) {
          source.waitMask.hide(); // 隐藏等待遮蔽。
          //////////////////////////////////////////////////////////////////////
          // 响应结果。
          //////////////////////////////////////////////////////////////////////
          const responseResult = xhr.response;
          if (Toolkit.stringEqualsIgnoreCase("SUCCESS", responseResult.status)) {
            const moduleAnnotation = responseResult.content;
            let tbodyCode = "";
            if (0 >= source.currentModuleClassArrayIndex) {
              //////////////////////////////////////////////////////////////////
              // 尚无数据，直接新增。
              //////////////////////////////////////////////////////////////////
              tbodyCode = `
                <tr class = "module">
                  <td colspan = "3">${moduleAnnotation.description}</td>
                </tr>
              `;
            } else {
              //////////////////////////////////////////////////////////////////
              // 已有数据，向下添加。
              //////////////////////////////////////////////////////////////////
              tbodyCode = source.moduleMethodTable.getObject().find("tbody").html();
              tbodyCode += `
                <tr class = "module">
                  <td colspan = "3">${moduleAnnotation.description}</td>
                </tr>
              `;
            }
            ////////////////////////////////////////////////////////////////////
            // 根据方法名称重新排序。
            ////////////////////////////////////////////////////////////////////
            moduleAnnotation.methods.sort(function(m1, m2) {
              return m1.name.localeCompare(m2.name);
            });
            ////////////////////////////////////////////////////////////////////
            // 遍历方法生成接口列表。
            ////////////////////////////////////////////////////////////////////
            for (let i = 0; i < moduleAnnotation.methods.length; i++) {
              const method = moduleAnnotation.methods[i];
              tbodyCode += `
                <tr class = "interface">
                  <td class = "interface_name"><a href = "#${moduleAnnotation.class_name}_${method.name}">${method.name}</a></td><td colspan = "2">${method.description}</td>
                </tr>
              `;
            }
            source.moduleMethodTable.getObject().find("tbody").html(tbodyCode);
            ////////////////////////////////////////////////////////////////////
            // 遍历方法生成接口数据。
            ////////////////////////////////////////////////////////////////////
            for (let i = 0; i < moduleAnnotation.methods.length; i++) {
              const method = moduleAnnotation.methods[i];
              let anonymousAccessCode = "";
              if (method.anonymous_access) {
                anonymousAccessCode = "是";
              } else {
                anonymousAccessCode = "否";
              }
              let frequencysCode = "";
              for (let j = 0; j < method.frequencys.length; j++) {
                const frequency = method.frequencys[j];
                frequencysCode += `${frequency.source}:${frequency.count}/${frequency.unit};`;
              }
              frequencysCode = frequencysCode.substring(0, frequencysCode.length - 1);
              const requestUrl = Configure.getServerUrl() + "/module/" + moduleAnnotation.class_name + "/" + method.name;
              let requestParameterCode = `<tr><td class = "rowspan value" colspan = "7">无</td></tr>`;
              if ((Toolkit.isJSONObjectExistKey(method, "parameters")) && (0 < method.parameters.length)) {
                requestParameterCode = "";
                for (let j = 0; j < method.parameters.length; j++) {
                  const parameter = method.parameters[j];
                  let allowNullCode = "";
                  if (parameter.allow_null) {
                    allowNullCode = "否";
                  } else {
                    allowNullCode = "是";
                  }
                  requestParameterCode += `
                    <tr>
                      <td class = "value">${parameter.name}</td>
                      <td class = "value">${parameter.text}</td>
                      <td class = "value">${parameter.type}</td>
                      <td class = "value">${allowNullCode}</td>
                      <td class = "value format">${parameter.format}</td>
                      <td class = "value format_prompt">${parameter.format_prompt}</td>
                      <td class = "value remark">${parameter.remark}</td>
                    </tr>
                  `;
                }
              }
              let returnDataCode = `<tr><td class = "rowspan value" colspan = "4">无</td></tr>`;
              if ((Toolkit.isJSONObjectExistKey(method, "returns")) && (0 < method.returns.length)) {
                returnDataCode = "";
                const indent = {};
                for (let j = 0; j < method.returns.length; j++) {
                  const returnObj = method.returns[j];
                  if ((Toolkit.isJSONObjectExistKey(returnObj, "parent_id")) && (0 < returnObj.parent_id.length)) {
                    // 子级
                    indent[returnObj.id] = indent[returnObj.parent_id] + 15;
                  } else {
                    // 顶级
                    indent[returnObj.id] = 0;
                  }
                }
                for (let j = 0; j < method.returns.length; j++) {
                  const returnObj = method.returns[j];
                  let isNecessaryCode = "";
                  if (Toolkit.isJSONObjectExistKey(returnObj, "is_necessary")) {
                    if (returnObj.is_necessary) {
                      isNecessaryCode = "是";
                    } else {
                      isNecessaryCode = "否";
                    }
                  }
                  returnDataCode += `
                    <tr>
                      <td class = "value" data-indent = "${indent[returnObj.id]}">${returnObj.name}</td>
                      <td class = "value">${returnObj.type}</td>
                      <td class = "value">${isNecessaryCode}</td>
                      <td class = "value">${returnObj.description}</td>
                    </tr>
                  `;
                }
              }
              const tableCode = `
                <table id = "${moduleAnnotation.class_name}_${method.name}" class="global_table method_table base_information">
                  <thead><tr><td colspan = "10">基本信息</td></tr></thead>
                  <tbody>
                    <tr>
                      <td class = "field">名称</td>
                      <td class = "value method_name">${method.name}</td>
                      <td class = "field">描述</td>
                      <td class = "value">${method.description}</td>
                      <td class = "field">匿名访问</td>
                      <td class = "value anonymous_access">${anonymousAccessCode}</td>
                      <td class = "field">频次限制</td>
                      <td class = "value">${frequencysCode}</td>
                      <td class = "field">请求类型</td>
                      <td class = "value method_type">${method.method_type}</td>
                    </tr>
                    <tr>
                      <td class = "field">请求地址</td><td class = "value" colspan = "9">${requestUrl}</td>
                    </tr>
                  </tbody>
                </table>
                <table class="global_table method_table request_parameter">
                  <thead><tr><td colspan = "7">请求参数</td></tr></thead>
                  <tbody>
                    <tr>
                      <td class = "field">名称</td>
                      <td class = "field">字段</td>
                      <td class = "field">类型</td>
                      <td class = "field">是否必填</td>
                      <td class = "field">校验正则</td>
                      <td class = "field">校验描述</td>
                      <td class = "field">备注</td>
                    </tr>
                    ${requestParameterCode}
                  </tbody>
                </table>
                <table class="global_table method_table return_data">
                  <thead><tr><td colspan = "4">返回数据</td></tr></thead>
                  <tbody>
                    <tr>
                      <td class = "field">名称</td><td class = "field">类型</td><td class = "field">是否必填</td><td class = "field">描述</td>
                    </tr>
                    ${returnDataCode}
                  </tbody>
                </table>
              `;
              source.container.getObject().append(tableCode);
              $(".return_data").find("tbody").find("tr").find("[data-indent]").each(function() {
                const a = parseInt($(this).css("padding-left"));
                const b = parseInt($(this).attr("data-indent"));
                const c = a + b;
                const paddingLeft = parseInt($(this).css("padding-left")) + parseInt($(this).attr("data-indent"));
                $(this).css("padding-left", paddingLeft + "px");
                $(this).removeAttr("data-indent");
              });
            }
            ////////////////////////////////////////////////////////////////////
            // 如果没有遍历完模块类数组，则增加当前模块类数组的序号继续遍历。
            ////////////////////////////////////////////////////////////////////
            source.currentModuleClassArrayIndex++;
            if (source.currentModuleClassArrayIndex < source.moduleClassArray.length) {
              source.getModuleAnnotation();
            }
          } else {
            Error.redirect("../home/error.html", responseResult.content, responseResult.attach, window.location.href);
          }
        }
      }
    );
  }
}
