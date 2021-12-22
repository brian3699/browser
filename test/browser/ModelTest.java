package browser;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ModelTest {


    @Test
    void testCompleteURLWhenWrongURL() throws MalformedURLException {
        Model testModel = new Model(new ArrayList<>(), -1, null);
        String wrongURLString = "youtube.com";
        URL correctURL = new URL("http://youtube.com");
        assertEquals(correctURL, testModel.completeURL(wrongURLString));
    }

    @Test
    void testCompleteURLWhenNoURL() throws MalformedURLException {
        Model testModel = new Model(new ArrayList<>(), -1, null);
        String wrongURLString = "";
        URL expectedURL = new URL("http:");
        assertEquals(expectedURL, testModel.completeURL(wrongURLString));
    }

    @Test
    void testCompleteURLWhenNull() {
        Model testModel = new Model(new ArrayList<>(), -1, null);
        assertEquals(null, testModel.completeURL(null));
    }

    @Test
    void testGetFiveMostFrequentURLWhenOnePageVisited() throws MalformedURLException {
        Model testModel = new Model(new ArrayList<>(), -1, null);
        URL sampleURL = new URL("http://youtube.com");
        ArrayList<URL> testURLs = new ArrayList<>();
        testURLs.add(sampleURL);
        testModel.rememberURL(sampleURL);
        assertEquals(testURLs, testModel.getFiveMostFrequentURL());
    }

    @Test
    void testGetFiveMostFrequentURLWhenSevenPageVisited() throws MalformedURLException {
        Model testModel = new Model(new ArrayList<>(), -1, null);
        URL sampleURL1 = new URL("http://youtube.com");
        URL sampleURL2 = new URL("http://naver.com");
        URL sampleURL3 = new URL("http://yahoo.com");
        URL sampleURL4 = new URL("http://facebook.com");
        URL sampleURL5 = new URL("http://instagram.com");
        URL sampleURL6 = new URL("http://netflix.com");

        ArrayList<URL> testURLs = new ArrayList<>();
        testURLs.add(sampleURL1);
        testURLs.add(sampleURL2);
        testURLs.add(sampleURL3);
        testURLs.add(sampleURL4);
        testURLs.add(sampleURL5);
        testURLs.add(sampleURL6);
        testURLs.add(sampleURL6);

        for(URL url : testURLs){
            testModel.rememberURL(url);
        }
        // check the most visited URL is in the returned list
        assertTrue(testModel.getFiveMostFrequentURL().contains(sampleURL6));
    }

    @Test
    void testGetFiveMostFrequentURLWhenNoPageVisited() {
        Model testModel = new Model(new ArrayList<>(), -1, null);
        ArrayList<URL> testURLs = new ArrayList<>();
        assertEquals(testURLs, testModel.getFiveMostFrequentURL());
    }

    @Test
    void testGetNextURLWhenNoNextURL() throws MalformedURLException {
        Model testModel = new Model(new ArrayList<>(), -1, null);
        URL testURL = new URL("http://naver.com");
        testModel.rememberURL(testURL);
        assertEquals(testURL, testModel.getNextURL(1));
    }

    @Test
    void testGetNextURLWhenNoPreviousURL() throws MalformedURLException {
        Model testModel = new Model(new ArrayList<>(), -1, null);
        URL testURL = new URL("http://naver.com");
        testModel.rememberURL(testURL);
        assertEquals(testURL, testModel.getNextURL(-1));
    }
}