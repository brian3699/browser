package browser;

import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;


/**
 * A class used to display the viewer for a simple HTML browser.
 *
 * See this tutorial for help on how to use all variety of components:
 *   http://download.oracle.com/otndocs/products/javafx/2/samples/Ensemble/
 *
 * @author Owen Astrachan
 * @author Marcin Dobosz
 * @author Yuzhang Han
 * @author Edwin Ward
 * @author Robert C. Duvall
 * @author Young Jun
 */
public class View {
    // constants
    public static final String BLANK = " ";
    public static final int MOVE_BACK = -1;
    public static final int MOVE_FRONT = 1;

    // web page
    private WebView myPage;
    // navigation
    private TextField myURLDisplay;
    // information area
    private Label myStatus;

    //backend model object
    private Model myModel;

    // current favorite button
    private Button currentFavoriteButton;
    // action to take for pressing Back and Next buttons
    private Map<String, EventHandler<ActionEvent>> backNextActions;
    // action to take for pressing added feature buttons
    private Map<String, EventHandler<ActionEvent>> additionalActions;



    /**
     * Create a web browser with prompts in the given language with initially empty state.
     */
    public View() {

        myModel = new Model(new ArrayList<>(), -1, null);
        currentFavoriteButton = null;
        setBackNextActions();
        setAdditionalActions();

    }

    // stores action to take for pressing Back and Next buttons
    private void setBackNextActions(){
        backNextActions = new TreeMap<String, EventHandler<ActionEvent>>();
        backNextActions.put("Back", event -> update(myModel.getNextURL(MOVE_BACK)));
        backNextActions.put("Next", event -> update(myModel.getNextURL(MOVE_FRONT)));
    }

    // stores action to take for pressing added feature buttons
    private void setAdditionalActions(){
        additionalActions = new TreeMap<String, EventHandler<ActionEvent>>();
        additionalActions.put("Frequently Visited", event -> makeFrequentlyVisitedButtons());
        additionalActions.put("Home", event -> updateAndStore(myModel.getHome()));
        additionalActions.put("Set Home", event -> myModel.setHomeToCurrentPage());
    }

    // make buttons from the String and action in the map and add it to the HBox
    private void putButtonFromMap(HBox result, Map<String, EventHandler<ActionEvent>> actions){
        for(String move: actions.keySet()){
            Button button = new Button(move);
            button.setOnAction(actions.get(move));
            result.getChildren().add(button);
        }
    }

    /**
     * Returns scene for the browser, so it can be added to stage.
     */
    public Scene makeScene (int width, int height) {
        BorderPane root = new BorderPane();
        // must be first since other panels may refer to page
        root.setCenter(makePageDisplay());
        root.setTop(makeInputPanel());
        root.setBottom(makeInformationPanel());
        // create scene to hold UI
        return new Scene(root, width, height);
    }

    /**
     * Display given URL.
     */
    public void showPage (String url) {
        try {
            URL tmp = myModel.completeURL(url);
            if (tmp != null) {
                // unfortunately, completeURL may not have returned a valid URL, so test it
                tmp.openStream();
                myModel.rememberURL(tmp);
                update(tmp);
            }
        }
        catch (Exception e) {
            showError(String.format("Could not load %s", url));
        }
    }

    // Update just the view to display given URL
    private void update (URL url) throws NullPointerException{
        String urlText = url.toString();
        myPage.getEngine().load(urlText);
        myURLDisplay.setText(urlText);
    }

    protected Model getMyModel(){
        return myModel;
    }

    // Update the view to display given URL and store URL
    private void updateAndStore (URL url) throws NullPointerException{
        myModel.rememberURL(url);
        update(url);
    }

    // Display given message as information in the GUI
    private void showStatus (String message) {
        myStatus.setText(message);
    }

