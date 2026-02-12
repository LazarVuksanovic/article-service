package rs.pravda.article_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import rs.pravda.article_service.dto.homePage.HomePageDto;
import rs.pravda.article_service.dto.homePage.HomePageUpdateDto;
import rs.pravda.article_service.service.HomePageService;

import static rs.pravda.article_service.service.impl.CategoryServiceImpl.toSlug;

@RestController
@RequiredArgsConstructor
@RequestMapping("/homepage")
public class HomePageController {

    private final HomePageService homePageService;

    @GetMapping
    public HomePageDto getGlobal() {
        return homePageService.getHomePageData(null);
    }

    @GetMapping("/{categorySlug}")
    public HomePageDto getByCategory(@PathVariable String categorySlug) {
        return homePageService.getHomePageData(toSlug(categorySlug));
    }

    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@RequestBody HomePageUpdateDto dto) {
        homePageService.updateLayout(dto);
    }
}