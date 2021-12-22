package browser;

import javafx.scene.Scene;
import org.junit.jupiter.api.Test;


import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

class ViewTest {

    @Test
    void testMakeSceneWidth() {
        View testView = new View();
        Scene testScene = testView.makeScene(1000, 100);
        assertEquals(1000, testScene.getWidth());
    }

    @Test
    void testMakeSceneHeight() {
        View testView = new View();
        Scene testScene = testView.makeScene(1000, 100);
        assertEquals(100, testScene.getHeight());
    }


    @Test
    void testShowPageStoresPage() throws MalformedURLException {
        View testView = new View();
        String testURL = "https://users.cs.duke.edu/rcd";
        testView.showPage(testURL);
        assertEquals(new URL(testURL), testView.getMyModel().getNextURL(1));
    }
}