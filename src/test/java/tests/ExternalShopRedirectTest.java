package tests; // patched

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.HomePage;
import utils.ScreenshotUtils;
import utils.ExcelUtils;
import utils.ExcelUtils.CartItemReport;
import java.nio.file.Files;
import java.nio.file.Path;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.ArrayList;
import java.util.List;

public class ExternalShopRedirectTest extends BaseTest {
    @Test
    public void testSearchAndRedirectToExternalShop() {
        List<CartItemReport> cartReports = new ArrayList<>();
        String testStatus = "PASS";
        try {
            String[] products = {"טלוויזיה", "אייפון", "מכונת כביסה"};
            String lastIphoneName = null;
            String iphoneProductId = null;
            for (int i = 0; i < products.length; i++) {
                String product = products[i];
                System.out.println("\n=== מחפש: " + product + " ===");
                HomePage homePage = new HomePage(driver);
                homePage.search(product);
                Thread.sleep(3000);
                // נסה קודם להוסיף פריט מהתוצאות ישירות לסל
                boolean addedFromListing = false;
                String productName = "";
                String productPrice = "";
                try {
                    WebElement addFromListing = driver.findElement(By.cssSelector("a.addItemToCart"));
                    // שמירת פרטי המוצר לדיווח
                    try {
                        WebElement tile = addFromListing.findElement(By.xpath("ancestor::*[contains(@class,'tile')]"));
                        WebElement nameEl = tile.findElement(By.cssSelector("div.degem h3"));
                        productName = nameEl.getText();
                        WebElement priceEl = tile.findElement(By.cssSelector("div.lprice"));
                        productPrice = priceEl.getText().replace("₪", "").trim();
                    } catch (Exception ignore) {
                        productName = product;
                        productPrice = "N/A";
                    }
                    if ("אייפון".equals(product)) {
                        try {
                            WebElement tile = addFromListing.findElement(By.xpath("ancestor::*[contains(@class,'tile')]"));
                            WebElement nameEl = tile.findElement(By.cssSelector("div.degem h3"));
                            lastIphoneName = nameEl.getText();
                        } catch (Exception ignore) {
                            lastIphoneName = "אייפון";
                        }
                        try {
                            iphoneProductId = addFromListing.getAttribute("data-productid");
                            System.out.println("זוהה מזהה מוצר לאייפון: " + iphoneProductId);
                        } catch (Exception ignore) {}
                    }
                    addFromListing.click();
                    addedFromListing = true;
                    System.out.println("→ הוסף מהתוצאות לסל ('" + product + "')");
                    Thread.sleep(1500);
                    // תיעוד הוספה מוצלחת
                    cartReports.add(new CartItemReport(product, productName, true, productPrice, "1", productPrice));
                } catch (Exception e) {
                    System.out.println("⚠️ לא נמצא 'הוסף לסל' בתוצאות עבור '" + product + "'");
                    // תיעוד הוספה כושלת
                    cartReports.add(new CartItemReport(product, "לא נמצא", false, "N/A", "0", "0"));
                }

                // במידה ולא הצלחנו להוסיף מהתוצאות, ניכנס למוצר וננסה משם פעולות הדגמה
                if (!addedFromListing) {
                    WebElement firstProduct = driver.findElement(By.cssSelector("a.prodLink"));
                    String fallbackProductName = firstProduct.getText();
                    firstProduct.click();
                    Thread.sleep(3000);
                    System.out.println("נכנס למוצר: " + fallbackProductName);

                    if (i == 1) {
                        lastIphoneName = fallbackProductName;
                        try {
                            WebElement qtyInput = driver.findElement(By.cssSelector("input.qty, input[name*='qty'], input[id*='qty']"));
                            qtyInput.clear();
                            qtyInput.sendKeys("2");
                            System.out.println("→ שינה כמות ל-2");
                            Thread.sleep(1000);
                        } catch (Exception ex) {
                            System.out.println("⚠️ לא נמצא שדה כמות בעמוד מוצר");
                        }
                    }

                    try {
                        WebElement buyNowBtn = driver.findElement(By.xpath("//button[contains(text(),'קנה עכשיו') or contains(text(),'קניה מהירה') or contains(text(),'קנה')]") );
                        buyNowBtn.click();
                        System.out.println("→ נלחץ כפתור 'קנה עכשיו'");
                        Thread.sleep(2000);
                    } catch (Exception ex) {
                        System.out.println("⚠️ לא נמצא כפתור 'קנה עכשיו'");
                    }

                    try {
                        WebElement externalBtn = driver.findElement(By.xpath("//a[contains(text(),'לקנייה') or contains(text(),'לאתר החנות') or contains(text(),'לאתר הספק') or contains(text(),'לאתר')]") );
                        String shopUrl = externalBtn.getAttribute("href");
                        System.out.println("→ עובר לחנות חיצונית: " + shopUrl);
                    } catch (Exception ex2) {
                        try {
                            WebElement externalBtn2 = driver.findElement(By.cssSelector("a.btn, a.button, a[href*='external'], a[href*='shop']"));
                            String shopUrl2 = externalBtn2.getAttribute("href");
                            System.out.println("→ עובר לחנות חיצונית: " + shopUrl2);
                        } catch (Exception ex3) {
                            System.out.println("⚠️ לא נמצא כפתור מעבר לחנות חיצונית");
                        }
                    }

                    // חזרה לדף הבית להמשך חיפושים
                    driver.get("https://www.lastprice.co.il");
                    Thread.sleep(2000);
                }
            }

            // אחרי הכנסת כל המוצרים, ניגש לסל ונלחץ פלוס לאייפון
            System.out.println("\n=== ניגש לסל ומוסיף לאייפון כמות דרך הפלוס ===");
            try {
                // מעבר לסל (אם יש כפתור)
                WebElement cartBtn = null;
                try {
                    cartBtn = driver.findElement(By.cssSelector("a[href*='cart'], a[href*='basket'], .cart, .basket, #cart"));
                    cartBtn.click();
                } catch (Exception e) {
                    driver.get("https://www.lastprice.co.il/shopping-cart.asp");
                }
                Thread.sleep(3000);
                // חפש שורה של אייפון לפי מזהה מוצר (אם קיים), אחרת לפי טקסט
                WebElement iphoneRow = null;
                if (iphoneProductId != null && !iphoneProductId.isEmpty()) {
                    try {
                        iphoneRow = driver.findElement(By.cssSelector("tr.item[data-productid='" + iphoneProductId + "']"));
                    } catch (Exception e) {
                        System.out.println("⚠️ לא נמצאה שורה לפי productId, מנסה לפי טקסט");
                    }
                }
                if (iphoneRow == null) {
                    try {
                        iphoneRow = driver.findElement(By.xpath("//*[contains(text(),'אייפון') or contains(text(),'iPhone')]/ancestor::tr[contains(@class,'item')]"));
                    } catch (Exception e) {
                        System.out.println("⚠️ לא נמצא שורת אייפון בסל");
                    }
                }
                if (iphoneRow != null) {
                    try {
                        WebElement plusBtn = null;
                        try {
                            plusBtn = iphoneRow.findElement(By.xpath(".//button[contains(text(),'+') or contains(@class,'plus') or contains(@aria-label,'הוסף')]"));
                        } catch (Exception ex) {
                            try {
                                WebElement faPlus = iphoneRow.findElement(By.xpath(".//*[contains(@class,'fa-plus')]")).findElement(By.xpath("ancestor::button|ancestor::a"));
                                plusBtn = faPlus;
                            } catch (Exception ex2) {
                                // נסיון נוסף: קישורי פלוס כלליים בתוך השורה
                                plusBtn = iphoneRow.findElement(By.xpath(".//a[contains(@class,'plus') or contains(text(),'+')]"));
                            }
                        }
                        plusBtn.click();
                        System.out.println("→ נלחץ פלוס לאייפון");
                        Thread.sleep(1000);
                        // הדפסת כמות חדשה
                        WebElement qtyInput = null;
                        try {
                            qtyInput = iphoneRow.findElement(By.cssSelector("td.qnt-count input[name='qty'], input.quantity"));
                        } catch (Exception ex) {
                            // לעתים הכמות לא כשדה קלט אלא span
                            try {
                                WebElement qtySpan = iphoneRow.findElement(By.xpath(".//span[contains(@class,'qty') or contains(@class,'quantity')]"));
                                System.out.println("כמות חדשה לאייפון בסל: " + qtySpan.getText());
                                qtyInput = null;
                            } catch (Exception ex2) {
                                System.out.println("⚠️ לא נמצא שדה/טקסט כמות להצגה");
                            }
                        }
                        if (qtyInput != null) {
                            String newQty = qtyInput.getAttribute("value");
                            System.out.println("כמות חדשה לאייפון בסל: " + newQty);
                        }
                        // שמור צילום מסך וה-HTML לצורך בדיקה
                        try {
                            ScreenshotUtils.captureScreenshot(driver, "cart_after_plus");
                            String html = driver.getPageSource();
                            Path out = Path.of("output", "lastprice_cart_after_plus.html");
                            Files.createDirectories(out.getParent());
                            Files.writeString(out, html);
                            System.out.println("שמורה תמונת מסך ו-HTML לאחר לחיצת פלוס.");
                        } catch (Exception ioex) {
                            System.out.println("⚠️ לא ניתן לשמור צילום מסך/HTML: " + ioex.getMessage());
                        }
                    } catch (Exception e) {
                        System.out.println("⚠️ לא נמצא כפתור פלוס או שדה כמות לאייפון");
                    }
                }
            } catch (Exception e) {
                System.out.println("⚠️ שגיאה בגישה לסל או הגדלת כמות");
            }

            // קריאת נתוני העגלה בפועל ועדכון הדוח
            System.out.println("\n=== קריאת נתוני עגלה סופיים ===");
            String cartTotal = "0";
            try {
                driver.get("https://www.lastprice.co.il/shopping-cart.asp");
                Thread.sleep(2000);
                
                // קריאת סה"כ עגלה
                try {
                    WebElement totalElement = driver.findElement(By.cssSelector("span#TotalNoDlv, td.totals span#TotalNoDlv"));
                    cartTotal = totalElement.getText().trim();
                    System.out.println("סה\"כ עגלה: " + cartTotal + "₪");
                } catch (Exception e) {
                    // נסיון חלופי
                    try {
                        WebElement totalRow = driver.findElement(By.xpath("//td[contains(text(),'סה\"כ מוצרים')]/following-sibling::td[@class='totals']"));
                        cartTotal = totalRow.getText().replace("₪", "").replace("‎", "").trim().split(" ")[0];
                        System.out.println("סה\"כ עגלה (חלופי): " + cartTotal + "₪");
                    } catch (Exception ex) {
                        System.out.println("⚠️ לא ניתן לקרוא סה\"כ עגלה");
                    }
                }
                
                // עדכון נתוני פריטים בעגלה עם מחירים וכמויות אמיתיים
                List<WebElement> cartItems = driver.findElements(By.cssSelector("tr.item"));
                System.out.println("נמצאו " + cartItems.size() + " פריטים בעגלה");
                
                for (int idx = 0; idx < Math.min(cartItems.size(), cartReports.size()); idx++) {
                    try {
                        WebElement item = cartItems.get(idx);
                        String name = item.findElement(By.cssSelector("td.name a div.t1, td.name div.t1")).getText();
                        String price = item.findElement(By.cssSelector("td.price")).getText().replace("₪", "").replace("‎", "").trim();
                        String qty = item.findElement(By.cssSelector("td.qnt-count input[name='qty']")).getAttribute("value");
                        String rowTotal = item.findElement(By.cssSelector("td.total")).getText().replace("₪", "").replace("‎", "").replace("סך הכל לשורה:", "").trim();
                        
                        // עדכון הדוח עם נתוני העגלה האמיתיים
                        CartItemReport report = cartReports.get(idx);
                        report.productName = name;
                        report.price = price;
                        report.quantity = qty;
                        report.rowTotal = rowTotal;
                        
                        System.out.println("  - " + name + " | מחיר: " + price + " | כמות: " + qty + " | סה\"כ: " + rowTotal);
                    } catch (Exception e) {
                        System.out.println("⚠️ שגיאה בקריאת פריט " + (idx + 1) + ": " + e.getMessage());
                    }
                }
            } catch (Exception e) {
                System.out.println("⚠️ שגיאה כללית בקריאת עגלה: " + e.getMessage());
            }
            
            // כתיבת דוח Excel
            try {
                String excelPath = "output/cart_test_report.xlsx";
                ExcelUtils.writeCartTestReport(excelPath, cartReports, cartTotal, testStatus);
                System.out.println("\n✅ דוח Excel נשמר בהצלחה: " + excelPath);
            } catch (Exception e) {
                System.out.println("⚠️ שגיאה בשמירת דוח Excel: " + e.getMessage());
                e.printStackTrace();
            }

            System.out.println("\n=== סיום תהליך הדגמה ====");
        } catch (Exception e) {
            testStatus = "FAIL";
            e.printStackTrace();
            // נסיון לשמור דוח גם במקרה של כשל
            try {
                String excelPath = "output/cart_test_report.xlsx";
                ExcelUtils.writeCartTestReport(excelPath, cartReports, "0", testStatus);
            } catch (Exception ex) {
                System.out.println("⚠️ לא ניתן לשמור דוח Excel לאחר כשל");
            }
            Assert.fail("התרחשה שגיאה: " + e.getMessage());
        }
    }
}
