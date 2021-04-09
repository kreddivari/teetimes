package com.nai.reservation;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class Scheduler {

    @Value("${teetime.firstName}")
    private  String firstName;

    @Value("${teetime.lastName}")
    private  String lastName;

    @Value("${teetime.email}")
    private  String email;

    @Value("${teetime.mobile}")
    private  String mobile;

    @Value("${teetime.url}")
    private  String url;

    @Value("${teetime.time}")
    private  String time;

    @Scheduled(cron = "0 48 16 * * SAT")
    public void cronJobSch() {
        try {
            scheduleTT();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void scheduleTT() {
        String userHome=System.getProperty("user.home");
        System.out.println("user home"+userHome);
        System.setProperty("webdriver.chrome.driver", userHome+"\\teetimes\\chromedriver.exe");
        WebDriver driver=new ChromeDriver();
        driver.manage().window().maximize();
        driver.get(url);
        driver.navigate().refresh();
        try{
            Boolean calenderSelected=false;
            driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
            WebElement table = driver.findElement(By.xpath("//table"));
            java.util.List<WebElement> allRows = table.findElements(By.tagName("tr"));
            for (WebElement row : allRows) {
                List<WebElement> cells = row.findElements(By.className("day"));
                for (WebElement cell : cells) {
                    if(cell.getText().equals("10")){

                        cell.click();
                        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
                        WebElement teetIme=driver.findElement(By.xpath("//h2[text()=\""+time+" "+"\"]/ancestor::div[2]/div[@class='panel-footer']/button"));

                        teetIme.click();

                        WebElement noOfGolfers=driver.findElement(By.xpath("//div[@id=\"qty_popup_notice\"]/a[1]"));
                        noOfGolfers.click();

                        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
                        WebElement termsAndConditions=driver.findElement(By.id("cboReqPolicy"));
                        termsAndConditions.click();

                        WebElement continueAsGuest=driver.findElement(By.id("btnBookTeeTimeGuest"));
                        continueAsGuest.click();
                        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

                        WebElement fName=driver.findElement(By.id("trfirst"));
                        WebElement lName=driver.findElement(By.id("trlast"));
                        WebElement mail=driver.findElement(By.id("tremail"));
                        WebElement phone=driver.findElement(By.id("trphone"));

                        fName.sendKeys(firstName);
                        lName.sendKeys(lastName);
                        mail.sendKeys(email);
                        phone.sendKeys(mobile);

                        WebElement reserve=driver.findElement(By.id("btnBookTeeTime"));
                        reserve.click();
                        driver.switchTo( ).alert( ).accept();
                        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
                       // driver.close();
                        return;
                    }
                }
            }
        }
        catch(Exception e){
            System.out.println("Exception >>   " + e.getMessage());
        }

    }

    public PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        String userHome=System.getProperty("user.home");
        PropertySourcesPlaceholderConfigurer properties =
                new PropertySourcesPlaceholderConfigurer();
        properties.setLocation(new FileSystemResource(userHome+"\\teetimes\\application.properties"));
        properties.setIgnoreResourceNotFound(false);
        return properties;
    }
}