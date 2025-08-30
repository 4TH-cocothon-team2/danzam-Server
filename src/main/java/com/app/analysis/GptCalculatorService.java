package com.app.analysis;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.json.JSONObject;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class GptCalculatorService {
    private final OkHttpClient client = new OkHttpClient();
    private final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final String MODEL = System.getenv().getOrDefault("OPENAI_MODEL",
            Optional.ofNullable(Dotenv.load().get("OPENAI_MODEL")).orElse("gpt-4o"));

    // ⚠️ 여기 수식/룰을 네가 원하는 대로 바꾸면 됨
    private static final String RULES = """
        # 목표
        - 카페인 반감기 모델을 사용해 시점별 잔존량(mg)을 계산한다.
        - 기본 반감기: 5h. 흡연자는 대사 ↑(반감기 5h / 1.5), 임신은 대사 ↓(반감기 5h * 2). 기타 약물 복용 true면 반감기 5h * 1.3. (필요시 조정)
        - 잔존량이 5mg 이하가 되는 첫 시점을 '예상 수면 시간'으로 본다.
        - 30분 간격 시계열을 생성한다(오늘 00:00 ~ 내일 06:00).
        - 현재 시각 기준 잔존량, 0에 도달까지 남은 시간(시간)을 구한다.
        - remainingPercent = (현재 잔존 mg / 오늘 총섭취 mg) * 100 (총섭취=0이면 0).
        - 사용자 키, 몸무게도 고려해 반영한다.

        # 출력 형식(JSON만)
        {
          "remainingMg": number,
          "remainingPercent": number,
          "hoursToZero": number,
          "estimatedSleepTime": "YYYY-MM-DDTHH:mm",
          "series": [{"time":"YYYY-MM-DDTHH:mm:ss","mg":number}, ...],
        }
        """;

    public String computeByGpt(User user, List<Intake> intakes, LocalDate date) {
        JSONObject payload = new JSONObject()
                .put("model", MODEL)
                .put("response_format", new JSONObject().put("type","json_object"))
                .put("messages", new org.json.JSONArray()
                        .put(new JSONObject().put("role","system").put("content","너는 수식에 따라 정확히 계산하는 엔진이다. 반드시 JSON만 출력해라."))
                        .put(new JSONObject().put("role","system").put("content", RULES))
                        .put(new JSONObject().put("role","user").put("content", buildUserContent(user, intakes, date)))
                );

        Request req = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Authorization", "Bearer " + apiKey())
                .post(RequestBody.create(payload.toString(), JSON))
                .build();

        try (Response resp = client.newCall(req).execute()) {
            if (!resp.isSuccessful()) throw new RuntimeException("HTTP " + resp.code());
            return new JSONObject(resp.body().string())
                    .getJSONArray("choices").getJSONObject(0)
                    .getJSONObject("message").getString("content");
        } catch (Exception e) {
            // 실패 시 null 반환 → 서비스에서 폴백 계산
            return null;
        }
    }

    private String apiKey() {
        String k = System.getenv("OPENAI_API_KEY");
        return (k != null && !k.isBlank()) ? k : dotenv.get("OPENAI_API_KEY");
    }

    private static String buildUserContent(User u, List<Intake> xs, LocalDate date){
        JSONObject user = new JSONObject()
                .put("weightKg", u.getWeight())
                .put("age", u.getAge())
                .put("smoker", Boolean.TRUE.equals(u.getSmoke()))
                .put("pregnant", Boolean.TRUE.equals(u.getPregnancy()))
                .put("meds", Boolean.TRUE.equals(u.getMedication()));

        org.json.JSONArray intakes = new org.json.JSONArray();
        xs.forEach(in -> intakes.put(new JSONObject()
                .put("mg", in.getCaffeine_mg())
                .put("takenAt", in.getIntakeAt().toString())
        ));

        return new JSONObject()
                .put("targetDate", date.toString())
                .put("now", LocalDateTime.now().toString())
                .put("user", user)
                .put("intakes", intakes)
                .toString();
    }

}
