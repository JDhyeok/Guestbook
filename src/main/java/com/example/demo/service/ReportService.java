package com.example.demo.service;

import com.example.demo.model.ReportEntity;
import com.example.demo.repository.ReportRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final OpenAiService openAiService;

    public List<ReportEntity> getAllReports() {
        return reportRepository.findAll();
    }

    public ReportEntity getReport(Long id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found with id: " + id));
    }

    public ReportEntity analyzeReport(String title, String content, String industry, String companyName) throws Exception {
        ReportEntity report = ReportEntity.builder()
                .title(title)
                .content(content)
                .industry(industry)
                .companyName(companyName)
                .build();

        String prompt = String.format("""
                다음 주식투자 분석 보고서를 평가해주세요:
 
                제목: %s
                기업: %s
                산업: %s
                내용: %s
    
                다음 세 가지 측면에서 100점 만점으로 평가하고, 구체적인 이유를 설명해주세요.
                반드시 다음과 같은 JSON 형식으로 응답해주세요:
                biasScore: 0-100 사이의 점수,
                biasAnalysis: 편향성에 대한 상세 분석,
                riskScore: 0-100 사이의 점수,
                riskAnalysis: 리스크 분석의 충실도에 대한 상세 분석,
                optimismScore: 0-100 사이의 점수,
                optimismAnalysis: 낙관론 정도에 대한 상세 분석,
                summary: 종합 분석
                
                각 항목별 평가 기준:
                1. 편향성 (객관적인 분석인가?)
                  - 100점: 완전히 객관적이고 중립적인 분석
                  - 70점: 대체로 객관적이나 일부 주관적 견해 포함
                  - 30점: 상당히 편향된 분석
                  - 0점: 극도로 편향된 분석

                2. 리스크 분석의 충실도
                  - 100점: 모든 주요 리스크 요인을 포괄적으로 분석
                  - 70점: 주요 리스크는 파악했으나 일부 누락
                  - 30점: 리스크 분석이 피상적
                  - 0점: 리스크 분석이 거의 없음

                3. 낙관론 정도 (점수가 낮을수록 과도하게 낙관적)
                  - 100점: 매우 현실적이고 균형 잡힌 시각
                  - 70점: 약간의 낙관적 편향
                  - 30점: 상당히 낙관적
                  - 0점: 지나치게 낙관적

                분석 시 반드시 고려해야 할 사항:
                  - 재무적 근거의 충실성
                  - 시장 환경 분석의 정확성
                  - 경쟁사 분석의 포함 여부
                  - 산업 특성의 반영도
                  - 거시경제 요인의 고려
                """, title, companyName, industry, content);

        String analysisJson = openAiService.getAnalysis(prompt);


        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode reponseNode = objectMapper.readTree(analysisJson);

            JsonNode candidatesNode = reponseNode.get("candidates").get(0);
            String textContent = candidatesNode.get("content").get("parts").get(0).get("text").asText();
            String cleanedJson = textContent.replace("```json", "").replace("```", "").trim();

            JsonNode analysisNode = objectMapper.readTree(cleanedJson);

            report.setBiasScore(analysisNode.get("biasScore").asDouble());
            report.setRiskAnalysisScore(analysisNode.get("riskScore").asDouble());
            report.setOptimismScore(analysisNode.get("optimismScore").asDouble());

            String formattedAnalysis = String.format("""
                            1. 편향성 분석 (%.1f점)
                            %s
                                            
                            2. 리스크 분석 충실도 (%.1f점)
                            %s
                                            
                            3. 낙관론 평가 (%.1f점)
                            %s
                                            
                            종합 분석:
                            %s
                            """,
                    report.getBiasScore(),
                    analysisNode.get("biasAnalysis").asText(),
                    report.getRiskAnalysisScore(),
                    analysisNode.get("riskAnalysis").asText(),
                    report.getOptimismScore(),
                    analysisNode.get("optimismAnalysis").asText(),
                    analysisNode.get("summary").asText()
            );

            report.setAiAnalysis(formattedAnalysis);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse analysis result", e);
        }

        return reportRepository.save(report);
    }
}
