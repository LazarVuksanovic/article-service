package rs.pravda.article_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Map;

@FeignClient(name = "frontend-client", url = "${application.frontend.url}")
public interface FrontendClient {

    @PostMapping("/api/revalidate")
    void triggerRevalidation(@RequestBody Map<String, String> body);
}