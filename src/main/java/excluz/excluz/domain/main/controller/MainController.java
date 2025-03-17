package excluz.excluz.domain.main.controller;

import excluz.excluz.auth.util.SecurityContextUtil;
import excluz.excluz.domain.order.order.dto.response.OrderResponseDto;
import excluz.excluz.domain.user.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class MainController {

    @GetMapping
    public ResponseEntity<Void> getMainPage() {
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create("https://github.com/TrainH/excluz"))
                .build();
    }
}
