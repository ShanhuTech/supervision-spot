package supervision.spot.report;

public class ReportKit {
    // 设计思路：后台可以设置报告类型以及上传、修改模板文件（base64），前端通过生成结构，降构造数据一次性.{1,}传入模板文件。
    // 在Framework.ProjectRealPath + "report_tmp_storage/"中存储生成的文件
    // 开启线程，每10分钟定时清空一次创建时间大于1小时的文件。
    // jar已集成
    // 参考：http://deepoove.com/poi-tl/
    // 模板目录：${Module}/res/report_template/
}