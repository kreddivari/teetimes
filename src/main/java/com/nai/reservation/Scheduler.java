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

import java.io.File;
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

    private static String OS = System.getProperty("os.name").toLowerCase();

    @Scheduled(cron = "0 00 05 * * SAT")
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
        String userHomeDir = null;
        if (isWindows()) {
            userHomeDir=userHome+File.separator+"teetimes"+ File.separator+"chromedriver.exe";
        } else if (isMac()) {
            userHomeDir=userHome+File.separator+"teetimes"+ File.separator+"chromedriver";
        }
        System.setProperty("webdriver.chrome.driver", userHomeDir);
        WebDriver driver=new ChromeDriver();
        driver.manage().window().maximize();
        driver.get(url);
        //driver.navigate().refresh();
        try{
            driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
           //day click
            //WebElement daytoclick=driver.findElement(By.xpath("//td[@class=\"day\"][text()="+dateToClick.toString()+"]"));
            WebElement daytoclick=driver.findElement(By.xpath("//td[contains(@class, 'day') or contains(@class, 'weekend-column') or contains(@class, 'new')][text()="+dateToClick.toString()+"]"));
            daytoclick.click();

            driver.manage().timeouts().pageLoadTimeout(2, TimeUnit.SECONDS);
           // java.util.List<WebElement> alltimes = driver.findElements(By.xpath("//div[@class=\"panel-heading\"]/h2"));
            java.util.List<WebElement> alltimes = driver.findElements(By.xpath("//h2"));

            for(WebElement times:alltimes){
                boolean timeFound=false;
                if(!timeFound){
                    String[] webtimeref=times.getText().split(":");
                    Integer webhourRef=Integer.parseInt(webtimeref[0]);
                    String webminrefmain=webtimeref[1].split("\\s+")[0];
                    String AMPM=webtimeref[1].split("\\s+")[1];
                    Integer webminRef=Integer.parseInt(webminrefmain);
                    String minref=null;
                    if(webminRef==0){
                        minref="00";
                    }else{
                        minref=webminRef.toString();
                    }
                    if((webhourRef>hourRef) || (webhourRef==hourRef && webminRef>=minRef)|| (webhourRef<hourRef && AMPM.equals("PM"))){
                        timeFound=true;
                        WebElement teetIme=driver.findElement(By.xpath("//h2[text()=\""+webhourRef+":"+minref+" "+"\"]/ancestor::div[2]/div[@class='panel-footer']/button"));
                        WebElement NumofGolfers=driver.findElement(By.xpath("//h2[text()=\""+webhourRef+":"+minref+" "+"\"]/ancestor::div[2]/div[@class='panel-body']/div[2]"));
                        if(NumofGolfers.getText().equals("1 to 4 golfers")){
                            teetIme.click();
                            break;
                        }
                    }
                }
            }

            //next page

            WebElement noOfGolfers=driver.findElement(By.xpath("//div[@id=\"qty_popup_notice\"]/a[1]"));
            noOfGolfers.click();

            driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
            WebElement termsAndConditions=driver.findElement(By.id("cboReqPolicy"));
            termsAndConditions.click();

            WebElement continueAsGuest=driver.findElement(By.id("btnBookTeeTimeGuest"));
            continueAsGuest.click();
            driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);

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
        }
        catch(Exception e){
            System.out.println("Exception >>   " + e.getMessage());
        }

    }

    public char getPathSeparator(){
        // Using System properties
        String pathSeparator = System.getProperty( "path.separator" );
        // Using File variable (String)
        String pathSeperator = File.pathSeparator;
       // Using File variable (char)
        char pathSeparatorChar = File.pathSeparatorChar;
        System.out.println("pathSeparatorChar"+pathSeparatorChar);
        return pathSeparatorChar;
    }

    public PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        String userHome=System.getProperty("user.home");
        PropertySourcesPlaceholderConfigurer properties =
                new PropertySourcesPlaceholderConfigurer();
        properties.setLocation(new FileSystemResource(userHome+getPathSeparator()+"teetimes"+getPathSeparator()+"application.properties"));
        properties.setIgnoreResourceNotFound(false);
        return properties;
    }

    public static boolean isWindows() {
        return (OS.indexOf("win") >= 0);
    }

    public static boolean isMac() {
        return (OS.indexOf("mac") >= 0);
    }
}