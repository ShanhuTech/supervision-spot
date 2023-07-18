"use strict";

class Configure {
  /**
   * 获取项目名称
   * @return 项目名称
   */
  static getProjectName() {
    return "supervision-spot";
  }

  /**
   * 获取后台服务的URL
   * @return 项目路径
   */
  static getServerUrl() {
    // return `http://127.0.0.1:9010/${Configure.getProjectName()}`;
    // return `http://192.168.10.196:9010/${Configure.getProjectName()}`;
    return `http://192.168.10.150:9010/${Configure.getProjectName()}`;
    // return `http://17.62.1.11:9010/${Configure.getProjectName()}`;
  }
}
