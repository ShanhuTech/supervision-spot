"use strict";

class RolePermission {
  /**
   * 构造函数
   *
   * @param ruleArray 规则数组
   */
  constructor(ruleArray) {
    ////////////////////////////////////////////////////////////////////////////
    // 角色规则。
    ////////////////////////////////////////////////////////////////////////////
    this.roleRule = ruleArray[0];
    ////////////////////////////////////////////////////////////////////////////
    // 角色数组。
    ////////////////////////////////////////////////////////////////////////////
    this.roleArray = new Array();
    ////////////////////////////////////////////////////////////////////////////
    // 模块方法数组。
    ////////////////////////////////////////////////////////////////////////////
    this.moduleMethodArray = new Array();
    ////////////////////////////////////////////////////////////////////////////
    // 队列。
    ////////////////////////////////////////////////////////////////////////////
    this.queue = new Queue();
    ////////////////////////////////////////////////////////////////////////////
    // 保存前的角色名称的数据。
    ////////////////////////////////////////////////////////////////////////////
    this.beforeSaveRoleNameData = {
      "uuid": null,
      "enabled": false,
    };
    ////////////////////////////////////////////////////////////////////////////
    // 提示显示的时间。
    ////////////////////////////////////////////////////////////////////////////
    this.promptShowTime = 1000;
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
    // 角色名称标签。
    ////////////////////////////////////////////////////////////////////////////
    this.roleNameLabel = new JSControl("label");
    ////////////////////////////////////////////////////////////////////////////
    // 角色名称下拉框。
    ////////////////////////////////////////////////////////////////////////////
    this.roleNameSelect = new JSControl("select");
    ////////////////////////////////////////////////////////////////////////////
    // 保存按钮。
    ////////////////////////////////////////////////////////////////////////////
    this.saveButton = new JSControl("button");
    ////////////////////////////////////////////////////////////////////////////
    // 提示。
    ////////////////////////////////////////////////////////////////////////////
    this.prompt = new Prompt();
    ////////////////////////////////////////////////////////////////////////////
    // 模块方法表格。
    ////////////////////////////////////////////////////////////////////////////
    this.moduleMethodTable = new JSControl("table");
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
    // 工具栏。
    ////////////////////////////////////////////////////////////////////////////
    this.toolbar.setAttribute(
      {
        "class": "tool_bar"
      }
    );
    ////////////////////////////////////////////////////////////////////////////
    // 角色名称标签。
    ////////////////////////////////////////////////////////////////////////////
    this.roleNameLabel.setAttribute(
      {
        "class": "global_label role_name_label"
      }
    );
    this.roleNameLabel.setContent("角色名称");
    ////////////////////////////////////////////////////////////////////////////
    // 角色名称下拉框。
    ////////////////////////////////////////////////////////////////////////////
    this.roleNameSelect.setAttribute(
      {
        "class": "global_select role_name_select"
      }
    );
    ////////////////////////////////////////////////////////////////////////////
    // 保存按钮。
    ////////////////////////////////////////////////////////////////////////////
    this.saveButton.setAttribute(
      {
        "class": "global_button_primary save_button"
      }
    );
    this.saveButton.setContent("保存");
    ////////////////////////////////////////////////////////////////////////////
    // 提示。
    ////////////////////////////////////////////////////////////////////////////
    this.prompt.setAttribute(
      {
        "class": "global_prompt prompt"
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
    this.moduleMethodTable.setContent(`
      <tbody>
        <tr>
          <td class = "rowspan" colspan = "5">尚无数据</td>
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
    // 容器。
    ////////////////////////////////////////////////////////////////////////////
    this.container.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // 工具栏。
    ////////////////////////////////////////////////////////////////////////////
    this.toolbar.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // 角色名称标签。
    ////////////////////////////////////////////////////////////////////////////
    this.roleNameLabel.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // 角色名称下拉框。
    ////////////////////////////////////////////////////////////////////////////
    this.roleNameSelect.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // 保存按钮。
    ////////////////////////////////////////////////////////////////////////////
    this.saveButton.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // 提示。
    ////////////////////////////////////////////////////////////////////////////
    this.prompt.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // 模块方法表格。
    ////////////////////////////////////////////////////////////////////////////
    this.moduleMethodTable.generateCode();
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
    // 容器添加工具栏。
    ////////////////////////////////////////////////////////////////////////////
    this.container.getObject().append(this.toolbar.getCode());
    ////////////////////////////////////////////////////////////////////////////
    // 工具栏添加角色名称标签。
    ////////////////////////////////////////////////////////////////////////////
    this.toolbar.getObject().append(this.roleNameLabel.getCode());
    ////////////////////////////////////////////////////////////////////////////
    // 工具栏添加角色名称下拉框。
    ////////////////////////////////////////////////////////////////////////////
    this.toolbar.getObject().append(this.roleNameSelect.getCode());
    ////////////////////////////////////////////////////////////////////////////
    // 工具栏添加保存按钮。
    ////////////////////////////////////////////////////////////////////////////
    this.toolbar.getObject().append(this.saveButton.getCode());
    ////////////////////////////////////////////////////////////////////////////
    // 容器添加模块方法表格。
    ////////////////////////////////////////////////////////////////////////////
    this.container.getObject().append(this.moduleMethodTable.getCode());
    ////////////////////////////////////////////////////////////////////////////
    // 容器添加提示。
    ////////////////////////////////////////////////////////////////////////////
    this.container.getObject().append(this.prompt.getCode());
    ////////////////////////////////////////////////////////////////////////////
    // 容器添加等待遮蔽。
    ////////////////////////////////////////////////////////////////////////////
    this.container.getObject().append(this.waitMask.getCode());
  }

  /**
   * 初始化布局
   */
  initLayout() {
    ////////////////////////////////////////////////////////////////////////////
    // 设置提示的宽度和绝对位置。这里没有必要将样式提取至css，直接写入计算即可。
    ////////////////////////////////////////////////////////////////////////////
    const saveButtonTop = this.saveButton.getObject().offset().top;
    const saveButtonLeft = this.saveButton.getObject().offset().left;
    const saveButtonWidth = Toolkit.getDomElementRect(this.saveButton.getObject().get(0)).width;
    const saveButtonHeight = this.saveButton.getObject().get(0).height;
    this.prompt.getObject().css("padding-top", "0rem");
    this.prompt.getObject().css("padding-bottom", "0rem");
    this.prompt.getObject().css("height", saveButtonHeight + "px");
    this.prompt.getObject().css("top", saveButtonTop + "px");
    this.prompt.getObject().css("left", saveButtonLeft + saveButtonWidth + 15 + "px");
  }

  /**
   * 初始化事件
   */
  initEvent() {
    ////////////////////////////////////////////////////////////////////////////
    // 注册角色名称下拉框的change事件。
    ////////////////////////////////////////////////////////////////////////////
    this.roleNameSelect.getObject().off("change").on("change", null, this, this.roleNameSelectChangeEvent);
    ////////////////////////////////////////////////////////////////////////////
    // 注册保存按钮的click事件。
    ////////////////////////////////////////////////////////////////////////////
    this.saveButton.getObject().off("click").on("click", null, this, this.saveButtonClickEvent);
  }

  /**
   * 角色名称下拉框change事件
   * @param event 事件对象
   */
  roleNameSelectChangeEvent(event) {
    ////////////////////////////////////////////////////////////////////////////
    // 获取调用源。
    ////////////////////////////////////////////////////////////////////////////
    const source = event.data;
    ////////////////////////////////////////////////////////////////////////////
    // 清空所有多选框。
    ////////////////////////////////////////////////////////////////////////////
    source.moduleMethodTable.getObject().find("tbody").find("tr").find("td").find(":checkbox").prop("checked", false);
    ////////////////////////////////////////////////////////////////////////////
    // 定义选中值变量。
    ////////////////////////////////////////////////////////////////////////////
    let selectVal = null;
    ////////////////////////////////////////////////////////////////////////////
    // 根据保存前的角色名称的数据是否可用，给选中值变量赋值。
    ////////////////////////////////////////////////////////////////////////////
    if (source.beforeSaveRoleNameData.enabled) {
      selectVal = source.beforeSaveRoleNameData.uuid;
    } else {
      //////////////////////////////////////////////////////////////////////////
      // 获取角色名称下拉框选中值。
      //////////////////////////////////////////////////////////////////////////
      selectVal = source.roleNameSelect.getObject().val();
    }
    if (0 < selectVal.length) {
      let roleObj = null;
      //////////////////////////////////////////////////////////////////////////
      // 根据角色名称下拉框选中的值，获取角色数组对象。
      //////////////////////////////////////////////////////////////////////////
      for (let i = 0; i < source.roleArray.length; i++) {
        if (selectVal == source.roleArray[i].uuid) {
          roleObj = source.roleArray[i];
          break;
        }
      }
      //////////////////////////////////////////////////////////////////////////
      // 如果找到了角色数组对象，则根据对象配置的权限取得当前角色的权限。
      //////////////////////////////////////////////////////////////////////////
      if ((null != roleObj) && (null != roleObj.permissions)) {
        const permissions = roleObj.permissions.split(";");
        for (let i = 0; i < permissions.length; i++) {
          const permission = permissions[i];
          const methodCheckbox = source.moduleMethodTable.getObject().find("tbody").find("tr").find("td").find(`:checkbox[value="${permission}"]`);
          methodCheckbox.prop("checked", true);
          const parentModule = methodCheckbox.attr("data-parent-module");
          const all = source.moduleMethodTable.getObject().find("tbody").find("tr").find("td").find(`:checkbox[data-parent-module="${parentModule}"]`).length;
          const checked = source.moduleMethodTable.getObject().find("tbody").find("tr").find("td").find(`:checkbox[data-parent-module="${parentModule}"]:checked`).length;
          if (all == checked) {
            source.moduleMethodTable.getObject().find("tbody").find("tr").find("td").find(`:checkbox[data-module="${parentModule}"]`).prop("checked", true);
          }
        }
      }
    }
  }

  /**
   * 保存按钮click事件
   * @param event 事件对象
   */
  saveButtonClickEvent(event) {
    ////////////////////////////////////////////////////////////////////////////
    // 获取调用源。
    ////////////////////////////////////////////////////////////////////////////
    const source = event.data;
    ////////////////////////////////////////////////////////////////////////////
    // 获取角色名称下拉框选中值的名称。
    ////////////////////////////////////////////////////////////////////////////
    const selectVal = source.roleNameSelect.getObject().val();
    if (0 < selectVal.length) {
      const checkedList = source.moduleMethodTable.getObject().find("tbody").find("tr").find("td").find(":checkbox:checked");
      if (0 >= checkedList.length) {
        source.prompt.show("error", "至少选择一个菜单", source.promptShowTime);
      } else {
        ////////////////////////////////////////////////////////////////////////
        // 遍历多选框选中项并组成权限参数。
        ////////////////////////////////////////////////////////////////////////
        let permissions = "";
        checkedList.each(function() {
          if ($(this).hasClass("method")) {
            permissions += `${$(this).val()};`;
          }
        });
        ////////////////////////////////////////////////////////////////////////
        // 参数检查数组。
        ////////////////////////////////////////////////////////////////////////
        const parameterCheckArray = new Array();
        parameterCheckArray.push({"name": "uuid", "value": selectVal, "id": source.roleNameSelect.getId(), "allow_null": false, "custom_error_message": null});
        parameterCheckArray.push({"name": "permissions", "value": permissions, "id": "0", "allow_null": false, "custom_error_message": null});
        ////////////////////////////////////////////////////////////////////////
        // 检查参数。
        ////////////////////////////////////////////////////////////////////////
        for (let i = 0; i < parameterCheckArray.length; i++) {
          const parameterObj = parameterCheckArray[i];
          if (!Module.checkParameter(source.roleRule, "modifyRolePermission", parameterObj, source, function error(source, errorMessage) {
            source.prompt.show("error", errorMessage, source.promptShowTime);
          })) {
            return;
          }
        }
        ////////////////////////////////////////////////////////////////////////
        // 参数数组。
        ////////////////////////////////////////////////////////////////////////
        const parameterArray = new Array();
        parameterArray.push({"Account-Token": AccountSecurity.getItem("account_token")});
        for (let i = 0; i < parameterCheckArray.length; i++) {
          const parameter = parameterCheckArray[i];
          const param = {};
          param[parameter.name] = parameter.value;
          parameterArray.push(param);
        }
        ////////////////////////////////////////////////////////////////////////
        // 修改角色权限。
        ////////////////////////////////////////////////////////////////////////
        Network.request(Network.RequestType.POST, Network.ResponseType.JSON, [{"Content-Type": "application/x-www-form-urlencoded"}],
          Configure.getServerUrl() + "/module/security.Role/modifyRolePermission", parameterArray, source,
          function loadStart(xhr, xhrEvent, source) {
            source.frozenControl("modifyRolePermission");// 冻结控件。
            source.waitMask.show(); // 显示等待遮蔽。
          },
          function error(xhr, xhrEvent, source) {
            source.prompt.show("error", "网络请求失败", source.promptShowTime);
          },
          function timeout(xhr, xhrEvent, source) {
            source.prompt.show("error", "网络请求超时", source.promptShowTime);
          },
          function readyStateChange(xhr, xhrEvent, source) {
            if ((XMLHttpRequest.DONE == xhr.readyState) && (200 == xhr.status)) {
              source.recoverControl("modifyRolePermission"); // 恢复控件。
              source.waitMask.hide(); // 隐藏等待遮蔽。
              //////////////////////////////////////////////////////////////////
              // 响应结果。
              //////////////////////////////////////////////////////////////////
              const responseResult = xhr.response;
              if (Toolkit.stringEqualsIgnoreCase("SUCCESS", responseResult.status)) {
                ////////////////////////////////////////////////////////////////
                // 存储保存前的角色名称的数据。
                ////////////////////////////////////////////////////////////////
                source.beforeSaveRoleNameData.uuid = source.roleNameSelect.getObject().val();
                source.beforeSaveRoleNameData.enabled = true;
                ////////////////////////////////////////////////////////////////
                // 显示成功信息。
                ////////////////////////////////////////////////////////////////
                source.prompt.show("success", "修改成功", source.promptShowTime);
                ////////////////////////////////////////////////////////////////
                // 获取角色。
                ////////////////////////////////////////////////////////////////
                source.getRole();
              } else {
                source.prompt.show("error", responseResult.attach, source.promptShowTime);
              }
            }
          }
        );
      }
    } else {
      source.prompt.show("error", "请先选择一个角色", source.promptShowTime);
    }
  }

  /**
   * 模块方法表格中多选框click事件
   * @param event 事件对象
   */
  moduleMethodTableCheckboxClickEvent(event) {
    ////////////////////////////////////////////////////////////////////////////
    // 获取调用源。
    ////////////////////////////////////////////////////////////////////////////
    const source = event.data;
    if ($(this).hasClass("module")) {
      //////////////////////////////////////////////////////////////////////////
      // 如果点击了模块，则全选或取消全选所有接口的多选框。
      //////////////////////////////////////////////////////////////////////////
      if ($(this).is(":checked")) {
        source.moduleMethodTable.getObject().find("tbody").find("tr").find("td").find(`:checkbox[data-parent-module="${$(this).attr("data-module")}"]`).prop("checked", true);
      } else {
        source.moduleMethodTable.getObject().find("tbody").find("tr").find("td").find(`:checkbox[data-parent-module="${$(this).attr("data-module")}"]`).prop("checked", false);
      }
    } else if ($(this).hasClass("method")) {
      if (!$(this).is(":checked")) {
        ////////////////////////////////////////////////////////////////////////
        // 如果模块方法表格中的接口多选框取消了任意一个，则取消模块多选框。
        ////////////////////////////////////////////////////////////////////////
        source.moduleMethodTable.getObject().find("tbody").find("tr").find("td").find(`:checkbox[data-module="${$(this).attr("data-parent-module")}"]`).prop("checked", false);
      } else {
        ////////////////////////////////////////////////////////////////////////
        // 如果模块方法表格中的接口多选框全部选中，则选中其所属的模块多选框。
        ////////////////////////////////////////////////////////////////////////
        const parentModule = $(this).attr("data-parent-module");
        const all = source.moduleMethodTable.getObject().find("tbody").find("tr").find("td").find(`:checkbox[data-parent-module="${parentModule}"]`).length;
        const checked = source.moduleMethodTable.getObject().find("tbody").find("tr").find("td").find(`:checkbox[data-parent-module="${parentModule}"]:checked`).length;
        if (all == checked) {
          source.moduleMethodTable.getObject().find("tbody").find("tr").find("td").find(`:checkbox[data-module="${parentModule}"]`).prop("checked", true);
        }
      }
    }
  }

  /**
   * 获取角色
   */
  getRole() {
    ////////////////////////////////////////////////////////////////////////////
    // 获取角色。
    ////////////////////////////////////////////////////////////////////////////
    Network.request(Network.RequestType.POST, Network.ResponseType.JSON, [{"Content-Type": "application/x-www-form-urlencoded"}],
      Configure.getServerUrl() + "/module/security.Role/getRole", [{"Account-Token": AccountSecurity.getItem("account_token")}], this,
      function loadStart(xhr, xhrEvent, source) {
        source.frozenControl("getRole"); // 冻结控件。
        source.waitMask.show(); // 显示等待遮蔽。
      },
      function error(xhr, xhrEvent, source) {
        Error.redirect("../home/error.html", "获取角色", "网络请求失败", window.location.href);
      },
      function timeout(xhr, xhrEvent, source) {
        Error.redirect("../home/error.html", "获取角色", "网络请求超时", window.location.href);
      },
      function readyStateChange(xhr, xhrEvent, source) {
        if ((XMLHttpRequest.DONE == xhr.readyState) && (200 == xhr.status)) {
          source.recoverControl("getRole"); // 恢复控件。
          source.waitMask.hide(); // 隐藏等待遮蔽。
          //////////////////////////////////////////////////////////////////////
          // 响应结果。
          //////////////////////////////////////////////////////////////////////
          const responseResult = xhr.response;
          if (Toolkit.stringEqualsIgnoreCase("SUCCESS", responseResult.status)) {
            ////////////////////////////////////////////////////////////////////
            // 清空角色数组。
            ////////////////////////////////////////////////////////////////////
            source.roleArray.splice(0, source.roleArray.length);
            ////////////////////////////////////////////////////////////////////
            // 提取角色数组。
            ////////////////////////////////////////////////////////////////////
            for (let i = 0; i < responseResult.content.array.length; i++) {
              //////////////////////////////////////////////////////////////////
              // 存入角色数组。
              //////////////////////////////////////////////////////////////////
              source.roleArray.push(responseResult.content.array[i]);
            }
            ////////////////////////////////////////////////////////////////////
            // 排序角色数组。
            ////////////////////////////////////////////////////////////////////
            source.roleArray.sort(function(a, b) {
              return a.order - b.order;
            });
            ////////////////////////////////////////////////////////////////////
            // 恢复控件。
            ////////////////////////////////////////////////////////////////////
            source.recoverControl("getRole");
            ////////////////////////////////////////////////////////////////////
            // 如果角色数组不为空，则添加下拉框数据。
            ////////////////////////////////////////////////////////////////////
            source.roleNameSelect.getObject().html(`<option value = "">请选择</option>`);
            for (let i = 0; i < source.roleArray.length; i++) {
              let selectedCode = "";
              const r = source.roleArray[i];
              if (r.uuid == source.beforeSaveRoleNameData.uuid) {
                selectedCode = ` selected = "selected"`;
              }
              source.roleNameSelect.getObject().append(`<option value = "${r.uuid}"${selectedCode}>${r.name}</option>`);
            }
            ////////////////////////////////////////////////////////////////////
            // 注册模块方法表格中多选框的click事件。
            ////////////////////////////////////////////////////////////////////
            source.moduleMethodTable.getObject().find("tbody").find("tr").find("td").find(":checkbox").off("click").on("click", null, source, source.moduleMethodTableCheckboxClickEvent);
            ////////////////////////////////////////////////////////////////////
            // 触发角色名称下拉框change事件。
            ////////////////////////////////////////////////////////////////////
            source.roleNameSelect.getObject().trigger("change", source.source);
            ////////////////////////////////////////////////////////////////////
            // 设置保存前的角色名称的数据不可用。
            ////////////////////////////////////////////////////////////////////
            source.beforeSaveRoleNameData.enabled = false;
          } else {
            Error.redirect("../home/error.html", "获取角色", responseResult.attach, window.location.href);
          }
        }
      }
    );
  }

  /**
   * 获取模块方法
   */
  getModuleMethod() {
    ////////////////////////////////////////////////////////////////////////////
    // 获取模块方法。
    ////////////////////////////////////////////////////////////////////////////
    Network.request(Network.RequestType.POST, Network.ResponseType.JSON, [{"Content-Type": "application/x-www-form-urlencoded"}],
      Configure.getServerUrl() + "/module/security.ModuleMethod/getModuleMethod", [{"Account-Token": AccountSecurity.getItem("account_token")}], this,
      function loadStart(xhr, xhrEvent, source) {
        source.frozenControl("getModuleMethod"); // 冻结控件。
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
          source.recoverControl("getModuleMethod"); // 恢复控件。
          source.waitMask.hide(); // 隐藏等待遮蔽。
          //////////////////////////////////////////////////////////////////////
          // 响应结果。
          //////////////////////////////////////////////////////////////////////
          const responseResult = xhr.response;
          if (Toolkit.stringEqualsIgnoreCase("SUCCESS", responseResult.status)) {
            ////////////////////////////////////////////////////////////////////
            // 清空模块方法数组。
            ////////////////////////////////////////////////////////////////////
            source.moduleMethodArray.splice(0, source.moduleMethodArray.length);
            ////////////////////////////////////////////////////////////////////
            // 提取模块方法数组。
            ////////////////////////////////////////////////////////////////////
            for (let i = 0; i < responseResult.content.array.length; i++) {
              source.moduleMethodArray.push(responseResult.content.array[i]);
            }
            ////////////////////////////////////////////////////////////////////
            // 排序模块方法数组。
            ////////////////////////////////////////////////////////////////////
            source.moduleMethodArray.sort(function(m1, m2) {
              m1.module.localeCompare(m2.module);
            });
            ////////////////////////////////////////////////////////////////////
            // 排序模块方法中方法的数据。
            ////////////////////////////////////////////////////////////////////
            for (let i = 0; i < source.moduleMethodArray.length; i++) {
              const moduleMethod = source.moduleMethodArray[i];
              moduleMethod.methods.sort(function(m1, m2) {
                m1.method_name.localeCompare(m2.method_name);
              });
            }
            ////////////////////////////////////////////////////////////////////
            // 恢复控件。
            ////////////////////////////////////////////////////////////////////
            source.recoverControl("getModuleMethod");
            ////////////////////////////////////////////////////////////////////
            // 如果模块方法数组不为空，则添加表格数据。
            ////////////////////////////////////////////////////////////////////
            if (0 < source.moduleMethodArray.length) {
              let tbodyCode = "";
              for (let i = 0; i < source.moduleMethodArray.length; i++) {
                ////////////////////////////////////////////////////////////////
                // 权限对象。
                ////////////////////////////////////////////////////////////////
                const moduleMethod = source.moduleMethodArray[i];
                const moduleCheckboxId = Toolkit.generateUuid();
                tbodyCode += `
                  <tr class = "module_row">
                    <td colspan = "5">
                      <input id = "${moduleCheckboxId}" class = "global_input module" type = "checkbox" data-module = "${moduleMethod.module}" />
                      <label for = "${moduleCheckboxId}" class = "global_label module" data-module = "${moduleMethod.module}">${moduleMethod.module}</label>
                    </td>
                  </tr>
                `;
                ////////////////////////////////////////////////////////////////
                // 屏蔽ping方法，角色权限中不需要。
                ////////////////////////////////////////////////////////////////
                for (let j = 0; j < moduleMethod.methods.length; j++) {
                  if (Toolkit.stringEqualsIgnoreCase("ping", moduleMethod.methods[j].method_name)) {
                    moduleMethod.methods.splice(j, 1);
                  }
                }
                for (let j = 0; j < moduleMethod.methods.length; j++) {
                  //////////////////////////////////////////////////////////////
                  // td代码。
                  //////////////////////////////////////////////////////////////
                  let tdCode = "";
                  //////////////////////////////////////////////////////////////
                  // 遍历权限中的接口。
                  //////////////////////////////////////////////////////////////
                  let n = 0;
                  for (n = 0; n < 4; n++) {
                    if ((j + n) < moduleMethod.methods.length) {
                      const methodCheckboxId = Toolkit.generateUuid();
                      tdCode += `
                          <td>
                            <input id = "${methodCheckboxId}" class = "global_input method" type = "checkbox" data-parent-module = "${moduleMethod.module}" value = "${moduleMethod.methods[j + n].method_full_name}" />
                            <label for = "${methodCheckboxId}" class = "global_label method" data-parent-module = "${moduleMethod.module}">${moduleMethod.methods[j + n].method_name}</label>
                          </td>
                      `;
                    } else {
                      tdCode += "<td></td>";
                    }
                  }
                  j += (n - 1);
                  tbodyCode += `<tr><td></td>${tdCode}</tr>`;
                }
              }
              source.moduleMethodTable.getObject().find("tbody").html(tbodyCode);
              //////////////////////////////////////////////////////////////////
              // 获取角色。
              //////////////////////////////////////////////////////////////////
              source.getRole();
            } else {
              source.moduleMethodTable.getObject().find("tbody").html(`
                <tr>
                  <td class = "rowspan" colspan = "5">尚无数据</td>
                </tr>
              `);
            }
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
    this.roleNameSelect.getObject().attr("disabled", "disabled");
    this.saveButton.getObject().attr("disabled", "disabled");
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
      this.roleNameSelect.getObject().removeAttr("disabled");
      this.saveButton.getObject().removeAttr("disabled");
    }
  }
}
