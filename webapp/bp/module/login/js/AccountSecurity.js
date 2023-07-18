"use strict";

class AccountSecurity {
  /**
   * 构造函数
   */
  constructor() {
  }

  /**
   * 获取标识
   */
  static getId() {
    ////////////////////////////////////////////////////////////////////////////
    // 不同的项目需要设置不同的标识以做区分。
    ////////////////////////////////////////////////////////////////////////////
    return `${Configure.getProjectName()}_bp`;
  }

  /**
   * 获取存储项
   *
   * @param key 存储项的key
   */
  static getItem(key) {
    const itemStr = localStorage.getItem(AccountSecurity.getId());
    if (null == itemStr) {
      return null;
    } else {
      try {
        const itemObj = JSON.parse(itemStr);
        if (Toolkit.isJSONObjectExistKey(itemObj, key)) {
          return itemObj[key];
        }
        return null;
      } catch (e) {
        console.error(e);
        return null;
      }
    }
  }

  /**
   * 设置存储项
   *
   * @param key 存储项的key
   * @param value 存储项的value
   */
  static setItem(key, value) {
    let itemObj = {};
    let itemStr = localStorage.getItem(AccountSecurity.getId());
    try {
      if (null != itemStr) {
        itemObj = JSON.parse(itemStr);
      }
      itemObj[key] = value;
      itemStr = JSON.stringify(itemObj);
      localStorage.setItem(AccountSecurity.getId(), itemStr);
    } catch (e) {
      console.error(e.message);
    }
  }

  /**
   * 删除存储项
   *
   * @param key 存储项的key
   */
  static removeItem(key) {
    let itemStr = localStorage.getItem(AccountSecurity.getId());
    if (null != itemStr) {
      try {
        let itemObj = JSON.parse(itemStr);
        delete itemObj[key];
        itemStr = JSON.stringify(itemObj);
        localStorage.setItem(AccountSecurity.getId(), itemStr);
      } catch (e) {
        console.error(e);
      }
    }
  }

  /**
   * 清除存储项
   */
  static clearItem() {
    localStorage.removeItem(AccountSecurity.getId());
  }

  /**
   * 检查登录状态
   */
  static checkLogonStatus() {
    ////////////////////////////////////////////////////////////////////////////
    // 如果没有账户名称跳转至登陆页面。
    ////////////////////////////////////////////////////////////////////////////
    if (null == AccountSecurity.getItem("account_name")) {
      window.location.href = "../login/login.html";
    }
    ////////////////////////////////////////////////////////////////////////////
    // 先执行一次后面再定时循环判断。
    ////////////////////////////////////////////////////////////////////////////
    function check() {
      //////////////////////////////////////////////////////////////////////////
      // 如果当前时间晚于账户过期时间跳转至登陆页面。
      //////////////////////////////////////////////////////////////////////////
      const accountExpires = AccountSecurity.getItem("account_expires");
      if (null != accountExpires) {
        const expires = new Date(accountExpires).getTime();
        const currentDate = new Date().getTime();
        if (0 >= (expires - currentDate)) {
          window.location.href = "../login/login.html";
        }
      } else {
        window.location.href = "../login/login.html";
      }
    }
    ////////////////////////////////////////////////////////////////////////////
    // 执行一次。
    ////////////////////////////////////////////////////////////////////////////
    check();
    ////////////////////////////////////////////////////////////////////////////
    // 定时循环判断。
    ////////////////////////////////////////////////////////////////////////////
    setInterval(check, 1000 * 10/* 3秒钟执行一次 */);
  }

  /**
   * 刷新管理员Token
   */
  static refreshAdminToken(callback) {
    ////////////////////////////////////////////////////////////////////////////
    // 刷新管理员Token。
    ////////////////////////////////////////////////////////////////////////////
    Network.request(Network.RequestType.POST, Network.ResponseType.JSON, [{"Content-Type": "application/x-www-form-urlencoded"}],
      Configure.getServerUrl() + "/module/security.Admin/refreshAdminToken", [{"Account-Token": AccountSecurity.getItem("account_token")}], this,
      function loadStart(xhr, xhrEvent, source) {},
      function error(xhr, xhrEvent, source) {
        console.error("刷新管理员Token网络请求失败");
      },
      function timeout(xhr, xhrEvent, source) {
        console.error("刷新管理员Token网络请求超时");
      },
      function readyStateChange(xhr, xhrEvent, source) {
        if ((XMLHttpRequest.DONE == xhr.readyState) && (200 == xhr.status)) {
          //////////////////////////////////////////////////////////////////////
          // 响应结果。
          //////////////////////////////////////////////////////////////////////
          const responseResult = xhr.response;
          if (Toolkit.stringEqualsIgnoreCase("SUCCESS", responseResult.status)) {
            ////////////////////////////////////////////////////////////////////
            // 存储用户信息。
            ////////////////////////////////////////////////////////////////////
            const account = responseResult.content;
            AccountSecurity.setItem("account_expires", account.expires);
            AccountSecurity.setItem("account_token", account.token);
            if (null != callback) {
              callback();
            }
          } else {
            console.error("刷新管理员Token错误: " + responseResult.attach);
            AccountSecurity.logoff();
          }
        }
      }
    );
  }

  /**
   * 刷新Token
   */
  static refreshToken() {
    ////////////////////////////////////////////////////////////////////////////
    // 执行一次。
    ////////////////////////////////////////////////////////////////////////////
    AccountSecurity.refreshAdminToken(null);
    ////////////////////////////////////////////////////////////////////////////
    // 定时循环刷新。
    ////////////////////////////////////////////////////////////////////////////
    setInterval(AccountSecurity.refreshAdminToken, 1000 * 60 * 1/* 1分钟执行一次 */, null/*refreshAdminToken参数*/);
  }

  /**
   * 登出
   */
  static logoff() {
    ////////////////////////////////////////////////////////////////////////////
    // 清空存储项。
    ////////////////////////////////////////////////////////////////////////////
    AccountSecurity.clearItem();
    ////////////////////////////////////////////////////////////////////////////
    // 跳转至首页。
    ////////////////////////////////////////////////////////////////////////////
    window.location.href = "../login/login.html";
  }
}