    // Display given message as an error in the GUI
    private void showError (String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Browser Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Make user-entered URL/text field and back/next buttons
    private Node makeInputPanel () throws NullPointerException{
        HBox result = new HBox();
        // create buttons for back and next based on their action
        putButtonFromMap(result, backNextActions);
        // if user presses button or enter in text field, load/show the URL
        EventHandler<ActionEvent> showHandler = event -> showPage(myURLDisplay.getText());
        myURLDisplay = makeInputField(70, showHandler);
        result.getChildren().addAll(makeButton("Go", showHandler), myURLDisplay);
        // create buttons with additional features
        putButtonFromMap(result, additionalActions);
        // creates Set Favorite button
        Button setFavoriteButton = makeButton("Set Favorite", event -> setFavoriteButton(result));
        // add all buttons to HBox
        result.getChildren().add(setFavoriteButton);

        return result;
    }

    // make a popup that contains buttons for 5 most frequently visited sites
    private void makeFrequentlyVisitedButtons(){
        // could also be made using listview, but preferred using HBox
        HBox favoritePage = new HBox();
        //ListView listView = new ListView(); - could also be made using listview
        ArrayList<URL> fiveFrequent = myModel.getFiveMostFrequentURL();

        for(URL url : fiveFrequent){
            String[] urlName = url.toString().split("//");
            String[] urlAfterHttps = urlName[1].split("\\.");
            String name;
            // find name of the website from URL
            if(urlAfterHttps[0].equals("www")){
                name = urlAfterHttps[1];
            }else{
                name = urlAfterHttps[0];
            }
            // add new button
            favoritePage.getChildren().add(makeButton(name, event -> updateAndStore(url)));
        }
        Stage favoritePageStage = new Stage();
        favoritePageStage.setTitle("Frequently Visited");
        //create new scene where the frequently visited page's buttons will be displayed
        Scene scene = new Scene(favoritePage, 300, 50);
        favoritePageStage.setScene(scene);
        favoritePageStage.show();
    }

    // creates a button for a new favorite page
    private void setFavoriteButton(HBox result){
        if(currentFavoriteButton != null) result.getChildren().remove(currentFavoriteButton);
        TextInputDialog dialog = new TextInputDialog();
        dialog.setContentText("Type Name Of Your Favorite Website");
        Optional<String> name = dialog.showAndWait();
        String favorite = name.get();
        myModel.setMyFavoriteToCurrentPage();
        currentFavoriteButton = makeButton(favorite, event -> updateAndStore(myModel.getMyFavorite()));
        result.getChildren().add(currentFavoriteButton);
    }




    // Make panel where "would-be" clicked URL is displayed
    private Node makeInformationPanel () {
        // BLANK must be non-empty or status label will not be displayed in GUI
        myStatus = new Label(BLANK);
        return myStatus;
    }

    // Typical code to create HTML page display
    private Node makePageDisplay () {
        myPage = new WebView();
        // catch "browsing" events within web page
        myPage.getEngine().getLoadWorker().stateProperty().addListener(new LinkListener());
        return myPage;
    }

    // Typical code to create button
    private Button makeButton (String label, EventHandler<ActionEvent> handler) {
        Button result = new Button();
        result.setText(label);
        result.setOnAction(handler);
        return result;
    }

    // Typical code to create text field for input
    private TextField makeInputField (int width, EventHandler<ActionEvent> handler) {
        TextField result = new TextField();
        result.setPrefColumnCount(width);
        result.setOnAction(handler);
        return result;
    }

    // Inner class to deal with link-clicks and mouse-overs Mostly taken from
    //   http://blogs.kiyut.com/tonny/2013/07/30/javafx-webview-addhyperlinklistener/
    private class LinkListener implements ChangeListener<State> {
        public static final String ANCHOR = "a";
        public static final String HTML_LINK = "href";
        public static final String EVENT_CLICK = "click";
        public static final String EVENT_MOUSEOVER = "mouseover";
        public static final String EVENT_MOUSEOUT = "mouseout";

        @Override
        public void changed (ObservableValue<? extends State> ov, State oldState, State newState) {
            if (newState == Worker.State.SUCCEEDED) {
                EventListener listener = event -> handleMouse(event);
                Document doc = myPage.getEngine().getDocument();
                NodeList nodes = doc.getElementsByTagName(ANCHOR);
                for (int k = 0; k < nodes.getLength(); k+=1) {
                    EventTarget node = (EventTarget)nodes.item(k);
                    node.addEventListener(EVENT_CLICK, listener, false);
                    node.addEventListener(EVENT_MOUSEOVER, listener, false);
                    node.addEventListener(EVENT_MOUSEOUT, listener, false);
                }
            }
        }

        // Give user feedback as expected by modern browsers
        private void handleMouse (Event event) {
            final String href = ((Element)event.getTarget()).getAttribute(HTML_LINK);
            if (href != null) {
                switch (event.getType()) {
                    case EVENT_CLICK -> showPage(href);
                    case EVENT_MOUSEOVER -> showStatus(href);
                    case EVENT_MOUSEOUT -> showStatus(BLANK);
                }
            }
        }
    }
}