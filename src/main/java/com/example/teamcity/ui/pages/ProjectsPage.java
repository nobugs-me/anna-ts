package com.example.teamcity.ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.example.teamcity.ui.elements.ProjectElement;
import com.example.teamcity.ui.elements.SideBarProjectElement;
import lombok.Getter;

import java.util.List;

import static com.codeborne.selenide.Selenide.*;

public class ProjectsPage extends BasePage {
    private static final String PROJECTS_URL = "/favorite/projects";

    private ElementsCollection projectElements = $$("div[class*='Subproject__container']");

    private SelenideElement spanFavoriteProjects = $("span[class='ProjectPageHeader__title--ih']");

    private SelenideElement header = $(".MainPanel__router--gF > div");

    private SelenideElement searchProjectInput = $("#search-projects");
    private SelenideElement addToFavorites = $("[aria-label='Add to favorites...']");
    private SelenideElement allProjects = $("[span='All Projects']");
    private ElementsCollection sideBarProjectElements = $$("[data-test='sidebar-item']");

    // ElementCollection -> List<ProjectElement>
    // UI elements -> List<Object>
    // ElementCollection -> List<BasePageElement>

    public ProjectsPage() {
        header.shouldBe(Condition.visible, BASE_WAITING);
    }

    public static ProjectsPage open() {
        return Selenide.open(PROJECTS_URL, ProjectsPage.class);
    }

    public List<ProjectElement> getProjects() {
        return generatePageElements(projectElements, ProjectElement::new);
    }

    public ProjectsPage searchForProject(String projectName) {
        searchProjectInput.val(projectName);
        addToFavorites.shouldNotBe(Condition.visible, BASE_WAITING);
        allProjects.shouldNotBe(Condition.visible, BASE_WAITING);
        header.shouldBe(Condition.visible, BASE_WAITING);
        //не нахожу к чему привязаться чтоб дожидалось обновления панели серча - подскажи пжта что-то стабильно работающее тут вместо слипа
        return this;
    }

    public List<SideBarProjectElement> getSideBarProjects() {
        return generatePageElements( sideBarProjectElements, SideBarProjectElement::new);
    }
    //можно ли в такой концепции чейн инвокейшин как то юзать геттеры и сеттеры юай элементов напрямую?
    //сейчас получается что когда юзаю например эелемент клик то цепочка прерывается и надо снова начинать с опен пейдж а это сбрасывпет все предыдущие действия
}
