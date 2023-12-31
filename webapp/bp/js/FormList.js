"use strict";

class FormList extends ObjectFactory {
  /**
   * 构造函数
   */
  constructor() {
    ////////////////////////////////////////////////////////////////////////////
    // 父类构造函数。
    ////////////////////////////////////////////////////////////////////////////
    super();
    ////////////////////////////////////////////////////////////////////////////
    // 结果信息淡入的时间。
    ////////////////////////////////////////////////////////////////////////////
    this.resultInfoFadeInTime = 500;
    ////////////////////////////////////////////////////////////////////////////
    // 提示信息淡入的时间。
    ////////////////////////////////////////////////////////////////////////////
    this.promptInfoFadeInTime = 500;
  }

  /**
   * 生成代码
   */
  generateCode() {
    ////////////////////////////////////////////////////////////////////////////
    // 结果信息标签。
    ////////////////////////////////////////////////////////////////////////////
    this.resultInfoLabel = new JSControl("label");
    ////////////////////////////////////////////////////////////////////////////
    // 确认按钮。
    ////////////////////////////////////////////////////////////////////////////
    this.confirmButton = new JSControl("button");
    ////////////////////////////////////////////////////////////////////////////
    // 取消按钮。
    ////////////////////////////////////////////////////////////////////////////
    this.cancelButton = new JSControl("button");
    ////////////////////////////////////////////////////////////////////////////
    // 结果信息标签。
    ////////////////////////////////////////////////////////////////////////////
    this.resultInfoLabel.setAttribute(
      {
        "class": "global_label gfl_result_info_label"
      }
    );
    ////////////////////////////////////////////////////////////////////////////
    // 确认按钮。
    ////////////////////////////////////////////////////////////////////////////
    this.confirmButton.setAttribute(
      {
        "class": "global_button_primary gfl_confirm_button"
      }
    );
    this.confirmButton.setContent("确认");
    ////////////////////////////////////////////////////////////////////////////
    // 取消按钮。
    ////////////////////////////////////////////////////////////////////////////
    this.cancelButton.setAttribute(
      {
        "class": "global_button_default gfl_cancel_button"
      }
    );
    this.cancelButton.setContent("取消");
    ////////////////////////////////////////////////////////////////////////////
    // 结果信息标签。
    ////////////////////////////////////////////////////////////////////////////
    this.resultInfoLabel.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // 确认按钮。
    ////////////////////////////////////////////////////////////////////////////
    this.confirmButton.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // 取消按钮。
    ////////////////////////////////////////////////////////////////////////////
    this.cancelButton.generateCode();
    ////////////////////////////////////////////////////////////////////////////
    // 属性代码。
    ////////////////////////////////////////////////////////////////////////////
    let attributeCode = "";
    Toolkit.eachJSONObjectKV(this.objectAttribute, function(key, value) {
      attributeCode += `${key} = "${value}"`;
    });
    ////////////////////////////////////////////////////////////////////////////
    // 列表代码。
    ////////////////////////////////////////////////////////////////////////////
    let listCode = "";
    for (let i = 0; i < this.objectContent.length; i++) {
      const list = this.objectContent[i];
      //////////////////////////////////////////////////////////////////////////
      // 列表的属性代码。
      //////////////////////////////////////////////////////////////////////////
      let labelCode = "global_label gfl_label";
      if (list.required) {
        labelCode += " global_label_required";
      }
      let listAttrCode = "";
      Toolkit.eachJSONObjectKV(list.attr, function(key, value) {
        listAttrCode += `${key} = "${value}"`;
      });
      listCode += `
        <tr ${listAttrCode}>
          <td class = "${labelCode}">${list.label_code}</td><td class = "gfl_control">${list.control_code}</td>
        </tr>
        <tr class = "gfl_prompt_info" data-for = "${list.control_id}">
          <td></td><td></td>
        </tr>
      `;
    }
    this.objectCode = `
      <table id = "${this.objectId}" ${attributeCode}>
        <tbody>
          <tr class = "gfl_result_info">
            <td colspan = "2">${this.resultInfoLabel.getCode()}</td>
          </tr>
          ${listCode}
          <tr class = "gfl_button">
            <td colspan = "2">${this.confirmButton.getCode()}${this.cancelButton.getCode()}</td>
          </tr>
        </tbody>
      </table>
    `;
  }

  /**
   * 显示结果消息
   * @param type 类型（包括：success和error）
   * @param content 内容
   */
  showResultInfo(type, content) {
    ////////////////////////////////////////////////////////////////////////////
    // 移除类型class。
    ////////////////////////////////////////////////////////////////////////////
    this.resultInfoLabel.getObject().removeClass("gfl_result_info_label_success gfl_result_info_label_error");
    if (Toolkit.stringEqualsIgnoreCase("success", type)) {
      this.resultInfoLabel.getObject().addClass("gfl_result_info_label_success");
    } else {
      this.resultInfoLabel.getObject().addClass("gfl_result_info_label_error");
    }
    ////////////////////////////////////////////////////////////////////////////
    // 设置结果消息内容。
    ////////////////////////////////////////////////////////////////////////////
    this.resultInfoLabel.getObject().html(content);
    ////////////////////////////////////////////////////////////////////////////
    // 显示结果消息内容。
    ////////////////////////////////////////////////////////////////////////////
    this.resultInfoLabel.getObject().parent().parent().fadeIn(this.resultInfoFadeInTime);
  }

  /**
   * 隐藏结果消息
   */
  hideResultInfo() {
    ////////////////////////////////////////////////////////////////////////////
    // 隐藏结果消息内容。
    ////////////////////////////////////////////////////////////////////////////
    this.resultInfoLabel.getObject().parent().parent().css("display", "none");
    ////////////////////////////////////////////////////////////////////////////
    // 清空结果消息内容。
    ////////////////////////////////////////////////////////////////////////////
    this.resultInfoLabel.getObject().html("");
  }

  /**
   * 显示提示
   * @param id 提示的控件
   * @param content 提示的内容
   */
  showPrompt(id, content) {
    this.getObject().find("tbody").children(`.gfl_prompt_info[data-for="${id}"]`).find("td:eq(1)").html(content);
    this.getObject().find("tbody").children(`.gfl_prompt_info[data-for="${id}"]`).fadeIn(this.promptInfoFadeInTime);
  }

  /**
   * 隐藏提示
   * @param id 提示的控件
   */
  hidePrompt(id) {
    this.getObject().find("tbody").children(`.gfl_prompt_info[data-for="${id}"]`).css("display", "none");
    this.getObject().find("tbody").children(`.gfl_prompt_info[data-for="${id}"]`).find("td:eq(1)").html("");
  }

  /**
   * 隐藏所有提示
   */
  hideAllPrompt() {
    this.getObject().find("tbody").find(".gfl_prompt_info").css("display", "none");
    this.getObject().find("tbody").children(".gfl_prompt_info").find("td:eq(1)").html("");
  }
}
