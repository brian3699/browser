import javafx.application.Application;
import javafx.stage.Stage;
import java.awt.Dimension;
import browser.View;


/**
 * A simple HTML browser.
 *
 * @author Robert C. Duvall
 */
public class Main extends Application {
    // convenience constants
    public static final String TITLE = "NanoBrowser";
    public static final String DEFAULT_START_PAGE = "https://users.cs.duke.edu/rcd";
    public static final Dimension DEFAULT_SIZE = new Dimension(1500, 1000);


    /**
     * @see Application#start(Stage)
     */
    @Override
    public void start (Stage stage) {
        View browser = new View();
        // give the window a title
        stage.setTitle(TITLE);
        // add our user interface components to Frame and show it
        stage.setScene(browser.makeScene(DEFAULT_SIZE.width, DEFAULT_SIZE.height));
        stage.show();
        // start somewhere, less typing for debugging
        browser.showPage(DEFAULT_START_PAGE);
    }
}
