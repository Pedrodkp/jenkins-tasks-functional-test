package com.upiara.tasks;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

class AppTest {
    Boolean SELENIUM_HUB = true;
    String LOCAL_IP = "192.168.0.106";

    public WebDriver acessarApp(String url) {
        WebDriver driver;
        if (SELENIUM_HUB)
            try {
                DesiredCapabilities cap = DesiredCapabilities.chrome();
                driver = new RemoteWebDriver(new URL("http://"+LOCAL_IP+":4444/wd/hub"), cap);
            } catch (Exception e) {
                return null;
            }
        else {
            driver = new ChromeDriver();
        }

        driver.navigate().to(url);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        return driver;
    }

    public String fillTaskForm(WebDriver driver, String task, String dueDate) {
        //click add Todo
        driver.findElement(By.id("addTodo")).click();
        
        //write desc
        if (task != null) 
            driver.findElement(By.id("task")).sendKeys(task);

        //write date
        if (dueDate != null)
            driver.findElement(By.id("dueDate")).sendKeys(dueDate);

        //click save
        driver.findElement(By.id("saveButton")).click();

        //check msg
        return driver.findElement(By.id("message")).getText();
    }

    @Test
    void testApp() {
        WebDriver driver = acessarApp("http://www.google.com");
        try {
            //
        } finally {
            driver.quit();
        }
    }

    @Test
    void deveSalvarTarefaComSucesso() {
        WebDriver driver = acessarApp("http://"+LOCAL_IP+":8001/tasks");

        try {
            String message = fillTaskForm(driver, "Teste via Selenium", "10/10/2030");
            Assert.assertEquals("Success!", message);

        } finally {
            driver.quit();
        }
    }

    @Test
    void naoDeveSalvarTarefaSemDescricao() {
        WebDriver driver = acessarApp("http://"+LOCAL_IP+":8001/tasks");

        try {
            String message = fillTaskForm(driver, null, "10/10/2030");
            Assert.assertEquals("Fill the task description", message);

        } finally {
            driver.quit();
        }
    }

    @Test
    void naoDeveSalvarTarefaSemData() {
        WebDriver driver = acessarApp("http://"+LOCAL_IP+":8001/tasks");

        try {
            String message = fillTaskForm(driver, "Teste via Selenium", null);
            Assert.assertEquals("Fill the due date", message);

        } finally {
            driver.quit();
        }
    }

    @Test
    void naoDeveSalvarConDataPassada() {
        WebDriver driver = acessarApp("http://"+LOCAL_IP+":8001/tasks");

        try {
            String message = fillTaskForm(driver, "Teste via Selenium", "10/10/2010");
            Assert.assertEquals("Due date must not be in past", message);

        } finally {
            driver.quit();
        }
    }
}
