package com.demo.service.impl;

import com.demo.client.AiClient;
import com.demo.pojo.BloodPressure;
import com.demo.pojo.BloodSugar;
import com.demo.pojo.HealthRecord;
import com.demo.service.AiChatService;
import com.demo.service.BloodPressureService;
import com.demo.service.BloodSugarService;
import com.demo.service.HealthRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * AI聊天服务实现类
 */
@Service
public class AiChatServiceImpl implements AiChatService {

    @Autowired
    private HealthRecordService healthRecordService;

    @Autowired
    private BloodPressureService bloodPressureService;

    @Autowired
    private BloodSugarService bloodSugarService;

    @Autowired
    private AiClient aiClient;

    @Override
    public String chat(Long userId, String message) {
        // ① 判断是不是"健康数据问题"
        if (isHealthQuestion(message)) {
            return answerHealthQuestion(userId, message);
        }

        // ② 普通聊天
        return aiClient.chat(message);
    }

    /**
     * 判断是否是健康数据问题
     * @param msg 消息内容
     * @return 是否是健康数据问题
     */
    private boolean isHealthQuestion(String msg) {
        return msg.contains("血压")
                || msg.contains("血糖")
                || msg.contains("健康")
                || msg.contains("吃")
                || msg.contains("体检")
                || msg.contains("身体")
                || msg.contains("状况");
    }

    /**
     * 回答健康数据问题
     * @param userId 老人ID
     * @param msg 问题内容
     * @return AI回答
     */
    private String answerHealthQuestion(Long userId, String msg) {
        // 获取健康档案
        List<HealthRecord> records = healthRecordService.getAll();
        HealthRecord record = null;
        for (HealthRecord r : records) {
            if (r.getResidentId().equals(userId.intValue())) {
                record = r;
                break;
            }
        }

        // 获取最新的血压记录
        List<BloodPressure> bloodPressureList = bloodPressureService.getBloodPressureRecordsByResidentId(userId.intValue());
        BloodPressure latestBloodPressure = null;
        if (!bloodPressureList.isEmpty()) {
            latestBloodPressure = bloodPressureList.get(bloodPressureList.size() - 1);
        }

        // 获取最新的血糖记录
        List<BloodSugar> bloodSugarList = bloodSugarService.getBloodSugarRecordsByResidentId(userId.intValue());
        BloodSugar latestBloodSugar = null;
        if (!bloodSugarList.isEmpty()) {
            latestBloodSugar = bloodSugarList.get(bloodSugarList.size() - 1);
        }

        if (record == null && latestBloodPressure == null && latestBloodSugar == null) {
            return "我暂时没有查到您的健康档案，请联系工作人员为您补充信息。";
        }

        // 构建AI提示
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是一名养老院健康助手，请用【通俗、温和、老人能听懂】的方式回答。\n\n");
        prompt.append("老人健康数据如下：\n");

        if (record != null) {
            prompt.append("既往病史：").append(record.getMedicalHistory() != null ? record.getMedicalHistory() : "无").append("\n");
            prompt.append("过敏史：").append(record.getAllergyHistory() != null ? record.getAllergyHistory() : "无").append("\n");
        }

        if (latestBloodPressure != null) {
            prompt.append("血压：").append(latestBloodPressure.getSystolic()).append("/").append(latestBloodPressure.getDiastolic()).append("mmHg\n");
        }

        if (latestBloodSugar != null) {
            prompt.append("血糖：").append(latestBloodSugar.getLevel()).append("mmol/L (").append(latestBloodSugar.getStatus()).append(")\n");
        }

        prompt.append("\n老人问题是：").append(msg).append("\n");

        // 调用AI生成回答
        return aiClient.chat(prompt.toString());
    }
}
