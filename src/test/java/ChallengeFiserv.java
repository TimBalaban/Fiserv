import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;

import java.util.*;

import java.util.concurrent.TimeUnit;

        /*
        1. Open www.amazon.com website.
        2. Enter the text “girls bicycle” in the search box. Hit enter
        3. From the results displayed on the first page add all the items marked as “Best seller” to the cart.
        4. Add any assertions you feel are pertinent to this test.
         */

public class ChallengeFiserv {
    public static void main(String[] args) throws InterruptedException {

        System.setProperty("webdriver.chrome.driver", "/Users/tim/Desktop/Tim/Selenium/libs/chromedriver");
        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

        //-------------- 1. Open www.amazon.com website. ------------------------------------
        driver.get("https://www.amazon.com/");

        //-------------- 2. Enter the text “girls bicycle” in the search box. Hit enter -----
        WebElement searchBox = driver.findElement(By.xpath("//input[@id=\"twotabsearchtextbox\"]"));
        searchBox.sendKeys("girls bicycle" + Keys.ENTER);
        //searchBox.sendKeys("baby toys" + Keys.ENTER);
        //searchBox.sendKeys("baby wipes" + Keys.ENTER);
        //searchBox.sendKeys("nec 2017" + Keys.ENTER);
        Thread.sleep(1000);

        //-------------- 3. From the results displayed on the first page add all the items marked as “Best seller” to the cart. -------

        List<WebElement> list = driver.findElements(By.xpath("//span[text()='Best Seller']/ancestor::div[@data-asin]"));
        System.out.println("All best sellers with duplicates from first page: " + list.size());

        Set<String> listSet = new TreeSet<>();
        for (WebElement each : list) {
            if (!each.getAttribute("data-asin").equals("")) {
                listSet.add(each.getAttribute("data-asin"));
            }
        }

        String urlDB = "https://www.amazon.com/dp/";
        Actions action = new Actions(driver);

        if (listSet.size() > 0) {
            System.out.println("List of Amazon Standard Identification Number marked as Best seller from first page (no duplicates): " + listSet.size());
            System.out.println(listSet);

            for (String each : listSet) {
                driver.navigate().to(urlDB + each);
                Thread.sleep(1000);

                //-------- Subscription ------------
                List<WebElement> oneTime = driver.findElements(By.xpath("//div[@class='a-accordion-row-a11y']"));
                if (oneTime.size() >= 1) {
                    oneTime.get(0).click();
                }
                Thread.sleep(1000);

                //-------- Cart button --------------
                driver.findElement(By.xpath("//input[@id='add-to-cart-button']")).click();
                Thread.sleep(1000);

                //-------- Cancel protection plan ---
                action.sendKeys(Keys.ESCAPE).perform();
                action.sendKeys(Keys.ESCAPE).perform();
                Thread.sleep(1000);
            }
        } else {
            System.out.println("Your Amazon Cart is empty!!!");
        }

        //--------- Click on cart --------
        WebElement cart = driver.findElement(By.xpath("//div[@id='nav-cart-count-container']"));
        cart.click();
        Thread.sleep(1000);


        //--------- List of Amazon Standard Identification Number from Cart ------------
        List<WebElement> cartList = driver.findElements(By.xpath("//div[@data-asin]"));

        Set<String> cartSet = new TreeSet<>();
        if (cartList.size() > 0) {
            for (WebElement each : cartList) {
                cartSet.add(each.getAttribute("data-asin"));
            }
            System.out.println("List of Amazon Standard Identification Number marked as Best seller from Cart: " + cartSet.size());
            System.out.println(cartSet);
        }

        //---------- Assert that items from first page with best sellers match the cart list --------
        Assert.assertEquals(cartSet, listSet);

        //--------- Assert that all items from the cart is marked as Best Seller ------
        List<WebElement> bestS = driver.findElements(By.xpath("//i[@class='a-icon a-icon-addon sc-best-seller-badge']"));
        for(WebElement each : bestS) {
            Assert.assertEquals("Best Seller", each.getText().substring(3));
        }
        driver.close();
    }
}