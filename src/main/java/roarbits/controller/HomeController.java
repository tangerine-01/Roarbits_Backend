package roarbits.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    // 루트로 들어왔을 때
    @GetMapping("/")
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("Hello, Roarbits!");
    }

    // 추가로 /api/hello 매핑
    @GetMapping("/api/hello")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("Hello, Roarbits!");
    }
}
