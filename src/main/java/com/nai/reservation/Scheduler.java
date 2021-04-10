package com.nai.reservation;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.FileSystemResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.concurrent.TimeUnit;

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

    @Value("${teetime.saturday.time}")
    private  String SaturdayStartTime;

    @Value("${teetime.sunday.time}")
    private  String SundayStartTime;

    @Scheduled(cron = "0 33 18 * * SAT")
    public void cronJobSaturday() {
        try {
            String[] timeref=SaturdayStartTime.split(":");
            Integer hourRef=Integer.parseInt(timeref[0]);
            Integer minRef=Integer.parseInt(timeref[1]);
            LocalDate dt = LocalDate.now();
            LocalDate nextSaturday=dt.with(TemporalAdjusters.next(DayOfWeek.SATURDAY));
            Integer dateToClick=nextSaturday.getDayOfMonth();
            scheduleTT(dateToClick,hourRef,hourRef);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Scheduled(cron = "0 00 05 * * SUN")
    public void cronJobSunday() {
        try {
            String[] timeref=SundayStartTime.split(":");
            Integer hourRef=Integer.parseInt(timeref[0]);
            Integer minRef=Integer.parseInt(timeref[1]);
            LocalDate dt = LocalDate.now();
            LocalDate nextSaturday=dt.with(TemporalAdjusters.next(DayOfWeek.SUNDAY));
            Integer dateToClick=nextSaturday.getDayOfMonth();
            scheduleTT(dateToClick,hourRef,hourRef);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void scheduleTT(Integer dateToClick,Integer hourRef,Integer minRef) {
        String userHome=System.getProperty("user.home");
        System.out.println("user home"+userHome);
        System.setProperty("webdriver.chrome.driver", userHome+"\\teetimes\\chromedriver.exe");
        WebDriver driver=new ChromeDriver();
        driver.manage().window().maximize();
        driver.get(url);
        driver.navigate().refresh();
        try{
            driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
           //day click
            WebElement daytoclick=driver.findElement(By.xpath("//td[text()="+dateToClick.toString()+"]"));
            daytoclick.click();

            driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
            java.util.List<WebElement> alltimes = driver.findElements(By.xpath("//div[@class=\"panel-heading\"]/h2"));
            for(WebElement times:alltimes){
                boolean timeFound=false;
                if(!timeFound){
                    String[] webtimeref=times.getText().split(":");
                    Integer webhourRef=Integer.parseInt(webtimeref[0]);
                    String webminrefmain=webtimeref[1].split("\\s+")[0];
                    String AMPM=webtimeref[1].split("\\s+")[1];
                    Integer webminRef=Integer.parseInt(webminrefmain);
                    if((webhourRef>hourRef) || (webhourRef==hourRef && webminRef>=minRef)|| (webhourRef<hourRef && AMPM.equals("PM"))){
                        timeFound=true;
                        WebElement teetIme=driver.findElement(By.xpath("//h2[text()=\""+webhourRef+":"+webminRef+" "+"\"]/ancestor::div[2]/div[@class='panel-footer']/button"));
                        teetIme.click();
                        System.out.println("time"+webhourRef+":"+webminRef);
                        break;
                    }
                }
            }

            //next page

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

           /* WebElement reserve=driver.findElement(By.id("btnBookTeeTime"));
            reserve.click();
            driver.switchTo( ).alert( ).accept();
            driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
*/

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